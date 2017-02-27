package com.ppfuns.ui.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.ppfuns.util.LogUtils;


/**
 * 为了兼容4.3以下版本的 AnimBridge. <br>
 * 使用方法： MainUpView.setAnimBridge(new AnimNoDrawBridge()); <br>
 * 如果边框带了阴影效果，使用这个函数自行调整: MainUpView.setDrawUpRectPadding(-12);
 * 
 * @author hailongqiu
 *
 */
public class EffectNoDrawBridge extends OpenEffectBridge {
	public static final String TAG = "EffectNoDrawBridge";
	protected AnimatorSet mCurrentAnimatorSet;
	private boolean isUp;
	private boolean isDown;
	private int initMapTransY1;
	private int initMapTransY2;
	private boolean isFirst = true;
	private boolean isFirstDown = false;
	private boolean isFromAlbum = false;

	public boolean isFromAlbum() {
		return isFromAlbum;
	}

	public void setFromAlbum(boolean fromAlbum) {
		isFromAlbum = fromAlbum;
	}

	public boolean isFirstDown() {
		return isFirstDown;
	}

	public void setFirstDown(boolean firstDown) {
		isFirstDown = firstDown;
	}

	public boolean isDown() {
		return isDown;
	}

	public void setDown(boolean down) {
		isDown = down;
	}

	public boolean isUp() {
		return isUp;
	}

	public void setUp(boolean up) {
		isUp = up;
	}

	/**
	 * 设置背景，边框不使用绘制.
	 */
	@Override
	public void setUpRectResource(int resId) {
		getMainUpView().setBackgroundResource(resId);
	}

	@Override
	public void setUpRectDrawable(Drawable upRectDrawable) {
		getMainUpView().setBackgroundDrawable(upRectDrawable);
	}

	@Override
	public void onOldFocusView(View oldFocusView, float scaleX, float scaleY) {
		if (!isAnimEnabled())
			return;
		if (oldFocusView != null) {
			oldFocusView.animate().scaleX(scaleX).scaleY(scaleY).setDuration(getTranDurAnimTime()).start();
		}
	}

	@Override
	public void onFocusView(View focusView, float scaleX, float scaleY) {
		if (!isAnimEnabled())
			return;
		if (focusView != null) {
			/**
			 * 我这里重写了onFocusView. <br>
			 * 并且交换了位置. <br>
			 * 你可以写自己的动画效果. <br>
			 */
			runTranslateAnimation(focusView, scaleX, scaleY);
			focusView.animate().scaleX(scaleX).scaleY(scaleY).setDuration(getTranDurAnimTime()).start();
		}
	}

	/**
	 * 重写边框移动函数.
	 */
	@Override
	public void flyWhiteBorder(final View focusView, final View moveView, float scaleX, float scaleY) {
		Rect paddingRect = getDrawUpRect();
		int newWidth = 0;
		int newHeight = 0;
		int oldWidth = 0;
		int oldHeight = 0;
		
		int newX = 0;
		int newY = 0;
		
		if (focusView != null) {
			newWidth = (int) (focusView.getMeasuredWidth() * scaleX);
			newHeight = (int) (focusView.getMeasuredHeight() * scaleY);
			oldWidth = moveView.getMeasuredWidth();
			oldHeight = moveView.getMeasuredHeight();
			Rect fromRect = findLocationWithView(moveView);
			Rect toRect = findLocationWithView(focusView);
			int x = toRect.left - fromRect.left - (paddingRect.left);
			int y = toRect.top - fromRect.top - (paddingRect.top);
			newX = x - Math.abs(focusView.getMeasuredWidth() - newWidth) / 2;
			newY = y - Math.abs(focusView.getMeasuredHeight() - newHeight) / 2;
			LogUtils.i(TAG,"newX: " + newX + " newY: "+ newY);
			newWidth += (paddingRect.right + paddingRect.left);
			newHeight += (paddingRect.bottom + paddingRect.top);
		}

		// 取消之前的动画.
		if (mCurrentAnimatorSet != null) {
			if (isFromAlbum) {
				// 兼容详情页推荐位长按动画
				mCurrentAnimatorSet.end();
			}
			mCurrentAnimatorSet.cancel();
		}

		ObjectAnimator transAnimatorX = ObjectAnimator.ofFloat(moveView, "translationX", newX);
		ObjectAnimator transAnimatorY = ObjectAnimator.ofFloat(moveView, "translationY", newY);
		if(isFirst){
			//记录首次偏移量
			initMapTransY1 = newY;
			isFirst = false;
		}
		if(isFirstDown){
			initMapTransY2 = newY;
			isFirstDown = false;
		}
		// BUG，因为缩放会造成图片失真(拉伸).
		// hailong.qiu 2016.02.26 修复 :)
		ObjectAnimator scaleXAnimator = ObjectAnimator.ofInt(new ScaleView(moveView), "width", oldWidth,
				(int) newWidth);
		ObjectAnimator scaleYAnimator = ObjectAnimator.ofInt(new ScaleView(moveView), "height", oldHeight,
				(int) newHeight);
		//
		AnimatorSet mAnimatorSet = new AnimatorSet();
		mAnimatorSet.playTogether(transAnimatorX, transAnimatorY, scaleXAnimator, scaleYAnimator);
		mAnimatorSet.setInterpolator(new DecelerateInterpolator(1));
		mAnimatorSet.setDuration(getTranDurAnimTime());
		mAnimatorSet.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
				if (isVisibleWidget()) {
					getMainUpView().setVisibility(View.GONE);
				}
				if (getNewAnimatorListener() != null)
					getNewAnimatorListener().onAnimationStart(EffectNoDrawBridge.this, focusView, animation);
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				getMainUpView().setVisibility(isVisibleWidget() ? View.GONE : View.VISIBLE);
				if (getNewAnimatorListener() != null)
					getNewAnimatorListener().onAnimationEnd(EffectNoDrawBridge.this, focusView, animation);
				//LogUtils.i(TAG,"end_transAnimatorY: " + moveView.getTranslationY());

				LogUtils.i(TAG,"initMapTransY1: " + initMapTransY1 + " initMapTransY2: "+ initMapTransY2);
//				if(isUp){
//					//LogUtils.i(TAG,"isUp: ");
//					isUp = false;
//					ObjectAnimator transAnimatorY2 = ObjectAnimator.ofFloat(moveView, "translationY", initMapTransY2);
//					transAnimatorY2.setDuration(200);
//					transAnimatorY2.start();
//				}else if(isDown){
//					//LogUtils.i(TAG,"isDown: ");
//					isDown = false;
//					ObjectAnimator transAnimatorY2 = ObjectAnimator.ofFloat(moveView, "translationY", initMapTransY1);
//					transAnimatorY2.setDuration(200);
//					transAnimatorY2.start();
//				}

				// XF add（先锋TV开发(404780246)修复)
				// BUG:5.0系统边框错位.
				if (Build.VERSION.SDK_INT >= 21) {
//					int newWidth = (int) (focusView.getMeasuredWidth() *
//							mScaleX);
//					int newHeight = (int) (focusView.getMeasuredHeight() *
//							mScaleY);
//					getMainUpView().getLayoutParams().width = newWidth;
//					getMainUpView().getLayoutParams().height = newHeight;
//					getMainUpView().requestLayout();
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});
		mAnimatorSet.start();
		mCurrentAnimatorSet = mAnimatorSet;
	}

	/**
	 * 重写该函数，<br>
	 * 不进行绘制 边框和阴影.
	 */
	@Override
	public boolean onDrawMainUpView(Canvas canvas) {
		return false;
	}

}
