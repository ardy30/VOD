package com.ppfuns.util;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by zpf on 2016/6/20.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private Context mContext = null;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static volatile CrashHandler instance = null;

    public static CrashHandler getInstance() {
        if (instance == null) {
            synchronized (CrashHandler.class) {
                if (instance == null) {
                    instance = new CrashHandler();
                }
            }
        }
        return instance;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        LogUtils.e("程序挂掉了 ");
        // 1.获取当前程序的版本号. 版本的id
        String versionInfo = SysUtils.getAppVersionName(mContext);

        // 2.获取手机的硬件信息.
        String mobileInfo = SysUtils.getDeviceInfo();

        // 3.把错误的堆栈信息 获取出来
        String errorInfo = getErrorInfo(ex);

//        ex.printStackTrace();
        // 4.把所有的信息 还有信息对应的时间 提交到服务器
        //// TODO: \添加提交信息
//        LogUtils.e(mobileInfo);
//        LogUtils.e(errorInfo);
        LogUtils.e(errorInfo);
        MobclickAgent.reportError(mContext, ex);
//        //干掉当前的程序
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 获取错误的信息
     *
     * @param arg1
     * @return
     */
    private String getErrorInfo(Throwable arg1) {
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        arg1.printStackTrace(pw);
        pw.close();
        String error = writer.toString();
        return error;
    }


}
