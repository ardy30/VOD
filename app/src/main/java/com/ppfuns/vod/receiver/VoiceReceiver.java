package com.ppfuns.vod.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ppfuns.util.LogUtils;
import com.ppfuns.vod.service.VoicePlayService;

/**
 * Created by zpf on 2016/8/30.
 */
public class VoiceReceiver extends BroadcastReceiver {
    private static final String TAG = "VoiceReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {

        String videoname = intent.getStringExtra("videoname");
        LogUtils.d(TAG,"VoiceReceiver:"+videoname);

        if (videoname!=null){
            context.startService(new Intent(context, VoicePlayService.class).putExtra("videoname",videoname));
        }
    }
}
