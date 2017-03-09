package com.ppfuns.vod.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.ppfuns.model.entity.DisplayHistory;
import com.ppfuns.util.LogUtils;
import com.ppfuns.util.SysUtils;
import com.ppfuns.util.eventbus.EventBusUtil;
import com.ppfuns.util.eventbus.EventConf;
import com.ppfuns.util.eventbus.InfoEvent;
import com.ppfuns.util.https.HttpUtil;
import com.ppfuns.vod.BaseApplication;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class UploadService extends Service {
    private static final String TAG = "UploadService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBusUtil.register(this);
        LogUtils.i(TAG, "onCreate");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
        LogUtils.i(TAG, "onDestroy");
    }


    public void upLoad(DisplayHistory displayHistory) {
        int userId = SysUtils.getUserId();
        LogUtils.d(TAG, "userId: " + userId);
        if (userId == -1) {
            return;
        }
        displayHistory.userId = String.valueOf(userId);
        String searchUrl = SysUtils.getUserUrl() + "/api/resumePoint/put.json";
        LogUtils.i(TAG, "history_upLoad: " + searchUrl);
        Map<String, String> map = new HashMap<>();
        map.put("userId", displayHistory.userId);
        map.put("resourceName", "" + displayHistory.resourceName);
        map.put("forwardUrl", "" + displayHistory.forwardUrl);
        map.put("timePosition", "" + displayHistory.timePosition);
        map.put("posterUrl", "" + displayHistory.posterUrl);
        map.put("cpVideoId", "" + displayHistory.cpVideoId);
        map.put("resourceId", "" + displayHistory.resourceId);
        map.put("type", "" + displayHistory.albumType);
        map.put("cpId", "" + displayHistory.cpId);
        map.put("playStatus", displayHistory.playStatus + "");
        map.put("duration", displayHistory.duration + "");
        map.put("userType", "1");
        LogUtils.i("UPLOADSERVICE", map.toString());
//        LogUtils.i("UPLOADSERVICE",displayHistory.toString()
//        LogUtils.i("UPLOADSERVICE",object);
        HttpUtil.post(searchUrl, map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {

                LogUtils.i("UPLOADSERVICE", response);
            }
        });
    }

    @Subscribe
    public void subscribe(InfoEvent infoEvent) {
        LogUtils.i(TAG, "upLoad");
        int id = infoEvent.id;
        Object obj = infoEvent.obj;
        switch (id) {
            case EventConf.UPLOAD_PUT_RESUME_POINT:
                if (obj instanceof DisplayHistory) {
                    DisplayHistory displayHistory = (DisplayHistory) obj;
                    LogUtils.i(TAG, "str=" + displayHistory.toString());
                    upLoad(displayHistory);
                }
                break;
        }


//        HttpUtil.getHttpHtml("", new StringCallback() {
//
//            @Override
//            public void onError(Call call, Exception e, int id) {
//
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//
//            }
//        });
    }

    /**
     * 返回一个Binder对象
     */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.


        LogUtils.i(TAG, "onBind");
        return new MsgBinder();
    }


    public class MsgBinder extends Binder {
        /**
         * 获取当前Service的实例
         *
         * @return
         */
        public UploadService getService() {
            return UploadService.this;
        }
    }
}
