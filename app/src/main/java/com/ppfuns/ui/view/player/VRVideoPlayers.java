package com.ppfuns.ui.view.player;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Service;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ppfuns.model.entity.Play;
import com.ppfuns.util.MultiScreenManager;
import com.ppfuns.util.LogUtils;
import com.ppfuns.vod.R;
import com.utovr.player.UVEventListener;
import com.utovr.player.UVInfoListener;
import com.utovr.player.UVMediaPlayer;
import com.utovr.player.UVMediaType;
import com.utovr.player.UVPlayerCallBack;
import com.utovr.playerdemo.Utils;
import com.utovr.playerdemo.VideoController;
import com.utovr.playerdemo.util.OPENLOG;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hmc on 2016/9/12.
 */
public class VRVideoPlayers extends VideoPlayer implements UVPlayerCallBack, VideoController.PlayerControl {

    private final String TAG = VRVideoPlayers.class.getSimpleName();

    private Activity mActivity;
    private ImageView imgBuffer;                // 缓冲动画
    private ImageView imgBack;
    private RelativeLayout rlToolbar;
    private RelativeLayout rlParent = null;
    private RelativeLayout rlPlayView;
    private RelativeLayout rlCover;
    private UVMediaPlayer mMediaPlayer = null;  // 媒体视频播放器
    private VideoController mCtrl = null;
    //    private String mPlayUrl = "http://192.168.1.12/zb/hangzhou.mp4";
    private String mPlayUrl = null;
    private String mType = "mp4";
    private boolean bufferResume = true;
    private boolean needBufferAnim = true;

    private int height;
    private int width;
    private boolean isCompet = false;
    protected int CurOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private boolean colseDualScreen = false;
    private int SmallPlayH = 0;
    private final String VIDEO_TYPE_MP4 = "mp4";
    private final String VIDEO_TYPE_M3U8 = "m3u8";

    public VRVideoPlayers(Context context) {
        this(context, null);
    }

    public VRVideoPlayers(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VRVideoPlayers(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mActivity = (Activity) mContext;
        initView();
    }

    private void initView() {
        LogUtils.i(TAG, "vr_initView");
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.vr_video_player_layout, this, true);
        rlParent = (RelativeLayout) view.findViewById(R.id.activity_rlParent);
        rlCover = (RelativeLayout) view.findViewById(R.id.rl_cover);
        imgBuffer = (ImageView) view.findViewById(R.id.activity_imgBuffer);
        rlPlayView = (RelativeLayout) view.findViewById(R.id.activity_rlPlayView);
        rlToolbar = (RelativeLayout) view.findViewById(R.id.activity_rlToolbar);
        rlToolbar.setVisibility(View.GONE);
        startUdpControl();
    }

    private long seekTime;

    private void changeOrientation(boolean isLandscape) {
        if (rlParent == null) {
            return;
        }
        if (isLandscape) {
            CurOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            rlParent.setLayoutParams(lp);
            if (colseDualScreen && mMediaPlayer != null) {
                mCtrl.setDualScreenEnabled(true);
            }
            colseDualScreen = false;
            mCtrl.changeOrientation(true, 0);
        } else {
            CurOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mActivity.getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getSmallPlayHeight();
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, SmallPlayH);
            rlParent.setLayoutParams(lp);
            if (mMediaPlayer != null && mMediaPlayer.isDualScreenEnabled()) {
                mCtrl.setDualScreenEnabled(false);
                colseDualScreen = true;
            }
            mCtrl.changeOrientation(false, 0);
        }
    }

    private void getSmallPlayHeight() {
        if (this.SmallPlayH != 0) {
            return;
        }
        int ScreenW = mActivity.getWindowManager().getDefaultDisplay().getWidth();
        int ScreenH = mActivity.getWindowManager().getDefaultDisplay().getHeight();
        if (ScreenW > ScreenH) {
            int temp = ScreenW;
            ScreenW = ScreenH;
            ScreenH = temp;
        }
        SmallPlayH = ScreenW * ScreenW / ScreenH;
    }

    public void onResume() {
        if (mMediaPlayer != null) {
            mMediaPlayer.onResume(mContext);
        }
    }

    private boolean isSeek;

    private UVEventListener mListener = new UVEventListener() {
        @Override
        public void onStateChanged(int playbackState) {
            Log.i(TAG, "+++++++ playbackState:" + playbackState);
            switch (playbackState) {
                case UVMediaPlayer.STATE_PREPARING:
                    if (!mIsAuthority) {
                        mHandler.removeMessages(PLAY_TIME);
                        mHandler.sendEmptyMessage(PLAY_TIME);
                    }
                    break;
                case UVMediaPlayer.STATE_BUFFERING:
                    if (needBufferAnim && mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        bufferResume = true;
                        Utils.setBufferVisibility(imgBuffer, true);
                    }
                    break;
                case UVMediaPlayer.STATE_READY:
                    // 设置时间和进度条
                    LogUtils.d(TAG, "STATE_READY seekTime" + seekTime);
                    if (!isSeek) {
                        seekTo(seekTime);
                        isSeek = true;
                    }

                    mCtrl.setInfo();
                    if (bufferResume) {
                        bufferResume = false;
                        Utils.setBufferVisibility(imgBuffer, false);
                    }
                    if (mDuration <= 0) {
                        mDuration = (mMediaPlayer != null ? mMediaPlayer.getDuration() : 0);
                        LogUtils.d("ZFZ", "control_play_duration: " + mDuration);
                    }
                    if(mVideoPlayCallback != null) {
                        mVideoPlayCallback.onStartPlay(mObject);
                    }
                    break;
                case UVMediaPlayer.STATE_ENDED:
                    //这里是循环播放，可根据需求更改
                    if (mVideoPlayCallback != null) {
                        mVideoPlayCallback.onPlayFinish(mObject);
                    }
                    break;
                case UVMediaPlayer.TRACK_DISABLED:
                case UVMediaPlayer.TRACK_DEFAULT:
                    break;
            }
        }

        @Override
        public void onError(Exception e, int ErrType) {
            Toast.makeText(mContext, Utils.getErrMsg(ErrType), Toast.LENGTH_SHORT).show();
            if (mVideoPlayCallback != null) {
                mVideoPlayCallback.onError();
            }
        }

        @Override
        public void onVideoSizeChanged(int width, int height) {
        }

    };

    private UVInfoListener mInfoListener = new UVInfoListener() {
        @Override
        public void onBandwidthSample(int elapsedMs, long bytes, long bitrateEstimate) {
        }

        @Override
        public void onLoadStarted() {
        }

        @Override
        public void onLoadCompleted() {
            if (bufferResume) {
                bufferResume = false;
                Utils.setBufferVisibility(imgBuffer, false);
            }
            if (mCtrl != null) {
                mCtrl.updateBufferProgress();
            }

        }
    };

    @Override
    public void createEnv() {
        try {
            LogUtils.i(TAG, "vr_createEnv");
            // 创建媒体视频播放器
            mMediaPlayer.initPlayer();
            mMediaPlayer.setListener(mListener);
            mMediaPlayer.setInfoListener(mInfoListener);
            if (mType.equals(VIDEO_TYPE_MP4)) {
                //如果是网络MP4，可调用
                //                mCtrl.startCachePro();
                //                mCtrl.stopCachePro();
                Log.d(TAG, VIDEO_TYPE_MP4);
                mMediaPlayer.setSource(UVMediaType.UVMEDIA_TYPE_MP4, mPlayUrl);
            } else {
                Log.d(TAG, VIDEO_TYPE_M3U8);
                mMediaPlayer.setSource(UVMediaType.UVMEDIA_TYPE_M3U8, mPlayUrl);

            }

        } catch (Exception e) {
            Log.e("utovr", e.getMessage(), e);
        }
    }

    @Override
    public void updateProgress(long position) {
        if (position > 100) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    rlCover.setVisibility(View.GONE);
                }
            });

        }
        if (mCtrl != null) {
            mCtrl.updateCurrentPosition();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        OPENLOG.D("" + event);
        return super.onTouchEvent(event);
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            sendTouchEventAsync(mKeyKode);
        }
    };
    private int mKeyKode;


    private void sendTouchEventAsync(final int keyCode) {
        Log.d("MyPrint", "sendTouchEventAsync");
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        height = dm.heightPixels;
        width = dm.widthPixels;
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    if (isCompet) {
                        return;
                    }
                    OPENLOG.D("sendTouch " + keyCode);
                    Log.d("MyPrint", "sendTouch");
                    sendTouchEventInst(keyCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    float stepX = 0;
    float stepY = 0;
    float step = 0;
    float stepC = 0;
    final float XSTEP = 40;
    final float YSTEP = 120;

    //將方向鍵轉換為觸摸事件
    private void sendTouchEventInst(int keyCode) {
        Log.d("MyPrint", "stepY:" + stepY);
        Instrumentation mInst = new Instrumentation();
        float x = 0;
        float y = 0;
        //垂直
        boolean vertical = false;
        final int EVENT_SIZE = 12;

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
                x = width / 2;
                y = height / 2;
                if (stepX < XSTEP)
                    stepX += 10;
                else
                    stepX = XSTEP;
                vertical = true;
                step = 10;
                moveAction(keyCode, mInst, x, y, vertical, EVENT_SIZE);
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                x = width / 2;
                y = height / 2;
                if (stepX > -XSTEP)
                    stepX -= 10;
                else
                    stepX = -XSTEP;
                vertical = true;
                step = -10;
                moveAction(keyCode, mInst, x, y, vertical, EVENT_SIZE);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                x = width / 2;
                y = height / 2;
                if (stepY == -YSTEP)
                    stepY = 0;
                else
                    stepY -= 10;
                Log.d(TAG, "stepY:" + stepY);
                vertical = false;
                step = 10;
                moveAction(keyCode, mInst, x, y, vertical, EVENT_SIZE);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                x = width / 2;
                y = height / 2;
                if (stepY == YSTEP)
                    stepY = 0;
                else
                    stepY += 10;
                Log.d(TAG, "stepY:" + stepY);
                vertical = false;
                step = -10;
                moveAction(keyCode, mInst, x, y, vertical, EVENT_SIZE);
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (this.isPlaying()) {
                    this.pause();
                } else {
                    this.play();
                }
                //                Log.d(TAG, "********************");
                //                x = width / 2;
                //                y = height / 2;
                //                vertical = true;
                //                step = -stepX;
                //                stepX = 0;
                //                Log.d(TAG, "stepY:" + stepY);
                //               /* if (stepY<0){
                //                    stepC=-120-stepY;
                //                }else {
                //                    stepC=120-stepY;
                //                }*/
                //                stepC = -stepY;
                //                if (stepC == YSTEP || stepC == -YSTEP)
                //                    stepC = 0;
                //                Log.d(TAG, "stepC:" + stepC);
                //                stepY = 0;
                //                moveAction(keyCode, mInst, x, y, vertical, EVENT_SIZE);
                break;
            default:

                break;
        }
    }

    private void moveAction(int keyCode, Instrumentation mInst, float x, float y, boolean vertical, int EVENT_SIZE) {
        OPENLOG.D("dispatchTouchEvent: " + keyCode);
        MotionEvent ev = MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0);
        mInst.sendPointerSync(ev);
        OPENLOG.D("" + ev);
        Log.d("zblPrint", "step:" + step);
        for (int i = 0; i < EVENT_SIZE; i++) {
            ev = MotionEvent.obtain(SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, x, y, 0);
            mInst.sendPointerSync(ev);
            try {
                Thread.sleep(10); //10ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            OPENLOG.D("" + ev);
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                y += step;
                x += stepC;
                Log.d("moveTAction", "移动step:" + step);
                Log.d("moveTAction", "移动stepC:" + stepC);
            } else {
                if (vertical) {
                    y += step;
                } else {
                    x += step;
                }
            }

        }
        ev = MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
        mInst.sendPointerSync(ev);
        OPENLOG.D("" + ev);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        switch (keyCode) {
        }

        //            singleThreadExecutor.shutdown();
        isCompet = true;
        Log.d("print", "onKeyUp");
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(final int keyCode, KeyEvent event) {
        Log.d(TAG, "keyCode:" + keyCode);
        mKeyKode = keyCode;
        isCompet = false;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                sendTouchEventAsync(keyCode);
                //                    mHandler.postDelayed(mRunnable,200);
                return true;

            default:
                return super.onKeyDown(keyCode, event);
        }

    }

    public void setVolume(float volume) {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
        int streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                (int) (volume * streamMaxVolume),
                AudioManager.FLAG_SHOW_UI);
    }

    @Override
    public void play() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            LogUtils.i(TAG, "vr_play");
            if (mVideoPlayCallback != null) {
                mVideoPlayCallback.onStartPlay(mObject);
            }
            mMediaPlayer.play();
        }
    }

    @Override
    public void startPlayer(Play play, long msec) {
        mPlay = play;
        startPlayer(play.uri, msec);
    }

    @Override
    public void startPlayer(String playUrl, long msec) {
        if (TextUtils.isEmpty(playUrl)) {
            return;
        }
        String type = null;
        if (playUrl.endsWith(".m3u8")) {
            type = VIDEO_TYPE_M3U8;
        } else if (playUrl.endsWith(".mp4")) {
            type = VIDEO_TYPE_MP4;
        }
        seekTime = msec;
        mPlayUrl = playUrl;
        mType = type;
        LogUtils.i(TAG, "mPlayUrl: " + mPlayUrl + ", mType: " + mType + ", seekTime: " + seekTime);
        rlToolbar.setVisibility(View.VISIBLE);
        mMediaPlayer = new UVMediaPlayer(mActivity, rlPlayView, this);
        //将工具条的显示或隐藏交个SDK管理，也可自己管理

        mMediaPlayer.setToolbar(rlToolbar, null, imgBack);
        mCtrl = new VideoController(rlToolbar, this, true);
        changeOrientation(true);
    }

    @Override
    public void pause() {
        pause(false);
    }

    @Override
    public void pause(boolean showSeekbar) {
        LogUtils.i(TAG, "vr_pause");
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            if (mVideoPlayCallback != null) {
                mVideoPlayCallback.onPausePlay();
            }
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void close() {
        LogUtils.i(TAG, "close");
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer.onResume(null);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        stopUdpControl();
        if (singleThreadExecutor != null && !singleThreadExecutor.isShutdown()) {
            singleThreadExecutor.shutdown();
        }
        isRun = false;
        mIsFirstPlay = false;
        mIsFromDMR = false;
        mDuration = -1;
        mPlay = null;
        isSeek = false;
    }

    @Override
    public void replay() {
        if (mMediaPlayer != null) {
            mMediaPlayer.replay();
        }
    }

    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            return true;
        }
        return false;
    }

    @Override
    public long getDuration() {
        if (mDuration <= 0) {
            return 0;
        } else {
            return mDuration;
        }
    }

    @Override
    public long getCurrentPosition() {
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : 0;
    }

    @Override
    public void seekTo(long positionMs) {
        if (mMediaPlayer != null)
            mMediaPlayer.seekTo(positionMs);
    }

    // TODO 暂未使用
    @Override
    public void seekTo(long msec, boolean showSeekBar) {
//        if (mMediaPlayer != null)
//            mMediaPlayer.seekTo(msec);
    }

    @Override
    public long getBufferedPosition() {
        return mMediaPlayer != null ? mMediaPlayer.getBufferedPosition() : 0;
    }

    @Override
    public void setGyroEnabled(boolean val) {
        if (mMediaPlayer != null)
            mMediaPlayer.setGyroEnabled(val);
    }

    @Override
    public boolean isGyroEnabled() {
        return mMediaPlayer != null ? mMediaPlayer.isGyroEnabled() : false;
    }

    @Override
    public void setDualScreenEnabled(boolean val) {

    }

    @Override
    public boolean isDualScreenEnabled() {
        return mMediaPlayer != null ? mMediaPlayer.isDualScreenEnabled() : false;
    }

    @Override
    public void toFullScreen() {

    }

    @Override
    public void toolbarTouch(boolean start) {
        if (mMediaPlayer != null) {
            if (true) {
                mMediaPlayer.cancelHideToolbar();
            } else {
                mMediaPlayer.hideToolbarLater();
            }
        }
    }

    public void setFromDMR(boolean fromDMR) {
        mIsFromDMR = fromDMR;
    }

    private boolean isRun = false;
    DatagramSocket socket = null;

    /**
     * 开始udp控制
     */
    private void startUdpControl() {
        isRun = true;
        new Thread() {

            private int mLastMse;

            @Override
            public void run() {
                super.run();
                try {
                    isRun = true;
                    socket = new DatagramSocket(12170);
                    byte data[] = new byte[1024];

                    //创建一个空的DatagramPacket对象

                    DatagramPacket packet =

                            new DatagramPacket(data, data.length);

                    //使用receive方法接收客户端所发送的数据，

                    //如果客户端没有发送数据，该进程就停滞在这里
                    Log.d(TAG, "开始接收---->");
                    while (isRun) {
                        socket.receive(packet);
                        byte[] packetData = packet.getData();
                        //判断头信息
                        //                        byte[] headByte = new byte[4];
                        //                        System.arraycopy(packetData, 28, headByte, 0, 4);
                        String s = new String(packetData, 0, 4);
                        if (!s.equals("AABB")) {
                            continue;
                        }
                        //获取时间戳；
                        byte[] mesSeqByte = new byte[4];
                        System.arraycopy(packetData, 8, mesSeqByte, 0, 4);
                        int curMeq = Utils.byteArray2int(mesSeqByte);
                        //判断是否消息回滚，丢弃久消息
                        if (curMeq >= mLastMse || (mLastMse - curMeq) > 100000) {
                            mLastMse = curMeq;
                            Log.d(TAG, "curMeq:" + curMeq);
                            //获取消息长度
                            int length = getMessageLength(packetData);
                            Log.d(TAG, "length:" + length);
                            String result = new
                                    String(packetData, 32,
                                    length);
                            Log.d(TAG, "result---->" + result);
                            List<Integer> list = parseResult(result);
                            sendEventAsync(list);
                        } else
                            continue;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void stopUdpControl() {
        if (socket != null) {
            socket.close();
//            socket = null;
        }
    }

    /**
     * 发送模拟触屏事件
     *
     * @param list
     */
    Instrumentation mInst = new Instrumentation();

    private void sendEventAsync(List<Integer> list) {
        try {
            Integer press = list.get(4);
            int actionDown = 0;
            switch (press) {
                case 0:
                    actionDown = MotionEvent.ACTION_DOWN;
                    break;
                case 1:
                    actionDown = MotionEvent.ACTION_MOVE;
                    break;
                case 2:
                    actionDown = MotionEvent.ACTION_UP;
                    break;
                case 3:
                    actionDown = MotionEvent.ACTION_POINTER_DOWN;
                    break;
                case 4:
                    actionDown = MotionEvent.ACTION_POINTER_UP;
                    break;
                default:
                    break;
            }
            MotionEvent ev = MotionEvent.obtain(SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(), actionDown, list.get(2), list.get(3), 0);
            mInst.sendPointerSync(ev);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        close();
    }

    /**
     * 解析数据
     *
     * @param s
     * @return
     */
    private List<Integer> parseResult(String s) {
        List<Integer> list = new ArrayList<>();
        String[] split = s.split("=");
        Log.d(TAG, "split.length:" + split.length);
        try {
            for (int i = 0; i < split.length; i++) {
                if (split.length == 6) {
                    if (i > 0 && i < 5) {
                        String[] strings = split[i].split("&");
                        String string = strings[0];
                        list.add(Integer.parseInt(string));
                    }
                    if (i == 5) {
                        char strings = split[i].charAt(0);
                        list.add(Integer.parseInt(String.valueOf(strings)));
                    }
                } else if (split.length == 9) {

                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        Log.d(TAG, list.toString());
        return list;

    }

    /**
     * 解析消息长度
     *
     * @param packetData
     * @return
     */
    private int getMessageLength(byte[] packetData) {
        byte[] b = new byte[4];
        System.arraycopy(packetData, 28, b, 0, 4);
        return Utils.byteArray2int(b);
    }

    private boolean mIsFromDMR = false;
    private boolean mIsFirstPlay = true;
    private final int SEEK_VR_DMR = 1;
    private final int PLAY_TIME = 2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEEK_VR_DMR:
                    if (mIsFromDMR) {
                        mHandler.removeMessages(SEEK_VR_DMR);
                        int position = (int) getCurrentPosition();
                        MultiScreenManager.sendState(mContext, MultiScreenManager.STATE_ON_POSITON_CHANGED, String.valueOf(position));
                        mHandler.sendEmptyMessageDelayed(SEEK_VR_DMR, 1000);
                    }
                    break;
                case PLAY_TIME:
                    mHandler.removeMessages(PLAY_TIME);
                    if (mDuration > 0) {
                        long timeBorder = 15 * 60 * 1000;
                        long playTime = mMediaPlayer.getCurrentPosition();
                        LogUtils.d(TAG, "playbackState playTime: " + playTime + "");
                        if (mDuration != 0 && (playTime >= mDuration / 10 || playTime >= timeBorder)) {
                            if (!mIsAuthority && mVideoPlayCallback != null) {
                                mVideoPlayCallback.onFreeEnd();
                                return;
                            }
                        }
                    }
                    mHandler.sendEmptyMessageDelayed(PLAY_TIME, 2000);
                    break;
            }
        }
    };
}
