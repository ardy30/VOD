package com.ppfuns.model.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ppfuns.model.entity.VideoBean;
import com.ppfuns.vod.R;

import java.util.List;

/**
 * Created by hmc on 2016/6/20.
 */
public class EpisodeContentAdapter extends RecyclerView.Adapter<EpisodeContentAdapter.ViewHolder> {

    private final String TAG = EpisodeContentAdapter.class.getSimpleName();
    private LayoutInflater mInflater;

    private List<VideoBean> mBeanList;

    private OnItemActionListener mListener;

    private final int EPISODE_CRITICAL_VALUE = 15;
    private int mCurCount = 0;
    private int mFakeCount = 0;

    public EpisodeContentAdapter(Context context, List<VideoBean> beanList) {
        mInflater = LayoutInflater.from(context);
        this.mBeanList = beanList;
    }

    public void setOnItemActionListener(OnItemActionListener listener) {
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_episode_content, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.tv_episode = (TextView) view.findViewById(R.id.tv_episode);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.tv_episode.setText("" + (position + 1));
        viewHolder.tv_episode.setFocusable(true);
        viewHolder.tv_episode.setVisibility(View.VISIBLE);
        if (position > mCurCount - 1) {
            viewHolder.tv_episode.setFocusable(false);
            viewHolder.tv_episode.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (mBeanList != null && mBeanList.size() > 0) {
            mCurCount = mBeanList.size();
            if (mCurCount <= EPISODE_CRITICAL_VALUE) return mCurCount;
            int residue = mCurCount % EPISODE_CRITICAL_VALUE;
            if (residue == 0) return mCurCount;
            return mCurCount + EPISODE_CRITICAL_VALUE - residue;
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnFocusChangeListener, View.OnKeyListener, View.OnClickListener {

        TextView tv_episode;

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
                mListener.onItemClick(EpisodeContentAdapter.this, itemView, getAdapterPosition());
            }
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (mListener != null) {
                mListener.onFocusChange(EpisodeContentAdapter.this, itemView, hasFocus, getAdapterPosition());
            }
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            boolean isExcute = false;
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    mListener.onKey(EpisodeContentAdapter.this, itemView, keyCode, event, getAdapterPosition());
                    isExcute = true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    if (getAdapterPosition() == 0) {
                        isExcute = true;
                    }
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    if (getAdapterPosition() == mBeanList.size() - 1) {
                        isExcute = true;
                    }
                }
            }
            return isExcute;
        }
    }
}
