package com.ppfuns.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.Scroller;

import com.ppfuns.util.LOG;

public class FocusedGridView extends GridView implements FlingManager.FlingCallback {
    private static final String TAG = "FocusedGridView";
    private static final boolean DEBUG = true;
    private static final int SCROLLING_DURATION = 1200;
    private static final int SCROLLING_DELAY = 10;
    private static final int SCROLL_UP = 0;
    private static final int SCROLL_DOWN = 1;
    private long KEY_INTERVEL = 250L;
    private long mKeyTime = 0L;
    protected int mCurrentPosition = -1;
    private int mLastPosition = -1;
    private OnScrollListener mOuterScrollListener;
    private boolean isScrolling = false;
    private Object lock = new Object();
    private int mStartX;
    //private FocusedGridView.FocusedGridPositionManager mPositionManager;
    private OnItemClickListener mOnItemClickListener = null;
    private FocusedBasePositionManager.FocusItemSelectedListener mOnItemSelectedListener = null;
    private int mFocusViewId = -1;
    private boolean mIsFocusInit = false;
    private int mLastOtherPosition = -1;
    private boolean mInit = false;
    private boolean mAutoChangeLine = true;
    private int mScrollDirection = 1;
    private int mLastScrollDirection = 1;
    private int mScrollDuration = 500;
    private FocusedGridView.FocusDrawListener mFocusDrawListener = null;
    private OnScrollListener mOnScrollListener = new OnScrollListener() {
        public void onScrollStateChanged(AbsListView var1, int var2) {
            if (FocusedGridView.this.mOuterScrollListener != null) {
                FocusedGridView.this.mOuterScrollListener.onScrollStateChanged(var1, var2);
            }
            if (scrollChangeListener != null) {
                scrollChangeListener.scrollMode(var2);
            }
            switch (var2) {
                case 0:
                    LOG.d(TAG, true, "onScrollStateChanged idle");
                    FocusedGridView.this.setScrolling(false);
                    if (FocusedGridView.this.getSelectedView() != null) {
                        FocusedGridView.this.performItemSelect(FocusedGridView.this.getSelectedView(), FocusedGridView.this.mCurrentPosition, true);
                    }
                    break;
                case 1:
                case 2:
                    LOG.d(TAG, true, "onScrollStateChanged fling");
                    FocusedGridView.this.setScrolling(true);
            }
        }

        public void onScroll(AbsListView var1, int var2, int var3, int var4) {
            if (FocusedGridView.this.mOuterScrollListener != null) {
                FocusedGridView.this.mOuterScrollListener.onScroll(var1, var2, var3, var4);
            }

        }
    };
    public static int FOCUS_ITEM_REMEMBER_LAST = 0;
    public static int FOCUS_ITEM_AUTO_SEARCH = 1;
    private int focusPositionMode;
    boolean isKeyDown;
    int mScrollDistance;
    int mScrollY;
    Handler mHandler;
    FlingManager mFlingManager;

    public void setFocusDrawListener(FocusedGridView.FocusDrawListener var1) {
        this.mFocusDrawListener = var1;
    }

    public void setScrollDuration(int var1) {
        this.mScrollDuration = var1;
    }

    public void setAutoChangeLine(boolean var1) {
        this.mAutoChangeLine = var1;
    }

    public void setFocusViewId(int var1) {
        this.mFocusViewId = var1;
    }

    public void setOnItemClickListener(OnItemClickListener var1) {
        this.mOnItemClickListener = var1;
    }

    public void setOnItemSelectedListener(FocusedBasePositionManager.FocusItemSelectedListener var1) {
        this.mOnItemSelectedListener = var1;
    }

//    public void setFrameRate(int var1) {
//        this.mPositionManager.setFrameRate(var1);
//    }
//
//    public void setScale(boolean var1) {
//        this.mPositionManager.setScale(var1);
//    }

    public FocusedGridView(Context var1) {
        super(var1);
        this.focusPositionMode = FOCUS_ITEM_REMEMBER_LAST;
        this.isKeyDown = false;
        this.mScrollDistance = 0;
        this.mScrollY = 0;
        this.mHandler = new Handler() {
            public void handleMessage(Message var1) {
                switch (var1.what) {
                    case 1:
                        if (FocusedGridView.this.getSelectedView() == null) {
                            Log.w(TAG, "Handler handleMessage selected view is null delay");
                            this.sendEmptyMessageDelayed(1, 10L);
                            return;
                        } else {
                            FocusedGridView.this.performItemSelect(FocusedGridView.this.getSelectedView(), FocusedGridView.this.mCurrentPosition, true);
                            if (!FocusedGridView.this.isScrolling()) {
                                FocusedGridView.this.invalidate();
                            }
                        }
                    default:
                }
            }
        };
        this.init(var1);
    }

    public FocusedGridView(Context var1, AttributeSet var2) {
        super(var1, var2);
        this.focusPositionMode = FOCUS_ITEM_REMEMBER_LAST;
        this.isKeyDown = false;
        this.mScrollDistance = 0;
        this.mScrollY = 0;
        this.mHandler = new Handler() {
            public void handleMessage(Message var1) {
                switch (var1.what) {
                    case 1:
                        if (FocusedGridView.this.getSelectedView() == null) {
                            Log.w(TAG, "Handler handleMessage selected view is null delay");
                            this.sendEmptyMessageDelayed(1, 10L);
                            return;
                        } else {
                            FocusedGridView.this.performItemSelect(FocusedGridView.this.getSelectedView(), FocusedGridView.this.mCurrentPosition, true);
                            if (!FocusedGridView.this.isScrolling()) {
                                FocusedGridView.this.invalidate();
                            }
                        }
                    default:
                }
            }
        };
        this.init(var1);
    }

    public FocusedGridView(Context var1, AttributeSet var2, int var3) {
        super(var1, var2, var3);
        this.focusPositionMode = FOCUS_ITEM_REMEMBER_LAST;
        this.isKeyDown = false;
        this.mScrollDistance = 0;
        this.mScrollY = 0;
        this.mHandler = new Handler() {
            public void handleMessage(Message var1) {
                switch (var1.what) {
                    case 1:
                        if (FocusedGridView.this.getSelectedView() == null) {
                            Log.w(TAG, "Handler handleMessage selected view is null delay");
                            this.sendEmptyMessageDelayed(1, 10L);
                            return;
                        } else {
                            if (!FocusedGridView.this.isScrolling()) {
                                FocusedGridView.this.invalidate();
                            }
                        }
                    default:
                }
            }
        };
        this.init(var1);
    }

    private void initLeftPosition() {
        if (!this.mInit) {
            this.mInit = true;
            int[] var1 = new int[2];
            this.getLocationOnScreen(var1);
            this.mStartX = var1[0] + this.getPaddingLeft();
            LOG.d(TAG, true, "initLeftPosition mStartX = " + this.mStartX);
        }

    }

    public void init(Context var1) {
        Log.i(TAG, "init mCurrentPosition11:" + this.mCurrentPosition);
        this.setChildrenDrawingOrderEnabled(true);
        //this.mPositionManager = new FocusedGridView.FocusedGridPositionManager(var1, this);
        super.setOnScrollListener(this.mOnScrollListener);
        Log.i(TAG, "init mCurrentPosition12:" + this.mCurrentPosition);
    }

    public boolean onTouchEvent(MotionEvent var1) {
        return true;
    }

    protected int getChildDrawingOrder(int var1, int var2) {
        int var3 = this.getSelectedItemPosition() - this.getFirstVisiblePosition();
        return var3 < 0 ? var2 : (var2 < var3 ? var2 : (var2 >= var3 ? var1 - 1 - var2 + var3 : var2));
    }

    public void setScrolling(boolean var1) {
        synchronized (this.lock) {
            this.isScrolling = var1;
        }
    }

    public boolean isScrolling() {
        synchronized (this.lock) {
            return this.isScrolling;
        }
    }

    public void dispatchDraw(Canvas var1) {
        LOG.d(TAG, true, "dispatchDraw child count = " + this.getChildCount() + ", first position = " + this.getFirstVisiblePosition() + ", last posititon = " + this.getLastVisiblePosition());
        super.dispatchDraw(var1);

    }

    protected void onDraw(Canvas var1) {
        if (this.mFocusDrawListener != null) {
            var1.save();
            this.mFocusDrawListener.beforFocusDraw(var1);
            var1.restore();
        }

        if (this.getSelectedView() != null && this.hasFocus()) {
            this.performItemSelect(this.getSelectedView(), this.mCurrentPosition, true);
        }

        if (this.getSelectedView() != null) {
            if (this.hasFocus()) {
                this.focusInit();
            }
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    public void getFocusedRect(Rect var1) {
        Log.i(TAG, "getFocusedRect mCurrentPosition" + this.mCurrentPosition + ",getFirstVisiblePosition:" + this.getFirstVisiblePosition() + ",getLastVisiblePosition:" + this.getLastVisiblePosition());
        if (this.mCurrentPosition < this.getFirstVisiblePosition() || this.mCurrentPosition > this.getLastVisiblePosition()) {
            this.mCurrentPosition = this.getFirstVisiblePosition();
            Log.i(TAG, "mCurrentPosition9:" + this.mCurrentPosition);
        }

        super.getFocusedRect(var1);
    }

    public void subSelectPosition() {
        this.arrowScroll(17);
    }

    public void setSelection(int var1) {
        LOG.i(TAG, true, "setSelection = " + var1 + ", mCurrentPosition = " + this.mCurrentPosition);
        if (isFromBack()) {
            setFromBack(false);
            super.setSelection(var1);
        }
        this.mLastPosition = this.mCurrentPosition;
        this.mCurrentPosition = var1;
        Log.i(TAG, "mCurrentPosition10:" + this.mCurrentPosition);
        this.performItemSelect(this.getSelectedView(), this.mCurrentPosition, true);
        this.requestLayout();
    }

    boolean fromBack;

    public boolean isFromBack() {
        return fromBack;
    }

    public void setFromBack(boolean fromBack) {
        this.fromBack = fromBack;
    }

    public void setOnScrollListener(OnScrollListener var1) {
        this.mOuterScrollListener = var1;
    }

    public void setFocusPositionMode(int var1) {
        LOG.i(TAG, true, "setFocusPositionMode mode = " + var1);
        this.focusPositionMode = var1;
    }

    public int getFocusPositionMode() {
        return this.focusPositionMode;
    }

    protected void onFocusChanged(boolean var1, int var2, Rect var3) {
        LOG.i(TAG, true, "onFocusChanged,gainFocus:" + var1 + ", mCurrentPosition = " + this.mCurrentPosition + ", child count = " + this.getChildCount());
        if (this.focusPositionMode == FOCUS_ITEM_AUTO_SEARCH) {
            LOG.d(TAG, true, "super.onFocusChanged");
            super.onFocusChanged(var1, var2, var3);
        }
        if (focusChangedListener != null) {
            focusChangedListener.focusLost(var1);
        }

        this.mKeyTime = System.currentTimeMillis();


        this.mIsFocusInit = false;

        this.focusInit();
        this.initLeftPosition();
    }

    private void focusInit() {
        if (!this.mIsFocusInit) {
            LOG.i(TAG, true, "focusInit mCurrentPosition = " + this.mCurrentPosition + ", getSelectedItemPosition() = " + this.getSelectedItemPosition());
            if (this.mCurrentPosition < 0) {
                Log.i(TAG, "mCurrentPosition1:" + this.mCurrentPosition);
                this.mCurrentPosition = this.getSelectedItemPosition();
            }

            if (this.mCurrentPosition < 0) {
                Log.i(TAG, "mCurrentPosition2:" + this.mCurrentPosition);
                this.mCurrentPosition = 0;
            }

            if (this.mCurrentPosition < this.getFirstVisiblePosition() || this.mCurrentPosition > this.getLastVisiblePosition()) {
                Log.i(TAG, "mCurrentPosition3:" + this.mCurrentPosition);
                this.mCurrentPosition = this.getFirstVisiblePosition();
            }

            if (!this.hasFocus()) {
                this.mLastPosition = this.mCurrentPosition;

            } else {
                if (this.focusPositionMode == FOCUS_ITEM_AUTO_SEARCH) {
                    this.mCurrentPosition = super.getSelectedItemPosition();
                    Log.i(TAG, "mCurrentPosition4:" + this.mCurrentPosition);
                } else {
                    this.setSelection(this.mCurrentPosition > -1 && this.mCurrentPosition < this.getCount() ? this.mCurrentPosition : 0);
                }
            }

            if (this.getSelectedView() != null) {
                this.performItemSelect(this.getSelectedView(), this.mCurrentPosition, this.hasFocus());

                if (this.mCurrentPosition >= 0) {
                    this.mIsFocusInit = true;
                }
            }
            this.invalidate();
        }
    }

    @Override
    public boolean isInTouchMode() {
        return !(hasFocus() && !super.isInTouchMode());
    }

    public void setItemScaleValue(float var1, float var2) {
    }

    public int getSelectedItemPosition() {
        return this.mCurrentPosition;
    }

    public int getLastSelectedItemPosition() {
        return this.mLastPosition;
    }

    public View getSelectedView() {
        if (this.getChildCount() <= 0) {
            return null;
        } else {
            int var1 = this.mCurrentPosition;
            if (var1 >= this.getFirstVisiblePosition() && var1 <= this.getLastVisiblePosition()) {

                int var2 = var1 - this.getFirstVisiblePosition();
                View var3 = this.getChildAt(var2);
                LOG.d(TAG, true, "getSelectedView getSelectedView: mCurrentPosition = " + this.mCurrentPosition + ", indexOfView = " + var2 + ", child count = " + this.getChildCount() + ", getFirstVisiblePosition() = " + this.getFirstVisiblePosition() + ", getLastVisiblePosition() = " + this.getLastVisiblePosition());
                return var3;
            } else {
                Log.w(TAG, "getSelectedView mCurrentPosition = " + this.mCurrentPosition + ", getFirstVisiblePosition() = " + this.getFirstVisiblePosition() + ", getLastVisiblePosition() = " + this.getLastVisiblePosition());
                return null;
            }
        }
    }

    private void performItemSelect(View var1, int var2, boolean var3) {
        if (this.mOnItemSelectedListener != null && var1 != null) {
            this.mOnItemSelectedListener.onItemSelected(var1, var2, var3, this);
        }

    }

    private void performItemClick() {
        View var1 = this.getSelectedView();
        if (var1 != null && this.mOnItemClickListener != null) {
            this.mOnItemClickListener.onItemClick(this, var1, this.mCurrentPosition, 0L);
        }

    }

    public boolean onKeyUp(int var1, KeyEvent var2) {
        LOG.i(TAG, true, "onKeyUp: " + "code: " + var1);
        if (this.getSelectedView() != null && this.getSelectedView().onKeyUp(var1, var2)) {
            this.isKeyDown = false;
            return true;
        } else if (var1 != KeyEvent.KEYCODE_DPAD_CENTER && var1 != KeyEvent.KEYCODE_ENTER) {
            if (this.isKeyDown) {
                this.isKeyDown = false;
                return true;
            } else {
                return super.onKeyUp(var1, var2);
            }
        } else {
            if (this.isKeyDown) {
                this.performItemClick();
            }

            this.isKeyDown = false;
            return true;
        }
    }

    public boolean onKeyDown(int var1, KeyEvent var2) {
        LOG.i(TAG, true, "onKeyDown  mKeyTime = " + this.mKeyTime);

        if (var1 != KeyEvent.KEYCODE_DPAD_CENTER && var1 != KeyEvent.KEYCODE_ENTER &&
                var1 != KeyEvent.KEYCODE_DPAD_LEFT && var1 != KeyEvent.KEYCODE_DPAD_RIGHT &&
                var1 != KeyEvent.KEYCODE_DPAD_DOWN && var1 != KeyEvent.KEYCODE_DPAD_UP) {
            return super.onKeyDown(var1, var2);
        } else {
            if (var2.getRepeatCount() == 0) {
                this.isKeyDown = true;
            }

            LOG.i(TAG, true, "onKeyDown keyCode = " + var1 + ", child count = " + this.getChildCount() + ", mCurrentPosition = " + this.mCurrentPosition);
            long currentTimes = System.currentTimeMillis();
            LOG.i(TAG, true, "onKeyDown code: " + var1 + "currentTimeMillis = " + currentTimes + ", mKeyTime = " + this.mKeyTime);
            if (currentTimes - this.mKeyTime <= this.KEY_INTERVEL) {
                return true;
            }
            if (this.isScrolling() && (this.mCurrentPosition < this.getFirstVisiblePosition() || this.mCurrentPosition > this.getLastVisiblePosition())) {
                return true;
            }

            this.mKeyTime = currentTimes;


            if (this.getChildCount() <= 0) {
                return true;
            } else {
                if (!this.mAutoChangeLine) {
                    if (var1 == KeyEvent.KEYCODE_DPAD_RIGHT && (this.mCurrentPosition + 1) % this.getNumColumns() == 0) {
                        return false;
                    }

                    if (var1 == KeyEvent.KEYCODE_DPAD_LEFT && this.mCurrentPosition != 0 && this.mCurrentPosition % this.getNumColumns() == 0) {
                        return false;
                    }
                }

                if (this.getSelectedView() != null && this.getSelectedView().onKeyDown(var1, var2)) {
                    return true;
                } else {
                    switch (var1) {
                        case KeyEvent.KEYCODE_DPAD_UP:
                            if (!this.arrowScroll(33)) {
                                Log.w(TAG, "arrowScroll up return false");
                                return false;
                            }

                            return true;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            if (!this.arrowScroll(130)) {
                                Log.w(TAG, "arrowScroll down return false");
                                return false;
                            }

                            return true;
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            if (!this.arrowScroll(17)) {
                                Log.w(TAG, "arrowScroll left return false");
                                return false;
                            }

                            return true;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            if (!this.arrowScroll(66)) {
                                Log.w(TAG, "arrowScroll right return false");
                                return false;
                            }

                            return true;
                        default:
                            return true;
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean arrowScroll(int var1) {
        if (this.mScrollDistance <= 0) {
            if (this.getCount() > 0) {
                this.mScrollDistance = this.getChildAt(0).getHeight() + this.getVerticalSpacing();
                LOG.d(TAG, true, "scrollBy: mScrollDistance " + this.mScrollDistance);
            }
        }

        LOG.i(TAG, true, "scrollBy:mCurrentPosition before " + this.mCurrentPosition);
        View var2 = this.getSelectedView();
        int var4 = this.mCurrentPosition;
        boolean var5 = true;
        int var6 = 0;
        int var7 = this.getNumColumns();
        int var8 = this.getListPaddingTop();
        int var9 = this.getHeight() - this.getListPaddingBottom();
        View var3;
        switch (var1) {
            case 17:
                if (this.mCurrentPosition <= 0) {
                    return false;
                }

                --this.mCurrentPosition;
                if ((this.mCurrentPosition + 1) % var7 == 0) {
                    var3 = this.getChildAt(this.mCurrentPosition - this.getFirstVisiblePosition());
                    if (var3 != null) {
                        LOG.d(TAG, true, "var3.getTop(): " + var3.getTop() + " getListPaddingTop: " + var8);
                        if (var3.getTop() < var8) {
                            this.endFling();
                            var6 = var3.getTop() - var8;
                        }

                        var5 = false;
                    } else {
                        LOG.d(TAG, true, "var3 is null: ");
                        this.endFling();
                        var6 = -this.mScrollDistance;
                    }
                }

                if (var6 != 0) {
                    this.mLastScrollDirection = this.mScrollDirection;
                    this.mScrollDirection = 0;
                }
                break;
            case 33:
                if (this.mCurrentPosition < var7) {
                    return false;
                }

                this.mCurrentPosition -= var7;
                var3 = this.getChildAt(this.mCurrentPosition - this.getFirstVisiblePosition());

                if (var3 != null) {
                    LOG.d(TAG, true, "var3.getTop(): " + var3.getTop() + " getListPaddingTop: " + var8);
                    if (var3.getTop() < var8) {
                        this.endFling();
                        var6 = var3.getTop() - var8;
                    }
                } else {
                    this.endFling();
                    var6 = -this.mScrollDistance;
                }

                if (var6 != 0) {
                    this.mLastScrollDirection = this.mScrollDirection;
                    this.mScrollDirection = 0;
                }
                break;
            case 66:
                if (this.mCurrentPosition >= this.getCount() - 1) {
                    return false;
                }

                ++this.mCurrentPosition;
                if (this.mCurrentPosition % var7 == 0) {
                    var3 = this.getChildAt(this.mCurrentPosition - this.getFirstVisiblePosition());
                    if (var3 != null) {
                        LOG.d(TAG, true, "var3.getBottom(): " + var3.getBottom() + " getListPaddingBottom: " + var9);
                        if (var3.getBottom() > var9) {
                            this.endFling();
                            var6 = var3.getBottom() - var9;
                        }
                        var5 = false;
                    } else {
                        this.endFling();
                        var6 = this.mScrollDistance;
                    }
                }

                if (var6 != 0) {
                    this.mLastScrollDirection = this.mScrollDirection;
                    this.mScrollDirection = 1;
                }
                break;
            case 130:
                if (this.mCurrentPosition / var7 >= (this.getCount() - 1) / var7) {
                    return false;
                }

                this.mCurrentPosition += var7;
                this.mCurrentPosition = this.mCurrentPosition > this.getCount() - 1 ? this.getCount() - 1 : this.mCurrentPosition;
                Log.i(TAG, "mCurrentPosition6:" + this.mCurrentPosition);
                var3 = this.getChildAt(this.mCurrentPosition - this.getFirstVisiblePosition());
                if (var3 != null) {
                    LOG.d(TAG, true, "var3.getBottom(): " + var3.getBottom() + " getListPaddingBottom: " + var9);
                    if (var3.getBottom() > var9) {
                        this.endFling();
                        var6 = var3.getBottom() - var9;
                    }
                } else {
                    this.endFling();
                    var6 = this.mScrollDistance;
                }

                if (var6 != 0) {
                    this.mLastScrollDirection = this.mScrollDirection;
                    this.mScrollDirection = 1;
                }
        }

        LOG.d(TAG, true, "arrowScroll: mCurrentPosition = " + this.mCurrentPosition);
        this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(var1));
        if (var4 != this.mCurrentPosition) {
            this.mLastPosition = var4;
        }
        boolean var10 = true;
        boolean var11 = true;
        LOG.d(TAG, true, "arrowScroll this.mLastPosition = " + this.mLastPosition + ", this.mCurrentPosition = " + this.mCurrentPosition + ", lastPosition = " + var4);

        if (var2 != null) {
            this.performItemSelect(var2, var4, false);
        }

        if (this.getSelectedView() != null && this.getSelectedView() != var2 && var4 != this.mCurrentPosition) {
            this.performItemSelect(this.getSelectedView(), this.mCurrentPosition, true);
        }
        if (var6 != 0) {
            LOG.i(TAG, true, "scrollBy: scrollBy = " + var6);
            this.arrowSmoothScroll(var6);
            this.mHandler.sendEmptyMessageDelayed(1, 10L);
        } else {
            this.invalidate();
        }

        return true;

    }

    void arrowSmoothScroll(int var1) {
        boolean var2 = this.isScrolling();
        this.endFling();
        int var3 = this.getCurrentY();
        LOG.d(TAG, true, "arrowSmoothScroll currY = " + var3 + ", mCurrentPosition = " + this.mCurrentPosition + ", isScrolling = " + var2);
        if (this.mScrollY < 0) {
            var3 -= 2147483647;
        }

        LOG.d(TAG, true, "arrowSmoothScroll currY = " + var3 + ", mScrollY = " + this.mScrollY + ", scrollBy = " + var1);
        this.mScrollY -= var3;
        if (this.mScrollDirection != this.mLastScrollDirection) {
            this.mScrollY = 0;
            boolean var4 = false;
        }

        this.mScrollY += var1;
        LOG.d(TAG, true, "arrowSmoothScroll mScrollY = " + this.mScrollY);
        this.smoothScrollBy(this.mScrollY, this.mScrollDuration);
    }

    void setNextSelectedPositionInt(int var1) {
    }

    void endFling() {
        if (this.mFlingManager != null) {
            this.mFlingManager.endFling();
        }
    }

    int getCurrentY() {
        return this.mFlingManager != null ? this.mFlingManager.getActualY() : 0;
    }

//    private boolean checkFocusPosition() {
//        Rect var1 = this.mPositionManager.getDstRectAfterScale(true);
//        return null != this.mPositionManager.getCurrentRect() && this.hasFocus() && null != var1 && this.isShown() && !this.mPositionManager.getContrantNotDraw() ? Math.abs(var1.left - this.mPositionManager.getCurrentRect().left) > 5 || Math.abs(var1.right - this.mPositionManager.getCurrentRect().right) > 5 || Math.abs(var1.top - this.mPositionManager.getCurrentRect().top) > 5 || Math.abs(var1.bottom - this.mPositionManager.getCurrentRect().bottom) > 5 : false;
//    }

    public void smoothScrollBy(int var1, int var2) {
        if (this.mFlingManager == null) {
            this.mFlingManager = new FlingManager(this, this);
        }

        int var3 = this.getFirstVisiblePosition();
        int var4 = this.getChildCount();
        int var5 = var3 + var4 - 1;
        int var6 = this.getPaddingTop();
        int var7 = this.getHeight() - this.getPaddingBottom();
        if (var1 != 0 && this.getAdapter().getCount() != 0 && var4 != 0 && (var3 != 0 || this.getChildAt(0).getTop() != var6 || var1 >= 0) && (var5 != this.getAdapter().getCount() - 1 || this.getChildAt(var4 - 1).getBottom() != var7 || var1 <= 0)) {
            this.mFlingManager.focusedReportScrollStateChange(2);
            this.mFlingManager.startScroll(var1, var2);
        } else {
            this.mFlingManager.endFling();
        }

    }

    public void flingLayoutChildren() {
        this.layoutChildren();
    }

    public int getClipToPaddingMask() {
        return 34;
    }

    public boolean flingOverScrollBy(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9) {
        return this.overScrollBy(var1, var2, var3, var4, var5, var6, var7, var8, var9);
    }

    public void flingDetachViewsFromParent(int var1, int var2) {
        this.detachViewsFromParent(var1, var2);
    }

    public boolean flingAwakenScrollBars() {
        return this.awakenScrollBars();
    }

    public interface ScrollerListener {
        void horizontalSmoothScrollBy(int var1, int var2);

        int getCurrX(boolean var1);

        int getFinalX(boolean var1);

        boolean isFinished();
    }

    class FocusedScroller extends Scroller {
        public FocusedScroller(Context var2, Interpolator var3, boolean var4) {
            super(var2, var3, var4);
        }

        public FocusedScroller(Context var2, Interpolator var3) {
            super(var2, var3);
        }

        public FocusedScroller(Context var2) {
            super(var2, new AccelerateDecelerateInterpolator());
        }

        public boolean computeScrollOffset() {
            boolean var1 = this.isFinished();
            //boolean var2 = FocusedGridView.this.checkFocusPosition();
            if (!var1) {
                FocusedGridView.this.invalidate();
            }

            boolean var3 = super.computeScrollOffset();
            return var3;
        }
    }

    public interface FocusDrawListener {
        void beforFocusDraw(Canvas var1);

        void drawChild(Canvas var1);
    }

    private FocusChangedListener focusChangedListener;

    public FocusChangedListener getFocusChangedListener() {
        return focusChangedListener;
    }

    public void setFocusChangedListener(FocusChangedListener focusChangedListener) {
        this.focusChangedListener = focusChangedListener;
    }

    public interface FocusChangedListener {
        void focusLost(boolean hasFocus);
    }

    private ScrollChangeListener scrollChangeListener;

    public interface ScrollChangeListener {
        void scrollMode(int scrollMode);
    }

    public ScrollChangeListener getScrollChangeListener() {
        return scrollChangeListener;
    }

    public void setScrollChangeListener(ScrollChangeListener scrollChangeListener) {
        this.scrollChangeListener = scrollChangeListener;
    }
}
