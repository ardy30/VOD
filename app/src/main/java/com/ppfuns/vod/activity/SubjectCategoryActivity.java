package com.ppfuns.vod.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ppfuns.model.adapter.SubjectAdapter;
import com.ppfuns.model.entity.Subjects;
import com.ppfuns.ui.view.EffectNoDrawBridge;
import com.ppfuns.ui.view.MainUpView;
import com.ppfuns.ui.view.TwoWayAdapterView;
import com.ppfuns.ui.view.TwoWayGridView;
import com.ppfuns.util.LogUtils;
import com.ppfuns.util.https.ContractUrl;
import com.ppfuns.util.https.HttpUtil;
import com.ppfuns.vod.R;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.callback.Callback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by fangyuan on 2016/6/17.
 */
public class SubjectCategoryActivity extends BaseActivity implements View.OnFocusChangeListener {

    @BindView(R.id.gridview)
    TwoWayGridView gridview;
    @BindView(R.id.albumNumbers)
    TextView albumNumbers;
    @BindView(R.id.pageIndex)
    TextView pageIndex;
    @BindView(R.id.pageIndex2)
    TextView pageIndex2;
    @BindView(R.id.go_left)
    ImageView go_left;
    @BindView(R.id.go_right)
    ImageView go_right;
    @BindView(R.id.mainUpView1)
    MainUpView mainUpView1;

    private List<Subjects> totalSubjectList;

    private SubjectAdapter subjectAdapter;
    private EffectNoDrawBridge bridget;
    private int selected;
    private boolean isLeft = false;
    private boolean isRinght = false;
    private int totalSize;
    private int scrollDistance; //滑三列

    private long keydownTime = 0;
    private long spaceTime = 200; //连续按键间隔时间限制
    private String mPlatform = "0"; //平台 "0" 机顶盒 "1"
    public String subjectUrl = ContractUrl.getVodUrl()  + "/r/subject/"; //专题详情
    private String oldData = "";
    private String TAG = "SubjectCategoryActivity";
    private boolean isFirst = true;
    private int oldSelected = -1;
    private int dalenTime = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_grid2);
        ButterKnife.bind(this);
        totalSubjectList = new ArrayList<>();
        scrollDistance = getResources().getDimensionPixelSize(R.dimen.subject_scrolldestance);
        gridview.setNextFocusDownId(R.id.gridview);
        gridview.setNextFocusUpId(R.id.gridview);
        gridview.setNextFocusLeftId(R.id.gridview);
        gridview.setNextFocusRightId(R.id.gridview);
        initView();
        initGridFous();
        getAlbumData(0, vodColumnId);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpUtil.cancelTag(SubjectCategoryActivity.this);
    }

    @Override
    public void reLoad() {
        super.reLoad();
        getAlbumData(0, vodColumnId);
    }

    private void initView() {
        int scrollX = getResources().getDimensionPixelSize(R.dimen.twoWayGridView_map_bottom);
        int padding = getResources().getDimensionPixelSize(R.dimen.focus_padding);
        mainUpView1.setEffectBridge(new EffectNoDrawBridge());
        bridget = (EffectNoDrawBridge) mainUpView1.getEffectBridge();
        //动画时间
        bridget.setTranDurAnimTime(0);
        bridget.setVisibleWidget(true);
        // 设置移动边框的图片.
        mainUpView1.setUpRectResource(R.drawable.main_focus_2);
        // 移动方框缩小的距离.
        mainUpView1.setDrawUpRectPadding(new Rect(padding, padding, padding, scrollX));

        subjectAdapter = new SubjectAdapter(this, totalSubjectList, imageLoader);
        gridview.setAdapter(subjectAdapter);

    }

    //初始化gridview动画等问题
    private void initGridFous() {
        albumNumbers.setFocusable(false);
        pageIndex.setFocusable(false);
        pageIndex2.setFocusable(false);
        go_left.setFocusable(false);
        go_right.setFocusable(false);

        go_right.setNextFocusDownId(R.id.gridview);
        go_right.setNextFocusUpId(R.id.gridview);
        go_right.setNextFocusLeftId(R.id.gridview);
        go_right.setNextFocusRightId(R.id.gridview);

        gridview.setOnItemClickListener(new TwoWayAdapterView.OnItemClickListener() {
            public void onItemClick(TwoWayAdapterView parent, View v, int position, long id) {
                if (!totalSubjectList.get(position).isLost) {
                    Intent intent = new Intent(SubjectCategoryActivity.this, SubjectDetailsActivity.class);
                    intent.putExtra("subjectDetails", totalSubjectList.get(position));
                    intent.putExtra(AlbumActivity.COL_CONTENT_ID, vodColumnId);
                    SubjectCategoryActivity.this.startActivity(intent);
                }
            }
        });

        gridview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    gridview.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bridget.setVisibleWidget(false);
                        }
                    }, 50);
                } else {
                    bridget.setVisibleWidget(true);
                }
            }
        });

        gridview.setOnItemSelectedListener(new TwoWayAdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(TwoWayAdapterView<?> parent, final View view, int position, long id) {
                LogUtils.i(TAG, "onItemSelected: " + position);
                if (view != null) {
                    if(!totalSubjectList.get(position).isLost()){
                        if (isLeft) {
                            isLeft = false;
                            bridget.setVisibleWidget(true);
                            gridview.smoothScrollBy(-scrollDistance, 0);
                            LogUtils.i(TAG, "scrollDistanceLeft: " + -scrollDistance);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    bridget.setTranDurAnimTime(0);
                                    mainUpView1.setFocusView(view, mOldView, 1.0f);
                                }
                            }, 50);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    bridget.setVisibleWidget(false);
                                }
                            }, 100);
                        } else {
                            if (isFirst) {
                                bridget.setVisibleWidget(true);
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        bridget.setVisibleWidget(false);
                                        isFirst = false;
                                    }
                                }, 200);
                            }
                            bridget.setTranDurAnimTime(200);
                            mainUpView1.setFocusView(view, mOldView, 1.0f);
                        }
                        if (isRinght) {
                            initCount(totalSize, selected);
                            isRinght = false;
                            bridget.setVisibleWidget(true);
                            gridview.smoothScrollBy(scrollDistance, 0);
                            LogUtils.i(TAG, "scrollDistanceRight: " + scrollDistance);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    bridget.setTranDurAnimTime(0);
                                    mainUpView1.setFocusView(view, mOldView, 1.0f);
                                }
                            }, 50);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    bridget.setVisibleWidget(false);
                                }
                            }, 100);
                        } else {
                            if (isFirst) {
                                bridget.setVisibleWidget(true);
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        bridget.setVisibleWidget(false);
                                        isFirst = false;
                                    }
                                }, 200);
                            }
                            bridget.setTranDurAnimTime(200);
                            mainUpView1.setFocusView(view, mOldView, 1.0f);
                        }
                        mOldView = view;
                        selected = position;
                        oldSelected = position;
                        initCount(totalSize, selected);
                    }else{
                        //bridget.setVisibleWidget(true);
                        if(oldSelected!=-1){
                            int currPageIndex = oldSelected / 6 + 1;
                            int totalPage = 0;
                            if(totalSize%6==0){
                                totalPage = totalSize/6;
                            }else{
                                totalPage = totalSize/6 + 1;
                            }
                            if(currPageIndex==totalPage){
                                gridview.setSelection(oldSelected);
                            }else{
                                oldSelected = (totalPage-1)*6;
                                gridview.setSelection(oldSelected);
                            }
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(TwoWayAdapterView<?> parent) {

            }
        });

        gridview.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                boolean result = false;
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN ||
                        keyCode == KeyEvent.KEYCODE_DPAD_UP ||
                        keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
                        keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    if (isOk()) {
                        result = false;
                        LogUtils.i(TAG, "keyCode: " + keyCode);
                        if (isAllPageLeft() && keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                                && event.getAction() == KeyEvent.ACTION_DOWN) {
                            isLeft = true;
                        }
                        if (isAllPageRight() && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                                && event.getAction() == KeyEvent.ACTION_DOWN) {
                            //scrollDistance = calculationDestance();
                            calculationDestance();
                            isRinght = true;
                        }
                    } else {
                        result = true;
                    }
                }
                return result;
            }
        });
    }

    //按键时间间隔是否合法
    private boolean isOk() {
        if ((System.currentTimeMillis() - keydownTime) > spaceTime) {
            keydownTime = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }

    private boolean isAllPageLeft() {
        int rawNum = totalSubjectList.size() / 6;
        for (int i = 1; i <= rawNum; i++) {
            if (selected == 6 * i) {
                return true;
            }
            if (selected == 6 * i + 1) {
                return true;
            }
        }
        return false;
    }

    private boolean isAllPageRight() {
        int rawNum = 0;
        if (totalSize % 6 == 0) {
            rawNum = totalSize / 6;
        } else {
            rawNum = totalSize / 6 + 1;
        }
        for (int i = 1; i < rawNum; i++) {
            if (selected == 6 * i - 1) {
                return true;
            }
            if (selected == 6 * i - 2) {
                return true;
            }
        }
        return false;
    }

    private boolean iscalculationDestance = false;

    private void calculationDestance(){
        iscalculationDestance = false;
        int surplus = totalSize-(selected+1);
        //scrollDistance = getResources().getDimensionPixelSize(R.dimen.subject_scrolldestance);
        if(surplus>0&&surplus<3){
            //一行
            //scrollDistance = getResources().getDimensionPixelSize(R.dimen.subject_destance1);
            iscalculationDestance = true;
        }else if(surplus>2&&surplus<5){
            //两行
            //scrollDistance = getResources().getDimensionPixelSize(R.dimen.subject_destance2);
            iscalculationDestance = true;
        }
    }

    int totalPage = 0;
    //显示多少影片，多少页等
    private void initCount(int totalSubject, int selected) {
        LogUtils.i(TAG,"totalSubject: "+ totalSubject + " selected: "+ selected);
        albumNumbers.setText("(" + totalSubject + "个)");
        if (selected < totalSubject) {
            pageIndex.setText(String.valueOf(selected / 6 + 1));
        }
        if (totalSubject % 6 == 0) {
            pageIndex2.setText("/" + (totalSubject / 6));
            totalPage = totalSubject / 6;
        } else {
            pageIndex2.setText("/" + (totalSubject / 6 + 1));
            totalPage = totalSubject / 6 + 1;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {


        }
    }

    //获取专题等显示内容（grid）
    private void getAlbumData(int subjectId, int col_contentId) {
        handler.sendEmptyMessageDelayed(1, dalenTime);
        String url = subjectUrl + subjectId + "-" + col_contentId + "-" + mPlatform + ".json";
        HttpUtil.getHttpHtml(url, SubjectCategoryActivity.this, new Callback<com.ppfuns.model.entity.Response<List<Subjects>>>() {
            @Override
            public com.ppfuns.model.entity.Response<List<Subjects>> parseNetworkResponse(Response response, int id) throws Exception {
                String htmlStr = response.body().string();
                if (oldData.equals(htmlStr)) {
                    return null;
                }
                oldData = htmlStr;
                Gson gson = new Gson();
                com.ppfuns.model.entity.Response<List<Subjects>> bean = gson.fromJson(htmlStr, new TypeToken<com.ppfuns.model.entity.Response<List<Subjects>>>() {
                }.getType());
                return bean;
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                handler.removeMessages(1);
                dissmissLoadingDialog();
                showDialogTips(getString(R.string.data_error));
                //ToastUtils.showShort(SubjectCategoryActivity.this, "error");
            }

            @Override
            public void onResponse(com.ppfuns.model.entity.Response<List<Subjects>> response, int id) {
                handler.removeMessages(1);
                dissmissLoadingDialog();
                com.ppfuns.model.entity.Response<List<Subjects>> bean = (com.ppfuns.model.entity.Response<List<Subjects>>) response;

                if (bean != null &&
                        bean.data != null &&
                        bean.data.result != null) {
                    if (bean.data.result != null &&
                            bean.data.result.size() > 0) {
//                        totalSubjectList.clear();
//                        subjectAdapter.notifyDataSetChanged();
                        totalSubjectList.addAll(bean.data.result);
//                        for(int i=0;i<4;i++){
//                            totalSubjectList.add(bean.data.result.get(i));
//                        }
//                        LogUtils.i(TAG,"totalSize1: "+ totalSize);
                        totalSize = totalSubjectList.size();
                        LogUtils.i(TAG,"totalSize2: "+ totalSize);
                        initCount(totalSubjectList.size(), 0);
                        if (totalSubjectList.size() % 6 > 0) {
                            int rest = 6 - totalSubjectList.size() % 6;
                            if (rest > 0) {
                                for (int i = 0; i < rest; i++) {
                                    Subjects item = new Subjects();
                                    item.subjectName = "";
                                    item.subjectPosters = "";
                                    item.isLost = true;
                                    totalSubjectList.add(item);
                                }
                            }
                        }

                        subjectAdapter.notifyDataSetChanged();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                gridview.setSelection(0);
                            }
                        }, 100);
                    }
                }
            }
        });
    }

    //分页
    private View mOldView;


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

}
