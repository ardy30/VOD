package com.ppfuns.util.https;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ppfuns.model.entity.AdBean;
import com.ppfuns.model.entity.Authority;
import com.ppfuns.model.entity.Data;
import com.ppfuns.model.entity.PayInfo;
import com.ppfuns.model.entity.PayState;
import com.ppfuns.model.entity.VideoBean;
import com.ppfuns.model.entity.VideoDetail;
import com.ppfuns.util.LogUtils;
import com.ppfuns.util.SysUtils;
import com.ppfuns.vod.BaseApplication;
import com.ppfuns.vod.activity.AlbumActivity;
import com.zhy.http.okhttp.callback.Callback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.ppfuns.util.JsonUtils.fromJson;

/**
 * Created by hmc on 2016/8/11.
 */
public class AlbumRequest {

    private final String TAG = AlbumRequest.class.getSimpleName();
    private static AlbumRequest mAlbumRequest;

    private com.ppfuns.model.entity.Response<VideoDetail> mAlbumResponse;
    private VideoDetail mVideoDetail;
    private List<VideoBean> mVideoList;
    private Authority mAuthority;
    private List<AdBean> mAdBeanList;
    private PayInfo mPayInfo;

    private String mAlbumUrl;
    private final String ALBUM_FLAG_PRE = "album";
    private final String AUTHORITY_FLAG_PRE = "authority";
    private String album_tag;
    private String authority_tag;

    private int mCurrentIndex = -1;
    private int mAlbumId = -1;
    private int mContentId = -1;
    private boolean mIsAuthoritySucess = false;
    private boolean mHasObtainAd = false;
    private int mVideoType = 0;
    private int mAlbumReqCount = 0;
    private int mCount = 0;

    private AlbumCallBack mCallBack;
    private Context mContext;

    private String requestAlbumUrl = null;
    private String checkAuthorityUrl = null;

    private AlbumRequest() {
    }

    public static synchronized AlbumRequest getInstance() {
        if (mAlbumRequest == null) {
            mAlbumRequest = new AlbumRequest();
        }
        return mAlbumRequest;
    }

    public void setAlbumCallBack(AlbumCallBack callBack) {
        mCallBack = callBack;
//        getDomain();
    }

    public void cancelAlbumCallBack() {
        mCallBack = null;
        release();
    }

    public void getAlbumInfo(int albumId, int col_contentId, int index, boolean isFromDMR) {
        if (mAlbumId == albumId) {
            updateVideo(albumId, index);
            return;
        }
        mCurrentIndex = index;
        mAlbumId = albumId;
        mContentId = col_contentId;
//        mAlbumUrl = ContractUrl.getVodUrl()  + "/v/album";
        requestAlbumUrl = ContractUrl.getVodUrl()  + "/v/album/" + mAlbumId + "-" + mContentId + "-0.json";
        LogUtils.i(TAG, "requestAlbumUrl: " + requestAlbumUrl);
        if (isFromDMR) {
            mIsAuthoritySucess = true;
            requestAlbumInfo(requestAlbumUrl);
        } else {
            checkAuthorityUrl = ContractUrl.getCheckAuthorityUrl() + "?contentid=" + albumId + "&contenttype=" + AlbumActivity.AUTHORITY_FLAG + "&userid=" + SysUtils.getUserId() + "&ca=" + SysUtils.getCa();
            requestCheckAuthority(checkAuthorityUrl);
        }
    }

    private void getDomain() {
//        if (mContext == null) {
//            return;
//        }
//        int domain = (int) SPUtils.getData(mContext, SPUtils.SHARED_NAME, SPConfig.DOMAIN_KEY, BackDoorActivity.mDomainEnvironment);
//        switch (domain) {
//            case BackDoorActivity.DOMAIN_FORMAL:
//                ContractUrl.DOMAIN = ContractUrl.URL_HEAD + ContractUrl.URL_DOMAIN_FORMAL;
//                break;
//            case BackDoorActivity.DOMAIN_TEST:
//                ContractUrl.DOMAIN = ContractUrl.URL_HEAD + ContractUrl.URL_DOMAIN_TEST;
//                break;
//        }
//        ContractUrl.VOD_URL = ContractUrl.DOMAIN + ContractUrl.COMMON;
        mAlbumUrl = ContractUrl.getVodUrl()  + "/v/album";
    }

    public void updateVideo(int albumId, int index) {
        LogUtils.i(TAG, "updateVideo_index: " + index);
        VideoBean videoBean = null;
        LogUtils.i(TAG, "albumId = " + albumId + ",mAlbumId = " + mAlbumId);
        if (mAlbumId == albumId && mVideoList != null && mVideoList.size() > 0) {
//            int len = mVideoList.size();
            if (index > 0 && index <= mCount) {
                videoBean = mVideoList.get(index - 1);
                if (videoBean != null) {
                    if (index != videoBean.getSeq()) {
                        for (int i = 0; i < mCount; i++) {
                            VideoBean iVideoBean = mVideoList.get(i);
                            if (iVideoBean != null) {
                                if (index == iVideoBean.getSeq()) {
                                    videoBean = iVideoBean;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            else {
                videoBean = mVideoList.get(0);
            }
        }
        if (mCallBack != null) {
            String blueRayImg = null;
            String picUrl = null;
            if (mVideoDetail != null) {
                blueRayImg = mVideoDetail.getBlueRayImg();
                picUrl = mVideoDetail.getPicUrl();
            }
            mCallBack.obtainAlbum(mIsAuthoritySucess, mAdBeanList, videoBean, mVideoType, mAuthority, blueRayImg, picUrl, mCount);
        }
    }

    public VideoBean updateVideoForVoice(int albumId, int index) {
        LogUtils.i(TAG, "updateVideoForVoice_index: " + index);
        VideoBean videoBean = null;
        if (mAlbumId == albumId && mVideoList != null && mVideoList.size() > 0) {
//            int len = mVideoList.size();
            if (index > 0 && index <= mCount) {
                videoBean = mVideoList.get(index - 1);
                if (videoBean != null) {
                    if (index != videoBean.getSeq()) {
                        for (int i = 0; i < mCount; i++) {
                            VideoBean iVideoBean = mVideoList.get(i);
                            if (iVideoBean != null) {
                                if (index == iVideoBean.getSeq()) {
                                    videoBean = iVideoBean;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
//            else {
//                videoBean = mVideoList.get(0);
//            }
        }
        return videoBean;
    }

    public void setAlbumId(int albumId, boolean isAuthoritySucess, Authority authority) {
        mAlbumId = albumId;
        mIsAuthoritySucess = isAuthoritySucess;
        mAuthority = authority;
    }

    public void updateAlbumInfo(VideoDetail videoDetail) {
        mVideoType = videoDetail.getContentType();
        mVideoList = (ArrayList<VideoBean>) ((ArrayList<VideoBean>) videoDetail.getVideoList()).clone();
        if (mVideoList != null && mVideoList.size() > 0)
            mCount = mVideoList.size();
        mAdBeanList = null;
        String strAd = videoDetail.getAdUrlList();
//        String strAd = "[{\"adType\":1,\"adTypeId\":\"16\",\"playTime\":5,\"playUrl\":\"http://192.168.1.85:8080/vod/ad196_ad359.mp4\",\"thirdUrl\":\"www.baidu.com\"},{\"adType\":1,\"adTypeId\":\"17\",\"playTime\":5,\"playUrl\":\"http://192.168.1.85:8060/ad_mange/2016920/1474341719194.jpg\",\"thirdUrl\":\"www.baidu.com\"}]";
//        strAd = "";
        LogUtils.d(TAG, "adList: " + strAd);
        if (!TextUtils.isEmpty(strAd)) {
            mAdBeanList = new Gson().fromJson(strAd, new TypeToken<List<AdBean>>() {
            }.getType());
        }
        mVideoDetail = videoDetail;
    }

    private void requestCheckAuthority(final String url) {
        LogUtils.i(TAG, "requestCheckAuthority");
        if (authority_tag != null) {
            HttpUtil.cancelTag(authority_tag);
            authority_tag = null;
        }
        authority_tag = AUTHORITY_FLAG_PRE + System.currentTimeMillis();
        LogUtils.i(TAG, "requestCheckAuthority : " + authority_tag);
        HttpUtil.getHttpHtml(url, authority_tag, new Callback<Authority>() {

            @Override
            public boolean validateReponse(Response response, int id) {
                LogUtils.e(TAG, "authority_response_code: " + response.code());
                return response.isSuccessful();
            }

            @Override
            public Authority parseNetworkResponse(Response response, int id) throws Exception {
                String result = response.body().string();
                Authority authority = new Gson().fromJson(result, Authority.class);
                LogUtils.d(TAG, "authority_result: " + result);
                return authority;
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(TAG, "HttpGet authority failed " + e.getMessage());
                if (!TextUtils.equals(e.getMessage(), "Canceled")) {
                    requestCheckAuthority(url);
                    return;
                }
                if (mCallBack != null) {
                    mCallBack.obtainAlbum(mIsAuthoritySucess, null, null, mVideoType, mAuthority, null, null, mCount);
                }
            }

            @Override
            public void onResponse(Authority authority, int id) {
                mAuthority = authority;
                if (mAuthority != null) {
                    if (TextUtils.equals(PayState.PAY_FREE, mAuthority.code)
                            || TextUtils.equals(PayState.PAY_THROUGH, mAuthority.code)) {
                        mIsAuthoritySucess = true;
                    }
                }
                if (mContentId <= 0 && BaseApplication.getmColumnId() != -1) {
                    mContentId = BaseApplication.getmColumnId();
                }
                LogUtils.i(TAG, "requestAlbumUrl: " + requestAlbumUrl);
                requestAlbumInfo(requestAlbumUrl);
            }
        });
    }

    private void requestAlbumInfo(final String url) {
        LogUtils.i(TAG, "requestAlbumInfo");
        if (album_tag != null) {
            HttpUtil.cancelTag(album_tag);
            album_tag = null;
        }
        mAlbumReqCount++;
        album_tag = ALBUM_FLAG_PRE + System.currentTimeMillis();
        LogUtils.i(TAG, "requestAlbumInfo: " + album_tag);
        HttpUtil.getHttpHtml(url, album_tag, new Callback<com.ppfuns.model.entity.Response<VideoDetail>>() {

            @Override
            public boolean validateReponse(Response response, int id) {
                LogUtils.e(TAG, "album_response_code: " + response.code());
                return response.isSuccessful();
            }

            @Override
            public com.ppfuns.model.entity.Response<VideoDetail> parseNetworkResponse(Response response, int id) throws Exception {
                String result = response.body().string();
                com.ppfuns.model.entity.Response<VideoDetail> resAlbum = (com.ppfuns.model.entity.Response<VideoDetail>) fromJson(result, new TypeToken<com.ppfuns.model.entity.Response<VideoDetail>>() {
                }.getType());
                LogUtils.d(TAG, "album_result: " + result);
                return resAlbum;
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(TAG, "HttpGet album failed " + e.getMessage());
                if (!TextUtils.equals(e.getMessage(), "Canceled") && mAlbumReqCount < 3) {
                    requestAlbumInfo(url);
                    return;
                }
                if (mCallBack != null) {
                    mCallBack.obtainAlbum(mIsAuthoritySucess, null, null, mVideoType, mAuthority, null, null, mCount);
                }
            }

            @Override
            public void onResponse(com.ppfuns.model.entity.Response<VideoDetail> response, int id) {
                mAlbumResponse = response;
                parseAlbum();
            }
        });
    }

    private void parseAlbum() {
        LogUtils.i(TAG, "parseAlbum");
        if (mAlbumResponse == null) {
            if (mCallBack != null) {
                mCallBack.obtainAlbum(mIsAuthoritySucess, null, null, mVideoType, mAuthority, null, null, mCount);
            }
            return;
        }
        Data<VideoDetail> dataBean = mAlbumResponse.data;
        if (dataBean == null) {
            if (mCallBack != null) {
                mCallBack.obtainAlbum(mIsAuthoritySucess, null, null, mVideoType, mAuthority, null, null, mCount);
            }
            return;
        }
        if (TextUtils.equals(RequestCode.RESPONSE_SUCCESS, dataBean.code)) {
            mVideoDetail = mAlbumResponse.data.result;
            if (mVideoDetail != null) {
                mAdBeanList = null;
                String strAd = mVideoDetail.getAdUrlList();
                if (!TextUtils.isEmpty(strAd)) {
                    mAdBeanList = new Gson().fromJson(strAd, new TypeToken<List<AdBean>>() {
                    }.getType());
                }
                mVideoList = mVideoDetail.getVideoList();
                mVideoType = mVideoDetail.getContentType();
                mCount = mVideoList.size();
                if (mCurrentIndex > 0 && mCurrentIndex <= mCount) {
                    if (mCallBack != null) {
                        mCallBack.obtainAlbum(mIsAuthoritySucess, mAdBeanList, mVideoList.get(mCurrentIndex - 1), mVideoType, mAuthority, mVideoDetail.getBlueRayImg(), mVideoDetail.getPicUrl(), mCount);
                    } else {
                        LogUtils.d(TAG, "ArrayIndexOutOfBounds");
                    }
                }
            }
        } else if (TextUtils.equals(RequestCode.CONTENT_EMPTY, dataBean.code)) {
            if (mCallBack != null) {
                mCallBack.obtainAlbum(false, null, null, mVideoType, mAuthority, null, null, -1);
            }
        } else {
            if (mCallBack != null) {
                mCallBack.obtainAlbum(mIsAuthoritySucess, null, null, mVideoType, mAuthority, null, null, mCount);
            }
        }
    }

    public void release() {
        LogUtils.i(TAG, "album_tag: " + album_tag + ", authority_tag: " + authority_tag);
        if (album_tag != null) {
            HttpUtil.cancelTag(album_tag);
            album_tag = null;
        }
        if (authority_tag != null) {
            HttpUtil.cancelTag(authority_tag);
            authority_tag = null;
        }
        mVideoList = null;
        mCallBack = null;
        mVideoDetail = null;

        mAlbumResponse = null;
        mCurrentIndex = -1;
        mAlbumId = -1;
        mVideoType = 0;
        mAdBeanList = null;
        mHasObtainAd = false;
        mIsAuthoritySucess = false;
        mPayInfo = null;
        mAlbumReqCount = 0;
		mCount = 0;
    }

    public interface AlbumCallBack {
        //        void obtainAd(AdBean adBean);
        void obtainAlbum(boolean isAuthority, List<AdBean> adBeanList, VideoBean videoBean, int videoType, Authority authority, String horizontalPicUrl, String verticalPicUrl, int count);
    }
}
