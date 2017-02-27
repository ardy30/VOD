package com.ppfuns.ui.view.player;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.ppfuns.util.LogUtils;

public class HorizontalProgressHintDelegate extends ProgressHintDelegate {

    public HorizontalProgressHintDelegate(SeekBar seekBar, AttributeSet attrs, int defStyleAttr) {
        super(seekBar, attrs, defStyleAttr);
    }

    @Override
    protected PointF getHintDragCoordinates(MotionEvent event) {
        float x = event.getRawX() - mSeekBar.getX();
        float y = mSeekBar.getY();
        return new PointF(x, y);
    }

    @Override
    protected Point getFixedHintOffset() {
        int xOffset = getHorizontalOffset(mSeekBar.getMax() / 2);
        int yOffset = getVerticalOffset();
        return new Point(xOffset, yOffset);
    }

    @Override
    protected Point getFollowHintOffset() {
        int xOffset = getHorizontalOffset(mSeekBar.getProgress());
        int yOffset = getVerticalOffset();
        return new Point(xOffset, yOffset);
    }

    private int getHorizontalOffset(int progress) {
        LogUtils.i(" mPopupView width = " + mPopupView.getMeasuredWidth());
        return getFollowPosition(progress) - mPopupView.getMeasuredWidth() / 2;
    }

    private int getVerticalOffset() {
        return -(mSeekBar.getHeight() + mPopupView.getMeasuredHeight() + mPopupOffset);
    }
}
