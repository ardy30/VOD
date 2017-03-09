package com.ppfuns.ui.view.player;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.ppfuns.model.entity.Play;

/**
 * Created by hmc on 2017/2/10.
 */

public abstract class VideoPlayer extends RelativeLayout {

    protected Context mContext;

    // 视频时长
    protected long mDuration = 0;

    // 是否鉴权通过
    protected boolean mIsAuthority = false;

    // 当前是否正在播放广告
    protected boolean mIsShowAd = false;

    // 当前是否显示菜单选项
    protected boolean mIsShowMenu = false;

    // 是否处于投屏
    protected boolean mIsFromDMR = false;

    protected Play mPlay;


    // 回调播放类型状态 1.播放广告 2.播放正片
    protected Object mObject;

    public VideoPlayer(Context context) {
        this(context, null);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean getAuthority() {
        return mIsAuthority;
    }

    public void setAuthority(boolean authority) {
        mIsAuthority = authority;
    }

    public boolean getShowAd() {
        return mIsShowAd;
    }

    public void setShowAd(boolean showAd) {
        mIsShowAd = showAd;
    }

    public boolean getShowMenu() {
        return mIsShowMenu;
    }

    public void setShowMenu(boolean showMenu) {
        mIsShowMenu = showMenu;
    }

    public void setFromDMR(boolean fromDMR) {
        mIsFromDMR = fromDMR;
    }

    public void setPlayTag(Object object) {
        mObject = object;
    }

    /**
     * 设置音量
     *
     * @param volume 音量0-1
     */
    public void setVolume(float volume) {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
        int streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                (int) (volume * streamMaxVolume),
                AudioManager.FLAG_SHOW_UI);
    }

    public abstract long getDuration();

    public abstract long getCurrentPosition();

    public abstract void play();

    public abstract void startPlayer(Play play, long msec);

    public abstract void startPlayer(String playUrl, long msec);

    public abstract void pause(boolean showSeekBar);

    public abstract void seekTo(long msec);

    public abstract void seekTo(long msec, boolean showSeekBar);

    public abstract boolean isPlaying();

    public abstract void stop();

    public abstract void close();

    public abstract void replay();

    protected VideoPlayCallbackImpl mVideoPlayCallback;

    /**
     * 设置播放回调
     * @param videoPlayCallback
     */
    public void setVideoPlayCallback(VideoPlayCallbackImpl videoPlayCallback) {
        mVideoPlayCallback = videoPlayCallback;
    }

    public interface VideoPlayCallbackImpl {

        void onError();

        void onPausePlay();

        void onCloseVideo();

        void onStartPlay(Object object);

        void onFreeEnd();

        void onPlayFinish(Object object);
    }

}
