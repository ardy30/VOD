package com.ppfuns.vod.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.ppfuns.model.adapter.EpisodeContentAdapter;
import com.ppfuns.model.adapter.EpisodeTitleAdapter;
import com.ppfuns.model.adapter.OnItemActionListener;
import com.ppfuns.model.adapter.RecommendAdapter;
import com.ppfuns.model.entity.Authority;
import com.ppfuns.model.entity.ContentType;
import com.ppfuns.model.entity.Data;
import com.ppfuns.model.entity.DisplayHistory;
import com.ppfuns.model.entity.EventBusBean;
import com.ppfuns.model.entity.PageContent;
import com.ppfuns.model.entity.PayInfo;
import com.ppfuns.model.entity.PayState;
import com.ppfuns.model.entity.RecommendBean;
import com.ppfuns.model.entity.Response2;
import com.ppfuns.model.entity.SubjectContent;
import com.ppfuns.model.entity.VideoBean;
import com.ppfuns.model.entity.VideoDetail;
import com.ppfuns.ui.view.CYTextView;
import com.ppfuns.ui.view.EffectNoDrawBridge;
import com.ppfuns.ui.view.MainUpView;
import com.ppfuns.ui.view.TipsDialog;
import com.ppfuns.ui.view.recycle.ChangeSizeTextView;
import com.ppfuns.util.ActivitiesManager;
import com.ppfuns.util.BroadcastUtils;
import com.ppfuns.util.JsonUtils;
import com.ppfuns.util.LogUtils;
import com.ppfuns.util.StringUtil;
import com.ppfuns.util.SysUtils;
import com.ppfuns.util.ToastUtils;
import com.ppfuns.util.eventbus.EventBusUtil;
import com.ppfuns.util.eventbus.EventConf;
import com.ppfuns.util.eventbus.InfoEvent;
import com.ppfuns.util.https.AlbumRequest;
import com.ppfuns.util.https.ContractUrl;
import com.ppfuns.util.https.HttpUtil;
import com.ppfuns.util.https.RequestCode;
import com.ppfuns.vod.BaseApplication;
import com.ppfuns.vod.R;
import com.ppfuns.vod.receiver.ReceiverActionFlag;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.callback.Callback;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnFocusChange;
import okhttp3.Call;
import okhttp3.Response;

public class AlbumActivity extends BaseActivity implements View.OnKeyListener, OnItemActionListener {
    @BindView(R.id.iv_pic)
    ImageView mPicture;
    @BindView(R.id.tv_type)
    TextView mTvType;
    @BindView(R.id.tv_director)
    TextView mTvDirector;
    @BindView(R.id.tv_actors)
    TextView mTvActors;
    @BindView(R.id.tv_score)
    TextView mTvScore;
    @BindView(R.id.btn_play)
    Button mBtnPlay;
    @BindView(R.id.btn_continue_play)
    Button mBtnContinuePlay;
    @BindView(R.id.btn_episode)
    Button mBtnEpisode;
    @BindView(R.id.btn_collection)
    Button mBtnCollection;
    @BindView(R.id.btn_order)
    Button mBtnOrder;
    @BindView(R.id.tv_tip)
    TextView mTvTip;
    @BindView(R.id.tv_name)
    ChangeSizeTextView mTvName;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_introduction)
    CYTextView mTvIntroduction;
    @BindView(R.id.rl_content)
    RelativeLayout mCotentRelativeLayout;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.tv_play_count)
    TextView mPlayCount;
    @BindView(R.id.tv_like)
    TextView mLike;
    @BindView(R.id.rv_title)
    RecyclerView mTitle;
    @BindView(R.id.rv_content)
    RecyclerView mContentRecyclerView;
    @BindView(R.id.rl_episode)
    RelativeLayout mEpisodeRelativeLayout;
    @BindView(R.id.mainUpView)
    MainUpView mMainUpView;
    @BindView(R.id.rl_main)
    RelativeLayout mMainLayout;

    EffectNoDrawBridge bridget;

    private final String TAG = AlbumActivity.class.getSimpleName();

    // 网络请求的url
    // "http://192.168.1.11:8002/video-operation/api/v/album/10443-1.json";
    private final String ALBUM_URL = ContractUrl.getVodUrl()  + "/v/album";
    // "http://192.168.1.11:8002/video-operation/api/r/Recommend/1-5-1-10443.json"
    private final String RECOMMEND_URL = ContractUrl.getVodUrl()  + "/r/Recommend";
    //    private final String CHECK_AUTHORITY_URL = ContractUrl.DOMAIN_AUTHORITY + ContractUrl.COMMON_QUTHORITY;
    private final String CHECK_HISTORY_URL = ContractUrl.getCommonUserApi() + "/checkResumePonit.json";
    private final String CHECK_COLLECTION_URL = ContractUrl.getCommonUser() + "/check.json";
    private final String SAVE_COLLECTION_URL = ContractUrl.getCommonUser()+ "/put.json";
    private final String DEL_COLLECTION_URL = ContractUrl.getCommonUser() + "/del.json";
    private String requestAlbumUrl = null;
    private String recommendUrl = null;
    private String checkAuthorityUrl = null;

    // 参数定义常量
    public static final String ALBUM_ID = "albumId";
    public static final String COL_CONTENT_ID = "col_contentId";
    public static final String CPID = "cpId";
    public static final String USER_TYPE_VALUE = "userTypeValue";
    public static final String USER_CURRID = "usercurrId";
    public static String IS_AUTHORITY = "isAuthority";
    private final String IS_FROM_ALBUM = "from_album";
    private final String USER_ID = "userId";
    private final String RESOURCE_ID = "resourceId";
    private final String TYPE = "type";
    private final String USER_TYPE = "userType";
    private final String RESOURCE_NAME = "resourceName";
    private final String POSTER_URL = "posterUrl";
    private final String CONTENT_ID = "contentid";
    private final String CONTENT_TYPE = "contenttype";
    private final String PAY_ACTION = "com.ppfuns.pay.action.ACTION_VIEW_PAY";

    //UrlTag 用于标记取消
    private String CHECK_HISTORY_URL_TAG = "checkHistoryUrlTag";//播放
    private String CHECK_AUTHORITY_URL_TAG = "checkAuthorityUrlTag";//鉴权
    private String CHECK_COLLECTION_URL_TAG = "checkCollectionUrlTag";//收藏记录
    private String RECOMMEND_URL_TAG = "recommendUrlTag";//推荐

    private RecommendAdapter mCoRecommendAdapter;
    private EpisodeTitleAdapter mTitleAdapter;
    private EpisodeContentAdapter mContentAdapter;

    // 网络数据存储的bean类
    //    private Album mAlbum;
    private com.ppfuns.model.entity.Response<VideoDetail> mAlbum;
    private VideoDetail mVideoDetail;
    private List<VideoBean> mVideoList;
    //    private Recommend mRecommend;
    private com.ppfuns.model.entity.Response<PageContent<List<RecommendBean>>> mRecommend;
    private PageContent<List<RecommendBean>> mRecommendDetail;
    //    private RecommendDetail mRecommendDetail;
    private List<RecommendBean> mRecommendBeanList =  new ArrayList<>();
    private Response2<DisplayHistory> mPlayHistory;
    private DisplayHistory mHistoryBean;
    //    private CheckCollection mCheckCollect;
    //    private CheckCollection mOperateCollect;
    private Response2<Object> mCheckCollect;
    private Response2<Object> mOperateCollect;
    private Authority mAuthority;
    private ArrayList<PayInfo> mPayInfos;
    private PayInfo mPayInfo;
    private UserSuccedReceiver mUserReceiver;
    private UserFailedReceiver mUserFailedReceiver;

    // handler消息标识
    private final int SHOW_LOADING = 0;
    private final int ALBUM_INFO = 1;
    private final int RECOMMEND_INFO = 2;
    private final int CHECK_COLLECTION_INFO = 3;
    private final int CHECK_HISTORY_INFO = 4;
    private final int OPERATE_COLLECTION_INFO = 5;
    private final int CHECK_AUTHORITY_INFO = 6;
    private final int QUEST_ALBUM_INFO = 7;
    private final int AAA_FOR_USER = 8;
    private final int AAA_FOR_USER_TIMEOUT = 9;

    public static final int AUTHORITY_FLAG = 2003; // 单片付费鉴权
    private int albumId = 0;
    private int col_contentId = 0;
    private int userType = 1;
    private int titlePosition = 0;
    private int mAlbumFailedCount = 0;
    private int mUserId = -1;
    private long orTimePosition = 0;
    private String mCa;
    private boolean isScroll = false;
    private boolean isCollect = false;
    private boolean isFromAlbum = false;
    private boolean isLoadingRecommend = false;
    private boolean isAuthoritySucess = false;
    private boolean isAuthoriting = false;
    private boolean hasChange = false;  // 收藏及播放历史是否有变化
    public static final int PLAY_REQUEST_CODE = 100;
    private final int PAY_REQUEST_KEY = 11111;
    private final int PAY_RESULT_KEY = 22222;
    private final int BACK_FOR_RESULT = 3;

    private int mRecommendPageNo = 1;
    private int mRecommendPageSize = 28;
    private boolean mIsCanLoadRecommend = false;

    private final int ALBUM_TYPE_MOVIE = 0;
    private final int ALBUM_TYPE_EPISODES = 1;

    private String mRequestPlayTag = "";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_LOADING:
                    showLoadingProgressDialog(getString(R.string.data_loading));
                    break;
                case ALBUM_INFO:
                    updateAlbumInfo();
                    break;
                case RECOMMEND_INFO:
                    updateRecommendInfo();
                    break;
                case CHECK_COLLECTION_INFO:
                    updateCollectionInfo(false);
                    break;
                case CHECK_HISTORY_INFO:
                    updateHistoryInfo();
                    break;
                case OPERATE_COLLECTION_INFO:
                    updateCollectionInfo(true);
                    break;
                case QUEST_ALBUM_INFO:
                    requestAlbumInfo(requestAlbumUrl);
                    break;
                case AAA_FOR_USER:
                case AAA_FOR_USER_TIMEOUT:
                    checkUserId();
                    break;
            }
            super.handleMessage(msg);

        }
    };
    private String mStrDesc;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        getIDFromIntent(true);
        loadData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        EventBusUtil.register(this);
        ButterKnife.bind(this);
        getIDFromIntent(false);
        ActivitiesManager.getManager().addActivity(this);
        initKeyListener();
        initMainUpView();
        initRecommendAdapter();
        mBtnContinuePlay.setVisibility(View.GONE);
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
//        if (isFromAlbum) {
//            isFromAlbum = false;
//            loadData();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.d(TAG, "onStop");
        cancelTag();
        setResult(BACK_FOR_RESULT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
        onRelease();
    }


    @Override
    public void reLoad() {
        super.reLoad();
        LogUtils.i(TAG, "reload");
        loadData();
    }

    @Override
    protected void cancel() {
        super.cancel();
        finish();
    }

    private void loadData() {
        if (!SysUtils.isNetworkAvailable(this)) {
            onError(getResources().getString(R.string.network_error));
            return;
        }
        try {
            if (mUserId == -1) {
                mUserId = SysUtils.getUserId();
            }
            mCa = SysUtils.getCa();
        } catch (ClassCastException e) {
            LogUtils.e(TAG, e.toString());
        }
        handler.sendEmptyMessageDelayed(SHOW_LOADING, 0);
        if (mUserId == -1) {
//            onError(getResources().getString(R.string.userid_error));
            ToastUtils.showLong(this, getString(R.string.request_userid));
            registUserReceiver();
            BroadcastUtils.sendAAAForUser(this);
            handler.sendEmptyMessageDelayed(AAA_FOR_USER_TIMEOUT, 5000);
            return;
        }
//        handler.sendEmptyMessageDelayed(SHOW_LOADING, 1500);
        requestAlbumInfo(requestAlbumUrl);
        //        requestHistory(CHECK_HISTORY_URL);
        //        requestCheckAuthority(checkAuthorityUrl);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mHistoryBean != null) {
                long timePosition = mHistoryBean.timePosition;
                if((orTimePosition == 0 && timePosition != 0) || (orTimePosition != 0 && timePosition == 0)) {
                    hasChange = true;
                }
            }
            LogUtils.i(TAG, "hasChage: " + hasChange);
            if (hasChange) {
                setResult(BACK_FOR_RESULT);
            }
        }
        return super.dispatchKeyEvent(event);
    }

    protected void initMainUpView() {
        mMainUpView.setEffectBridge(new EffectNoDrawBridge());
        bridget = (EffectNoDrawBridge) mMainUpView.getEffectBridge();
        bridget.setFromAlbum(true);
        bridget.setVisibleWidget(true);
        bridget.setTranDurAnimTime(0);
        //        // 设置移动边框的图片.
        mMainUpView.setUpRectResource(R.drawable.album_btn_focus);
        //        // 移动方框缩小的距离.
        //        mMainUpView.setDrawUpRectPadding(new Rect(0, 0, 0, 0));

    }

    protected void initKeyListener() {

        mBtnPlay.setOnKeyListener(this);
        mBtnEpisode.setOnKeyListener(this);
        mBtnCollection.setOnKeyListener(this);
        mBtnContinuePlay.setOnKeyListener(this);
        mBtnOrder.setOnKeyListener(this);
        mTvIntroduction.setOnKeyListener(this);
    }

    protected void getIDFromIntent(boolean isFromNewIntent) {
        Intent iIntent = getIntent();
        if (iIntent != null) {
            albumId = iIntent.getIntExtra(ALBUM_ID, -1);
//                        albumId = 30411;
            col_contentId = vodColumnId;
            LogUtils.d(TAG, "_contentId : " + vodColumnId);
            userType = iIntent.getIntExtra(USER_TYPE_VALUE, 1);
            mUserId = iIntent.getIntExtra(USER_CURRID, -1);
            if (!isFromNewIntent) {
                isFromAlbum = true;
            }
//            if (BaseApplication.getmColumnId() != -1) {
//                col_contentId = BaseApplication.getmColumnId();
//            }
            requestAlbumUrl = ALBUM_URL + "/" + albumId + "-" + col_contentId + "-0.json?areaCode=" + SysUtils.getAreaCode();
            LogUtils.d(TAG, "getdata albumId: " + albumId + ", cpId: " + col_contentId);
//            recommendUrl = RECOMMEND_URL + "/" + mRecommendPageNo + "-100-" + col_contentId + "-" + albumId + "-0.json";
//            ContractUrl.CHECK_AUTHORITY_URL = "http://192.168.1.11:8013/api/aaa/content/check.json";
            checkAuthorityUrl = ContractUrl.getCheckAuthorityUrl() + "?contentid=" + albumId + "&contenttype=" + AUTHORITY_FLAG;
        }
    }

    @OnFocusChange({R.id.btn_continue_play, R.id.btn_play, R.id.btn_episode, R.id.btn_collection, R.id.rv_title, R.id.rv_content,
            R.id.recyclerview, R.id.tv_introduction, R.id.btn_order})
    public void OnFoucusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.btn_play:
                if (hasFocus) {
                    mTvTip.setText(getString(R.string.ablum_txt_recommend));
                    mEpisodeRelativeLayout.setVisibility(View.INVISIBLE);
                    mRecyclerView.setVisibility(View.VISIBLE);

                    mMainUpView.setUpRectResource(R.drawable.album_btn_focus);
                    mMainUpView.setDrawUpRectPadding(new Rect(0, 0, 0, 0));
                    mMainUpView.setFocusView(v, 1.0f);
                }
                break;
            case R.id.btn_episode:
                if (hasFocus) {
                    mTvTip.setText(getString(R.string.ablum_txt_episode));
                    mEpisodeRelativeLayout.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.INVISIBLE);

                    mMainUpView.setUpRectResource(R.drawable.album_btn_focus);
                    mMainUpView.setDrawUpRectPadding(new Rect(0, 0, 0, 0));
                    mMainUpView.setFocusView(v, 1.0f);
                }
                break;
            case R.id.btn_order:
            case R.id.btn_collection:

                if (hasFocus) {
                    //                    mEpisodeRelativeLayout.setVisibility(View.VISIBLE);
                    //                    mRecyclerView.setVisibility(View.INVISIBLE);
                    mMainUpView.setUpRectResource(R.drawable.album_btn_focus);
                    mMainUpView.setDrawUpRectPadding(new Rect(0, 0, 0, 0));
                    mMainUpView.setFocusView(v, 1.0f);
                }
                break;
            case R.id.btn_continue_play:
                if (hasFocus) {
                    mEpisodeRelativeLayout.setVisibility(View.INVISIBLE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mMainUpView.setUpRectResource(R.drawable.album_btn_focus);
                    mMainUpView.setDrawUpRectPadding(new Rect(0, 0, 0, 0));
                    mMainUpView.setFocusView(v, 1.0f);
                }
                break;
            case R.id.rv_title:
                if (hasFocus) {
                    if (mTitle.getChildAt(titlePosition) != null) {
                        mTitle.getChildAt(titlePosition).requestFocus();
                    }
                }
                break;
            case R.id.rv_content:
            case R.id.recyclerview:
                if (hasFocus) {
                    RecyclerView recyclerView = (RecyclerView) v;
                    View view = recyclerView.getChildAt(0);
                    if (view != null) {
                        int fItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                        int cItemPosition = recyclerView.getChildLayoutPosition(view);
                        LogUtils.i("Decode", "fItemPosition: " + fItemPosition + ",cItemPosition: " + cItemPosition);
                        int index = fItemPosition - cItemPosition;
                        if (index < 0) {
                            index = 0;
                        }
                        View currentView = recyclerView.getChildAt(index);
                        if (currentView != null) {
                            currentView.requestFocus();
                        }
                    }
                }
                break;
            case R.id.tv_introduction:
                if (hasFocus) {
                    int pad = getResources().getDimensionPixelSize(R.dimen.introduction_focus_padding);
                    mMainUpView.setUpRectResource(R.drawable.album_btn_focus);
                    mMainUpView.setDrawUpRectPadding(new Rect(pad, pad, pad, pad));
                    mMainUpView.setFocusView(v, 1.0f);
                }
                break;
        }
    }

    private void showMainView() {
        handler.removeMessages(SHOW_LOADING);
        dissmissLoadingDialog();
        mMainLayout.setVisibility(View.VISIBLE);
        if (mBtnContinuePlay.getVisibility() == View.VISIBLE) {

            mBtnContinuePlay.requestFocus();
            mBtnContinuePlay.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mMainUpView.setFocusView(mBtnContinuePlay, 1.0f);
                    bridget.setTranDurAnimTime(200);
                    bridget.setVisibleWidget(false);
                    mBtnContinuePlay.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        } else {
            mBtnPlay.requestFocus();
            mBtnPlay.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mMainUpView.setFocusView(mBtnPlay, 1.0f);
                    bridget.setTranDurAnimTime(200);
                    bridget.setVisibleWidget(false);
                    mBtnPlay.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        }
        //        LogUtils.i(TAG, "request Focus =" + mBtnPlay.requestFocus());
        mMainUpView.setVisibility(View.GONE);
    }

    protected void updateAlbumInfo() {
        LogUtils.i(TAG, "updateAlbumInfo");
        if (mVideoDetail == null) {
            onError(getString(R.string.album_content_empty));
            return;
        }
        requestCollection(CHECK_COLLECTION_URL);
        String strName = mVideoDetail.getAlbumName();
        int iTime = mVideoDetail.getYear();
        String strPlayCount = mVideoDetail.getPlayCounts();
        String strLike = mVideoDetail.getSupportCounts();
        String strMainActors = mVideoDetail.getMainActors();
        double score = mVideoDetail.getScore();
        mStrDesc = mVideoDetail.getAlbumDesc();
        String strLabels = mVideoDetail.getLabels();
        String strDirector = mVideoDetail.getDirectors();
        mTvScore.setText(String.valueOf(score));
        if (!TextUtils.isEmpty(strName)) {
            mTvName.setText(strName, "(" + iTime + ")");
        }

        if (!TextUtils.isEmpty(strPlayCount)) {
            mPlayCount.setText(strPlayCount);
            //            mPlayCount.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(strLike)) {
            mLike.setText(strLike);
            //            mLike.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(strMainActors)) {
            strMainActors = StringUtil.replaceVertical(strMainActors);
            if (strMainActors.length() >= 37) {
                strMainActors = strMainActors.substring(0, 34) + "...";
            }
            mTvActors.setText(getString(R.string.ablum_txt_actors) + getString(R.string.txt_double_space) + strMainActors);
        }
        if (!TextUtils.isEmpty(mStrDesc)) {
            mTvIntroduction.setText(getString(R.string.ablum_txt_introduction) + getString(R.string.txt_double_space) + mStrDesc);
        } else {
            mTvIntroduction.setText(getString(R.string.ablum_txt_introduction) + getString(R.string.txt_double_space) + "暂无内容");
        }
        if (!TextUtils.isEmpty(strLabels)) {
            strLabels = StringUtil.replaceVertical(strLabels);
            mTvType.setText(getString(R.string.ablum_txt_type) + getString(R.string.txt_double_space) + strLabels);
        }
        if (!TextUtils.isEmpty(strDirector)) {
            strDirector = StringUtil.replaceVertical(strDirector);
            mTvDirector.setText(getString(R.string.ablum_txt_director) + getString(R.string.txt_double_space) + strDirector);
        }

        String imageUrl = mVideoDetail.getPicUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            ImageLoader.getInstance().displayImage(imageUrl, mPicture, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    super.onLoadingFailed(imageUri, view, failReason);
                    ImageLoader.getInstance().cancelDisplayTask((ImageView) view);
                }
            });
        }
        int albumType = mVideoDetail.getAlbumType();
        if (albumType == ALBUM_TYPE_MOVIE) {
            mBtnEpisode.setVisibility(View.GONE);
        } else {
            initEpisodeAdapter();
        }
//        isAuthoritySucess = true;
        if (!isAuthoritySucess) {
            mBtnPlay.setText(getString(R.string.ablum_btn_free));
            mBtnContinuePlay.setVisibility(View.GONE);
            if(mPayInfos != null && mPayInfos.size() > 0) {
                int size = mPayInfos.size();
                boolean isPayColumn = false;
                for (int i = 0; i < size; i++) {
                    PayInfo payInfo = mPayInfos.get(i);
                    if (payInfo != null) {
                        String chargingType = payInfo.chargingType;
                        if (!TextUtils.isEmpty(chargingType) && 2001 == Integer.valueOf(chargingType)) {
                            isPayColumn = true;
                            break;
                        }
                    }
                }
                if (isPayColumn) {
                    mBtnOrder.setText(R.string.album_btn_pay_column);
                }
            }
            mBtnOrder.setVisibility(View.VISIBLE);
        }
        String recommendUrl = getRecommendUrl(mRecommendPageNo);
        requestRecommendInfo(recommendUrl);
        updateHistoryInfo();
        showMainView();
    }


    protected void updateRecommendInfo() {
        LogUtils.i(TAG, "updateRecommendInfo");
        if (mRecommendBeanList == null || mRecommendBeanList.size() <= 0) {
            LogUtils.e(TAG, "recommendBeanList null");
            return;
        }
        initRecommendAdapter();
    }

    protected void updateCollectionInfo(boolean isOperateCollect) {
        LogUtils.i(TAG, "updateCollectionInfo");
        if (!isOperateCollect && mCheckCollect == null) {
            ToastUtils.showShort(AlbumActivity.this, getResources().getString(R.string.operate_error));
            return;
        }
        if (isOperateCollect && mOperateCollect == null) {
            ToastUtils.showShort(AlbumActivity.this, getResources().getString(R.string.operate_error));
            return;
        }
        if (!isOperateCollect) {
            if (TextUtils.equals(RequestCode.RESPONSE_SUCCESS, mCheckCollect.code)) {
                mBtnCollection.setText(getString(R.string.ablum_btn_cancel_collection));
                isCollect = true;
            }
        } else {
            if (isCollect) {
                if (TextUtils.equals(RequestCode.RESPONSE_SUCCESS, mOperateCollect.code)) {
                    mBtnCollection.setText(getString(R.string.ablum_btn_collection));
                    isCollect = false;
                    ToastUtils.showShort(this, getString(R.string.ablum_btn_cancel_collection_success));
                } else {
                    ToastUtils.showShort(this, getString(R.string.ablum_btn_cancel_collection_failed) + " " + mOperateCollect.message);
                }
            } else {
                if (TextUtils.equals(RequestCode.RESPONSE_SUCCESS, mOperateCollect.code)) {
                    mBtnCollection.setText(getString(R.string.ablum_btn_cancel_collection));
                    isCollect = true;
                    ToastUtils.showShort(this, getString(R.string.ablum_btn_collection_success));
                } else {
                    ToastUtils.showShort(this, getString(R.string.ablum_btn_collection_failed) + " " + mOperateCollect.message);
                }
            }
        }

    }

    protected void updateHistoryInfo() {
        LogUtils.i(TAG, "updateHistoryInfo");
        if (mPlayHistory == null) {
            return;
        }

        if (TextUtils.equals(RequestCode.RESPONSE_SUCCESS, mPlayHistory.code)) {
            mHistoryBean = mPlayHistory.result;
            if (mHistoryBean != null && mHistoryBean.timePosition > 0) {
                orTimePosition = mHistoryBean.timePosition;
                mBtnContinuePlay.setVisibility(View.VISIBLE);
                mBtnContinuePlay.requestFocus();

                //                mBtnPlay.setText(getString(R.string.ablum_btn_continue_play));
            } else {
                mBtnContinuePlay.setVisibility(View.GONE);
            }
        }
        //        showMainView();
    }

    protected void initRecommendAdapter() {
        mCoRecommendAdapter = new RecommendAdapter(this, mRecommendBeanList);
        mCoRecommendAdapter.setOnItemActionListener(this);
        MyLinearLayoutManager linearLayoutManager = new MyLinearLayoutManager(this);
        int scrollValue = getResources().getDimensionPixelSize(R.dimen.album_recommend_iv_width);
        linearLayoutManager.setScrollValue(scrollValue);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            // 滑动计数，判断是否滑动完成
            private int count = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                count++;
//                ImageLoader.getInstance().pause();
                Glide.with(AlbumActivity.this).onStop();
                if (!isScroll) {
                    isScroll = true;
                }
                if (count == RecyclerView.SCROLL_STATE_SETTLING) {
//                    ImageLoader.getInstance().resume();
                    Glide.with(AlbumActivity.this).onStart();
//                    View view = recyclerView.getFocusedChild();
//                    int pad = getResources().getDimensionPixelSize(R.dimen.item_mianupview_pleft);
//                    int padRight = getResources().getDimensionPixelSize(R.dimen.item_mianupview_pright);
//                    mMainUpView.setUpRectResource(R.drawable.album_item_focus);
//                    mMainUpView.setDrawUpRectPadding(new Rect(pad, pad, padRight, pad));
//                    mMainUpView.setFocusView(view, 1.0f);
                    isScroll = false;
                    count = 0;
                }
                LogUtils.i("Decode", "onScrollStateChanged_down_down_oldPos_isScroll: " + isScroll + ", count: " + count);
            }
        });
        mRecyclerView.setAdapter(mCoRecommendAdapter);
    }

    protected void initEpisodeAdapter() {
        if (mVideoDetail.getVideoList() == null) {
            return;
        }
        mVideoList = mVideoDetail.getVideoList();

        mTitleAdapter = new EpisodeTitleAdapter(this, mVideoList.size());
        mTitleAdapter.setOnItemActionListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mTitle.setLayoutManager(linearLayoutManager);
        mTitle.setAdapter(mTitleAdapter);

        mContentAdapter = new EpisodeContentAdapter(this, mVideoList);
        mContentAdapter.setOnItemActionListener(this);
        MyLinearLayoutManager contentManager = new MyLinearLayoutManager(this);
        int scrollValue = getResources().getDimensionPixelSize(R.dimen.item_tv_episode_width);
        contentManager.setScrollValue(scrollValue);
        contentManager.setType(1);
        contentManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mContentRecyclerView.setLayoutManager(contentManager);
        mContentRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            // 滑动计数，判断是否滑动完成
            private int count = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LogUtils.i(TAG, "onScrollStateChanged");
                count++;
                isScroll = true;
                if (count == RecyclerView.SCROLL_STATE_SETTLING) {
//                    View view = recyclerView.getFocusedChild();
//                    int contentPad = getResources().getDimensionPixelSize(R.dimen.item_eposide_padding);
//                    mMainUpView.setDrawUpRectPadding(new Rect(contentPad, contentPad, contentPad, contentPad));
//                    mMainUpView.setUpRectResource(R.drawable.album_item_focus);
//                    mMainUpView.setFocusView(view, 1.0f);
                    isScroll = false;
                    count = 0;
                }
            }
        });
        mContentRecyclerView.setAdapter(mContentAdapter);
    }

    protected void parseAlbum() {
        LogUtils.i(TAG, "parseAlbum");
        LogUtils.i(TAG, "parseAlbum =" + mAlbum);
        if (mAlbum == null) {
            onError(getString(R.string.album_content_empty));
            return;
        }
        Data dataBean = mAlbum.data;
        if (TextUtils.equals(RequestCode.RESPONSE_SUCCESS, dataBean.code)) {
            mVideoDetail = mAlbum.data.result;
            if (mVideoDetail != null) {
                mVideoList = mVideoDetail.getVideoList();
                requestCheckAuthority(checkAuthorityUrl);
            } else {
                onError(getString(R.string.film_shelf));
            }
        } else if (TextUtils.equals(RequestCode.CONTENT_EMPTY, dataBean.code)) {
            onError(getString(R.string.film_shelf));
        } else {
            onError(getString(R.string.data_error));
        }
    }

    protected void parseRecommend() {
        LogUtils.i(TAG, "parseRecommend");
        if (mRecommend == null) {
            return;
        }
        Data dataBean = mRecommend.data;
        if (TextUtils.equals(RequestCode.RESPONSE_SUCCESS, dataBean.code)) {
            mRecommendDetail = mRecommend.data.result;
            if (mRecommendDetail != null) {
                List<RecommendBean> beanList = mRecommendDetail.getPageContent();
                if (beanList == null || beanList.size() <= 0) {
                    return;
                }
                if (beanList.size() >= mRecommendPageSize) {
                    mIsCanLoadRecommend = true;
                }
                int oldSize = mRecommendBeanList.size();
                mRecommendBeanList.addAll(beanList);
                mCoRecommendAdapter.notifyItemRangeInserted(oldSize, beanList.size());
//                if (mRecommendBeanList != null && mRecommendBeanList.size() > 0) {
//                    updateRecommend(beanList, mRecommendBeanList.size());
//                    return;
//                }
//                mRecommendBeanList = beanList;
//                handler.sendEmptyMessage(RECOMMEND_INFO);
            }
        }
    }

    private void updateRecommend(List<RecommendBean> beanList, int startPos) {
        if (mCoRecommendAdapter == null || beanList == null || beanList.size() <= 0) {
            return;
        }
        mCoRecommendAdapter.updateData(beanList, startPos);
    }

    protected void requestAlbumInfo(String url) {
        LogUtils.i(TAG, "requestAlbumInfo");
        HttpUtil.getHttpHtml(url, requestAlbumUrl, new Callback<com.ppfuns.model.entity.Response<VideoDetail>>() {

            @Override
            public boolean validateReponse(Response response, int id) {
                LogUtils.i(TAG, "album_response_code: " + response.code());
                return response.isSuccessful();
            }

            @Override
            public com.ppfuns.model.entity.Response<VideoDetail> parseNetworkResponse(Response response, int id) throws Exception {
                String result = response.body().string();
//                result = "{\"data\":{\"code\":\"N000004\",\"codeV\":null,\"count\":null,\"longTime\":1487662306578,\"message\":\"\",\"msg\":\"已下架\",\"result\":null,\"resultex\":null,\"stringTime\":\"2017-02-21 15:31:46.578\"}}";
                com.ppfuns.model.entity.Response<VideoDetail> resAlbum = (com.ppfuns.model.entity.Response<VideoDetail>) JsonUtils.fromJson(result, new TypeToken<com.ppfuns.model.entity.Response<VideoDetail>>() {
                }.getType());
                LogUtils.d(TAG, "album_result: " + result);
                LogUtils.d(TAG, "album_resAlbum: " + resAlbum);
                return resAlbum;
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(TAG, "HttpGet album failed " + e.getMessage());
                if (mAlbumFailedCount < 3) {
                    mAlbumFailedCount++;
                    if (handler != null) {
                        handler.sendEmptyMessageDelayed(QUEST_ALBUM_INFO, 500);
                    }
                    return;
                }
                AlbumActivity.this.onError(getString(R.string.data_error));
            }

            @Override
            public void onResponse(com.ppfuns.model.entity.Response<VideoDetail> resAlbum, int id) {
                mAlbum = resAlbum;
                parseAlbum();
            }
        });
    }

    private void requestCheckAuthority(String url) {
        LogUtils.i(TAG, "requestCheckAuthority");
        if (mUserId == -1) {
            onError(getString(R.string.userid_error));
            return;
        }
        url = url + "&userid=" + mUserId + "&ca=" + mCa;
        //        url = "http://192.168.1.11:8013/api/aaa/content/check.json?contentid=171&contenttype=1&userid=3010285&ca=" + ca;
        isAuthoriting = true;
        CHECK_AUTHORITY_URL_TAG = CHECK_AUTHORITY_URL_TAG + SystemClock.currentThreadTimeMillis();

        HttpUtil.getHttpHtml(url, CHECK_AUTHORITY_URL_TAG, new Callback<Authority>() {

            @Override
            public boolean validateReponse(Response response, int id) {
                LogUtils.e(TAG, "authority_response_code: " + response.code());
                return response.isSuccessful();
            }

            @Override
            public Authority parseNetworkResponse(Response response, int id) throws Exception {
                String result = response.body().string();
                Authority authority = new Gson().fromJson(result, Authority.class);
                LogUtils.d(TAG, "authority_result: " + result);
                return authority;
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (!TextUtils.equals(e.getMessage(), "Canceled")) {
                    if (mAlbumFailedCount < 3) {
                        mAlbumFailedCount++;
                        handler.sendEmptyMessageDelayed(QUEST_ALBUM_INFO, 500);
                        return;
                    }
                }
                isAuthoriting = false;
                handler.sendEmptyMessage(ALBUM_INFO);
                //                AlbumActivity.this.onError(getString(R.string.data_error));
                LogUtils.e(TAG, "HttpGet authority failed " + e.getMessage());
            }

            @Override
            public void onResponse(Authority authority, int id) {
                isAuthoriting = false;
                mAuthority = authority;
                if (mAuthority != null) {
                    if (TextUtils.equals(PayState.PAY_FREE, mAuthority.code) || TextUtils.equals(PayState.PAY_THROUGH, mAuthority.code)) {
                        isAuthoritySucess = true;
                        requestHistory(CHECK_HISTORY_URL);
                        return;
                    } else {
                        if (mAuthority.result != null) {
                            mPayInfos = mAuthority.result;
                            if (mPayInfos != null && mPayInfos.size() > 0) {
                                mPayInfos.get(0).videoName = mVideoDetail.getAlbumName();
//                                mPayInfo = mPayInfos.get(0);
//                                mPayInfo.videoName = mVideoDetail.getAlbumName();
                            }
                        }
                    }
                }
                handler.sendEmptyMessage(ALBUM_INFO);
            }
        });
    }

    protected void requestRecommendInfo(String url) {
        LogUtils.i(TAG, "requestRecommendInfo");
        if (isLoadingRecommend) {
            return;
        }
        isLoadingRecommend = true;
        RECOMMEND_URL_TAG = RECOMMEND_URL_TAG + SystemClock.currentThreadTimeMillis();

        HttpUtil.getHttpHtml(url, RECOMMEND_URL_TAG, new Callback<com.ppfuns.model.entity.Response<PageContent<List<RecommendBean>>>>() {

            @Override
            public boolean validateReponse(Response response, int id) {
                LogUtils.e(TAG, "recommend_response_code: " + response.code());
                return response.isSuccessful();
            }

            @Override
            public com.ppfuns.model.entity.Response<PageContent<List<RecommendBean>>> parseNetworkResponse(Response response, int id) throws Exception {
                String result = response.body().string();
                com.ppfuns.model.entity.Response<PageContent<List<RecommendBean>>> recommend = new Gson().fromJson(result, new TypeToken<com.ppfuns.model.entity.Response<PageContent<List<RecommendBean>>>>() {
                }.getType());
                LogUtils.d(TAG, "recommend_result: " + result);
                return recommend;
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(TAG, "HttpGet recommend failed " + e.getMessage());
                if (isLoadingRecommend) {
                    isLoadingRecommend = false;
                }
				mIsCanLoadRecommend = false;
            }

            @Override
            public void onResponse(com.ppfuns.model.entity.Response<PageContent<List<RecommendBean>>> recommend, int id) {
                if (isLoadingRecommend) {
                    isLoadingRecommend = false;
                }
                mIsCanLoadRecommend = false;
                mRecommend = recommend;
                parseRecommend();
            }
        });
    }

    protected void requestCollection(String url) {
        LogUtils.i(TAG, "requestCollection");
        if (mUserId == -1) {
            ToastUtils.showShort(this, getString(R.string.userid_error));
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(USER_ID, String.valueOf(mUserId));
        map.put(RESOURCE_ID, String.valueOf(mVideoDetail.getAlbumId()));
        map.put(TYPE, String.valueOf(mVideoDetail.getAlbumType()));
        //TODO 用户设备类型
        map.put(USER_TYPE, userType + "");
        map.put(CPID, String.valueOf(col_contentId));
        LogUtils.i(TAG + "requestCollection map: " + map.toString());
        CHECK_COLLECTION_URL_TAG = CHECK_COLLECTION_URL_TAG + SystemClock.currentThreadTimeMillis();

        HttpUtil.post(url, map, CHECK_COLLECTION_URL_TAG, new Callback<Response2<Object>>() {

            @Override
            public boolean validateReponse(Response response, int id) {
                LogUtils.e(TAG, "check_collect_response_code: " + response.code());
                return response.isSuccessful();
            }

            @Override
            public Response2<Object> parseNetworkResponse(Response response, int id) throws Exception {
                String result = response.body().string();
                Response2<Object> collection = new Gson().fromJson(result, new TypeToken<Response2<Object>>() {
                }.getType());
                LogUtils.d(TAG, "check_collect_result: " + result);
                return collection;
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(TAG, "HttpGet check_collect failed " + e.getMessage());
            }

            @Override
            public void onResponse(Response2<Object> collection, int id) {
                mCheckCollect = collection;
                handler.sendEmptyMessage(CHECK_COLLECTION_INFO);
            }
        });
    }


    protected void requestHistory(String url) {
        LogUtils.i(TAG, "requestHistory");
        if (mUserId == -1) {
            onError(getString(R.string.userid_error));
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(USER_ID, String.valueOf(mUserId));
        map.put(RESOURCE_ID, String.valueOf(albumId));
        //TODO 用户设备类型
        map.put(USER_TYPE, "1");
        map.put(CPID, String.valueOf(col_contentId));
        LogUtils.i(TAG, "history: " + map.toString());
        CHECK_HISTORY_URL_TAG = CHECK_HISTORY_URL_TAG + SystemClock.currentThreadTimeMillis();

        HttpUtil.post(url, map, CHECK_HISTORY_URL_TAG, new Callback<Response2<DisplayHistory>>() {

            @Override
            public boolean validateReponse(Response response, int id) {
                LogUtils.e(TAG, "history_response_code: " + response.code());
                return response.isSuccessful();
            }

            @Override
            public Response2<DisplayHistory> parseNetworkResponse(Response response, int id) throws Exception {
                String result = response.body().string();
                Response2<DisplayHistory> history = new Gson().fromJson(result, new TypeToken<Response2<DisplayHistory>>() {
                }.getType());
                LogUtils.d(TAG, "history_result: " + result);
                return history;
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(TAG, "HttpGet history failed " + e.getMessage());
                if (handler != null) {
                    handler.sendEmptyMessage(ALBUM_INFO);
                }
            }

            @Override
            public void onResponse(Response2<DisplayHistory> history, int id) {
                mPlayHistory = history;
                handler.sendEmptyMessage(ALBUM_INFO);
                //                handler.sendEmptyMessage(CHECK_HISTORY_INFO);
            }
        });
    }

    protected void onError(String message) {
        if (TextUtils.isEmpty(message) || message.length() == 0) {
            message = getResources().getString(R.string.data_error);
        }
        handler.removeMessages(SHOW_LOADING);
        showDialogTips(message);
        dissmissLoadingDialog();
    }

    private void startPay() {
        LogUtils.i(TAG, "startPay");
        if (mPayInfos == null || mPayInfos.size() <= 0) {
            ToastUtils.showShort(this, getString(R.string.authority_data_error));
            return;
        }
//        int size = mPayInfos.size();
//        boolean isPayColumn = false;
//        for (int i = 0; i < size; i++) {
//            PayInfo payInfo = mPayInfos.get(i);
//            if (payInfo != null) {
//                String chargingType = payInfo.chargingType;
//                if (!TextUtils.isEmpty(chargingType) && 2001 == Integer.valueOf(chargingType)) {
//                    isPayColumn = true;
//                    break;
//                }
//            }
//        }
//
//        Bundle bundle = new Bundle();
//        if (isPayColumn) {
//            //专区付费
//            String payInfosString = new Gson().toJson(mPayInfos).toString();
//            bundle.putString("pay_info", payInfosString);
//            Intent intent = new Intent();
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setAction("com.ppfuns.pay.action.PAY_COLUMN");
//            intent.putExtras(bundle);
//            startActivityForResult(intent, 111);
//        } else {
//            PayInfo payInfo = mPayInfos.get(0);
//            bundle.putSerializable(PayInfoActivity.PAY_INFO_KEY, payInfo);
//            Intent intent = new Intent(this, PayInfoActivity.class);
//            intent.putExtras(bundle);
//            startActivity(intent);
//        }
//        if (mPayInfo == null || TextUtils.isEmpty(mPayInfo.partnerId)) {
//            ToastUtils.showShort(this, getString(R.string.authority_data_error));
//            return;
//        }
        LogUtils.i(TAG, "intent_pay");
        Bundle bundle = new Bundle();
        bundle.putSerializable(PayInfoActivity.PAY_INFO_KEY, mPayInfos);
        Intent intent = new Intent(this, PayInfoActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void startPlay(VideoBean videoBean) {
        startPlay(videoBean, false);
    }

    protected void startPlay(VideoBean videoBean, boolean isContinue) {
        if (videoBean == null || mVideoDetail == null) {
            return;
        }
        String playUrl = videoBean.getM3u8Url();
        String duration = videoBean.getDuration();
        int videoType = mVideoDetail.getContentType();
        int playIndex = videoBean.getSeq();
        if (playIndex <= 0) {
            playIndex = 1;
        }
        int type = mVideoDetail.getAlbumType();
        AlbumRequest.getInstance().setAlbumId(albumId, isAuthoritySucess, mAuthority);
        AlbumRequest.getInstance().updateAlbumInfo(mVideoDetail);
        long timePosition = 0;
        if (isContinue) {
            if (mHistoryBean != null && mHistoryBean.timePosition != 0) {
                timePosition = mHistoryBean.timePosition;
            }
        }
        if (!TextUtils.isEmpty(playUrl)) {
            mRequestPlayTag = "" + System.currentTimeMillis() + albumId + BaseApplication.getmColumnId();
            Intent intent = new Intent(AlbumActivity.this, PlayerActivity.class);
            intent.putExtra(PlayerActivity.ALBUM_ID, String.valueOf(videoBean.getAlbumId()));
            intent.putExtra(PlayerActivity.COL_CONTENTID, String.valueOf(col_contentId));
            intent.putExtra(PlayerActivity.POSITION, String.valueOf(timePosition));
            intent.putExtra(PlayerActivity.TYPE, String.valueOf(type));
            intent.putExtra(PlayerActivity.DURATION, duration);
            intent.putExtra(PlayerActivity.INDEX, String.valueOf(playIndex));
            intent.putExtra(PlayerActivity.REQUEST_PLAY_TAG, mRequestPlayTag);
            startActivityForResult(intent, PLAY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d("Decode", "requestCode: " + requestCode + ", resultCode: " + resultCode);
        //        if (PLAY_REQUEST_CODE == requestCode && resultCode == PlayerActivity.ALBUM_RESULT_CODE && data != null) {
        //            long timePosition = data.getLongExtra(PlayerActivity.POSITION, -1);
        //            int index = data.getIntExtra(PlayerActivity.INDEX, 1);
        //            isAuthoritySucess = data.getBooleanExtra(IS_AUTHORITY, false);
        //            if (isAuthoritySucess) {
        //                mBtnPlay.setText(getString(R.string.ablum_btn_play));
        //                mBtnPlay.requestFocus();
        //                mBtnOrder.setVisibility(View.GONE);
        //            } else {
        //                mBtnContinuePlay.setVisibility(View.GONE);
        //            }
        //            LogUtils.i(TAG, "album_position: " + timePosition);
        //            if (timePosition != -1) {
        //                if (mHistoryBean == null) {
        //                    mHistoryBean = new DisplayHistory();
        //                }
        //                mHistoryBean.timePosition = timePosition;
        //                mHistoryBean.cpVideoId = index;
        //                mBtnContinuePlay.setVisibility(View.VISIBLE);
        //                mBtnContinuePlay.requestFocus();
        //
        //                mBtnContinuePlay.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        //                    @Override
        //                    public void onGlobalLayout() {
        //                        mMainUpView.setFocusView(mBtnContinuePlay, 1.0f);
        //                        bridget.setTranDurAnimTime(200);
        //                        bridget.setVisibleWidget(false);
        //                        mBtnContinuePlay.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        //                    }
        //                });
        //                //                mBtnPlay.setText(getString(R.string.ablum_btn_continue_play));
        //            }
        //        } else

//        if (PAY_REQUEST_KEY == requestCode && PAY_RESULT_KEY == resultCode && data != null) {
//            String payResult = data.getExtras().getString("result");
//            LogUtils.d("Decode", "payResult: " + payResult);
//            if (!TextUtils.isEmpty(payResult)) {
//                PayAlbum payAlbum = new Gson().fromJson(payResult, new TypeToken<PayAlbum>() {
//                }.getType());
//                if (payAlbum != null) {
//                    String payCode = payAlbum.resultCode;
//                    if (TextUtils.equals(payCode, "0000")) {
//                        mBtnPlay.setText(getString(R.string.ablum_btn_play));
//                        mBtnPlay.requestFocus();
//                        mBtnOrder.setVisibility(View.GONE);
//                        isAuthoritySucess = true;
//                    }
//                    String payMsg = payAlbum.resultMsg;
//                    ToastUtils.showLong(AlbumActivity.this, payMsg);
//                }
//            } else {
//                ToastUtils.showLong(AlbumActivity.this, getString(R.string.pay_failed));
//            }
//        }
    }

    protected void changeTitleItemState(boolean selected) {
        RelativeLayout rl = (RelativeLayout) mTitle.getChildAt(titlePosition);
        TextView tv = (TextView) rl.findViewById(R.id.tv_episode_title);
        ImageView iv = (ImageView) rl.findViewById(R.id.iv_line);
        if (selected) {
            iv.setVisibility(View.VISIBLE);
            tv.getPaint().setFakeBoldText(true);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.item_title_focus_size));
        } else {
            iv.setVisibility(View.INVISIBLE);
            tv.getPaint().setFakeBoldText(false);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.item_title_normal_size));
        }

    }

    protected void saveOrCancelCollect() {
        LogUtils.i(TAG, "saveOrCancelCollect");
        if (userType != 1) {
            ToastUtils.showShort(AlbumActivity.this, getResources().getString(R.string.album_no_permission));
            return;
        }
        if (mUserId == -1) {
            ToastUtils.showShort(this, getString(R.string.userid_error));
            return;
        }
        if (isCollect) {
            cancelCollect(DEL_COLLECTION_URL, mUserId);
        } else {
            saveCollect(SAVE_COLLECTION_URL, mUserId);
        }
    }

    protected void saveCollect(String url, int userId) {
        LogUtils.i(TAG, "saveCollect");
        if (mVideoDetail == null) {
            return;
        }
        String albumName = mVideoDetail.getAlbumName();
        //// TODO: 2016/9/20 两张海报图上传
        String posterUrl="";
        if(!TextUtils.isEmpty(mVideoDetail.getPicUrl())&&
                !TextUtils.isEmpty(mVideoDetail.getBlueRayImg())){
            posterUrl=mVideoDetail.getBlueRayImg()+"|" + mVideoDetail.getPicUrl();
        }else if(TextUtils.isEmpty(mVideoDetail.getPicUrl())&&
                !TextUtils.isEmpty(mVideoDetail.getBlueRayImg())){
            posterUrl=mVideoDetail.getBlueRayImg();
        }else if(!TextUtils.isEmpty(mVideoDetail.getPicUrl())&&
                TextUtils.isEmpty(mVideoDetail.getBlueRayImg())){
            posterUrl=mVideoDetail.getPicUrl();
        }
        if (TextUtils.isEmpty(albumName)) {
            albumName = "";
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(USER_ID, String.valueOf(userId));
        map.put(RESOURCE_ID, String.valueOf(mVideoDetail.getAlbumId()));
        map.put(RESOURCE_NAME, albumName);
        map.put(TYPE, "1");
        map.put(POSTER_URL, posterUrl);
        map.put(CPID, String.valueOf(col_contentId));
        //TODO 用户设备类型
        map.put(USER_TYPE, userType + "");
        LogUtils.i(TAG, "saveCollect map: " + map.toString());

        HttpUtil.post(url, map, OPERATE_COLLECTION_INFO, new Callback<Response2<Object>>() {

            @Override
            public boolean validateReponse(Response response, int id) {
                LogUtils.e(TAG, "save_collect_response_code: " + response.code());
                return response.isSuccessful();
            }

            @Override
            public Response2<Object> parseNetworkResponse(Response response, int id) throws Exception {
                String result = response.body().string();
                Response2<Object> collection = new Gson().fromJson(result, new TypeToken<Response2<Object>>() {
                }.getType());
                LogUtils.d(TAG, "save_collect_result: " + result);
                return collection;
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.showShort(AlbumActivity.this, getResources().getString(R.string.operate_error));
                LogUtils.e(TAG, "HttpGet save_collect failed " + e.getMessage());
            }

            @Override
            public void onResponse(Response2<Object> collection, int id) {
                hasChange = !hasChange;
                mOperateCollect = collection;
                if (handler != null) {
                    handler.sendEmptyMessage(OPERATE_COLLECTION_INFO);
                }
            }
        });

    }

    protected void cancelCollect(String url, int userId) {
        LogUtils.i(TAG, "cancelCollect");
        if (mVideoDetail == null) {
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(USER_ID, String.valueOf(userId));
        map.put(RESOURCE_ID, String.valueOf(mVideoDetail.getAlbumId()));
        map.put(TYPE, "1");
        map.put(CPID, String.valueOf(col_contentId));
        //TODO 用户设备类型
        map.put(USER_TYPE, userType + "");
        LogUtils.i(TAG, "cancelCollect map: " + map.toString());
        HttpUtil.post(url, map, OPERATE_COLLECTION_INFO, new Callback<Response2<Object>>() {

            @Override
            public boolean validateReponse(Response response, int id) {
                LogUtils.e(TAG, "cancel_collect_response_code: " + response.code());
                return response.isSuccessful();
            }

            @Override
            public Response2<Object> parseNetworkResponse(Response response, int id) throws Exception {
                String result = response.body().string();
                Response2<Object> collection = new Gson().fromJson(result, new TypeToken<Response2<Object>>() {
                }.getType());
                LogUtils.d(TAG, "cancel_collect_result: " + result);
                return collection;
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.showShort(AlbumActivity.this, getResources().getString(R.string.operate_error));
                LogUtils.e(TAG, "HttpGet cancel_collect failed " + e.getMessage());
            }

            @Override
            public void onResponse(Response2<Object> collection, int id) {
                hasChange = !hasChange;
                mOperateCollect = collection;
                if (handler != null) {
                    handler.sendEmptyMessage(OPERATE_COLLECTION_INFO);
                }
            }
        });

    }

    /**
     * 注册用户鉴权广播
     */
    private void registUserReceiver() {
        LogUtils.i(TAG, "registUserReceiver");
        if (mUserReceiver == null) {
            mUserReceiver = new UserSuccedReceiver();
        }
        IntentFilter userFilter = new IntentFilter();
        userFilter.addAction(ReceiverActionFlag.USER_AUTHORITY_SUCCED);
        registerReceiver(mUserReceiver, userFilter);

        if (mUserFailedReceiver == null) {
            mUserFailedReceiver = new UserFailedReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ReceiverActionFlag.USER_AUTHORITY_FAILED);
        registerReceiver(mUserFailedReceiver, filter);

    }

    /**
     * 取消用户鉴权广播
     */
    private void unregistUserReceiver() {
        LogUtils.i(TAG, "unregistUserReceiver");
        if (mUserReceiver != null) {
            unregisterReceiver(mUserReceiver);
        }
        if (mUserFailedReceiver != null) {
            unregisterReceiver(mUserFailedReceiver);
        }
    }

    private void checkUserId() {
        LogUtils.i(TAG, "checkUserId");
        unregistUserReceiver();
        handler.removeMessages(AAA_FOR_USER_TIMEOUT);
        dissmissLoadingDialog();
        try {
            mUserId = SysUtils.getUserId();
        } catch (ClassCastException e) {
            LogUtils.e(TAG, e.toString());
        }
        if (mUserId == -1) {
            onError(getResources().getString(R.string.userid_error));
            return;
        }
        requestAlbumInfo(requestAlbumUrl);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        int id = v.getId();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    if (R.id.btn_play == id) {
//                        Button btn = (Button) v;
                        if (mVideoList != null && mVideoList.size() > 0) {
                            VideoBean videoBean = mVideoList.get(0);
                            if (videoBean != null) {
                                startPlay(videoBean);
                            }
                        }
                    } else if (R.id.btn_continue_play == id) {
                        if (mVideoList != null && mVideoList.size() > 0) {
                            int index = 0;
                            int type = mVideoDetail.getAlbumType();
                            if (type == ALBUM_TYPE_EPISODES) {
                                if (mHistoryBean != null && mHistoryBean.timePosition != 0) {
                                    index = mHistoryBean.cpVideoId - 1;
                                }
                            }
                            if (0 > index || index >= mVideoList.size()) {
                                index = 0;
                            }
                            VideoBean videoBean = mVideoList.get(index);
                            if (videoBean != null) {
                                startPlay(videoBean, mHistoryBean != null);
                            }
                        }
                    } else if (R.id.btn_collection == id) {
                        saveOrCancelCollect();
                    } else if (R.id.tv_introduction == id) {
                        String desc = mVideoDetail.getAlbumDesc();
                        TipsDialog.isshowAlbum = true;
                        showOnlyMessageDialog(desc, false);
                        TipsDialog.isshowAlbum = false;
                    } else if (R.id.btn_order == id) {
                        startPay();
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (R.id.btn_episode == id) {
                        mBtnPlay.requestFocus();
                    } else if (R.id.btn_collection == id) {
                        if (View.VISIBLE == mBtnEpisode.getVisibility()) {
                            mBtnEpisode.requestFocus();
                        } else {
                            mBtnPlay.requestFocus();
                        }
                    } else if (R.id.btn_play == id) {
                        if (View.VISIBLE == mBtnContinuePlay.getVisibility()) {
                            mBtnContinuePlay.requestFocus();
                        } else {
                            mBtnPlay.requestFocus();
                        }
                    } else if (R.id.btn_order == id) {
                        mBtnCollection.requestFocus();
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (R.id.btn_play == id) {
                        if (View.VISIBLE == mBtnEpisode.getVisibility()) {
                            mBtnEpisode.requestFocus();
                        } else {
                            mBtnCollection.requestFocus();
                        }
                    } else if (R.id.btn_episode == id) {
                        mBtnCollection.requestFocus();
                    } else if (R.id.btn_continue_play == id) {
                        mBtnPlay.requestFocus();
                    } else if (R.id.btn_collection == id) {
                        if (View.VISIBLE == mBtnOrder.getVisibility()) {
                            mBtnOrder.requestFocus();
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (R.id.btn_play == id || R.id.btn_continue_play == id) {
                        if (mRecyclerView != null && mRecyclerView.getChildAt(0) != null) {
                            mRecyclerView.requestFocus();
                        }
                    } else if (R.id.btn_episode == id) {
                        mTitle.requestFocus();
                    } else if (R.id.tv_introduction == id) {
                        if (mBtnContinuePlay.getVisibility() != View.VISIBLE) {
                            mBtnPlay.requestFocus();
                        } else {
                            mBtnContinuePlay.requestFocus();
                        }
                    } else {
                        if (View.VISIBLE == mBtnEpisode.getVisibility()) {
                            mTitle.requestFocus();
                        } else {
                            mRecyclerView.requestFocus();
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (R.id.btn_play == id || R.id.btn_episode == id || R.id.btn_collection == id || R.id.btn_continue_play == id || R.id.btn_order == id) {
                        if (mTvIntroduction.isFocusable()) {
                            mTvIntroduction.requestFocus();
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_BACK:
                    this.finish();
                    break;
                case KeyEvent.KEYCODE_VOLUME_UP:
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    return false;
            }
            return true;
        }

        return false;
    }

    private void cancelTag() {
        HttpUtil.cancelTag(requestAlbumUrl);
        HttpUtil.cancelTag(RECOMMEND_URL_TAG);//推荐
        HttpUtil.cancelTag(CHECK_COLLECTION_URL_TAG);//收藏记录
        HttpUtil.cancelTag(OPERATE_COLLECTION_INFO);
        HttpUtil.cancelTag(CHECK_AUTHORITY_URL_TAG);//鉴权
        HttpUtil.cancelTag(CHECK_HISTORY_URL_TAG);//播放
    }

    protected void onRelease() {
        handler.removeCallbacksAndMessages(null);
        handler = null;
        mMainUpView.destroyDrawingCache();
        mMainUpView = null;
        if (mRecommendBeanList != null) {
            mRecommendBeanList.clear();
            mRecommendBeanList = null;
        }
        if (mVideoDetail != null) {
            mVideoDetail = null;
        }
        LogUtils.i(TAG, "onRelease");
        ActivitiesManager.getManager().finishActivity(this);
        //        ActivitiesManager.getManager().finishFirstActivity();
    }


    @Override
    public void onItemClick(RecyclerView.Adapter adapter, View view, int position) {
        if (adapter instanceof EpisodeTitleAdapter) {
            ((LinearLayoutManager) mTitle.getLayoutManager()).scrollToPositionWithOffset((position * EpisodeTitleAdapter.EPISODE_CRITICAL_VALUE), 0);
        } else if (adapter instanceof EpisodeContentAdapter) {
            if (mVideoList != null) {
                VideoBean bean = mVideoList.get(position);
                boolean playContinue = false;
                if (bean != null && mHistoryBean != null && bean.getSeq() == mHistoryBean.cpVideoId) {
                    playContinue = true;
                }
                startPlay(bean, playContinue);
            }
        } else {
            RecommendBean bean = mRecommendBeanList.get(position);
            if (bean != null) {
                int contentType = bean.getContentType();
                if (contentType == ContentType.AR_TYPE) {
                    String posUrl = bean.getContentPosters();
                    if (!TextUtils.isEmpty(posUrl)) {
                        SubjectContent subjectContent = new SubjectContent();
                        subjectContent.contentPosters = posUrl;
                        ArrayList<SubjectContent> sList = new ArrayList<>();
                        sList.add(subjectContent);
                        Intent bIntent = new Intent(this, ARdetailActivity.class);
                        bIntent.putExtra(ARdetailActivity.LIST, sList);
                        bIntent.putExtra(AlbumActivity.COL_CONTENT_ID, col_contentId);
                        startActivity(bIntent);
                    }
                } else {
//                    int col_contentId = bean.getCpId();
                    int albumId = bean.getContentId();
                    Intent intent = new Intent(this, AlbumActivity.class);
                    intent.putExtra(ALBUM_ID, albumId);
                    intent.putExtra(COL_CONTENT_ID, col_contentId);
                    intent.putExtra(IS_FROM_ALBUM, true);
                    LogUtils.d(TAG, "dircetion albumId: " + albumId + ", cpId: " + col_contentId);
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    public void onFocusChange(RecyclerView.Adapter adapter, View view, boolean focused, int position) {
        if (adapter instanceof EpisodeTitleAdapter) {
            if (focused) {
                mMainUpView.setUpRectResource(R.drawable.album_item_focus);
                int titlePad = getResources().getDimensionPixelSize(R.dimen.item_eposide_padding);
                mMainUpView.setDrawUpRectPadding(new Rect(titlePad, titlePad, titlePad, titlePad));
                mMainUpView.setFocusView(view, 1.0f);
                ((LinearLayoutManager) mContentRecyclerView.getLayoutManager()).scrollToPositionWithOffset((position * EpisodeTitleAdapter.EPISODE_CRITICAL_VALUE), 0);
            }
        } else if (adapter instanceof EpisodeContentAdapter) {
            if (focused) {
                if (titlePosition * EpisodeTitleAdapter.EPISODE_CRITICAL_VALUE <= position &&
                        position <= titlePosition * EpisodeTitleAdapter.EPISODE_CRITICAL_VALUE + EpisodeTitleAdapter.EPISODE_CRITICAL_VALUE - 1) {
                    // 在该区间，什么都不做
                } else if (position > titlePosition * EpisodeTitleAdapter.EPISODE_CRITICAL_VALUE + EpisodeTitleAdapter.EPISODE_CRITICAL_VALUE - 1) {
                    changeTitleItemState(false);
                    titlePosition++;
                    changeTitleItemState(true);
                } else if (position < titlePosition * EpisodeTitleAdapter.EPISODE_CRITICAL_VALUE + EpisodeTitleAdapter.EPISODE_CRITICAL_VALUE - 1) {
                    changeTitleItemState(false);
                    titlePosition--;
                    changeTitleItemState(true);
                }

                scrollFocusable(false);
                if (isScroll) {
                    return;
                }
                // 焦点框padding值根据不拉伸的阴影计算
                int contentPad = getResources().getDimensionPixelSize(R.dimen.item_eposide_padding);
                mMainUpView.setDrawUpRectPadding(new Rect(contentPad, contentPad, contentPad, contentPad));
                mMainUpView.setUpRectResource(R.drawable.album_item_focus);
                mMainUpView.setFocusView(view, 1.0f);

            }
        } else {
            if (focused) {

                scrollFocusable(false);
                LogUtils.i("Decode", "_down_down_oldPos_position: " + position +",isScroll: "+isScroll);
                if (mIsCanLoadRecommend && position == mRecommendBeanList.size() / 2) {
                    mRecommendPageNo++;
                    String recommendUrl = getRecommendUrl(mRecommendPageNo);
                    requestRecommendInfo(recommendUrl);
                }
                if (isScroll) {
                    return;
                }
                int pad = getResources().getDimensionPixelSize(R.dimen.item_mianupview_pleft);
                int padRight = getResources().getDimensionPixelSize(R.dimen.item_mianupview_pright);
                mMainUpView.setUpRectResource(R.drawable.album_item_focus);
                mMainUpView.setDrawUpRectPadding(new Rect(pad, pad, padRight, pad));
                mMainUpView.setFocusView(view, 1.0f);
            }

        }
    }

    private String getRecommendUrl(int pageSize) {
        String hyphen = "-";
        StringBuilder builder = new StringBuilder();
        builder.append(RECOMMEND_URL).append("/").append(pageSize).append(hyphen).append(mRecommendPageSize).append(hyphen).append(col_contentId).append(hyphen).append(albumId).append("-0.json");
//        String recommendUrl = RECOMMEND_URL + "/" + pageSize + "-100-" + col_contentId + "-" + albumId + "-0.json";
        LogUtils.i(TAG, "recommendUrl: " + builder.toString());
        return builder.toString();
    }

    private void scrollFocusable(boolean isFocused) {
        mBtnContinuePlay.setFocusable(isFocused);
        mBtnCollection.setFocusable(isFocused);
        mBtnEpisode.setFocusable(isFocused);
        mBtnPlay.setFocusable(isFocused);
        if (mTvIntroduction.getText().toString().endsWith("查看更多")) {
            mTvIntroduction.setFocusable(isFocused);
        }
        if (View.VISIBLE == mBtnOrder.getVisibility()) {
            mBtnOrder.setFocusable(isFocused);
        }
    }

    @Override
    public void onKey(RecyclerView.Adapter adapter, View view, int keyCode, KeyEvent event, int position) {
        if (adapter instanceof EpisodeTitleAdapter) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    titlePosition = position;
                    mContentRecyclerView.requestFocus();
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    titlePosition = position;
                    mBtnEpisode.requestFocus();
                }
            }
        } else if (adapter instanceof EpisodeContentAdapter) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {

                    scrollFocusable(true);

                    mTitle.requestFocus();
                }
            }
        } else {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    if (!isScroll) {
                        scrollFocusable(true);

                        if (mBtnContinuePlay.getVisibility() == View.VISIBLE) {
                            mBtnContinuePlay.requestFocus();
                        } else {
                            mBtnPlay.requestFocus();
                        }
                    }
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//                    if (view == null) {
//                        View curView = mRecyclerView.getLayoutManager().findViewByPosition(position);
//                        LogUtils.i("Decode", "_down_down_oldPos_kkk: " + position + ", curView: " + curView);
//                        if (curView != null) curView.requestFocus();
//                    }
                    if (isLoadingRecommend) {
                        ToastUtils.showShort(this, getString(R.string.data_loading));
                    }
                }
            }
        }
    }


    @Subscribe
    public void subscribe(InfoEvent infoEvent) {
        LogUtils.i(TAG, "upLoad");
        int id = infoEvent.id;
        Object obj = infoEvent.obj;
        LogUtils.i("InfoEvent =" + infoEvent.toString());
        scrollFocusable(true);
        switch (id) {
            case EventConf.SENO_MESSAGE_BY_PAY_INFO:
                LogUtils.i("Decode", "SENO_MESSAGE_BY_PAY_INFO");
                isAuthoritySucess = (Boolean) obj;
                if (isAuthoritySucess) {
                    mBtnPlay.setText(getString(R.string.ablum_btn_play));
                    mBtnPlay.requestFocus();
                    mBtnOrder.setVisibility(View.GONE);
                    isAuthoritySucess = true;
                    mAuthority.code = PayState.PAY_THROUGH;
                    if (mHistoryBean != null && mHistoryBean.timePosition > 0) {
                        setFocusView(mBtnContinuePlay);
                    }
                } else {
                    mBtnContinuePlay.setVisibility(View.GONE);
                }
                break;
            case EventConf.CALL_ALBUM_TO_UPDATE_RESUME_PLAY:
                EventBusBean<Map<String, String>> mapEventBusBean = (EventBusBean<Map<String, String>>) obj;
                LogUtils.i("mapEventBusBean.TAG = " + mapEventBusBean.TAG);
                if (!TextUtils.equals(mapEventBusBean.TAG, mRequestPlayTag) || TextUtils.isEmpty(mRequestPlayTag)) {
                    return;
                }
                Map<String, String> map = mapEventBusBean.obj;
                String p = map.get(PlayerActivity.POSITION);
                long timePosition = Long.parseLong(p);
                int index = Integer.parseInt(map.get(PlayerActivity.INDEX));

                if (timePosition < 0) {
                    // 视频未播放，不做处理
                    return;
                } else if (timePosition > 0) {
                    if (mHistoryBean == null) {
                        mHistoryBean = new DisplayHistory();
                    }
                    mHistoryBean.timePosition = timePosition;
                    mHistoryBean.cpVideoId = index;
                    // 保留历史记录，付费成功可显示续播
                    if (!isAuthoritySucess) return;
                    setFocusView(mBtnContinuePlay);
                } else {
                    mBtnContinuePlay.setVisibility(View.GONE);
//                    mBtnPlay.requestFocus();
                    setFocusView(mBtnPlay);
                }
                break;
        }
    }

    // 监听绘画完成再获得焦点
    private void setFocusView(final View view) {
        if (view == null) return;
        view.clearFocus();
        bridget.setVisibleWidget(true);
        bridget.setTranDurAnimTime(0);
//        if (view.getVisibility() != View.VISIBLE || !view.hasFocus()) {
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mMainUpView.setUpRectResource(R.drawable.album_btn_focus);
                            mMainUpView.setDrawUpRectPadding(new Rect(0, 0, 0, 0));
                            mMainUpView.setFocusView(view, 1.0f);
                            bridget.setTranDurAnimTime(200);
                            bridget.setVisibleWidget(false);
                        }
                    }, 200);
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });

//        }
        view.setVisibility(View.VISIBLE);
        view.requestFocus();
    }

    class MyLinearLayoutManager extends LinearLayoutManager {

        private int mScrollVaule;
        private int mType = 0;
        private Context mContext;

        public MyLinearLayoutManager(Context context) {
            super(context);
            mContext =context;
        }

        public void setScrollValue(int scrollValue) {
            mScrollVaule = scrollValue;
        }

        public void setType(int type) {
            mType = type;
        }

        @Override
        public View onFocusSearchFailed(View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
            int focusPos = this.getPosition(focused);
            int nextPos = focusPos + calcOffsetToNextView(focusDirection);
            View view = findViewByPosition(nextPos);
            if (view == null && mType == 0) {
                View view1 = null;
//                int scrollWidth = mContext.getResources().getDimensionPixelSize(R.dimen.album_recommend_iv_width);
                if (focusDirection == View.FOCUS_RIGHT) {
                    scrollHorizontallyBy(mScrollVaule, recycler, state);
                    int pos = findLastCompletelyVisibleItemPosition();
                    view1 = findViewByPosition(pos);
                } else if (focusDirection == View.FOCUS_LEFT) {
                    scrollHorizontallyBy(-mScrollVaule, recycler, state);
                    int pos = findFirstCompletelyVisibleItemPosition();
                    view1 = findViewByPosition(pos);
                }
                return view1;
            }
            LogUtils.i("Decode", "onFocusSearchFailed_position: " + focusPos + ", nextPos: " + nextPos + ", view: " + view);
            return view;
        }

        protected int calcOffsetToNextView(int direction) {
            int orientation = getOrientation();
            if (orientation == VERTICAL) {
                switch (direction) {
                    case View.FOCUS_DOWN:
                        return 1;
                    case View.FOCUS_UP:
                        return -1;
                }
            } else if (orientation == HORIZONTAL) {
                switch (direction) {
                    case View.FOCUS_RIGHT:
                        return 1;
                    case View.FOCUS_LEFT:
                        return -1;
                }
            }
            return 0;
        }
    }

    class UserSuccedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.d(TAG, "UserSuccedReceiver onReceiver");
            if (handler != null) {
                handler.sendEmptyMessage(AAA_FOR_USER);
            }
        }
    }

    class UserFailedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.d(TAG, "UserFailedReceiver onReceiver");
            if (handler != null) {
                handler.sendEmptyMessage(AAA_FOR_USER);
            }
        }
    }
}
