package com.ppfuns.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * Created by hmc on 2016/11/16.
 */

public class BroadcastUtils {

    private static final String TAG = BroadcastUtils.class.getSimpleName();
    private static final String ACTION_MSG = "com.ppfuns.action.msg";
    private static final String ACTION_MSG_TYPE = "type";
    private static final String ACTION_MSG_ACTIONS = "actions";
    private static final String ACTION_AAA_USER = "com.aaaservice.action.TRY_REG";

    /**
     * 行为采集
     * @param context
     * @param type  采集类型
     * @param actions  采集字段(JSON格式)
     */
    public static void actionCollection(Context context, int type, String actions) {
        LogUtils.d(TAG, "actions: " + actions);
        if (TextUtils.isEmpty(actions)) {
            return;
        }
        Intent intent =  new Intent(ACTION_MSG);
        Bundle bundle =  new Bundle();
        bundle.putInt(ACTION_MSG_TYPE, type);
        bundle.putString(ACTION_MSG_ACTIONS, actions);
        intent.putExtras(bundle);
        context.sendBroadcast(intent);
    }

    public static void actionCollection(Context context, int type, Object objActions) {
        if (objActions == null) {
            return;
        }
        String actions = JsonUtils.toJson(objActions);
        actionCollection(context, type, actions);
    }

    public static void sendAAAForUser(Context context) {
        sendBroadcast(context, ACTION_AAA_USER);
    }

    public static void sendBroadcast(Context context, String action) {
        LogUtils.d(TAG, "sendBroadcast action: " + action);
        Intent intent =  new Intent();
        intent.setAction(action);
        context.sendBroadcast(intent);
    }

    public static void registBroadcast(Context context, BroadcastReceiver receiver, String action) {
    }
}
