package com.ppfuns.ui.view;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.OverScroller;

import com.ppfuns.util.LOG;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class FlingManager implements Runnable {
	private static final String TAG = "FlingManager";
	private static final boolean DEBUG = true;
	private final OverScroller mScroller;
	private final FocusedGridView mGridView;
	private int mLastFlingY;
	int mActualY;
	FlingManager.FlingCallback mFlingCallback;
	private static final int FLYWHEEL_TIMEOUT = 40;

	int getActualY() {
		synchronized(this) {
			return this.mActualY;
		}
	}

	public FlingManager(FocusedGridView var1, FlingManager.FlingCallback var2) {
		this.mGridView = var1;
		this.mFlingCallback = var2;
		this.mScroller = new OverScroller(this.mGridView.getContext());
	}

	void startScroll(int var1, int var2) {
		LOG.i("FlingManager", true, "FlingRunnable startScroll distance = " + var1 + ", duration");
		int var3 = var1 < 0?2147483647:0;
		this.mLastFlingY = var3;
		this.mActualY = var3;
		this.mScroller.startScroll(0, var3, 0, var1, var2);
		this.setTouchMode(4);
		this.mGridView.post(this);
	}

	void endFling() {
		LOG.i("FlingManager", true, "FlingRunnable endFling");
		this.setTouchMode(-1);
		this.mGridView.removeCallbacks(this);
		this.focusedReportScrollStateChange(0);
		this.focusedClearScrollingCache();
		this.mScroller.abortAnimation();
		this.finishFlingStrictSpan();
	}

	public void run() {
		int var1 = this.getTouchMode();
		switch(var1) {
			case 4:
				if(this.getDataChanged()) {
					this.mFlingCallback.flingLayoutChildren();
				}

				if(this.mGridView.getAdapter().getCount() != 0 && this.mGridView.getChildCount() != 0) {
					OverScroller var2 = this.mScroller;
					boolean var3 = var2.computeScrollOffset();
					int var4 = var2.getCurrY();
					synchronized(this) {
						this.mActualY = var4;
					}

					int var5 = this.mLastFlingY - var4;
					View var6;
					if(var5 > 0) {
						this.setMotionPosition(this.mGridView.getFirstVisiblePosition());
						var6 = this.mGridView.getChildAt(0);
						this.setMotionViewOriginalTop(var6.getTop());
						var5 = Math.min(this.mGridView.getHeight() - this.mGridView.getPaddingBottom() - this.mGridView.getPaddingTop() - 1, var5);
					} else {
						int var10 = this.mGridView.getChildCount() - 1;
						this.setMotionPosition(this.mGridView.getFirstVisiblePosition() + var10);
						View var7 = this.mGridView.getChildAt(var10);
						this.setMotionViewOriginalTop(var7.getTop());
						var5 = Math.max(-(this.mGridView.getHeight() - this.mGridView.getPaddingBottom() - this.mGridView.getPaddingTop() - 1), var5);
					}

					var6 = this.mGridView.getChildAt(this.getMotionPosition() - this.mGridView.getFirstVisiblePosition());
					boolean var11 = false;
					if(var6 != null) {
						int var12 = var6.getTop();
					}

					boolean var8 = this.trackMotionScroll(var5, var5) && var5 != 0;
					if(var8) {
						Log.w("FlingManager", "FlingManager.run at end");
						this.endFling();
					} else if(var3 && !var8) {
						this.mGridView.invalidate();
						this.mLastFlingY = var4;
						this.mGridView.post(this);
					} else {
						this.endFling();
					}

					return;
				}

				this.endFling();
				return;
			default:
				this.endFling();
		}
	}

	boolean trackMotionScroll(int var1, int var2) {
		LOG.i("FlingManager", true, "FlingRunnable trackMotionScroll");
		int var3 = this.mGridView.getChildCount();
		if(var3 == 0) {
			return true;
		} else {
			View var4 = null;

			int var5;
			for(var5 = 0; var5 < this.mGridView.getChildCount(); ++var5) {
				var4 = this.mGridView.getChildAt(var5);
				if(var4.getScaleX() == 1.0F || var4.getScaleY() == 1.0F) {
					break;
				}
			}

			if(var4 == null) {
				return true;
			} else {
				var5 = var4.getTop();
				int var6 = this.mGridView.getChildAt(var3 - 1).getBottom();
				Rect var7 = this.getListPadding();
				int var8 = 0;
				int var9 = 0;
				if((this.getGroupFlags() & this.mFlingCallback.getClipToPaddingMask()) == this.mFlingCallback.getClipToPaddingMask()) {
					var8 = var7.top;
					var9 = var7.bottom;
				}

				int var10 = var8 - var5;
				int var11 = this.mGridView.getHeight() - var9;
				int var12 = var6 - var11;
				int var13 = this.mGridView.getHeight() - this.mGridView.getPaddingBottom() - this.mGridView.getPaddingTop();
				if(var1 < 0) {
					var1 = Math.max(-(var13 - 1), var1);
				} else {
					var1 = Math.min(var13 - 1, var1);
				}

				if(var2 < 0) {
					var2 = Math.max(-(var13 - 1), var2);
				} else {
					var2 = Math.min(var13 - 1, var2);
				}

				int var14 = this.mGridView.getFirstVisiblePosition();
				if(var14 == 0) {
					this.setFirstPositionDistanceGuess(var5 - var7.top);
				} else {
					this.setFirstPositionDistanceGuess(var2);
				}

				if(var14 + var3 == this.mGridView.getAdapter().getCount()) {
					this.setFirstPositionDistanceGuess(var6 + var7.bottom);
				} else {
					this.setFirstPositionDistanceGuess(var2);
				}

				boolean var15 = var14 == 0 && var5 >= var7.top && var2 >= 0;
				boolean var16 = var14 + var3 == this.mGridView.getAdapter().getCount() && var6 <= this.mGridView.getHeight() - var7.bottom && var2 <= 0;
				if(!var15 && !var16) {
					boolean var17 = var2 < 0;
					boolean var18 = this.mGridView.isInTouchMode();
					if(var18) {
						this.focusedHideSelector();
					}

					int var20 = this.mGridView.getAdapter().getCount() - 0;
					int var21 = 0;
					int var22 = 0;
					int var23;
					int var24;
					View var25;
					int var26;
					if(var17) {
						var23 = -var2;
						if((this.getGroupFlags() & this.mFlingCallback.getClipToPaddingMask()) == this.mFlingCallback.getClipToPaddingMask()) {
							var23 += var7.top;
						}

						for(var24 = 0; var24 < var3; ++var24) {
							var25 = this.mGridView.getChildAt(var24);
							if(var25.getBottom() >= var23) {
								break;
							}

							++var22;
							var26 = var14 + var24;
							if(var26 >= 0 && var26 < var20) {
								this.focusedAddScrapView(var25, var26);
							}
						}
					} else {
						var23 = this.mGridView.getHeight() - var2;
						if((this.getGroupFlags() & this.mFlingCallback.getClipToPaddingMask()) == this.mFlingCallback.getClipToPaddingMask()) {
							var23 -= var7.bottom;
						}

						for(var24 = var3 - 1; var24 >= 0; --var24) {
							var25 = this.mGridView.getChildAt(var24);
							if(var25.getTop() <= var23) {
								break;
							}

							var21 = var24;
							++var22;
							var26 = var14 + var24;
							if(var26 >= 0 && var26 < var20) {
								this.focusedAddScrapView(var25, var26);
							}
						}
					}

					this.setMotionViewNewTop(this.getMotionViewOriginalTop() + var1);
					this.setBlockLayoutRequests(true);
					if(var22 > 0) {
						this.mFlingCallback.flingDetachViewsFromParent(var21, var22);
					}

					this.focusedOffsetChildrenTopAndBottom(var2);
					if(var17) {
						this.setFirstPosition(this.mGridView.getFirstVisiblePosition() + var22);
					}

					this.mGridView.invalidate();
					var23 = Math.abs(var2);
					if(var10 < var23 || var12 < var23) {
						this.focusedFillGap(var17);
					}

					if(!var18 && this.getSelectedPosition() != -1) {
						var24 = this.getSelectedPosition() - this.mGridView.getFirstVisiblePosition();
						if(var24 >= 0 && var24 < this.mGridView.getChildCount()) {
							this.focusedPositionSelector(this.getSelectedPosition(), this.mGridView.getChildAt(var24));
						}
					} else if(this.getSelectorPosition() != -1) {
						var24 = this.getSelectorPosition() - this.mGridView.getFirstVisiblePosition();
						if(var24 >= 0 && var24 < this.mGridView.getChildCount()) {
							this.focusedPositionSelector(-1, this.mGridView.getChildAt(var24));
						}
					} else {
						this.setSelectorRectEmpty();
					}

					this.setBlockLayoutRequests(false);
					this.focusedInvokeOnItemScrollListener();
					this.mFlingCallback.flingAwakenScrollBars();
					return false;
				} else {
					Log.w("FlingManager", "trackMotionScroll cannotScrollDown = " + var15 + ", cannotScrollUp = " + var16);
					return var2 != 0;
				}
			}
		}
	}

	Rect getListPadding() {
		try {
			Class var1 = Class.forName("android.widget.AbsListView");
			Field var2 = var1.getDeclaredField("mListPadding");
			var2.setAccessible(true);
			return (Rect)var2.get(this.mGridView);
		} catch (SecurityException var3) {
			var3.printStackTrace();
		} catch (NoSuchFieldException var4) {
			var4.printStackTrace();
		} catch (IllegalArgumentException var5) {
			var5.printStackTrace();
		} catch (IllegalAccessException var6) {
			var6.printStackTrace();
		} catch (ClassNotFoundException var7) {
			var7.printStackTrace();
		}

		return null;
	}

	int getGroupFlags() {
		try {
			Class var1 = Class.forName("android.view.ViewGroup");
			Field var2 = var1.getDeclaredField("mGroupFlags");
			var2.setAccessible(true);
			return var2.getInt(this.mGridView);
		} catch (SecurityException var3) {
			var3.printStackTrace();
		} catch (NoSuchFieldException var4) {
			var4.printStackTrace();
		} catch (IllegalArgumentException var5) {
			var5.printStackTrace();
		} catch (IllegalAccessException var6) {
			var6.printStackTrace();
		} catch (ClassNotFoundException var7) {
			var7.printStackTrace();
		}

		return 0;
	}

	void focusedHideSelector() {
		try {
			Class var1 = Class.forName("android.widget.AbsListView");
			Method var2 = var1.getDeclaredMethod("hideSelector", new Class[0]);
			var2.setAccessible(true);
			var2.invoke(this.mGridView, new Object[0]);
		} catch (SecurityException var3) {
			var3.printStackTrace();
		} catch (IllegalArgumentException var4) {
			var4.printStackTrace();
		} catch (IllegalAccessException var5) {
			var5.printStackTrace();
		} catch (NoSuchMethodException var6) {
			var6.printStackTrace();
		} catch (InvocationTargetException var7) {
			var7.printStackTrace();
		} catch (ClassNotFoundException var8) {
			var8.printStackTrace();
		}

	}

	void focusedAddScrapView(View var1, int var2) {
		try {
			Class var3 = Class.forName("android.widget.AbsListView");
			Field var4 = var3.getDeclaredField("mRecycler");
			var4.setAccessible(true);
			Object var5 = var4.get(this.mGridView);
			Class var6 = Class.forName("android.widget.AbsListView$RecycleBin");
			Method var7 = var6.getDeclaredMethod("addScrapView", new Class[]{View.class, Integer.TYPE});
			var7.setAccessible(true);
			var7.invoke(var5, new Object[]{var1, Integer.valueOf(var2)});
		} catch (SecurityException var8) {
			var8.printStackTrace();
		} catch (NoSuchFieldException var9) {
			var9.printStackTrace();
		} catch (IllegalArgumentException var10) {
			var10.printStackTrace();
		} catch (IllegalAccessException var11) {
			var11.printStackTrace();
		} catch (ClassNotFoundException var12) {
			var12.printStackTrace();
		} catch (NoSuchMethodException var13) {
			var13.printStackTrace();
		} catch (InvocationTargetException var14) {
			var14.printStackTrace();
		}

	}

	void setFirstPositionDistanceGuess(int var1) {
		try {
			Class var2 = Class.forName("android.widget.AbsListView");
			Field var3 = var2.getDeclaredField("mFirstPositionDistanceGuess");
			var3.setAccessible(true);
			var3.setInt(this.mGridView, var1);
		} catch (SecurityException var4) {
			var4.printStackTrace();
		} catch (NoSuchFieldException var5) {
			var5.printStackTrace();
		} catch (IllegalArgumentException var6) {
			var6.printStackTrace();
		} catch (IllegalAccessException var7) {
			var7.printStackTrace();
		} catch (ClassNotFoundException var8) {
			var8.printStackTrace();
		}

	}

	int getMotionViewOriginalTop() {
		try {
			Class var1 = Class.forName("android.widget.AbsListView");
			Field var2 = var1.getDeclaredField("mMotionViewOriginalTop");
			var2.setAccessible(true);
			return var2.getInt(this.mGridView);
		} catch (SecurityException var3) {
			var3.printStackTrace();
		} catch (NoSuchFieldException var4) {
			var4.printStackTrace();
		} catch (IllegalArgumentException var5) {
			var5.printStackTrace();
		} catch (IllegalAccessException var6) {
			var6.printStackTrace();
		} catch (ClassNotFoundException var7) {
			var7.printStackTrace();
		}

		return 0;
	}

	void setMotionViewOriginalTop(int var1) {
		try {
			Class var2 = Class.forName("android.widget.AbsListView");
			Field var3 = var2.getDeclaredField("mMotionViewOriginalTop");
			var3.setAccessible(true);
			var3.setInt(this.mGridView, var1);
		} catch (SecurityException var4) {
			var4.printStackTrace();
		} catch (NoSuchFieldException var5) {
			var5.printStackTrace();
		} catch (IllegalArgumentException var6) {
			var6.printStackTrace();
		} catch (IllegalAccessException var7) {
			var7.printStackTrace();
		} catch (ClassNotFoundException var8) {
			var8.printStackTrace();
		}

	}

	void setMotionViewNewTop(int var1) {
		try {
			Class var2 = Class.forName("android.widget.AbsListView");
			Field var3 = var2.getDeclaredField("mMotionViewNewTop");
			var3.setAccessible(true);
			var3.setInt(this.mGridView, var1);
		} catch (SecurityException var4) {
			var4.printStackTrace();
		} catch (NoSuchFieldException var5) {
			var5.printStackTrace();
		} catch (IllegalArgumentException var6) {
			var6.printStackTrace();
		} catch (IllegalAccessException var7) {
			var7.printStackTrace();
		} catch (ClassNotFoundException var8) {
			var8.printStackTrace();
		}

	}

	void setBlockLayoutRequests(boolean var1) {
		try {
			Class var2 = Class.forName("android.widget.AdapterView");
			Field var3 = var2.getDeclaredField("mBlockLayoutRequests");
			var3.setAccessible(true);
			var3.setBoolean(this.mGridView, var1);
		} catch (SecurityException var4) {
			var4.printStackTrace();
		} catch (NoSuchFieldException var5) {
			var5.printStackTrace();
		} catch (IllegalArgumentException var6) {
			var6.printStackTrace();
		} catch (IllegalAccessException var7) {
			var7.printStackTrace();
		} catch (ClassNotFoundException var8) {
			var8.printStackTrace();
		}

	}

	void focusedOffsetChildrenTopAndBottom(int var1) {
		try {
			Class var2 = Class.forName("android.view.ViewGroup");
			Method var3 = var2.getDeclaredMethod("offsetChildrenTopAndBottom", new Class[]{Integer.TYPE});
			var3.setAccessible(true);
			var3.invoke(this.mGridView, new Object[]{Integer.valueOf(var1)});
		} catch (SecurityException var4) {
			var4.printStackTrace();
		} catch (IllegalArgumentException var5) {
			var5.printStackTrace();
		} catch (IllegalAccessException var6) {
			var6.printStackTrace();
		} catch (NoSuchMethodException var7) {
			var7.printStackTrace();
		} catch (InvocationTargetException var8) {
			var8.printStackTrace();
		} catch (ClassNotFoundException var9) {
			var9.printStackTrace();
		}

	}

	void setFirstPosition(int var1) {
		try {
			Class var2 = Class.forName("android.widget.AdapterView");
			Field var3 = var2.getDeclaredField("mFirstPosition");
			var3.setAccessible(true);
			var3.setInt(this.mGridView, var1);
		} catch (SecurityException var4) {
			var4.printStackTrace();
		} catch (NoSuchFieldException var5) {
			var5.printStackTrace();
		} catch (IllegalArgumentException var6) {
			var6.printStackTrace();
		} catch (IllegalAccessException var7) {
			var7.printStackTrace();
		} catch (ClassNotFoundException var8) {
			var8.printStackTrace();
		}

	}

	void focusedFillGap(boolean var1) {
		try {
			Class var2 = Class.forName("android.widget.GridView");
			Method var3 = var2.getDeclaredMethod("fillGap", new Class[]{Boolean.TYPE});
			var3.setAccessible(true);
			var3.invoke(this.mGridView, new Object[]{Boolean.valueOf(var1)});
		} catch (SecurityException var4) {
			var4.printStackTrace();
		} catch (IllegalArgumentException var5) {
			var5.printStackTrace();
		} catch (IllegalAccessException var6) {
			var6.printStackTrace();
		} catch (NoSuchMethodException var7) {
			var7.printStackTrace();
		} catch (InvocationTargetException var8) {
			var8.printStackTrace();
		} catch (ClassNotFoundException var9) {
			var9.printStackTrace();
		}

	}

	int getSelectedPosition() {
		try {
			Class var1 = Class.forName("android.widget.AdapterView");
			Field var2 = var1.getDeclaredField("mSelectedPosition");
			var2.setAccessible(true);
			var2.getInt(this.mGridView);
		} catch (SecurityException var3) {
			var3.printStackTrace();
		} catch (NoSuchFieldException var4) {
			var4.printStackTrace();
		} catch (IllegalArgumentException var5) {
			var5.printStackTrace();
		} catch (IllegalAccessException var6) {
			var6.printStackTrace();
		} catch (ClassNotFoundException var7) {
			var7.printStackTrace();
		}

		return 0;
	}

	int getSelectorPosition() {
		try {
			Class var1 = Class.forName("android.widget.AbsListView");
			Field var2 = var1.getDeclaredField("mSelectorPosition");
			var2.setAccessible(true);
			var2.getInt(this.mGridView);
		} catch (SecurityException var3) {
			var3.printStackTrace();
		} catch (NoSuchFieldException var4) {
			var4.printStackTrace();
		} catch (IllegalArgumentException var5) {
			var5.printStackTrace();
		} catch (IllegalAccessException var6) {
			var6.printStackTrace();
		} catch (ClassNotFoundException var7) {
			var7.printStackTrace();
		}

		return 0;
	}

	void focusedPositionSelector(int var1, View var2) {
		try {
			Class var3 = Class.forName("android.widget.AbsListView");
			Method var4 = var3.getDeclaredMethod("positionSelector", new Class[]{Integer.TYPE, View.class});
			var4.setAccessible(true);
			var4.invoke(this.mGridView, new Object[]{Integer.valueOf(var1), var2});
		} catch (SecurityException var5) {
			var5.printStackTrace();
		} catch (IllegalArgumentException var6) {
			var6.printStackTrace();
		} catch (IllegalAccessException var7) {
			var7.printStackTrace();
		} catch (NoSuchMethodException var8) {
			var8.printStackTrace();
		} catch (InvocationTargetException var9) {
			var9.printStackTrace();
		} catch (ClassNotFoundException var10) {
			var10.printStackTrace();
		}

	}

	void setSelectorRectEmpty() {
		try {
			Class var1 = Class.forName("android.widget.AbsListView");
			Field var2 = var1.getDeclaredField("mSelectorRect");
			var2.setAccessible(true);
			Class var3 = Class.forName("android.graphics.Rect");
			Method var4 = var3.getDeclaredMethod("setEmpty", new Class[0]);
			var4.invoke(this.mGridView, new Object[0]);
		} catch (SecurityException var5) {
			var5.printStackTrace();
		} catch (NoSuchFieldException var6) {
			var6.printStackTrace();
		} catch (IllegalArgumentException var7) {
			var7.printStackTrace();
		} catch (IllegalAccessException var8) {
			var8.printStackTrace();
		} catch (ClassNotFoundException var9) {
			var9.printStackTrace();
		} catch (NoSuchMethodException var10) {
			var10.printStackTrace();
		} catch (InvocationTargetException var11) {
			var11.printStackTrace();
		}

	}

	void focusedInvokeOnItemScrollListener() {
		try {
			Class var1 = Class.forName("android.widget.AbsListView");
			Method var2 = var1.getDeclaredMethod("invokeOnItemScrollListener", new Class[0]);
			var2.setAccessible(true);
			var2.invoke(this.mGridView, new Object[0]);
		} catch (SecurityException var3) {
			var3.printStackTrace();
		} catch (IllegalArgumentException var4) {
			var4.printStackTrace();
		} catch (IllegalAccessException var5) {
			var5.printStackTrace();
		} catch (NoSuchMethodException var6) {
			var6.printStackTrace();
		} catch (InvocationTargetException var7) {
			var7.printStackTrace();
		} catch (ClassNotFoundException var8) {
			var8.printStackTrace();
		}

	}

	void setTouchMode(int var1) {
		try {
			Class var2 = Class.forName("android.widget.AbsListView");
			Field var3 = var2.getDeclaredField("mTouchMode");
			var3.setAccessible(true);
			var3.get(this.mGridView);
			var3.setInt(this.mGridView, var1);
		} catch (SecurityException var5) {
			var5.printStackTrace();
		} catch (NoSuchFieldException var6) {
			var6.printStackTrace();
		} catch (IllegalArgumentException var7) {
			var7.printStackTrace();
		} catch (IllegalAccessException var8) {
			var8.printStackTrace();
		} catch (ClassNotFoundException var9) {
			var9.printStackTrace();
		}

	}

	int getTouchMode() {
		try {
			Class var1 = Class.forName("android.widget.AbsListView");
			Field var2 = var1.getDeclaredField("mTouchMode");
			var2.setAccessible(true);
			return var2.getInt(this.mGridView);
		} catch (SecurityException var3) {
			var3.printStackTrace();
		} catch (NoSuchFieldException var4) {
			var4.printStackTrace();
		} catch (IllegalArgumentException var5) {
			var5.printStackTrace();
		} catch (IllegalAccessException var6) {
			var6.printStackTrace();
		} catch (ClassNotFoundException var7) {
			var7.printStackTrace();
		}

		return 0;
	}

	boolean getDataChanged() {
		try {
			Class var1 = Class.forName("android.widget.AdapterView");
			Field var2 = var1.getDeclaredField("mDataChanged");
			var2.setAccessible(true);
			return var2.getBoolean(this.mGridView);
		} catch (SecurityException var3) {
			var3.printStackTrace();
		} catch (NoSuchFieldException var4) {
			var4.printStackTrace();
		} catch (IllegalArgumentException var5) {
			var5.printStackTrace();
		} catch (IllegalAccessException var6) {
			var6.printStackTrace();
		} catch (ClassNotFoundException var7) {
			var7.printStackTrace();
		}

		return false;
	}

	int getMotionPosition() {
		try {
			Class var1 = Class.forName("android.widget.AbsListView");
			Field var2 = var1.getDeclaredField("mMotionPosition");
			var2.setAccessible(true);
			return var2.getInt(this.mGridView);
		} catch (SecurityException var3) {
			var3.printStackTrace();
		} catch (NoSuchFieldException var4) {
			var4.printStackTrace();
		} catch (IllegalArgumentException var5) {
			var5.printStackTrace();
		} catch (IllegalAccessException var6) {
			var6.printStackTrace();
		} catch (ClassNotFoundException var7) {
			var7.printStackTrace();
		}

		return 0;
	}

	void setMotionPosition(int var1) {
		try {
			Class var2 = Class.forName("android.widget.AbsListView");
			Field var3 = var2.getDeclaredField("mMotionPosition");
			var3.setAccessible(true);
			var3.setInt(this.mGridView, var1);
		} catch (SecurityException var4) {
			var4.printStackTrace();
		} catch (NoSuchFieldException var5) {
			var5.printStackTrace();
		} catch (IllegalArgumentException var6) {
			var6.printStackTrace();
		} catch (IllegalAccessException var7) {
			var7.printStackTrace();
		} catch (ClassNotFoundException var8) {
			var8.printStackTrace();
		}

	}

	int getOverflingDistance() {
		try {
			Class var1 = Class.forName("android.widget.AbsListView");
			Field var2 = var1.getDeclaredField("mOverflingDistance");
			var2.setAccessible(true);
			return var2.getInt(this.mGridView);
		} catch (SecurityException var3) {
			var3.printStackTrace();
		} catch (NoSuchFieldException var4) {
			var4.printStackTrace();
		} catch (IllegalArgumentException var5) {
			var5.printStackTrace();
		} catch (IllegalAccessException var6) {
			var6.printStackTrace();
		} catch (ClassNotFoundException var7) {
			var7.printStackTrace();
		}

		return 0;
	}

	void edgeReached(int var1) {
		try {
			Class var2 = Class.forName("android.widget.AbsListView");
			Method var3 = var2.getDeclaredMethod("edgeReached", new Class[0]);
			var3.setAccessible(true);
			var3.invoke(this.mGridView, new Object[0]);
		} catch (SecurityException var4) {
			var4.printStackTrace();
		} catch (IllegalArgumentException var5) {
			var5.printStackTrace();
		} catch (IllegalAccessException var6) {
			var6.printStackTrace();
		} catch (NoSuchMethodException var7) {
			var7.printStackTrace();
		} catch (InvocationTargetException var8) {
			var8.printStackTrace();
		} catch (ClassNotFoundException var9) {
			var9.printStackTrace();
		}

	}

	void focusedReportScrollStateChange(int var1) {
		try {
			Class var2 = Class.forName("android.widget.AbsListView");
			Method var10 = var2.getDeclaredMethod("reportScrollStateChange", new Class[]{Integer.TYPE});
			var10.setAccessible(true);
			var10.invoke(this.mGridView, new Object[]{Integer.valueOf(var1)});
		} catch (SecurityException var4) {
			var4.printStackTrace();
		} catch (IllegalArgumentException var5) {
			var5.printStackTrace();
		} catch (IllegalAccessException var6) {
			var6.printStackTrace();
		} catch (NoSuchMethodException var7) {
			var7.printStackTrace();
		} catch (InvocationTargetException var8) {
			boolean var3 = true;
			var8.printStackTrace();
		} catch (ClassNotFoundException var9) {
			var9.printStackTrace();
		}

	}

	void focusedClearScrollingCache() {
		try {
			Class var1 = Class.forName("android.widget.AbsListView");
			Method var2 = var1.getDeclaredMethod("clearScrollingCache", new Class[0]);
			var2.setAccessible(true);
			var2.invoke(this.mGridView, new Object[0]);
		} catch (SecurityException var3) {
			var3.printStackTrace();
		} catch (IllegalArgumentException var4) {
			var4.printStackTrace();
		} catch (IllegalAccessException var5) {
			var5.printStackTrace();
		} catch (NoSuchMethodException var6) {
			var6.printStackTrace();
		} catch (InvocationTargetException var7) {
			var7.printStackTrace();
		} catch (ClassNotFoundException var8) {
			var8.printStackTrace();
		}

	}

	void finishFlingStrictSpan() {
		try {
			Class var1 = Class.forName("android.widget.AbsListView");
			Field var2 = var1.getDeclaredField("mFlingStrictSpan");
			var2.setAccessible(true);
			Object var3 = var2.get(this.mGridView);
			if(null == var3) {
				return;
			}

			Class var4 = Class.forName("android.os.StrictMode$Span");
			Method var5 = var4.getDeclaredMethod("finish", new Class[0]);
			var5.invoke(this.mGridView, new Object[0]);
		} catch (SecurityException var6) {
			var6.printStackTrace();
		} catch (IllegalArgumentException var7) {
			var7.printStackTrace();
		} catch (IllegalAccessException var8) {
			var8.printStackTrace();
		} catch (NoSuchMethodException var9) {
			var9.printStackTrace();
		} catch (InvocationTargetException var10) {
			var10.printStackTrace();
		} catch (NoSuchFieldException var11) {
			var11.printStackTrace();
		} catch (ClassNotFoundException var12) {
			var12.printStackTrace();
		}

	}

	void focusedSetNextSelectedPositionInt(int var1) {
		try {
			Class var2 = Class.forName("android.widget.AdapterView");
			Method var3 = var2.getDeclaredMethod("setNextSelectedPositionInt", new Class[]{Integer.TYPE});
			var3.setAccessible(true);
			var3.invoke(this.mGridView, new Object[]{Integer.valueOf(var1)});
		} catch (SecurityException var4) {
			var4.printStackTrace();
		} catch (IllegalArgumentException var5) {
			var5.printStackTrace();
		} catch (IllegalAccessException var6) {
			var6.printStackTrace();
		} catch (NoSuchMethodException var7) {
			var7.printStackTrace();
		} catch (InvocationTargetException var8) {
			var8.printStackTrace();
		} catch (ClassNotFoundException var9) {
			var9.printStackTrace();
		}

	}

	public interface FlingCallback {
		void flingLayoutChildren();

		int getScrollY();

		int getClipToPaddingMask();

		boolean flingOverScrollBy(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9);

		void flingDetachViewsFromParent(int var1, int var2);

		boolean flingAwakenScrollBars();
	}
}