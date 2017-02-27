package com.utovr.playerdemo.util;

import android.content.Context;

/**
 * @describe <dp和px的转化>
 */
public class DensityUtil {
    public static int dpToPx(Context context, float dpValue) {//dp转换为px
        float scale=context.getResources().getDisplayMetrics().density;//获得当前屏幕密度
        return (int)(dpValue*scale+0.5f);
    }

    public static int pxToDp(Context context,float pxValue) {//px转换为dp
        float scale=context.getResources().getDisplayMetrics().density;//获得当前屏幕密度
        return (int)(pxValue/scale+0.5f);
    }
}
