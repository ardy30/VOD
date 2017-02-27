package com.ppfuns.vod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.reflect.TypeToken;
import com.ppfuns.model.adapter.MainAdapter;
import com.ppfuns.model.entity.Channel;
import com.ppfuns.model.entity.Columns;
import com.ppfuns.model.entity.Focus;
import com.ppfuns.ui.view.AlwaysMarqueeTextView;
import com.ppfuns.ui.view.MainUpView;
import com.ppfuns.ui.view.OpenEffectBridge;
import com.ppfuns.ui.view.recycle.GridLayoutManagerTV;
import com.ppfuns.ui.view.recycle.RecyclerViewTV;
import com.ppfuns.util.LogUtils;
import com.ppfuns.util.SysUtils;
import com.ppfuns.util.ThreadManager;
import com.ppfuns.util.https.ContractUrl;
import com.ppfuns.util.https.HttpUtil;
import com.ppfuns.util.https.RequestCode;
import com.ppfuns.vod.R;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Response;

import static com.ppfuns.util.JsonUtils.fromJson;
import static com.ppfuns.vod.R.attr.titleWidth;


public class MainActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private static final String TAG = "MainActivity";
    //title部分
    private ImageView mIndividual;
    private ImageView mSearch;
    private ImageView mIcon;

    private TextView mTime;
    private TextView mTvMainTitle;


    //中心内容部分
    private RelativeLayout mContent;

    private RelativeLayout        mTvPlay;//热门电视剧
    private ImageView             mTvPlayIv;
    private AlwaysMarqueeTextView mTvPlayText;
    private AlwaysMarqueeTextView mTvPlayFocusText;

    private RelativeLayout        mHotMovie;//热门电影
    private ImageView             mHotMovieIv;
    private AlwaysMarqueeTextView mHotMovieText;
    private AlwaysMarqueeTextView mHotMovieFocusText;

    private RelativeLayout        mAnimation;//热门动漫
    private ImageView             mAnimationIv;
    private AlwaysMarqueeTextView mAnimationText;
    private AlwaysMarqueeTextView mAnimationFocusText;

    private RelativeLayout        mHotvariety;//热门综艺
    private ImageView             mHotvarietyIv;
    private AlwaysMarqueeTextView mHotvarietyText;
    private AlwaysMarqueeTextView mHotvarietyFocusText;

    private RelativeLayout        mSports;//体育专区
    private ImageView             mSportsIv;
    private AlwaysMarqueeTextView mSportsText;
    private AlwaysMarqueeTextView mSportsFocusText;

    private RelativeLayout        mSpecial;//专题
    private ImageView             mSpecialIv;
    private AlwaysMarqueeTextView mSpecialText;
    private AlwaysMarqueeTextView mSpecialFocusText;


    private RecyclerViewTV mRVcolumn;//右边栏目位

    //焦点框相关
    private MainUpView       mMainUpView;
    private OpenEffectBridge mOpenEffectBridge;
    private View             mOldFocus;
    private int              oldId;//用于控制焦点移动的id
    private float scale = 1.00f;//动画缩放比例

    //主页焦点图URL
    private String mFocusUrl;
    //主页频道URL
    private String mChannelUrl;
    // 栏目URL
    private String mColumnUrl;
    //栏目id
    //    private String mColumnId;
    //平台 "0" 机顶盒 "1"
    private String mPlatform = "0";


    private com.ppfuns.model.entity.Response<List<Focus>> mFocusResponse;
    Map<Integer, Focus> mFocusResults = new TreeMap<>();

    //    private ChannelBean mChannelBean;//频道
    private com.ppfuns.model.entity.Response<List<Channel>> mChannelResponse;

    //0为专辑  1为专题
    private static final String TYPE_ALBUM   = "0";
    private static final String TYPE_SPECIAL = "1";

    //焦点图位置
    private static final int TVPLAY_TYPE    = 1;
    private static final int MOVIE_TYPE     = 2;
    private static final int ANIMATION_TYPE = 3;
    private static final int VARIETY_TYPE   = 4;
    private static final int SPORTS_TYPE    = 5;
    private static final int SPECIAL_TYPE   = 6;


    //mRVcolumn的适配器
    private MainAdapter mMainAdapter;

    //时间
    private Date             mDate;
    private SimpleDateFormat mDateFormat;

    //更新时间的类
    private TimeTask mTimeTask;
    Handler mHandler = new Handler();
    private List<Channel> mResult = new ArrayList<>();
    private View mCurrentFocus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }


    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.d(TAG, "onStop");
        if (mTimeTask != null)
            mTimeTask.stop();
        mCurrentFocus = getCurrentFocus();
    }

    //    @Override
    //    public void onBackPressed() {
    //        super.onBackPressed();
    //        Intent intent = new Intent(Intent.ACTION_MAIN);
    //        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
    //        intent.addCategory(Intent.CATEGORY_HOME);
    //        startActivity(intent);
    //        finish();
    //    }

    /**
     * 初始化控件
     */
    private void initView() {
        //        mChannelBean = new ChannelBean();
        mChannelResponse = new com.ppfuns.model.entity.Response<>();
        //个人中心
        //搜索
        //时间
        mIndividual = (ImageView) findViewById(R.id.main_iv_individual);
        mIcon = (ImageView) findViewById(R.id.main_title_logo);
        mTvMainTitle = (TextView) findViewById(R.id.tv_main_title);
        //        Glide.with(this).load(R.drawable.ju_logo).into(mIcon);
        mSearch = (ImageView) findViewById(R.id.main_iv_search);
        mTime = (TextView) findViewById(R.id.main_tv_time);

        //设置默认透明度
        mIndividual.getBackground().setAlpha(50);
        mSearch.getBackground().setAlpha(50);

        //内容区域
        mContent = (RelativeLayout) findViewById(R.id.main_content);

        //热门电视剧
        mTvPlay = (RelativeLayout) findViewById(R.id.main_rl_hotTvPlay);
        mTvPlayIv = (ImageView) findViewById(R.id.main_iv_hotTvPlay);
        mTvPlayText = (AlwaysMarqueeTextView) findViewById(R.id.main_tv_hotTvPlay);
        mTvPlayFocusText = (AlwaysMarqueeTextView) findViewById(R.id.main_tv_hotTvPlayFocus);


        //热门电影
        mHotMovie = (RelativeLayout) findViewById(R.id.main_rl_hotMovie);
        mHotMovieIv = (ImageView) findViewById(R.id.main_iv_hotMovie);
        mHotMovieText = (AlwaysMarqueeTextView) findViewById(R.id.main_tv_hotMovie);
        mHotMovieFocusText = (AlwaysMarqueeTextView) findViewById(R.id.main_tv_hotMovieFocus);

        //热门动漫
        mAnimation = (RelativeLayout) findViewById(R.id.main_rl_animation);
        mAnimationIv = (ImageView) findViewById(R.id.main_iv_animation);
        mAnimationText = (AlwaysMarqueeTextView) findViewById(R.id.main_tv_animation);
        mAnimationFocusText = (AlwaysMarqueeTextView) findViewById(R.id.main_tv_animation_focus);

        //热门综艺
        mHotvariety = (RelativeLayout) findViewById(R.id.main_rl_hotvariety);
        mHotvarietyIv = (ImageView) findViewById(R.id.main_iv_hotvariety);
        mHotvarietyText = (AlwaysMarqueeTextView) findViewById(R.id.main_tv_hotvariety);
        mHotvarietyFocusText = (AlwaysMarqueeTextView) findViewById(R.id.main_tv_hotvariety_focus);

        //体育专区
        mSports = (RelativeLayout) findViewById(R.id.main_rl_sports);
        mSportsIv = (ImageView) findViewById(R.id.main_iv_sports);
        mSportsText = (AlwaysMarqueeTextView) findViewById(R.id.main_tv_sports);
        mSportsFocusText = (AlwaysMarqueeTextView) findViewById(R.id.main_tv_sports_focus);

        //专题推荐位
        mSpecial = (RelativeLayout) findViewById(R.id.main_rl_special);
        mSpecialIv = (ImageView) findViewById(R.id.main_iv_special);
        mSpecialText = (AlwaysMarqueeTextView) findViewById(R.id.main_tv_special);
        mSpecialFocusText = (AlwaysMarqueeTextView) findViewById(R.id.main_tv_special_focus);


        //栏目位
        mRVcolumn = (RecyclerViewTV) findViewById(R.id.main_rv_column);


        //焦点框
        mMainUpView = (MainUpView) findViewById(R.id.mainUpView1);

        mOpenEffectBridge = (OpenEffectBridge) mMainUpView.getEffectBridge();

        //设置边框
        mMainUpView.setUpRectResource(R.drawable.main_focus_2);
        mOpenEffectBridge.setVisibleWidget(true);
        mOpenEffectBridge.setTranDurAnimTime(0);


        //禁用个人中心的左键
        mIndividual.setNextFocusLeftId(R.id.main_iv_individual);
        mSearch.setNextFocusRightId(R.id.main_iv_search);


        //设置mainUpView
        setMainUpView();
    }


    /**
     * 初始化数据
     */
    private void initData() {

        initConf();

        setTime();

        RequestDataFromNet();

        //        setMainTitle();

    }

    @Override
    protected void getColumnCallBack() {
        LogUtils.i(TAG, "getColumnCallBack");
        super.getColumnCallBack();
        setMainTitle();
    }

    private void setMainTitle() {
        LogUtils.i(TAG, "setMainTitle");
        if (mColumns == null) {
            mColumns = getColumn();
        }
        if (mColumns != null) {
            String name = mColumns.name;
            String logo = mColumns.logo;
            if (!TextUtils.isEmpty(name)) {
                mTvMainTitle.setText(name);
            }
            if (!TextUtils.isEmpty(logo)) {
                Glide.with(this).load(logo).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(R.drawable.ju_logo).into(mIcon);
            }
        } else {
            Glide.with(this).load(R.drawable.ju_logo).into(mIcon);
        }
    }

    private void initConf() {
        //栏目id
        //        if (BaseApplication.getmColumnId() != -1) {
        //            mColumnId = BaseApplication.getmColumnId() + "";
        //        } else {
        //            mColumnId = "20";
        //        }
        //TODO :平台类型

        //URL
        mFocusUrl = ContractUrl.getVodUrl() + "/r/focus/" + vodColumnId + "-" + mPlatform + ".json";
        //        mFocusUrl = "http://192.168.1.82:9061/video-operation/api/r/focus/34-0.json";
        mChannelUrl = ContractUrl.getVodUrl() + "/r/channel/" + vodColumnId + "-" + mPlatform + ".json";
        mColumnUrl = SysUtils.getContentUrl() + ContractUrl.COMMON + ContractUrl.COMMOL_COLUMNS_INFO + vodColumnId + "-0.json";
        LogUtils.d(TAG + "1", mFocusUrl);
        LogUtils.d(TAG + "1", mChannelUrl);
        LogUtils.d(TAG + "1", mColumnUrl);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {

        mIndividual.setOnClickListener(this);
        mSearch.setOnClickListener(this);

        mTvPlay.setOnClickListener(this);
        mTvPlay.setOnFocusChangeListener(this);
        mHotMovie.setOnClickListener(this);
        mHotMovie.setOnFocusChangeListener(this);
        mAnimation.setOnClickListener(this);
        mAnimation.setOnFocusChangeListener(this);
        mHotvariety.setOnClickListener(this);
        mHotvariety.setOnFocusChangeListener(this);
        mSports.setOnClickListener(this);
        mSports.setOnFocusChangeListener(this);
        mSpecial.setOnClickListener(this);
        mSpecial.setOnFocusChangeListener(this);
    }


    /**
     * 控制mainUpView移动焦点相关的方法
     */
    private void setMainUpView() {
        //目标布局
        mContent.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, final View newFocus) {
                //TODO 如果不需要缩放动画 这里的scale不需要赋值 只需把scale设为1
                // mMainUpView.setDrawUpRectPadding(new Rect(0, 0, 0, 0));

                if (newFocus != null) {

                    //设置没有频道位时 频道不能获取焦点
                    if (newFocus.getId() == R.id.main_rl_special || newFocus.getId() == R.id.main_rl_sports) {
                        if (mChannelResponse == null || mRVcolumn.getChildCount() == 0) {
                            newFocus.setNextFocusRightId(newFocus.getId());

                        }
                    }

                    //不需要焦点框的控件
                    if (newFocus.getId() == R.id.main_iv_search || newFocus.getId() == R.id.main_iv_individual) {

                        //记录内容位中上次获得焦点的控件
                        if (oldFocus != null && oldFocus.getId() != R.id.main_iv_search && oldFocus.getId() != R.id.main_iv_individual) {

                            oldId = oldFocus.getId();
                        }
                        newFocus.setNextFocusDownId(oldId);


                        //设置焦点框显示与隐藏
                        if (newFocus.hasFocus()) {
                            newFocus.getBackground().setAlpha(-50);
                            mOpenEffectBridge.setVisibleWidget(true);
                        }
                        if (newFocus.getId() == R.id.main_iv_search) {

                            mIndividual.getBackground().setAlpha(50);
                        } else {

                            mSearch.getBackground().setAlpha(50);
                        }
                        scale = 1.0f;

                    } else {
                        mIndividual.getBackground().setAlpha(50);

                        mSearch.getBackground().setAlpha(50);

                        mOpenEffectBridge.setVisibleWidget(false);

                        newFocus.bringToFront();
                        // TODO:更改控件缩放
                        scale = 1.00f;
                    }
                    mMainUpView.setFocusView(newFocus, mOldFocus, scale);

                    mOldFocus = newFocus;

                }
            }


        });
    }

    /**
     * 设置时间的方法
     */
    private void setTime() {
        if (mTimeTask == null) {
            mTimeTask = new TimeTask();
            ThreadManager.getNormalPool().execute(mTimeTask);
        } else {
            mTimeTask.start();
        }

    }

    /**
     * 1分钟更新一次时间的类
     */
    private class TimeTask implements Runnable {
        public void start() {
            mTime.setText(getTime());
            mHandler.postDelayed(this, 60000);
        }

        @Override
        public void run() {
            start();
        }

        public void stop() {
            mHandler.removeCallbacks(this);
        }
    }

    /**
     * 获取当前时间的方法
     */
    private String getTime() {
        if (mDate == null) {
            mDate = new Date();
        }
        if (mDateFormat == null) {
            mDateFormat = new SimpleDateFormat("HH:mm");
        }
        mDate.setTime(System.currentTimeMillis());
        return mDateFormat.format(mDate);
    }

    @Override
    public void reLoad() {
        super.reLoad();
        RequestDataFromNet();
        getColumnContent();
    }

    /**
     * 请求网络的方法
     */
    private void RequestDataFromNet() {

        //1.请求焦点图网络数据

        HttpUtil.getHttpHtml(mFocusUrl, new StringCallback() {

            @Override
            public boolean validateReponse(Response response, int id) {
                LogUtils.i("Decode", "validateReponse_code: " + response.code());
                return response.isSuccessful();
            }

            @Override
            public String parseNetworkResponse(Response response, int id) throws IOException {

                LogUtils.i("Decode", "parseNetworkResponse_code: " + response.code());
                return super.parseNetworkResponse(response, id);
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                showDialogTips(getResources().getString(R.string.data_error));
            }

            @Override
            public void onResponse(String response, int id) {
                //                mFocusBean = JsonUtils.fromJson(response, FocusBean.class);
                mFocusResponse = (com.ppfuns.model.entity.Response<List<Focus>>) fromJson(response, new TypeToken<com.ppfuns.model.entity.Response<List<Focus>>>() {
                }.getType());
                LogUtils.e(mFocusResponse.toString());
                //排序 去重复 取最后一个重复的数据
                sortFocusBean();
                //更新Ui
                upDateUI();

                LogUtils.d(TAG, "NET");
            }

        });
        //2.请求频道位数据
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
                mChannelResponse = (com.ppfuns.model.entity.Response<List<Channel>>) fromJson(response, new TypeToken<com.ppfuns.model.entity.Response<List<Channel>>>() {
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

    String channelJson;

    /**
     * 对焦点图数据进行排序及去重复处理
     */
    private void sortFocusBean() {

        mFocusResults.clear();

        for (int i = 0; i < mFocusResponse.data.result.size(); i++) {

            Focus value = mFocusResponse.data.result.get(i);

            int key = mFocusResponse.data.result.get(i).seq;

            //TODO:add
            //判断数据是否重复 重复替换 不重复添加


            mFocusResults.put(key, value);
        }
    }

    /**
     * 更新焦点图的方法
     */
    private void upDateUI() {

        //        if (!mFocusBean.data.code.equals(RequestCode.RESPONSE_SUCCESS)) {
        //            return;
        //        }
        if (!mFocusResponse.data.code.equals(RequestCode.RESPONSE_SUCCESS)) {
            return;
        }
        setUi(TVPLAY_TYPE, mTvPlayText, mTvPlayFocusText, mTvPlayIv, R.drawable.ju_default_420x692, R.drawable.ju_default_420x692);
        //        if (mTvPlay.hasFocus()) {
        //            AlwaysMarqueeTextView childAt = (AlwaysMarqueeTextView) mTvPlay.getChildAt(2);
        //            if (childAt.getTextWidth() > mTvPlay.getWidth()) {
        //                childAt.startScroll();
        //            }
        //        }
        setUi(MOVIE_TYPE, mHotMovieText, mHotMovieFocusText, mHotMovieIv, R.drawable.ju_default_730x458, R.drawable.ju_default_730x458);

        setUi(ANIMATION_TYPE, mAnimationText, mAnimationFocusText, mAnimationIv, R.drawable.ju_default_360x224, R.drawable.ju_default_360x224);

        setUi(VARIETY_TYPE, mHotvarietyText, mHotvarietyFocusText, mHotvarietyIv, R.drawable.ju_default_360x224, R.drawable.ju_default_360x224);
        setUi(SPORTS_TYPE, mSportsText, mSportsFocusText, mSportsIv, R.drawable.ju_default_360x224, R.drawable.ju_default_360x224);
        setUi(SPECIAL_TYPE, mSpecialText, mSpecialFocusText, mSpecialIv, R.drawable.ju_default_360x458, R.drawable.ju_default_360x458);

        View view = this.getCurrentFocus();
        if (view != null && view instanceof RelativeLayout) {
            View.OnFocusChangeListener listener = view.getOnFocusChangeListener();
            if (listener != null) {
                listener.onFocusChange(view, view.requestFocus());
            }
        }
    }

    /**
     * 设置ui的方法
     *
     * @param key          序号
     * @param textView     图片上面的文字
     * @param focusContent 图片上面的文字看点
     * @param imageView    需要设置的图片
     * @param loadingImage 加载时的图片
     * @param errImage     加载错误显示的图片
     */
    public void setUi(int key, TextView textView, TextView focusContent, ImageView imageView, int loadingImage, int errImage) {
        if (mFocusResults.size() == 0) {
            return;
        }
        if (key >= 7 || key <= 0) {
            return;
        }
        if (!mFocusResults.containsKey(key)) {
            return;
        }

        setTextView(textView, mFocusResults.get(key).focusName);
       // setTextView(textView, "1234567890.1234567890.1234567890.123456789.123456789.1234567890.123456789..");
        setTextView(focusContent, mFocusResults.get(key).focusContent);

        imageLoad(mFocusResults.get(key).smallPic, imageView, loadingImage, errImage);
    }


    /**
     * 设置焦点图文字的方法
     *
     * @param textView  需要设置的控件
     * @param focusName 焦点图的名称
     */
    private void setTextView(TextView textView, String focusName) {
        if (!TextUtils.isEmpty(focusName)) {
            textView.setText(focusName);
        }
    }

    /**
     * Glide加载图片的方法
     *
     * @param imageView    当前图片控件
     * @param loadingImage 加载时显示的图片
     * @param errImage     加载错误时显示的图片
     */


    public void imageLoad(String imageURL, ImageView imageView, int loadingImage, int errImage) {

        if (TextUtils.isEmpty(imageURL) || imageView == null || loadingImage == 0 || errImage == 0) {
            return;
        }
        Glide.with(this).load(imageURL)
                .placeholder(loadingImage)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(errImage)
                .into(imageView);
    }

    /**
     * 设置或更新右边频道位的方法
     */
    private void setRecyclerViewAdapter() {
        if (mMainAdapter == null) {

            mMainAdapter = new MainAdapter(this, mChannelResponse.data.result);

            GridLayoutManagerTV gridLayoutManagerTV = new GridLayoutManagerTV(this, 3);

            gridLayoutManagerTV.setOrientation(GridLayoutManagerTV.HORIZONTAL);

            mRVcolumn.setLayoutManager(gridLayoutManagerTV);

            int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.main_content_item_margin);
            mRVcolumn.addItemDecoration(new MainAdapter.SpaceItemDec(spacingInPixels, spacingInPixels));

            mRVcolumn.setAdapter(mMainAdapter);

            mMainAdapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(View view, int position) {
                    openItem(position);
                }
            });

        } else {
            //   mMainAdapter.notifyItemRangeInserted(1, mChannelResponse.data.result.size());
            mMainAdapter.notifyDataSetChanged();
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
        //        int cpId = mChannelResponse.data.result.get(position).cpId;
        if (channelId == -1) {
            showDialogTips(getResources().getString(R.string.data_error));
            return;
        }

        Intent intent = new Intent(this, ChannelCategoryActivity.class);
        String channelName = "";
        intent.putExtra("channelId", channelId);
        intent.putExtra(AlbumActivity.COL_CONTENT_ID, vodColumnId);
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


    /**
     * 根据类型 打开不同的Activity
     *
     * @param key map中的key
     */
    public void openActivity(int key) {
        if (!SysUtils.isNetworkAvailable(this)) {
            showDialogTips(getResources().getString(R.string.network_error));
            return;
        }

        if (mFocusResponse == null) {
            showDialogTips(getResources().getString(R.string.data_error));
            return;
        }

        if (RequestCode.SYSTEM_ERROR.equals(mFocusResponse.data.code)) {
            showDialogTips(getResources().getString(R.string.data_error));

            return;
        }
        if (mFocusResponse.data.code.equals(RequestCode.REQUEST_NOT_FOUND)) {
            showDialogTips(getResources().getString(R.string.data_error));

            return;
        }
        if (mFocusResponse.data.code.equals(RequestCode.CONTENT_EMPTY)) {
            showDialogTips(getResources().getString(R.string.data_error));

            return;

        }
        if (key > 6 || !mFocusResults.containsKey(key) ||
                mFocusResults.get(key).contentId == null || mFocusResults.get(key).contentId <= 0 ||
                TextUtils.isEmpty(mFocusResults.get(key).contentType)) {
            showDialogTips(getResources().getString(R.string.data_error));

            return;
        }


        String contentType = mFocusResults.get(key).contentType;

        Integer contentId = mFocusResults.get(key).contentId;
        //        Integer cpId = mFocusResults.get(key).cpId;

        if (contentType.equals(TYPE_ALBUM)) {
            Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
            //专辑id和栏目id
            intent.putExtra(AlbumActivity.ALBUM_ID, contentId);

            intent.putExtra(AlbumActivity.COL_CONTENT_ID, vodColumnId);

            MainActivity.this.startActivity(intent);
            Map<String, String> map = new HashMap<>();
            map.put("type", TYPE_ALBUM);
            map.put("typeName", "专辑");
            map.put(AlbumActivity.ALBUM_ID, contentId + "");
            map.put(AlbumActivity.COL_CONTENT_ID, vodColumnId + "");
            MobclickAgent.onEvent(this, "focus_select", map);

        } else if (contentType.equals(TYPE_SPECIAL)) {
            Intent intent;
            if (contentId == 0) {
                intent = new Intent(MainActivity.this, SubjectCategoryActivity.class);
            } else {
                intent = new Intent(MainActivity.this, SubjectDetailsActivity.class);
            }

            intent.putExtra("subjectId", contentId);
            intent.putExtra(AlbumActivity.COL_CONTENT_ID, vodColumnId);
            startActivity(intent);
        } else {
            showDialogTips(getResources().getString(R.string.data_error));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "onDestroy");
        if (mFocusResponse != null) {
            HttpUtil.cancelTag(mFocusResponse);
        }
        if (mChannelResponse != null) {
            HttpUtil.cancelTag(mChannelResponse);
        }

    }

    @Override
    public void onClick(View v) {

        LogUtils.d(TAG, "cpId: " + vodColumnId);
        switch (v.getId()) {

            case R.id.main_iv_individual:

                startActivity(new Intent(MainActivity.this, CollectRecordActivity.class).putExtra("collectRecordId", 0).putExtra(AlbumActivity.COL_CONTENT_ID, vodColumnId));

                break;
            case R.id.main_iv_search:

                startActivity(new Intent(MainActivity.this, SearchActivity.class).putExtra(AlbumActivity.COL_CONTENT_ID, vodColumnId));

                break;
            case R.id.main_rl_hotTvPlay:

                openActivity(TVPLAY_TYPE);

                break;
            case R.id.main_rl_hotMovie:

                openActivity(MOVIE_TYPE);

                break;
            case R.id.main_rl_animation:

                openActivity(ANIMATION_TYPE);

                break;
            case R.id.main_rl_hotvariety:

                openActivity(VARIETY_TYPE);

                break;
            case R.id.main_rl_sports:

                openActivity(SPORTS_TYPE);

                break;
            case R.id.main_rl_special:

                openActivity(SPECIAL_TYPE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        LogUtils.d(TAG, "Main onFocusChange");

        RelativeLayout layout = (RelativeLayout) v;

        View shadeView = layout.getChildAt(1);
        AlwaysMarqueeTextView titleText = (AlwaysMarqueeTextView) layout.getChildAt(2);
        AlwaysMarqueeTextView focusText = (AlwaysMarqueeTextView) layout.getChildAt(3);

        int titleWidth = titleText.getTextWidth();
        int focusWidth = focusText.getTextWidth();

        if (hasFocus) {
            shadeView.setVisibility(View.VISIBLE);
            titleText.setVisibility(View.VISIBLE);
            focusText.setVisibility(View.VISIBLE);

            if (focusWidth <= 0 && titleWidth > 0) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) titleText.getLayoutParams();
                params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.px20);
                titleText.setLayoutParams(params);
            }
            focusText.startScroll();
            titleText.startScroll();

        } else {
            shadeView.setVisibility(View.INVISIBLE);
            titleText.setVisibility(View.INVISIBLE);
            focusText.setVisibility(View.INVISIBLE);
            titleText.stopScroll();
            focusText.stopScroll();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
        LogUtils.d(TAG, "onPause");

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);

        if (mCurrentFocus == null) {
            LogUtils.d(TAG + "11", "mCurrentFocus == null");
            mCurrentFocus = mTvPlay;
            //设置默认大小
            ViewTreeObserver vto = mTvPlay.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mTvPlay.requestFocus();
                    mOpenEffectBridge.setVisibleWidget(true);
                    mOpenEffectBridge.setTranDurAnimTime(0);
                    mMainUpView.setFocusView(mTvPlay, scale);
                    mOpenEffectBridge.setVisibleWidget(false);
                    mOpenEffectBridge.setTranDurAnimTime(300);
                    LogUtils.d(TAG + "11", "mTvPlay GlobalLayout");
                    mTvPlay.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
            mOldFocus = mTvPlay;
        } else {
            LogUtils.d(TAG + "11", "mCurrentFocus not null");
            mCurrentFocus.requestFocus();
        }
    }

}


