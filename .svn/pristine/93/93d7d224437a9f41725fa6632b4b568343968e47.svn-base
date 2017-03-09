package com.ppfuns.ui.view;

import android.view.View;

import java.util.List;

/**
 * Created by pc1 on 2016/8/3.
 */
public interface ILoopPlayView {
    /**
     * 设置图片的网络请求地址，用以显示图片
     *
     * @param urlList
     */
    ILoopPlayView setImageViewUrls(List<String> urlList);

    ILoopPlayView setImageViewUrls(String urls[]);

    ILoopPlayView setmContentViewHeight(int mContentViewHeight);

    ILoopPlayView setmContentViewWidth(int mContentViewWidth);

    ILoopPlayView setmContentViewPadding(int mContentViewPadding);

    ILoopPlayView setmAnimatorDuration(int mAnimatorDuration);

    /**
     * 设置图片的回调地址
     *
     * @param setImageSrc
     */
    void setImageSrc(SetImageSrc setImageSrc);


    /**
     * 设置屏幕可见的显示的图片控件数量
     *
     * @param num
     */
    ILoopPlayView setShowImageNum(int num);

    /**
     * 设置第一张获取焦点的图片
     *
     * @param point
     */
    ILoopPlayView setFirstFocusItem(int point);

    interface SetImageSrc {
        void setImageSrc(View view, int point);
    }
}
