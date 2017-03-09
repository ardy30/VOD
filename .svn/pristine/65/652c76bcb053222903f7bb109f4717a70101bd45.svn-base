package com.ppfuns.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import com.ppfuns.util.LogUtils;
import com.ppfuns.util.StrUtils;


/**
 * 创建者     庄丰泽
 * 创建时间   2016/6/29 11:56
 * 描述	      可改变特定字符颜色的textView
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ChangeColorTextView extends AlwaysMarqueeTextView {

    private SpannableStringBuilder mStyle;

    public ChangeColorTextView(Context context) {
        this(context, null);
    }

    public ChangeColorTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setText(String text, int TextChangeSize, String key) {

        if (TextUtils.isEmpty(text)) {
            return;
        }

        if (TextChangeSize > text.length()) {
            TextChangeSize = text.length();
        }
        mStyle = new SpannableStringBuilder(text);

        int startIndex = 0;
        text = StrUtils.change1(text);

        LogUtils.d(TAG,text);

        String textPY = StrUtils.getPYIndexStr(text, true).toUpperCase();
        String keyPY = StrUtils.getPYIndexStr(key, true).toUpperCase();

        LogUtils.d(TAG, textPY);
        LogUtils.d(TAG, keyPY);

            if (textPY.contains(keyPY)){
                int i = textPY.indexOf(keyPY);
                startIndex = i;
            }else if (textPY.contains(keyPY.charAt(0)+"")){
                int i = textPY.indexOf(keyPY.charAt(0));
                startIndex = i;
            }

        int endIndex = startIndex + TextChangeSize;

        LogUtils.d(TAG, startIndex + "");
        LogUtils.d(TAG, endIndex + "");

        mStyle.setSpan(new ForegroundColorSpan(Color.GRAY), 0, startIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置指定位置文字的颜色

        mStyle.setSpan(new ForegroundColorSpan(Color.WHITE), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置指定位置文字的颜色
        mStyle.setSpan(new ForegroundColorSpan(Color.GRAY), endIndex, text.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置指定位置文字的颜色

        isMeasure = false;
        this.setText(mStyle);
    }

    private static final String TAG = "ChangeColorTextView";
}
