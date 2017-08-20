package me.unibike.network;

/**
 * @author LuoLiangchen
 * @since 16/11/5
 */

public class ApiResponse<DataType> {

    protected int code;

    protected String message;

    protected DataType payload;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public DataType getPayload() {
        return payload;
    }
}
