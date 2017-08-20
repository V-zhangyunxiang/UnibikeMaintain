package me.unibike.unilock.api;

import me.unibike.network.UniNetwork;

/**
 * @author LuoLiangchen
 * @since 16/11/5
 */

public class LockService {

    private static final String TAG = LockService.class.getSimpleName();

    private static volatile UniLockApi uniLockApi;

    public static UniLockApi getService() {
        UniLockApi instance = uniLockApi;
        if (instance == null) {
            synchronized (LockService.class) {
                instance = uniLockApi;
                if (instance == null) {
                    instance = UniNetwork.createApi(UniLockApi.class);
                    uniLockApi = instance;
                }
            }
        }
        return instance;
    }
}
