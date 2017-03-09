package com.ppfuns.vod;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ppfuns.util.LogUtils;
import com.ppfuns.util.SysUtils;
import com.ppfuns.util.UIUtils;
import com.ppfuns.util.https.HttpUtil;
import com.ppfuns.vod.service.UploadService;
import com.squareup.leakcanary.RefWatcher;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by hmc on 2016/6/14.
 */
public class BaseApplication extends Application {

    private final String TAG = BaseApplication.class.getSimpleName();
    private static Context appContext;

    private RefWatcher refWatcher;

    public static int mColumnId = -1;

    public static int userId = -1;

    private final String PROPERTY_USER_ID = "sys.platform.userid";

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();

        // 初始化内存泄露监控
//        refWatcher = LeakCanary.install(this);

        MobclickAgent.setDebugMode(true);

        MobclickAgent.setDebugMode(BuildConfig.DEBUG);

        //初始化全局异常处理
//        CrashHandler.getInstance().init(appContext);

        // 初始化HttpClient
        HttpUtil.initClient(this);
        LogUtils.isDebug = true;
        LogUtils.isDebug2ViewOrOutPut = true;

        String strUserId = SysUtils.getSysProperty(PROPERTY_USER_ID, null);
        if (!TextUtils.isEmpty(strUserId)) {
            try {
                userId = Integer.valueOf(strUserId);
            } catch (ClassCastException e) {
                LogUtils.e(TAG, e.toString());
            }
        }
        LogUtils.i(TAG, "userId: " + userId);

//        // 获取property配置的url
//        String contentUrl = SysUtils.getSysProperty(PROPERTY_CONTNET, ContractUrl.DOMAIN);
//        LogUtils.i("Decode", "contentUrl: " + contentUrl);
//        if (!TextUtils.equals(contentUrl, ContractUrl.DOMAIN)) {
//            ContractUrl.DOMAIN = contentUrl;
//            ContractUrl.VOD_URL = ContractUrl.DOMAIN + ContractUrl.COMMON;
//        } else {
//            int domain = (int) SPUtils.getData(this, SPUtils.SHARED_NAME, SPConfig.DOMAIN_KEY, BackDoorActivity.mDomainEnvironment);
//            switch (domain) {
//                case BackDoorActivity.DOMAIN_FORMAL:
//                    ContractUrl.DOMAIN = ContractUrl.URL_HEAD + ContractUrl.URL_DOMAIN_FORMAL;
//                    break;
//                case BackDoorActivity.DOMAIN_TEST:
//                    ContractUrl.DOMAIN = ContractUrl.URL_HEAD + ContractUrl.URL_DOMAIN_TEST;
//                    break;
//            }
//            ContractUrl.VOD_URL = ContractUrl.DOMAIN + ContractUrl.COMMON;
//        }

//        String userUrl = SysUtils.getSysProperty(PROPERTY_USER, ContractUrl.DOMAIN_USER);
//        String authorityUrl = SysUtils.getSysProperty(PROPERTY_AUTHORITY, ContractUrl.DOMAIN_AUTHORITY);
//        if (!TextUtils.isEmpty(userUrl)) {
//            ContractUrl.DOMAIN_USER = userUrl;
//            ContractUrl.VOD_URL_USER = ContractUrl.DOMAIN_USER;
//        }
//        if (!TextUtils.isEmpty(authorityUrl)) {
//            ContractUrl.DOMAIN_AUTHORITY = authorityUrl;
//            ContractUrl.CHECK_AUTHORITY_URL = ContractUrl.DOMAIN_AUTHORITY + ContractUrl.COMMON_QUTHORITY;
//        }

//        // 切换栏目ID
//        int columnId = (int) SPUtils.getData(this, SPUtils.SHARED_NAME, SPConfig.COLUMN_ID_KEY, -1);
//        if (columnId != -1) {
//            mColumnId = columnId;
//        }

        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);
        UIUtils.init(this);
        Intent intent = new Intent(this, UploadService.class);
        startService(intent);

    }

    public static Context getAppContext() {
        return appContext;
    }

    public static RefWatcher getRefWatcher(Context context) {
        BaseApplication application = (BaseApplication) context.getApplicationContext();
        return application.refWatcher;
    }


    public static int getUserId() {

        userId = SysUtils.getUserId();
        return userId;
    }


    public static int getmColumnId() {

//        // 切换栏目ID
//        int columnId = (int) SPUtils.getData(getAppContext(), SPUtils.SHARED_NAME, SPConfig.COLUMN_ID_KEY, -1);
//        if (columnId != -1) {
//            mColumnId = columnId;
//        }

        return SysUtils.getColumnId();
    }


}
