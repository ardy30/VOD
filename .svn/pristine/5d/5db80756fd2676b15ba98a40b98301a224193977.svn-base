package com.ppfuns.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.ppfuns.util.LogUtils;

/**
 * Created by fangyuan on 2016/7/5.
 */
public class AlwaysMarqueeTextView extends TextView {
    private int currentScrollX;// 当前滚动的位置
    private boolean isStop = false;
    private int textWidth;
    protected boolean isMeasure = false;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            currentScrollX -= 1;// 滚动速度
            scrollTo(-currentScrollX, 0);
            if (isStop) {
                return;
            }
            if (getScrollX() >= textWidth-getPaddingLeft()) {
                LogUtils.d(TAG,"INVISIBLE");
                scrollTo(-(getRight()-getPaddingRight()), 0);

                currentScrollX = getRight()-getPaddingRight();
            }
            postDelayed(this, 10);
        }
    };

    public AlwaysMarqueeTextView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public AlwaysMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlwaysMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if (!isMeasure) {// 文字宽度只需获取一次就可以了
            getTextWidth();
            isMeasure = true;
        }
    }

    /**
     * 获取文字宽度
     */
    public int getTextWidth() {
        Paint paint = this.getPaint();
        String str = this.getText().toString();
        textWidth = (int) paint.measureText(str);
        return textWidth;
    }

    private static final String TAG = "AlwaysMarqueeTextView";

    // 开始滚动
    public void startScroll() {
        if (textWidth>getWidth()-getPaddingLeft()-getPaddingRight()) {
            if (getGravity() == Gravity.CENTER) {
                setGravity(Gravity.CENTER_VERTICAL);
            }
            isStop = false;
            this.removeCallbacks(mRunnable);
            postDelayed(mRunnable, 500);
        }
    }

    // 停止滚动
    public void stopScroll() {
        isStop = true;
        currentScrollX = 0;
        scrollTo(0, 0);
        removeCallbacks(mRunnable);
    }

    // 从头开始滚动
    public void startFrom0() {
        getTextWidth();
        currentScrollX = 0;
        startScroll();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtils.d(TAG, "onDetachedFromWindow");
        stopScroll();
    }
}
