package com.ppfuns.ui.view.player;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.ppfuns.model.entity.Play;
import com.ppfuns.model.entity.PlayerActions;
import com.ppfuns.ui.view.player.type.BPlayer;
import com.ppfuns.ui.view.player.type.PlayerType;
import com.ppfuns.util.BroadcastUtils;
import com.ppfuns.util.LogUtils;
import com.ppfuns.vod.R;

/**
 * SuperVideoPlayer
 */
public class SuperVideoPlayers extends VideoPlayer {

    public final static String TAG = "SuperVideoPlayers";

    //默认超时时间
    private final static long TIMEOUTDEFAULT = 10000;

    public static int TIME_SHOW_CONTROLLER = 6000;

    private VideoViews mVideoView;//播放器
    private IMediaControl mMediaController;//控制器

    private View mProgressBarView;//加载中按钮
    private View mError_tips;
    private View mBtnRetry;

    private int mKeycode = KeyEvent.KEYCODE_DPAD_RIGHT; // 判断进度条快进还是快退
    private final int PLAYER_ACTIONS_TYPE = 11;

    private boolean mIsBuffing = false;  //是否在缓冲

    private int mBufferPercent;

    /**
     * 是否自动播放
     */
    private boolean mAutoPlay = true;

    /**
     * 是否自动隐藏控制面板
     */
    private boolean mAutoHideController = true;


    /**
     * 播放器类型（时移？回看？）
     */
    private PlayerType mPlayerType;


    public void setPlayerType(PlayerType mPlayerType) {
        this.mPlayerType = mPlayerType;
    }

    private Handler mHandler = new Handler();


    public SuperVideoPlayers(Context context) {
        this(context, null);
    }

    public SuperVideoPlayers(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context.getApplicationContext(), attrs, defStyleAttr);
        initView(context.getApplicationContext());
    }

    public SuperVideoPlayers(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 更新播放时间
     */
    private Runnable mUpdatePlayTime = new Runnable() {
        @Override
        public void run() {
            updatePlayTime();
            updatePlayProgress();
            mHandler.removeCallbacks(mUpdatePlayTime);
            mHandler.postDelayed(mUpdatePlayTime, 500);
        }
    };

    /**
     * 隐藏控制面板
     */
    private Runnable mHideController = new Runnable() {
        @Override
        public void run() {
            showOrHideController(false);
        }
    };

    /**
     * 超时
     */
    private Runnable mTimeOutError = new Runnable() {

        @Override
        public void run() {
            Log.e(TAG, "open video time out : Uri = " + mPlay);

            close();
            mHandler.removeCallbacks(mTimeOutError);
            mProgressBarView.setVisibility(GONE);
            mVideoPlayCallback.onError();
            actionsCollection(PlayerActions.PlayerStatus.STATUS_ERROR);
//            }
        }
    };

    public void setControlView(@NonNull View view) {
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(view, lp);
        mMediaController = (IMediaControl) view;
        mMediaController.setMediaControl(mMediaControl);
        invalidate();
    }

    private int mForwardAndRewindSpeed = 1000;


    public void setAutoPlay(boolean auto) {
        this.mAutoPlay = auto;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.i(TAG, "KeyCode=" + keyCode);

        if (!(keyCode == KeyEvent.KEYCODE_VOLUME_UP) && !(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN))
            showOrHideController(true);

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            hideProgressView();
            mHandler.removeCallbacks(mProgress);
            mHandler.postDelayed(mProgress, 1000);
            if (mProgressBarView.getVisibility() != VISIBLE) {
                this.mPlayerType.rewind(mMediaController, mVideoView);
            }
            mHandler.removeCallbacks(mCustomPlayTime);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            hideProgressView();
            mHandler.removeCallbacks(mProgress);
            mHandler.postDelayed(mProgress, 1000);
            if (mProgressBarView.getVisibility() != VISIBLE) {
                this.mPlayerType.forward(mMediaController, mVideoView);
            }
            mHandler.removeCallbacks(mCustomPlayTime);
        } else if (keyCode != KeyEvent.KEYCODE_BACK && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            mMediaController.showRFControl();
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            if (mIsBuffing) {
                return true;
            }
            this.mPlayerType.enter(mMediaControl, mVideoView, mPlay);
        }
        return super.onKeyDown(keyCode, event);
    }

    long MIN_CLICK_DELAY_TIME = 500;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (mPlayerType != null) {
                mKeycode = KeyEvent.KEYCODE_DPAD_LEFT;
                this.mPlayerType.seekTo(mVideoView);
            }
            mHandler.removeCallbacks(mCustomPlayTime);

//            long time = System.currentTimeMillis();
//            if (time - lastFoTime <= 200) {
//                return true;
//            }
//            lastFoTime = time;
//            if (isPlaying())
//                mHandler.postDelayed(mCustomPlayTime, MIN_CLICK_DELAY_TIME);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (mPlayerType != null) {
                mKeycode = KeyEvent.KEYCODE_DPAD_RIGHT;
                this.mPlayerType.seekTo(mVideoView);
            }
            mHandler.removeCallbacks(mCustomPlayTime);
//            long currentTime = Calendar.getInstance().getTimeInMillis();
//            if (currentTime - lastFoTime > MIN_CLICK_DELAY_TIME) {
//            if (isPlaying())
//                mHandler.postDelayed(mCustomPlayTime, MIN_CLICK_DELAY_TIME);
//                lastFoTime = currentTime;
//            }
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 自计算播放时间
     */
    private Runnable mCustomPlayTime = new Runnable() {
        @Override
        public void run() {
//            customPlayTime();
            customPlayProgress();
            mHandler.removeCallbacks(mCustomPlayTime);
            mHandler.postDelayed(mCustomPlayTime, 1000);
        }
    };

    private void customPlayProgress() {
        if (mPlayerType != null)
            mPlayerType.customPlayProgress();
    }

    /**
     * 获取进度条当前位置
     */
    public long getCurrentPosition() {
        return mVideoView.getCurrentPosition();
    }

    @Override
    public void play() {
        mVideoView.start();
        if (mMediaController == null) {
            return;
        }
        mHandler.removeCallbacks(mHideController);
        mHandler.removeCallbacks(mUpdatePlayTime);
        mHandler.removeCallbacks(mCustomPlayTime);

        mHandler.postDelayed(mUpdatePlayTime, 0);
        mHandler.postDelayed(mHideController, 0);
        mHandler.postDelayed(mCustomPlayTime, 0);
        if (mVideoPlayCallback != null) {
            mVideoPlayCallback.onStartPlay(mObject);
            actionsCollection(PlayerActions.PlayerStatus.STATUS_PLAY);
        }
        mMediaController.setPlayState(IMediaControl.PLAY);
        resetHideTimer();
        resetUpdateTimer();
    }

    @Override
    public void startPlayer(Play play, long msec) {
        mPlay = play;
        mHandler.postDelayed(mProgress, 1000);
        mMediaController.setInfoText(play.info);
        mMediaController.setTitleText(play.title);
        LogUtils.i(TAG, "videoTitle: " + play.title);
        startPlayer(play.uri, msec);
    }

    @Override
    public void startPlayer(String playUrl, long msec) {
        mError_tips.setVisibility(GONE);
        mVideoView.setOnPreparedListener(mOnPreparedListener);
        mVideoView.setOnErrorListener(mOnErrorListener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            LogUtils.i(TAG, "_setOnInfoListener");
            mVideoView.setOnInfoListener(mOnInfoListener);
        }
        mVideoView.setVideoURI(Uri.parse(playUrl));
        mVideoView.setVisibility(VISIBLE);
        startPlayVideo(msec);
    }
    /**
     * 暂停播放
     *
     * @param showSeekbar 是否显示控制条
     */
    @Override
    public void pause(boolean showSeekbar) {
        mVideoView.pause();
        if (mMediaController == null) {
            return;
        }
        mHandler.removeCallbacks(mHideController);
        mHandler.removeCallbacks(mUpdatePlayTime);
        mHandler.removeCallbacks(mCustomPlayTime);
        if (mVideoPlayCallback != null) {
            mVideoPlayCallback.onPausePlay();
            actionsCollection(PlayerActions.PlayerStatus.STATUS_PAUSE);
        }
        mMediaController.setPlayState(IMediaControl.PAUSE);
        stopHideTimer(showSeekbar);
    }

    @Override
    public void seekTo(long msec) {
        mVideoView.seekTo((int)msec);
    }

    @Override
    public void seekTo(long msec, boolean showSeekBar) {
        gotoPosition((int) msec, showSeekBar);
    }

    /**
     * 设置进度条跳转到具体的位置
     */
    private void gotoPosition(int playTime) {
        gotoPosition(playTime, true);
    }

    private void gotoPosition(int playTime, boolean seekTo) {
        LogUtils.i(TAG, "gotoPosition: " + seekTo);
        if (mMediaController == null) {
            return;
        }
        mHandler.removeCallbacks(mCustomPlayTime);
        int progressBarMax = mMediaController.getProgressBarMax();
        long allTime = mVideoView.getDuration() == 0 ? ((mDuration == 0) ? 0 : mDuration) : mVideoView.getDuration();
        int forwardPosition = playTime > allTime ? 0 : playTime;
        int loadProgress = mVideoView.getBufferPercentage();
        int progress = (int) ((float) forwardPosition * progressBarMax / allTime);
        LogUtils.i(TAG, "gotoPosition_allTime: " + allTime + ", forwardPosition: " + forwardPosition + ", playTime: " + playTime + ",progress :" + progress);
        mMediaController.setProgressBar(progress, loadProgress);
        showOrHideController(true);
        if (mPlayerType != null)
            ((BPlayer) mPlayerType).setCustomCurrentTime(forwardPosition);
        mHandler.post(mCustomPlayTime);
        if (seekTo) {
            seekTo(forwardPosition);
//            mVideoView.seekTo(forwardPosition);
        }
    }

    // 播放器控制条的回调函数
    private IMediaControl.ControlCallback mMediaControl = new IMediaControl.ControlCallback() {

        /**
         * 播放回调
         */
        @Override
        public void onPlayTurn() {
            if (mVideoView.isPlaying()) {
                pause(true);
                mVideoPlayCallback.onPausePlay();
                actionsCollection(PlayerActions.PlayerStatus.STATUS_PAUSE);
            } else {
                play();
                mVideoPlayCallback.onStartPlay(mObject);
                actionsCollection(PlayerActions.PlayerStatus.STATUS_PLAY);
            }
        }

        /**
         * 进度条回调
         * @param state    进度条状态
         * @param progress 进度
         */
        @Override
        public void onProgressTurn(@IMediaControl.ProgressState int state, int progress) {
            if (state == IMediaControl.START) {
                mHandler.removeCallbacks(mHideController);
                mHandler.postDelayed(mHideController, 0);
            } else if (state == IMediaControl.STOP) {
                resetHideTimer();
            } else {
                if (mMediaController == null) return;
                long allTime = mVideoView.getDuration() == 0 ? ((mDuration == 0) ? 0 : mDuration) : mVideoView.getDuration();
                int time = (int) ((float) progress * allTime / mMediaController.getProgressBarMax());
                seekTo(time);
                updatePlayTime();
            }
        }

        @Override
        public void handleCtrl() {
            showOrHideController(true);
        }
    };

    // 当MediaPlayer准备好后触发该回调
    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mDuration = mediaPlayer.getDuration();
            if (mediaPlayer != null) {
//                mediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
                mediaPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
            }
            LogUtils.i(TAG, "mDuration: " + mDuration);
            mVideoView.setVisibility(VISIBLE);
            mHandler.removeCallbacks(mProgress);
            hideProgressView();
            mHandler.removeCallbacks(mTimeOutError);
            mHandler.removeCallbacks(mCustomPlayTime);
            mHandler.postDelayed(mCustomPlayTime, 0);
            mVideoPlayCallback.onStartPlay(mObject);
            SuperVideoPlayers.this.requestFocus();
            actionsCollection(PlayerActions.PlayerStatus.STATUS_PLAY);
            if (!mIsShowAd) {
                showOrHideController(true);
            } else {
                showOrHideController(false);
            }
            LogUtils.i(TAG + " mOnPreparedListener info", "OnPreparedListener");
//            mHandler.removeCallbacks(mHideController);
        }
    };

    private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            LogUtils.i(TAG, "_onSeekComplete");
            mHandler.removeCallbacks(mProgress);
            hideProgressView();
            if (mp.isPlaying()) {
                LogUtils.i(TAG, "_onSeekComplete_isplaying");
                mHandler.removeCallbacks(mCustomPlayTime);
                mHandler.postDelayed(mCustomPlayTime, 500);
            }
            LogUtils.d(TAG, "seek_status: " + mKeycode);
            if (mKeycode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                actionsCollection(PlayerActions.PlayerStatus.STATUS_FORWARD);
            } else {
                actionsCollection(PlayerActions.PlayerStatus.STATUS_REWIND);
            }
        }
    };

    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            LogUtils.i(TAG, "---------------MediaPlayer  mOnErrorListener--------------");
            LogUtils.i(TAG, " mOnErrorListener " + " what = " + what + " \textra = " + extra);
//            Toast.makeText(mContext, "无法播放视频", Toast.LENGTH_SHORT).show();
            close();
            mHandler.removeCallbacks(mCustomPlayTime);

            mVideoPlayCallback.onError();
            actionsCollection(PlayerActions.PlayerStatus.STATUS_ERROR);
            mVideoPlayCallback.onCloseVideo();
            LogUtils.i(TAG, "---------------MediaPlayer  mOnErrorListener end--------------");
            return true;
        }
    };


    private MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            mBufferPercent = percent * mp.getDuration();
        }
    };

    private MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            LogUtils.i(TAG, "---------------MediaPlayer  mOnInfoListener--------------");
            LogUtils.i(TAG, "mOnInfoListener what= " + what + "\textra = " + extra);
            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                LogUtils.i(TAG, "buffering_start");
                mHandler.postDelayed(mProgress, 1000);
//                showProgressView(true);
                mIsBuffing = true;
                mHandler.removeCallbacks(mCustomPlayTime);
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                //此接口每次回调完START就回调END,若不加上判断就会出现缓冲图标一闪一闪的卡顿现象
                LogUtils.i(TAG, "buffering_end");
                mIsBuffing = false;
                if (mp.isPlaying()) {
//                    mProgressBarView.setVisibility(View.GONE);
                    hideProgressView();
                }
                mHandler.removeCallbacks(mProgress);
                mHandler.post(mCustomPlayTime);
            }
            LogUtils.i(TAG, "---------------MediaPlayer  mOnInfoListener end --------------");
            return true;
        }
    };

    // 当MediaPlayer播放完成后触发该回调
    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            LogUtils.i(TAG, "---------------MediaPlayer  mOnCompletionListener--------------");
            stopHideTimer(true);
            if (mMediaController != null) {
                mMediaController.playFinish(mVideoView.getDuration());
            }
            mHandler.removeCallbacks(mCustomPlayTime);
            mVideoPlayCallback.onPlayFinish(mObject);
            LogUtils.i(TAG, "---------------MediaPlayer  mOnCompletionListener end--------------");
        }
    };

    public VideoPlayCallbackImpl getVideoPlayCallback() {
        return mVideoPlayCallback;
    }

//    /**
//     * 暂停播放
//     *
//     * @param isShowController 是否显示控制条
//     */
//    public void pausePlay(boolean isShowController) {
//        mVideoView.pause();
//        if (mMediaController == null) {
//            return;
//        }
//        mHandler.removeCallbacks(mHideController);
//        mHandler.removeCallbacks(mUpdatePlayTime);
//        mHandler.removeCallbacks(mCustomPlayTime);
//        if (mVideoPlayCallback != null) {
//            mVideoPlayCallback.onPausePlay();
//            actionsCollection(PlayerActions.PlayerStatus.STATUS_PAUSE);
//        }
//        mMediaController.setPlayState(IMediaControl.PAUSE);
//        stopHideTimer(isShowController);
//    }

    /**
     * 关闭视频
     */
    @Override
    public void close() {

        LogUtils.i(TAG, "----------close---------");
        LogUtils.i(TAG, "mVideoView.isPlaying ?" + mVideoView.isPlaying());
//        if (!mVideoView.isPlaying()) return;
        mHandler.removeCallbacksAndMessages(null);
        if (mMediaController != null) {
            mMediaController.setPlayState(IMediaControl.PAUSE);
        }
//        mMediaController.setPlayState(IMediaControl.PAUSE);
        stopHideTimer(true);

//        mVideoView.pause();
        mMediaController = null;
        mVideoView.stopPlayback();
        mVideoView.setVisibility(GONE);
        LogUtils.i(TAG, "----------end---------");
    }

    /**
     * 判断是否自动隐藏控制器
     */
    public boolean isAutoHideController() {
        return mAutoHideController;
    }

    public void setAutoHideController(boolean autoHideController) {
        mAutoHideController = autoHideController;
    }


    /**
     * 初始化View
     *
     * @param context
     */
    private void initView(Context context) {
        mContext = context;
        View.inflate(context, R.layout.super_vodeo_players_layout, this);
        mVideoView = (VideoViews) findViewById(R.id.video_view);
//        mMediaController = (MediaController) findViewById(R.id.controller);
        mProgressBarView = findViewById(R.id.progressbar);//加载中的那个圆圈
        mProgressBarView.setFocusable(false);
//        mProgressBarView.setBackgroundResource(R.color.transparent);
        mError_tips = findViewById(R.id.error_tips);
        mBtnRetry = findViewById(R.id.btn_retry);

        mHandler.removeCallbacks(mProgress);

        if (mMediaController == null) {
            return;
        }
        mMediaController.setMediaControl(mMediaControl);
    }

    @Override
    public void replay() {
        if (mVideoView == null) {
            return;
        }
        if (mIsShowAd) {
            mMediaController.hideControl();
        } else {
            mMediaController.showControl();
        }
        seekTo(0);
    }

    /**
     * 播放视频
     * should called after setVideoPath()
     */
    private void startPlayVideo(long seekTime) {
        resetUpdateTimer();
        resetHideTimer();
        mVideoView.setOnCompletionListener(mOnCompletionListener);
//        mVideoPlayCallback.onStartPlay();
        if (mAutoPlay) {
            mVideoView.start();
            mHandler.postDelayed(mTimeOutError, TIMEOUTDEFAULT);
            if (seekTime > 0) {
                seekTo(seekTime);
                if (mPlayerType != null)
                    ((BPlayer) mPlayerType).setCustomCurrentTime(seekTime);
            }
            if (mMediaController == null) {
                return;
            }
            if (!mIsShowAd) {
                mMediaController.setPlayState(IMediaControl.PLAY);
//                mMediaController.showControl();
            } else {
                mMediaController.hideControl();
            }

        }
    }

    private void actionsCollection(int status) {
        if (mContext == null || mDuration <=0 || mPlay == null) {
            return;
        }
        PlayerActions actions = new PlayerActions();
        actions.serviceId = mPlay.albumId;
        actions.serviceName = mPlay.title;
        actions.columnID = mPlay.columnID;
        actions.duration = (int) mDuration;
        actions.restTime = (int)(mDuration - getCurrentPosition() / 1000);
        actions.status = status;
        actions.url = mPlay.uri;
        actions.serviceIndex = mPlay.index;
        BroadcastUtils.actionCollection(mContext, PLAYER_ACTIONS_TYPE, actions);
    }

    /**
     * 更新播放的进度时间
     */
    private void updatePlayTime() {
        mPlayerType.updatePlayTime(mVideoView);
    }

    /**
     * 更新播放进度条
     */
    private void updatePlayProgress() {
        mPlayerType.updatePlayProgress(mVideoView);
    }

    /**
     * 显示loading圈
     *
     * @param isTransparentBg isTransparentBg
     */
    public void showProgressView(Boolean isTransparentBg) {
        LogUtils.d(TAG, "showProgressView");
        mProgressBarView.setVisibility(VISIBLE);
        mProgressBarView.bringToFront();
        if (!isTransparentBg) {
            mProgressBarView.setBackgroundResource(android.R.color.black);
        } else {
            mProgressBarView.setBackgroundResource(android.R.color.transparent);
        }
    }

    public Runnable mProgress = new Runnable() {
        @Override
        public void run() {
            LogUtils.d(TAG, "run_mProgress");
            showProgressView(true);
        }
    };


    public void hideProgressView() {
        LogUtils.d(TAG, "hideProgressView");
        if (mProgressBarView != null && View.VISIBLE == mProgressBarView.getVisibility()) {
            mProgressBarView.setVisibility(INVISIBLE);
        }
    }

    /**
     * 控制器的显示与隐藏
     */
    public void showOrHideController(boolean isKey) {
        if (mMediaController == null) {
            return;
        }

        LogUtils.i(TAG, "showOrHideController ");
        LogUtils.i(TAG, "mMediaController isShowControl ?" + mMediaController.isShowControl());
        if (mIsShowAd) {
            mMediaController.hideControl();
            return;
        }
//        if (mMediaController.isShowControl()) {
//            if (isKey) {// 按键触发的情况
//                resetHideTimer();
//            } else {
//                mMediaController.hideControl();
//
//                mPlayerType.updateSeekState(false);
//            }
//        } else {
//            if (!isShowMenu) {
//                mMediaController.showControl();
//                mPlayerType.updateSeekState(false);
//                resetHideTimer();
//            }
//        }
        if (isKey) {
            if (!mIsShowMenu) {
                resetHideTimer();
                mMediaController.showControl();
                mPlayerType.updateSeekState(false);
            }
        } else {
            if (mMediaController.isShowControl()) {
                resetHideTimer();
                mMediaController.hideControl();
                mPlayerType.updateSeekState(false);
            }
        }
    }

    public boolean isShowMediaController() {
        boolean isShowing = false;
        if (mMediaControl != null) {
            isShowing = mMediaController.isShowControl();
        }
        return isShowing;
    }

    /**
     * 重置隐藏定时
     */
    private void resetHideTimer() {
        if (!isAutoHideController()) return;

        LogUtils.i(TAG, "resetHideTimer");
        mHandler.removeCallbacks(mHideController);

        mHandler.postDelayed(mHideController, TIME_SHOW_CONTROLLER);
    }

    /**
     * 取消隐藏定时
     *
     * @param isShowController 是否显示控制面板
     */
    private void stopHideTimer(boolean isShowController) {
        if (mMediaController == null) {
            return;
        }
        mHandler.removeCallbacks(mUpdatePlayTime);
        if (isShowController)
            mMediaController.showControl();
    }

    /**
     * 重置更新播放时间
     */
    private void resetUpdateTimer() {
        mHandler.removeCallbacks(mUpdatePlayTime);

        mHandler.postDelayed(mUpdatePlayTime, 0);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtils.i(TAG, "-------------onDetachedFromWindow--------------");
        closeWhenDetached();
        mHandler.removeCallbacks(mTimeOutError);
        mHandler.removeCallbacks(mUpdatePlayTime);
        mHandler.removeCallbacks(mHideController);
        mHandler.removeCallbacks(mCustomPlayTime);
        actionsCollection(PlayerActions.PlayerStatus.STATUS_CLOSE);
    }

    public void closeWhenDetached() {
        LogUtils.i(TAG, "mVideoView.isPlaying ?" + mVideoView.isPlaying());

        if (mMediaController == null) {
            LogUtils.i(TAG, "MediaController is null  ?" + (mMediaController == null));
            return;
        }
//        mMediaController.setPlayState(IMediaControl.PAUSE);
        stopHideTimer(false);

        mVideoView.stopPlayback();
        mVideoView.setVisibility(GONE);
    }

    @Override
    public long getDuration() {
        if(mDuration <= 0) {
            return 0;
        } else {
            return mDuration;
        }
    }

    @Override
    public boolean isPlaying() {
        return mVideoView.isPlaying();
    }

    @Override
    public void stop() {
        mHandler.removeCallbacksAndMessages(null);
        mPlayerType.init();
        mVideoView.stopPlayback();
        mVideoView.setVisibility(GONE);
    }

    public void setVolume(float volume) {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
        int streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                (int) (volume * streamMaxVolume),
                AudioManager.FLAG_SHOW_UI);
    }
}