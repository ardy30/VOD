package com.ppfuns.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.AttributeSet;
import android.view.View;

import com.ppfuns.util.LogUtils;
import com.ppfuns.vod.R;

/**
 * 创建者     庄丰泽
 * 创建时间   2016/10/18 11:44
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class SearchGridLayoutManagerTV extends GridLayoutManagerTV {


    private Context mContext;

    public SearchGridLayoutManagerTV(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public SearchGridLayoutManagerTV(Context context, int spanCount) {
        super(context, spanCount);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    RecyclerView mParent;
    int mTopPadding    = 0;
    int mBottomPadding = 0;
    int mLeftPadding   = 0;
    int mRightPadding  = 0;
    int mDy            = 0;
    private OnChildSelectedListener mChildSelectedListener = null;
    View              mSelectedView;
    SelectionNotifier mSelectionNotifier;
    boolean isFirst = true;

    @Override
    public boolean requestChildRectangleOnScreen(final RecyclerView parent, View child, Rect rect, boolean immediate) {

        mParent = parent;
        int topPadding = mTopPadding;
        int bottomPadding = mBottomPadding;
        int leftPadding = mLeftPadding;
        int rightPadding = mRightPadding;
        //
        final int parentLeft = getPaddingLeft();
        final int parentTop = getPaddingTop();
        final int parentRight = getWidth() - getPaddingRight();
        final int parentBottom = getHeight() - getPaddingBottom();
        final int childLeft = child.getLeft() + rect.left;
        final int childTop = child.getTop() + rect.top;
        final int childRight = childLeft + rect.width();
        final int childBottom = childTop + rect.height();

        final int offScreenLeft = Math.min(0, childLeft - parentLeft - leftPadding);
        final int offScreenTop = Math.min(0, childTop - parentTop - topPadding);
        final int offScreenRight = Math.max(0, childRight - parentRight + leftPadding);
        final int offScreenBottom = Math.max(0, childBottom - parentBottom + bottomPadding);

        Rect childRect = new Rect(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
        // Favor the "start" layout direction over the end when bringing one
        // side or the other
        // of a large rect into view. If we decide to bring in end because start
        // is already
        // visible, limit the scroll such that start won't go out of bounds.
        final int dx;
        if (getLayoutDirection() == ViewCompat.LAYOUT_DIRECTION_RTL) {
            dx = offScreenRight != 0 ? offScreenRight : Math.max(offScreenLeft, childRight - parentRight);
        } else {
            dx = offScreenLeft != 0 ? offScreenLeft : Math.min(childLeft - parentLeft, offScreenRight);
        }

        // Favor bringing the top into view over the bottom. If top is already
        // visible and
        // we should scroll to make bottom visible, make sure top does not go
        // out of bounds.
        int dy = offScreenTop != 0 ? offScreenTop : Math.min(childTop - parentTop, offScreenBottom);
        //
        this.mDy = dy;
        mSelectedView = child;
        if (mSelectionNotifier == null) {
            mSelectionNotifier = new SelectionNotifier();
        }
        //
        if (dx != 0 || dy != 0) {
            if (immediate) {
                parent.scrollBy(dx, dy);
            } else {
                parent.smoothScrollBy(dx, dy);
            }
            //
            if (isFirst) {
                parent.addOnScrollListener(new OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            parent.post(mSelectionNotifier);
                        } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        }
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        //						parent.post(mSelectionNotifier);
                    }
                });
                isFirst = false;
            }
            return true;
        }
        //
        parent.post(mSelectionNotifier);
        return false;
    }

    /**
     * 焦点搜索失败处理.
     */
    @Override
    public View onFocusSearchFailed(View focused, int focusDirection, RecyclerView.Recycler recycler,
                                    RecyclerView.State state) {
        //		View nextFocus = super.onFocusSearchFailed(focused, focusDirection, recycler, state);

        if (focused != null) {
            int focusPos=0;
            try {
                focusPos =  this.getPosition(focused);
            } catch (ClassCastException e) {

            }

            int nextPos = focusPos + calcOffsetToNextView(focusDirection);
            View view = findViewByPosition(nextPos);
            if (view == null) {
                View view1 = null;
                int scrollHeight = mContext.getResources().getDimensionPixelSize(R.dimen.search_gird_poster_item_height);
                if (focusDirection == View.FOCUS_DOWN) {
                    scrollVerticallyBy(scrollHeight, recycler, state);
                    if (focusPos % 2 == 0) {
                        view1 = getChildAt(9);
                    } else {

                        view1 = getChildAt(10);
                    }
                } else if (focusDirection == View.FOCUS_UP) {
                    scrollVerticallyBy(-scrollHeight, recycler, state);
                    if (focusPos % 2 == 0) {
                        view1 = getChildAt(0);
                    } else {

                        view1 = getChildAt(1);
                    }
                }
                if (view1 != null) {
                    int view1Pos = getPosition(view1);
                    LogUtils.i("Decode", "onFocusSearchFailed: " + focusPos + ", nextPos: " + nextPos + ", view: " + view + ", view1: " + view1 + ", pos: " + view1Pos);
                    return view1;
                }
            }
            LogUtils.i("Decode", "onFocusSearchFailed_position: " + focusPos + ", nextPos: " + nextPos + ", view: " + view);
            return view;
        }

        return null;

    }

    protected int calcOffsetToNextView(int direction) {
        int orientation = getOrientation();
        if (orientation == VERTICAL) {
            switch (direction) {
                case View.FOCUS_DOWN:
                    return 2;
                case View.FOCUS_UP:
                    return -2;
            }
        } else if (orientation == HORIZONTAL) {
            switch (direction) {
                case View.FOCUS_RIGHT:
                    return 1;
                case View.FOCUS_LEFT:
                    return -1;
            }
        }
        return 0;
    }

    private class SelectionNotifier implements Runnable {
        @Override
        public void run() {
            fireOnSelected();
        }
    }

    public View getSelectedView() {
        return mSelectedView;
    }

    public void setTopPadding(int topPadding) {
        this.mTopPadding = topPadding;
    }

    public void setBottomPadding(int bottomPadding) {
        this.mBottomPadding = bottomPadding;
    }

    public void setLeftPadding(int leftPadding) {
        this.mLeftPadding = leftPadding;
    }

    public void setRightPadding(int rightPadding) {
        this.mRightPadding = rightPadding;
    }

    private void fireOnSelected() {
        if (mChildSelectedListener != null) {
            int pos = getPosition(getSelectedView());
            View view = getSelectedView();
            mChildSelectedListener.onChildSelected(mParent, getSelectedView(), pos, mDy);
        }
    }

    public void setOnChildSelectedListener(OnChildSelectedListener listener) {
        mChildSelectedListener = listener;
    }
}
