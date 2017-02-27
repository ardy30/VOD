package com.ppfuns.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.ppfuns.util.https.ContractUrl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by hmc on 2016/6/14.
 */
public class SysUtils {

    private final static String PROPERTY_CONTNET = "persist.sys.vod.content";
    private final static String PROPERTY_USER = "persist.sys.vod.user";
    private final static String PROPERTY_AUTHORITY = "persist.sys.vod.authority";
    private final static String PROPERTY_USER_ID = "sys.platform.userid";
    private final static String PROPERTY_CA = "persist.sys.ca.card_id";
    private final static String PROPERTY_COLUMNID = "persist.sys.vod.columnid";
    private final static String PROPERTY_AREA_CODE = "persist.sys.osupdate.areacode";

    /**
     * 获取应用版本名称
     *
     * @param ctx
     */
    public static String getAppVersionName(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            String pkgName = ctx.getPackageName();
            PackageInfo pkgInfo = pm.getPackageInfo(pkgName, 0);
            String ver = pkgInfo.versionName;
            return ver;
        } catch (NameNotFoundException e) {
            Log.e("exception", "NameNotFoundException: " + e);
            return "0";
        }
    }

    /**
     * 判断应用是否安装
     */
    public static boolean getAppIsInstall(String packageName, Context context) {

        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        boolean isInstall = false;

        for (int i = 0; i < packages.size(); i++) {

            PackageInfo packageInfo = packages.get(i);

            String packagename = packageInfo.packageName;
            if (packagename.equals(packageName)) {
                isInstall = true;
            }

        }
        return isInstall;


    }


    /**
     * 获取应用版本号
     *
     * @param ctx
     */
    public static int getAppVersionCode(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            String pkgName = ctx.getPackageName();
            PackageInfo pkgInfo = pm.getPackageInfo(pkgName, 0);
            int ver = pkgInfo.versionCode;
            return ver;
        } catch (NameNotFoundException e) {
            Log.e("exception", "NameNotFoundException: " + e);
            return 0;
        }
    }

    /**
     * 获取系统sdk版本号
     */
    public static int getSdkVersion() {
        int version = 0;
        try {
            version = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            Log.e("exception", "NameNotFoundException: " + e);
        }
        return version;
    }

    /**
     * 获取设备宽度分辨率
     *
     * @param ctx
     */
    public static int getScreenWidth(Context ctx) {
        return ctx.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取设备高度分辨率
     *
     * @param ctx
     */
    public static int getScreenHeight(Context ctx) {
        return ctx.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 判断网络是否可用
     *
     * @param ctx
     * @return boolean
     */
    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }

        if (networkinfo.getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        return false;
    }

    /**
     * 获取无线mac地址
     *
     * @param ctx
     * @return
     */
    public static String getWifiMacAddr(Context ctx) {
        String WifiMac = "";
        WifiManager mWifi = (WifiManager) ctx
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifi.getConnectionInfo();
        if ((WifiMac = wifiInfo.getMacAddress()) == null) {
            WifiMac = null;
            Log.v("HAI", "WifiMac  getMacAddr = NULL");
        }
        return WifiMac;
    }

    /**
     * 获取有线mac地址
     *
     * @return
     */
    public static String getEth0MacAddress(Context context) {
        String macSerial = "";
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/eth0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;
    }

    /**
     * 获取String Array
     *
     * @param context
     * @param resId
     * @return
     */
    public static String[] getStringArray(Context context, int resId) {
        return context.getResources().getStringArray(resId);
    }

    /**
     * 获取String
     *
     * @param context
     * @param resId
     * @return
     */
    public static String getString(Context context, int resId) {
        return context.getString(resId);
    }

    /**
     * 获取硬件信息
     *
     * @return
     */
    public static String getDeviceInfo() {
        StringBuffer sb = new StringBuffer();
        //通过反射获取系统的硬件信息
        try {

            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                //获取私有的信息
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get(null).toString();
                sb.append(name + "=" + value);
                sb.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 获取Property属性值
     *
     * @return
     */
    public static String getSysProperty(String proName, String defValue) {
        if (TextUtils.isEmpty(proName)) {
            return defValue;
        }
        String value = null;
        try {
            Class cls = Class.forName("android.os.SystemProperties");
            Method hideMethod = cls.getMethod("get", String.class);

            Object object = cls.newInstance();
            value = (String) hideMethod.invoke(object, proName);
            if (!TextUtils.isEmpty(value)) {
                return value;
            }
        } catch (Exception e) {
        }

        return defValue;
    }

    public static int getUserId() {
        return Integer.valueOf(SysUtils.getSysProperty(PROPERTY_USER_ID, "-1"));
    }

    public static String getContentUrl() {
        return SysUtils.getSysProperty(PROPERTY_CONTNET, ContractUrl.DOMAIN);
    }

    public static String getAuthorityUrl() {
        return SysUtils.getSysProperty(PROPERTY_AUTHORITY, ContractUrl.DOMAIN_AUTHORITY);
    }

    public static String getUserUrl() {
        return SysUtils.getSysProperty(PROPERTY_USER, ContractUrl.DOMAIN_USER);
    }

    public static String getCa() {
        return SysUtils.getSysProperty(PROPERTY_CA, "-1");
    }

    public static String getSn() {
        return SysUtils.getSysProperty(PROPERTY_CA, null);
    }

    public static int getColumnId() {
        return Integer.valueOf(getColumnId("1"));
    }

    public static String getColumnId(String defValue) {
        return SysUtils.getSysProperty(PROPERTY_COLUMNID, defValue);
    }

    public static String getAreaCode() {
        return SysUtils.getSysProperty(PROPERTY_AREA_CODE, "");
    }

}
