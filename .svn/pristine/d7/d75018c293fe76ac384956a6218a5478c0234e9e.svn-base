package com.ppfuns.model;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by lenovo on 2016/6/29.
 */
public interface IAdsModule {

    /**
     * 获取构造intent的string字符串
     *
     * @return 返回对应intent字符串
     */
    String getIntentInfo();

    /**
     * 获取用于显示用的广告图片
     *
     * @return 返回广告图片的bitmap对象
     */
    Bitmap getAdsImg();

    /**
     * 刷新广告数据，接收到广告更新广播时调用
     *
     * @param context
     * @return
     */
    boolean refreshAdsData(Context context);

    /**
     * 获取广告图片的颜色
     *
     * @return 返回此广告图片的颜色
     */
    int getAdsImgColor();

    /**
     * 获取广告是否可获焦
     *
     * @return 返回次广告是否可获焦的布尔值
     */
    boolean getAdsFocusable();

    /**
     * 注册广播
     */
    void registerReceiver(Context context);

    /**
     * 注销广播
     */
    void unRegisterReceiver(Context context);

    /**
     * 增加新广告到来的监听
     *
     * @param onAdsUpdateListener
     */
    public void addOnAdsUpdateListener(AdsModule.OnAdsUpdateListener onAdsUpdateListener);

    /**
     * 移除新广告到来的监听
     *
     * @param onAdsUpdateListener
     */
    public void removeOnUpdateListener(AdsModule.OnAdsUpdateListener onAdsUpdateListener);

    /**
     * 移除所有监听
     */
    public void removeAllListenter();
}
