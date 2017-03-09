package com.ppfuns.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;

import com.ppfuns.util.LOG;

/**
 * Created by lenovo on 2016/12/30.
 */
public abstract class FocusedBasePositionManager {
    public static final String TAG = "FocusedBasePositionManager";
    private static final int DEFAULT_FRAME_RATE = 6;
    private static final int DEFAULT_FRAME = 1;
    public static final int FOCUS_SYNC_DRAW = 0;
    public static final int FOCUS_ASYNC_DRAW = 1;
    public static final int FOCUS_STATIC_DRAW = 2;
    public static final int STATE_IDLE = 0;
    public static final int STATE_DRAWING = 1;
    public static final int SCALED_FIXED_COEF = 1;
    public static final int SCALED_FIXED_X = 2;
    public static final int SCALED_FIXED_Y = 3;
    private boolean DEBUG = true;
    private int mCurrentFrame = 1;
    private int mFrameRate = 6;
    private int mFocusFrameRate = 2;
    private int mScaleFrameRate = 2;
    private float mScaleXValue = 1.0F;
    private float mScaleYValue = 1.0F;
    private int mScaledMode = 2;
    private int mFixedScaledX = 30;
    private int mFixedScaledY = 30;
    private int mState = 0;
    private boolean mNeedDraw = false;
    private int mode = 1;
    private Rect mSelectedPaddingRect = new Rect();
    private Rect mManualSelectedPaddingRect = new Rect();
    protected Drawable mMySelectedDrawable = null;
    private Drawable mMySelectedDrawableShadow;
    private Rect mMySelectedPaddingRectShadow;
    private boolean mIsFirstFrame = true;
    private boolean mConstrantNotDraw = false;
    private boolean mIsLastFrame = false;
    private View mSelectedView;
    private View mLastSelectedView;
    private View mContainerView;
    private boolean mHasFocus = false;
    private boolean mTransAnimation = false;
    private Context mContext;
    private Rect mLastFocusRect;
    private Rect mFocusRect;
    private Rect mCurrentRect;
    private boolean mScaleCurrentView = true;
    private boolean mScaleLastView = true;
    private boolean mIsScale = true;

    public FocusedBasePositionManager(Context var1, View var2) {
        this.mContext = var1;
        this.mContainerView = var2;
    }

    public void drawFrame(Canvas var1) {
        LOG.d("FocusedBasePositionManager", this.DEBUG, "drawFrame: mCurrentFrame = " + this.mCurrentFrame + ", needDraw = " + this.mNeedDraw + ", mode = " + this.mode + ", mScaleXValue = " + this.mScaleXValue + ", mScaleYValue = " + this.mScaleYValue + ", mFocusFrameRate = " + this.mFocusFrameRate + ", mScaleFrameRate" + this.mScaleFrameRate);
        if(0 == this.mode) {
            this.drawSyncFrame(var1);
        } else if(1 == this.mode) {
            this.drawAsyncFrame(var1);
        } else if(2 == this.mode) {
            this.drawStaticFrame(var1);
        }

    }

    public void setScaleMode(int var1) {
        this.mScaledMode = var1;
    }

    public void setScale(boolean var1) {
        this.mIsScale = var1;
    }

    public void setScaleCurrentView(boolean var1) {
        this.mScaleCurrentView = var1;
    }

    public void setScaleLastView(boolean var1) {
        this.mScaleLastView = var1;
    }

    public boolean isLastFrame() {
        return this.mIsLastFrame;
    }

    public void setContrantNotDraw(boolean var1) {
        this.mConstrantNotDraw = var1;
    }

    public boolean getContrantNotDraw() {
        return this.mConstrantNotDraw;
    }

    public void setFocusDrawableVisible(boolean var1, boolean var2) {
        this.mMySelectedDrawable.setVisible(var1, var2);
    }

    public void setFocusDrawableShadowVisible(boolean var1, boolean var2) {
        this.mMySelectedDrawableShadow.setVisible(var1, var2);
    }

    public void setLastSelectedView(View var1) {
        this.mLastSelectedView = var1;
    }

    public void setTransAnimation(boolean var1) {
        this.mTransAnimation = var1;
    }

    public void setNeedDraw(boolean var1) {
        this.mNeedDraw = var1;
    }

    public void setFocusResId(int var1) {
        this.mMySelectedDrawable = this.mContext.getResources().getDrawable(var1);
        this.mSelectedPaddingRect = new Rect();
        this.mMySelectedDrawable.getPadding(this.mSelectedPaddingRect);
    }

    public void setFocusShadowResId(int var1) {
        this.mMySelectedDrawableShadow = this.mContext.getResources().getDrawable(var1);
        this.mMySelectedPaddingRectShadow = new Rect();
        this.mMySelectedDrawableShadow.getPadding(this.mMySelectedPaddingRectShadow);
    }

    public void setItemScaleValue(float var1, float var2) {
        this.mScaleXValue = var1;
        this.mScaleYValue = var2;
    }

    public void setItemScaleFixedX(int var1) {
        this.mFixedScaledX = var1;
    }

    public void setItemScaleFixedY(int var1) {
        this.mFixedScaledY = var1;
    }

    public float getItemScaleXValue() {
        return this.mScaleXValue;
    }

    public float getItemScaleYValue() {
        return this.mScaleYValue;
    }

    public Rect getCurrentRect() {
        return this.mCurrentRect;
    }

    public void setState(int var1) {
        synchronized(this) {
            this.mState = var1;
        }
    }

    public int getState() {
        synchronized(this) {
            return this.mState;
        }
    }

    public void setManualPadding(int var1, int var2, int var3, int var4) {
        this.mManualSelectedPaddingRect.left = var1;
        this.mManualSelectedPaddingRect.right = var3;
        this.mManualSelectedPaddingRect.top = var2;
        this.mManualSelectedPaddingRect.bottom = var4;
    }

    public int getManualPaddingLeft() {
        return this.mManualSelectedPaddingRect.left;
    }

    public int getManualPaddingRight() {
        return this.mManualSelectedPaddingRect.right;
    }

    public int getManualPaddingTop() {
        return this.mManualSelectedPaddingRect.top;
    }

    public int getManualPaddingBottom() {
        return this.mManualSelectedPaddingRect.bottom;
    }

    public int getSelectedPaddingLeft() {
        return this.mSelectedPaddingRect.left;
    }

    public int getSelectedPaddingRight() {
        return this.mSelectedPaddingRect.right;
    }

    public int getSelectedPaddingTop() {
        return this.mSelectedPaddingRect.top;
    }

    public int getSelectedPaddingBottom() {
        return this.mSelectedPaddingRect.bottom;
    }

    public int getSelectedShadowPaddingLeft() {
        return this.mMySelectedPaddingRectShadow.left;
    }

    public int getSelectedShadowPaddingRight() {
        return this.mMySelectedPaddingRectShadow.right;
    }

    public int getSelectedShadowPaddingTop() {
        return this.mMySelectedPaddingRectShadow.top;
    }

    public int getSelectedShadowPaddingBottom() {
        return this.mMySelectedPaddingRectShadow.bottom;
    }

    public void setFocus(boolean var1) {
        this.mHasFocus = var1;
    }

    public boolean hasFocus() {
        return this.mHasFocus;
    }

    public void setFocusMode(int var1) {
        this.mode = var1;
    }

    public void setFrameRate(int var1) {
        this.mFrameRate = var1;
        if(var1 % 2 == 0) {
            this.mScaleFrameRate = var1 / 2;
            this.mFocusFrameRate = var1 / 2;
        } else {
            this.mScaleFrameRate = var1 / 2;
            this.mFocusFrameRate = var1 / 2 + 1;
        }

    }

    public void setFrameRate(int var1, int var2) {
        this.mFrameRate = var1 + var2;
        this.mScaleFrameRate = var1;
        this.mFocusFrameRate = var2;
    }

    public void setSelectedView(View var1) {
        LOG.i("FocusedBasePositionManager", this.DEBUG, "setSelectedView v = " + var1);
        this.mSelectedView = var1;
    }

    public View getSelectedView() {
        return this.mSelectedView;
    }

    private void drawSyncFrame(Canvas var1) {
        LOG.i("FocusedBasePositionManager", this.DEBUG, "drawSyncFrame");
        if(this.getSelectedView() != null) {
            if(this.mCurrentFrame < this.mFrameRate && this.mNeedDraw) {
                if(this.mIsFirstFrame) {
                    this.drawFirstFrame(var1, true, true);
                } else {
                    this.drawOtherFrame(var1, true, true);
                }
            } else {
                if(this.mCurrentFrame != this.mFrameRate || !this.mNeedDraw) {
                    if(this.hasFocus()) {
                        Rect var2 = this.getDstRectBeforeScale(true);
                        if(var2 != null) {
                            this.mLastFocusRect = var2;
                            this.drawFocus(var1, false);
                        }
                    }

                    return;
                }

                this.drawLastFrame(var1, true, true);
            }
        } else {
            //Log.i("FocusedBasePositionManager", "drawSyncFrame select view is null");
        }

    }

    private void drawAsyncFrame(Canvas var1) {
        LOG.i("FocusedBasePositionManager", this.DEBUG, "drawAsyncFrame");
        if(this.getSelectedView() != null) {
            boolean var2 = this.mCurrentFrame > this.mFocusFrameRate;
            if(this.mCurrentFrame < this.mFrameRate && this.mNeedDraw) {
                if(this.mIsFirstFrame) {
                    this.drawFirstFrame(var1, var2, !var2);
                } else {
                    this.drawOtherFrame(var1, var2, !var2);
                }
            } else if(this.mCurrentFrame == this.mFrameRate) {
                this.drawLastFrame(var1, var2, !var2);
            } else if(!this.mConstrantNotDraw) {
                if(this.hasFocus()) {
                    Rect var3 = this.getDstRectBeforeScale(true);
                    if(var3 != null) {
                        this.mLastFocusRect = var3;
                        this.drawFocus(var1, false);
                    }
                }

                return;
            }
        } else {
            //Log.w("FocusedBasePositionManager", "drawAsyncFrame select view is null");
        }

    }

    private void drawStaticFrame(Canvas var1) {
        LOG.i("FocusedBasePositionManager", this.DEBUG, "drawStaticFrame");
        if(this.getSelectedView() != null) {
            if(this.mCurrentFrame < this.mFrameRate && this.mNeedDraw) {
                if(this.mIsFirstFrame) {
                    this.drawFirstFrame(var1, true, false);
                } else {
                    this.drawOtherFrame(var1, true, false);
                }
            } else if(this.mCurrentFrame == this.mFrameRate && this.mNeedDraw) {
                this.drawLastFrame(var1, true, false);
            } else if(!this.mConstrantNotDraw) {
                if(this.hasFocus()) {
                    Rect var2 = this.getDstRectBeforeScale(true);
                    if(var2 != null) {
                        this.mLastFocusRect = var2;
                        this.drawFocus(var1, false);
                    }
                }

                return;
            }
        } else {
            //Log.w("FocusedBasePositionManager", "drawStaticFrame select view is null");
        }

    }

    private void drawFirstFrame(Canvas var1, boolean var2, boolean var3) {
        boolean var4 = var3;
        float var5 = this.mScaleXValue;
        float var6 = this.mScaleXValue;
        if(1 == this.mode) {
            var4 = false;
            var5 = 1.0F;
            var6 = 1.0F;
        }

        this.mIsLastFrame = false;
        Rect var7;
        if(var4) {
            var7 = this.getDstRectBeforeScale(!var4);
            if(null == var7) {
                return;
            }

            this.mFocusRect = var7;
        } else {
            var7 = this.getDstRectAfterScale(!var4);
            if(null == var7) {
                return;
            }

            this.mFocusRect = var7;
        }

        this.mCurrentRect = this.mFocusRect;
        LOG.d("FocusedBasePositionManager", this.DEBUG, "drawFirstFrame: mFocusRect = " + this.mFocusRect + ", this = " + this);
        this.drawScale(var2);
        if(this.hasFocus()) {
            this.drawFocus(var1, var3);
        }

        this.mIsFirstFrame = false;
        ++this.mCurrentFrame;
        this.mContainerView.invalidate();
    }

    private void drawOtherFrame(Canvas var1, boolean var2, boolean var3) {
        LOG.i("FocusedBasePositionManager", this.DEBUG, "drawOtherFrame, this = " + this);
        this.mIsLastFrame = false;
        this.drawScale(var2);
        if(this.hasFocus()) {
            this.drawFocus(var1, var3);
        }

        ++this.mCurrentFrame;
        this.mContainerView.invalidate();
    }

    private void drawLastFrame(Canvas var1, boolean var2, boolean var3) {
        LOG.i("FocusedBasePositionManager", this.DEBUG, "drawLastFrame, this = " + this);
        Rect var4 = this.getDstRectBeforeScale(true);
        if(null != var4) {
            this.mIsLastFrame = true;
            this.drawScale(var2);
            if(this.hasFocus()) {
                this.drawFocus(var1, var3);
            }

            this.mCurrentFrame = 1;
            this.mScaleLastView = this.mScaleCurrentView;
            this.mLastSelectedView = this.getSelectedView();
            this.mNeedDraw = false;
            this.mIsFirstFrame = true;
            this.mLastFocusRect = var4;
            this.setState(0);
        }
    }

    private void scaleSelectedView() {
        View var1 = this.getSelectedView();
        if(var1 != null) {
            float var2 = this.mScaleXValue - 1.0F;
            float var3 = this.mScaleYValue - 1.0F;
            int var4 = this.mFrameRate;
            int var5 = this.mCurrentFrame;
            if(1 == this.mode) {
                var4 = this.mScaleFrameRate;
                var5 -= this.mFocusFrameRate;
                if(var5 <= 0) {
                    return;
                }
            }

            float var6 = 1.0F + var2 * (float)var5 / (float)var4;
            float var7 = 1.0F + var3 * (float)var5 / (float)var4;
            LOG.d("FocusedBasePositionManager", this.DEBUG, "scaleSelectedView: dstScaleX = " + var6 + ", dstScaleY = " + var7 + ", mScaleXValue = " + this.mScaleXValue + ", mScaleYValue = " + this.mScaleYValue + ", Selected View = " + var1 + ", this = " + this);
            var1.setScaleX(var6);
            var1.setScaleY(var7);
        }

    }

    private void scaleLastSelectedView() {
        if(this.mLastSelectedView != null) {
            float var1 = this.mScaleXValue - 1.0F;
            float var2 = this.mScaleYValue - 1.0F;
            int var3 = this.mFrameRate;
            int var4 = this.mCurrentFrame;
            if(1 == this.mode) {
                var3 = this.mScaleFrameRate;
                if(var4 > var3) {
                    return;
                }
            }

            var4 = var3 - var4;
            float var5 = 1.0F + var1 * (float)var4 / (float)var3;
            float var6 = 1.0F + var2 * (float)var4 / (float)var3;
            LOG.d("FocusedBasePositionManager", this.DEBUG, "scaleLastSelectedView: dstScaleX = " + var5 + ", dstScaleY = " + var6 + ", mScaleXValue = " + this.mScaleXValue + ", mScaleYValue = " + this.mScaleYValue + ", mLastSelectedView = " + this.mLastSelectedView + ", this = " + this + ", mCurrentFrame = " + this.mCurrentFrame);
            this.mLastSelectedView.setScaleX(var5);
            this.mLastSelectedView.setScaleY(var6);
        }

    }

    private void drawScale(boolean var1) {
        LOG.i("FocusedBasePositionManager", this.DEBUG, "drawScale: mCurrentFrame = " + this.mCurrentFrame + ", mScaleXValue = " + this.mScaleXValue + ", mScaleYValue = " + this.mScaleYValue + ", this = " + this + ", mScaleCurrentView = " + this.mScaleCurrentView + ", mScaleLastView = " + this.mScaleLastView);
        if(this.hasFocus() && var1 && this.mScaleCurrentView && this.mIsScale) {
            this.scaleSelectedView();
        }

        if(this.mScaleLastView && this.mIsScale) {
            this.scaleLastSelectedView();
        }

    }

    private void drawFocus(Canvas var1, boolean var2) {
        LOG.i("FocusedBasePositionManager", this.DEBUG, "drawFocus: mCurrentFrame = " + this.mCurrentFrame + ", mScaleXValue = " + this.mScaleXValue + ", mScaleYValue = " + this.mScaleYValue + ", this = " + this);
        if(!this.mConstrantNotDraw) {
            if(var2 && this.mTransAnimation && this.mLastFocusRect != null && this.getState() != 0 && !this.isLastFrame()) {
                this.drawDynamicFocus(var1);
            } else {
                this.drawStaticFocus(var1);
            }

        }
    }

    private void drawStaticFocus(Canvas var1) {
        float var2 = this.mScaleXValue - 1.0F;
        float var3 = this.mScaleYValue - 1.0F;
        int var4 = this.mFrameRate;
        int var5 = this.mCurrentFrame;
        float var6 = 1.0F + var2 * (float)var5 / (float)var4;
        float var7 = 1.0F + var3 * (float)var5 / (float)var4;
        LOG.i("FocusedBasePositionManager", this.DEBUG, "drawStaticFocus: mCurrentFrame = " + this.mCurrentFrame + ", dstScaleX = " + var6 + ", dstScaleY = " + var7 + ", mScaleXValue = " + this.mScaleXValue + ", mScaleYValue = " + this.mScaleYValue + ", this = " + this);
        Rect var8 = this.getDstRectAfterScale(true);
        if(null != var8) {
            if(this.mSelectedView != null && var1 != null && (this.mState == 0 || this.isLastFrame())) {
                this.drawChild(var1);
            }
            this.mFocusRect = var8;
            this.mCurrentRect = var8;
            if(this.isLastFrame()) {
                this.mMySelectedDrawableShadow.setBounds(var8);
                this.mMySelectedDrawableShadow.draw(var1);
                this.mMySelectedDrawableShadow.setVisible(true, true);
            } else {
                this.mMySelectedDrawable.setBounds(var8);
                this.mMySelectedDrawable.draw(var1);
                this.mMySelectedDrawable.setVisible(true, true);
            }



        }
    }

    private void drawDynamicFocus(Canvas var1) {
        Rect var2 = new Rect();
        int var3 = this.mFrameRate;
        if(1 == this.mode) {
            var3 = this.mFocusFrameRate;
        }
        if(this.mSelectedView != null && var1 != null && (this.mState == 0 || this.isLastFrame())) {
            this.drawChild(var1);
        }
        int var4 = this.mFocusRect.left - this.mLastFocusRect.left;
        int var5 = this.mFocusRect.right - this.mLastFocusRect.right;
        int var6 = this.mFocusRect.top - this.mLastFocusRect.top;
        int var7 = this.mFocusRect.bottom - this.mLastFocusRect.bottom;
        var2.left = this.mLastFocusRect.left + var4 * this.mCurrentFrame / var3;
        var2.right = this.mLastFocusRect.right + var5 * this.mCurrentFrame / var3;
        var2.top = this.mLastFocusRect.top + var6 * this.mCurrentFrame / var3;
        var2.bottom = this.mLastFocusRect.bottom + var7 * this.mCurrentFrame / var3;
        LOG.i("FocusedBasePositionManager", this.DEBUG, "drawDynamicFocus: mCurrentFrame = " + this.mCurrentFrame + ", dstRect.left = " + var2.left + ", dstRect.right = " + var2.right + ", dstRect.top = " + var2.top + ", dstRect.bottom = " + var2.bottom + ", this = " + this);
        this.mCurrentRect = var2;
        this.mMySelectedDrawable.setBounds(var2);
        this.mMySelectedDrawable.draw(var1);
        this.mMySelectedDrawable.setVisible(true, true);

    }

    public void computeScaleXY() {
        if(2 == this.mScaledMode || 3 == this.mScaledMode) {
            View var1 = this.getSelectedView();
            int[] var2 = new int[2];
            var1.getLocationOnScreen(var2);
            int var3 = var1.getWidth();
            int var4 = var1.getHeight();
            if(2 == this.mScaledMode) {
                this.mScaleXValue = (float)(var3 + this.mFixedScaledX) / (float)var3;
                this.mScaleYValue = this.mScaleXValue;
            } else if(3 == this.mScaledMode) {
                this.mScaleXValue = (float)(var4 + this.mFixedScaledY) / (float)var4;
                this.mScaleYValue = this.mScaleXValue;
            }

            LOG.d("FocusedBasePositionManager", this.DEBUG, "computeScaleXY mScaleXValue = " + this.mScaleXValue + ", mScaleYValue = " + this.mScaleYValue + ", mFixedScaledX = " + this.mFixedScaledX + ", mFixedScaledY = " + this.mFixedScaledY + ", height = " + var4 + ", width = " + var3);
        }

    }

    public abstract Rect getDstRectBeforeScale(boolean var1);

    public abstract Rect getDstRectAfterScale(boolean var1);

    public abstract void drawChild(Canvas var1);

    public interface FocusItemSelectedListener {
        void onItemSelected(View var1, int var2, boolean var3, View var4);
    }

    public interface PositionInterface {
        void setManualPadding(int var1, int var2, int var3, int var4);

        void setFrameRate(int var1);

        void setFocusResId(int var1);

        void setFocusShadowResId(int var1);

        void setItemScaleValue(float var1, float var2);

        void setFocusMode(int var1);

        void setFocusViewId(int var1);

        void setOnItemClickListener(AdapterView.OnItemClickListener var1);

        void setOnItemSelectedListener(FocusedBasePositionManager.FocusItemSelectedListener var1);
    }
}
