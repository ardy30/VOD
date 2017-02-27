package com.ppfuns.util.https;

import android.content.Context;
import android.text.TextUtils;

import com.ppfuns.util.LogUtils;
import com.ppfuns.util.SysUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by hmc on 2016/6/15.
 */
public class HttpUtil {

    private static final String TAG = HttpUtil.class.getSimpleName();

    private static final int FLAG_HTTP = 100;

    private static final int FLAG_HTTPS = 100;

    private static Context mContext;

    public static void initClient(final Context context) {
        mContext = context;
        File httpCacheDirectory = new File(context.getCacheDir(), "responses");
        //设置缓存 10M
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                LogUtils.d(TAG, "intercept");
                if (!SysUtils.isNetworkAvailable(context)) {
                    LogUtils.d(TAG, "No NETWork  ReadCache;Request");
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                }
                Response originalResponse = chain.proceed(request);
                if (SysUtils.isNetworkAvailable(context)) {
                    LogUtils.d(TAG, "has NETWork  ReadNetWork;Response");
                    String cacheControl = request.cacheControl().toString();
                    return originalResponse.newBuilder()
                            .header("Cache-Control", "public, max-age=" + 0)
                            .removeHeader("Pragma")
                            .build();
                } else {
                    LogUtils.d(TAG, "No NETWork  ReadNetWork;Response");
                    return originalResponse.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=2419200")
                            .removeHeader("Pragma")
                            .build();
                }
            }

        };

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor).addInterceptor(interceptor)
                .cache(cache).addInterceptor(LoggingInterceptor)
                .connectTimeout(10_000, TimeUnit.MILLISECONDS)
                .writeTimeout(10_000, TimeUnit.MILLISECONDS)
                .readTimeout(10_000, TimeUnit.MILLISECONDS).build();
        initClient(okHttpClient);


    }

    public static boolean needCache = true;

    private static final Interceptor LoggingInterceptor = new LoggerInterceptor(TAG) {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            long t1 = System.nanoTime();
            LogUtils.d(TAG, String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));
            Response response = chain.proceed(request);
            long t2 = System.nanoTime();

            LogUtils.d(TAG, String.format("Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));



            ResponseBody responseBody = response.body();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();
            String result = buffer.clone().readString(UTF8);

            LogUtils.Log2View(mContext, "vod",LogUtils.Debug, "url:"+request.url());
            LogUtils.Log2View(mContext, "vod",LogUtils.Debug, "response:"+result);
            LogUtils.d(TAG,result);


            return response;
        }

    };

    private static final Charset UTF8 = Charset.forName("UTF-8");





    public static void initClient(OkHttpClient okHttpClient) {
        OkHttpUtils.initClient(okHttpClient);
    }

    public static void getHttpHtml(String url, Callback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        OkHttpUtils.get().url(url).id(FLAG_HTTP).build().execute(callback);
    }

    public static void getHttpHtml(String url, Object tag, Callback callback) {
        if (tag == null) {
            getHttpHtml(url, callback);
            return;
        }
        if (TextUtils.isEmpty(url)) {
            return;
        }
        OkHttpUtils.get().url(url).id(FLAG_HTTP).tag(tag).build().execute(callback);
    }

    public static void getHttpsHtml(String url, Callback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        OkHttpUtils.get().url(url).id(FLAG_HTTPS).build().execute(callback);
    }

    public static void getHttpsHtml(String url, Object tag, Callback callback) {
        if (tag == null) {
            getHttpsHtml(url, callback);
            return;
        }
        if (TextUtils.isEmpty(url)) {
            return;
        }
        OkHttpUtils.get().url(url).id(FLAG_HTTPS).tag(tag).build().execute(callback);
    }

    public static void post(String url, Callback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        OkHttpUtils.post().url(url).build().execute(callback);
    }

    public static void post(String url, Object tag, Callback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (tag == null) {
            post(url, callback);
            return;
        }
        OkHttpUtils.post().url(url).tag(tag).build().execute(callback);
    }

    public static void post(String url, Map<String, String> mData, Callback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (mData == null) {
            post(url, callback);
            return;
        }
        OkHttpUtils.post().url(url).params(mData).build().execute(callback);
    }

    public static void post(String url, Map<String, String> mData, Object tag, Callback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (mData == null) {
            post(url, tag, callback);
            return;
        }
        if (tag == null) {
            post(url, mData, callback);
            return;
        }
        OkHttpUtils.post().url(url).params(mData).tag(tag).build().execute(callback);
    }

    // 提交字符串到服务器端
    public static void postString(String url, JSONObject json, Callback callback) {
        if (TextUtils.isEmpty(url) || !(json != null && json.length() > 0)) {
            return;
        }
        postString(url, json.toString(), callback);
    }

    public static void postString(String url, JSONObject json, Object tag, Callback callback) {
        if (tag == null) {
            postString(url, json, callback);
        }
        if (TextUtils.isEmpty(url) || !(json != null && json.length() > 0)) {
            return;
        }
        postString(url, json.toString(), tag, callback);
    }

    public static void postString(String url, String content, Callback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        OkHttpUtils.postString().url(url).content(content).build().execute(callback);
    }

    public static void postString(String url, String content, Object tag, Callback callback) {
        if (tag == null) {
            postString(url, content, callback);
            return;
        }
        if (TextUtils.isEmpty(url)) {
            return;
        }
        OkHttpUtils.postString().url(url).content(content).tag(tag).build().execute(callback);
    }

    public static void downLoadFile(String url, FileCallBack callBack) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        OkHttpUtils.get().url(url).build().execute(callBack);
    }

    public static void downLoadFile(String url, Object tag, FileCallBack callBack) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (tag == null) {
            downLoadFile(url, callBack);
            return;
        }
        OkHttpUtils.get().url(url).tag(tag).build().execute(callBack);
    }

    public static void downLoadFile(String url, Map<String, String> mData, FileCallBack callBack) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (mData == null) {
            downLoadFile(url, callBack);
            return;
        }
        OkHttpUtils.get().url(url).params(mData).build().execute(callBack);
    }

    public static void downLoadFile(String url, Map<String, String> mData, Object tag, FileCallBack callBack) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (mData == null) {
            downLoadFile(url, tag, callBack);
            return;
        }
        if (tag == null) {
            downLoadFile(url, mData, callBack);
            return;
        }
        OkHttpUtils.get().url(url).params(mData).tag(tag).build().execute(callBack);
    }

    public static void uploadFile(String url, File file, Callback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (!file.exists()) {
            LogUtils.e(TAG, "file not exist");
            return;
        }
        OkHttpUtils.postFile().url(url).file(file).build().execute(callback);
    }

    public static void uploadFile(String url, File file, Object tag, Callback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (!file.exists()) {
            LogUtils.e(TAG, "file not exist");
            return;
        }
        if (tag == null) {
            uploadFile(url, file, callback);
            return;
        }
        OkHttpUtils.postFile().url(url).file(file).tag(tag).build().execute(callback);
    }

    public static void getImage(String url, BitmapCallback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        OkHttpUtils.get().url(url).build().execute(callback);
    }

    public static void getImage(String url, Object tag, BitmapCallback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (tag == null) {
            getImage(url, callback);
            return;
        }
        OkHttpUtils.get().url(url).tag(tag).build().execute(callback);
    }

    public static void cancelTag(Object tag) {
        OkHttpUtils.getInstance().cancelTag(tag);
    }
}
