package com.ppfuns.vod.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ppfuns.model.entity.PageContent;
import com.ppfuns.model.entity.Response;
import com.ppfuns.model.entity.Search;
import com.ppfuns.model.entity.Subjects;
import com.ppfuns.util.LogUtils;
import com.ppfuns.util.SPConfig;
import com.ppfuns.util.SPUtils;
import com.ppfuns.util.SysUtils;
import com.ppfuns.util.https.ContractUrl;
import com.ppfuns.util.https.HttpUtil;
import com.ppfuns.vod.R;
import com.ppfuns.vod.activity.BackDoorActivity;
import com.ppfuns.vod.activity.SubjectDetailsActivity;
import com.ppfuns.vod.activity.VoicePlayListActivtiy;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import static com.ppfuns.util.JsonUtils.fromJson;

/**
 * 创建者     庄丰泽
 * 创建时间   2016/8/31 17:18
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class VoicePlayService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    private static final String TAG = "VoicePlayService";

    //搜索接口
    private String SEARCH_URL  = ContractUrl.getVodUrl() + "/r/so/";
    //专辑接口
    private String ALBUM_URL   = ContractUrl.getVodUrl() + "/v/album/";
    private String SPECIAL_URL = ContractUrl.getVodUrl() + "/r/subject/";
    //域名
    private String BASE_DOMAIN;


    //当前页
    private int mCurrentPage = 1;
    //栏目id
    private String mColumnId;
    //平台 :"0" 机顶盒 ,"1" 手机
    private String mPlatform;

    @Override
    public void onCreate() {
        super.onCreate();
        mPageContent = new ArrayList<>();
        mSearchBean = new Response<>();

    }

    /**
     * 获取应用配置相关 栏目id 平台类型
     */
    private final String PROPERTY_CONTNET = "persist.vod.content";

    private void initConf() {
        //获取域名
        BASE_DOMAIN = SysUtils.getContentUrl();


        LogUtils.i(TAG, "BASE_DOMAIN: " + BASE_DOMAIN);

        if (TextUtils.isEmpty(BASE_DOMAIN)) {
            LogUtils.d(TAG, ContractUrl.DOMAIN);
            //            BASE_DOMAIN = SPUtils.getData(this, SPUtils.SHARED_NAME, SPConfig.DOMAIN_KEY, "") + "";
            int domain = (int) SPUtils.getData(this, SPUtils.SHARED_NAME, SPConfig.DOMAIN_KEY, BackDoorActivity.mDomainEnvironment);
            switch (domain) {
                case BackDoorActivity.DOMAIN_FORMAL:
                    BASE_DOMAIN = ContractUrl.URL_HEAD + ContractUrl.URL_DOMAIN_FORMAL;
                    break;
                case BackDoorActivity.DOMAIN_TEST:
                    BASE_DOMAIN = ContractUrl.URL_HEAD + ContractUrl.URL_DOMAIN_TEST;
                    break;
            }
        }
        SEARCH_URL = BASE_DOMAIN + "/video-operation/api/r/so/";
        ALBUM_URL = BASE_DOMAIN + "/video-operation/api/v/album/";
        SPECIAL_URL = BASE_DOMAIN + "/video-operation/api/r/subject/";
        //获取栏目id
        mColumnId =SysUtils.getColumnId("");
        //        if ((int) SPUtils.getData(this, SPUtils.SHARED_NAME, SPConfig.COLUMN_ID_KEY, -1) != -1) {
        //            mColumnId = SPUtils.getData(this, SPUtils.SHARED_NAME, SPConfig.COLUMN_ID_KEY, -1) + "";
        //
        //
        //        } else {
        //            mColumnId = "20";
        //        }

        //获取平台
        mPlatform = "0";
        LogUtils.d(TAG, SEARCH_URL);
        LogUtils.d(TAG, ALBUM_URL);
        LogUtils.d(TAG, SPECIAL_URL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initConf();
        if (SysUtils.getUserId() == -1) {

            Toast.makeText(this, "用户异常", Toast.LENGTH_SHORT).show();

        } else {
            String videoname = intent.getStringExtra("videoname");

            if (videoname != null) {
                //请求网络
                LogUtils.d(TAG, videoname);
                requestDataFromSearch(videoname);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 请求搜索接口
     */
    private Response<PageContent<List<Search>>> mSearchBean;
    private List<Search>                        mPageContent;
    private static final int TYPE_ALBUM   = 0;
    private static final int TYPE_SPECIAL = 1;

    private void requestDataFromSearch(final String videoname) {
        mCurrentPage = 1;


        String keyWord = String.format(getString(R.string.keyword), mCurrentPage + "", "30", "", "",
                "", videoname, mPlatform);

        String searchUrl = SEARCH_URL + keyWord + ".json";

        HttpUtil.getHttpHtml(searchUrl, new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                //                mSearchBean = JsonUtils.fromJson(response, SearchBean.class);
                mSearchBean = (Response<PageContent<List<Search>>>) fromJson(response, new TypeToken<Response<PageContent<List<Search>>>>() {
                }.getType());

                mPageContent.clear();

                mPageContent.addAll(mSearchBean.data.result.pageContent);

                if (mPageContent.size() == 0) {
                    LogUtils.d(TAG, "search and numer == 0");
                    Toast.makeText(VoicePlayService.this, "没有该影片或该指令", Toast.LENGTH_SHORT).show();
                } else if (mPageContent.size() != 1) {
                    //数量大于1 显示所有内容 让用户操作

                    startActivity(new Intent(VoicePlayService.this, VoicePlayListActivtiy.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("response", response));

                    LogUtils.d(TAG, "search and numer != 1");
                } else {
                    LogUtils.d(TAG, "search and numer == 1");
                    //数据为1时 判断影片类型 继续请求网络
                    int isAlbum = mPageContent.get(0).isAlbum;

                    if (isAlbum == TYPE_ALBUM) {
                        //专辑
                        Toast.makeText(VoicePlayService.this, "正在为你播放" + videoname, Toast.LENGTH_SHORT).show();
                        Player(mPageContent.get(0).contentId + "",mPageContent.get(0).cpId+"");

                    } else if (isAlbum == TYPE_SPECIAL) {
                        //专题
                        requestDataFromSpecial(mPageContent.get(0).contentId + "");
                    }
                }
            }
        });
    }

    /**
     * 请求接口
     */
    /**
     * 调用播放器
     */
    private void Player(String albumId,String cpId) {
        Intent intent = new Intent();
        intent.setAction("com.ppfuns.vod.action.ACTION_VIEW_VOD_PLAY");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("albumId", albumId + "");
        intent.putExtra("index", 1 + "");
        intent.putExtra("col_contentId", cpId + "");


        LogUtils.d(TAG, albumId + ":\"albumId\"");
       // LogUtils.d(TAG, mColumnId + ":\"albumId\"");
        startActivity(intent);

    }

    /**
     * 请求专题接口
     */
    private String subjectJson = "";
    private Subjects bean;

    private void requestDataFromSpecial(String subjectId) {
        String url = SPECIAL_URL + subjectId + "-" + "" + "-" + mPlatform + ".json";
        HttpUtil.getHttpHtml(url, new Callback<com.ppfuns.model.entity.Response<List<Subjects>>>() {
            @Override
            public com.ppfuns.model.entity.Response<List<Subjects>> parseNetworkResponse(okhttp3.Response response, int id) throws Exception {
                String htmlStr = response.body().string();
                LogUtils.i(TAG, "htmlStr: " + htmlStr);
                if (subjectJson.equals(htmlStr)) {
                    return null;
                }
                subjectJson = htmlStr;
                Gson gson = new Gson();
                return gson.fromJson(htmlStr, new TypeToken<com.ppfuns.model.entity.Response<List<Subjects>>>() {
                }.getType());
            }

            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(com.ppfuns.model.entity.Response<List<Subjects>> response, int id) {
                com.ppfuns.model.entity.Response<List<Subjects>> subjectbean = (com.ppfuns.model.entity.Response<List<Subjects>>) response;
                if (subjectbean != null && subjectbean.data != null && subjectbean.data.result != null && subjectbean.data.result.size() > 0) {
                    bean = subjectbean.data.result.get(0);
                    if (subjectbean.data.result.get(0).getSubjectContentList().size() == 1) {
                        //只有一个专辑时 调用播放
                        int albumId = subjectbean.data.result.get(0).getSubjectContentList().get(0).contentId;
                        int cpId = subjectbean.data.result.get(0).getSubjectContentList().get(0).cpId;
                        Player(albumId + "",cpId+"");

                    } else {
                        //多个专辑 展示多个专辑
                        Intent intent = new Intent(VoicePlayService.this, SubjectDetailsActivity.class);

                        intent.putExtra("subjectId", subjectbean.data.result.get(0).subjectId);

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(VoicePlayService.this, "没有该影片", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
