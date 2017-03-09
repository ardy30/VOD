package com.ppfuns.ui.view.player;

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by lenovo on 2016/6/17.
 */
public interface IMediaControl {


    /**
     * 播放样式 展开、缩放
     */
    int EXPAND = 1;
    int SHRINK = 2;

    @IntDef({EXPAND, SHRINK})
    @Retention(RetentionPolicy.SOURCE)
    @interface PageType {

    }


    /**
     * 播放状态 播放 暂停
     */
    int PLAY = 1;
    int PAUSE = 2;

    @IntDef({PLAY, PAUSE})
    @Retention(RetentionPolicy.SOURCE)
    @interface PlayState {

    }


    int START = 1;
    int DOING = 2;
    int STOP = 3;

    @IntDef({START, DOING, STOP})
    @Retention(RetentionPolicy.SOURCE)
    @interface ProgressState {

    }


    void setTitleText(@NonNull CharSequence string);

    void setInfoText(@NonNull CharSequence string);

    /**
     * 设置进度条
     *
     * @param progress     进度
     * @param loadProgress 预加载精度
     */
    void setProgressBar(@IntRange(from = 0, to = 100) int progress, @IntRange(from = 0, to = 100) int loadProgress);

    @IntRange(from = 0, to = 100)
    int getProgressFromSeekBar();

    /**
     * 设置播放状态
     *
     * @param playState
     */
    void setPlayState(@IMediaControl.PlayState int playState);


    void nextPlay();

    void lastPlay();

    void setProgressBarMax(int max);

    int getProgressBarMax();

    /**
     * 控制器是否展示
     *
     * @return
     */
    boolean isShowControl();

    /**
     * 显示控制器
     *
     * @return
     */
    void showControl();

    /**
     * 隐藏控制器
     *
     * @return
     */
    void hideControl();

    //
    void playFinish(int duration);


    /**
     * 隐藏快进快退
     */
    void hideRFControl();

    /**
     * 显示
     */
    void showRFControl();


    /**
     * 显示剧集选择
     */
    void showDramaSelect();

    /**
     * 隐藏剧集选择
     */
    void hideDramaSelect();

    /**
     * s设置毁掉
     *
     * @param mMediaControl
     */
    void setMediaControl(ControlCallback mMediaControl);

    void setPlayProgressTxt(int nowSecond, int allSecond);

    void setPopupText(int nowSecond);

    void forceLandscapeMode();

    void initTrimmedMode();


    interface ControlCallback {
        /**
         * 点击播放按钮
         */
        void onPlayTurn();


        /**
         * 进度条点击
         *
         * @param state    进度条状态
         * @param progress 进度
         */
        void onProgressTurn(@IMediaControl.ProgressState int state, int progress);

        /**
         *
         */
        void handleCtrl();
    }

}
