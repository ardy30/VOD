package com.ppfuns.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zpf on 2016/12/27.
 */

public class BitmapUtils {

    private static final String TAG = BitmapUtils.class.getSimpleName();

    public static void saveBitmap(String filePath, String fileName, Bitmap bm) {
        LogUtils.i(TAG, "saveBitmap");
        boolean canMakeDirs = FileUtils.makeDirs(filePath);
        if (canMakeDirs) {
            if (TextUtils.isEmpty(fileName) || bm == null) {
                return;
            }
            File file = new File(filePath, fileName);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
            } catch (IOException e) {
                LogUtils.e(TAG, e.toString());
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        LogUtils.e(TAG, e.toString());
                    }
                }
                if (bm != null) {
                    bm.recycle();
                }
            }
        } else {
            if (bm != null) {
                bm.recycle();
            }
        }
    }

    public static Bitmap scaleBitmap(Context context, Bitmap bm) {
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;
        Bitmap bitmap = scaleBitmap(width, height, bm);
        return bitmap;
    }

    /**
     * 宽高拉伸或缩放至目标宽高
     *
     * @param width  目标宽度
     * @param height 目标长度
     * @param bm
     */
    public static Bitmap scaleBitmap(int width, int height, Bitmap bm) {
        LogUtils.i(TAG, "scaleBitmap");
        if (bm == null) {
            return null;
        }
        int bWidth = bm.getWidth();
        int bHeight = bm.getHeight();
        LogUtils.d(TAG, "width: " + width + ", height: " + height + ", bm width: " + bWidth + ", height: " + bHeight);

        Bitmap targetBitmap = null;
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(bm, width, height, true);
        if (scaleBitmap != null) {
            try {
                targetBitmap = Bitmap.createBitmap(scaleBitmap, 0, 0, width, height);
                LogUtils.d(TAG, "target width: " + targetBitmap.getWidth() + ", height: " + targetBitmap.getHeight());
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
            } finally {
                scaleBitmap.recycle();
            }
        }
        return targetBitmap;
    }
}
