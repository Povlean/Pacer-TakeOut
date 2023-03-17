package com.ean.reggie.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
/**
 * 通用返回结果，服务器响应的数据最终都会封装成此对象。
 * @param <T>
 * */
@Data
public class Result<T> {

    private Integer code;
    private String msg;
    private T data;
    // 动态数据
    private Map map = new HashMap();

    // <T> Result<T>
    // 前面的<T>是为了声明泛型，后面的Result<T>是返回值类型
    public static <T> Result<T> success(T data){
        Result<T> r = new Result<T>();
        r.data = data;
        r.code = 1;
        return r;
    }

    public static <T> Result<T> error(String msg){
        Result r = new Result();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public Result<T> add(String key,Object value){
        this.map.put(key,value);
        return this;
    }
}
