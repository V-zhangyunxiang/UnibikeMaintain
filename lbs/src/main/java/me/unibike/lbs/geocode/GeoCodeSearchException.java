package me.unibike.lbs.geocode;

/**
 * @author LuoLiangchen
 * @since 2016/12/27
 */

public class GeoCodeSearchException extends RuntimeException {

    public static final int SUCCESS = 1000;

    public GeoCodeSearchException() {
        super();
    }

    public GeoCodeSearchException(String msg) {
        super(msg);
    }
}
