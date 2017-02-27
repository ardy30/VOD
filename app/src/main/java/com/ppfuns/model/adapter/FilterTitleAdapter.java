package com.ppfuns.model.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ppfuns.util.LogUtils;
import com.ppfuns.vod.R;

import java.util.List;

/**
 * Created by hmc on 2016/9/5.
 */
public class FilterTitleAdapter extends RecyclerView.Adapter<FilterTitleAdapter.ViewHolder> {

    private final String TAG = FilterTitleAdapter.class.getSimpleName();

    private Context mContext;
    private List<String> mList;
    private LayoutInflater mInflater;

    public static final int TITLE_FLAG = 0;
    public static final int CONTENT_FLAG = 1;

    private int mType = 0;

    private FilterActionListener mListener;

    public FilterTitleAdapter(Context context, List<String> list, int type) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mList = list;
        mType = type;
    }

    public void setOnFilterActionListener(FilterActionListener listener) {
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_filter_title, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_name);
//        if (mType == CONTENT_FLAG) {
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 324);
//            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
//            viewHolder.tvTitle.setLayoutParams(params);
//            view.setFocusable(true);
//            view.setBackgroundResource(R.drawable.filter_item_bg);
//            viewHolder.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, 39);
//        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.tvTitle.setText(mList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mList != null && mList.size() > 0) {
            return mList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnFocusChangeListener, View.OnKeyListener {

        TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            if (itemView == null) {
                throw new IllegalArgumentException(TAG + "itemView may be null");
            }

            itemView.setOnFocusChangeListener(this);
//            itemView.setOnClickListener(this);
            itemView.setOnKeyListener(this);
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            LogUtils.i("Decode", "item_onFocusChange: " + hasFocus);
            if (mListener != null) {
//                mListener.onFocusChange(FilterTitleAdapter.this, itemView, hasFocus, getAdapterPosition());
            }
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            boolean isExcute = false;
            LogUtils.i("Decode", "item_onKey");
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP:
                        if (getAdapterPosition() == 0) {
                            isExcute = true;
                        }
                        break;
                    case KeyEvent.KEYCODE_ENTER:
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                        if (mListener != null) {
                            mListener.onClick(0, getAdapterPosition());
                        }
                        break;
                }
//                else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
//                    if (getAdapterPosition() == 0) {
//                        isExcute = true;
//                    }
//                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//                    if (getAdapterPosition() == mBeanList.size() - 1) {
//                        isExcute = true;
//                    }
//                }
            }
            return isExcute;
        }
    }

    public interface FilterActionListener {
        void onClick(int type, int position);
    }

}
