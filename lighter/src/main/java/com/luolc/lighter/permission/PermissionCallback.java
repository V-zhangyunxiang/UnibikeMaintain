package com.luolc.lighter.permission;

import android.support.annotation.NonNull;

/**
 * @author LuoLiangchen
 * @since 16/11/5
 */

public interface PermissionCallback {

    void onResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}
