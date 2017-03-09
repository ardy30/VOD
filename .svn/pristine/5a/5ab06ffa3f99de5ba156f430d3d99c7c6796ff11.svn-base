package com.ppfuns.model.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ppfuns.model.entity.ChartChannel;
import com.ppfuns.util.LogUtils;
import com.ppfuns.vod.R;

import java.util.List;

/**
 * Created by zpf on 2016/12/8.
 */

public class ChartChannelAdapter extends RecyclerView.Adapter<ChartChannelAdapter.ViewHolder> {

    private final String TAG = ChartChannelAdapter.class.getSimpleName();
    private Context mContext;
    private LayoutInflater mInflater;
    private List<ChartChannel> mChannelList;

    public ChartChannelAdapter(Context context, List<ChartChannel> channelList) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mChannelList = channelList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_chart_channel, parent, false);
        ViewHolder viewHolder =  new ViewHolder(view);
        viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LogUtils.d(TAG, "position: " + position);
        ChartChannel chartChannel = mChannelList.get(position);
        if (chartChannel != null) {
            String txt = chartChannel.operationTagName;
            if(!TextUtils.isEmpty(txt)) {
                holder.tvTitle.setText(txt + mContext.getString(R.string.chart_flag_name));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mChannelList != null) {
            return mChannelList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class SpaceItemDec extends RecyclerView.ItemDecoration {

        private int mRightSpace;

        public SpaceItemDec(int rightSpace) {
            this.mRightSpace = rightSpace;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.right = mRightSpace;
        }
    }
}
