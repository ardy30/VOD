package com.utovr.playerdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


public abstract class BaseDmrActivity extends Activity {
    private static final String TAG = BaseDmrActivity.class.getSimpleName();

    public static final String REC_ACTION = "com.ppfuns.dlnaservice.DMR_PLAYER_RECEIVER";

    public static final String ACTION_VIDEO = "com.ppfuns.filemanager.VIDEO_PLAYER";
    public static final String ACTION_AUDIO = "com.ppfuns.filemanager.AUDIO_PLAYER";
    public static final String ACTION_IMAGE = "com.ppfuns.filemanager.IMAGE_PLAYER";


    public static final String STATE_CHANGE = "state_change";
    public static final String ON_PLAY = "onPlay";
    public static final String ON_PAUSE = "onPause";
    public static final String ON_STOP = "onStop";
    public static final String ON_END_OF_MEDIA = "onEndOfMedia";
    public static final String ON_POS_CHANGE = "onPositionChanged";
    public static final String ON_DUR_CHANGE = "onDurationChanged";

    public static final String REC_PARAM_POSITION = "position";
    public static final String REC_PARAM_DURATION = "duration";


    /**
     * 控制类行的标记
     */
    public static final String CONTROL = "control"; //key
    public static final String CONTROL_START = "start";
    public static final String CONTROL_PLAY = "play";
    public static final String CONTROL_PAUSE = "pause";
    public static final String CONTROL_STOP = "stop";
    public static final String CONTROL_VOLUME = "volume";
    public static final String CONTROL_SEEK = "seek";

    /**
     * 控制参数
     */
    public static final String CTL_PARAM_TITLE = "title";
    public static final String CTL_PARAM_URL = "path";
    public static final String CTL_PARAM_MIMETYPE = "mimeType";
    public static final String CTL_PARAM_ALBUMARTURI = "albumArtUri";
    public static final String CTL_PARAM_DATE = "date";
    public static final String CTL_PARAM_SIZE = "size";
    public static final String CTL_PARAM_DURATION = "duration";
    public static final String CTL_PARAM_ALBUM = "album";
    public static final String CTL_PARAM_ARTIST = "artist";
    public static final String CTL_PARAM_VOLUME = "volume";
    public static final String CTL_PARAM_SEEK = "seek";

    private boolean isPlaying = false;
    private boolean isRunning;
    private boolean isCreated = false;

    private int mLastPosition;
    private int mLastDuration;
    private String mCurrentPlayeState;
    private Thread mUpDateThread;
    private boolean isSeeking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isRunning = true;
        isSeeking = false;
        mCurrentPlayeState = ON_END_OF_MEDIA;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (!isCreated) {
            Log.d(TAG, "onPostCreate: ");
            isCreated = true;
            onNewIntent(getIntent());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isPlaying = false;
        String currentPlayerState = getCurrentPlayerState();
        if (currentPlayerState != null && currentPlayerState != ON_END_OF_MEDIA && currentPlayerState != ON_STOP) {
            updatePlayerState(ON_PAUSE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
        updatePlayerState(ON_STOP);
    }

    @NonNull
    @Override
    protected void onNewIntent(Intent intent) {
        String control = intent.getStringExtra(CONTROL);
        if (control == null) {
            return;
        }
        Log.d(TAG, "onNewIntent: " + control);

        switch (control) {
            case CONTROL_START:
                //重置状态
                mLastDuration = 0;
                mLastPosition = 0;

                String url = intent.getStringExtra(CTL_PARAM_URL);
                String title = intent.getStringExtra(CTL_PARAM_TITLE);
                long size = intent.getLongExtra(CTL_PARAM_SIZE, 0);
                String mimeType = intent.getStringExtra(CTL_PARAM_MIMETYPE);
                if (mimeType == null) {
                    return;
                }
                if (mimeType.trim().contains("video")) {
                    String albumArtUri = intent.getStringExtra(CTL_PARAM_ALBUMARTURI);
                    long duration = intent.getLongExtra(CTL_PARAM_DURATION, 0);
                    //调用视频播放
                    setPlayVideo(url, title, size, mimeType, albumArtUri, duration);
                } else if (mimeType.trim().contains("audio")) {
                    String albumArtUri = intent.getStringExtra(CTL_PARAM_ALBUMARTURI);
                    int duration = intent.getIntExtra(CTL_PARAM_DURATION, 0);
                    String album = intent.getStringExtra(CTL_PARAM_ALBUM);
                    String artist = intent.getStringExtra(CTL_PARAM_ARTIST);
                    //调用音频播放
                    setPlayAudio(url, title, size, mimeType, albumArtUri, duration, album, artist);
                } else if (mimeType.trim().contains("image")) {
                    String albumArtUri = intent.getStringExtra(CTL_PARAM_ALBUMARTURI);
                    String date = intent.getStringExtra(CTL_PARAM_DATE);
                    //调用图片播放
                    setPlayImage(url, title, size, mimeType, albumArtUri, date);
                }

                break;
            case CONTROL_PLAY:
                play();
                break;
            case CONTROL_PAUSE:
                pasue();
                break;
            case CONTROL_STOP:
                stop();
                break;
            case CONTROL_VOLUME:
                double volume = intent.getDoubleExtra(CTL_PARAM_VOLUME, 0);
                volume(volume);
                break;
            case CONTROL_SEEK:
                int seek = intent.getIntExtra(CTL_PARAM_SEEK, 0);
                seek(seek);
                break;
            default:
        }
    }

    private Thread startUpDateThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    /**
                     * 获取当前播放器状态
                     */
                    String currentPlayerState = getCurrentPlayerState();
                    if (mCurrentPlayeState != null && currentPlayerState != null && !mCurrentPlayeState.equals(currentPlayerState)) {
                        Log.d(TAG, "状态改变了: " + mCurrentPlayeState + " -> " + currentPlayerState);
                        mCurrentPlayeState = currentPlayerState;
                        updatePlayerState(currentPlayerState);
                    }


                    if (isPlaying) {
                        /**
                         * 播放总时长更新
                         */
                        int currentDuration = getCurrentDuration();
                        if (mLastDuration != currentDuration) {
                            mLastDuration = currentDuration;
                            updatePlayerState(ON_DUR_CHANGE, currentDuration);
                        }

                        /**
                         * 当前播放时间
                         */
                        int currrentPosition = getCurrrentPosition();
                        if (mLastPosition != currrentPosition) {
                            mLastPosition = currrentPosition;
                            updatePlayerState(ON_POS_CHANGE, currrentPosition);
                        }
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        return thread;
    }

    protected abstract String getCurrentPlayerState();

    private void setPlayImage(String url, String title, long size, String mimeType, String albumArtUri, String date) {
        onDmrSetImage(url, title, size, mimeType, albumArtUri, date);
    }


    private void setPlayAudio(String url, String title, long size, String mimeType, String albumArtUri, int duration, String album, String artist) {
        onDmrSetAudio(url, title, size, mimeType, albumArtUri, duration, album, artist);
        if (mUpDateThread == null) {
            mUpDateThread = startUpDateThread();
        }
    }

    private void setPlayVideo(String url, String title, long size, String mimeType, String albumArtUri, long duration) {
        onDmrSetVideo(url, title, size, mimeType, albumArtUri, duration);
        if (mUpDateThread == null) {
            mUpDateThread = startUpDateThread();
        }
    }

    private void seek(int seek) {
        onDmrSeek(seek);
    }


    private void volume(double volume) {
        /**
         * 音量变化后，发送更新音量值
         */
        onDmrVolume(volume);
    }

    private void stop() {
        /**
         * 在关闭之前，做一些释放操作
         */
        onDmrStop();
        updatePlayerState(ON_STOP);
        isPlaying = false;
    }

    private void pasue() {
        onDmrPause();

        /**
         * 通知切换为暂停状态
         */
        updatePlayerState(ON_PAUSE);
        isPlaying = false;
    }

    private void play() {
        onDmrPlay();
        /**
         * 通知切换为播放状态
         */
        updatePlayerState(ON_PLAY);
        isPlaying = true;
    }


    protected void updatePlayerState(String stateChange, Object... params) {
        Intent intent = new Intent(REC_ACTION)
                .putExtra(STATE_CHANGE, stateChange);

        switch (stateChange) {
            case ON_PLAY:
            case ON_PAUSE:
            case ON_STOP:
            case ON_END_OF_MEDIA:
                break;
            case ON_POS_CHANGE:
                int pos = (int) params[0];
                intent.putExtra(REC_PARAM_POSITION, pos);
                break;
            case ON_DUR_CHANGE:
                int dur = (int) params[0];
                intent.putExtra(REC_PARAM_DURATION, dur);
//                Log.d(TAG, "updatePlayerState: " + stateChange);
                break;
            default:

        }
        sendBroadcast(intent);
    }

    protected abstract void onDmrSetImage(String url, String title, long size, String mimeType, String albumArtUri, String date);

    protected abstract void onDmrSetAudio(String url, String title, long size, String mimeType, String albumArtUri, int duration, String album, String artist);

    protected abstract void onDmrSetVideo(String url, String title, long size, String mimeType, String albumArtUri, long duration);

    protected abstract void onDmrVolume(double volume);

    protected abstract void onDmrSeek(int position);

    protected abstract void onDmrStop();

    protected abstract void onDmrPause();

    protected abstract void onDmrPlay();

    protected abstract int getCurrrentPosition();

    protected abstract int getCurrentDuration();

}
