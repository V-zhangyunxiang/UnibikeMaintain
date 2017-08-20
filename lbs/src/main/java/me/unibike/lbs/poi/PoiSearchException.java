package me.unibike.lbs.poi;

/**
 * @author LuoLiangchen
 * @since 2016/12/27
 */

public class PoiSearchException extends RuntimeException {

    public static final int SUCCESS = 1000;

    public PoiSearchException() {
        super();
    }

    public PoiSearchException(String msg) {
        super(msg);
    }
}
