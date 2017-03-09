package com.ppfuns.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.ppfuns.model.adapter.SearchGridAdapter;
import com.ppfuns.model.adapter.SearchListAdapter;
import com.ppfuns.model.entity.PageContent;
import com.ppfuns.model.entity.Response;
import com.ppfuns.model.entity.Search;
import com.ppfuns.model.entity.SubjectContent;
import com.ppfuns.ui.view.LinearLayoutManagerTV;
import com.ppfuns.ui.view.SearchGridLayoutManagerTV;
import com.ppfuns.ui.view.SearchListLayoutManagerTV;
import com.ppfuns.ui.view.recycle.RecyclerViewTV;
import com.ppfuns.util.LogUtils;
import com.ppfuns.util.SysUtils;
import com.ppfuns.util.eventbus.EventBusUtil;
import com.ppfuns.util.eventbus.EventConf;
import com.ppfuns.util.eventbus.InfoEvent;
import com.ppfuns.util.https.ContractUrl;
import com.ppfuns.util.https.HttpUtil;
import com.ppfuns.vod.BaseApplication;
import com.ppfuns.vod.R;
import com.ppfuns.vod.activity.ARdetailActivity;
import com.ppfuns.vod.activity.AlbumActivity;
import com.ppfuns.vod.activity.SearchActivity;
import com.ppfuns.vod.activity.SubjectDetailsActivity;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import static com.ppfuns.util.JsonUtils.fromJson;

/**
 * 创建者     庄丰泽
 * 创建时间   2016/6/29 14:22
 * 描述	      搜索界面内容区域的fragment
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ContentFragment extends Fragment {
    private static final String TAG = "ContentFragment";

    private Handler mHandler = new Handler();

    private SearchActivity mActivity;

    private TextView mTitle;
    private TextView mCount;

    public  RecyclerViewTV            mRecyclerViewList;
    private SearchListLayoutManagerTV mLinearLayoutManager;
    private SearchListAdapter         mSearchListAdapter;

    private RecyclerViewTV            mRecyclerViewGrid;
    private SearchGridLayoutManagerTV mGridLayoutManager;
    private SearchGridAdapter         mSearchGridAdapter;


    //搜索界面数据的封装

    private Response<PageContent<List<Search>>> mSearchBean;

    private List<Search> mPageContent;

    //0为专辑  1为专题 2为频道
    private static final String TYPE_ALBUM   = "0";
    private static final String TYPE_SPECIAL = "1";

    private static final String SEARCH_ALL = "all";

    //搜索接口
    private String SEARCH_URL = ContractUrl.getVodUrl() + "/r/so/";
    //关键字长度
    private int mKeySize;
    //关键字
    private String mKey         = "";
    //当前页
    private int    mCurrentPage = 1;
    //总页
    private int    mPageCount   = -1;

    //栏目id
    private String mColumnId;
    //平台 :"0" 机顶盒 ,"1" 手机
    private String mPlatform;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (SearchActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return rootView();
    }


    private View rootView() {

        View rootView = View.inflate(getActivity(), R.layout.fragment_search_center, null);


        mRecyclerViewList = (RecyclerViewTV) rootView.findViewById(R.id.search_center_rv_data);


        mRecyclerViewGrid = (RecyclerViewTV) rootView.findViewById(R.id.search_poster_rv_data);

        mRecyclerViewGrid.setNextFocusUpId(R.id.search_poster_rv_data);
        mRecyclerViewGrid.setNextFocusDownId(R.id.search_poster_rv_data);

        mCount = (TextView) rootView.findViewById(R.id.search_tv_count);
        mTitle = (TextView) rootView.findViewById(R.id.search_tv_title);


        mRecyclerViewList.setVisibility(View.INVISIBLE);
        mRecyclerViewGrid.setVisibility(View.INVISIBLE);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();

        initEvent();


    }

    /**
     * 初始化数据
     */
    private void initData() {

        initConf();


        //        mSearchBean = new SearchBean();
        mSearchBean = new Response<>();
        mPageContent = new ArrayList<>();

        requestDataFromNet();

        mTitle.setText(R.string.search_commend);

    }

    /**
     * 获取应用配置相关 栏目id 平台类型
     */
    private void initConf() {
        //获取栏目id
        //        mColumnId = mActivity.getVodColumnId() + "";
        mColumnId = "";
        Intent intent = mActivity.getIntent();
        if (intent != null) {
            String searchType = intent.getStringExtra("searchType");
            if (!TextUtils.equals(searchType, SEARCH_ALL)) {
                int cpId = intent.getIntExtra("col_contentId", -1);
                if (cpId == -1) {
                    mColumnId = SysUtils.getColumnId("");
                } else {
                    mColumnId = cpId + "";
                }
            }
        }
        LogUtils.d(TAG, "cpId: " + mColumnId);
        //        if (TextUtils.equals(mColumnId, "0")) {
        //            mColumnId = "";
        //        }
        //        if (BaseApplication.getmColumnId() != -1) {
        //            mColumnId = getColumnId("") + "";
        //        } else {
        //            mColumnId = "20";
        //        }

        //获取平台
        mPlatform = "0";
    }

    private int loadCount = 100;

    /**
     * 请求网络
     */
    private void requestDataFromNet() {
        mCurrentPage = 1;
        mPageCount = -1;
        mActivity.showLoadingProgressDialog("");


        String keyWord = String.format(getString(R.string.keyword), mCurrentPage, loadCount + "", "", "",
                mColumnId, mKey, mPlatform);

        String searchUrl = SEARCH_URL + keyWord + ".json";

        HttpUtil.getHttpHtml(searchUrl, new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

                mActivity.dissmissLoadingDialog();
                mActivity.showDialogTips(getResources().getString(R.string.data_error));
            }

            @Override
            public void onResponse(String response, int id) {

                //                mSearchBean = JsonUtils.fromJson(response, SearchBean.class);
                mSearchBean = (Response<PageContent<List<Search>>>) fromJson(response, new TypeToken<Response<PageContent<List<Search>>>>() {
                }.getType());

                mPageContent.clear();
                if (mSearchBean != null && mSearchBean.data.result != null) {
                    List<Search> pageContent = mSearchBean.data.result.pageContent;
                    if (pageContent != null && pageContent.size() != 0) {
                        mPageContent.addAll(pageContent);
                    }
                    mPageCount = mSearchBean.data.result.pageCount;

                }

                UpdateUI();


            }
        });


    }

    /**
     * 更新Ui
     */
    private void UpdateUI() {
        mCount.setText(String.format(getString(R.string.search_all_result), 0));
        //设置数量
        if (mSearchBean != null && mSearchBean.data.result != null) {
            Integer recCount = mSearchBean.data.result.recCount;
            if (recCount != null) {
                mCount.setText(String.format(getString(R.string.search_all_result), recCount));
            }
        }


        //判断列表和内容区域是否可以获取焦点
        if (mPageContent.size() == 0) {
            mRecyclerViewList.setVisibility(View.INVISIBLE);
            mRecyclerViewGrid.setVisibility(View.INVISIBLE);
        } else {
            mRecyclerViewList.setVisibility(View.VISIBLE);
            mRecyclerViewGrid.setVisibility(View.VISIBLE);

            //有数据 滚动回列表与内容顶部
            if (mRecyclerViewList.getChildAt(0) != null)
                mRecyclerViewList.getLayoutManager().scrollToPosition(0);

            if (mRecyclerViewGrid.getChildAt(0) != null)
                mRecyclerViewGrid.getLayoutManager().scrollToPosition(0);
        }


        //设置列表和内容
        setListAdapter();
        setGridAdapter();

        //数据请求完成 关闭菊花
        mActivity.dissmissLoadingDialog();

    }

    /**
     * 设置或更新列表
     */
    public void setListAdapter() {
        if (mSearchListAdapter == null) {

            mSearchListAdapter = new SearchListAdapter(getActivity(), mPageContent, mKeySize, mKey);


            mLinearLayoutManager = new SearchListLayoutManagerTV(getActivity());
            mLinearLayoutManager.setOrientation(LinearLayoutManagerTV.VERTICAL);


            mRecyclerViewList.setLayoutManager(mLinearLayoutManager);
            mRecyclerViewList.setAdapter(mSearchListAdapter);

            mSearchListAdapter.setOnItemClickListener(new SearchListAdapter.OnItemClickListener() {
                @Override
                public void OnClick(View view, int position) {
                    //                    Toast.makeText(mActivity, "1111", Toast.LENGTH_SHORT).show();
                    openItem(position);
                }
            });
            mSearchListAdapter.setOnLoadMoreDataListener(new SearchListAdapter.OnLoadMoreDataListener() {
                @Override
                public void loadData(View focusView, int postion) {
                    loadMoreDataFromNet();
                }
            });


        } else {
            mSearchListAdapter.upDateAdapter(mKeySize, mKey);
        }


    }

    /**
     * 设置或更新右边的内容
     */
    private void setGridAdapter() {

        if (mSearchGridAdapter == null) {

            mSearchGridAdapter = new SearchGridAdapter(mActivity, mPageContent);

            mGridLayoutManager = new SearchGridLayoutManagerTV(getContext(), 2);

            if (mPageContent.size() == 1) {
                mGridLayoutManager.setSpanCount(1);
            } else {
                mGridLayoutManager.setSpanCount(2);

            }
            mRecyclerViewGrid.setLayoutManager(mGridLayoutManager);
            mRecyclerViewGrid.setAdapter(mSearchGridAdapter);

            mSearchGridAdapter.setOnItemClickListener(new SearchGridAdapter.OnItemClickListener() {
                @Override
                public void OnClick(View view, int position) {

                    openItem(position);
                }
            });

            mSearchGridAdapter.setOnLoadMoreDataListener(new SearchGridAdapter.OnLoadMoreDataListener() {
                @Override
                public void LoadData(View focusView, int postion) {
                    loadMoreDataFromNet();
                }
            });

        } else {

            SearchGridLayoutManagerTV gridLayoutManagerTV = (SearchGridLayoutManagerTV) mRecyclerViewGrid.getLayoutManager();
            if (mPageContent.size() == 1) {
                gridLayoutManagerTV.setSpanCount(1);
            } else {
                gridLayoutManagerTV.setSpanCount(2);

            }
            mRecyclerViewGrid.setLayoutManager(gridLayoutManagerTV);
            mSearchGridAdapter.notifyDataSetChanged();
        }

    }

    private void openItem(int position) {
        Integer contentId = mPageContent.get(position).contentId;

        if (contentId == null || contentId == 0) {
            mActivity.showDialogTips(getResources().getString(R.string.data_error));
            return;
        }

        String isAlbum = mPageContent.get(position).isAlbum + "";
        if (TextUtils.isEmpty(isAlbum)) {
            mActivity.showDialogTips(getResources().getString(R.string.data_error));
            return;
        }

        String contentType = mPageContent.get(position).contentType;

        if (TextUtils.isEmpty(contentType)) {
            mActivity.showDialogTips(getResources().getString(R.string.data_error));
            return;
        }
        int cpId = SysUtils.getColumnId();
        try {
            cpId = Integer.valueOf(mColumnId);
        } catch (NumberFormatException e) {
            LogUtils.e(TAG, e.getMessage());
            cpId = mPageContent.get(position).cpId;
        }
        LogUtils.d(TAG, "cpId: " + cpId);
        if (isAlbum.equals(TYPE_ALBUM)) {
            if (contentType.equals(CONTENT_MOVIE) || contentType.equals(CONTENT_TVPLAY) || contentType.equals(CONTENT_MV) || contentType.equals(CONTENT_VR)) {
                startActivity(
                        new Intent(getActivity(), AlbumActivity.class)
                                .putExtra(AlbumActivity.ALBUM_ID, contentId)
                                .putExtra(AlbumActivity.COL_CONTENT_ID, cpId));
            } else if (contentType.equals(CONTENT_AR)) {
                String posUrl = mPageContent.get(position).picUrl;
                if (!TextUtils.isEmpty(posUrl)) {
                    SubjectContent subjectContent = new SubjectContent();
                    subjectContent.contentPosters = posUrl;
                    ArrayList<SubjectContent> sList = new ArrayList<>();
                    sList.add(subjectContent);
                    Intent bIntent = new Intent(mActivity, ARdetailActivity.class);
                    bIntent.putExtra(ARdetailActivity.LIST, sList);
                    bIntent.putExtra(AlbumActivity.COL_CONTENT_ID, cpId);
                    startActivity(bIntent);
                }
            } else {
                mActivity.showDialogTips(getResources().getString(R.string.data_error));
            }
        } else if (isAlbum.equals(TYPE_SPECIAL)) {
            startActivity(
                    new Intent(getActivity(), SubjectDetailsActivity.class)
                            .putExtra("subjectId", contentId).putExtra(AlbumActivity.COL_CONTENT_ID, cpId));

        } else {

            mActivity.showDialogTips(getResources().getString(R.string.data_error));
        }

    }

    //contentType: 内容类型 1是电影  2是电视剧  3是MV  4 VR  5 AR  99 其他
    private static final String CONTENT_MOVIE  = "1";
    private static final String CONTENT_TVPLAY = "2";
    private static final String CONTENT_MV     = "3";
    private static final String CONTENT_VR     = "4";
    private static final String CONTENT_AR     = "5";

    private void initEvent() {

        //列表的焦点事件处理 滚动时 自己处理 非滚动时 交给Activity去处理
        mRecyclerViewList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int mLastItem;//用于记录每次滚动后 界面可视的最后一个View

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //滚动记录索引
                mLastItem = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //滚动时
                if (newState == RecyclerView.SCROLL_STATE_SETTLING || newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    mActivity.isScrollOver = false;
                    mActivity.mRecyclerViewBridge.setTranDurAnimTime(0);
                }

                //滚动时的焦点动画
                mActivity.mRecyclerViewBridge.setFocusView(recyclerView.getFocusedChild(),
                        mActivity.mOldView, 1.0f);

                //滚动完成
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mActivity.isScrollOver = true;
                    mActivity.mRecyclerViewBridge.setTranDurAnimTime(300);
                }
            }
        });

        //内容的焦点事件处理 滚动时 自己处理 非滚动时 交给Activity去处理
        mRecyclerViewGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private View mChildView;
            private int mLastItem;//用于记录每次滚动后 界面可视的最后一个View

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LogUtils.d(TAG, "onScrollStateChanged focusDirection: " + mSearchGridAdapter.mKeyCode);
                super.onScrollStateChanged(recyclerView, newState);

                mActivity.mHandler.removeCallbacks(mActivity.mRunnable);
                if (newState == RecyclerView.SCROLL_STATE_SETTLING || newState == RecyclerView.SCROLL_STATE_DRAGGING) {

                    mActivity.isScrollOver = false;
                    //  ImageLoader.getInstance().pause();
                    //                    mActivity.mRecyclerViewBridge.setTranDurAnimTime(100);
                    Glide.with(mActivity).onStop();
                    mChildView = recyclerView.getFocusedChild();
                    //                    if (mChildView != null) {
                    //                        LogUtils.d(TAG, "onScrollStateChanged 1111 : " + mChildView.getId());
                    //                        mChildView = mChildView.findViewById(R.id.search_rl_content);
                    //                        mActivity.mRecyclerViewBridge.setTranDurAnimTime(100);
                    //                        mActivity.mRecyclerViewBridge.setFocusView(mChildView, 1.0f);
                    //                    }
                    if (mSearchGridAdapter != null && mChildView != null) {
                        View view = null;
                        int position = recyclerView.getLayoutManager().getPosition(mChildView);
                        if (position % 2 == 0) {
                            if (mSearchGridAdapter.mKeyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                                view = mActivity.findViewById(R.id.rl_bottom_left);
                            } else {
                                view = mActivity.findViewById(R.id.rl_up_left);
                            }
                        } else {
                            if (mSearchGridAdapter.mKeyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                                view = mActivity.findViewById(R.id.rl_bottom_right);
                            } else {
                                view = mActivity.findViewById(R.id.rl_up_right);
                            }
                        }
                        if (view != null) {
                            mActivity.mRecyclerViewBridge.setTranDurAnimTime(100);
                            mActivity.mRecyclerViewBridge.setFocusView(view, 1.0f);
                        }
                    }

                } else {
                    Glide.with(mActivity).onStart();
                    mActivity.isScrollOver = true;
                    //                    mChildView = recyclerView.getFocusedChild();
                    //                    if (mChildView != null) {
                    //                        LogUtils.d(TAG, "onScrollStateChanged 2222: " + mChildView.getId());
                    //                        mChildView = mChildView.findViewById(R.id.search_rl_content);
                    //                        mActivity.mRecyclerViewBridge.setTranDurAnimTime(150);
                    //                        mActivity.mRecyclerViewBridge.setFocusView(mChildView, 1.0f);
                    //                    }

                    //ImageLoader.getInstance().resume();
                    //                    View view = mActivity.getCurrentFocus();
                    ////                    if (view != null) {
                    ////                        LogUtils.d(TAG, "childView onScrollStateChanged 111");
                    ////                        mActivity.mRecyclerViewBridge.setTranDurAnimTime(100);
                    ////                        mActivity.mRecyclerViewBridge.setFocusView(view, 1.0f);
                    ////                    }
                    //                    if (view.getId() == R.id.search_rl_content) {
                    //                        mActivity.mRecyclerViewBridge.setTranDurAnimTime(200);
                    //                        mActivity.mRecyclerViewBridge.setFocusView(view, 1.0f);
                    //                    } else {
                    //                        final View childView = view.findViewById(R.id.search_rl_content);
                    //                        mActivity.mRecyclerViewBridge.setTranDurAnimTime(200);
                    //                        mActivity.mRecyclerViewBridge.setFocusView(childView, 1.0f);
                    //                    }

                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //滚动记录索引
                mLastItem = mGridLayoutManager.findLastVisibleItemPosition();
            }

        });

    }

    private Runnable mLoding = new Runnable() {
        @Override
        public void run() {
            mActivity.showLoadingProgressDialog("");
        }
    };
    /**
     * 加载更多数据的方法
     */
    public void loadMoreDataFromNet() {
        if (mCurrentPage == mPageCount) {
            return;
        }

        mHandler.postDelayed(mLoding, 2000);


        //1.请求前
        mCurrentPage++;

        String keyWord = String.format(getString(R.string.keyword), mCurrentPage + "", loadCount + "", "",
                "", mColumnId, mKey, mPlatform);

        String searchUrl = SEARCH_URL + keyWord + ".json";

        HttpUtil.getHttpHtml(searchUrl, new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

                mActivity.dissmissLoadingDialog();
                mActivity.showDialogTips(getResources().getString(R.string.data_error));
            }

            @Override
            public void onResponse(String response, int id) {

                //                mSearchBean = JsonUtils.fromJson(response, SearchBean.class);
                mSearchBean = (Response<PageContent<List<Search>>>) fromJson(response, new TypeToken<Response<PageContent<List<Search>>>>() {
                }.getType());
                mPageContent.addAll(mSearchBean.data.result.pageContent);


                //2.数据请求完成
                mSearchListAdapter.notifyItemRangeInserted(mPageContent.size() - mSearchBean.data.result.pageContent.size(),
                        mSearchBean.data.result.pageContent.size());

                mSearchGridAdapter.notifyItemRangeInserted(mPageContent.size() - mSearchBean.data.result.pageContent.size(),
                        mSearchBean.data.result.pageContent.size());

                mHandler.removeCallbacks(mLoding);

                mActivity.dissmissLoadingDialog();

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBusUtil.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
    }

    @Subscribe
    public void callEvent(InfoEvent infoEvent) {
        //根据键盘录入字符 重新请求网络数据更新UI
        if (infoEvent.id == EventConf.SEND_MESSAGE_BY_KEYBOARD) {

            if (infoEvent.obj instanceof String) {

                mKey = (String) infoEvent.obj;
                mKeySize = mKey.length();
                loadCount = 100;
                mTitle.setText(R.string.search_result_title);
                if (TextUtils.isEmpty(mKey)) {
                    //清空记录
                    mPageContent.clear();
                    mSearchListAdapter.notifyDataSetChanged();
                    mSearchGridAdapter.notifyDataSetChanged();
                    mRecyclerViewList.setVisibility(View.INVISIBLE);
                    mRecyclerViewGrid.setVisibility(View.INVISIBLE);
                    mCount.setText("共0条结果");
                } else {
                    requestDataFromNet();
                }

            }
            //数据异常 重新加载
        } else if (infoEvent.id == EventConf.SEND_MESSAGE_BY_SEARCH_RELOAD) {
            loadCount = 100;
            requestDataFromNet();

        }
    }
}
