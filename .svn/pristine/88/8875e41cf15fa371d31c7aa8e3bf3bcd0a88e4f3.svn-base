package com.ppfuns.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ppfuns.util.LogUtils;
import com.ppfuns.vod.R;

/**
 * Created by lenovo on 2016/6/22.
 */
public class CusSeekBar extends SeekBar {
    private java.lang.String TAG = "CusSeekBar";
    private boolean toShowPop = true;

    public CusSeekBar(Context context) {
        this(context, null);
    }

    public CusSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CusSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private PopupWindow mPopupWindow;

    private LayoutInflater mInflater;
    private View mView;
    private int[] mPosition;

    private Context mContext;

    private final int mThumbWidth = 25;
    private TextView mTvProgress;

    private int mWidth;
    private int mHeight;
    int mProgress;
    int mViewWidth;
    int mDX;
    int mOneStep;
    int mStartX;


    private void init(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mView = mInflater.inflate(R.layout.layout_seekbar_popwindow, null);
        mTvProgress = (TextView) mView.findViewById(R.id.tvPop);
        mPopupWindow = new PopupWindow(mView, mView.getWidth(),
                mView.getHeight(), true);
        mPopupWindow.setFocusable(false);
        mPosition = new int[2];
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);

        mWidth = wm.getDefaultDisplay().getWidth();
        mHeight = wm.getDefaultDisplay().getHeight();
        LogUtils.i(TAG, "mWidth=" + mWidth);
        LogUtils.i(TAG, "mHeight=" + mHeight);
        mViewWidth = getWidth();
        mDX = mWidth - mViewWidth;
        mOneStep = mViewWidth / getMax();
        mStartX = mWidth - mDX / 2;
        LogUtils.i(TAG, "mViewWidth=" + mViewWidth);
        LogUtils.i(TAG, "mDX=" + mDX);
        LogUtils.i(TAG, "mOneStep=" + mOneStep);
        LogUtils.i(TAG, "mStartX=" + mStartX);


    }

    public void setSeekBarText(String str) {
        mTvProgress.setText(str);
        mProgress = getProgress();
        LogUtils.i(TAG, "str=" + str);
        try {

//                mPopupWindow.showAtLocation(this, Gravity.TOP, mStartX + mOneStep * mProgress, mHeight - 300);
            if (toShowPop)
                mPopupWindow.showAsDropDown(this, mStartX + mOneStep * mProgress, mHeight - 100);
//            LogUtils.i(TAG, "setSeekBarText mStartX=" + mStartX);
//            LogUtils.i(TAG, "setSeekBarText mOneStep=" + mOneStep);
//            LogUtils.i(TAG, "setSeekBarText mProgress=" + mProgress);
//            LogUtils.i(TAG, "setSeekBarText mHeight=" +  (mHeight*2 - 30));
//            LogUtils.i(TAG, "setSeekBarText mStartX=" + mStartX);


        } catch (Exception e) {
//            e.printStackTrace();
        }
    }


    private int getViewWidth(View v) {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);
        return v.getMeasuredWidth();
    }

    private int getViewHeight(View v) {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);
        return v.getMeasuredHeight();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        int thumb_x = this.getProgress() * (this.getWidth() - mThumbWidth)
                / this.getMax();
        int middle = mHeight - 100;//这里控制pop的Y轴位置
        super.onDraw(canvas);

        if (mPopupWindow != null) {
            try {
                this.getLocationOnScreen(mPosition);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();
                if (lp != null) {
                    LogUtils.i(TAG, lp.bottomMargin + "bottomMargin");

                }
                LogUtils.i(TAG, "middle=" + middle);
                mPopupWindow.update(thumb_x + mPosition[0]
                                - getViewWidth(mView) / 2 + mThumbWidth / 2, 110,
                        getViewWidth(mView), getViewHeight(mView));

            } catch (Exception e) {

            }
        }

    }


    public void hidepopwindow() {
        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();
        toShowPop = false;
    }


    public void showpopWindow() {
        toShowPop = true;
//        mPopupWindow.showAtLocation(this, Gravity.TOP, mStartX + mOneStep * mProgress, mHeight - 300);
    }
}
