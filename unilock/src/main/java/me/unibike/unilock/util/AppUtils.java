package me.unibike.unilock.util;

import android.os.Build;

/**
 * @author LuoLiangchen
 * @since 16/9/28
 */

public class AppUtils {

    public static boolean debugging;

    public static boolean checkApi(int apiLevel) {
        return Build.VERSION.SDK_INT >= apiLevel;
    }

    /**
     * debugging or released
     */
    public static boolean debugging() {
        return debugging;
    }

}
