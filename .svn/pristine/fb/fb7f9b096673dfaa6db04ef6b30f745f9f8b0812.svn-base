package com.ppfuns.model.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ppfuns.model.entity.RecommendBean;
import com.ppfuns.ui.view.AlwaysMarqueeTextView;
import com.ppfuns.util.LogUtils;
import com.ppfuns.vod.R;

import java.util.List;

/**
 * Created by hmc on 2016/6/20.
 */
public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.ViewHolder> {

    private final String TAG = RecommendAdapter.class.getSimpleName();

    private LayoutInflater      mInflater;
    private List<RecommendBean> mBeanList;

    private OnItemActionListener mListener;

    private DisplayImageOptions mOptions;
    private Context             mContext;

    public RecommendAdapter(Context context, List<RecommendBean> list) {
        mInflater = LayoutInflater.from(context);
        this.mBeanList = list;

//        mOptions = new DisplayImageOptions.Builder()
//                .resetViewBeforeLoading(true)
//                .showImageOnLoading(R.drawable.search_default_195x260)
//                .showImageOnFail(R.drawable.search_default_195x260)
//                .cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
//                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
//                .imageScaleType(ImageScaleType.EXACTLY)
//                .bitmapConfig(Bitmap.Config.RGB_565)
//                .build();
        mContext = context;
    }

    public void setOnItemActionListener(OnItemActionListener listener) {
        this.mListener = listener;
    }

    @Override
    public RecommendAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.recommend_detail, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.ivPic = (ImageView) view.findViewById(R.id.iv_pic);
        viewHolder.tvTxt = (AlwaysMarqueeTextView) view.findViewById(R.id.item_tv_name);
        viewHolder.tvTxt.bringToFront();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecommendAdapter.ViewHolder viewHolder, final int position) {

        RecommendBean bean = mBeanList.get(position);
        if (bean == null) {
            LogUtils.i(TAG, "recommend bean null");
            return;
        }
        String name = bean.getContentName();
        if (!TextUtils.isEmpty(name)) {
            viewHolder.tvTxt.setText(name);
        }
        String imageUrl = bean.getContentPosters();
        if (!TextUtils.isEmpty(imageUrl)) {
//            ImageLoader.getInstance().displayImage(imageUrl, viewHolder.ivPic, mOptions);
            Glide.with(mContext).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.SOURCE).priority(Priority.HIGH)
                    .placeholder(R.drawable.search_default_195x260).into(viewHolder.ivPic);

        }
        viewHolder.tvTxt.setGravity(Gravity.CENTER);
    }

    @Override
    public int getItemCount() {
        if (mBeanList != null && mBeanList.size() > 0) {
            return mBeanList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnFocusChangeListener, View.OnKeyListener, View.OnClickListener {
        AlwaysMarqueeTextView tvTxt;
        ImageView             ivPic;

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
            if (tvTxt.getTextWidth() > tvTxt.getWidth() - tvTxt.getPaddingLeft() - tvTxt.getPaddingRight()) {
                if (hasFocus) {
                    tvTxt.startFrom0();
                } else {
                    tvTxt.stopScroll();
                }
            }
            if (mListener != null) {
                mListener.onFocusChange(RecommendAdapter.this, ivPic, hasFocus, getAdapterPosition());
            }
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            boolean isExcute = false;
            int position = getAdapterPosition();
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                if (isFastClick() && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
//                    return true;
//                }
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP:
                        mListener.onKey(RecommendAdapter.this, itemView, keyCode, event, position);
                        isExcute = true;
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        isExcute = true;
                        break;
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        if (position == 0) return true;
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        if (position == mBeanList.size() - 1) {
//                            mListener.onKey(RecommendAdapter.this, itemView, keyCode, event, getAdapterPosition());
                            isExcute = true;
                        }
                        break;
                }
            }
            return isExcute;
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(RecommendAdapter.this, tvTxt, getAdapterPosition());
            }
        }
    }

    private static long lastClickTime = 0;
    private boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime <= 200) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public void updateData(List<RecommendBean> beanList, int startPos) {
        if (mBeanList != null) {
            mBeanList.addAll(beanList);
            notifyItemRangeInserted(startPos, beanList.size());
        }
    }
}
