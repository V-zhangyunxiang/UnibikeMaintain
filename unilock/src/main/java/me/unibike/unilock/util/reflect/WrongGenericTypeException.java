package me.unibike.unilock.util.reflect;

/**
 * @author LuoLiangchen
 * @since 16/9/28
 */

public class WrongGenericTypeException extends RuntimeException{
    public WrongGenericTypeException(){
        super("use anonymous inner class!");
    }
}
