package com.ppfuns.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ppfuns.vod.R;

/**
 * Created by pc1 on 2016/8/3.
 */
public class VodPlayImages extends BaseLoopPlayView {
    private String TAG = "VodPlayImages";

    public VodPlayImages(Context context) {
        super(context);
    }

    public VodPlayImages(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initView() {
        removeAllViews();
//        mShowViews = new View[mShowImg + 2];
//        mShowImageViews = new ImageView[mShowImg + 2];
        int max;
//        if(mShowImg<4){
//            max = mShowImg;
//            mShowViews = new View[mShowImg+1];
//            mShowImageViews = new ImageView[mShowImg+1];
//        }else{
        max = mShowImg + 1;
        mShowViews = new View[mShowImg + 2];
        mShowImageViews = new ImageView[mShowImg + 2];
        // }
        for (int i = 0; i <= max; i++) {
            ImageView imageView = new ImageView(context);
            LayoutParams lp1 = new LayoutParams(mContentViewWidth, mContentViewHeight);
            imageView.setLayoutParams(lp1);
            mShowViews[i] = imageView;
            mShowImageViews[i] = imageView;
            imageView.setPadding(mContentViewPadding, mContentViewPadding, mContentViewPadding, mContentViewPadding);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            if (setImageSrc != null) {
                setImageSrc.setImageSrc(imageView, i);
            } else {
                if (mImgUrls != null) {
                    String url = mImgUrls[((i - mFirstFocusItem) + mImgUrls.length) % mImgUrls.length];
                    imageLoader.displayImage(url, imageView,options);
                } else {
                    imageView.setImageResource(R.drawable.ju_default_360x458);
                }
            }
            this.addView(imageView);
        }
        for (int i = mShowViews.length - 1; i >= 0; i--) {
            mShowViews[i].bringToFront();
        }

        computeRects(mShowImg, width, mContentViewWidth);
        for (int i = 0; i < mShowViews.length; i++) {
            mShowViews[i].layout(mViewInfos[i].left, mViewInfos[i].top, mViewInfos[i].right, mViewInfos[i].bottom);
        }
    }

    @Override
    protected void computeRects(int mShowImg, int width, int mContentViewWidth) {
        mViewInfos = new LoopViewInfo[mShowImg + 2];
        int stepWidth = (int) Math.ceil((width - mContentViewWidth - getPaddingLeft() - getPaddingRight()) / (float) (mShowImg - 1));//使用Math.ceil修正1相素值
        mViewInfos[0] = new LoopViewInfo();
        mViewInfos[0].left = -mContentViewWidth;
        mViewInfos[0].right = 0;
        mViewInfos[0].top = (height - mContentViewHeight) / 2;
        mViewInfos[0].bottom = mViewInfos[0].top + mContentViewHeight;
        mViewInfos[0].scale = 1.0f;

        for (int i = 1; i < mViewInfos.length; i++) {
            mViewInfos[i] = new LoopViewInfo();
            //float scale = 0.0f;
            Log.i(TAG, "mShowImg: " + mShowImg);
            if (i == 2) {
                int leftOne = getResources().getDimensionPixelSize(R.dimen.subject_detail_left_one);
                mViewInfos[i].left = leftOne;
                //scale = (float) (1.0f / Math.pow(mScale, (i-1)));
                mShowViews[i].animate().scaleX(0.88205f).scaleY(0.88205f).setDuration(0).start();
                mViewInfos[i].scale = 0.88205f;
            } else if (i == 3) {
                int leftTwo = getResources().getDimensionPixelSize(R.dimen.subject_detail_left_two);
                mViewInfos[i].left = leftTwo;
                mShowViews[i].animate().scaleX(0.800256f).scaleY(0.800256f).setDuration(0).start();
                mViewInfos[i].scale = 0.800256f;
            } else if (i == 4) {
                int leftThree = getResources().getDimensionPixelSize(R.dimen.subject_detail_left_three);
                mViewInfos[i].left = leftThree;
                mShowViews[i].animate().scaleX(0.70769f).scaleY(0.70769f).setDuration(0).start();
                mViewInfos[i].scale = 0.70769f;
            } else {
//                if(mShowImg==2&&i==mViewInfos.length-1){
//                    mViewInfos[i].left = 260;
//                    mShowViews[i].animate().scaleX(0.88205f).scaleY(0.88205f).setDuration(0).start();
//                    mViewInfos[i].scale = 0.88205f;
//                }else if(mShowImg==3&&i==mViewInfos.length-1){
//                    mViewInfos[i].left = 485;
//                    mShowViews[i].animate().scaleX(0.800256f).scaleY(0.800256f).setDuration(0).start();
//                    mViewInfos[i].scale = 0.800256f;
//                }else{
//                    float scale = (float) (1.0f / Math.pow(mScale, (i - 1)));
//                    mShowViews[i].animate().scaleX(scale).scaleY(scale).setDuration(0).start();
//                    mViewInfos[i].scale = scale;
//                    mViewInfos[i].left = (int) (getPaddingLeft() + (i - 1) * stepWidth + mContentViewWidth - mContentViewWidth * scale);
//                }
                float scale = (float) (1.0f / Math.pow(mScale, (i - 1)));
                mShowViews[i].animate().scaleX(scale).scaleY(scale).setDuration(0).start();
                mViewInfos[i].scale = scale;
                mViewInfos[i].left = (int) (getPaddingLeft() + (i - 1) * stepWidth + mContentViewWidth - mContentViewWidth * scale);
            }
            mViewInfos[i].right = mViewInfos[i].left + mContentViewWidth;
            mViewInfos[i].top = (height - mContentViewHeight) / 2;
            mViewInfos[i].bottom = mViewInfos[i].top + mContentViewHeight;
        }
    }

    @Override
    public void ItemViewsbringToFront() {
        for (int i = mShowViews.length - 1; i >= 0; i--) {
            mShowViews[i].bringToFront();
        }
    }

}
