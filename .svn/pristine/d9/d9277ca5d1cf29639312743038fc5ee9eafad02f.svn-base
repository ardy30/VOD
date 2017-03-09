package com.ppfuns.vod.activity;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ppfuns.ui.fragment.ContentFragment;
import com.ppfuns.ui.fragment.KeyBoradFragment;
import com.ppfuns.ui.view.MainUpView;
import com.ppfuns.ui.view.RecyclerViewBridge;
import com.ppfuns.ui.view.SmoothHorizontalScrollView;
import com.ppfuns.util.LogUtils;
import com.ppfuns.util.SysUtils;
import com.ppfuns.util.eventbus.EventBusUtil;
import com.ppfuns.util.eventbus.EventConf;
import com.ppfuns.vod.R;
import com.umeng.analytics.MobclickAgent;

public class SearchActivity extends BaseActivity {
    private static final String TAG = "SearchActivity";


    private FragmentManager     mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private KeyBoradFragment    mKeyBoradFragment;//键盘
    private ContentFragment     mContentFragment;//列表和内容区域

    private RelativeLayout             mContent;//根view
    private SmoothHorizontalScrollView mSmoothHorizontalScrollView;
    private int                        mSmoothHorizontalScrollViewWidth;//scrollview的宽度

    public MainUpView         mMainUpView;
    public RecyclerViewBridge mRecyclerViewBridge;
    //失去焦点的view
    public View               mOldView;

    //记录列表和内容是否处于滚动状态
    public boolean isScrollOver = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mFragmentManager = getSupportFragmentManager();
        initKeyBoardFragment();
        initMainUpView();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initContentFragment();
            }
        }, 200);
        Intent intent = getIntent();

        if (intent != null) {

            String videoname = intent.getStringExtra("videoname");

            if (videoname != null) {
                LogUtils.d(TAG, videoname);
                EventBusUtil.postInfoEvent(EventConf.SEND_MESSAGE_BY_VOICE, videoname);
            }
        }
    }

    /**
     * 开启语音的方法
     */
    private void start() {
        if (SysUtils.getAppIsInstall("com.ppfuns.speech", this)) {
            Intent intent = new Intent("android.intent.action.Speech");

            intent.addCategory("android.intent.category.DEFAULT");

            intent.putExtra("VOD_SEARCH", true);

            startActivityForResult(intent, 1);
        } else {
            Toast.makeText(this, "尚未安装语音服务", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (data != null) {

                String action = data.getStringExtra("searchText");

                Log.d(TAG, action);
                EventBusUtil.postInfoEvent(EventConf.SEND_MESSAGE_BY_VOICE, action);
            }

        }
    }

    public View ivBack;

    private void initMainUpView() {
        final int bigdimension = (int) getResources().getDimension(R.dimen.search_mainupview_big_padding);
        final int smalldimension = (int) getResources().getDimension(R.dimen.search_mainupview_small_padding);


        mContent = (RelativeLayout) findViewById(R.id.search_root);
        mSmoothHorizontalScrollView = (SmoothHorizontalScrollView) findViewById(R.id.search_scrollview);

        mMainUpView = (MainUpView) findViewById(R.id.search_mainupview);
        mMainUpView.setEffectBridge(new RecyclerViewBridge());
        mRecyclerViewBridge = (RecyclerViewBridge) mMainUpView.getEffectBridge();
        mRecyclerViewBridge.setUpRectResource(R.drawable.search_focus);
        mRecyclerViewBridge.setDrawUpRectPadding(new Rect(smalldimension, smalldimension, smalldimension, smalldimension));

        //获取ScrollView的宽度
        mSmoothHorizontalScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mSmoothHorizontalScrollViewWidth = mSmoothHorizontalScrollView.getWidth();
                mSmoothHorizontalScrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });


        //全局控制焦点
        mContent.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {

            @Override
            public void onGlobalFocusChanged(View oldFocus, final View newFocus) {
                if (newFocus != null) {

                    //设置不同控件获取焦点时,焦点框的大小
                    if (newFocus.getId() == R.id.item_search_lv_root) {
                        mRecyclerViewBridge.setDrawUpRectPadding(new Rect(smalldimension + 10, smalldimension - 10, smalldimension + 10, smalldimension - 10));
                    } else if (newFocus.getId() == R.id.iv_back || newFocus.getId() == R.id.iv_delete
                            || newFocus.getId() == R.id.iv_number) {
                        mRecyclerViewBridge.setDrawUpRectPadding(new Rect(bigdimension, bigdimension, bigdimension, bigdimension));

                    } else {

                        mRecyclerViewBridge.setDrawUpRectPadding(new Rect(smalldimension, smalldimension, smalldimension, smalldimension));

                    }

                    //右侧内容区域获得焦点 滚动到底部之后 再做平移动画
                    if (oldFocus != null && oldFocus.getId() == R.id.item_search_lv_root
                            && (newFocus.getId() == R.id.search_rl_content || newFocus.getId() == R.id.item_search_gv_root)
                            ) {
                        //滚动到最右
                        int[] location = new int[2];
                        ivBack.getLocationOnScreen(location);
                        int x = location[0];

                        if (x>0){
                            LogUtils.d(TAG,"smoothScroll to right");
                            mSmoothHorizontalScrollView.smoothScrollTo(mSmoothHorizontalScrollViewWidth, 0);
                        } else {
                            mSmoothHorizontalScrollView.smoothScrollTo(1080, 0);
                        }
                        mRecyclerViewBridge.setTranDurAnimTime(300);
                        showFocus(null, 100);
//                        mHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {

//                                View focusView  = SearchActivity.this.getCurrentFocus();
//                                if (focusView.getId() == R.id.search_rl_content) {
////                                    mMainUpView.setFocusView(focusView, mOldView, 1);
////                                    mOldView = focusView;
//                                    showFocus(focusView, 100);
//                                } else {
//                                    final View childView = focusView.findViewById(R.id.search_rl_content);
////                                    mMainUpView.setFocusView(childView, mOldView, 1);
////                                    mOldView = childView;
//                                    showFocus(childView, 100);
//                                }
////                            }
//                        }, 100);

//                        if (newFocus.getId() == R.id.search_rl_content) {
//                            mHandler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    View focus = SearchActivity.this.getCurrentFocus();
//                                    mMainUpView.setFocusView(focus, mOldView, 1);
//                                    mOldView = focus;
//                                }
//                            }, 100);
////                            mMainUpView.setFocusView(newFocus, 1);
////                            mOldView = newFocus;
//                        } else {
//                            LogUtils.d(TAG, "childView");
//                            final View childView = newFocus.findViewById(R.id.search_rl_content);
////                            mMainUpView.setFocusView(childView, mOldView, 1);
////                            mOldView = childView;
//                            mHandler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    View focus = SearchActivity.this.getCurrentFocus();
//                                    mMainUpView.setFocusView(focus, mOldView, 1);
//                                    mOldView = focus;
//                                }
//                            }, 100);
//                        }
                        return;
                    }
                    //左侧内容区域获得焦点 滚动到顶之后 再做平移动画
                    if (newFocus.getId() == R.id.iv_back&&mContentFragment.mRecyclerViewList.getChildCount()>=2) {
                        mSmoothHorizontalScrollView.smoothScrollTo(0, 0);
                        mRecyclerViewBridge.setTranDurAnimTime(300);
                        mMainUpView.setFocusView(newFocus, 1);
                        mOldView = newFocus;
                        return;
                    }


                    //设置焦点落在不同控件的显示与隐藏
                    if (newFocus.getId() == R.id.skbContainer) {
                        //焦点落在键盘位时 隐藏大焦点框 把焦点框移动交给本身处理
                        mRecyclerViewBridge.setVisibleWidget(true);

                    } else if (oldFocus != null && oldFocus.getId() == R.id.skbContainer) {
                        mRecyclerViewBridge.setVisibleWidget(false);
                    }
                    //列表或内容非滚动状态 处理焦点框的移动
                    if (isScrollOver) {

                        if (oldFocus != null && oldFocus.getId() == R.id.skbContainer) {

                            mRecyclerViewBridge.setTranDurAnimTime(0);
                            mMainUpView.setFocusView(newFocus, mOldView, 1);
                        }

                        else if (newFocus.getId() == R.id.item_search_gv_root) {
                            LogUtils.d(TAG,"HEHEHDA");

                            View childView = newFocus.findViewById(R.id.search_rl_content);
                            mRecyclerViewBridge.setTranDurAnimTime(300);
                            mMainUpView.setFocusView(childView, mOldView, 1);
                        }else {

                            mRecyclerViewBridge.setTranDurAnimTime(300);
                            mMainUpView.setFocusView(newFocus, mOldView, 1);
                        }
                    }

                    //滚动状态下 但焦点框切换到左侧菜单栏的处理
                    if ((newFocus.getId() == R.id.iv_number || newFocus.getId() == R.id.iv_back ||
                            newFocus.getId() == R.id.iv_delete) && !isScrollOver) {

                        isScrollOver = true;
                        //mRecyclerViewBridge.setTranDurAnimTime(300);
                        mMainUpView.setFocusView(newFocus, mOldView, 1);
                    }


                    mOldView = newFocus;
                }

                }
            }

            );
        }

    public static final int FOCUS_FLAG = 0X1000;
    public Runnable mRunnable = null;
    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FOCUS_FLAG:
                    break;
            }
        }
    };

    private void showFocus(final View focusView, long time) {
        if (mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
            mRunnable = null;
        }
        mRunnable = new Runnable() {
            @Override
            public void run() {
                View view = SearchActivity.this.getCurrentFocus();
                if (view.getId() == R.id.search_rl_content) {
                    mMainUpView.setFocusView(view, mOldView, 1);
                    mOldView = view;
                } else {
                    final View childView = view.findViewById(R.id.search_rl_content);
                    mMainUpView.setFocusView(childView, mOldView, 1);
                    mOldView = childView;
                }
                LogUtils.d(TAG, "childView22");
            }
        };
        mHandler.postDelayed(mRunnable, time);
    }

    private class IRunnable implements Runnable{

        @Override
        public void run() {

        }
    }


    private void initKeyBoardFragment() {
        mKeyBoradFragment = (KeyBoradFragment) Fragment.instantiate(this, KeyBoradFragment.class.getName());
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.ll_key, mKeyBoradFragment);
        mFragmentTransaction.commit();

    }


    private void initContentFragment() {
        mContentFragment = (ContentFragment) Fragment.instantiate(this, ContentFragment.class.getName());
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.search_fl_content, mContentFragment);
        mFragmentTransaction.commit();


    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN)
            if (mKeyBoradFragment.onKeyDown(event.getKeyCode(), event))
                return true;
        return super.dispatchKeyEvent(event);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.d(TAG, keyCode + "");

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mKeyBoradFragment.onKeyUp(keyCode, event))
            return true;
        return super.onKeyUp(keyCode, event);
    }

    public void reLoad() {
        super.reLoad();
        //发送消息 数据异常需要重新请求
        EventBusUtil.postInfoEvent(EventConf.SEND_MESSAGE_BY_SEARCH_RELOAD, null);
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
