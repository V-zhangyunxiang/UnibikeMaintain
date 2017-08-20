package me.unibike.unilock.api;

import me.unibike.network.*;
import rx.functions.Func1;

/**
 * @author LuoLiangchen
 * @since 16/11/5
 */

public class ApiDataTransformFunc<DataType> implements Func1<ApiResponse<DataType>, DataType> {

    public static final int SUCCESS = 0;

    @Override
    public DataType call(ApiResponse<DataType> response) {
        int code = response.getCode();
        String msg = response.getMessage();
        switch (code) {
            case SUCCESS:
                return response.getPayload();
            default:
                throw new ApiException(code, msg);
        }
    }
}
