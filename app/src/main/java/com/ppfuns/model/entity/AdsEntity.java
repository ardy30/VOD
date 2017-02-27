package com.ppfuns.model.entity;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lenovo on 2016/6/28.
 */
public class AdsEntity {
    public ArrayList<AdsBean> mAdsBeanArrayList;
    public ArrayList<AdsFile> mAdsFileArrayList;


    public AdsEntity(ArrayList<AdsBean> adsBeanArrayList, ArrayList<AdsFile> adsFileArrayList) {
        mAdsBeanArrayList = adsBeanArrayList;
        mAdsFileArrayList = adsFileArrayList;
    }


    public static class AdsBean implements Serializable {
        public int adId;
        public String strIntent;
        public boolean focusable;
    }

    public static class AdsFile {
        public String cacheKey;
        public Bitmap adsImg;
    }

    public void clear() {
        if (mAdsBeanArrayList != null) {
            mAdsBeanArrayList.clear();
        }

        if (mAdsFileArrayList != null) {
            for (AdsFile file : mAdsFileArrayList) {
                file.adsImg.recycle();
            }
            mAdsFileArrayList.clear();
        }
    }

    public boolean isAvaliable() {
        boolean result = true;

        if (mAdsBeanArrayList == null || mAdsBeanArrayList.isEmpty()) {
            result = false;
        }
        if (mAdsFileArrayList == null || mAdsFileArrayList.isEmpty()) {
            result = false;
        }

        return result;
    }
}

