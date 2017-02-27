package com.ppfuns.vod.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ppfuns.model.db.ColumnDBManager;
import com.ppfuns.model.db.ColumnDbHelper;
import com.ppfuns.model.entity.ColumnInfo;
import com.ppfuns.model.entity.Columns;
import com.ppfuns.model.entity.Data;
import com.ppfuns.model.entity.Response;
import com.ppfuns.util.BitmapUtils;
import com.ppfuns.util.LogUtils;
import com.ppfuns.util.SysUtils;
import com.ppfuns.util.https.ContractUrl;
import com.ppfuns.util.https.HttpUtil;
import com.ppfuns.util.https.RequestCode;
import com.ppfuns.vod.activity.PlayerActivity;
import com.zhy.http.okhttp.callback.Callback;

import java.util.List;

import okhttp3.Call;

public class ColumnService extends Service {

    private final String TAG = ColumnService.class.getSimpleName();

    private int mRetryCount = 1;
    private final int RETRY_COUNT = 3;

    private ColumnDBManager mDBManager;

    public ColumnService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDBManager = ColumnDBManager.getInstance(this.getApplicationContext());
        LogUtils.i(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.i(TAG, "onStartCommand");
        if (intent !=null) {
            int cpId = intent.getIntExtra(PlayerActivity.COL_CONTENTID, 0);
            LogUtils.i(TAG, "cpId: " + cpId);
            if (cpId == 0) {
                mDBManager.deleteTable();
            }
            asyncTask(cpId);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void asyncTask(final int cpId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getColumnContent(cpId);
            }
        }).start();
    }

    private void getColumnContent(final int cpId) {
        String url = SysUtils.getContentUrl() + ContractUrl.COMMON + ContractUrl.COMMOL_COLUMNS_INFO + cpId + "-0.json";
        LogUtils.d(TAG, "getColumnContent " + url);
        HttpUtil.getHttpHtml(url, new Callback<Response<List<Columns>>>() {

            @Override
            public boolean validateReponse(okhttp3.Response response, int id) {
                LogUtils.i(TAG, "album_response_code: " + response.code());
                return response.isSuccessful();
            }

            @Override
            public Response<List<Columns>> parseNetworkResponse(okhttp3.Response response, int id) throws Exception {
                String result = response.body().string();
                LogUtils.d(TAG, "response result " + result);
                Response<List<Columns>> res = new Gson().fromJson(result, new TypeToken<Response<List<Columns>>>(){}.getType());
                return res;
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(TAG, "onError" + e.getMessage());
                if (mRetryCount <= RETRY_COUNT) {
                    mRetryCount++;
                    getColumnContent(cpId);
                } else {
                    mRetryCount = 1;
                }
            }

            @Override
            public void onResponse(Response<List<Columns>> response, int id) {
                if (response != null) {
                    Data data = response.data;
                    if (data != null) {
                        if (TextUtils.equals(data.code, RequestCode.RESPONSE_SUCCESS)) {
                            List<Columns> list = (List<Columns>) data.result;
                            if (list != null && list.size() > 0) {
                                int size = list.size();
                                if (size == 1) {
                                    mDBManager.addColumn(list.get(0));
                                } else {
                                    mDBManager.deleteTable();
                                    mDBManager.addColumns(list);
                                }
//                                mDBManager.closeDB();
                                for (int i = 0; i < size; i++) {
                                    Columns columns = list.get(i);
                                    if (columns != null) {
                                        int cpId = columns.cpId;
                                        String posterUrl = columns.logoDetail;
                                        loadImage(posterUrl, cpId);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void loadImage(String posterUrl, final int cpId) {
        LogUtils.i(TAG, "loadImage");
        if (TextUtils.isEmpty(posterUrl)) {
            return;
        }
        Glide.with(this).load(posterUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                saveImage(cpId, resource);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                LogUtils.e(TAG, "onLoadFailed");
            }
        });
    }

    private void saveImage(int cpId, Bitmap bm) {
        LogUtils.i(TAG, "saveImage");
        if (bm == null) {
            return;
        }
        String path = null;
        if (TextUtils.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState())) {
            path = Environment.getExternalStorageDirectory() + "/" +getPackageName() + "/files/columns/";
        } else {
            path = this.getFilesDir() + "/columns/";
        }
        String fileName = cpId + ".png";
        Bitmap bitmap = BitmapUtils.scaleBitmap(this, bm);
        BitmapUtils.saveBitmap(path, fileName, bitmap);
    }
}
