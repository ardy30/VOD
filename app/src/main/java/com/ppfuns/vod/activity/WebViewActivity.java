package com.ppfuns.vod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ppfuns.util.LogUtils;
import com.ppfuns.vod.R;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewActivity extends BaseActivity {

    @BindView(R.id.webview)
    WebView mWebView;

    private final String TAG     = WebViewActivity.class.getSimpleName();
    private       String mWebUrl = null;

    public static final String WEB_URL = "web_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        showLoadingProgressDialog(getString(R.string.data_loading));
        getUrl();
        intWebView();
        loadUrl();
    }

    private void getUrl() {
        Intent intent = getIntent();
        if (intent != null) {

            mWebUrl = intent.getStringExtra(WEB_URL);
            if (!mWebUrl.startsWith("http")) {
                mWebUrl = "http://" + mWebUrl;
            }

        }

        LogUtils.d("ZFZ", mWebUrl);
    }

    private void intWebView() {
        //        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        WebSettings settings = mWebView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
    }

    public void loadUrl() {
        LogUtils.i(TAG, "loadUrl");
        if (TextUtils.isEmpty(mWebUrl)) {
            LogUtils.i(TAG, "mWebUrl error");
            dissmissLoadingDialog();
            showDialogTips(getString(R.string.data_error));
            return;
        }
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dissmissLoadingDialog();
                mWebView.setVisibility(View.VISIBLE);
            }
        });

        mWebView.loadUrl(mWebUrl);
    }

    @Override
    protected void reLoad() {
        super.reLoad();
        loadUrl();
    }

    @Override
    protected void cancel() {
        super.cancel();
        finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack();//返回上一页面
                return true;
            } else {
                finish();
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
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

}
