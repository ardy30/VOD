package com.ppfuns.model.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ppfuns.util.LogUtils;
import com.ppfuns.vod.R;
import com.ppfuns.vod.activity.PlayerActivity;

import java.util.List;

/**
 * Created by hmc on 2016/9/5.
 */
public class FilterContentAdapter extends RecyclerView.Adapter<FilterContentAdapter.ViewHolder> {

    private final String TAG = FilterTitleAdapter.class.getSimpleName();

    private Context mContext;
    private List<String> mList;
    private LayoutInflater mInflater;

    public static final int TITLE_FLAG = 0;
    public static final int CONTENT_FLAG = 1;

    private int mType = 0;
    private int currentPosition = 0;

    private FilterActionListener mListener;

    private View oldView;

    public FilterContentAdapter(Context context, List<String> list, int type) {
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
        viewHolder.tvName = (TextView) view.findViewById(R.id.tv_name);
//        if (mType == CONTENT_FLAG) {
        viewHolder.itemView.setFocusable(true);
        viewHolder.tvSelect = (TextView) view.findViewById(R.id.tv_select);
        viewHolder.itemView.setBackgroundResource(R.drawable.filter_item_bg);
        int size = mContext.getResources().getDimensionPixelSize(R.dimen.filter_item_select_size);
        viewHolder.tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
//        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (position == currentPosition) {
            viewHolder.tvSelect.setVisibility(View.VISIBLE);
            oldView = viewHolder.tvSelect;
        }
        viewHolder.tvName.setText(mList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mList != null && mList.size() > 0) {
            return mList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnFocusChangeListener, View.OnKeyListener {

        TextView tvName;
        TextView tvSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            if (itemView == null) {
                throw new IllegalArgumentException(TAG + "itemView may be null");
            }

            itemView.setOnFocusChangeListener(this);
            itemView.setOnKeyListener(this);
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
//            if (mListener != null) {
//                mListener.onFocusChange(FilterTitleAdapter.this, itemView, hasFocus, getAdapterPosition());
//            }
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            boolean isExcute = false;
            LogUtils.i("Decode", "item_onKey currentPosition " + currentPosition + ", pos: " + getAdapterPosition());
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                PlayerActivity activity = (PlayerActivity) mContext;
                activity.mHandler.removeMessages(PlayerActivity.HIDE_FILTER);
                activity.mHandler.sendEmptyMessageDelayed(PlayerActivity.HIDE_FILTER, 5000);
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP:
                        if (getAdapterPosition() == 0) {
                            isExcute = true;
                        }
                        break;
                    case KeyEvent.KEYCODE_ENTER:
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                        int position = getAdapterPosition();
                        if (currentPosition == position) {
                            return true;
                        }
                        if (mListener != null) {
                            if (oldView != null) {
                                oldView.setVisibility(View.INVISIBLE);
                            }
                            tvSelect.setVisibility(View.VISIBLE);
                            oldView = tvSelect;
                            currentPosition = position;
                            mListener.onClick(0, getAdapterPosition());
                        }
                        break;
                }
            }
            return isExcute;
        }
    }

    public interface FilterActionListener {
        void onClick(int type, int position);
    }

}
