package com.ppfuns.model.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ppfuns.vod.R;

/**
 * Created by hmc on 2016/6/20.
 */
public class EpisodeTitleAdapter extends RecyclerView.Adapter<EpisodeTitleAdapter.ViewHolder> {

    private final String TAG = EpisodeTitleAdapter.class.getSimpleName();
    private Context mContext;
    private LayoutInflater mInflater;

    private OnItemActionListener mListener;

    private int len = 0;
    private int count = 0;
    /**
     * 选集的每栏显示的剧集数
     */
    public static final int EPISODE_CRITICAL_VALUE = 15;

    public EpisodeTitleAdapter(Context context, int len) {
        this.len = len;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setOnItemActionListener(OnItemActionListener listener) {
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_episode_title, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.tv_title = (TextView) view.findViewById(R.id.tv_episode_title);
        viewHolder.tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.item_title_normal_size));
        viewHolder.iv_line = (ImageView) view.findViewById(R.id.iv_line);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        if (position < (count - 1)) {
            viewHolder.tv_title.setText((position * EPISODE_CRITICAL_VALUE + 1) + "-" + ((position + 1) * EPISODE_CRITICAL_VALUE));
        } else {
            int residue = len % EPISODE_CRITICAL_VALUE;
            if (residue == 0) residue = EPISODE_CRITICAL_VALUE;
            viewHolder.tv_title.setText((position * EPISODE_CRITICAL_VALUE + 1) + "-" + (position * EPISODE_CRITICAL_VALUE + residue));
        }
    }

    @Override
    public int getItemCount() {
        int residue = len % EPISODE_CRITICAL_VALUE;
        if (residue == 0) {
            count = len / EPISODE_CRITICAL_VALUE;
        } else {
            count = len / EPISODE_CRITICAL_VALUE + 1;
        }
        return count;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnFocusChangeListener, View.OnKeyListener, View.OnClickListener {

        TextView tv_title;
        ImageView iv_line;

        public ViewHolder(View itemView) {
            super(itemView);
            if (itemView == null) {
                throw new IllegalArgumentException(TAG + "itemView may be null");
            }
            itemView.setOnFocusChangeListener(this);
            itemView.setOnClickListener(this);
            itemView.setOnKeyListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(EpisodeTitleAdapter.this, itemView, getAdapterPosition());
            }
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (mListener != null) {
                mListener.onFocusChange(EpisodeTitleAdapter.this, itemView, hasFocus, getAdapterPosition());
            }
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            iv_line.setVisibility(View.INVISIBLE);
            tv_title.getPaint().setFakeBoldText(false);
            tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.item_title_normal_size));
            boolean isExcute = false;
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    iv_line.setVisibility(View.VISIBLE);
                    tv_title.getPaint().setFakeBoldText(true);
                    tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.item_title_focus_size));
                    mListener.onKey(EpisodeTitleAdapter.this, itemView, keyCode, event, getAdapterPosition());
                    isExcute = true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    if (getAdapterPosition() == count - 1) {
                        isExcute = true;
                    }
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    if (getAdapterPosition() == 0) {
                        isExcute = true;
                    }
                }
            }
            return isExcute;
        }
    }

    public int getCount() {
        return count;
    }

}
