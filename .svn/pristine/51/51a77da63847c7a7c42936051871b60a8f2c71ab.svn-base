package com.ppfuns.ui.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class RecyclerViewTV extends RecyclerView {

	public RecyclerViewTV(Context context) {
		super(context);
		init(context);
	}

	public RecyclerViewTV(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public RecyclerViewTV(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	WidgetTvViewBring mWidgetTvViewBring;

	private void init(Context context) {
		setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
		setHasFixedSize(true);
		setWillNotDraw(true);
		setOverScrollMode(View.OVER_SCROLL_NEVER);
		setChildrenDrawingOrderEnabled(true);
		mWidgetTvViewBring = new WidgetTvViewBring(this);
	}

	@Override
	public void bringChildToFront(View child) {
		mWidgetTvViewBring.bringChildToFront(this, child);
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		return mWidgetTvViewBring.getChildDrawingOrder(childCount, i);
	}

	private int lastWidth;
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (getChildCount() > 0) {
			int newWidth = 0;
			for (int i = 0; i < getChildCount(); i++) {
				newWidth += getChildAt(i).getMeasuredWidth();
			}
			if (lastWidth!=newWidth) {
				lastWidth = newWidth;

				int empty = getMeasuredWidth() - newWidth;
				if (empty > 0) {
					if (getPaddingLeft() == empty / 2) {
						return;
					}

					setPadding(empty / 2, 0, empty / 2, 0);
					//如果不再一次onLayout，子view就不会有padding
					super.onLayout(changed, l, t, r, b);
				}
			}
		}
	}

	//    //固定宽度的居中方式
	//    protected void onMeasure(int widthSpec, int heightSpec) {
	//        if (getAdapter()==null) {
	//            super.onMeasure(widthSpec, heightSpec);
	//        } else {
	//            int specHeight = MeasureSpec.getSize(heightSpec);
	//            int maxHeight = MeasureSpec.makeMeasureSpec(specHeight, MeasureSpec.AT_MOST);
	//            //item_data weidth:60dp
	//            setMeasuredDimension(DensityUtil.dip2px(getContext(),60*getAdapter().getItemCount()), maxHeight);
	//        }
	//    }

}
