package com.luolc.lighter.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;
import com.luolc.lighter.MathLighter;

/**
 * @author LuoLiangchen
 * @since 16/11/5
 */

public class Permissions {

    private static final int REQUEST_CODE_UPPER_BOUND = 1 << 16;

    public interface Action0 {

        void call();
    }

    public interface OnGranted extends Action0 {}

    public interface OnDenied extends Action0 {}

    public static PermissionCallback emptyCallback() {
        return (requestCode, permissions, grantResults) -> {};
    }

    @TargetApi(23)
    public static <V> void request(V framework, OnGranted onGranted, OnDenied onDenied, String...permissions) {
        if (framework instanceof Activity) request((Activity) framework, onGranted, onDenied, permissions);
        else if (framework instanceof Fragment) request((Fragment) framework, onGranted, onDenied, permissions);
        else throw new IllegalArgumentException("framework should be an instance of Activity or Fragment");
    }

    @TargetApi(23)
    public static void request(Activity activity, OnGranted onGranted, OnDenied onDenied, String...permissions) {
        String[] unGrantedPermissions = Stream.of(permissions)
                .filter(p -> ContextCompat.checkSelfPermission(activity, p) != PackageManager.PERMISSION_GRANTED)
                .toArray(String[]::new);
        if (unGrantedPermissions.length > 0) {
            if (activity instanceof PermissionHandler) {
                ((PermissionHandler) activity).setPermissionCallback((requestCode, ps, grantResults) -> {
                    if (isGrantedResult(requestCode, ps, grantResults)) onGranted.call();
                    else onDenied.call();
                });
            }
            activity.requestPermissions(unGrantedPermissions,
                    MathLighter.upperBoundHashCode(REQUEST_CODE_UPPER_BOUND, (Object[]) unGrantedPermissions));
        }
    }

    @TargetApi(23)
    public static void request(Fragment fragment, OnGranted onGranted, OnDenied onDenied, String...permissions) {
        String[] unGrantedPermissions = Stream.of(permissions)
                .filter(p -> ContextCompat.checkSelfPermission(fragment.getContext(), p) != PackageManager.PERMISSION_GRANTED)
                .toArray(String[]::new);
        if (unGrantedPermissions.length > 0) {
            if (fragment instanceof PermissionHandler) {
                ((PermissionHandler) fragment).setPermissionCallback((requestCode, ps, grantResults) -> {
                    if (isGrantedResult(requestCode, ps, grantResults)) onGranted.call();
                    else onDenied.call();
                });
            }
            fragment.requestPermissions(unGrantedPermissions,
                    MathLighter.upperBoundHashCode(REQUEST_CODE_UPPER_BOUND, (Object[]) unGrantedPermissions));
        }
    }

    public static boolean isGrantedResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        return grantResults.length > 0
                && IntStream
                        .of(grantResults)
                        .reduce(PackageManager.PERMISSION_GRANTED, (l, r) -> l | r)
                        == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isGrantedCurrently(Context context, String...permissions) {
        return Stream.of(permissions)
                .map(p -> ContextCompat.checkSelfPermission(context, p))
                .reduce(PackageManager.PERMISSION_GRANTED, (l, r) -> l | r)
                == PackageManager.PERMISSION_GRANTED;
    }
}
