package me.unibike.unilock;

import me.unibike.network.ApiException;

/**
 * @author LuoLiangchen
 * @since 16/10/20
 */

public class LockException extends RuntimeException {

    public static final int CODE_UNKNOWN = -1;
    public static final int CODE_NET = 0;
    public static final int CODE_CONNECTION = 1;
    public static final int CODE_READ = 2;
    public static final int CODE_WRITE = 3;
    public static final int CODE_NOTIFY_SETUP = 4;
    public static final int CODE_NOTIFY_HANDLE = 5;
    public static final int CODE_SCAN = 6;

    public final int code;

    public final int apiCode;

    public LockException() {
        this("Unknown LockException");
    }

    public LockException(String msg) {
        this(msg, CODE_UNKNOWN);
    }

    public LockException(String msg, int code) {
        this(new RuntimeException(msg), code);
    }

    public LockException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
        if (throwable instanceof ApiException) apiCode = ((ApiException) throwable).code;
        else apiCode = 0;
    }
}
