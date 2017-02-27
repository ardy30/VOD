package com.ppfuns.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.ppfuns.vod.R;

import java.util.Vector;

/**
 * Created by zpf on 2017/1/3.
 */

public class CYTextView extends TextView {
    private static final String TAG = CYTextView.class.getSimpleName();

    private final String DEFAULT_END_TIPS = ".....查看更多";
    private int mWidth = 0;
    private int mHeight = 0;

    private TextPaint mPaint;
    private float mLineSpace;
    private int mMaxLines;
    private float mTextSize;
    private int mTextColor;

    private String mEntTip;
    private String mText;


    public CYTextView(Context context) {
        this(context, null);
    }

    public CYTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CYTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CYTextView);
        if (a != null) {
            mEntTip = a.getString(R.styleable.CYTextView_endTips);
            a.recycle();
        }

        if (TextUtils.isEmpty(mEntTip)) {
            mEntTip = DEFAULT_END_TIPS;
        }
    }

    private void initPaint() {
        if (mPaint != null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mLineSpace = getLineSpacingExtra();
            mMaxLines = getMaxLines();
        }
        mText = (String) getText();
        mTextSize = getTextSize();
        mTextColor = getCurrentTextColor();

        mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        Log.i(TAG, "onSizeChanged w " + w + ", h " + h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (TextUtils.isEmpty(mText) || mWidth <= 0) {
            return;
        }
        initPaint();

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int w = 0;
        int h = 0;
        int startPos = 0;
        int curLines = 1;
        int textWidth = mWidth - paddingLeft - paddingRight;
        int textHight = mHeight - paddingTop - paddingBottom;

        Paint.FontMetrics fm = mPaint.getFontMetrics();
        float fontHeight = (int) Math.floor(fm.descent - fm.top) + (int) mLineSpace;

        Vector strings = new Vector();
        mText = mText.trim().replaceAll("\\n", " ").replaceAll("\\t", " ");
        int textLength = mText.length();
        float tipWidth = mPaint.measureText(mEntTip);

        String endText = "";
        for (int i = 0; i < textLength; i++) {
            char ch = mText.charAt(i);
            float[] widths = new float[1];
            String str = String.valueOf(ch);
            mPaint.getTextWidths(str, widths);
            w += widths[0];
            if ((curLines < mMaxLines || h + fontHeight > textHight) && w > textWidth) {
                curLines++;
                strings.addElement(mText.substring(startPos, i));
                startPos = i;
                i--;
                w = 0;
                h += fontHeight;
            } else if (curLines == mMaxLines || h + fontHeight > textHight) {
                endText = mText.substring(startPos, textLength);
                break;
            }
        }

        if (curLines == 1) {
            strings.addElement(mText);
        } else if (curLines == mMaxLines || h + fontHeight > textHight) {
            w = 0;
            startPos = 0;
            float measureText = mPaint.measureText(endText);
            textLength = endText.length();
            if (measureText > textWidth) {
                for (int i = 0; i < textLength; i++) {
                    char ch = endText.charAt(i);
                    float[] widths = new float[1];
                    String str = String.valueOf(ch);
                    mPaint.getTextWidths(str, widths);
                    w += widths[0];
                    if (w > textWidth - tipWidth) {
                        strings.addElement(endText.substring(startPos, i - 1) + mEntTip);
                        setFocusable(true);
                        break;
                    }
                }
            } else {
                strings.addElement(endText);
            }
        } else {
            endText = mText.substring(startPos, textLength);
            strings.addElement(endText);
        }
        int size = strings.size();
        for (int i = 0; i < size; i++) {
            float y = paddingTop + (i + 1) * fontHeight - mLineSpace;
            canvas.drawText((String) strings.elementAt(i), paddingLeft, y, mPaint);
        }
    }

    public void setText(String text) {
        initPaint();
        mText = text;
        invalidate();
    }

}
