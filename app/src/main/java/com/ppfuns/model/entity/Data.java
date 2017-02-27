package com.ppfuns.model.entity;

import java.util.List;

/**
 * Created by zpf on 2016/8/15.
 */
public class Data<T> {
    public String code;
    public String codeV;
    public String count;
    public long   longTime;
    public String message;
    public String msg;
    public String resultex;
    public String stringTime;
    public T result;


    @Override
    public String toString() {
        return "Data{" +
                "code='" + code + '\'' +
                ", codeV=" + codeV +
                ", count=" + count +
                ", longTime=" + longTime +
                ", message='" + message + '\'' +
                ", msg=" + msg +
                ", resultex=" + resultex +
                ", stringTime='" + stringTime + '\'' +
                ", result=" + result +
                '}';
    }


}
