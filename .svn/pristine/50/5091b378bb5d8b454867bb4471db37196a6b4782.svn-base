package com.ppfuns.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;

import com.ppfuns.util.LogUtils;

/**
 * GridView TV版本.
 * @author hailongqiu 356752238@qq.com
 *
 */
public class GridViewTV extends GridView {
	private boolean isScroll = false;
	private int position = 0;
	//  private int iCount;
	private int iColumns;
	private int iFirstView;
	private int iLastView;
	private int iselecte;
	private boolean isFirst = true;
	static int iPageNum = 0;

	public GridViewTV(Context context) {
		super(context);
		init(context, null);
	}

	public GridViewTV(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	/**
	 * 崩溃了.
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		try {
			super.dispatchDraw(canvas);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	WidgetTvViewBring mWidgetTvViewBring;
	
	private void init(Context context, AttributeSet attrs) {
		this.setChildrenDrawingOrderEnabled(true);
		mWidgetTvViewBring = new WidgetTvViewBring(this); 
	}

	@Override
	public void bringChildToFront(View child) {
		mWidgetTvViewBring.bringChildToFront(this, child);
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		// position = getSelectedItemPosition() - getFirstVisiblePosition();
		return mWidgetTvViewBring.getChildDrawingOrder(childCount, i);
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		final int lastSelectItem = getSelectedItemPosition();
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		if (gainFocus) {
			setSelection(lastSelectItem);
		}
	}

	@Override
	public boolean isInTouchMode() {
		return !(hasFocus() && !super.isInTouchMode());
	}
}
