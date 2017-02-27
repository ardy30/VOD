package com.utovr.playerdemo;

import android.app.Instrumentation;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.utovr.player.UVEventListener;
import com.utovr.player.UVInfoListener;
import com.utovr.player.UVMediaPlayer;
import com.utovr.player.UVMediaType;
import com.utovr.player.UVPlayerCallBack;
import com.utovr.playerdemo.util.OPENLOG;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class VRPlayerActivity extends BaseDmrActivity implements UVPlayerCallBack, VideoController.PlayerControl {
    private static final String TAG = "zblPrint";
    private static final int SPEED = 5000;
    private UVMediaPlayer mMediaplayer = null;  // 媒体视频播放器
    private VideoController mCtrl = null;
    private String Path = "";
    private boolean bufferResume = true;
    private boolean needBufferAnim = true;
    private ImageView imgBuffer;                // 缓冲动画
    private ImageView imgBack;
    private RelativeLayout rlParent = null;
    protected int CurOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private int SmallPlayH = 0;
    private boolean colseDualScreen = false;
    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private RelativeLayout rlPlayView;
    private int height;
    private int width;
    private boolean isCompet = false;
    private String mType;
    private boolean isRun;
    Instrumentation mInst = new Instrumentation();
    private RelativeLayout mRlToolbar;
    private String mCurrrentPlayerState;
    private RelativeLayout mRelativeLayoutCover;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_activity);


        Log.d(TAG, "onCreate: " + getIntent().getAction());
        Log.d(TAG, "onCreate: " + this);

        transportPlayerState(ON_END_OF_MEDIA);

        initView();
        //初始化播放器
        boolean initData = initData();
        startUdpControl();
        //false表示是dlna投屏启动
        if (!initData) {
            return;
        }


        initPlayer();


    }

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
                DatagramSocket socket = null;
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

    /**
     * 初始化播放器
     */
    private boolean initPlayer() {
        mMediaplayer = new UVMediaPlayer(VRPlayerActivity.this, rlPlayView, this);
        //将工具条的显示或隐藏交个SDK管理，也可自己管理
        mMediaplayer.setToolbar(mRlToolbar, null, imgBack);
        mCtrl = new VideoController(mRlToolbar, this, true);

        changeOrientation(true);

        return true;
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
                }else if (split.length==9){

                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        Log.d(TAG, list.toString());
        return list;

    }

    /**
     * 发送模拟触屏事件
     *
     * @param list
     */
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

    private boolean initData() {
        Intent intent = getIntent();

        String stringExtra = intent.getStringExtra(CONTROL);
        if (stringExtra != null) {
            return false;
        }

        Path = intent.getStringExtra("url");
//        Path="http://192.168.1.12/zb/hangzhou.mp4";
        Log.d(TAG, "Path:" + Path);
        mType = intent.getStringExtra("type");
//        mType = "mp4";

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMediaplayer != null) {
            mMediaplayer.onResume(VRPlayerActivity.this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaplayer != null) {
            mMediaplayer.onPause();
        }
    }

    @Override
    public void onDestroy() {
        releasePlayer();
        isRun = false;//关闭udp线程。
        super.onDestroy();
        Log.d(TAG, "onDestroy: " + this);
    }

    @Override
    protected String getCurrentPlayerState() {
        return mCurrrentPlayerState;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        OPENLOG.D("" + event);
        return super.onTouchEvent(event);
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
        isCompet = false;
        switch (keyCode) {
            case KeyEvent.KEYCODE_7:
                //快进
                seekTo(getCurrentPosition()+SPEED);
                return super.onKeyDown(keyCode, event);
            case KeyEvent.KEYCODE_8:
                //快退
                seekTo(getCurrrentPosition()-SPEED);
                return super.onKeyDown(keyCode, event);
            case KeyEvent.KEYCODE_9:
                if (mMediaplayer.isPlaying()) {
                    pause();
                } else {
                    play();
                }
                return super.onKeyDown(keyCode, event);
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                sendTouchEventAsync(keyCode);
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }

    }

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
                Log.d(TAG, "********************");
                x = width / 2;
                y = height / 2;
                vertical = true;
                step = -stepX;
                stepX = 0;
                Log.d(TAG, "stepY:" + stepY);
               /* if (stepY<0){
                    stepC=-120-stepY;
                }else {
                    stepC=120-stepY;
                }*/
                stepC = -stepY;
                if (stepC == YSTEP || stepC == -YSTEP)
                    stepC = 0;
                Log.d(TAG, "stepC:" + stepC);
                stepY = 0;
                moveAction(keyCode, mInst, x, y, vertical, EVENT_SIZE);
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        changeOrientation(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    private void changeOrientation(boolean isLandscape) {
        if (rlParent == null) {
            return;
        }
        if (isLandscape) {
            CurOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            rlParent.setLayoutParams(lp);
            if (colseDualScreen && mMediaplayer != null) {
                mCtrl.setDualScreenEnabled(true);
            }
            colseDualScreen = false;
            mCtrl.changeOrientation(true, 0);
        } else {
            CurOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getSmallPlayHeight();
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, SmallPlayH);
            rlParent.setLayoutParams(lp);
            if (mMediaplayer != null && mMediaplayer.isDualScreenEnabled()) {
                mCtrl.setDualScreenEnabled(false);
                colseDualScreen = true;
            }
            mCtrl.changeOrientation(false, 0);
        }
    }

    private void initView() {
        mRelativeLayoutCover= (RelativeLayout) findViewById(R.id.rl_cover);
        rlParent = (RelativeLayout) findViewById(R.id.activity_rlParent);
        imgBuffer = (ImageView) findViewById(R.id.activity_imgBuffer);
        rlPlayView = (RelativeLayout) findViewById(R.id.activity_rlPlayView);
        mRlToolbar = (RelativeLayout) findViewById(R.id.activity_rlToolbar);
    }

    /**
     * 切换当前播放器状态，仅仅设置标记
     *
     * @param currrentPlayerState
     */
    private void transportPlayerState(String currrentPlayerState) {
        mCurrrentPlayerState = currrentPlayerState;
    }

    public void releasePlayer() {
        if (mMediaplayer != null) {
            mMediaplayer.release();
            mMediaplayer = null;
        }
    }

    @Override
    public void createEnv() {
        try {
            // 创建媒体视频播放器
            mMediaplayer.initPlayer();
            mMediaplayer.setListener(mListener);
            mMediaplayer.setInfoListener(mInfoListener);
            if (mType.equals("mp4")) {
                //如果是网络MP4，可调用
//                mCtrl.startCachePro();
//                mCtrl.stopCachePro();
                Log.d(TAG, "mp4");
                mMediaplayer.setSource(UVMediaType.UVMEDIA_TYPE_MP4, Path);
            } else {
                Log.d(TAG, "m3u8");
                mMediaplayer.setSource(UVMediaType.UVMEDIA_TYPE_M3U8, Path);
            }
        } catch (Exception e) {
            Log.e("utovr", e.getMessage(), e);
        }
    }

    @Override
    public void updateProgress(long position) {
        if (position>100){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mRelativeLayoutCover.setVisibility(View.GONE);
                }
            });

        }
        if (mCtrl != null) {
            mCtrl.updateCurrentPosition();
        }
    }

    private UVEventListener mListener = new UVEventListener() {
        @Override
        public void onStateChanged(int playbackState) {
            switch (playbackState) {
                case UVMediaPlayer.STATE_PREPARING:
                    break;
                case UVMediaPlayer.STATE_BUFFERING:
                    if (needBufferAnim && mMediaplayer != null && mMediaplayer.isPlaying()) {
                        bufferResume = true;
                        Utils.setBufferVisibility(imgBuffer, true);
                    }
                    break;
                case UVMediaPlayer.STATE_READY:
                    // 设置时间和进度条
                    mCtrl.setInfo();
                    if (bufferResume) {
                        bufferResume = false;
                        Utils.setBufferVisibility(imgBuffer, false);
                    }
                    transportPlayerState(ON_PLAY);
                    break;
                case UVMediaPlayer.STATE_ENDED:
                    //这里是循环播放，可根据需求更改
//                    mMediaplayer.replay();
                    transportPlayerState(ON_END_OF_MEDIA);
                    break;
                case UVMediaPlayer.TRACK_DISABLED:
                case UVMediaPlayer.TRACK_DEFAULT:
                    break;
            }
        }

        @Override
        public void onError(Exception e, int ErrType) {
            Toast.makeText(VRPlayerActivity.this, Utils.getErrMsg(ErrType), Toast.LENGTH_SHORT).show();
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
    public long getDuration() {
        return mMediaplayer != null ? mMediaplayer.getDuration() : 0;
    }

    @Override
    public long getBufferedPosition() {
        return mMediaplayer != null ? mMediaplayer.getBufferedPosition() : 0;
    }

    @Override
    public long getCurrentPosition() {
        return mMediaplayer != null ? mMediaplayer.getCurrentPosition() : 0;
    }

    @Override
    public void setGyroEnabled(boolean val) {
        if (mMediaplayer != null)
            mMediaplayer.setGyroEnabled(val);
    }

    @Override
    public boolean isGyroEnabled() {
        return mMediaplayer != null ? mMediaplayer.isGyroEnabled() : false;
    }

    @Override
    public boolean isDualScreenEnabled() {
        return mMediaplayer != null ? mMediaplayer.isDualScreenEnabled() : false;
    }

    @Override
    public void toolbarTouch(boolean start) {
        if (mMediaplayer != null) {
            if (true) {
                mMediaplayer.cancelHideToolbar();
            } else {
                mMediaplayer.hideToolbarLater();
            }
        }
    }

    @Override
    public void pause() {
        if (mMediaplayer != null) {
            Log.d(TAG, "before pause   mMediaplayer.isPlaying: " + mMediaplayer.isPlaying());
            mMediaplayer.pause();
            Log.d(TAG, "after pause    mMediaplayer.isPlaying: " + mMediaplayer.isPlaying());
            transportPlayerState(ON_PAUSE);
            Log.d(TAG, "pause: 执行了暂停" + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + Thread.currentThread().getId() + " >>>>>>>>>>>>>>>>>>>>>>>>>>>>>  " + mMediaplayer.hashCode());
        } else {
            Log.d(TAG, "pause: mMediaplayer = null" + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + Thread.currentThread().getId());
        }
    }

    @Override
    public void seekTo(long positionMs) {
        if (mMediaplayer != null)
            mMediaplayer.seekTo(positionMs);
    }

    private void stop() {
        if (mMediaplayer != null)
            mMediaplayer.stop();
    }

    @Override
    public void play() {
        if (mMediaplayer != null) {
            Log.d(TAG, "before play  mMediaplayer.isPlaying: " + mMediaplayer.isPlaying());
            mMediaplayer.play();
            Log.d(TAG, "after play  mMediaplayer.isPlaying: " + mMediaplayer.isPlaying());
            transportPlayerState(ON_PLAY);
            Log.d(TAG, "play: 执行了播放" + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + Thread.currentThread().getId() + " >>>>>>>>>>>>>>>>>>>>>>>>>>>>>  " + mMediaplayer.hashCode());
        } else {
            Log.d(TAG, "play: mMediaplayer = null" + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + Thread.currentThread().getId());
        }
    }

    @Override
    protected void onDmrSetImage(String url, String title, long size, String mimeType, String albumArtUri, String date) {

    }

    @Override
    protected void onDmrSetAudio(String url, String title, long size, String mimeType, String albumArtUri, int duration, String album, String artist) {

    }

    @Override
    protected void onDmrSetVideo(String url, String title, long size, String mimeType, String albumArtUri, long duration) {
        Log.d(TAG, "onDmrSetVideo: " + url);

        Path = url;

        /**
         * 暂时这样判断
         */
        if (Path != null) {
            if (Path.contains("mp4")) {
                mType = "mp4";
            } else if (Path.contains("m3u8")) {
                mType = "m3u8";
            }
        }

        if (mMediaplayer == null) {
            initPlayer();
        }
        mMediaplayer.release();
        mMediaplayer = new UVMediaPlayer(this, rlPlayView, this);
        mMediaplayer.setToolbar(mRlToolbar, null, imgBack);
        mCtrl = new VideoController(mRlToolbar, VRPlayerActivity.this, true);
        mMediaplayer.play();
    }

    @Override
    protected void onDmrVolume(double volume) {
        Log.d(TAG, "volume:" + volume);
        AudioManager audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        int streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                (int) (volume * streamMaxVolume),
                AudioManager.FLAG_SHOW_UI);
    }

    @Override
    protected void onDmrSeek(int position) {
        Log.d(TAG, "onDmrSeek: " + position);
        seekTo(position);
    }

    @Override
    protected void onDmrStop() {
        Log.d(TAG, "onDmrStop: ");
        releasePlayer();
        finish();
    }

    @Override
    protected void onDmrPause() {
        Log.d(TAG, "onDmrPause: " + mMediaplayer.isPlaying());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                pause();
            }
        });
    }

    @Override
    protected void onDmrPlay() {
        Log.d(TAG, "onDmrPlay: ");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                play();
            }
        });
    }

    @Override
    protected int getCurrrentPosition() {
        return (int) mMediaplayer.getCurrentPosition();
    }

    @Override
    protected int getCurrentDuration() {
        return (int) mMediaplayer.getDuration();
    }

    @Override
    public void setDualScreenEnabled(boolean val) {
        if (mMediaplayer != null)
            mMediaplayer.setDualScreenEnabled(val);
    }

    @Override
    public void toFullScreen() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
    /* 大小屏切 可以再加上 ActivityInfo.SCREEN_ORIENTATION_SENSOR 效果更佳**/

    private void back() {
        if (CurOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            releasePlayer();
            finish();
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void getSmallPlayHeight() {
        if (this.SmallPlayH != 0) {
            return;
        }
        int ScreenW = getWindowManager().getDefaultDisplay().getWidth();
        int ScreenH = getWindowManager().getDefaultDisplay().getHeight();
        if (ScreenW > ScreenH) {
            int temp = ScreenW;
            ScreenW = ScreenH;
            ScreenH = temp;
        }
        SmallPlayH = ScreenW * ScreenW / ScreenH;
    }

}
