package com.ppfuns.ui.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.LruCache;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.ppfuns.util.LogUtils;
import com.ppfuns.vod.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * Created by FlyZebra on 2016/8/3.
 */
public abstract class BaseLoopPlayView extends ViewGroup implements ILoopPlayView {
    protected int width;
    protected int height;
    protected Context context;
    protected LruCache<String, Bitmap> mImgLruCahe;//图片缓存
    protected String[] mImgUrls;//图片网络请求地址
    protected AnimatorSet mAnimatorSet;
    protected int mAnimatorDuration = 300;//屏幕滚动延续时长
    protected int mContentViewWidth = 585;//最大图片的宽度
    protected int mContentViewHeight = 780;//最大图片的高度
    protected int mContentViewPadding = 5;//图片填充内容
    protected int mCurrentItem = 0;

    protected View[] mShowViews;
    protected ImageView[] mShowImageViews;//用来显示图片的控件
    protected LoopViewInfo[] mViewInfos;
    protected DisplayImageOptions options = new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .showImageOnLoading(R.drawable.ju_default_360x458)
            .showImageOnFail(R.drawable.ju_default_360x458)
            .showImageForEmptyUri(R.drawable.ju_default_360x458)
            .cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
            .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new FadeInBitmapDisplayer(300)).build();
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    /**
     * 控件显示图片的数量
     */
    protected int mShowImg = 4;//显示的图片数
    protected float mScale = 1.18f;

    protected int mFirstFocusItem = 1;//第一张获取焦点的图片

    private Handler mHandle = new Handler();

    public BaseLoopPlayView(Context context) {
        this(context, null);
    }

    public BaseLoopPlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseLoopPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        this.context = context;
        mImgLruCahe = new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 4)) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
        init(context);
    }

    /**
     * 根据设置参数（未设置的取默认参数）初始化控件
     *
     * @param context
     */
    public void init(Context context) {
        this.context = context;
        mContentViewWidth = context.getResources().getDimensionPixelSize(R.dimen.subject_detail_content_width);
        mContentViewHeight = context.getResources().getDimensionPixelSize(R.dimen.subject_detail_content_height);
        initView();
    }

    /**
     * 创建播放轮播动画所需的控件，
     * NOTE:轮播动画所需的控件数并不等于mMaxImg，个数由具体动画决定。
     */
    public abstract void initView();

    /**
     * 计算每个图片的放置位置
     */
    protected abstract void computeRects(int mMaxImg, int width, int mContentViewWidth);


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        for (int i = 0; i < getChildCount(); i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            computeRects(mShowImg, width, mContentViewWidth);
            for (int i = 0; i < mShowViews.length; i++) {
                mShowViews[i].layout(mViewInfos[i].left, mViewInfos[i].top, mViewInfos[i].right, mViewInfos[i].bottom);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.i("<BaseLoopPlayView>onKeyDown->keyCode=" + keyCode + ",event=" + event);
        if(mImgUrls!=null&&mImgUrls.length>0){
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if(mShowImg>1){
                        goLeftItem();
                        mCurrentItem = (mCurrentItem + mImgUrls.length - 1) % mImgUrls.length;
                        if(selectItem!=null){
                            selectItem.selectItem(mCurrentItem);
                        }
                    }

                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if(mShowImg>1){
                        goRightItem();
                        mCurrentItem = (mCurrentItem + 1) % mImgUrls.length;
                        if(selectItem!=null){
                            selectItem.selectItem(mCurrentItem);
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    selectItemClickListener.selectItemClick(mCurrentItem);
                    //Toast.makeText(context, "选中子项-->" + mCurrentItem, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private SelectItem selectItem;
    public void setSelectItem(SelectItem selectItem){
        this.selectItem = selectItem;
    }
    public interface SelectItem{
        void selectItem(int point);
    }

    private SelectItemClickListener selectItemClickListener;

    public SelectItemClickListener getSelectItemClickListener() {
        return selectItemClickListener;
    }

    public void setSelectItemClickListener(SelectItemClickListener selectItemClickListener) {
        this.selectItemClickListener = selectItemClickListener;
    }

    public interface SelectItemClickListener{
        void selectItemClick(int point);
    }

    public void goRightItem() {
        Set<Animator> mAnimSet = new HashSet<>();
        //向左移动，第一张图片要特殊处理，更换显示图像
        {
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(mShowViews[0], "translationX",
                    mViewInfos[mViewInfos.length - 1].left - mViewInfos[0].left,
                    mViewInfos[mViewInfos.length - 1].left - mViewInfos[0].left);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(mShowViews[0], "scaleX", mViewInfos[mViewInfos.length - 1].scale, mViewInfos[mViewInfos.length - 1].scale);
            ObjectAnimator animator3 = ObjectAnimator.ofFloat(mShowViews[0], "scaleY", mViewInfos[mViewInfos.length - 1].scale, mViewInfos[mViewInfos.length - 1].scale);
            mAnimSet.add(animator1);
            mAnimSet.add(animator2);
            mAnimSet.add(animator3);

            if (mShowImageViews != null && mImgUrls != null) {
                int num = (mCurrentItem + mShowImageViews.length-mFirstFocusItem+mImgUrls.length) % mImgUrls.length;
                final ImageView imageView = mShowImageViews[0];
                imageLoader.displayImage(mImgUrls[num],imageView,options);
            }
        }

        for (int i = 1; i < mShowViews.length; i++) {
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(mShowViews[i], "translationX", mViewInfos[i].left - mViewInfos[i - 1].left, 0);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(mShowViews[i], "scaleX", mViewInfos[i].scale, mViewInfos[i - 1].scale);
            ObjectAnimator animator3 = ObjectAnimator.ofFloat(mShowViews[i], "scaleY", mViewInfos[i].scale, mViewInfos[i - 1].scale);
            mAnimSet.add(animator1);
            mAnimSet.add(animator2);
            mAnimSet.add(animator3);
        }
        if (mAnimatorSet != null) {
            if (mAnimatorSet.isRunning()) {
                mAnimatorSet.end();
//                finishAnimta(false);
            }
        }
        mAnimatorSet = new AnimatorSet();
        //将需要移动的图片置前
        //TODO 考虑一张图片的情况
        if (mShowViews.length > 1) {
            mShowViews[mFirstFocusItem + 1].bringToFront();
        }

        startAnimta(false);
        mAnimatorSet.playTogether(mAnimSet);
        mAnimatorSet.setInterpolator(new DecelerateInterpolator());
        mAnimatorSet.setDuration(mAnimatorDuration);
        mAnimatorSet.start();

    }

    public void goLeftItem() {
        Set<Animator> mAnimSet = new HashSet<>();
        //向右移动，最后一张图片要特殊处理
        {
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(mShowViews[mViewInfos.length - 1], "translationX",
                    mViewInfos[0].left - mViewInfos[mViewInfos.length - 1].left,
                    mViewInfos[0].left - mViewInfos[mViewInfos.length - 1].left);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(mShowViews[mViewInfos.length - 1], "scaleX", mViewInfos[0].scale, mViewInfos[0].scale);
            ObjectAnimator animator3 = ObjectAnimator.ofFloat(mShowViews[mViewInfos.length - 1], "scaleY", mViewInfos[0].scale, mViewInfos[0].scale);
            mAnimSet.add(animator1);
            mAnimSet.add(animator2);
            mAnimSet.add(animator3);

            if (mShowImageViews != null && mImgUrls != null) {
                int num = (mCurrentItem + mImgUrls.length -1-mFirstFocusItem) % mImgUrls.length;
                final ImageView imageView = mShowImageViews[mShowImageViews.length -1];
                imageLoader.displayImage(mImgUrls[num],imageView,options);
            }
        }


        for (int i = 0; i < mShowViews.length - 1; i++) {
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(mShowViews[i], "translationX", mViewInfos[i].left - mViewInfos[i + 1].left, 0);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(mShowViews[i], "scaleX", mViewInfos[i].scale, mViewInfos[i + 1].scale);
            ObjectAnimator animator3 = ObjectAnimator.ofFloat(mShowViews[i], "scaleY", mViewInfos[i].scale, mViewInfos[i + 1].scale);
            mAnimSet.add(animator1);
            mAnimSet.add(animator2);
            mAnimSet.add(animator3);
        }

        if (mAnimatorSet != null) {
            if (mAnimatorSet.isRunning()) {
                mAnimatorSet.end();
            }
        }

        mAnimatorSet = new AnimatorSet();
        //将需要移动的图片置前
        //TODO 考虑一张图片的情况
        if (mShowViews.length > 1) {
            mShowViews[mFirstFocusItem - 1].bringToFront();
        }

        startAnimta(true);
        mAnimatorSet.playTogether(mAnimSet);
        mAnimatorSet.setInterpolator(new DecelerateInterpolator());
        mAnimatorSet.setDuration(mAnimatorDuration);
        mAnimatorSet.start();
    }

    private void startAnimta(boolean isLeft) {
        if (isLeft) {
            View tempView = mShowViews[mShowViews.length - 1];
            for (int i = mShowViews.length - 1; i > 0; i--) {
                mShowViews[i] = mShowViews[i - 1];
            }
            mShowViews[0] = tempView;
        } else {
            View tempView = mShowViews[0];
            for (int i = 1; i < mShowViews.length; i++) {
                mShowViews[i - 1] = mShowViews[i];
            }
            mShowViews[mShowViews.length - 1] = tempView;
        }

        if (mShowImageViews != null) {
            if (isLeft) {
                ImageView tempView = mShowImageViews[mShowImageViews.length - 1];
                for (int i = mShowImageViews.length - 1; i > 0; i--) {
                    mShowImageViews[i] = mShowImageViews[i - 1];
                }
                mShowImageViews[0] = tempView;
            } else {
                ImageView tempView = mShowImageViews[0];
                for (int i = 1; i < mShowImageViews.length; i++) {
                    mShowImageViews[i - 1] = mShowImageViews[i];
                }
                mShowImageViews[mShowImageViews.length - 1] = tempView;
            }
        }

        for (int i = 0; i < mShowViews.length; i++) {
            mShowViews[i].setTranslationX(0);
            mShowViews[i].setScaleY(1);
            mShowViews[i].setScaleX(1);
            mShowViews[i].layout(mViewInfos[i].left, mViewInfos[i].top, mViewInfos[i].right, mViewInfos[i].bottom);
            LogUtils.i("<VodPlayImages>onLayout Rect->" + i + "->=" + mViewInfos[i]);
        }
        ItemViewsbringToFront();
    }

    public abstract void ItemViewsbringToFront();


    @Override
    public BaseLoopPlayView setImageViewUrls(@NonNull List<String> urlList) {
        String[] urls = (String[]) urlList.toArray();
        setImageViewUrls(urls);
        return this;
    }

    @Override
    public BaseLoopPlayView setImageViewUrls(@NonNull String[] urls) {
        mImgUrls = new String[urls.length];
        System.arraycopy(urls, 0, mImgUrls, 0, urls.length);
        return this;
    }

    protected SetImageSrc setImageSrc;

    @Override
    public void setImageSrc(SetImageSrc setImageSrc) {
        this.setImageSrc = setImageSrc;
    }


    @Override
    protected void onDetachedFromWindow() {
        mHandle.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }

    @Override
    public BaseLoopPlayView setmContentViewHeight(int mContentViewHeight) {
        this.mContentViewHeight = mContentViewHeight;
        return this;
    }

    @Override
    public BaseLoopPlayView setmContentViewWidth(int mContentViewWidth) {
        this.mContentViewWidth = mContentViewWidth;
        return this;
    }

    @Override
    public BaseLoopPlayView setmContentViewPadding(int mContentViewPadding) {
        this.mContentViewPadding = mContentViewPadding;
        return this;
    }

    @Override
    public BaseLoopPlayView setmAnimatorDuration(int mAnimatorDuration) {
        this.mAnimatorDuration = mAnimatorDuration;
        return this;
    }

    @Override
    public BaseLoopPlayView setShowImageNum(int num) {
        mShowImg = num;
        return this;
    }


    @Override
    public BaseLoopPlayView setFirstFocusItem(int point) {
        mFirstFocusItem = point;
        return this;
    }

}
