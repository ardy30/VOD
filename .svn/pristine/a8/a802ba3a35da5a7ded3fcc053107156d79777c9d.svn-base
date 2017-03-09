package com.ppfuns.vod.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ppfuns.model.adapter.VoicePlayListAdapter;
import com.ppfuns.model.entity.PageContent;
import com.ppfuns.model.entity.Response;
import com.ppfuns.model.entity.Search;
import com.ppfuns.ui.view.GridLayoutManagerTV;
import com.ppfuns.ui.view.MainUpView;
import com.ppfuns.ui.view.OnChildSelectedListener;
import com.ppfuns.ui.view.RecyclerViewBridge;
import com.ppfuns.ui.view.RecyclerViewTV;
import com.ppfuns.util.LogUtils;
import com.ppfuns.vod.BaseApplication;
import com.ppfuns.vod.R;

import java.util.ArrayList;
import java.util.List;

import static com.ppfuns.util.JsonUtils.fromJson;

/**
 * 创建者     庄丰泽
 * 创建时间   2016/9/1 17:15
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class VoicePlayListActivtiy extends BaseActivity {
    private static final String TAG = "VoicePlayListActivtiy";


    private VoicePlayListAdapter mVoicePlayListAdapter;
    private GridLayoutManagerTV  mGridLayoutManager;
    private RecyclerViewTV       mSearchPosterRvData;

    private MainUpView         mMainUpView;
    private RecyclerViewBridge mOpenEffectBridge;
    private String             mColumnId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voiceplaylist);
        initConf();
        initMainUpWindow();

        float dimension = getResources().getDimension(R.dimen.album_width);

        LogUtils.d(TAG + "111", dimension + "");

        initData();

        initEvent();
    }

    private void initMainUpWindow() {
        mSearchPosterRvData = (RecyclerViewTV) findViewById(R.id.voiceplaylist_datas);

        mMainUpView = (MainUpView) findViewById(R.id.voiceplaylist_mainupview);
        mMainUpView.setEffectBridge(new RecyclerViewBridge());

        mOpenEffectBridge = (RecyclerViewBridge) mMainUpView.getEffectBridge();

        //设置边框
        mOpenEffectBridge.setUpRectResource(R.drawable.search_focus);
        mOpenEffectBridge.setTranDurAnimTime(300);
        final int padding = (int) getResources().getDimension(R.dimen.voice_mainupwindow_padding);

        mMainUpView.setDrawUpRectPadding(new Rect((int) (getResources().getDimension(R.dimen.voice_mainupwindow_padding) / 1.5),
                padding,
                (int) (getResources().getDimension(R.dimen.voice_mainupwindow_padding) / 2.5),
                padding / 2));
    }

    private void initEvent() {

    }


    private List<Search> mPageContent;

    private void initData() {

        Intent intent = getIntent();
        if (intent != null) {
            String response = intent.getStringExtra("response");
            if (response != null) {
                LogUtils.d(TAG, response);
                Response<PageContent<List<Search>>> searchBean = (Response<PageContent<List<Search>>>) fromJson(response, new TypeToken<Response<PageContent<List<Search>>>>() {
                }.getType());
                mPageContent = new ArrayList<>();
                mPageContent.clear();
                mPageContent.addAll(searchBean.data.result.pageContent);
                setGridAdapter();
            }
        }

    }

    private void setGridAdapter() {

        if (mVoicePlayListAdapter == null) {

            mVoicePlayListAdapter = new VoicePlayListAdapter(this, mPageContent);

            if (mPageContent.size() < 6) {
                mGridLayoutManager = new GridLayoutManagerTV(this, mPageContent.size());
            } else {
                mGridLayoutManager = new GridLayoutManagerTV(this, 6);
            }
            mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            mGridLayoutManager.setOnChildSelectedListener(new OnChildSelectedListener() {
                @Override
                public void onChildSelected(RecyclerView parent, View view, int position, int dy) {
                    super.onChildSelected(parent, view, position, dy);
                    mOpenEffectBridge.setFocusView(view, 1);
                }
            });

            mSearchPosterRvData.setLayoutManager(mGridLayoutManager);
            mSearchPosterRvData.setAdapter(mVoicePlayListAdapter);

            mVoicePlayListAdapter.setOnItemClickListener(new VoicePlayListAdapter.OnItemClickListener() {
                @Override
                public void OnClick(View view, int position) {
                    openItem(position);
                }
            });

        } else {
            mVoicePlayListAdapter.notifyDataSetChanged();
        }
        Toast.makeText(this, "请选择需要观看的影片", Toast.LENGTH_SHORT).show();
    }

    private void openItem(int position) {
        Integer contentId = mPageContent.get(position).contentId;
        Integer cpId = mPageContent.get(position).cpId;

        if (contentId == null || contentId == 0) {
            showDialogTips(getResources().getString(R.string.data_error));
            return;
        }

        String isAlbum = mPageContent.get(position).isAlbum + "";
        if (TextUtils.isEmpty(isAlbum)) {
            showDialogTips(getResources().getString(R.string.data_error));
            return;
        }

        if (isAlbum.equals(TYPE_ALBUM)) {
            Player(contentId + "",cpId+"");
        } else if (isAlbum.equals(TYPE_SPECIAL)) {
            startActivity(
                    new Intent(this, SubjectDetailsActivity.class)
                            .putExtra("subjectId", contentId));

        } else {

            showDialogTips(getResources().getString(R.string.data_error));
        }

    }

    /**
     * 调用播放器
     */
    private void Player(String albumId, String cpId) {
        Intent intent = new Intent();
        intent.setAction("com.ppfuns.vod.action.ACTION_VIEW_VOD_PLAY");
        intent.putExtra("albumId", albumId);
        intent.putExtra("index", 1);
        intent.putExtra("col_contentId", cpId);
        intent.putExtra("timePosition", 0);
        startActivity(intent);
    }


    private void initConf() {
        //获取栏目id
        if (BaseApplication.getmColumnId() != -1) {
            mColumnId = BaseApplication.getmColumnId() + "";
        } else {
            mColumnId = "20";
        }
    }


    //0为专辑  1为专题 2为频道
    private static final String TYPE_ALBUM   = "0";
    private static final String TYPE_SPECIAL = "1";
}
