package com.ppfuns.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ListView;

import com.ppfuns.vod.R;

/**
 * Created by lenovo on 2016/6/22.
 */
public class MemListView extends ListView {
    //private int selectionTop;
    public MemListView(Context context) {
        super(context);
        //selectionTop = getResources().getDimensionPixelSize(R.dimen.listview1_selectionTop);
    }

    public MemListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //selectionTop = getResources().getDimensionPixelSize(R.dimen.listview1_selectionTop);
    }

    public MemListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //selectionTop = getResources().getDimensionPixelSize(R.dimen.listview1_selectionTop);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        int lastSelectItem = getSelectedItemPosition();
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            if(getSelectedView()!=null){
                setSelectionFromTop(lastSelectItem,getSelectedView().getTop() - getListPaddingTop());
            }else{
                setSelection(lastSelectItem);
            }
        }
    }
}
