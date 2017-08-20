package me.unibike.lbs.location;

/**
 * @author LuoLiangchen
 * @since 16/11/1
 */

public class LocationException extends RuntimeException {

    public final int code;

    public final String hint;

    public LocationException(int code) {
        this(code, "未知错误，无法获取定位", "");
    }

    public LocationException(int code, String message) {
        this(code, message, "");
    }

    public LocationException(int code, String message, String hint) {
        super(message);
        this.code = code;
        this.hint = hint;
    }
}
