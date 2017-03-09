package com.ppfuns.model.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ppfuns.model.entity.Channel;
import com.ppfuns.ui.view.AlwaysMarqueeTextView;
import com.ppfuns.vod.R;
import com.ppfuns.vod.activity.MainActivity;

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
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private List<Channel> mDatas;


    private MainActivity mContext;

    private int id;

    public MainAdapter(MainActivity mainActivity, List<Channel> datas) {

        mDatas = datas;
        mContext = mainActivity;
        id = 10000;
    }


    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = View.inflate(mContext, R.layout.item_rv_main, null);

        //View view= LayoutInflater.from(mContext).inflate(R.layout.item_rv_main,parent,false);


        // view.bringToFront();

        return new MainViewHolder(view);

    }

    @Override
    public int getItemCount() {
        if (mDatas.size() != 0)
            return mDatas.size();
        return 0;
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {

        holder.setImageView(position);
        holder.setTitle(mDatas.get(position).operationTagName);

    }

    public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener {


        private ImageView mIcon;

        private AlwaysMarqueeTextView mTitle;
        private View                  mMas;


        public MainViewHolder(View itemView) {

            super(itemView);

            mIcon = (ImageView) itemView.findViewById(R.id.main_rv_item_iv_icon);

            mTitle = (AlwaysMarqueeTextView) itemView.findViewById(R.id.main_rv_item_tv_title);

            mMas = itemView.findViewById(R.id.item_mas);

            itemView.setOnClickListener(this);

            itemView.setOnFocusChangeListener(this);

            mIcon.setOnClickListener(this);
        }

        private void setOnItemClickListener(View itemView) {

            if (mOnItemClickListener != null) {

                mOnItemClickListener.OnItemClick(itemView, getPosition());
            }

        }

        public void setTitle(String titleStr) {
            if (!TextUtils.isEmpty(titleStr)) {
                mTitle.setText(titleStr);
            }
        }

        public void setImageView(int position) {

            imageLoad(position, mIcon);


            itemView.setId(id++);

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

            Glide.with(mContext).load(imageURL).
                    placeholder(R.drawable.ju_default_240x224)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .error(R.drawable.ju_default_240x224)
                    .into(imageView);
        }


        @Override
        public void onClick(View v) {
            setOnItemClickListener(v);
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                //                mMas.setVisibility(View.VISIBLE);
                //                mTitle.setVisibility(View.VISIBLE);
                mTitle.startFrom0();

            } else {
                //                mMas.setVisibility(View.INVISIBLE);
                //                mTitle.setVisibility(View.INVISIBLE);
                mTitle.stopScroll();
            }
        }
    }

    public static class SpaceItemDec extends RecyclerView.ItemDecoration {

        private int mRightSpace;
        private int mBottomSpace;

        public SpaceItemDec(int rightSpace, int bottomSpace) {
            this.mRightSpace = rightSpace;
            this.mBottomSpace = bottomSpace;
        }


        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.right = mRightSpace;
            outRect.bottom = mBottomSpace;

        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {

        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {

        void OnItemClick(View view, int position);

    }

    private OnItemClickListener mOnItemClickListener;

}
