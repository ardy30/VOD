package com.ppfuns.vod.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ppfuns.util.LogUtils;
import com.ppfuns.vod.service.ColumnService;


public class BootBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = BootBroadcastReceiver.class.getSimpleName();
    static final String ACTION_BOOT = "com.ppfuns.launcher.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d(TAG, intent.getAction());
        if (intent.getAction().equals(ACTION_BOOT)) {
//            Intent ootStartIntent = new Intent(context, UploadService.class);
//            ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startService(ootStartIntent);

            // 开启栏目下载服务
            Intent serviceIntent = new Intent(context, ColumnService.class);
//            serviceIntent.putExtra(PlayerActivity.COL_CONTENTID, 0);
            context.startService(serviceIntent);
        }

    }

}