package com.ppfuns.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ppfuns.vod.receiver.ReceiverActionFlag;

import java.util.Map;

/**
 * 定时反馈播放状态给DlnaService
 * Created by hmc on 2017/2/17.
 */

public class MultiScreenManager {

    /**
     * 当前播放器的状态
     */
    public static final String KEY_STATE_CHANGE = "state_change";

    /**
     * 正在播放
     */
    public static final String STATE_ON_PLAY = "onPlay";

    /**
     * 暂停
     */
    public static final String STATE_ON_PAUSE = "onPause";

    /**
     * 播放停止
     */
    public static final String STATE_ON_STOP = "onStop";

    /**
     * 播放结束
     */
    public static final String STATE_ON_END_OF_MEDIA = "onEndOfMedia";

    /**
     * 播放进度改变
     */
    public static final String STATE_ON_POSITON_CHANGED = "onPositionChanged";

    /**
     * 播放时长改变
     */
    public static final String STATE_ON_DURATION_CHANGED = "onDurationChanged";

    /**
     * 当前播放器的播放进度
     */
    public static final String KEY_POSITION = "position";

    /**
     * 当前播放视频的总时长
     */
    public static final String KEY_DURATION = "duration";

    /**
     * 标记发送来源
     */
    public static final String KEY_SENDER = "sender";

    private static final String TAG = MultiScreenManager.class.getSimpleName();

    /**
     * 向Dlna发送播放器状态
     * @param context
     * @param map
     */
    public static void sendState(Context context, Map<String, Object> map) {
        LogUtils.i(TAG, "sendState");
        if (map == null || !map.containsKey(KEY_STATE_CHANGE)) {
            return;
        }
        try {
            String stateChange = (String) map.get(KEY_STATE_CHANGE);
            Intent intent = new Intent();
            intent.setAction(ReceiverActionFlag.DMR_PLAYER_ACTION);
            intent.putExtra(KEY_STATE_CHANGE, stateChange);
            intent.putExtra(KEY_SENDER, "vod");

            if (TextUtils.equals(stateChange, STATE_ON_POSITON_CHANGED)) {
                intent.putExtra(STATE_ON_POSITON_CHANGED, (int)map.get(STATE_ON_POSITON_CHANGED));
            }
            if (TextUtils.equals(stateChange, STATE_ON_DURATION_CHANGED)) {
                intent.putExtra(STATE_ON_DURATION_CHANGED, (int)map.get(STATE_ON_DURATION_CHANGED));
            }
            context.sendBroadcast(intent);
        } catch (Exception e) {
            LogUtils.e(TAG, "error " + e.getMessage());
        }
    }

    /**
     * 向Dlna发送播放器状态
     * @param context
     * @param params 0:state_change 1:duration或position
     */
    public static void sendState(Context context, String... params) {
        LogUtils.i(TAG, "sendState");
        try {
            String stateChange = params[0];
            LogUtils.d(TAG, "stateChange: " + stateChange);
            Intent intent = new Intent();
            intent.setAction(ReceiverActionFlag.DMR_PLAYER_ACTION);
            intent.putExtra(KEY_STATE_CHANGE, stateChange);
            intent.putExtra(KEY_SENDER, "vod");

            if (TextUtils.equals(stateChange, STATE_ON_POSITON_CHANGED)) {
                intent.putExtra(KEY_POSITION, Integer.valueOf(params[1]));
            } else if (TextUtils.equals(stateChange, STATE_ON_DURATION_CHANGED)) {
                intent.putExtra(KEY_DURATION, Integer.valueOf(params[1]));
            }
            context.sendBroadcast(intent);
        } catch (Exception e) {
            LogUtils.e(TAG, "error " + e.getMessage());
        }
    }
}
