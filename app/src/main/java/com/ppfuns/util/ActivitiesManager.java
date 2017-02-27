package com.ppfuns.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;

import java.util.Stack;

/**
 * Created by hmc on 2016/8/12.
 */
public class ActivitiesManager {

    // Activity栈
    private static Stack<Activity> mActivityStack;
    // 单例模式
    private static ActivitiesManager mInstance;

    private ActivitiesManager() {

        mHandler.post(mRunable);
    }

    /**
     * 单一实例
     */
    public static ActivitiesManager getManager() {
        if (mInstance == null) {
            mInstance = new ActivitiesManager();
        }
        return mInstance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (mActivityStack == null) {
            mActivityStack = new Stack<Activity>();
        }
        if (mActivityStack.size() == 0) {
            mHandler.post(mRunable);
        }
        mActivityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = mActivityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishLastActivity() {
        Activity activity = mActivityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishFirstActivity() {
        if (mActivityStack == null) {
            return;
        }
        if (mActivityStack.size() > 3) {
            Activity activity = mActivityStack.firstElement();
            finishActivity(activity);
        } else {
            finishLastActivity();
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            mActivityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : mActivityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0; i < mActivityStack.size(); i++) {
            if (null != mActivityStack.get(i)) {
                mActivityStack.get(i).finish();
            }
        }
        mActivityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
        }
    }

    public static Handler mHandler = new Handler();

    private java.lang.String TAG = "Activity Manager";
    public Runnable mRunable = new Runnable() {
        @Override
        public void run() {
            Activity activity = null;
//            LogUtils.i(TAG, "mActivityStack size = " + mActivityStack.size());
//            LogUtils.i(TAG, "mActivityStack  " + mActivityStack);
            if (mActivityStack.size() > 3) {
                activity = mActivityStack.firstElement();
                if (activity != null) {
                    finishActivity(activity);
                }
            }
            if (mActivityStack.size() == 0) {
                mHandler.removeCallbacks(this);
                return;
            }
            mHandler.postDelayed(this, 1000);


        }
    };


}
