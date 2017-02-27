package com.ppfuns.model.entity;

/**
 * Created by zpf on 2016/8/17.
 */
public class Response2<T> {
    public String code;
    public long longTime;
    public String stringTime;
    public String message;
    public T result;
    public boolean cached;
    public int cachedTime;
}
