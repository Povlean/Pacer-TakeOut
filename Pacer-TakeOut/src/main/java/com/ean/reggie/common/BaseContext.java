package com.ean.reggie.common;

/**
 * @description:TODO
 * @author:Povlean
 */
public class BaseContext {

    private static ThreadLocal<Long> thread = new ThreadLocal<>();

    public static void setThread(Long id){
        thread.set(id);
    }

    public static Long getThread(){
        return thread.get();
    }
}
