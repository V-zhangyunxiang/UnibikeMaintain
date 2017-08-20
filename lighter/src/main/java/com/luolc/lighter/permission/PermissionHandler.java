package com.luolc.lighter.permission;

import android.support.annotation.NonNull;

/**
 * @author LuoLiangchen
 * @since 16/11/5
 */

public interface PermissionHandler {

    void setPermissionCallback(@NonNull PermissionCallback callback);
}
