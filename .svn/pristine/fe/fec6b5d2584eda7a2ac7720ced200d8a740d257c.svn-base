package com.ppfuns.util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.TypedValue;

/**
 * 创建者     庄丰泽
 * 创建时间   2016-06-14 21:09
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class UIUtils {

    public static void startActivity(Context context, Class cl) {
        Intent intent = new Intent(context, cl);
        context.startActivity(intent);
    }


    private static Context mContext;
    private static Handler mHandler;


    public static void init(Context baseApplication) {
        mContext = baseApplication;
        mHandler = new Handler();
    }

    /**
     * 提交任务，主线程更新UI
     *
     * @param task
     */
    public static void post(Runnable task) {
        mHandler.post(task);
    }

    /**
     * 延时任务
     *
     * @param task
     * @param delay
     */
    public static void post(Runnable task, long delay) {
        mHandler.postDelayed(task, delay);
    }

    public static void remove(Runnable task) {
        mHandler.removeCallbacks(task);
    }

    public static int dp2Px(int value) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, value, mContext.getResources().getDisplayMetrics());
        return (int) (px + 0.5f);
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
