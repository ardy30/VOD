package com.ppfuns.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by lenovo on 2016/7/7.
 */
public class MyGridView extends GridView {
    private String TAG = "MyGridView";

    // 频道分类左边列表切换刷新后重置gridview默认item位置
    private boolean isRefresh = false;

    public MyGridView(Context context) {
        super(context);
    }

    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isInTouchMode() {
        return !(hasFocus() && !super.isInTouchMode());
    }

    @Override
    public int computeVerticalScrollOffset() {
        return super.computeVerticalScrollOffset();
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        int lastSelectItem = getSelectedItemPosition();
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            if(lastSelectItem==-1 || isRefresh){
                isRefresh = false;
                setSelection(0);
            }else{
                setSelection(lastSelectItem);
            }
        }
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }
}
