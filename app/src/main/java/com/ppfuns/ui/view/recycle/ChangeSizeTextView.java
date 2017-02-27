package com.ppfuns.ui.view.recycle;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;

import com.ppfuns.ui.view.AlwaysMarqueeTextView;
import com.ppfuns.vod.R;

/**
 * 创建者     庄丰泽
 * 创建时间   2016/9/20 17:52
 * 描述	      可以动态设置大小的并带跑马灯的TextView
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ChangeSizeTextView extends AlwaysMarqueeTextView {
    private SpannableStringBuilder mStyle;

    public ChangeSizeTextView(Context context) {
        this(context, null);
    }

    public ChangeSizeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setText(String startStr, String endStr) {
        String str = startStr + endStr;
        mStyle = new SpannableStringBuilder(str);

        mStyle.setSpan(new AbsoluteSizeSpan((int)getResources().getDimension(R.dimen.album_tv_name)), 0, startStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mStyle.setSpan(new AbsoluteSizeSpan((int)getResources().getDimension(R.dimen.album_tv_time)), startStr.length(), str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        this.setText(mStyle);
    }

    private static final String TAG = "ChangeSizeTextView";
}
