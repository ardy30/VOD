package com.ppfuns.model.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ppfuns.model.entity.Channel;
import com.ppfuns.ui.view.AlwaysMarqueeTextView;
import com.ppfuns.vod.R;
import com.ppfuns.vod.activity.AllChannelActivity;

import java.util.List;


/**
 * 创建者     庄丰泽
 * 创建时间   2016/6/17 14:53
 * 描述	      主页面 栏目位gridView 的适配器
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class AllChannelAdapter extends RecyclerView.Adapter<AllChannelAdapter.AllChannelViewHolder> {

    private List<Channel> mDatas;


    private Context mContext;


    public AllChannelAdapter(Context context, List<Channel> datas) {

        mDatas = datas;
        mContext = context;
        AllChannelActivity cxt = (AllChannelActivity) mContext;
        cxt.mOpenEffectBridge.setVisibleWidget(true);
        cxt.mOpenEffectBridge.setTranDurAnimTime(0);
    }


    @Override
    public AllChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_allchannel, parent, false);


        return new AllChannelViewHolder(view);

    }

    @Override
    public int getItemCount() {
        if (mDatas.size() != 0)
            return mDatas.size();
        return 0;
    }

    @Override
    public void onBindViewHolder(final AllChannelViewHolder holder, int position) {
        holder.setImageView(position);
        holder.setTextView(position);
        if (position == 0){
//            holder.itemView.requestFocus();
//            final AllChannelActivity context = (AllChannelActivity) mContext;
//            holder.itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    context.mOpenEffectBridge.setFocusView(holder.itemView,1);
//                    holder.itemView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                }
//            });
        }

    }

    public class AllChannelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener {


        private ImageView mIcon;
        private AlwaysMarqueeTextView mTextView;


        public AllChannelViewHolder(View itemView) {

            super(itemView);

            mIcon = (ImageView) itemView.findViewById(R.id.allchannel_rv_iv_icon);

            mTextView = (AlwaysMarqueeTextView) itemView.findViewById(R.id.allchannel_tv_name);

            itemView.setOnClickListener(this);
            itemView.setOnFocusChangeListener(this);

        }

        public void setImageView(int position) {

            imageLoad(position, mIcon);
        }

        public void setTextView(int position) {
            if (mDatas == null || mDatas.size() <=0 || position >= mDatas.size()) {
                return;
            }
            Channel channel = mDatas.get(position);
            if (channel != null) {
                String name = channel.operationTagName;
                if (!TextUtils.isEmpty(name)) {
                    mTextView.setText(name);
                }
            }
            if (position == 0) {
                final AllChannelActivity context = (AllChannelActivity) mContext;
//                context.mOpenEffectBridge.setFocusView(itemView,1);
                mTextView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        itemView.requestFocus();
//                        final AllChannelActivity context = (AllChannelActivity) mContext;
//                        context.mOpenEffectBridge.setFocusView(itemView,1);
//                        context.mOpenEffectBridge.setVisibleWidget(false);
//                        context.mOpenEffectBridge.setTranDurAnimTime(200);
                        mTextView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });
            }
        }

        /**
         * ImageLoader加载图片的方法
         *
         * @param position  数据索引
         * @param imageView 当前图片控件
         */
        public void imageLoad(int position, ImageView imageView) {

            if (position >= mDatas.size()) {
                return;
            }

            String imageURL = mDatas.get(position).picUrl;

            if (TextUtils.isEmpty(imageURL) || imageView == null) {
                return;
            }

            Glide.with(mContext).load(imageURL)
                    .placeholder(R.drawable.ju_default_240x224)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .error(R.drawable.ju_default_240x224)
                    .crossFade()
                    .into(imageView);
        }

        @Override
        public void onClick(View v) {
            AllChannelActivity context = (AllChannelActivity) mContext;
            context.openItem(getAdapterPosition());
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (mTextView.getTextWidth() > mTextView.getWidth() - mTextView.getPaddingLeft() - mTextView.getPaddingRight() && mTextView.getWidth() > 0) {
                if (hasFocus) {
                    mTextView.startFrom0();
                } else {
                    mTextView.stopScroll();
                }
            }
        }
    }

}
