package com.ppfuns.vod.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.ppfuns.model.adapter.AllChannelAdapter;
import com.ppfuns.model.entity.Channel;
import com.ppfuns.model.entity.Response;
import com.ppfuns.ui.view.GridLayoutManagerTV;
import com.ppfuns.ui.view.MainUpView;
import com.ppfuns.ui.view.OnChildSelectedListener;
import com.ppfuns.ui.view.RecyclerViewBridge;
import com.ppfuns.ui.view.RecyclerViewTV;
import com.ppfuns.util.LogUtils;
import com.ppfuns.util.SysUtils;
import com.ppfuns.util.https.ContractUrl;
import com.ppfuns.util.https.HttpUtil;
import com.ppfuns.util.https.RequestCode;
import com.ppfuns.vod.BaseApplication;
import com.ppfuns.vod.R;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static com.ppfuns.util.JsonUtils.fromJson;

/**
 * 创建者     庄丰泽
 * 创建时间   2016/11/23 11:44
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class AllChannelActivity extends BaseActivity {

    private RecyclerView      mAllChannel;
    private GridLayoutManagerTV mGridLayoutManagerTV;
    private AllChannelAdapter   mAllChannelAdapter;

    private MainUpView         mMainUpView;
    public  RecyclerViewBridge mOpenEffectBridge;


    //主页频道URL
    private String mChannelUrl;
    //栏目id
//    private String mColumnId;
    //平台 "0" 机顶盒 "1"
    private String mPlatform = "0";
    //    private ChannelBean mChannelBean;//频道
    private com.ppfuns.model.entity.Response<List<Channel>> mChannelResponse;
    String channelJson;
    private List<Channel> mResult = new ArrayList<>();
    private ImageView mIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allchannel);

        DisplayMetrics displayMetrics = new DisplayMetrics();

        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        defaultDisplay.getMetrics(displayMetrics);
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();


        initView();

    }

    private void initView() {

        mAllChannel = (RecyclerView) findViewById(R.id.allchannel_rv);

        mMainUpView = (MainUpView) findViewById(R.id.allchannel_mainupview);

//        mIcon = (ImageView) findViewById(R.id.allchannel_title_logo);
//
//        Glide.with(this).load(R.drawable.ju_logo).into(mIcon);


        setMainUpView();

    }

    private void setMainUpView() {
        mMainUpView.setEffectBridge(new RecyclerViewBridge());

        mOpenEffectBridge = (RecyclerViewBridge) mMainUpView.getEffectBridge();

        //设置边框
        mMainUpView.setUpRectResource(R.drawable.main_focus_2);

        int focusSize = (int) getResources().getDimension(R.dimen.focus_padding);

        mMainUpView.setDrawUpRectPadding(new Rect(focusSize, focusSize, focusSize, focusSize));
        mOpenEffectBridge.setVisibleWidget(true);
    }


    @Override
    protected void onStart() {
        super.onStart();
        initData();


    }

    private void initData() {
        initConf();

        requestAllChannel();
    }

    private void initConf() {
        //栏目id
//        if (BaseApplication.getmColumnId() != -1) {
//            mColumnId = BaseApplication.getmColumnId() + "";
//        } else {
//            mColumnId = "20";
//        }
        //TODO :平台类型

        mChannelUrl = ContractUrl.getVodUrl() + "/r/channel/" + vodColumnId + "-" + mPlatform + ".json";

        LogUtils.d(TAG + "1", mChannelUrl);

    }

    private void requestAllChannel() {
        HttpUtil.getHttpHtml(mChannelUrl, mChannelResponse, new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                if (TextUtils.equals(response, channelJson)) {
                    return;
                }

                channelJson = response;


                //                mChannelBean = JsonUtils.fromJson(response, ChannelBean.class);
                mChannelResponse = (com.ppfuns.model.entity.Response<List<Channel>>) fromJson(response, new TypeToken<Response<List<Channel>>>() {
                }.getType());

                LogUtils.d(TAG, mChannelResponse.data.result.toString());
                LogUtils.d(TAG, mResult.toString());

                if (TextUtils.equals(mResult.toString(), mChannelResponse.data.result.toString())) {
                    return;
                }
                mResult = mChannelResponse.data.result;

                //更新Ui
                setRecyclerViewAdapter();
            }
        });
    }

    private static final String TAG = "AllChannelActivity";

    private void setRecyclerViewAdapter() {
        if (mAllChannelAdapter == null) {
            mAllChannelAdapter = new AllChannelAdapter(this, mResult);

            mGridLayoutManagerTV = new GridLayoutManagerTV(this, 2);
            mGridLayoutManagerTV.setOrientation(GridLayoutManager.HORIZONTAL);

            mAllChannel.setLayoutManager(mGridLayoutManagerTV);
            mAllChannel.setAdapter(mAllChannelAdapter);

            int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.main_content_item_margin);
            mAllChannel.addItemDecoration(new SpaceItemDecoration(spacingInPixels));

            mGridLayoutManagerTV.setOnChildSelectedListener(new OnChildSelectedListener() {

                @Override
                public void onChildSelected(RecyclerView parent, View view, int position, int dy) {
                    super.onChildSelected(parent, view, position, dy);
                    mOpenEffectBridge.setFocusView(view, 1);
                    mOpenEffectBridge.setTranDurAnimTime(200);
                    mOpenEffectBridge.setVisibleWidget(false);
                }
            });

        } else {
            mAllChannelAdapter.notifyDataSetChanged();
        }

    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = space;
            outRect.right = space;
        }
    }

    /**
     * 打开频道位Activity的方法
     *
     * @param position 频道位中的item下标
     */
    public void openItem(int position) {
        if (!SysUtils.isNetworkAvailable(this)) {
            showDialogTips(getResources().getString(R.string.network_error));
            return;
        }
        if (mChannelResponse == null) {
            showDialogTips(getResources().getString(R.string.data_loading));

            return;
        }


        if (mChannelResponse.data.code.equals(RequestCode.SYSTEM_ERROR)) {
            showDialogTips(getResources().getString(R.string.data_error));
            return;
        }
        if (mChannelResponse.data.code.equals(RequestCode.REQUEST_NOT_FOUND)) {
            showDialogTips(getResources().getString(R.string.network_error));
            return;
        }
        if (mChannelResponse.data.code.equals(RequestCode.CONTENT_EMPTY)) {
            showDialogTips(getResources().getString(R.string.data_error));
            return;
        }
        if (position >= mChannelResponse.data.result.size()) {

            return;
        }

        int channelId = -1;
        if (mChannelResponse.data.result.get(position).operationTagId == 0) {
            showDialogTips(getResources().getString(R.string.data_error));
            return;
        }
        channelId = mChannelResponse.data.result.get(position).operationTagId;

        if (channelId == -1) {
            showDialogTips(getResources().getString(R.string.data_error));
            return;
        }

        Intent intent = new Intent(this, ChannelCategoryActivity.class);
        String channelName = "";
        intent.putExtra("channelId", channelId);
        if (!TextUtils.isEmpty(mChannelResponse.data.result.get(position).operationTagName)) {
            intent.putExtra("channelName", mChannelResponse.data.result.get(position).operationTagName);
            channelName = mChannelResponse.data.result.get(position).operationTagName;
        }
        Map<String, String> map = new HashMap<>();
        map.put("channelId", "" + channelId);
        map.put("channelName", channelName);
        MobclickAgent.onEvent(this, "channel_select", map);
        startActivity(intent);
    }
}
