package com.ppfuns.ui.view.player.type;

import android.widget.VideoView;

import com.ppfuns.model.entity.Play;
import com.ppfuns.ui.view.player.IMediaControl;
import com.ppfuns.ui.view.player.MediaController;
import com.ppfuns.ui.view.player.SuperVideoPlayers;
import com.ppfuns.ui.view.player.VideoViews;
import com.ppfuns.util.LogUtils;

import java.util.Calendar;

/**
 * Created by zpf on 2016/7/19.
 */
public class BPlayer implements PlayerType {


    private static final String TAG = "BPlayer";


    private SuperVideoPlayers superVideoPlayers = null;

    private MediaController mediaController;
    private Play url;


    //当前系统时间；
    private long mSystemCurrentTime;
    // 直播开始系统时间
    private long mStartSystemTime;
    //直播结束系统时间
    private long mEndSystemTime;

    //当前系统时间seekBar位置
//    private long mSystemCurrentTimeSeekBarPercent;
    //进度条，圆点位置
    private long mSeekBarThumbIndex;
    //从开始，到圆点之间的时间长
    private long mSeekBarThumbPosition;

    //移动前的圆点位置
    private long mSeekBarThumbIndexOld;

    //是否第一次进入
    private boolean isFirstOpenTimeShift = true;
    //这个跟上面是一样的，不过因为要修改时间进度显示的字符串，而在第一次updatePlayProgress
    //会将isFirstOpenTimeShift的值修改，所以用两个值区分开
    //// TODO: 2016/7/28  @zpf 需要优化，
    private boolean isFirstOpenTimeShift2 = true;

    //是否跳动过
    private boolean isSeek = false;


    private boolean isSeekToLast;
    private boolean isSeekToNext;


    //视频总时长
    private long mAllTime = 0;
    //一次跳动百分比
    private int mSeekPercent = 10;

    //总拖动进度
    private int mSeekToPositionAll = 0;

    //是否正在拖动，拖动时不修改进度时间，隐藏控制器时会将值改为false，
    private boolean isSeeking = false;

    private long mCustomCurrentTime = 0L;
    private long mCustomCurrentTimeAll = 0L;

    public BPlayer() {
        this.isFirstOpenTimeShift = true;
        isFirstOpenTimeShift2 = true;
        isSeek = false;
    }

    public void setmStartSystemTime(long mStartSystemTime) {
        this.mStartSystemTime = mStartSystemTime;
//        this.mStartSystemTime = 1472867530000L;
    }

    public void setmEndSystemTime(long mEndSystemTime) {
        this.mEndSystemTime = mEndSystemTime;
//        this.mEndSystemTime = 1472867990000L;
    }

    public void setMediaController(MediaController mediaController) {
        this.mediaController = mediaController;
    }

    public void setSuperVideoPlayers(SuperVideoPlayers superVideoPlayers) {
        this.superVideoPlayers = superVideoPlayers;
    }

    public void setAllTime(int mAllTime) {
        this.mAllTime = mAllTime;
    }

    @Override
    public void rewind(IMediaControl mediaController, VideoViews videoViews) {

        if (mediaController == null) {
//        if (mediaController == null || videoViews.isPlaying()) {
            return;
        }
        isSeeking = true;

        int seekTime = 0;
        this.isSeek = true;
        LogUtils.i(TAG, "----------rewind----------");
        mSeekBarThumbIndex = mediaController.getProgressFromSeekBar();
        //如果当前进度，小于跳动长度
        if (mSeekBarThumbIndex - mSeekPercent < 0) {
            isSeekToLast = true;
            seekTime = (int) -mSeekBarThumbIndex;
            mSeekBarThumbIndex = -mSeekBarThumbIndex;
        } else {
            seekTime = -mSeekPercent;
            mSeekBarThumbIndex -= mSeekPercent;
        }
        mSeekToPositionAll += seekTime;
        mSeekBarThumbIndexOld = mSeekBarThumbIndex - mSeekToPositionAll;
        mediaController.setProgressBar((int) mSeekBarThumbIndex, (int) 0);
        //计算popupwindow 显示时间
        int PopupTime = (int) ((mSeekBarThumbIndex) * mAllTime / mediaController.getProgressBarMax());
        //修改popupwindow 显示时间
        mediaController.setPopupText(PopupTime);
        LogUtils.i(TAG, "----------rewind end----------");
    }

    @Override
    public void forward(IMediaControl mediaController, VideoViews videoViews) {
        if (mediaController == null) {
//        if (mediaController == null || videoViews.isPlaying()) {
            return;
        }
        LogUtils.i(TAG, "----------forward----------");

        isSeeking = true;
        this.isSeek = true;
        mSeekBarThumbIndex = mediaController.getProgressFromSeekBar();
        int seekTime = 0;
        LogUtils.i(TAG, "mSeekBarThumbIndex = " + mSeekBarThumbIndex);
        LogUtils.i(TAG, "mSeekPercent = " + mSeekPercent);
        LogUtils.i(TAG, "mediaController.getProgressBarMax() = " + mediaController.getProgressBarMax());

        //如果当前进度，小于跳动长度
        if (mSeekBarThumbIndex + mSeekPercent > mediaController.getProgressBarMax()) {
            seekTime = (int) mSeekBarThumbIndex;
            mSeekBarThumbIndex = +mSeekBarThumbIndex;
        } else {
            seekTime = mSeekPercent;
            mSeekBarThumbIndex += mSeekPercent;
        }
        mSeekToPositionAll += seekTime;
        mSeekBarThumbIndexOld = mSeekBarThumbIndex - mSeekToPositionAll;
        mediaController.setProgressBar((int) mSeekBarThumbIndex, (int) 0);
        int PopupTime = (int) ((mSeekBarThumbIndex) * mAllTime / mediaController.getProgressBarMax());
        LogUtils.i(TAG, "popuptime = " + PopupTime);
        mediaController.setPopupText(PopupTime);

        callFreeEnd(PopupTime);
        LogUtils.i(TAG, "----------forward end----------");
    }


    @Override
    public void enter(IMediaControl.ControlCallback mMediaControl, VideoViews videoViews, Play url) {
        this.url = url;
        LogUtils.i(TAG, "----------enter start----------");
        this.mAllTime = superVideoPlayers.getDuration();
        isSeeking = false;
        mMediaControl.onPlayTurn();
        mSeekToPositionAll = 0;
        LogUtils.i(TAG, "----------enter end----------");
    }

    @Override
    public void updatePlayProgress(VideoViews videoView) {
        LogUtils.i(TAG, "updatePlayProgress =");
        int mDuration = (int) superVideoPlayers.getDuration();
        if (mediaController == null) {
            return;
        }
        long allTime = superVideoPlayers.getDuration();
        if (allTime == 0) return;
        int playTime = videoView.getCurrentPosition();
        int loadProgress = videoView.getBufferPercentage();
        long progress = playTime * mediaController.getProgressBarMax() / allTime;
        boolean isAuthority = superVideoPlayers.getAuthority();
        boolean isShowAd = superVideoPlayers.getShowAd();
        long timeBorder = 15 * 60 * 1000;
        LogUtils.i("Decode", "isShowAd: " + isShowAd + ", isAuthority: " + isAuthority
                + ", playTime_free: " + playTime + ", duration: " + mDuration);
        if (mDuration != 0 && (playTime >= mDuration / 10 || playTime >= timeBorder)) {
            if (!isShowAd && !isAuthority) {
                superVideoPlayers.getVideoPlayCallback().onFreeEnd();
            }
        }
    }

    // 免费试看时间
    private void callFreeEnd (long playTime) {
        long allTime = superVideoPlayers.getDuration();
        if (allTime == 0)
            return;
        boolean isAuthority = superVideoPlayers.getAuthority();
        boolean isShowAd = superVideoPlayers.getShowAd();
        long timeBorder = 15 * 60 * 1000;
        LogUtils.i("Decode", "isShowAd: " + isShowAd + ", isAuthority: " + isAuthority
                + ", playTime_free: " + playTime + ", duration: " + allTime);
        if (allTime != 0 && (playTime >= allTime / 10 || playTime >= timeBorder)) {
            if (!isShowAd && !isAuthority) {
                superVideoPlayers.getVideoPlayCallback().onFreeEnd();
            }
        }
    }

    int mBufferPercentage = 0;

    @Override
    public void updatePlayTime(VideoViews videoView) {
        mBufferPercentage = videoView.getBufferPercentage();

    }

    @Override
    public void updateSeekState(boolean isSeeking) {
        this.isSeeking = isSeeking;
        mSeekToPositionAll = 0;
    }

    @Override
    public void updateProgramInfo(Play play) {
//        Context context = BaseApplication.getAppContext();
//        mediaController.setInfoText(TextUtils.isEmpty(play.last) ? context.getString(R.string.last_program, context.getString(R.string.not_program)) : context.getString(R.string.last_program, play.last));
//        mediaController.setTitleText(TextUtils.isEmpty(play.current) ? context.getString(R.string.current_program, context.getString(R.string.not_program)) : context.getString(R.string.current_program, play.current));
//        mediaController.setNextText(TextUtils.isEmpty(play.next) ? context.getString(R.string.next_program, context.getString(R.string.not_program)) : context.getString(R.string.next_program, play.next));
//        mediaController.mNext.setVisibility(View.GONE);
//        mediaController.mLast.setVisibility(View.GONE);
    }

    @Override
    public void seekTo(VideoView videoView) {


        this.mAllTime =superVideoPlayers.getDuration();
        isSeeking = false;
        //非播放状态下或者拖动进度不为0

        //获取当前进度原点位置
        mSeekBarThumbIndex = mediaController.getProgressFromSeekBar();
        LogUtils.i(TAG, "mSeekToPositionAll = " + mSeekToPositionAll);

        int allTime = videoView.getDuration() <= 0 ? (int) mAllTime : videoView.getDuration();
//            int allTime = (int) this.mAllTime;
        LogUtils.i(TAG, "allTime =" + allTime);
        if (allTime > 0) {
            LogUtils.i(TAG, "mSeekToPosition  " + mSeekToPositionAll);
            LogUtils.i(TAG, "SeekTo " + mSeekBarThumbIndex);
            //跳动时间，单位秒
            int delaytime = 0;
            mCustomCurrentTime = (long) (((float) mSeekBarThumbIndex * this.mAllTime) / mediaController.getProgressBarMax());
            int mSeekToPostionTime = (int) ((((float) mSeekBarThumbIndex / mediaController.getProgressBarMax()) * allTime));
            videoView.seekTo(mSeekToPostionTime);
        }
    }

    @Override
    public void correctionscPlayTime() {

    }

    long lastFoTime = 0L;
    static final int MIN_CLICK_DELAY_TIME = 30000;

    @Override
    public void customPlayProgress() {

        if (mediaController == null) {
            return;
        }

        if(superVideoPlayers.getShowAd()) {
            return;
        }
        mCustomCurrentTime += 1000;
//        } else {
//            mCustomCurrentTime = (long) (((float) mSeekBarThumbIndex * this.mAllTime) / mediaController.getProgressBarMax());
//            mCustomCurrentTime += 1000;
//        }
        LogUtils.i("mCustomCurrentTime = " + mCustomCurrentTime);
        this.mAllTime = superVideoPlayers.getDuration();
        if (!mediaController.isShowControl()) {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            LogUtils.i("currentTime = " + currentTime);
            LogUtils.i("lastFoTime = " + lastFoTime);
            if (currentTime - lastFoTime > MIN_CLICK_DELAY_TIME) {
                long current = superVideoPlayers.getCurrentPosition();
                if (current >= mAllTime
                        || mCustomCurrentTime <= 0
                        || current <= 0
                        || current > 0.05 * mAllTime) {

                } else {
                    mCustomCurrentTime = superVideoPlayers.getCurrentPosition();
                    lastFoTime = currentTime;
                }
            }
        }
        LogUtils.i("mCustomCurrentTime = " + mCustomCurrentTime);

//        if (!isSeeking) {
////            long currentTime = Calendar.getInstance().getTimeInMillis();
////            if (currentTime - lastFoTime > MIN_CLICK_DELAY_TIME) {
////                mCustomCurrentTime = superVideoPlayers.getCurrentPosition();
////                lastFoTime = currentTime;
////            }
//
//            this.mAllTime = mEndSystemTime - mStartSystemTime;
//            LogUtils.i("mCustomCurrentTime = " + mCustomCurrentTime);
//            int index = (int) ((float) mCustomCurrentTime * mediaController.getProgressBarMax() / this.mAllTime);
//
//
//            mediaController.setPopupText(index);
//            mediaController.setPlayProgressTxt((int) mCustomCurrentTime, (int) mAllTime);
//        } else {
//            mCustomCurrentTime = mSeekBarThumbIndex * this.mAllTime / mediaController.getProgressBarMax();
        int index = (int) ((float) mCustomCurrentTime * mediaController.getProgressBarMax() / this.mAllTime);
        LogUtils.i("allTime = " + mAllTime);
        LogUtils.i("index = " + index);
        mediaController.setPopupText(index);
        mediaController.setPlayProgressTxt((int) mCustomCurrentTime, (int) mAllTime);
        mediaController.setProgressBar(index, (int) ((float) mBufferPercentage * mediaController.getProgressBarMax() / this.mAllTime));

        mSeekBarThumbIndex = mediaController.getProgressFromSeekBar();
//        }


    }

    public long getCustomCurrentTime() {
        return mCustomCurrentTime;
    }

    public void setCustomCurrentTime(long customCurrentTime) {
        LogUtils.i("Decode", "customCurrentTime: " + customCurrentTime);
        mCustomCurrentTime = customCurrentTime;
    }

    @Override
    public void init() {
        mSeekBarThumbIndex = 0;
        mSeekToPositionAll = 0;
        mCustomCurrentTime = 0;
    }


}
