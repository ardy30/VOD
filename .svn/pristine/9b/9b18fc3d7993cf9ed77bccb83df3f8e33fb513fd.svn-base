package com.ppfuns.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.ppfuns.model.adapter.FilterContentAdapter;
import com.ppfuns.model.adapter.FilterTitleAdapter;
import com.ppfuns.model.adapter.FilterContentAdapter.FilterActionListener;
import com.ppfuns.model.entity.PlayUrlBean;
import com.ppfuns.util.LogUtils;
import com.ppfuns.vod.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hmc on 2016/9/5.
 */
public class FilterMenuLayout extends RelativeLayout{

    Context mContext;
    private RecyclerView mTitleRecyclerView;
    private RecyclerView mContentRecyclerView;

    public FilterMenuLayout(Context context) {
        super(context);
        mContext = context;
        initUI();
    }

    public FilterMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FilterMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initUI() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_filter_menu, this, true);
        mTitleRecyclerView = (RecyclerView) findViewById(R.id.rv_title);
        mContentRecyclerView = (RecyclerView) findViewById(R.id.rv_content);

    }

    public void setFilterMenuView(FilterActionListener listener, List<String> titleList, List<PlayUrlBean> playUrlList) {
        setTitleView(titleList);
        setDefinitionView(listener, playUrlList);
    }

//    public void setFilterMenuView(String title, String playUrl) {
//        List<PlayUrlBean> mPlayUrlList =
//    }

    public void setTitleView(List<String> titleList) {
        FilterTitleAdapter titleAdapter = new FilterTitleAdapter(mContext, titleList, FilterTitleAdapter.TITLE_FLAG);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mTitleRecyclerView.setLayoutManager(manager);
        mTitleRecyclerView.setAdapter(titleAdapter);
    }

    public void setDefinitionView(FilterActionListener listener, List<PlayUrlBean> playUrlList) {
        if (playUrlList == null || playUrlList.size() <= 0 ) {
            return;
        }
        List<String> list = new ArrayList<>();
        int size = playUrlList.size();
        for (int i = 0; i < size; i++) {
            PlayUrlBean bean = playUrlList.get(i);
            if (bean != null) {
                String def = bean.codingGradeName;
                if (!TextUtils.isEmpty(def)) {
                    list.add(def);
                }
            }
        }
        FilterContentAdapter adapter = new FilterContentAdapter(mContext, list, FilterContentAdapter.CONTENT_FLAG);
        adapter.setOnFilterActionListener(listener);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mContentRecyclerView.setLayoutManager(manager);
        mContentRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        LogUtils.i("Decode", "filter_menu_onFocusChanged: " + gainFocus);
        if (gainFocus) {
            View view = mContentRecyclerView.getChildAt(0);
            if (view != null) {
//                view.setFocusable(true);
                view.requestFocus();
            }
//            mContentRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    mContentRecyclerView.getChildAt(0).requestFocus();
//                }
//            });

        }
    }

//    public FilterTitleAdapter getFilterTitleAdapter() {
//        return
//    }
}
