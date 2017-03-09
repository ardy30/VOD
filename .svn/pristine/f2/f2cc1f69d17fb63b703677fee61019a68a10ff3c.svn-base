package com.ppfuns.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import com.ppfuns.util.LogUtils;
import com.ppfuns.vod.R;

/**
 * Created by zpf on 2016/12/9.
 */

public class ChartHorizontalScrollView extends HorizontalScrollView {

    private final String TAG = ChartHorizontalScrollView.class.getSimpleName();

    private Context mContext;
    private boolean stopScroll = false;
    private int mContentSize = 0;
    private int mItemWidth = 0;
    private int mContentWidth = 0;
    private int mKeyCode = -1;
    private ImageView mIvLeft;
    private ImageView mIvRight;

    public ChartHorizontalScrollView(Context context) {
        this(context, null);
    }

    public ChartHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setContentSize(int size) {
        LogUtils.d(TAG, "contentSize " + size);
        mContentSize = size;
        mItemWidth = mContext.getResources().getDimensionPixelSize(R.dimen.chart_iv_width)
                + getResources().getDimensionPixelSize(R.dimen.chart_iv_mleft);
        mContentWidth = mItemWidth * mContentSize;
    }

    public void setLeftArrow(ImageView imageView) {
        mIvLeft = imageView;
    }

    public void setRightArrow(ImageView imageView) {
        mIvRight = imageView;
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {

        int width = getWidth();
        int screenLeft = getScrollX();
        int screenRight = screenLeft + width;
        if (rect.left + mItemWidth + 100 >= mContentWidth) {
            stopScroll = true;
        } else {
            stopScroll = false;
        }
        if (mContentSize > 4) {
            if (mKeyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (rect.right > mItemWidth * 4 + 100) {
                    mIvLeft.setVisibility(VISIBLE);
                }
                if (rect.right + 100 > mContentWidth) {
                    mIvRight.setVisibility(INVISIBLE);
                }
            } else if (mKeyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (rect.left + 100 < mItemWidth) {
                    mIvLeft.setVisibility(INVISIBLE);
                }
                if (rect.left + 100 < mContentWidth - mItemWidth * 4) {
                    mIvRight.setVisibility(VISIBLE);
                }
            }
        }
        LogUtils.d(TAG, "screenLeft: " + screenLeft + ", screenRight: " + (rect.right) + ", rect: " + rect.toString());
        return super.computeScrollDeltaToGetChildRectOnScreen(rect);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            mKeyCode = keyCode;
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (stopScroll && mContentSize > 4) {
                        return true;
                    }
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
