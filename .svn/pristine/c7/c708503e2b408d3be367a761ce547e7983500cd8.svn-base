package com.ppfuns.model.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ppfuns.model.entity.Search;
import com.ppfuns.ui.view.ChangeColorTextView;
import com.ppfuns.ui.view.recycle.RecyclerViewTV;
import com.ppfuns.util.LogUtils;
import com.ppfuns.vod.R;
import com.ppfuns.vod.activity.SearchActivity;

import java.util.List;

;

/**
 * 创建者     庄丰泽
 * 创建时间   2016/6/29 15:09
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class SearchListAdapter extends RecyclerViewTV.Adapter<SearchListAdapter.SearchListViewHolder> {

    private static final String TAG = "SearchListAdapter";

    private SearchActivity mContext;


    private int mTextColorChangeSize;


    private List<Search> mPageContent;

    private String mKey;


    public SearchListAdapter(Context context, List<Search> pageContent,
                             int textColorChangeSize, String key) {
        mContext = (SearchActivity) context;
        mPageContent = pageContent;
        mTextColorChangeSize = textColorChangeSize;
        mKey = key;

    }

    @Override
    public SearchListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_center, parent, false);
        return new SearchListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchListViewHolder holder, int position) {

        holder.setName(mPageContent.get(position).contentName, mTextColorChangeSize, mKey);
        holder.setType(mPageContent.get(position).labels);
        holder.setSeq(position + 1);

    }

    public void setTextColorChangeSize(int textColorChangeSize) {
        mTextColorChangeSize = textColorChangeSize;
    }

    @Override
    public int getItemCount() {

        if (mPageContent == null)
            return 0;

        return mPageContent.size();
    }


    class SearchListViewHolder extends RecyclerViewTV.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener {
        private TextView mSeq;

        private ChangeColorTextView mName;

        private TextView mType;

        public SearchListViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnFocusChangeListener(this);
            itemView.setOnKeyListener(this);
            itemView.setNextFocusLeftId(R.id.iv_back);
            mSeq = (TextView) itemView.findViewById(R.id.item_search_lv_tv_seq);

            mName = (ChangeColorTextView) itemView.findViewById(R.id.item_search_lv_tv_name);

            mType = (TextView) itemView.findViewById(R.id.item_search_lv_tv_type);

        }


        public void setName(String name, int textColorChangeSize, String key) {

            if (name != null)
                mName.setText(name, textColorChangeSize, key);
        }

        public void setSeq(int position) {

            mSeq.setText(position + "、");
        }

        public void setType(String type) {
            if (!TextUtils.isEmpty(type)) {
                mType.setText(type);
            }
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.OnClick(v, getAdapterPosition());
            }
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    mName.startFrom0();
                } else {
                    mName.stopScroll();
                }
            if (getAdapterPosition() == mPageContent.size() - 1 && mLoadMoreDataListener != null) {
                mLoadMoreDataListener.loadData(v, getAdapterPosition());
            }

        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
/*            if (isFastClick() && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                LogUtils.d(TAG, "KeyEvent.KEYCODE_DPAD_DOWN");
                return true;
            }*/

            if (getAdapterPosition() == 0 && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                LogUtils.d(TAG, "KEYCODE_DPAD_UP");

                return true;
            } else if (getAdapterPosition() == mPageContent.size() - 1 && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                LogUtils.d(TAG, "KEYCODE_DPAD_DOWN");
                return true;
            }


            return false;
        }
    }

    private static long lastClickTime;

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 200) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public interface OnItemClickListener {
        void OnClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnLoadMoreDataListener {
        void loadData(View focusView, int postion);
    }

    private OnLoadMoreDataListener mLoadMoreDataListener;

    public void setOnLoadMoreDataListener(OnLoadMoreDataListener loadMoreDataListener) {
        mLoadMoreDataListener = loadMoreDataListener;
    }


    public void upDateAdapter(int textColorChangeSize, String key) {

        mTextColorChangeSize = textColorChangeSize;

        mKey = key;
        this.notifyDataSetChanged();
    }


}
