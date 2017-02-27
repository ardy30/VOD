package com.ppfuns.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by hmc on 2016/6/14.
 */
public class LogUtils {

    /**
     * 是否启动调试模式
     */
    public static boolean isDebug = false;

    public static void d(String msg) {
        d("DEBUG", msg);
    }

    public static void d(String tag, String msg) {
        println(Log.DEBUG, tag, msg);
    }

    public static void i(String msg) {
        i("INFO", msg);
    }

    public static void i(String tag, String msg) {
        println(Log.INFO, tag, msg);
    }

    public static void w(String msg) {
        w("warning", msg);
    }

    public static void w(String tag, String msg) {
        println(Log.WARN, tag, msg);
    }

    public static void e(String msg) {
        e("error", msg);
    }

    public static void e(String tag, String msg) {
        println(Log.ERROR, tag, msg);
    }

    private static void println(int level, String tag, String msg) {
        if (isDebug) {
            if (tag == null) {
                switch (level) {
                    case Log.ASSERT:
                        tag = "assert";
                        break;
                    case Log.DEBUG:
                        tag = "debug";
                        break;
                    case Log.INFO:
                        tag = "info";
                        break;
                    case Log.WARN:
                        tag = "warning";
                        break;
                    case Log.ERROR:
                        tag = "error";
                        break;
                    case Log.VERBOSE:
                        tag = "verbose";
                        break;
                }
            }
            Log.println(level, tag, msg);
        }
    }

    /**
     * 是否开启视窗调试模式或将log信息保存到sdka下的logcat目录下
     */
    public static boolean isDebug2ViewOrOutPut = false;

    public static String AppNameKey      = "appName";
    public static String levelKey        = "level";
    public static String messageKey      = "message";
    public static String isOnlyOutputKey = "isOnlyOutput";

    public static String Debug  = "debug";
    public static String Normal = "normal";


    /**
     * 将log打印到视窗上
     *
     * @param context 上下文
     * @param app     当前应用名
     * @param message 内容
     */
    public static void Log2View(Context context, String app, String message) {
        Log2View(context, app, Debug, message);
    }

    /**
     * 将log打印到视窗上
     *
     * @param context 上下文
     * @param app     当前应用名
     * @param level   是否显示完整 debug 完整 normal只显示前200字符
     * @param message 内容
     */
    public static void Log2View(Context context, String app, String level, String message) {
        Log2View(context, app, level, message, false);
    }

    /**
     * 将log打印到视窗上
     *
     * @param context      上下文
     * @param app          当前应用名
     * @param level        是否显示完整 debug 完整 normal只显示前200字符
     * @param message      内容
     * @param isOnlyOutput 是否只写入到sd卡上 true 只写入不显示视窗 false 写入且显示到视窗
     */
    public static void Log2View(Context context, String app, String level, String message, boolean isOnlyOutput) {
        if (isDebug2ViewOrOutPut) {
            context.startService(new Intent("com.ppfuns.debug")
                    .putExtra(messageKey, message)
                    .putExtra(levelKey, level)
                    .putExtra(AppNameKey, app)
                    .putExtra(isOnlyOutputKey, isOnlyOutput));
        }

    }


    /**
     * 关闭视窗 并清除log
     */
    public static void clearLogView(Context context) {
        if (isDebug2ViewOrOutPut) {
            context.startService(new Intent("com.ppfuns.clearLog"));
        }
    }
}
