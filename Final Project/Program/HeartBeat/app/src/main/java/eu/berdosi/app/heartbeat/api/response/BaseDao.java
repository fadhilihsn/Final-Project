package eu.berdosi.app.heartbeat.api.response;

import com.google.gson.annotations.SerializedName;

public class BaseDao<T> {
    @SerializedName("status") private int code;
    private String message;
    private T data;

    public BaseDao(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public BaseDao() {
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
