package com.ppfuns.model.entity;

/**
 * Created by zpf on 2016/8/15.
 */
public class Response<T> {

    public Data<T> data;

    @Override
    public String toString() {
        return "Response{" +
                "data=" + data +
                '}';
    }
}
