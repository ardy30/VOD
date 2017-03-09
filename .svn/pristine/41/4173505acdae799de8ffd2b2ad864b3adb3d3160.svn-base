package com.ppfuns.vod.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.ppfuns.model.adapter.FilterContentAdapter;
import com.ppfuns.model.entity.AdBean;
import com.ppfuns.model.entity.AdCode;
import com.ppfuns.model.entity.Authority;
import com.ppfuns.model.entity.ContentType;
import com.ppfuns.model.entity.DisplayHistory;
import com.ppfuns.model.entity.EventBusBean;
import com.ppfuns.model.entity.PayInfo;
import com.ppfuns.model.entity.PayState;
import com.ppfuns.model.entity.Play;
import com.ppfuns.model.entity.PlayUrlBean;
import com.ppfuns.model.entity.PlayerActions;
import com.ppfuns.model.entity.VideoBean;
import com.ppfuns.ui.view.FilterMenuLayout;
import com.ppfuns.ui.view.player.MediaController;
import com.ppfuns.ui.view.player.SuperVideoPlayers;
import com.ppfuns.ui.view.player.VRVideoPlayers;
import com.ppfuns.ui.view.player.VideoPlayer;
import com.ppfuns.ui.view.player.type.BPlayer;
import com.ppfuns.util.AdCountTimer;
import com.ppfuns.util.BroadcastUtils;
import com.ppfuns.util.MultiScreenManager;
import com.ppfuns.util.LogUtils;
import com.ppfuns.util.SysUtils;
import com.ppfuns.util.ToastUtils;
import com.ppfuns.util.eventbus.EventBusUtil;
import com.ppfuns.util.eventbus.EventConf;
import com.ppfuns.util.https.AlbumRequest;
import com.ppfuns.vod.R;
import com.ppfuns.vod.receiver.ReceiverActionFlag;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerActivity extends BaseActivity {

    @BindView(R.id.time_count)
    TextView mTextView;
    @BindView(R.id.rl_main)
    RelativeLayout mRelativeLayout;
    ImageView mImageView;

    private static final String TAG = "PlayerActivity";
    private static final String ACTION = "com.ppfuns.vod.action.ACTION_VIEW_VOD_PLAY";
    public static final String ALBUM_ID = "albumId";
    public static final String POSTER = "posterUrl";
    public static final String POSITION = "timePosition";
    public static final String VIDEO = "videoId";
    public static final String INFO = "INFO";
    public static final String TITLE = "TITLE";
    public static final String URI = "URI";
    public static final String TYPE = "type";
    public static final String VIDEO_TYPE = "videoType";
    public static final String DURATION = "duration";
    public static final String ADS = "ads";
    public static final String INDEX = "index";
    public static final String VIDEO_ID = "videoId";
    public static final String COL_CONTENTID = "col_contentId";
    public static final String REQUEST_PLAY_TAG = "request_play_tag";

    public String mRequestPlayTag = "";

    private final int ALBUM_TYPE_MOVIE = 0;
    private final int ALBUM_TYPE_EPISODES = 1;

    public DisplayHistory displayHistory = null;

    private VideoPlayer mVideoPlayer;

//    FreeSeeDialog mDialog;
    FilterMenuLayout mFilterMenuLayout;
    MediaController mMediaControl = null;
    Play mPlay;
//    private PayInfo mPayInfo;
    private Authority mAuthority;
    private String mInfo = null;
    private String mTitle = null;
    private int albumId = 0;
    private String posterUrl = null;
    private String posterVUrl = null;
    private String videoId = null;
    int index = 1;
    long timePosition = 0;
    private int mAdPlayTime = 0;
    private int type = 0;
    private int mPlayVideoType = 0;
    private int mCount = 0;

    VideoBean mVideoBean;
    //    List<AdBean> mAdBeanList;
    List<PlayUrlBean> mPlayUrlList;
    List<String> mTitleList = new ArrayList<>();
    CtrlReceiver mCtrlReceiver = null;
    private NetworkReceiver mNetworkReceiver;
    private long mAllTime = 0;
    private boolean mIsAuthority = true;
    private boolean mIsShowAd = false;
    private boolean mIsInStart = false;
    private boolean mIsInLast = false;

    private boolean mIsFromDMR = false;
    private boolean mIsFirstPlay = true;
    private boolean mPlayFinish = false;
    private boolean mIsToPay = false;
    private boolean mIsFirstEntry = true;
    private boolean mIsRunPause = false;

    private AdBean mStartAdBean;
    private AdBean mPauseAdBean;
    private AdBean mLastAdBean;

    private int mOldCpVideoId = 0;
    private AdCountTimer mCountDownTimer;

    private final int SEEK_DMR = 1;
    public static final int HIDE_FILTER = 2;
    private final int SHOW_IMAGE_AD = 3;
    private final int HIDE_IMAGE_AD = 4;

    private final int PLAYER_ACTIONS_TYPE = 11;
    private boolean mIsSeek = false;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEEK_DMR:
                    if (mIsFromDMR && mIsSeek && mVideoPlayer != null) {
                        int position = (int) mVideoPlayer.getCurrentPosition();
                        MultiScreenManager.sendState(PlayerActivity.this, MultiScreenManager.STATE_ON_POSITON_CHANGED, String.valueOf(position));
                        mHandler.removeMessages(SEEK_DMR);
                        mHandler.sendEmptyMessageDelayed(SEEK_DMR, 1000);
                    }
                    break;
                case HIDE_FILTER:
                    hideFilterMenu();
                    break;
                case SHOW_IMAGE_AD:
                    showImageAd();
                    break;
                case HIDE_IMAGE_AD:
                    hideImageAd();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mIsRequestColumn = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        LogUtils.i(TAG, "onNewIntent");
        mIsRequestColumn = false;
        super.onNewIntent(intent);
        clearVideo();
        setIntent(intent);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.i(TAG, "onResume");
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
        if (mIsFirstEntry) {
            mIsFirstEntry = false;
            return;
        } else if (mIsRunPause) {
            mIsRunPause = false;
            return;
        }
        resumePlay();
    }

    private void resumePlay() {
        // 外部重新回到页面或重试操作
        LogUtils.i(TAG, "resumePlay");
        if (mIsShowAd && mCountDownTimer != null) {
            showTimeCountView();
            mCountDownTimer.resume();
        }
        startPlayer(timePosition);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.i(TAG, "onPause");
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
        mIsRunPause = true;
        if (mCountDownTimer != null) {
            mCountDownTimer.pause();
        }
        // TODO: 2017/2/14
        if (mIsShowAd && mVideoPlayer != null) {
            timePosition = mVideoPlayer.getCurrentPosition();
        } else {
            saveHistory();
        }
    }

    private void saveHistory() {
        if (displayHistory == null) return;
        long duration = 0;
        int playStatus = 0;
        if (mPlayFinish) {
            playStatus = 1;
            timePosition = 0;
        } else if (mVideoPlayer != null) {
            duration = mVideoPlayer.getDuration();
            if (duration <= 0) {
                duration = mAllTime;
            }
            timePosition = mVideoPlayer.getCurrentPosition();
        }

        displayHistory.timePosition = timePosition;
        displayHistory.cpVideoId = index;
        displayHistory.cpId = vodColumnId;
        displayHistory.duration = duration;
        displayHistory.playStatus = playStatus;

        LogUtils.i(TAG, "posterUrl: " + displayHistory.posterUrl + ", _timePosition: "
                + displayHistory.timePosition + "cpId: " + displayHistory.cpId);
        EventBusUtil.postInfoEvent(EventConf.UPLOAD_PUT_RESUME_POINT, displayHistory);
        int videoId = -1;
        if(mVideoBean != null) {
            videoId = mVideoBean.getVideoId();
        }
        // 通知续播按钮状态
        Map<String, String> map = new HashMap<>();
        map.put(POSITION, "" + timePosition);
        map.put(INDEX, "" + index);
        map.put(VIDEO_ID, "" + videoId);
        if (!TextUtils.isEmpty(mRequestPlayTag)) {
            EventBusBean<Map<String, String>> mapEventBusBean = new EventBusBean<>();
            mapEventBusBean.TAG = mRequestPlayTag;
            mapEventBusBean.obj = map;
            EventBusUtil.postInfoEvent(EventConf.CALL_ALBUM_TO_UPDATE_RESUME_PLAY, mapEventBusBean);
        }
    }

    private void clearVideo() {
        LogUtils.i(TAG, "clearVideo");
        if (mVideoPlayer != null) {
            if (mMediaControl != null) {
                mMediaControl.hideControl();
            }
            mVideoPlayer.setVisibility(View.INVISIBLE);
            mVideoPlayer.setVideoPlayCallback(null);
            mVideoPlayer.close();
            mRelativeLayout.removeView(mVideoPlayer);
            mVideoPlayer = null;
        }
    }

    private void release() {
        clearVideo();
        mOldCpVideoId = 0;
        mIsToPay = false;
        mIsRunPause = false;
        if (mCtrlReceiver != null) {
            unregisterReceiver(mCtrlReceiver);
            mCtrlReceiver = null;
        }
    }

    @Override
    protected void onDestroy() {
        LogUtils.i(TAG, "onDestroy");
        super.onDestroy();
        if (mNetworkReceiver != null) {
            unregisterReceiver(mNetworkReceiver);
        }
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
//        if (mDialog != null) {
//            mDialog.dismiss();
//            mDialog = null;
//        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mFilterMenuLayout != null) {
            mFilterMenuLayout.destroyDrawingCache();
        }
        AlbumRequest.getInstance().cancelAlbumCallBack();
        mIsToPay = false;
    }

    private void initVideoView() {
        LogUtils.i(TAG, "initPlayerView");
//        if(mVideoPlayer != null && (mIsShowAd || ContentType.VR_TYPE != mPlayVideoType)) {
//            return;
//        }
        clearVideo();
        if (mPlayVideoType == ContentType.VR_TYPE && !mIsShowAd) {
            mVideoPlayer = new VRVideoPlayers(this);
        } else {
            mMediaControl = new MediaController(this);
            mVideoPlayer = new SuperVideoPlayers(this);
            ((SuperVideoPlayers)mVideoPlayer).setControlView(mMediaControl);
            ((SuperVideoPlayers)mVideoPlayer).setPlayerType(new BPlayer());
            ((SuperVideoPlayers)mVideoPlayer).showOrHideController(false);
            initBPlayer();
        }
        mVideoPlayer.setFromDMR(mIsFromDMR);
        mVideoPlayer.setAuthority(mIsAuthority);
        mVideoPlayer.setVideoPlayCallback(mVideoPlayCallback);
        mVideoPlayer.setFocusable(true);
        mRelativeLayout.addView(mVideoPlayer);
    }

    private void initFilterMenu() {
        if (mFilterMenuLayout != null) {
            return;
        }
        mFilterMenuLayout = new FilterMenuLayout(this);
        mFilterMenuLayout.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mFilterMenuLayout.setLayoutParams(params);
        mRelativeLayout.addView(mFilterMenuLayout);
    }

    private void initBPlayer() {
        BPlayer bPlayer = new BPlayer();
        bPlayer.setAllTime((int) mAllTime);
        bPlayer.setCustomCurrentTime(timePosition);
        ((SuperVideoPlayers)mVideoPlayer).setPlayerType(bPlayer);
        bPlayer.setSuperVideoPlayers((SuperVideoPlayers)mVideoPlayer);
        bPlayer.setMediaController(mMediaControl);
        mMediaControl.setProgressBarMax(1000);
    }

    private void resetValue() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
//        if (mDialog != null) {
//            mDialog.dismiss();
//            mDialog = null;
//        }

        mVideoBean = null;
        mPlay = null;
        mIsAuthority = true;
        mIsShowAd = false;
        mIsInStart = false;
        mIsInLast = false;
        mIsFromDMR = false;
        mIsFirstPlay = true;
        mPlayFinish = false;
        mIsToPay = false;
        mPlayVideoType = 0;
        mAdPlayTime = 0;
        mOldCpVideoId = 0;
    }

    private void initData() {
        resetValue();
        registControlReceiver();
        registNetworkReceiver();
        displayHistory = new DisplayHistory();
        Intent intent = getIntent();
        String strAlbumId = intent.getStringExtra(ALBUM_ID);
        String strColContentId = intent.getStringExtra(COL_CONTENTID);
        if (!TextUtils.isEmpty(strColContentId)) {
            vodColumnId = Integer.valueOf(strColContentId);
        }
        LogUtils.d(TAG, "strAlbumId: " + strAlbumId + ", cpId: " + vodColumnId);
        String strTimePosition = intent.getStringExtra(POSITION);
        String strType = intent.getStringExtra(TYPE);
        String strIndex = intent.getStringExtra(INDEX);
        String strDuration = intent.getStringExtra(DURATION);
        String strVideoType = intent.getStringExtra(VIDEO_TYPE);
        LogUtils.i(TAG, "strVideoType: " + strVideoType);
        if (!TextUtils.isEmpty(strAlbumId)) {
            albumId = Integer.valueOf(strAlbumId);
        }
        if (!TextUtils.isEmpty(strTimePosition)) {
            timePosition = Long.valueOf(strTimePosition);
        }
        LogUtils.i(TAG, "strTimePosition: " + strTimePosition + ", timePostion: " + timePosition);
        if (!TextUtils.isEmpty(strIndex)) {
            index = Integer.valueOf(strIndex);
        }
        if (!TextUtils.isEmpty(strType)) {
            type = Integer.valueOf(strType);
        }
        if (!TextUtils.isEmpty(strDuration)) {
            mAllTime = Long.valueOf(strDuration);
        }
        if (index <= 0) {
            index = 1;
        }
        mIsFromDMR = intent.getBooleanExtra("from_dmr", false);
        if (mIsFromDMR) {
            AlbumRequest.getInstance().release();
        }
        AlbumRequest.getInstance().setAlbumCallBack(albumCallBack);
        if (TextUtils.equals(intent.getAction(), ACTION)) {
//            mIsFromDMR = intent.getBooleanExtra("from_dmr", false);
            if (mIsFromDMR && albumId == -1) {
                String playUrl = intent.getStringExtra("playUrl");
                LogUtils.i(TAG, "playUrl: " + playUrl);
                if (!TextUtils.isEmpty(playUrl)) {
                    if (!TextUtils.isEmpty(strVideoType)) {
                        mPlayVideoType = Integer.valueOf(strVideoType);
                    }
                    startPlayer(playUrl, 0);
                } else {
                    showDialogTips(getString(R.string.data_error));
                }
                return;
            }
            AlbumRequest.getInstance().getAlbumInfo(albumId, vodColumnId, index, mIsFromDMR);
        } else {
            AlbumRequest.getInstance().updateVideo(albumId, index);
        }
        mRequestPlayTag = intent.getStringExtra(REQUEST_PLAY_TAG);
    }

    /**
     * 注册播放控制广播
     */
    private void registControlReceiver() {
        if (mCtrlReceiver == null) {
            mCtrlReceiver = new CtrlReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ReceiverActionFlag.PLAY_CONTROL_ACTION);
            registerReceiver(mCtrlReceiver, filter);
        }
    }

    private boolean playWithAd(int adTypeId, boolean isPlayNext) {
        boolean isPlayAd = false;
        AdBean adBean = null;
        switch (adTypeId) {
            case AdCode.AdTypeId.START_FLAG:
                if (mStartAdBean != null) {
                    adBean = mStartAdBean;
                    mIsInStart = true;
                }
                break;
            case AdCode.AdTypeId.LAST_FLAG:
                if (mLastAdBean != null) {
                    adBean = mLastAdBean;
                    mIsInLast = true;
                }
                break;
            default:
                isPlayAd = false;
                adBean = null;
                break;
        }
        if (adBean != null) {
            int playTime = adBean.playTime;
            int adType = adBean.adType;
            String playUrl = adBean.playUrl;
            if (!TextUtils.isEmpty(playUrl)) {
                isPlayAd = true;
                mIsShowAd = true;
                mAdPlayTime = playTime;
                if (mVideoPlayer != null && mVideoPlayer instanceof SuperVideoPlayers) {
                    ((SuperVideoPlayers)mVideoPlayer).showOrHideController(false);
                }
                //只有VOD正常的片才有广告
                if (AdCode.AdType.IMAGE_FLAG == adType) {
                    startAdPicAndPlay(adBean, playTime);
                } else {
                    startPlayer(playUrl, 0);
                }
            } else {
                startNormalPlay(isPlayNext);
            }
        } else {
            startNormalPlay(isPlayNext);
        }

        return isPlayAd;
    }

    private void startNormalPlay(boolean isPlayNext) {
        if (isPlayNext) {
            autoPlayNext();
        } else {
            initBean();
            startPlayer(timePosition);
        }
    }

    private void startAdPicAndPlay(AdBean adBean, int playTime) {
        mImageView = null;
        initImageAd(adBean.adTypeId);
        inflateImage(adBean.playUrl);
        mImageView.setVisibility(View.VISIBLE);
        mImageView.bringToFront();
        startCountTimer(playTime);
    }

    private void startCountTimer(int playTime) {
        LogUtils.i(TAG, "startCountTimer");
        if (mCountDownTimer == null) {
            initTimerCount(playTime);
        }
        mCountDownTimer.start();
        showTimeCountView();
    }

    private void closeAdAndPlay() {
        LogUtils.i(TAG, "closeAdAndPlay");
        boolean isPlayNext = false;
        if (mIsInStart) {
            mIsInStart = false;
            isPlayNext = false;
        }
        if (mIsInLast) {
            mIsInLast = false;
            isPlayNext = true;
        }
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        mIsShowAd = false;
        if (mVideoPlayer != null) {
            mVideoPlayer.setShowAd(mIsShowAd);
        }
        if (mImageView != null && View.VISIBLE == mImageView.getVisibility()) {
            mImageView.setVisibility(View.INVISIBLE);
            mImageView.destroyDrawingCache();
            mImageView = null;
        }
        startNormalPlay(isPlayNext);
    }

    private void autoPlayNext() {
        LogUtils.i(TAG, "autoPlayNext");
        if (type == ALBUM_TYPE_EPISODES) {
            if (mVideoBean == null) {
                AlbumRequest.getInstance().updateVideo(Integer.valueOf(albumId), ++index);
            } else {
                AlbumRequest.getInstance().updateVideo(Integer.valueOf(albumId), mVideoBean.getSeq() + 1);
            }
        } else {
            finish();
        }
    }

    private void showTimeCountView() {
        if (mTextView != null) {
            mTextView.bringToFront();
            mTextView.setVisibility(View.VISIBLE);
        }
    }

    private void showHtml(String url) {
        LogUtils.i(TAG, "showHtml_url: " + url);
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(WebViewActivity.WEB_URL, url);
        startActivity(intent);
    }

    @Override
    protected void cancel() {
        super.cancel();
//        setResult(-1);
        finish();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogUtils.i(TAG, "dispatchKeyEvent");
        menuCount = 0;
        boolean isExcute = false;
        int keyCode = event.getKeyCode();
        if (KeyEvent.ACTION_UP == event.getAction()) {
            if (mIsShowAd) {
                return true;
            }
        }
        if (KeyEvent.ACTION_DOWN == event.getAction()) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (mFilterMenuLayout != null && View.VISIBLE == mFilterMenuLayout.getVisibility()) {
                    hideFilterMenu();
                } else {
                    if (mIsFromDMR) {
                        mPlayFinish = true;
                        MultiScreenManager.sendState(PlayerActivity.this, MultiScreenManager.STATE_ON_STOP);
                    }
                    finish();
                }
                return true;
            }
            if (mIsShowAd) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                    String thirdUrl = null;
                    if (mIsInStart) {
                        thirdUrl = mStartAdBean.thirdUrl;
                    } else if (mIsInLast) {
                        thirdUrl = mLastAdBean.thirdUrl;
                    }
                    if (!TextUtils.isEmpty(thirdUrl)) {
                        showHtml(thirdUrl);
                    }
                } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    return false;
                }
                return true;
            }
            switch (keyCode) {
                case KeyEvent.KEYCODE_MENU:
                    if (View.VISIBLE == mFilterMenuLayout.getVisibility()) {
                        hideFilterMenu();
                    } else {
                        showFilterMenu();
                    }
                    isExcute = true;
                    break;
            }
        }
        return isExcute || super.dispatchKeyEvent(event);
    }

    private void hideImageAd() {
        if (mImageView != null && View.VISIBLE == mImageView.getVisibility()) {
            mImageView.setVisibility(View.INVISIBLE);
        }
    }

    private void showImageAd() {
        if (mPauseAdBean != null && AdCode.AdType.IMAGE_FLAG == mPauseAdBean.adType) {
            if (mImageView == null) {
                initImageAd(AdCode.AdTypeId.PAUSE_FLAG);
                inflateImage(mPauseAdBean.playUrl);
            }
            mImageView.setVisibility(View.VISIBLE);
            mImageView.bringToFront();
        }
    }

    private void hideFilterMenu() {
        if (mFilterMenuLayout == null || mVideoPlayer == null) return;
        mFilterMenuLayout.clearFocus();
        mFilterMenuLayout.setVisibility(View.INVISIBLE);
        mVideoPlayer.setShowMenu(false);
    }

    private void showFilterMenu() {
        if (mFilterMenuLayout == null || mVideoPlayer == null) return;
        mHandler.removeMessages(HIDE_FILTER);
        if (mVideoPlayer instanceof SuperVideoPlayers) {
            if (((SuperVideoPlayers)mVideoPlayer).isShowMediaController()) {
                ((SuperVideoPlayers)mVideoPlayer).showOrHideController(false);
            }
        }
        mVideoPlayer.setShowMenu(true);
        mFilterMenuLayout.setVisibility(View.VISIBLE);
        mFilterMenuLayout.setFocusable(true);
        mFilterMenuLayout.requestFocus();
        mFilterMenuLayout.bringToFront();
        mHandler.sendEmptyMessageDelayed(HIDE_FILTER, 5000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void reLoad() {
        super.reLoad();
        LogUtils.i(TAG, "reLoad");
        resumePlay();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.i(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.i(TAG, "onStop");
        release();
    }

    private VideoPlayer.VideoPlayCallbackImpl mVideoPlayCallback = new VideoPlayer.VideoPlayCallbackImpl() {

        @Override
        public void onError() {
            actionsCollection(PlayerActions.PlayerStatus.STATUS_ERROR);
            if (mImageView != null && mImageView.getVisibility() == View.VISIBLE) {
                mImageView.setVisibility(View.INVISIBLE);
            }
            showDialogTips("视频加载失败");
            if (mIsFromDMR) {
                MultiScreenManager.sendState(PlayerActivity.this, MultiScreenManager.STATE_ON_STOP);
            }
        }

        @Override
        public void onPausePlay() {
            LogUtils.i(TAG, "onPausePlay");
            actionsCollection(PlayerActions.PlayerStatus.STATUS_PAUSE);
            if (mIsFromDMR) {
                MultiScreenManager.sendState(PlayerActivity.this, MultiScreenManager.STATE_ON_PAUSE);
                mIsSeek = true;
                mHandler.removeMessages(SEEK_DMR);
            }
            mHandler.removeMessages(SHOW_IMAGE_AD);
            mHandler.sendEmptyMessage(SHOW_IMAGE_AD);
        }

        /**
         * 播放器关闭按钮回调
         */
        @Override
        public void onCloseVideo() {
            mVideoPlayer.close();//关闭VideoView
            mVideoPlayer.setVisibility(View.GONE);
            actionsCollection(PlayerActions.PlayerStatus.STATUS_CLOSE);
        }

        @Override
        public void onStartPlay(Object tag) {
            LogUtils.i(TAG, "onStartPlay");
            actionsCollection(PlayerActions.PlayerStatus.STATUS_PLAY);
            if (tag != null && PlayTag.PLAY_AD == (int)tag) {
                startCountTimer(mAdPlayTime);
                return;
            }
            dissmissLoadingDialog();
//            if (mImageView != null && View.VISIBLE == mImageView.getVisibility()) {
//                mImageView.setVisibility(View.INVISIBLE);
//            }
            mHandler.removeMessages(HIDE_IMAGE_AD);
            mHandler.sendEmptyMessage(HIDE_IMAGE_AD);
            int duration = (int) mVideoPlayer.getDuration(); //开发者需要自己计算播放时长
            LogUtils.i("Decode", "duration: " + duration);
            if (duration <= 0) {
                duration = (int) mAllTime;
            }
            if (!mIsShowAd && mIsFromDMR && duration > 0) {
                if (mIsFirstPlay) {
                    mIsFirstPlay = false;
                    MultiScreenManager.sendState(PlayerActivity.this, MultiScreenManager.STATE_ON_DURATION_CHANGED, String.valueOf(duration));
                }
                LogUtils.i("Decode", "first_seek");
                MultiScreenManager.sendState(PlayerActivity.this, MultiScreenManager.STATE_ON_PLAY);
                mIsSeek = true;
                if (mHandler != null) {
                    mHandler.removeMessages(SEEK_DMR);
                    mHandler.sendEmptyMessage(SEEK_DMR);
                }
            }
            // 友盟视频播放操作行为采集
            Map<String, String> map = new HashMap<>();
            map.put("albumId", albumId + "");
            map.put("videoName", mTitle + "");
            map.put("videoId", videoId + "");
            MobclickAgent.onEventValue(PlayerActivity.this, "video", map, duration);
        }

        @Override
        public void onFreeEnd() {
            LogUtils.i(TAG, "onFreeEnd");
            if (mIsToPay) {
                return;
            }
            mIsToPay = true;
            startPay();
        }

        /**
         * 播放完成回调
         */
        @Override
        public void onPlayFinish(Object tag) {
            LogUtils.i(TAG, "onPlayFinish_tag: " + tag);
            if (tag != null && PlayTag.PLAY_AD == (int)tag) {
                mVideoPlayer.replay();
                return;
            }
            if (type == ALBUM_TYPE_MOVIE) {
                mPlayFinish = true;
            } else if(type == ALBUM_TYPE_EPISODES && mCount == index) {
                // 剧集播放结束
                LogUtils.i("Decode", "mCount: " + mCount + ", index: " + index);
                mPlayFinish = true;
                actionsCollection(PlayerActions.PlayerStatus.STATUS_CLOSE);
                finish();
                return;
            }
            if (mIsFromDMR) {
                MultiScreenManager.sendState(PlayerActivity.this, MultiScreenManager.STATE_ON_STOP);
                mIsSeek = false;
                mHandler.removeMessages(SEEK_DMR);
                finish();
                return;
            }
            playWithAd(AdCode.AdTypeId.LAST_FLAG, true);
        }
    };

    private void startPay() {
        LogUtils.i(TAG, "startPay");
        finish();
        if (mAuthority == null) {
            ToastUtils.showShort(this, getString(R.string.pay_authority_error));
            return;
        }
        ArrayList<PayInfo> payInfos = mAuthority.result;
        if (payInfos == null || payInfos.size() <= 0) {
            ToastUtils.showShort(this, getString(R.string.pay_authority_error));
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(PayInfoActivity.PAY_INFO_KEY, payInfos);
        Intent intent = new Intent(this, PayInfoActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

//        LogUtils.i(TAG, "intent_pay");
//        int size = payInfos.size();
//        boolean isPayColumn = false;
//        for (int i = 0; i < size; i++) {
//            PayInfo payInfo = payInfos.get(i);
//            if (payInfo != null) {
//                String chargingType = payInfo.chargingType;
//                if (!TextUtils.isEmpty(chargingType) && 2001 == Integer.valueOf(chargingType)) {
//                    isPayColumn = true;
//                    break;
//                }
//            }
//        }
//
//        Bundle bundle = new Bundle();
//        if (isPayColumn) {
//            //专区付费
//            String payInfosString = new Gson().toJson(payInfos).toString();
//            bundle.putString("pay_info", payInfosString);
//            Intent intent = new Intent();
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setAction("com.ppfuns.pay.action.PAY_COLUMN");
//            intent.putExtras(bundle);
//            startActivityForResult(intent, 111);
//        } else {
//            PayInfo payInfo = payInfos.get(0);
//            bundle.putSerializable(PayInfoActivity.PAY_INFO_KEY, payInfo);
//            Intent intent = new Intent(this, PayInfoActivity.class);
//            intent.putExtras(bundle);
//            startActivity(intent);
//        }
    }

    private void actionsCollection(int status) {
        LogUtils.d(TAG, "actionsCollection");
        if (mPlay == null && mVideoPlayer == null) {
            return;
        }
        long duration = mVideoPlayer.getDuration();
        PlayerActions actions = new PlayerActions();
        actions.serviceId = mPlay.albumId;
        actions.serviceName = mPlay.title;
        actions.columnID = mPlay.columnID;
        actions.duration = (int) duration;
        actions.restTime = (int) (duration - mVideoPlayer.getCurrentPosition() / 1000);
        actions.status = status;
        actions.url = mPlay.uri;
        actions.serviceIndex = mPlay.index;
        BroadcastUtils.actionCollection(this, PLAYER_ACTIONS_TYPE, actions);
        LogUtils.d(TAG, "actionsCollection sendBroadcast");
    }

    /**
     * 设置正片内容
     */
    private void initBean() {
        LogUtils.i(TAG, "initBean");
        if (mVideoBean == null) {
            return;
        }
        timePosition = Long.parseLong(getIntent().getStringExtra(POSITION));
        int videoId = mVideoBean.getVideoId();
        if (mOldCpVideoId != 0 && mOldCpVideoId != videoId) {
            timePosition = 0;
        }
        mOldCpVideoId = videoId;

        initDefinition(mVideoBean);
        mTitle = mVideoBean.getName();
        mInfo = mVideoBean.getFocus();
        albumId = mVideoBean.getAlbumId();
//        posterUrl = videoBean.getPicUrl();
        if (mAllTime == 0) {
            String time = mVideoBean.getDuration();
            if (!TextUtils.isEmpty(time)) {
                mAllTime = Long.valueOf(time);
            }
        }
        String playUrl = getFeaturePlayUrl();
        index = mVideoBean.getSeq();
        if (index == 0) {
            index = 1;
        }
        mPlay = new Play();
        mPlay.info = mInfo;
        mPlay.title = mTitle;
        mPlay.uri = playUrl;
        mPlay.timePosition = timePosition;
        mPlay.albumId = albumId;
        mPlay.columnID = vodColumnId;
        mPlay.index = index;

        displayHistory.resourceName = mTitle;
        displayHistory.resourceId = albumId;
        displayHistory.cpVideoId = index;
        // TODO: 2016/9/20 支持两张海报图上传
        displayHistory.posterUrl = posterVUrl + "|" + posterUrl;
        //displayHistory.posterUrl = posterUrl;
        LogUtils.i(TAG, "posterUrl: " + displayHistory.posterUrl);
        displayHistory.forwardUrl = posterUrl;
        displayHistory.timePosition = timePosition;
    }

    /**
     * 获取正片的播放地址
     * @return
     */
    private String getFeaturePlayUrl() {
        String playUrl = null;
        if (mPlayUrlList != null && mPlayUrlList.size() > 0) {
            Collections.reverse(mPlayUrlList);
            int size = mPlayUrlList.size();
            if (mPlayUrlList != null && size > 0) {
                for (int i = 0; i < size; i++) {
                    PlayUrlBean urlBean = mPlayUrlList.get(i);
                    if (urlBean != null) {
                        String url = urlBean.m3u8Url;
                        if (!TextUtils.isEmpty(url)) {
                            playUrl = url;
                            break;
                        }
                    }
                }
            }
        }
        if (TextUtils.isEmpty(playUrl)) {
            playUrl = mVideoBean.getM3u8Url();
        }
        return playUrl;
    }

    /*
     * 用applicationContext 獲取系統服務
     * ，避免服務持有activity，導致activity無法回收
     * ，從而內存洩漏
     *
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new ContextWrapper(newBase) {
            @Override
            public Object getSystemService(String name) {
                if (Context.AUDIO_SERVICE.equals(name)) {
                    LogUtils.i(TAG, "AUDIO_SERVICE");
                    return getApplicationContext().getSystemService(name);
                }
                return super.getSystemService(name);
            }
        });
    }

    private void initTimerCount(int time) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        //TODO 修改广告时间
        mCountDownTimer = new AdCountTimer(time * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished, int percent) {
                LogUtils.i(TAG, "" + millisUntilFinished / 1000);
                mTextView.setText("广告" + String.valueOf(millisUntilFinished / 1000) + "秒");
            }

            @Override
            public void onFinish() {
                mTextView.setVisibility(View.INVISIBLE);
                closeAdAndPlay();
            }
        };
    }

    private void initImageAd(int adType) {
        if (mImageView == null) {
            mImageView = new ImageView(this);
            int width = -1;
            int height = -1;
            if (adType == AdCode.AdTypeId.PAUSE_FLAG) {
                width = getResources().getDimensionPixelSize(R.dimen.image_count_down_width);
                height = getResources().getDimensionPixelSize(R.dimen.image_count_down_height);
            }
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            mRelativeLayout.addView(mImageView, layoutParams);
//            mImageView.setBackgroundResource(R.drawable.album_btn_bg);
            mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mImageView.setVisibility(View.INVISIBLE);
        }
    }

    private void inflateImage(String imageUrl) {
        DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        if (!TextUtils.isEmpty(imageUrl)) {
            ImageLoader.getInstance().displayImage(imageUrl, mImageView, mOptions);
        }
    }

    private void initAdBeans(List<AdBean> adBeanList) {
        if (adBeanList == null) {
            return;
        }
        int size = adBeanList.size();
        for (int i = 0; i < size; i++) {
            AdBean adBean = adBeanList.get(i);
            if (adBean != null) {
                int adTypeId = adBean.adTypeId;
                switch (adTypeId) {
                    case AdCode.AdTypeId.START_FLAG:
                        if (mStartAdBean == null) {
                            mStartAdBean = adBean;
                        }
                        break;
                    case AdCode.AdTypeId.PAUSE_FLAG:
                        if (mPauseAdBean == null) {
                            mPauseAdBean = adBean;
                        }
                        break;
                    case AdCode.AdTypeId.LAST_FLAG:
                        if (mLastAdBean == null) {
                            mLastAdBean = adBean;
                        }
                        break;
                }
            }
        }
    }

    private void initDefinition(VideoBean videoBean) {
        if (videoBean == null) {
            return;
        }
        if (mPlayUrlList != null) {
            mPlayUrlList = null;
        }
        String definition = videoBean.getPlayUrlList();
        if (!TextUtils.isEmpty(definition)) {
            try {
                mPlayUrlList = new Gson().fromJson(definition, new TypeToken<List<PlayUrlBean>>() {
                }.getType());
            } catch (Exception e) {
                LogUtils.i(TAG, e.toString());
            }
        }
        if (mPlayUrlList != null && mPlayUrlList.size() > 0) {
            mTitleList.add("清晰度");
//            mFilterMenuLayout.setTitleView(mTitleList);
        }
        initFilterMenu();
        mFilterMenuLayout.setFilterMenuView(new FilterContentAdapter.FilterActionListener() {
            @Override
            public void onClick(int type, int position) {
                LogUtils.i("Decode", "onClick_position: " + position);
                if (mVideoPlayer != null && mPlayUrlList != null && mPlayUrlList.size() > 0) {
                    if (0 <= position && position < mPlayUrlList.size()) {
                        PlayUrlBean urlBean = mPlayUrlList.get(position);
                        if (urlBean != null) {
                            String playUrl = urlBean.m3u8Url;
                            if (!TextUtils.isEmpty(playUrl)) {
                                int seekTime = (int) mVideoPlayer.getCurrentPosition();
                                mVideoPlayer.close();
                                mVideoPlayer.startPlayer(playUrl, seekTime);
                                hideFilterMenu();
                            }
                        }
                    }
                }
            }
        }, mTitleList, mPlayUrlList);

    }

    private AlbumRequest.AlbumCallBack albumCallBack = new AlbumRequest.AlbumCallBack() {

        @Override
        public void obtainAlbum(boolean isAuthority, List<AdBean> adBeanList, VideoBean videoBean, int videoType, Authority authority, String url, String verticalPicUrl, int count) {
            LogUtils.i(TAG, "obtainAlbum_videoBean: " + videoBean);
            if (videoBean == null) {
                String error = getResources().getString(R.string.data_error);
                if (count == -1) {
                    error = getResources().getString(R.string.film_shelf);
                }
                showDialogTips(error);
                return;
            }
            hideDialogTips();
            mVideoBean = videoBean;
            mPlayFinish = false;
            mIsAuthority = isAuthority;
            mAuthority = authority;
            posterVUrl = url;
            posterUrl = verticalPicUrl;
            mCount = count;
            mPlayVideoType = videoType;
            // 广告
            initAdBeans(adBeanList);
            int adType = AdCode.AdTypeId.NONE_FLAG;
            if (!mIsFromDMR && adBeanList != null && adBeanList.size() > 0
                    && mAuthority != null && (TextUtils.equals(PayState.PAY_FREE, mAuthority.code)
                    || TextUtils.equals(PayState.ERROR, mAuthority.code )
                    || TextUtils.equals(PayState.PAY_NO_PASS, mAuthority.code))) {
                adType = AdCode.AdTypeId.START_FLAG;
            }
            playWithAd(adType, false);
        }
    };

    private void startPlayer(String playUrl, long timePosition) {
        mPlay = new Play();
        mPlay.uri = playUrl;
        mPlay.info = "";
        mPlay.title = "";
        startPlayer(timePosition);
    }

    private void startPlayer(long timePosition) {
        if (mPlay == null) {
            showDialogTips(getString(R.string.data_error));
            return;
        }
        initVideoView();
        if (mIsShowAd) {
            mVideoPlayer.setPlayTag(PlayTag.PLAY_AD);
        } else {
            mVideoPlayer.setPlayTag(PlayTag.PLAY_VOD);
        }
        mVideoPlayer.setShowAd(mIsShowAd);
        mVideoPlayer.startPlayer(mPlay, timePosition);
    }

    private void next() {
        if (mIsShowAd) {
            return;
        }
        mVideoBean = AlbumRequest.getInstance().updateVideoForVoice(albumId, ++index);
        if (mVideoBean == null) {
            --index;
            ToastUtils.showShort(this, "没有下一集");
            return;
        }

        playWithAd(AdCode.AdTypeId.START_FLAG, false);
    }

    private void last() {
        if (mIsShowAd) {
            return;
        }
        mVideoBean = AlbumRequest.getInstance().updateVideoForVoice(albumId, --index);
        if (mVideoBean == null) {
            ++index;
            ToastUtils.showShort(this, "没有上一集");
            return;
        }
        playWithAd(AdCode.AdTypeId.START_FLAG, false);
    }

    private void playIndexVideo(int number) {
        if (mIsShowAd) {
            return;
        }
        if (index == number) {
            ToastUtils.showShort(this, "当前正播放" + number + "集");
            return;
        }
        mVideoBean = AlbumRequest.getInstance().updateVideoForVoice(albumId, number);
        if (mVideoBean == null) {
            ToastUtils.showShort(this, "找不到第" + number + "集");
            return;
        }
        index = number;
        playWithAd(AdCode.AdTypeId.START_FLAG, false);
    }

    public class CtrlReceiver extends BroadcastReceiver {
//                private static final String ACTION = "com.ppfuns.vod.playcontrol";
        private final String TAG = "CtrlReceiver";
        private static final String ORDER = "order";
        private static final String NEXT = "next";
        private static final String LAST = "last";
        private static final String NUMBER = "number";
        private static final String PLAY = "play";
        private static final String PAUSE = "pause";
        private static final String STOP = "stop";
        private static final String CLOSE = "close";
        private static final String SEEK = "seek";
        private static final String VOLUME = "volume";
        private String order;

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.i(TAG, "onReceive");
            String action = intent.getAction();
            if (TextUtils.equals(action, ReceiverActionFlag.PLAY_CONTROL_ACTION)) {
                mIsFirstEntry = true;
                order = intent.getStringExtra(ORDER);
                if (!TextUtils.isEmpty(order)) {
                    LogUtils.i(TAG, "onReceive_order: " + order);
                    switch (order) {
                        case NEXT:
                            next();
                            break;
                        case LAST:
                            last();
                            break;
                        case NUMBER:
                            try {
                                int number = (int)intent.getLongExtra(NUMBER, -1);
                                LogUtils.i(TAG, "number: " + number);
                                if (number > 0) {
                                    playIndexVideo(number);
                                }
                            } catch (NumberFormatException e) {
                                LogUtils.i(TAG, e.toString());
                            }
                            break;
                        case PLAY:
                            if (!mIsShowAd && mVideoPlayer != null && !mVideoPlayer.isPlaying()) {
                                mVideoPlayer.play();
                            }
                            break;
                        case PAUSE:
                            LogUtils.i(TAG, "pause");
                            if (!mIsShowAd && mVideoPlayer != null && mVideoPlayer.isPlaying()) {
                                mVideoPlayer.pause(true);
                            }
                            break;
                        case STOP:
                        case CLOSE:
                            mPlayFinish = true;
                            finish();
                            break;
                        case SEEK:
                            int seekTime = intent.getIntExtra(SEEK, -1);
                            LogUtils.i(TAG, "play_seekTime: " + seekTime);
                            if (mVideoPlayer != null && mVideoPlayer.isPlaying()
                                    && 0 <= seekTime && seekTime <= mVideoPlayer.getDuration()) {
                                mVideoPlayer.seekTo(seekTime, true);
                            }
                            break;
                        case VOLUME:
                            float volume = intent.getFloatExtra(VOLUME, -1);
                            LogUtils.i(TAG, "play_volume: " + volume);
                            if (mVideoPlayer != null) {
                                mVideoPlayer.setVolume(volume);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    // 注册网络监听
    protected void registNetworkReceiver() {
        if (mNetworkReceiver == null) {
            mNetworkReceiver = new NetworkReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkReceiver, filter);
    }

    public class NetworkReceiver extends BroadcastReceiver {

        private final String TAG = NetworkReceiver.class.getSimpleName();
        private boolean isFirst = true;  // 规避刚进入播放就回调网络监听导致播放2次视频
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.i(TAG, "onReceive");
            if (!SysUtils.isNetworkAvailable(context)) {
                if (mIsShowAd && mCountDownTimer != null) {
                    mCountDownTimer.pause();
                }
                if (mVideoPlayer != null) {
                    timePosition = mVideoPlayer.getCurrentPosition();
                    mVideoPlayer.close();
                }
                clearVideo();
                if (mImageView != null && mImageView.getVisibility() == View.VISIBLE) {
                    mImageView.setVisibility(View.INVISIBLE);
                }
                showDialogTips(getString(R.string.network_error));
            } else {
                if (isFirst) {
                    isFirst = false;
                    return;
                }
                reLoad();
            }
        }
    }

    interface PlayTag {
        int PLAY_AD = 0X0001;
        int PLAY_VOD = 0X0002;
    }
}
