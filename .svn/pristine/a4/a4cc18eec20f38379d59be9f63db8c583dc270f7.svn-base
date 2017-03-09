package com.ppfuns.model.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ppfuns.model.entity.AlbumContents;
import com.ppfuns.model.entity.ContentType;
import com.ppfuns.model.entity.SubjectContent;
import com.ppfuns.ui.view.AlwaysMarqueeTextView;
import com.ppfuns.ui.view.MainUpView;
import com.ppfuns.util.LogUtils;
import com.ppfuns.util.ToastUtils;
import com.ppfuns.vod.R;
import com.ppfuns.vod.activity.ARdetailActivity;
import com.ppfuns.vod.activity.AlbumActivity;
import com.ppfuns.vod.activity.BaseActivity;
import com.ppfuns.vod.activity.SubjectDetailsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zpf on 2016/12/5.
 */

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ViewHolder> {

    private final String TAG = ChartAdapter.class.getSimpleName();

    private Context mContext;
    private LayoutInflater mInflater;
    private MainUpView mMainUpView;
    private List<AlbumContents> mList;

    public ChartAdapter(Context context, List<AlbumContents> list) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_chart, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.button = (TextView) view.findViewById(R.id.btn_number);
        viewHolder.text = (AlwaysMarqueeTextView) view.findViewById(R.id.tv_text);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0) {
            holder.button.setBackgroundResource(R.drawable.first);
        } else if (position == 1) {
            holder.button.setBackgroundResource(R.drawable.second);
        } else if (position == 2) {
            holder.button.setBackgroundResource(R.drawable.third);
        } else {
            holder.button.setText(String.valueOf(position + 1));
        }
        AlbumContents chart = mList.get(position);
        if (chart != null) {
            String txt = chart.contentName;
            if (!TextUtils.isEmpty(txt)) {
                holder.text.setText(txt);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnFocusChangeListener, View.OnClickListener, View.OnKeyListener{

        TextView button;
        AlwaysMarqueeTextView text;

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
        public void onFocusChange(View v, boolean hasFocus) {
            LogUtils.d(TAG, "onFocusChange item");
            if (hasFocus) {
                if (mMainUpView != null) {
                    mMainUpView.setFocusView(v, 1.0f);
                }
                if (text.getTextWidth() > text.getWidth() - text.getPaddingLeft() - text.getPaddingRight()) {
                    text.startFrom0();
                }
            } else {
                text.stopScroll();
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            AlbumContents content = mList.get(position);
            redirtPage(content);
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && mList != null && getAdapterPosition() == mList.size() - 1) {
                    return true;
                }
            }
            return false;
        }
    }

    private void redirtPage(AlbumContents content) {
        if (content == null) {
            ToastUtils.showShort(mContext, mContext.getString(R.string.data_error));
            return;
        }
        int cpId = ((BaseActivity)mContext).getVodColumnId();
        int contentType = content.contentType;
        if (contentType == 1) {//专题
            Intent intent = new Intent(mContext, SubjectDetailsActivity.class);
            intent.putExtra("subjectId", content.contentId);
            intent.putExtra(AlbumActivity.COL_CONTENT_ID, cpId);
            mContext.startActivity(intent);
        } else if (contentType == 0) {//专辑
            Intent intent = new Intent(mContext, AlbumActivity.class);
            intent.putExtra(AlbumActivity.ALBUM_ID, content.contentId);
            intent.putExtra(AlbumActivity.COL_CONTENT_ID, cpId);
            mContext.startActivity(intent);
        } else if (contentType == ContentType.AR_TYPE) {
            String posUrl = content.contentPosters;
            if (!TextUtils.isEmpty(posUrl)) {
                SubjectContent subjectContent = new SubjectContent();
                subjectContent.contentPosters = posUrl;
                ArrayList<SubjectContent> sList = new ArrayList<>();
                sList.add(subjectContent);
                Intent bIntent = new Intent(mContext, ARdetailActivity.class);
                bIntent.putExtra(ARdetailActivity.LIST, sList);
                bIntent.putExtra(AlbumActivity.COL_CONTENT_ID, cpId);
                mContext.startActivity(bIntent);
            }
        }
    }

    public void setFocusView(MainUpView mainUpView) {
        mMainUpView = mainUpView;
    }
}
