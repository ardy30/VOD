package com.ppfuns.model.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.ppfuns.model.entity.Search;
import com.ppfuns.ui.view.AlwaysMarqueeTextView;
import com.ppfuns.vod.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 创建者     庄丰泽
 * 创建时间   2016/9/2 16:25
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class VoicePlayListAdapter extends RecyclerView.Adapter<VoicePlayListAdapter.VoicePlayListHolder> {
    private static final java.lang.String TAG = "VoicePlayListAdapter";
    private Context mContext;

    private List<Search> mPageContent;

    DisplayImageOptions mOptions;

    public VoicePlayListAdapter(Context context, List<Search> pageContent
    ) {
        mContext = context;
        mPageContent = pageContent;
        mOptions = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .showImageOnLoading(R.drawable.movie_default_270x360)
                .showImageOnFail(R.drawable.movie_default_270x360)
                .cacheOnDisc(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

    }

    @Override
    public VoicePlayListAdapter.VoicePlayListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_voiceplay, parent, false);
       // View view = View.inflate(mContext,R.layout.item_voiceplay,null);

        return new VoicePlayListHolder(view);
    }

    @Override
    public void onBindViewHolder(VoicePlayListAdapter.VoicePlayListHolder holder, int position) {
        imageLoad(mPageContent.get(position).picUrl, holder.imageView);
        holder.title.setText(mPageContent.get(position).contentName);
    }

    @Override
    public int getItemCount() {

        if (mPageContent.size() == 0)
            return 0;

        return mPageContent.size();
    }


    /**
     * ImageLoader加载图片的方法
     *
     * @param imageURL  图片网络地址
     * @param imageView 当前图片控件
     */
    public void imageLoad(String imageURL, ImageView imageView) {
        if (TextUtils.isEmpty(imageURL)) {
            imageView.setImageResource(R.drawable.movie_default_270x360);
            return;
        }
        ImageLoader.getInstance().displayImage(imageURL, imageView, mOptions);
    }


    public class VoicePlayListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.voiceplay_img)
        ImageView             imageView;
        @BindView(R.id.voiceplay_videoname)
        AlwaysMarqueeTextView title;
        @BindView(R.id.voiceplay_item_root)
        RelativeLayout        root;


        public VoicePlayListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            root.setOnClickListener(this);
            imageView.setOnClickListener(this);
            title.setOnClickListener(this);
        }

        public void setTitle(String str) {
            if (!TextUtils.isEmpty(str)) {
                title.setText(str);
            }
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null)
                mOnItemClickListener.OnClick(v, getAdapterPosition());
        }
    }


    public interface OnItemClickListener {
        void OnClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
