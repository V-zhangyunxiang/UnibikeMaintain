package me.unibike.lbs.location;

/**
 * @author LuoLiangchen
 * @since 16/11/7
 */

public class LocationFromMockException extends LocationException {

    public static final int CODE = 101;

    public LocationFromMockException() {
        super(CODE, "请关闭开发者模式中的允许模拟位置选项，以便UniBike获取正确的单车位置", "请关闭开发者模式中的允许模拟位置选项，以便UniBike获取正确的单车位置");
    }
}
