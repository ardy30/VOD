package com.ppfuns.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * 创建者     庄丰泽
 * 创建时间   2016-06-14 20:48
 * 描述	      ${TODO}
 * <p/>
 * 更新者     郑培丰
 * 更新时间   2016-8-16 15:15
 * 更新描述   修改成单利
 */
public class JsonUtils {

    private static Gson gson=new Gson();
    private JsonUtils() {
    }

    public static <T> T fromJson(String source, Class<T> cl) {
        T t = gson.fromJson(source, cl);
        return t;
    }

    public static <T> T fromJson(JSONObject source, Class<T> cl) {
        T t = gson.fromJson(source.toString(), cl);
        return t;
    }

    public static Object fromJson(String json, Type type) {

        return gson.fromJson(json, type);
    }




    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }



}
