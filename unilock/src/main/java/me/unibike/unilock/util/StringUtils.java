package me.unibike.unilock.util;

import android.text.TextUtils;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;


/**
 * @author LuoLiangchen
 * @since 16/9/28
 */

public class StringUtils {

    public static final String EMPTY = "";
    public static final String ZERO = "0";
    public static final String ZERO_DECIMAL = "0.00";

    public static String toString(String s) {
        return toString(s, EMPTY);
    }

    public static String toString(String s, String replace) {
        return s == null ? replace : s;
    }

    public static String map2GetParams(String url, Map<?, ?> params) {
        if (CollectionUtils.isEmpty(params)) return url;
        StringBuilder sb = new StringBuilder(url).append("?");
        map2GetParams(sb, params);
        return sb.toString();
    }

    public static String map2GetParams(Map<?, ?> keyValue) {
        StringBuilder sb = new StringBuilder();
        map2GetParams(sb, keyValue);
        return sb.toString();
    }

    public static void map2GetParams(StringBuilder sb, Map<?, ?> params) {
        if (CollectionUtils.isEmpty(params) || sb == null) {
            if (AppUtils.debugging())
                throw new NullPointerException("Your " + (sb == null ? "StringBuilder" : "params") + " is null or empty");
            return;
        }
        Iterator<? extends Map.Entry<?, ?>> entryIt = params.entrySet().iterator();
        Map.Entry<?, ?> entry;
        int i = 0;
        while (entryIt.hasNext()) {
            if (i++ > 0) sb.append("&");
            entry = entryIt.next();
            if (TextUtils.isEmpty(ObjectUtils.toString(entry.getKey(), null))) continue;
            sb.append(entry.getKey())
                    .append("=")
                    .append(ObjectUtils.toString(entry.getValue()));
        }
    }


    public static boolean notEmpty(CharSequence charSequence) {
        return !TextUtils.isEmpty(charSequence);
    }

    public static boolean notEmpty(CharSequence c, boolean trim) {
        if (!trim) return notEmpty(c);
        if (c == null) return false;
        return c.toString().trim().length() > 0;
    }

    public static boolean isEmpty(CharSequence c) {
        return isEmpty(c, true);
    }

    public static boolean isEmpty(CharSequence c, boolean trim) {
        if (trim) {
            if (c == null) return true;
            else return c.toString().trim().length() == 0;
        } else {
            return TextUtils.isEmpty(c);
        }
    }


    public static String splice(Object... os) {
        StringBuilder sb = new StringBuilder();
        for (Object o : os) {
            sb.append(ObjectUtils.toString(o));
        }
        return sb.toString();
    }

    public static void clean(StringBuilder builder) {
        if (builder == null || builder.length() == 0) return;
        builder.delete(0, builder.length());
    }

    public static int length(CharSequence chars) {
        return chars == null ? 0 : chars.length();
    }

    public static String format(String pattern, Object... os) {
        return String.format(Locale.CHINA, pattern, os);
    }

    public static String getString(String seed, int count) {
        StringBuilder sb = new StringBuilder();
        while (count-- > 0) {
            sb.append(seed);
        }
        return sb.toString();
    }
}

