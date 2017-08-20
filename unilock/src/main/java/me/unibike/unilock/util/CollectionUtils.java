package me.unibike.unilock.util;

import java.util.Collection;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

/**
 * @author LuoLiangchen
 * @since 16/9/28
 */

public class CollectionUtils {

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean notEmpty(Collection collection) {
        return !isEmpty(collection);
    }


    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean notEmpty(Map map) {
        return !isEmpty(map);
    }

    public static boolean isEmpty(Dictionary<?, ?> dictionary) {
        return dictionary == null || dictionary.isEmpty();
    }

    public static boolean notEmpty(Dictionary<?, ?> dictionary) {
        return !isEmpty(dictionary);
    }

    public static <A> boolean isInArray(A[] array, A a) {
        if (isEmpty(array)) return false;
        for (A check : array) {
            if (a == check) return true;
        }
        return false;
    }

    public static <A> boolean isEmpty(A[] array) {
        return array == null || array.length == 0;
    }

    public static <A> boolean notEmpty(A[] array) {
        return !isEmpty(array);
    }

    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    public static boolean notEmpty(byte[] array) {
        return !isEmpty(array);
    }

    public static int size(Collection c) {
        return c == null ? 0 : c.size();
    }

    public static int size(byte[] bytes) {
        return bytes == null ? 0 : bytes.length;
    }

    public static int size(Map<?, ?> map) {
        return map == null ? 0 : map.size();
    }

    public static <E> int size(E[] e) {
        return e == null ? 0 : e.length;
    }

    public static int size(int[] e) {
        return e == null ? 0 : e.length;
    }

    public static <E> E getLast(List<E> list) {
        if (CollectionUtils.isEmpty(list)) return null;
        return list.get(list.size() - 1);
    }

    public static <E> E getSafe(int index, List<E> list) {
        if (list == null) return null;
        if (list.size() <= index) return null;
        return list.get(index);
    }

    public static boolean notEmpty(int[] a) {
        return a != null && a.length > 0;
    }

    public static <T> T get(int i, T[] t) {
        if (CollectionUtils.size(t) <= i) return null;
        return t[i];
    }
}
