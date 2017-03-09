package com.ppfuns.vod.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ppfuns.util.SPConfig;
import com.ppfuns.util.SPUtils;
import com.ppfuns.util.SysUtils;
import com.ppfuns.util.ToastUtils;
import com.ppfuns.util.https.ContractUrl;
import com.ppfuns.vod.BaseApplication;
import com.ppfuns.vod.R;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BackDoorActivity extends BaseActivity {
    private static final String TAG = "BackDoorActivity";
    @BindView(R.id.btn_formal)
    Button btnFormal;
    @BindView(R.id.btn_test)
    Button btnTest;
    @BindView(R.id.layout_top)
    LinearLayout layoutTop;
    @BindView(R.id.tv_domain)
    TextView tvDomain;
    @BindView(R.id.tv_version_name)
    TextView tvVersionName;
    @BindView(R.id.tv_version_code)
    TextView tvVersionCode;
    @BindView(R.id.tv_svn_version)
    TextView tvSvnVersion;
    @BindView(R.id.tv_domain_user)
    TextView tvDomainUser;
    @BindView(R.id.et_column)
    EditText etColumn;
    @BindView(R.id.btn_column_commit)
    Button btnColumnCommit;

    // 域名环境：0：正式环境；1：测试环境
    public static final int DOMAIN_FORMAL = 0;
    public static final int DOMAIN_TEST = 1;
    public static int mDomainEnvironment = DOMAIN_FORMAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_door);
        ButterKnife.bind(this);
        initData();
    }

    protected void initData() {
        switch (mDomainEnvironment) {
            case DOMAIN_FORMAL:
                tvDomain.setText(getString(R.string.menu_txt_domain) + getString(R.string.txt_double_space)
                        + ContractUrl.URL_DOMAIN_FORMAL);
                tvDomainUser.setText(getString(R.string.menu_txt_domain_user) + getString(R.string.txt_double_space)
                        + ContractUrl.URL_DOMAIN_USER);
                btnFormal.requestFocus();
                break;
            case DOMAIN_TEST:
                tvDomain.setText(getString(R.string.menu_txt_domain) + getString(R.string.txt_double_space)
                        + ContractUrl.URL_DOMAIN_TEST);
                tvDomainUser.setText(getString(R.string.menu_txt_domain) + getString(R.string.txt_double_space)
                        + ContractUrl.URL_DOMAIN_USER);
                btnTest.requestFocus();
                break;
        }

        tvVersionName.setText(getString(R.string.menu_txt_version_name) + getString(R.string.txt_double_space)
                + SysUtils.getAppVersionName(this));
        tvVersionCode.setText(getString(R.string.menu_txt_version_code) + getString(R.string.txt_double_space)
                + SysUtils.getAppVersionCode(this));
        tvSvnVersion.setText(getString(R.string.menu_txt_svn_version) + getString(R.string.txt_double_space)
                + getUserConfig("svn_version"));
    }

    private String getUserConfig(String key) {
        Properties pro = new Properties();
        InputStream is = null;
        try {
            is = getAssets().open("user_config.properties");
            pro.load(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String value = (String) pro.get(key);

        return value;
    }

    @OnClick({R.id.btn_formal, R.id.btn_test, R.id.btn_column_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_formal:
                mDomainEnvironment = DOMAIN_FORMAL;
                ContractUrl.DOMAIN = ContractUrl.URL_HEAD + ContractUrl.URL_DOMAIN_FORMAL;
                tvDomain.setText(getString(R.string.menu_txt_domain) + getString(R.string.txt_double_space)
                        + ContractUrl.URL_DOMAIN_FORMAL);
                clickAbleShouldClose();
                SPUtils.putData(this, SPUtils.SHARED_NAME, SPConfig.DOMAIN_KEY, mDomainEnvironment);
                ContractUrl.VOD_URL = ContractUrl.DOMAIN + ContractUrl.COMMON;
                break;
            case R.id.btn_test:
                mDomainEnvironment = DOMAIN_TEST;
                ContractUrl.DOMAIN = ContractUrl.URL_HEAD + ContractUrl.URL_DOMAIN_TEST;
                tvDomain.setText(getString(R.string.menu_txt_domain) + getString(R.string.txt_double_space)
                        + ContractUrl.URL_DOMAIN_TEST);
                clickAbleShouldClose();
                SPUtils.putData(this, SPUtils.SHARED_NAME, SPConfig.DOMAIN_KEY, mDomainEnvironment);
                ContractUrl.VOD_URL = ContractUrl.DOMAIN + ContractUrl.COMMON;
                break;
            case R.id.btn_column_commit:
                if (TextUtils.isEmpty(etColumn.getText().toString())) {
                    ToastUtils.showShort(this, getString(R.string.menu_txt_column));
                } else {
                    BaseApplication.mColumnId = Integer.valueOf(etColumn.getText().toString());
                    SPUtils.putData(this, SPUtils.SHARED_NAME, SPConfig.COLUMN_ID_KEY, BaseApplication.getmColumnId());
                    ToastUtils.showShort(this, getString(R.string.menu_txt_confirm_tips));
                }
                break;
        }
    }

    protected void clickAbleShouldClose() {
        btnFormal.setClickable(true);
        btnTest.setClickable(true);
        switch (mDomainEnvironment) {
            case DOMAIN_FORMAL:
                btnFormal.setClickable(false);
                break;
            case DOMAIN_TEST:
                btnTest.setClickable(false);
                break;
        }
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
