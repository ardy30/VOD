package com.ppfuns.vod.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ppfuns.model.adapter.CategoryAdapter;
import com.ppfuns.model.adapter.CollectAdapter;
import com.ppfuns.model.entity.Category;
import com.ppfuns.model.entity.RecordingContents;
import com.ppfuns.model.entity.Response2;
import com.ppfuns.model.entity.Result;
import com.ppfuns.ui.view.AlwaysMarqueeTextView;
import com.ppfuns.ui.view.CircularProgress;
import com.ppfuns.ui.view.CollectionPageDrawBridge;
import com.ppfuns.ui.view.FocusedBasePositionManager;
import com.ppfuns.ui.view.FocusedGridView;
import com.ppfuns.ui.view.MainUpView;
import com.ppfuns.ui.view.MemListView;
import com.ppfuns.util.LogUtils;
import com.ppfuns.util.SysUtils;
import com.ppfuns.util.ToastUtils;
import com.ppfuns.util.UIUtils;
import com.ppfuns.util.https.ContractUrl;
import com.ppfuns.util.https.HttpUtil;
import com.ppfuns.vod.R;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.callback.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 收藏/记录页面
 */
public class CollectRecordActivity extends BaseActivity implements View.OnFocusChangeListener {

    @BindView(R.id.channelName)
    TextView channelName;
    @BindView(R.id.albumNumbers)
    TextView albumNumbers;
    @BindView(R.id.pageIndex)
    TextView pageIndex;
    @BindView(R.id.pageIndex2)
    TextView pageIndex2;
    @BindView(R.id.listview)
    MemListView listview;
    @BindView(R.id.gridview)
    FocusedGridView gridview;
    @BindView(R.id.mainUpView1)
    MainUpView mainUpView1;
    @BindView(R.id.progressbar)
    CircularProgress progressbar;

    private CollectAdapter collectAdapter;
    private CategoryAdapter categoryAdapter;
    private AlwaysMarqueeTextView oldfilmeName;


    private List<RecordingContents> albumList;
    private List<Category> categoryList;


    private int pageSize = 20;//
    private int type = 1;//点播
    private final int USER_FOR_BOX = 1;
    private final int REFRESH = 100; //刷新
    private final int LOADMORE = 101; //加载更多
//    private int userId = 3010211;//用户id

    private int pagecount; //总页数
    private int windowWidth;
    private int picWidth;
    private int totalCount;
    private AlwaysMarqueeTextView filmeText;


    private boolean isFirst = true;
    private CollectionPageDrawBridge bridget;


    private View mOldView;
    private View mNewView;
    private int selected;
    private int serverTotalCount = 100; //服务器总数
    private boolean isUp = false;
    private boolean isDown = false;

    private boolean isFirstDown = false; //首次按下down
    private boolean isFirstChange = true; //首次按下down
    private long keydownTime = 0;
    private long spaceTime = 80; //连续按键间隔时间限制

    private boolean loadNextPage = false;
    private HashMap<Integer, List<RecordingContents>> cacheMap = new HashMap<>(); //缓存数据
    public static final String TAG = "CollectRecordActivity";
    private int listId;//list列表Id
    private int listPostion;//list列表选择id
    private int dalenTime = 500;

    //获取收藏列表
    public final String collectListUrl = ContractUrl.getVodUrlUser() + "/getBookmarkList.json";
    //获取播放记录
    public final String recordingUrl = ContractUrl.getCommonUserApi() + "/getResumePointList.json";
    private boolean isFirstload = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_detail);
        ButterKnife.bind(this);
        LogUtils.i(TAG, "_columnId: " + vodColumnId);
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        windowWidth = dm.widthPixels;

        albumList = new ArrayList<>();
        categoryList = new ArrayList<>();
        listview.requestFocus();

        initGridPicSize();
        initView();
        initGridFous();
        Intent intent = getIntent();//获取一个intent对象
        Bundle bundle = intent.getExtras();//获取传递过来的值

        listId = bundle.getInt("collectRecordId", 0);
        LogUtils.i(TAG, "listId: " + listId);

        categoryList.add(new Category(getString(R.string.demand_records), false, 0));
        categoryList.add(new Category(getString((R.string.collection_records)), false, 1));
        //categoryList.add(new Category(getString(R.string.purchase_records), false));
        categoryAdapter.notifyDataSetChanged();
        listview.setSelection(listId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isFirst) {

            isFirst = false;
        }
    }

    private void initView() {

        channelName.setFocusable(false);

        albumNumbers.setFocusable(false);
        pageIndex.setFocusable(false);

        listview.setOnFocusChangeListener(this);
        gridview.setOnFocusChangeListener(this);

        listview.setNextFocusRightId(R.id.gridview);
        gridview.setNextFocusLeftId(R.id.listview);

        mainUpView1.setEffectBridge(new CollectionPageDrawBridge());
        bridget = (CollectionPageDrawBridge) mainUpView1.getEffectBridge();
        //动画时间
        bridget.setTranDurAnimTime(200);

        // bridget.setAnimEnabled(false);

        // 设置移动边框的图片.
        mainUpView1.setUpRectResource(R.drawable.focus);


        // 移动方框缩小的距离.
        int left = getResources().getDimensionPixelSize(R.dimen.left);
        int top = getResources().getDimensionPixelSize(R.dimen.top);
        int right = getResources().getDimensionPixelSize(R.dimen.right);
        int bottom = getResources().getDimensionPixelSize(R.dimen.bottom);
        mainUpView1.setDrawUpRectPadding(new Rect(left, top, right, bottom));
        categoryAdapter = new CategoryAdapter(this, categoryList);
        listview.setAdapter(categoryAdapter);
    }


    /**
     * 获取播放记录数据
     */
    private void getRecordingData(final int userType, String url, final int flag) {
        int userId = SysUtils.getUserId();
        if (userId == -1) {
            return;
        }
        int cpId = SysUtils.getColumnId();
        handler.sendEmptyMessageDelayed(1, dalenTime);
        LogUtils.i(TAG, "userId=" + userId);
        LogUtils.i(TAG, "URL=" + url);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", "" + userId);
        params.put("userType", "" + userType);
        if (cpId != vodColumnId) {
            params.put("cpId", "" + vodColumnId);
        }
        // params.put("type", "" + type);
        Log.d(TAG, "map: " + params.toString() + "url : " + url);
        HttpUtil.post(url, params, new Callback<Response2<Result<List<RecordingContents>>>>() {
            @Override
            public Response2<Result<List<RecordingContents>>> parseNetworkResponse(Response response, int id) throws Exception {
                String json = response.body().string();
                LogUtils.i(TAG, "json:--------------- " + json);
                Gson gson = new Gson();
                Response2<Result<List<RecordingContents>>> bean = gson.fromJson(json, new TypeToken<Response2<Result<List<RecordingContents>>>>() {
                }.getType());
                //LogUtils.i(TAG, "json:--------------- " + bean);
                return bean;
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (loadNextPage) {

                    loadNextPage = false;
                }
                handler.removeMessages(1);
                dissmissLoadingDialog();
                if (progressbar.getVisibility() == View.VISIBLE) {
                    progressbar.setVisibility(View.INVISIBLE);
                }
                LogUtils.i(TAG, "showDialogTips0");
                showDialogTips(getResources().getString(R.string.data_error));
            }


            @Override
            public void onResponse(Response2<Result<List<RecordingContents>>> response, int id) {
                handler.removeMessages(1);

                if (progressbar.getVisibility() == View.VISIBLE) {
                    progressbar.setVisibility(View.INVISIBLE);
                }
                if (loadNextPage) {
                    loadNextPage = false;
                }
                Response2<Result<List<RecordingContents>>> bean = response;

                if (bean != null &&
                        bean.result != null &&
                        bean.result.rows != null &&
                        bean.result.rows.size() > 0) {
                    if (cacheMap != null &&
                            cacheMap.containsKey(0)) if (flag == LOADMORE) {
                        List<RecordingContents> cancelbean = cacheMap.get(0);
                        cancelbean.addAll(bean.result.rows);
                        cacheMap.put(0, cancelbean);
                    } else {
                        cacheMap.put(0, bean.result.rows);
                    }
                    else {
                        cacheMap.put(0, bean.result.rows);
                    }
                } else {
                    if (loadNextPage) {
                        loadNextPage = false;
                    }
                    albumList.clear();
                    collectAdapter.notifyDataSetChanged();
                }
                clearSame(0);

            }
        });
    }


    /**
     * 获取收藏数据
     */
    private void getCollectData(final int type, final int userType, String url, final int flag) {
        int userId = SysUtils.getUserId();
        if (userId == -1) {
            ToastUtils.showShort(this, getString(R.string.userid_error));
            return;
        }
        int cpId = SysUtils.getColumnId();
        handler.sendEmptyMessageDelayed(1, 1000);
        LogUtils.i(TAG, "userId=" + userId);
        LogUtils.i(TAG, "url=" + url);

        //  String url =collectListUrl;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", "" + userId);
        params.put("userType", "" + userType);
        params.put("type", "" + type);
        if (cpId != vodColumnId) {
            params.put("cpId", "" + vodColumnId);
        }
//        params.put("pageSize", "" + pageSize);
        Log.d(TAG, "map: " + params.toString());
        HttpUtil.post(url, params, new Callback<Response2<Result<List<RecordingContents>>>>() {
            @Override
            public Response2<Result<List<RecordingContents>>> parseNetworkResponse(Response response, int id) throws Exception {
                String json = response.body().string();
                LogUtils.i(TAG, "json:--------------- " + json);
                Gson gson = new Gson();
                Response2<Result<List<RecordingContents>>> bean = gson.fromJson(json, new TypeToken<Response2<Result<List<RecordingContents>>>>() {
                }.getType());
                LogUtils.i(TAG, "json:--------------- " + bean);
                return bean;
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (loadNextPage) {
                    loadNextPage = false;
                }
                handler.removeMessages(1);
                dissmissLoadingDialog();
                if (progressbar.getVisibility() == View.VISIBLE) {
                    progressbar.setVisibility(View.INVISIBLE);
                }
                LogUtils.i(TAG, "showDialogTips1");
                showDialogTips(getResources().getString(R.string.data_error));
            }

            @Override
            public void onResponse(Response2<Result<List<RecordingContents>>> response, int id) {
                handler.removeMessages(1);
                dissmissLoadingDialog();
                if (progressbar.getVisibility() == View.VISIBLE) {
                    progressbar.setVisibility(View.INVISIBLE);
                }
                if (loadNextPage) {
                    loadNextPage = false;
                }
                Response2<Result<List<RecordingContents>>> bean = response;

                if (bean != null &&
                        bean.result != null &&
                        bean.result.rows != null &&
                        bean.result.rows.size() > 0) {
                    if (cacheMap != null &&
                            cacheMap.containsKey(1)) {
                        if (flag == LOADMORE) {
                            List<RecordingContents> cancelbean = cacheMap.get(1);
                            cancelbean.addAll(bean.result.rows);
                            cacheMap.put(1, cancelbean);
                        } else {
                            cacheMap.put(1, bean.result.rows);
                        }
                    } else {
                        cacheMap.put(1, bean.result.rows);
                    }
                } else {
                    if (loadNextPage) {
                        loadNextPage = false;
                    }
                    albumList.clear();
                    collectAdapter.notifyDataSetChanged();
                }
                clearSame(1);
            }
        });
    }

    private void clearSame(int position) {
        if (cacheMap.containsKey(position)) {
            List<RecordingContents> cacheBean = cacheMap.get(position);
            List<RecordingContents> sameList = new ArrayList<>();
            List<RecordingContents> nullList = new ArrayList<>();
            if (cacheBean != null && cacheBean.size() > 0) {
                LogUtils.i(TAG, "cacheBeanSize: " + cacheBean.size());
                for (RecordingContents item : cacheBean) {
                    if (TextUtils.isEmpty(item.resourceName) || item.resourceName.equals("null")) {
                        nullList.add(item);
                    }
                }
                if (nullList != null && nullList.size() > 0) {
                    cacheBean.removeAll(nullList);
                }

                RecordingContents temp = null;
                for (int i = cacheBean.size() - 1; i >= 0; i--) {
                    RecordingContents sameBean1 = cacheBean.get(i);
                    if (sameList.contains(sameBean1)) {
                        continue;
                    }
                    for (int j = i - 1; j >= 0; j--) {
                        RecordingContents sameBean2 = cacheBean.get(j);
                        if (sameBean1.resourceId.equals(sameBean2.resourceId)) {
                            if (sameBean1.userType == sameBean2.userType) {
                                temp = sameBean1;
                                if (!sameList.contains(temp)) {
                                    sameList.add(temp);
                                }
                                continue;
                            }
                            if (sameBean1.userType == 1) {
                                temp = sameBean2;
                            } else if (sameBean2.userType == 1) {
                                temp = sameBean1;
                            } else {
                                temp = sameBean2;
                            }
                            if (!sameList.contains(temp)) {
                                sameList.add(temp);
                            }
                        }
                    }
                }
                cacheBean.removeAll(sameList);
                sameList = null;
            }
            //cacheBean.removeAll(sameList);
            cacheMap.put(position, cacheBean);
            initCount(cacheBean);
            updateGridView(cacheBean, REFRESH);
        }
    }

    private void checkCache(int position) {
        if (cacheMap.containsKey(position)) {
            List<RecordingContents> cacheBean = cacheMap.get(position);
            initCount(cacheBean);
            updateGridView(cacheBean, REFRESH);
        } else {
            if (position == 0) {
                getRecordingData(USER_FOR_BOX, recordingUrl, LOADMORE);
            } else if (position == 1) {
                getCollectData(1, USER_FOR_BOX, collectListUrl, LOADMORE);
            }
        }
    }

    private void updateGridView(List<RecordingContents> bean, int flag) {
        if (bean != null &&
                bean != null &&
                bean.size() > 0) {
            if (flag == REFRESH) {
                albumList.clear();
                if (collectAdapter != null) {
                    collectAdapter.notifyDataSetInvalidated();
                }
            }
            albumList.addAll(bean);
            if (collectAdapter == null) {
                collectAdapter = new CollectAdapter(CollectRecordActivity.this, albumList, imageLoader, picWidth);
                if(listview.getSelectedItemPosition()==0){
                    collectAdapter.setRecordType(1);
                }else{
                    collectAdapter.setRecordType(2);
                }
                gridview.setAdapter(collectAdapter);
            } else {
                if(listview.getSelectedItemPosition()==0){
                    collectAdapter.setRecordType(1);
                }else{
                    collectAdapter.setRecordType(2);
                }
                collectAdapter.notifyDataSetInvalidated();
                gridview.setSelection(0);
            }
            totalCount = albumList.size();
            serverTotalCount = bean.size();

            dissmissLoadingDialog();
        }
    }

    @Override
    public void reLoad() {
        super.reLoad();
        LogUtils.i(TAG, "reLoad()0");
        LogUtils.i(TAG, "listId=" + listId);
        if (listPostion == 0) {
            LogUtils.i(TAG, "reLoad()1");
            albumList.clear();
            initpage();
            collectAdapter.notifyDataSetChanged();
            getRecordingData(USER_FOR_BOX, recordingUrl, LOADMORE);
        } else if (listPostion == 1) {
            LogUtils.i(TAG, "reLoad()2");

            albumList.clear();
            initpage();
            collectAdapter.notifyDataSetChanged();
            getCollectData(1, USER_FOR_BOX, collectListUrl, LOADMORE);
        } else if (listPostion == 2) {
        }
    }

    private void updatePageNoData(int selected) {
        int totalPage = 0;
        if (serverTotalCount % 10 == 0) {
            totalPage = serverTotalCount / 10;
        } else {
            totalPage = serverTotalCount / 10 + 1;
        }

        pageIndex2.setText("/" + totalPage);
        pageIndex.setText("" + (selected / 10 + 1));
    }


//    /**获取购买数据
//     *
//     * @param cpId
//     * @param channelId
//     * @param channelCategoryId
//     * @param count
//     */
//    private void getData(int cpId, int channelId, int channelCategoryId, int count) {
//        pageNo=1;
//        albumNumbers.setText("总共" + count + "条记录");
//        ("加载中...");
//
//        Map<String, String> params = new HashMap<String, String>();
//
//        params.put("cpId", cpId + "");
//        params.put("channelId", channelId + "");
//        params.put("channelCategoryId", channelCategoryId + "");
//        albumList.clear();
//        for (int i = 0; i < count; i++) {
//            RecordingContent item = new RecordingContent();
//         //   item.setContentName(i + "风云");
//            albumList.add(item);
//
//        }
//
//        collectAdapter.notifyDataSetChanged();
//        dissmissLoadingDialog();
//    }


    //初始化gridview动画等问题
    private void initGridFous() {
        gridview.setFocusViewId(R.id.img);
        gridview.setAutoChangeLine(false);
        gridview.setFocusChangedListener(new FocusedGridView.FocusChangedListener() {
            @Override
            public void focusLost(boolean hasFocus) {
                LogUtils.i(TAG, "focusLost hasFocus: " + hasFocus);
                if (hasFocus) {
                    bridget.setTranDurAnimTime(200);
                    bridget.setTranDurAnimTime(0);
                    bridget.setVisibleWidget(true);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (gridview.hasFocus()) {
                                bridget.setVisibleWidget(false);
                                bridget.setTranDurAnimTime(200);
                            }
                        }
                    }, 50);

                    if (oldfilmeName != null) {
                        oldfilmeName.setSelected(true);
                        oldfilmeName.setBackgroundResource(R.drawable.collect_btn_bg);
                    }
                } else {
                    if (oldfilmeName != null) {
                        oldfilmeName.setSelected(false);
                        oldfilmeName.setBackgroundResource(R.drawable.mas3);
                    }
                    bridget.setTranDurAnimTime(0);
                    bridget.setVisibleWidget(true);
                }
            }
        });

        gridview.setScrollChangeListener(new FocusedGridView.ScrollChangeListener() {
            @Override
            public void scrollMode(int scrollMode) {
                if (scrollMode == 0) {
                    imageLoader.resume();
                    if (oldfilmeName != null) {
                        oldfilmeName.setBackgroundResource(R.drawable.collect_btn_bg);
                    }
                } else {
                    imageLoader.pause();
                    if (oldfilmeName != null) {
                        oldfilmeName.setBackgroundResource(R.drawable.mas3);
                    }
                }
            }
        });

        gridview.setOnItemSelectedListener(new FocusedBasePositionManager.FocusItemSelectedListener() {
            @Override
            public void onItemSelected(View var1, int var2, boolean var3, View var4) {
                if (var1 != null && var3 && !gridview.isScrolling() && gridview.hasFocus()) {
                    LogUtils.i(TAG, "onItemSelected: " + var2);
                    selected = var2;
                    if (isFirstDown) {
                        isFirstDown = false;
                        bridget.setFirstDown(true);
                    }
                    if (oldfilmeName != null) {
                        oldfilmeName.setBackgroundResource(R.drawable.mas3);
                        oldfilmeName.setSelected(false);
                    }
                    filmeText = (AlwaysMarqueeTextView) var1.findViewById(R.id.filmeName);
                    filmeText.setSelected(true);
                    oldfilmeName = filmeText;
                    final int lastSelectPosition = gridview.getSelectedItemPosition();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (gridview.hasFocus() && lastSelectPosition == gridview.getSelectedItemPosition()) {
                                filmeText.setBackgroundResource(R.drawable.collect_btn_bg);
                            }
                        }
                    }, 175);
                    int padding = getResources().getDimensionPixelSize(R.dimen.focus_padding);
                    mainUpView1.setDrawUpRectPadding(new Rect(padding, padding, padding, padding));

                    mainUpView1.setFocusView(var1, mOldView, 1.0f);
                    mOldView = var1;
                }
                updatePageNoData(var2);
            }
        });

        /**点击进入影片详情
         *
         */
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                /**点击单个影片，跳转影片详情
                 *
                 */
                Intent intent = new Intent(CollectRecordActivity.this, AlbumActivity.class);

                intent.putExtra(AlbumActivity.ALBUM_ID, Integer.valueOf(albumList.get(position).resourceId));
                intent.putExtra(AlbumActivity.USER_TYPE_VALUE, albumList.get(position).userType);
                intent.putExtra(AlbumActivity.USER_CURRID, albumList.get(position).userId);
                intent.putExtra(AlbumActivity.COL_CONTENT_ID, albumList.get(position).cpId);
                LogUtils.i(TAG, "ResourceId=" + albumList.get(position).resourceId);
                CollectRecordActivity.this.startActivityForResult(intent, 7);


            }
        });


        listview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (view != null) {


                    switch (position) {
                        case 0:
                            /**焦点移动到列表栏点播记录项，刷新点播记录数据
                             *
                             */

//                            albumList.clear();
                            initpage();
//                            collectAdapter.notifyDataSetChanged();


                            /**userId（1）用户ID
                             * userType（2）1：盒子 2：手机
                             *  url（3）URL
                             * flag（4）加载更多
                             */
                            //getRecordingData(USER_FOR_BOX, recordingUrl, LOADMORE);
                            checkCache(0);

                            break;
                        case 1:
                            /**焦点移动到列表栏收藏记录项，刷新收藏记录数据
                             *
                             */

//                            albumList.clear();
                            initpage();
//                            collectAdapter.notifyDataSetChanged();

                            /**userId（1）用户ID
                             * type（2）类型:1：点播；2：频道回看; 3：频道节目单
                             * userType（3）1：盒子 2：手机
                             * url（4）URL
                             * flag（5）加载更多
                             */
                            // getCollectData(1, USER_FOR_BOX, collectListUrl, LOADMORE);
                            checkCache(1);

                            break;

                        case 2:
                            /**焦点移动到列表栏购买记录项，刷新够买记录数据
                             *
                             */
                            albumList.clear();
                            initpage();
                            collectAdapter.notifyDataSetChanged();

                            break;

                    }
                    listPostion = position;

                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

    }

    private boolean isLastPage() {
        if ((gridview.getLastVisiblePosition() == totalCount - 1)
                && totalCount < serverTotalCount) {
            return true;
        }
        return false;
    }

    //初始化专辑图片高宽
    private void initGridPicSize() {
        int surplusWidth = windowWidth - UIUtils.dip2px(this, 150) - (int) (windowWidth / 5.7);
        picWidth = surplusWidth / 5;
        collectAdapter = new CollectAdapter(this, albumList, imageLoader, picWidth);
        gridview.setAdapter(collectAdapter);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.gridview:
                if (hasFocus) {
                    bridget.setTranDurAnimTime(0);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (oldfilmeName != null) {
                                bridget.setVisibleWidget(false);
                                oldfilmeName.setBackgroundResource(R.drawable.collect_btn_bg);
                            }
                        }
                    }, 100);
                } else {

                    if (oldfilmeName != null) {

                        oldfilmeName.setBackgroundResource(R.drawable.mas3);
                    }
                    bridget.setVisibleWidget(true);
                }
                break;
            case R.id.listview:

                break;
        }
    }

    //显示多少影片，多少页等
    private void initCount(List<RecordingContents> bean) {

        albumNumbers.setText("总共" + bean.size() + "部影片");
        int PageCount = bean.size();

        int numberpage;
        if (PageCount % 10 == 0) {
            numberpage = PageCount / 10;
        } else {
            numberpage = PageCount / 10 + 1;
        }
        pageIndex.setText("" + 1);
        pageIndex2.setText("/" + numberpage);
    }


    //初始化页码
    private void initpage() {
        pageIndex.setText(1 + "");
        pageIndex2.setText("/" + 1);
        albumNumbers.setText("总共" + 0 + "条记录");


    }

    @Override
    protected void onStop() {
        //记录焦点框所在位置
        listId = listPostion;
        LogUtils.i(TAG, "listId=" + listId);
        super.onStop();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    showLoadingProgressDialog("");
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == 3) {
            LogUtils.i(TAG, "getSelectedItemPosition: " + listview.getSelectedItemPosition());
            cacheMap.clear();
            checkCache(listview.getSelectedItemPosition());
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogUtils.i(TAG,"dispatchKeyEvent action: "+ event.getAction() + " code: "+ event.getKeyCode());
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.i(TAG,"onKeyDown action: "+ event.getAction() + " code: "+ keyCode);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        LogUtils.i(TAG,"onKeyUp action: "+ event.getAction() + " code: "+ keyCode);
        return super.onKeyUp(keyCode, event);
    }
}