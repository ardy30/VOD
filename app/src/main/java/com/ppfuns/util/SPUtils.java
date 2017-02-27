package com.ppfuns.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Map;

/**
 * Created by hmc on 2016/6/15.
 */
public class SPUtils {

    private static final String TAG = SPUtils.class.getSimpleName();

    public static final String SHARED_NAME = "vod";

    /**
     * 保存数据到SharedPreferences.xml
     *
     * @param context
     * @param fileName
     * @param key
     * @param object
     * @return
     */
    public static void putData(Context context, String fileName, String key, Object object){

        if (context == null || TextUtils.isEmpty(fileName) || TextUtils.isEmpty(key) || object == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        if(object instanceof String){
            edit.putString(key, (String)object);
        }
        else if(object instanceof Integer){
            edit.putInt(key, (Integer)object);
        }
        else if(object instanceof Boolean){
            edit.putBoolean(key, (Boolean)object);
        }
        else if(object instanceof Float){
            edit.putFloat(key, (Float)object);
        }
        else if(object instanceof Long){
            edit.putLong(key, (Long)object);
        }
        else {
            LogUtils.e(TAG, "not catch class, commit failed");
            return;
        }
        edit.commit();
    }


    /**
     * 保存数据到SharedPreferences.xml
     *
     * @param context
     * @param fileName
     * @param map
     * @return
     */
    public static void putData(Context context, String fileName, Map<String, Object> map) {
        if (context == null || TextUtils.isEmpty(fileName) || map == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof String) {
                edit.putString(entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof Boolean) {
                edit.putBoolean(entry.getKey(), (Boolean) entry.getValue());
            } else if (entry.getValue() instanceof Integer) {
                edit.putInt(entry.getKey(), (Integer) entry.getValue());
            } else if (entry.getValue() instanceof Float) {
                edit.putFloat(entry.getKey(), (Float) entry.getValue());
            } else if (entry.getValue() instanceof Long) {
                edit.putLong(entry.getKey(), (Long) entry.getValue());
            } else {
                LogUtils.e(TAG, "not catch class, commit failed");
            }
        }
        edit.commit();
    }

    /**
     * 获取SharedPreferences.xml的数据
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static Object getData(Context context, String fileName, String key, Object defValue) {
        if (context == null || TextUtils.isEmpty(fileName) || TextUtils.isEmpty(key) || defValue == null) {
            return null;
        }
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        if(defValue instanceof String){
            return sp.getString(key, (String)defValue);
        } else if(defValue instanceof Integer){
            return sp.getInt(key, (Integer)defValue);
        } else if(defValue instanceof Boolean){
            return sp.getBoolean(key, (Boolean)defValue);
        } else if(defValue instanceof Float){
            return sp.getFloat(key, (Float)defValue);
        } else if(defValue instanceof Long){
            return sp.getLong(key, (Long)defValue);
        } else {
            LogUtils.e(TAG, "not catch class, commit failed");
        }
        return null;
    }

    /**
     * 删除某个设置
     *
     * @param context
     * @param key
     */
    public static void removeData(Context context, String fileName, String key) {
        if (context == null || TextUtils.isEmpty(fileName) || TextUtils.isEmpty(key)) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit = sp.edit();
        edit.remove(key);
        edit.commit();
    }
}
