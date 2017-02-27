package com.ppfuns.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ppfuns.model.AdsModule;
import com.ppfuns.model.IAdsModule;
import com.ppfuns.vod.activity.WebViewActivity;

/**
 * Created by zpf on 2016/7/14.
 */
public class AdsView extends ImageView {


    private final static String TAG = AdsView.class.getSimpleName();

    private Context mContext;

    private final IAdsModule mAdsModule;

    private AdsModule.OnAdsUpdateListener mOnAdsUpdateListener;

    public AdsView(Context context) {
        this(context, null);
    }

    public AdsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mAdsModule = AdsModule.getInstance(getContext().getApplicationContext());
    }


    /**
     * 确认后执行的行为
     */
    public void doAction() {
        String intentInfo = mAdsModule.getIntentInfo();
        if (TextUtils.isEmpty(intentInfo)) {
            Toast.makeText(getContext(), "广告异常", Toast.LENGTH_SHORT).show();
        } else {
            showHtml(intentInfo);
            Toast.makeText(getContext(), "广告跳转 >>> " + intentInfo, Toast.LENGTH_SHORT).show();
        }
    }

    private void showHtml(String url) {
        Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.putExtra(WebViewActivity.WEB_URL, "http://www.baidu.com");
        mContext.startActivity(intent);
    }

    /**
     * 显示广告内容
     */
    public void showAds() {
        //根据广告数据，设置view的属性
        Bitmap adImg = mAdsModule.getAdsImg();
        this.setFocusable(mAdsModule.getAdsFocusable());

        if (adImg != null) {
            try {
                this.setImageBitmap(adImg);
            } catch (Exception e) {
                Log.d(TAG, "showAds: 出异常了");
            }
        }
    }


    /**
     * 注册广播
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mOnAdsUpdateListener = new AdsModule.OnAdsUpdateListener() {
            @Override
            public void onAdsUpdate(Bitmap bitmap) {
                showAds();
            }
        };
        showAds();

        mAdsModule.addOnAdsUpdateListener(mOnAdsUpdateListener);
    }

    /**
     * 注销广播
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAdsModule.removeOnUpdateListener(mOnAdsUpdateListener);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                doAction();
                return true;
            default:
                break;

        }

        return super.onKeyDown(keyCode, event);
    }
}
