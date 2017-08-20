package me.unibike.unilock.util.reflect;

import java.lang.reflect.ParameterizedType;

import me.unibike.unilock.util.AppUtils;

/**
 * @author LuoLiangchen
 * @since 16/9/28
 */

public class GenericGetter<T> {

    @SuppressWarnings("unchecked")
    public Class<T> getGenericClass(){
        System.out.println("getClass="+getClass().getName());
        if(AppUtils.debugging())if(getClass().equals(GenericGetter.class)) throw new WrongGenericTypeException();
        try {
            ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
            return (Class<T>) (type.getActualTypeArguments()[0]);
        }catch (Exception e){
            if(AppUtils.debugging()){
                WrongGenericTypeException wtException = new WrongGenericTypeException();
                wtException.initCause(e);
                throw wtException;
            }
            else return null;
        }
    }
}
