package com.ppfuns.model.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ppfuns.model.entity.Search;
import com.ppfuns.ui.view.AlwaysMarqueeTextView;
import com.ppfuns.util.LogUtils;
import com.ppfuns.vod.R;
import com.ppfuns.vod.activity.SearchActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zpf on 2016/6/30.
 */
public class SearchGridAdapter extends RecyclerView.Adapter<SearchGridAdapter.SearchGridHolder> {

    private static final java.lang.String TAG = "SearchGridAdapter";
    private Context mContext;

    private List<Search> mPageContent;

    public int mKeyCode = -1;


    public SearchGridAdapter(Context context, List<Search> pageContent) {
        mContext = context;
        mPageContent = pageContent;

    }

    @Override
    public SearchGridHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_poster, parent, false);

        return new SearchGridHolder(view);
    }


    @Override
    public void onBindViewHolder(final SearchGridHolder holder, int position) {
        imageLoad(mPageContent.get(position).picUrl, holder.imageView);
        holder.setTitle(mPageContent.get(position).contentName);
        holder.setInfo(mPageContent.get(position).mainActors);
        holder.setType(mPageContent.get(position).labels);

    }

    private static long lastClickTime;

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 250) {
            return true;
        }
        lastClickTime = time;
        return false;
    }


    /**
     * ImageLoader加载图片的方法
     *
     * @param imageURL  图片网络地址
     * @param imageView 当前图片控件
     */
    public void imageLoad(String imageURL, ImageView imageView) {
        if (TextUtils.isEmpty(imageURL)) {
            imageView.setImageResource(R.drawable.search_default_195x260);
            return;
        }
        Glide.with(mContext).load(imageURL).diskCacheStrategy(DiskCacheStrategy.SOURCE).priority(Priority.HIGH).error(R.drawable.search_default_195x260).placeholder(R.drawable.search_default_195x260).into(imageView);
    }


    @Override
    public int getItemCount() {

        if (mPageContent.size() == 0)
            return 0;

        return mPageContent.size();
    }


    class SearchGridHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener {

        @BindView(R.id.item_search_lv_iv)
        ImageView             imageView;
        @BindView(R.id.item_search_lv_tv_title)
        AlwaysMarqueeTextView title;
        @BindView(R.id.item_search_lv_tv_info)
        TextView              info;
        @BindView(R.id.item_search_lv_tv_type)
        TextView              type;
        @BindView(R.id.search_rl_content)
        View                  childView;

        public SearchGridHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnFocusChangeListener(this);
            itemView.setOnKeyListener(this);
            childView.setOnClickListener(this);
            childView.setOnFocusChangeListener(this);
            childView.setOnKeyListener(this);

        }


        public void setTitle(String str) {
            if (!TextUtils.isEmpty(str)) {
                title.setText(str);
            }
        }

        public void setInfo(String str) {
            if (TextUtils.isEmpty(str)) {
                return;
            }

            LogUtils.d(TAG, str);
            info.setText(String.format(mContext.getString(R.string.search_actor), str));

        }

        public void setType(String str) {
            if (TextUtils.isEmpty(str)) {
                return;
            }
            type.setText(String.format(mContext.getString(R.string.search_type), str));
        }


        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null)
                mOnItemClickListener.OnClick(v, getAdapterPosition());
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    title.startFrom0();
                } else {
                    title.stopScroll();
                }
            if (getAdapterPosition() >= mPageContent.size() - 2 && mLoadMoreDataListener != null) {
                mLoadMoreDataListener.LoadData(v, getAdapterPosition());
            }

        }
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            LogUtils.d(TAG, "onkey keycode: " + keyCode + ", action: " + event.getAction());
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                mKeyCode = keyCode;
            }
            SearchActivity activity=(SearchActivity)mContext;
            if (!activity.isScrollOver && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT)) {
                return true;
            }
            if (getAdapterPosition() % 2 == 1 && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {

                if (getAdapterPosition() % 2 == 1 ){
                    return true;

                }

            }
            if ((getAdapterPosition() == 0 || getAdapterPosition() == 1) && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                return true;
            } else if ((getAdapterPosition() == mPageContent.size() - 1 || getAdapterPosition() == mPageContent.size() - 2)
                    && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                return true;
            }


            return false;
        }
    }


    public interface OnItemClickListener {
        void OnClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    private OnLoadMoreDataListener mLoadMoreDataListener;

    public interface OnLoadMoreDataListener {
        void LoadData(View focusView, int postion);
    }

    public void setOnLoadMoreDataListener(OnLoadMoreDataListener loadMoreDataListener) {
        mLoadMoreDataListener = loadMoreDataListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
