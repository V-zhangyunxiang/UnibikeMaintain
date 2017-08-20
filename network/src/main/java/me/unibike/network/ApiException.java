package me.unibike.network;

/**
 * @author LuoLiangchen
 * @since 16/11/5
 */

public class ApiException extends RuntimeException {

    public final int code;

    public final String msg;

    public ApiException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
