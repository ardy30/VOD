package com.ppfuns.vod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ppfuns.model.adapter.ChartAdapter;
import com.ppfuns.model.adapter.ChartChannelAdapter;
import com.ppfuns.model.entity.AlbumContents;
import com.ppfuns.model.entity.ChartChannel;
import com.ppfuns.model.entity.PageContent;
import com.ppfuns.model.entity.Response;
import com.ppfuns.ui.view.ChartHorizontalScrollView;
import com.ppfuns.ui.view.EffectNoDrawBridge;
import com.ppfuns.ui.view.MainUpView;
import com.ppfuns.util.LogUtils;
import com.ppfuns.util.SysUtils;
import com.ppfuns.util.https.ContractUrl;
import com.ppfuns.util.https.HttpUtil;
import com.ppfuns.util.https.RequestCode;
import com.ppfuns.vod.R;
import com.zhy.http.okhttp.callback.Callback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class ChartActivity extends BaseActivity implements View.OnFocusChangeListener {

    private final String TAG = ChartActivity.class.getSimpleName();

    private RelativeLayout mMainLayout;
    private RelativeLayout mContentLayout;
    private ImageView mIvLeft;
    private ImageView mIvRight;
    private RecyclerView mChannelRecyclerView;
    private RecyclerView mFirstRecyclerView;
    private ChartHorizontalScrollView mScrollView;

    private ChartChannelAdapter mChannelAdapter;

    private Response<List<ChartChannel>> mChartChannelResponse;
    private List<ChartChannel> mChartChannelList;
    private List<Integer> mTagList = new ArrayList<>();
    private String mChartChannelUrl;
    private final int BUSINESS_TYPE = 1;
    private int mChartCount = 0;

    /**
     * 网络失败重试操作次数
     */
    private final int RETRY_COUNT = 2;
    private int mRetryCount = 0;

    private MainUpView mMainUpView;
    private EffectNoDrawBridge bridget;

    private final String CHART_CHANNEL_TAG = "chart_channel";
    private final String CHART_INFO_TAG = "chart_info";
    private final long SHOW_LOADING_DEFAULT_TIME = 1000;

    private final int SHOW_LOADING = 0;
    private final int UPDATE_INFO = 1;
    private final int PARSE_CHART_CHANNEL = 2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_LOADING:
                    showLoadingProgressDialog(getString(R.string.data_loading));
                    break;
                case UPDATE_INFO:
                    break;
                case PARSE_CHART_CHANNEL:
                    parseChartChannel();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        initView();
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mChartChannelList == null || mChartChannelList.size() <= 0) {
            loadData();
        }
    }

    private void initView() {
        mMainLayout = (RelativeLayout) this.findViewById(R.id.rl_main);
        mContentLayout = (RelativeLayout) this.findViewById(R.id.rl_content);
        mIvLeft = (ImageView) this.findViewById(R.id.iv_left);
        mIvRight = (ImageView) this.findViewById(R.id.iv_right);
        mScrollView = (ChartHorizontalScrollView) this.findViewById(R.id.scrollView);
        mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mChannelRecyclerView.setFocusable(false);
            }
        });
        mChannelRecyclerView = (RecyclerView) this.findViewById(R.id.rv_channel);
        mMainUpView = (MainUpView) this.findViewById(R.id.main_up_view);
        initMainUpView();
    }

    /**
     * 初始化焦点框
     */
    protected void initMainUpView() {
        mMainUpView.setEffectBridge(new EffectNoDrawBridge());
        bridget = (EffectNoDrawBridge) mMainUpView.getEffectBridge();
        bridget.setVisibleWidget(true);
        bridget.setTranDurAnimTime(0);
        // 设置移动边框的图片.
        mMainUpView.setUpRectResource(R.drawable.item_chart_focus);
        // 移动方框缩小的距离.
    }

    private void initData() {
        if(vodColumnId < 0) {
            showDataError();
            return;
        }
        mChartChannelUrl = SysUtils.getContentUrl() + ContractUrl.COMMON + ContractUrl.COMMOL_CHART_CHANNEL + "/" + vodColumnId + "-0.json?businessType=" + BUSINESS_TYPE;
        LogUtils.d(TAG, "channel_url: " + mChartChannelUrl);

    }

    private void loadData() {
        mHandler.sendEmptyMessageDelayed(SHOW_LOADING, SHOW_LOADING_DEFAULT_TIME);
        requestChartChannel(mChartChannelUrl);
        mRetryCount = 0;
        mChartCount = 0;
    }

    private void parseChartChannel() {
        LogUtils.d(TAG, "parseChartChannel");
        if (mChartChannelResponse != null) {
            String code = mChartChannelResponse.data.code;
            if (TextUtils.equals(code, RequestCode.RESPONSE_SUCCESS)) {
                mChartChannelList = mChartChannelResponse.data.result;
                if (mChartChannelList != null || mChartChannelList.size() > 0) {
                    updateChannelView();
                    requestChartInfo();
                    return;
                }
                showDataError();
            } else {
                showDataError();
            }
        } else {
            showDataError();
        }
    }

    private void updateChannelView() {
        LogUtils.i(TAG, "updateChannelView");
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mChannelAdapter = new ChartChannelAdapter(this, mChartChannelList);
        mChannelRecyclerView.setLayoutManager(manager);
        int spaceItem = getResources().getDimensionPixelSize(R.dimen.chart_iv_mleft);
        mChannelRecyclerView.addItemDecoration(new ChartChannelAdapter.SpaceItemDec(spaceItem));
        mChannelRecyclerView.setAdapter(mChannelAdapter);

        int size = mChartChannelList.size();
        if (size > 4) {
            mIvRight.setVisibility(View.VISIBLE);
            mScrollView.setContentSize(size);
            mScrollView.setLeftArrow(mIvLeft);
            mScrollView.setRightArrow(mIvRight);
        }
    }

    private void requestChartInfo() {
        int len = mChartChannelList.size();
        for (int i = 0; i < len; i++) {
            ChartChannel chartChannel = mChartChannelList.get(i);
            if (chartChannel != null) {
                int operationTagId = chartChannel.operationTagId;
                mTagList.add(operationTagId);
                requestChartInfo(operationTagId);
            }
        }
    }

    private void requestChartInfo(final int operationTagId) {
        String url = SysUtils.getContentUrl() + ContractUrl.COMMON + ContractUrl.COMMOL_CHART_INFO + "/0-10-" + operationTagId + "-0.json?businessType=" + BUSINESS_TYPE + "&cpId=" + vodColumnId;
//        url = "http://192.168.1.11:9061/video-operation/api/r/classify_content/0-10-758-0.json?businessType=1";
        LogUtils.d(TAG, "chart_info_url: " + url);
        HttpUtil.getHttpHtml(url, operationTagId, new Callback<Response<PageContent<List<AlbumContents>>>>() {

            @Override
            public boolean validateReponse(okhttp3.Response response, int id) {
                LogUtils.d(TAG, "chart_info_response_code: " + response.code());
                return response.isSuccessful();
            }

            @Override
            public Response<PageContent<List<AlbumContents>>> parseNetworkResponse(okhttp3.Response response, int id) throws Exception {
                String result = response.body().string();
                LogUtils.d(TAG, "chart_channel_result: " + result);
                Response<PageContent<List<AlbumContents>>> resResponse = new Gson().fromJson(result, new TypeToken<Response<PageContent<List<AlbumContents>>>>() {
                }.getType());
                return resResponse;
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(TAG, "HttpGet " + operationTagId + " chartInfo failed " + e.getMessage());
                mChartCount++;
                showDataError();
            }

            @Override
            public void onResponse(Response<PageContent<List<AlbumContents>>> response, int id) {
                mChartCount++;
                boolean dataSuc = false;
                if (response != null) {
                    String code = response.data.code;
                    if (TextUtils.equals(code, RequestCode.RESPONSE_SUCCESS)) {
                        final List<AlbumContents> resList = response.data.result.pageContent;
                        if (resList != null && resList.size() > 0) {
                            updateChartInfo(resList, operationTagId);
                            dataSuc = true;
                        }
                    }
                }
                if (!dataSuc) {
                    showDataError();
                    return;
                }
                if (mChartCount == mChartChannelList.size()) {
                    showMainLayout();
                }
            }
        });
    }

    private void updateChartInfo(List<AlbumContents> list, final int operationTagId) {
        LogUtils.d(TAG, "updateChartInfo operationTagId: " + operationTagId);
        int width = getResources().getDimensionPixelSize(R.dimen.chart_rv_width);
        int topMargin = getResources().getDimensionPixelSize(R.dimen.chart_rv_mtop);

        int len = mChartChannelList.size();
        int i = 0;
        for (; i < len; i++) {
            ChartChannel chartChannel = mChartChannelList.get(i);
            if (chartChannel.operationTagId == operationTagId) {
                break;
            }
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        params.topMargin = topMargin;
        params.leftMargin = i * (getResources().getDimensionPixelSize(R.dimen.chart_iv_width)
                + getResources().getDimensionPixelSize(R.dimen.chart_iv_mleft))
                + getResources().getDimensionPixelSize(R.dimen.chart_iv_movie_mleft);

        final RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setFocusable(true);
        recyclerView.setLayoutParams(params);
        recyclerView.setDescendantFocusability(RecyclerView.FOCUS_BEFORE_DESCENDANTS);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        ChartAdapter mChannelAdapter = new ChartAdapter(this, list);
        mChannelAdapter.setFocusView(mMainUpView);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mChannelAdapter);
        if (i == 0) {
            recyclerView.setFocusable(true);
            mFirstRecyclerView = recyclerView;
        }
        mContentLayout.addView(recyclerView);
    }

    private void showMainLayout() {
        mHandler.removeMessages(SHOW_LOADING);
        dissmissLoadingDialog();
        mMainLayout.setVisibility(View.VISIBLE);
        mMainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mFirstRecyclerView != null) {
                    View childView = mFirstRecyclerView.getChildAt(0);
                    if (childView != null) {
                        childView.requestFocus();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                bridget.setVisibleWidget(false);
                                bridget.setTranDurAnimTime(200);
                            }
                        }, 100);
                    }
                }
                mMainLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    @Override
    protected void reLoad() {
        super.reLoad();
        loadData();
    }

    @Override
    protected void cancel() {
        super.cancel();
        finish();
    }

    private void showDataError() {
        mHandler.removeMessages(SHOW_LOADING);
        mChartCount = 0;
        cancelTag();
        dissmissLoadingDialog();
        showDialogTips(getString(R.string.data_error));
        mTipsDialog.setDisableBackKey(true);
    }

    /**
     * 获取排行榜频道数据
     *
     * @param url 请求地址
     */
    private void requestChartChannel(final String url) {
        LogUtils.i(TAG, "requestChartChannel");
        HttpUtil.getHttpHtml(url, CHART_CHANNEL_TAG, new Callback<Response<List<ChartChannel>>>() {

            @Override
            public boolean validateReponse(okhttp3.Response response, int id) {
                LogUtils.d(TAG, "chart_channel_response_code: " + response.code());
                return response.isSuccessful();
            }

            @Override
            public Response<List<ChartChannel>> parseNetworkResponse(okhttp3.Response response, int id) throws Exception {
                String result = response.body().string();
                LogUtils.d(TAG, "chart_channel_result: " + result);
                Response<List<ChartChannel>> resResponse = new Gson().fromJson(result, new TypeToken<Response<List<ChartChannel>>>() {
                }.getType());
                return resResponse;
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(TAG, "HttpGet chartChannel failed " + e.getMessage());
                if (!TextUtils.equals(e.getMessage(), "Canceled")) {
                    if (mRetryCount > RETRY_COUNT) {
                        showDataError();
                        return;
                    }
                    mRetryCount++;
                    requestChartChannel(url);
                }
            }

            @Override
            public void onResponse(Response<List<ChartChannel>> response, int id) {
                mChartChannelResponse = response;
                mHandler.sendEmptyMessage(PARSE_CHART_CHANNEL);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelTag();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void cancelTag() {
        HttpUtil.cancelTag(CHART_CHANNEL_TAG);
        if (mTagList != null) {
            int len = mTagList.size();
            if (len > 0) {
                for (int i = 0; i < len; i++) {
                    HttpUtil.cancelTag(mTagList.get(i));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainUpView.destroyDrawingCache();
        if (mChartChannelList != null) {
            mChartChannelList.clear();
            mChartChannelList = null;
        }
        if(mTagList != null) {
            mTagList.clear();
            mTagList = null;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
    }
}
