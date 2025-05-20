package com.liu.blogger.pojo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author : Lucifer
 * @Date : 2025/04/27/21:56
 * @Description :
 */
@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 0;
        r.message = "OK";
        r.data = data;
        return r;
    }
}
