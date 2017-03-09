package com.ppfuns.vod.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ppfuns.model.adapter.AlbumAdapter;
import com.ppfuns.model.adapter.ChannelCategoryAdapter;
import com.ppfuns.model.entity.AlbumContents;
import com.ppfuns.model.entity.ChannelCategorys;
import com.ppfuns.model.entity.ContentType;
import com.ppfuns.model.entity.PageContent;
import com.ppfuns.model.entity.SubjectContent;
import com.ppfuns.ui.view.AlwaysMarqueeTextView;
import com.ppfuns.ui.view.CircularProgress;
import com.ppfuns.ui.view.EffectNoDrawBridge;
import com.ppfuns.ui.view.FocusedBasePositionManager;
import com.ppfuns.ui.view.FocusedGridView;
import com.ppfuns.ui.view.MainUpView;
import com.ppfuns.ui.view.MemListView;
import com.ppfuns.util.LogUtils;
import com.ppfuns.util.ToastUtils;
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
 * Created by fangyuan on 2016/6/17.
 */
public class ChannelCategoryActivity extends BaseActivity implements View.OnFocusChangeListener, View.OnClickListener {
    @BindView(R.id.channelName)
    TextView channelName;
    @BindView(R.id.channelCategoryName)
    TextView channelCategoryName;
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
    @BindView(R.id.img_arrow)
    View img_arrow;
    @BindView(R.id.btn_search)
    Button mSearchButton;

    @BindView(R.id.gridviewParent)
    RelativeLayout mGridviewParent;

    @BindView(R.id.rl_left_listview)
    RelativeLayout mRlLeftListView;

    private int mOnlyShowAllCategory = 1;//1为只显示全部分类的信息(并隐藏其他分类),0为显示所有分类信息(显示其他分类)
    private boolean mIsFirstShowGridView = false;
    private AlbumAdapter albumAdapter;
    private ChannelCategoryAdapter categoryAdapter;
    private List<AlbumContents> albumList;
    private List<ChannelCategorys> categoryList;
    private AlwaysMarqueeTextView oldfilmeName;

    private int pageSize = 100; //一页几个
    private int loadDataPageNo = 1; //访问接口页码
    private final int REFRESH = 150; //刷新焦点重置
    private final int LOADMORE = 101; //加载更多焦点不重置
    private int channelId;//频道Id
    private int cpId = 1; //栏目id
    private int assetId = -1; //频道分类Id

    private View mOldView;
    private EffectNoDrawBridge bridget;
    private int nothing = 3;
    public static final String TAG = "ChannelCategoryActivity";

    private int selected;
    private boolean isFirstDown = false; //首次按下down
    private int totalCount;
    private int serverTotalCount = 100; //服务器总数
    private int currOperationTagId = -1; //当前分类id
    private boolean loadNextPage = false;
    private HashMap<Integer, com.ppfuns.model.entity.Response<PageContent<List<AlbumContents>>>> cacheMap = new HashMap<>(); //缓存数据

    public String channelCategoryUrl = ContractUrl.getVodUrl() + "/r/classify/"; //频道分类
    private String alumblistUrl = ContractUrl.getVodUrl() + "/r/classify_content/"; //视频内容（专辑列表接口）
    private AlwaysMarqueeTextView filmeText;
    private boolean isFirst = true;
    private String mPlatform = "0"; //平台 "0" 机顶盒 "1"
    private boolean isScrolling = false;

    private int gridColumn;//列数
    private int pageNo; //一页个数


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_detail);
        channelId = getIntent().getIntExtra("channelId", 40);
        assetId = getIntent().getIntExtra("assetId", -1);
        mOnlyShowAllCategory = getIntent().getIntExtra("onlyShowAllCategory", 0);
        LogUtils.i(TAG, "channelId: " + channelId + ", _vodColumnId: " + vodColumnId);
        ButterKnife.bind(this);
        if (!TextUtils.isEmpty(getIntent().getStringExtra("channelName"))) {
            channelName.setText(getIntent().getStringExtra("channelName"));
        } else {
            channelName.setText("");
        }
        listview.requestFocus();
        albumList = new ArrayList<>();
        categoryList = new ArrayList<>();
        initView();
        initGridFous();
        if (mOnlyShowAllCategory == 1) {
            mIsFirstShowGridView = true;
            //gridColumn = gridview.getNumColumns();
            gridview.setNumColumns(6);
            gridColumn = 6;
            pageNo = gridColumn * 2;
            pageSize = pageNo * 10;
            mRlLeftListView.setVisibility(View.GONE);
            getAlbumData(channelId + "", REFRESH);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mGridviewParent.getLayoutParams();
            layoutParams.setMargins((int) getResources().getDimension(R.dimen.text_marleft), 0, (int) getResources().getDimension(R.dimen.layout_marright), 0);
            mGridviewParent.setLayoutParams(layoutParams);
        } else {
            //gridColumn = gridview.getNumColumns();
            gridColumn = 5;
            pageNo = gridColumn * 2;
            pageSize = pageNo * 10;
            LogUtils.i(TAG, "gridColumn: " + gridColumn + " pageNo: " + pageNo);
            mRlLeftListView.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mGridviewParent.getLayoutParams();
            layoutParams.setMargins((int) getResources().getDimension(R.dimen.layout_marleft), 0, (int) getResources().getDimension(R.dimen.layout_marright), 0);
            mGridviewParent.setLayoutParams(layoutParams);
            getChannleCategoryData();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpUtil.cancelTag(ChannelCategoryActivity.this);

        if (!cacheMap.isEmpty()) {
            cacheMap.clear();
        }
        if (!albumList.isEmpty()) {
            albumList.clear();
        }
        if (!categoryList.isEmpty()) {
            categoryList.clear();
        }
        cacheMap = null;
        albumList = null;
        categoryList = null;

    }

    private void initView() {

        albumAdapter = new AlbumAdapter(this, albumList, imageLoader);
        gridview.setAdapter(albumAdapter);
        channelName.setFocusable(false);
        channelCategoryName.setFocusable(false);
        albumNumbers.setFocusable(false);
        pageIndex.setFocusable(false);
        pageIndex2.setFocusable(false);
        mSearchButton.setOnClickListener(this);
        mSearchButton.setNextFocusDownId(R.id.gridview);
        mSearchButton.setOnFocusChangeListener(this);
        gridview.setOnFocusChangeListener(this);
        listview.setOnFocusChangeListener(this);
        listview.setNextFocusRightId(R.id.gridview);
//        listview.setNextFocusUpId(-1);
        if (mOnlyShowAllCategory != 1) {
            gridview.setNextFocusLeftId(R.id.listview);
            gridview.setNextFocusUpId(R.id.btn_search);
            mSearchButton.setVisibility(View.VISIBLE);
        } else {
            mSearchButton.setVisibility(View.GONE);
            gridview.setNextFocusUpId(R.id.gridview);
        }

        gridview.setNextFocusDownId(R.id.gridview);

        mainUpView1.setEffectBridge(new EffectNoDrawBridge());
        bridget = (EffectNoDrawBridge) mainUpView1.getEffectBridge();
        bridget.setTranDurAnimTime(0);
        bridget.setVisibleWidget(true);
        // 设置移动边框的图片.
        mainUpView1.setUpRectResource(R.drawable.main_focus_2);
        // 移动方框缩小的距离.
        //int pad = (int) getResources().getDimension(R.dimen.channel_map_apd);
        int pad = 50;
        mainUpView1.setDrawUpRectPadding(new Rect(pad, pad, pad, pad));
        categoryAdapter = new ChannelCategoryAdapter(this, categoryList);
        listview.setAdapter(categoryAdapter);

    }

    @Override
    public void reLoad() {
        super.reLoad();
        getChannleCategoryData();
    }

    //获取频道分类数据
    private void getChannleCategoryData() {
        handler.postDelayed(mRunnable, 1000);
//        showLoadingProgressDialog("");
        String url = channelCategoryUrl + channelId + "-" + mPlatform + ".json";
        HttpUtil.getHttpHtml(url, ChannelCategoryActivity.this, new Callback<com.ppfuns.model.entity.Response<List<ChannelCategorys>>>() {
            @Override
            public com.ppfuns.model.entity.Response<List<ChannelCategorys>> parseNetworkResponse(Response response, int id) throws Exception {
                if (response != null) {
                    String htmlStr = response.body().string();
                    LogUtils.i(TAG, "response: " + htmlStr);
                    Gson gson = new Gson();
                    return gson.fromJson(htmlStr, new TypeToken<com.ppfuns.model.entity.Response<List<ChannelCategorys>>>() {
                    }.getType());
                }
                return null;
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                handler.removeCallbacks(mRunnable);
                dissmissLoadingDialog();
                e.printStackTrace();
                showDialogTips(getString(R.string.data_error));
            }

            @Override
            public void onResponse(com.ppfuns.model.entity.Response<List<ChannelCategorys>> response, int id) {
                handler.removeCallbacks(mRunnable);
                dissmissLoadingDialog();
                if (response != null) {
                    com.ppfuns.model.entity.Response<List<ChannelCategorys>> bean = response;
                    if (bean.data.result != null && bean.data.result.size() > 0) {
                        categoryList.clear();
                        categoryAdapter.notifyDataSetChanged();
                        categoryList.addAll(bean.data.result);
                        categoryAdapter.notifyDataSetChanged();
                        if (categoryList.size() > 7) {
                            img_arrow.setVisibility(View.VISIBLE);
                        }
                        if (assetId != -1) {
                            for (int i = 0; i < categoryList.size(); i++) {
                                if (categoryList.get(i).operationTagId == assetId) {
                                    listview.setSelection(i);
                                    break;
                                }
                            }
                        } else {
                            if (mOnlyShowAllCategory == 1) {
                                handler.removeMessages(3);
                                handler.removeMessages(4);
                                Message msg = new Message();
                                msg.what = 4;
                                mainUpView1.setDrawUpRectPadding(new Rect(0, 0, 0, 0));
                                if (isFirst) {
                                    handler.sendMessageDelayed(msg, 0);
                                } else {
                                    handler.sendMessageDelayed(msg, 300);
                                }
                            } else {
                                listview.setSelection(0);
                            }
                        }
                    } else {
                        //ToastUtils.showShort(ChannelCategoryActivity.this, "无数据");
                    }
                }
            }
        });
    }

    //获取专辑等显示内容（grid）
    private void getAlbumData(String chnTags, final int flag) {
        LogUtils.i(TAG, "getAlbumData");
        dissmissLoadingDialog();
        if (flag == LOADMORE) {
            progressbar.setVisibility(View.VISIBLE);
        } else {
            handler.postDelayed(mRunnable, 1000);
//            showLoadingProgressDialog("加载中...");
            albumList.clear();
            if (albumAdapter != null) {
                albumAdapter.notifyDataSetChanged();
            }
        }
        String url = alumblistUrl + loadDataPageNo + "-" + pageSize + "-" + chnTags + "-" + mPlatform + ".json";
        LogUtils.i(TAG, "url: " + url);
        HttpUtil.getHttpHtml(url, ChannelCategoryActivity.this, new Callback<com.ppfuns.model.entity.Response<PageContent<List<AlbumContents>>>>() {
            @Override
            public com.ppfuns.model.entity.Response<PageContent<List<AlbumContents>>> parseNetworkResponse(Response response, int id) throws Exception {
                String htmlStr = response.body().string();
                LogUtils.i(TAG, "response: " + htmlStr);
                Gson gson = new Gson();
                return gson.fromJson(htmlStr, new TypeToken<com.ppfuns.model.entity.Response<PageContent<List<AlbumContents>>>>() {
                }.getType());
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (loadNextPage) {
                    loadDataPageNo--;
                    loadNextPage = false;
                }
                handler.removeCallbacks(mRunnable);
                dissmissLoadingDialog();
                // showDialogTips("加载出错");
                if (progressbar.getVisibility() == View.VISIBLE) {
                    progressbar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onResponse(com.ppfuns.model.entity.Response<PageContent<List<AlbumContents>>> response, int id) {
                handler.removeCallbacks(mRunnable);
                dissmissLoadingDialog();
                if (progressbar.getVisibility() == View.VISIBLE) {
                    progressbar.setVisibility(View.INVISIBLE);
                }
                if (loadNextPage) {
                    loadNextPage = false;
                }
                com.ppfuns.model.entity.Response<PageContent<List<AlbumContents>>> bean = (com.ppfuns.model.entity.Response<PageContent<List<AlbumContents>>>) response;
                if (bean != null &&
                        bean.data != null &&
                        bean.data.result != null &&
                        bean.data.result.pageContent != null &&
                        bean.data.result.pageContent.size() > 0) {
                    if (cacheMap != null &&
                            cacheMap.containsKey(currOperationTagId)) {
                        if (flag == LOADMORE) {
                            com.ppfuns.model.entity.Response<PageContent<List<AlbumContents>>> cancelbean = cacheMap.get(currOperationTagId);
                            cancelbean.data.result.pageContent.addAll(bean.data.result.pageContent);
                            cacheMap.put(currOperationTagId, cancelbean);
                        } else {
                            cacheMap.put(currOperationTagId, bean);
                        }
                    } else {
                        if (cacheMap != null) {
                            cacheMap.put(currOperationTagId, bean);
                        }
                    }

                    updateGridView(bean, flag);
                } else {
                    updateGridView(bean, flag);
                    if (loadNextPage) {
                        loadDataPageNo--;
                        loadNextPage = false;
                    }
                }
            }
        });
    }

    private void updateGridView(com.ppfuns.model.entity.Response<PageContent<List<AlbumContents>>> bean, int flag) {
        if (bean != null &&
                bean.data != null &&
                bean.data.result != null &&
                bean.data.result.pageContent != null &&
                bean.data.result.pageContent.size() > 0) {
            if (flag == REFRESH) {
                albumList.clear();
                if (albumAdapter != null) {
                    albumAdapter.notifyDataSetInvalidated();
                }
            }
            albumList.addAll(bean.data.result.pageContent);
            if (albumAdapter == null) {
                albumAdapter = new AlbumAdapter(ChannelCategoryActivity.this, albumList, imageLoader);
                gridview.setAdapter(albumAdapter);
            } else {
                if(flag == REFRESH){
                    albumAdapter.notifyDataSetInvalidated();
                }else{
                    albumAdapter.notifyDataSetChanged();
                }
            }
//            if (mOnlyShowAllCategory == 1) {
//                gridview.setRefresh(false);
//            } else {
//                gridview.setRefresh(true);
//            }
            totalCount = albumList.size();
            serverTotalCount = bean.data.result.recCount;
            updatePageNoData(selected);
            initCount(bean.data.result);
        } else {
            updatePageNoData(selected);
            //ToastUtils.showShort(ChannelCategoryActivity.this, "无数据");
        }
    }

    //更新页码
    private void updatePageNoData(int selected) {
        if (serverTotalCount == 0) {
            int totalPage = 0;
            pageIndex2.setText(getString(R.string.sslash) + totalPage);
            pageIndex.setText(R.string.szero);
            albumNumbers.setText(getString(R.string.totaltext) +
                    0 + getString(R.string.totaltext1));
        } else {
            int totalPage;
            if (serverTotalCount % pageNo == 0) {
                totalPage = serverTotalCount / pageNo;
            } else {
                totalPage = serverTotalCount / pageNo + 1;
            }
            LogUtils.i(TAG, "totalPage: " + totalPage + " serverTotalCount: " + serverTotalCount + " pageNo: " + pageNo);
            pageIndex2.setText(getString(R.string.sslash) + totalPage);
            pageIndex.setText(String.valueOf(selected / pageNo + 1));
        }
    }

    //初始化gridview动画等问题
    private void initGridFous() {
        gridview.setFocusViewId(R.id.img);
        gridview.setAutoChangeLine(false);
        gridview.setFocusChangedListener(new FocusedGridView.FocusChangedListener() {
            @Override
            public void focusLost(boolean hasFocus) {
                LogUtils.i(TAG,"focusLost hasFocus: " + hasFocus);
                if (hasFocus) {
                    bridget.setTranDurAnimTime(200);
                    if (!mSearchButton.hasFocusable()) {
                        mSearchButton.setFocusable(true);
                    }
                    if (mOnlyShowAllCategory != 1) {
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
                    }
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
                if(scrollMode==0){
                    imageLoader.resume();
                    if (oldfilmeName != null) {
                        oldfilmeName.setBackgroundResource(R.drawable.collect_btn_bg);
                    }
                }else{
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
                if(var1!=null&&var3&&!gridview.isScrolling()){
                    LogUtils.i(TAG,"onItemSelected: "+ var2);
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
                            if(gridview.hasFocus()&&lastSelectPosition==gridview.getSelectedItemPosition()){
                                filmeText.setBackgroundResource(R.drawable.collect_btn_bg);
                            }
                        }
                    }, 175);
                    int padding = getResources().getDimensionPixelSize(R.dimen.focus_padding);
                    mainUpView1.setDrawUpRectPadding(new Rect(padding, padding, padding, padding));

                    if (mOnlyShowAllCategory == 1 && var2 == 0 && mIsFirstShowGridView) {
                        mIsFirstShowGridView = false;
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
                    }
                    mainUpView1.setFocusView(var1, mOldView, 1.0f);
                    mOldView = var1;
                    if (isLastPage()) {
                        loadNextPage = true;
                        loadDataPageNo++;
                        if(mOnlyShowAllCategory==1){
                            getAlbumData(channelId + "", LOADMORE);
                        }else{
                            getAlbumData(currOperationTagId + "", LOADMORE);
                        }
                    }
                }
                updatePageNoData(var2);
            }
        });

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int contentType = albumList.get(position).contentType;
                if (contentType == 1) {//专题
                    Intent intent = new Intent(ChannelCategoryActivity.this, SubjectDetailsActivity.class);
                    intent.putExtra("subjectId", albumList.get(position).contentId);
                    intent.putExtra(AlbumActivity.COL_CONTENT_ID, vodColumnId);
                    ChannelCategoryActivity.this.startActivity(intent);
                } else if (contentType == 0) {//专辑
                    Intent intent = new Intent(ChannelCategoryActivity.this, AlbumActivity.class);
                    intent.putExtra(AlbumActivity.ALBUM_ID, albumList.get(position).contentId);
                    intent.putExtra(AlbumActivity.COL_CONTENT_ID, vodColumnId);
                    ChannelCategoryActivity.this.startActivity(intent);
                } else if (contentType == ContentType.AR_TYPE) {
                    if (contentType == ContentType.AR_TYPE) {
                        String posUrl = albumList.get(position).contentPosters;
                        if (!TextUtils.isEmpty(posUrl)) {
                            SubjectContent subjectContent = new SubjectContent();
                            subjectContent.contentPosters = posUrl;
                            ArrayList<SubjectContent> sList = new ArrayList<>();
                            sList.add(subjectContent);
                            Intent bIntent = new Intent(ChannelCategoryActivity.this, ARdetailActivity.class);
                            bIntent.putExtra(ARdetailActivity.LIST, sList);
                            bIntent.putExtra(AlbumActivity.COL_CONTENT_ID, vodColumnId);
                            startActivity(bIntent);
                        }
                    }
                }
            }
        });

        listview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                if (view != null) {
                    LogUtils.i(TAG, "list_position: " + position);

                    if (progressbar.getVisibility() == View.VISIBLE) {
                        progressbar.setVisibility(View.INVISIBLE);
                    }
                    channelCategoryName.setText(getString(R.string.leftbrackets) +
                            categoryList.get(position).operationTagName + getString(R.string.rightbrackets));
                    loadDataPageNo = 1;
                    totalCount = 0;
                    serverTotalCount = 0;
                    selected = 0;
                    HttpUtil.cancelTag(ChannelCategoryActivity.this);
                    handler.removeMessages(3);
                    Message msg = new Message();
                    msg.what = 3;
                    msg.arg1 = position;
                    mainUpView1.setDrawUpRectPadding(new Rect(0, 0, 0, 0));
                    if (isFirst) {
                        handler.sendMessageDelayed(msg, 0);
                    } else {
                        handler.sendMessageDelayed(msg, 300);
                    }
                    //initArrowVisable();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    // 滑到顶部
                }

                // 滑到底部
                if (categoryList != null && categoryList.size() > 7) {
                    LogUtils.i(TAG, "visibleItemCount: " + visibleItemCount + " firstVisibleItem: " + firstVisibleItem + " totalItemCount: " + totalItemCount);
                    if (visibleItemCount + firstVisibleItem == totalItemCount) {
                        View lastVisibleItemView = view.getChildAt(view.getChildCount() - 1);
                        if (lastVisibleItemView != null) {
                            if (listview.getHeight() == lastVisibleItemView.getBottom()) {
                                if (img_arrow.getVisibility() == View.VISIBLE) {
                                    img_arrow.setVisibility(View.GONE);
                                }
                            } else {
                                if (img_arrow.getVisibility() == View.GONE) {
                                    img_arrow.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } else {
                        if (img_arrow.getVisibility() == View.GONE) {
                            img_arrow.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadDataPageNo = 1;
                totalCount = 0;
                serverTotalCount = 0;
                currOperationTagId = categoryList.get(position).operationTagId;
                getAlbumData(categoryList.get(position).operationTagId + "", nothing);
            }
        });
    }

    //是否到最后一页并且还有数据
    private boolean isLastPage() {
        return (gridview.getLastVisiblePosition() == totalCount - 1)
                && totalCount < serverTotalCount;
    }

    //显示多少影片，多少页等
    private void initCount(PageContent<List<AlbumContents>> bean) {
        albumNumbers.setText(getString(R.string.totaltext) +
                bean.getRecCount() + getString(R.string.totaltext1));
        //pageIndex.setText("" + bean.getPageNo());
        //pageIndex2.setText("/" + bean.getPageCount());
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        LogUtils.i(TAG, "v: " + v.getClass().getName() + " hasFocus: " + hasFocus);
        switch (v.getId()) {
            case R.id.gridview:
                if (hasFocus) {
//                    bridget.setTranDurAnimTime(200);
                    if (!mSearchButton.hasFocusable()) {
                        mSearchButton.setFocusable(true);
                    }
                    if (mOnlyShowAllCategory != 1) {
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
                    }
                    if (oldfilmeName != null) {
                        oldfilmeName.setBackgroundResource(R.drawable.collect_btn_bg);
                    }
                } else {
                    if (oldfilmeName != null) {

                        oldfilmeName.setBackgroundResource(R.drawable.mas3);
                    }
                    bridget.setTranDurAnimTime(0);
                    bridget.setVisibleWidget(true);
                }
                break;
            case R.id.listview:
                if (hasFocus) {
                    if (!mSearchButton.hasFocusable()) {
                        mSearchButton.setFocusable(true);
                    }
                }
                break;
            case R.id.btn_search:
                if (!hasFocus) {
                    if (mOnlyShowAllCategory == 1) {
                        mIsFirstShowGridView = true;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (gridview.hasFocus()) {
                                    bridget.setVisibleWidget(false);
                                    bridget.setTranDurAnimTime(200);
                                }
                            }
                        }, 50);
                    }
                }
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3:
                    if (currOperationTagId != categoryList.get(msg.arg1).operationTagId) {
                        currOperationTagId = categoryList.get(msg.arg1).operationTagId;
                        if (cacheMap != null && cacheMap.containsKey(currOperationTagId)) {
                            com.ppfuns.model.entity.Response<PageContent<List<AlbumContents>>> bean = cacheMap.get(currOperationTagId);
                            if (bean != null) {
                                updateGridView(bean, REFRESH);
                            }
                        } else {
                            getAlbumData(currOperationTagId + "", REFRESH);
                        }
                    }
                    break;
                case 4:
                    if (currOperationTagId != categoryList.get(msg.arg1).operationTagId) {
                        currOperationTagId = categoryList.get(msg.arg1).operationTagId;
                        if (cacheMap != null && cacheMap.containsKey(currOperationTagId)) {
                            com.ppfuns.model.entity.Response<PageContent<List<AlbumContents>>> bean = cacheMap.get(currOperationTagId);
                            if (bean != null) {
                                updateGridView(bean, REFRESH);
                            }
                        } else {
                            getAlbumData(channelId + "", REFRESH);
                        }
                    }

                    break;
            }
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            showLoadingProgressDialog("");
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_search:
                Intent intent = new Intent(this, SearchActivity.class);
                intent.putExtra(AlbumActivity.COL_CONTENT_ID, vodColumnId);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogUtils.i(TAG,"dispatchKeyEvent: "+ event.getAction());
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (gridview != null && gridview.hasFocus() && gridview.getSelectedItemPosition() > (gridColumn - 1)) {
                    gridview.setFromBack(true);
                    gridview.setSelection(0);
                    ToastUtils.showShort(this, "再按一次退出页面");
                } else {
                    finish();
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
