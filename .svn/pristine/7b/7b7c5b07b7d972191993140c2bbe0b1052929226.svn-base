package com.utovr.playerdemo;

import android.app.Application;
import android.content.Intent;

import com.utovr.playerdemo.util.OPENLOG;


/**
 * Created by lenovo on 2016/7/28.
 */
public class MyApplication extends Application implements
        Thread.UncaughtExceptionHandler {
    private static final String TAG = "playerDemo";

    @Override
    public void onCreate() {
        OPENLOG.initTag(TAG, true);
        super.onCreate();
        //设置Thread Exception Handler
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        System.exit(0);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
