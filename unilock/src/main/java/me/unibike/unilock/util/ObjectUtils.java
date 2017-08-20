package me.unibike.unilock.util;

import android.text.TextUtils;

import java.lang.ref.Reference;
import java.lang.reflect.Constructor;

import me.unibike.unilock.util.reflect.GenericGetter;

/**
 * @author LuoLiangchen
 * @since 16/9/28
 */

public class ObjectUtils {

    public static boolean isNull(Object... objects) {
        for (Object o : objects) {
            if (o == null) return true;
        }
        return false;
    }

    public static boolean notNull(Object... objects) {
        return !isNull(objects);
    }

    public static String toString(Object o) {
        return toString(o, StringUtils.EMPTY);
    }

    public static String toString(String s) {
        return toString(s, StringUtils.EMPTY);
    }

    public static String toString(String s, String replace) {
        return TextUtils.isEmpty(s) ? replace : s;
    }

    public static String toString(Object o, String replace) {
        return o == null ? replace : o.toString();
    }

    public static <T> T checkNull(T t) {
        if (t != null) return t;
        if (AppUtils.debugging()) throw new NullPointerException("Null object");
        else {
            try {
                Class<T> clazz = new GenericGetter<T>(){}.getGenericClass();
                Constructor<T> c = clazz.getConstructor();
                return c.newInstance();
            } catch (Exception e) {
                LogUtils.ex(e);
            }
        }
        return null;
    }

    public static <T>T get(Reference<T> reference){
        return reference==null?null:reference.get();
    }

    public static boolean equals(Object a, Object b){
        return equals(a,b,false);
    }

    public static boolean equals(Object a, Object b, boolean allowNull){
        if(a==null){
            if(allowNull)return b==null;
            return false;
        }
        if(a==b) return true;
        return a.equals(b);
    }
}
