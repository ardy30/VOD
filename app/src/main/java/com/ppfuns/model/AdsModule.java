package com.ppfuns.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ppfuns.adtools.AdsClient;
import com.ppfuns.adtools.AdsInfo;
import com.ppfuns.model.entity.AdsEntity;
import com.ppfuns.util.LogUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdsModule implements IAdsModule {
    private final static String TAG = AdsModule.class.getSimpleName();
    private static final int VOD_PLAYER_ADS_TYPE = 15;
    private static final String ADS_UPDATE_ACTION = "com.ppfuns.ads.update";

    private static final String KEY_ADSBEAN = "key_adsbean";
    private static final String KEY_ADSFILE_KEYSET = "key_adsfile_keyset";
    private static final String ADS_CACHE_PATH = "/ads_cache";

    private static AdsModule module;
    private AdsEntity mAdsEntity;
    private IDiskCache mDiskCache;
    private BroadcastReceiver mBroadcastReceiver;

    private AdsModule(Context context) {
        init(context);
    }

    private void init(Context context) {
        mAdsUpdateListenerArrayList = new ArrayList<>();
        try {
            List<AdsInfo> adsInfoList = AdsClient.getADinfo(context, VOD_PLAYER_ADS_TYPE);
            Collections.sort(adsInfoList, new AdsInfoCompartor());
            mAdsEntity = makeAdsEntity(adsInfoList, context);
            Toast.makeText(context, "广告加载成功", Toast.LENGTH_SHORT).show();
            removeOldAdsCache(context);
            cacheAdsEntity(mAdsEntity, context);
            Log.d(TAG, "AdsCellView: 缓存了广告");
        } catch (Exception e) {
            e.printStackTrace();
            loadCachedData(context);
        }
    }

    public static IAdsModule getInstance(Context context) {
        if (module == null) {
            module = new AdsModule(context);
        }
        return module;
    }


    /**
     * 刷新广告数据
     *
     * @return true表示刷新成功, false表示失败
     */
    @Override
    public boolean refreshAdsData(Context context) {
        try {
            List<AdsInfo> adsInfoList = AdsClient.getADinfo(context, VOD_PLAYER_ADS_TYPE);
            //根据优先级进行排序
            Collections.sort(adsInfoList, new AdsInfoCompartor());
            mAdsEntity.clear();
            mAdsEntity = makeAdsEntity(adsInfoList, context);
            removeOldAdsCache(context);
            cacheAdsEntity(mAdsEntity, context);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            loadCachedData(context);
            return false;
        }
    }

    @Override
    public int getAdsImgColor() {
        return 0;
    }

    @Override
    public boolean getAdsFocusable() {
        if (mAdsEntity.isAvaliable()) {
            String strIntent = mAdsEntity.mAdsBeanArrayList.get(0).strIntent;
            if (TextUtils.isEmpty(strIntent)) {
                return false;
            }
            return true;
        }
        return true;
    }

    @Override
    public void registerReceiver(Context context) {
        if (mBroadcastReceiver == null) {
            IntentFilter intentFilter = new IntentFilter(ADS_UPDATE_ACTION);
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG, "onReceive: 广告服务更新");
                    refreshAdsData(context.getApplicationContext());
                    for (OnAdsUpdateListener listener : mAdsUpdateListenerArrayList) {
                        listener.onAdsUpdate(getAdsImg());
                    }
                }
            };
            context.registerReceiver(mBroadcastReceiver, intentFilter);
        }
    }

    @Override
    public void unRegisterReceiver(Context context) {
        if (mBroadcastReceiver != null) {
            context.unregisterReceiver(mBroadcastReceiver);
        }
    }

    private ArrayList<OnAdsUpdateListener> mAdsUpdateListenerArrayList;


    public interface OnAdsUpdateListener {
        void onAdsUpdate(Bitmap bitmap);
    }

    @Override
    public void addOnAdsUpdateListener(OnAdsUpdateListener onAdsUpdateListener) {
        mAdsUpdateListenerArrayList.add(onAdsUpdateListener);
    }

    @Override
    public void removeOnUpdateListener(OnAdsUpdateListener onAdsUpdateListener) {
        mAdsUpdateListenerArrayList.remove(onAdsUpdateListener);
    }

    @Override
    public void removeAllListenter() {
        mAdsUpdateListenerArrayList.clear();
    }

    @Override
    public String getIntentInfo() {
        if (mAdsEntity.isAvaliable()) {
            return mAdsEntity.mAdsBeanArrayList.get(0).strIntent;
        }
        return null;
    }


    @Override
    public Bitmap getAdsImg() {
        if (mAdsEntity.isAvaliable()) {
            return mAdsEntity.mAdsFileArrayList.get(0).adsImg;
        }
        return null;
    }

    /**
     * 比较器,根据优先级进行比较
     */
    private static class AdsInfoCompartor implements Comparator<AdsInfo> {
        @Override
        public int compare(AdsInfo lhs, AdsInfo rhs) {
            if (lhs.getPriority() > rhs.getPriority()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /**
     * 加载缓存广告数据
     */
    private void loadCachedData(Context context) {
        mAdsEntity = getCachedAdsEntity(context);
        if (mAdsEntity.isAvaliable()) {
            Log.d(TAG, "loadCachedData: 读取缓存成功");
        } else {
            Log.d(TAG, "loadCachedData: 没有缓存");
        }
    }

    /**
     * 将从广告服务获取的数据，进行封装
     *
     * @param adsInfoList 从广告服务获取回来的数据
     * @param context     上下文
     * @return 封装了的广告数据
     */
    private AdsEntity makeAdsEntity(List<AdsInfo> adsInfoList, Context context) {
        if (adsInfoList == null) {
            return null;
        }

        ArrayList<AdsEntity.AdsBean> adsBeanList = new ArrayList<>();
        ArrayList<AdsEntity.AdsFile> adsFileList = new ArrayList<>();

        for (AdsInfo info : adsInfoList) {
            AdsEntity.AdsBean adsBean = new AdsEntity.AdsBean();
            AdsEntity.AdsFile adsFile = new AdsEntity.AdsFile();

            adsBean.adId = info.getAdId();
            adsBean.strIntent = info.getThirdurl();
            // TODO: 2016/7/4 后期数据将从广告中真实获取
            adsBean.focusable = info.isFocusable().equals("1");

            try {
                InputStream inputStream = AdsClient.getADfile(context, info.getAdfileurl());
                Log.d(TAG, "inputStream: " + inputStream);
                adsFile.adsImg = BitmapFactory.decodeStream(inputStream);
                if (adsFile.adsImg == null) {
                    Log.d(TAG, "adsFile.adsImg: null");
                }
                //文件缓存的key为其id
                adsFile.cacheKey = "" + info.getAdId();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            adsBeanList.add(adsBean);
            adsFileList.add(adsFile);
        }

        return new AdsEntity(adsBeanList, adsFileList);
    }

    /**
     * 缓存广告数据
     *
     * @param adsEntity 被缓存的广告数据
     * @param context   上下文
     */
    private void cacheAdsEntity(AdsEntity adsEntity, Context context) {
        createCacheIfNotExist(context);
        /**
         * 缓存数据
         */
        mDiskCache.saveObj(adsEntity.mAdsBeanArrayList, KEY_ADSBEAN);

        /**
         * 缓存文件
         * 文件的key/文件本身,需单独缓存
         */
        String fileCacheKeySet = null;
        for (AdsEntity.AdsFile file : adsEntity.mAdsFileArrayList) {
            if (fileCacheKeySet != null) {
                //缓存的文件的key拼接而成的字符串,各key之间,以"#"分割
                fileCacheKeySet = "#" + file.cacheKey;
            } else {
                fileCacheKeySet = file.cacheKey;
            }
            mDiskCache.saveBitmapFromBitmap(file.cacheKey, file.adsImg);
        }
        mDiskCache.saveString(fileCacheKeySet, KEY_ADSFILE_KEYSET);
    }

    /**
     * 删除原有广告缓存
     *
     * @param context 上下文
     * @return true删除成功，false删除失败
     */
    private boolean removeOldAdsCache(Context context) {
        boolean result;

        createCacheIfNotExist(context);


        String keySet = mDiskCache.getString(KEY_ADSFILE_KEYSET);
        result = mDiskCache.delFileKey(KEY_ADSFILE_KEYSET);
        if (keySet != null) {
            for (String key : keySet.split("#")) {
                result = mDiskCache.delFileUrl(key);
            }
        }

        return result;
    }

    /**
     * 读取缓存，获取缓存广告的数据
     *
     * @param context 上下文
     * @return 广告数据
     */
    private AdsEntity getCachedAdsEntity(Context context) {
        createCacheIfNotExist(context);

        ArrayList<AdsEntity.AdsBean> adsBeanList = (ArrayList<AdsEntity.AdsBean>) mDiskCache.getObj(KEY_ADSBEAN);
        ArrayList<AdsEntity.AdsFile> adsFileList = new ArrayList<>();

        String keySet = mDiskCache.getString(KEY_ADSFILE_KEYSET);
        if (keySet != null) {
            for (String key : keySet.split("#")) {
                AdsEntity.AdsFile adsFile = new AdsEntity.AdsFile();
                adsFile.cacheKey = key;
                adsFile.adsImg = mDiskCache.getBitmap(key);
                adsFileList.add(adsFile);
            }
        }

        return new AdsEntity(adsBeanList, adsFileList);
    }

    /**
     * 缓存不存在时，创建disk缓存
     *
     * @param context 上下文
     */
    private void createCacheIfNotExist(Context context) {
        if (mDiskCache == null) {
            mDiskCache = new DiskCache().init(context, 10 * 1024 * 1024, context.getCacheDir() + ADS_CACHE_PATH);
        }
    }
}
