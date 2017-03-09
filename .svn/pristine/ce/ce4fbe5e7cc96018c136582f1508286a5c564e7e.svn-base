package com.ppfuns.model.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.ppfuns.model.entity.AlbumContents;
import com.ppfuns.ui.view.AlwaysMarqueeTextView;
import com.ppfuns.vod.R;

import java.util.List;

public class AlbumAdapter extends BaseAdapter {

    private Activity owner;
    private List<AlbumContents> contentList;
    private ImageLoader imageLoader;
    protected DisplayImageOptions options = new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .showImageOnLoading(R.drawable.index_big_default2)
            .showImageOnFail(R.drawable.index_big_default2)
            .showImageForEmptyUri(R.drawable.index_big_default2)
            .cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
            .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new FadeInBitmapDisplayer(300)).build();

    protected DisplayImageOptions options2 = new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
            .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new FadeInBitmapDisplayer(300)).build();

    public AlbumAdapter(Activity owner, List<AlbumContents> vec, ImageLoader imageLoader) {
        this.owner = owner;
        this.contentList = vec;
        this.imageLoader = imageLoader;
    }

    public int getCount() {
        return contentList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(owner).inflate(R.layout.channel_detail_grid_item, parent,false);

            holder = new ViewHolder();
            holder.filmeName = (AlwaysMarqueeTextView) convertView.findViewById(R.id.filmeName);
            holder.score = (TextView) convertView.findViewById(R.id.score);
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.img_tip = (ImageView) convertView.findViewById(R.id.img_tip);
            holder.img_layout = (RelativeLayout) convertView.findViewById(R.id.img_layout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AlbumContents bean = contentList.get(position);
        imageLoader.displayImage(bean.contentPosters, holder.img, options);
        if(!TextUtils.isEmpty(bean.cornerMark)){
            holder.img_tip.setVisibility(View.VISIBLE);
            imageLoader.displayImage(bean.cornerMark, holder.img_tip, options2);
        }else{
            holder.img_tip.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(bean.contentName)) {
            holder.filmeName.setText(bean.contentName);
        } else {
            holder.filmeName.setText("");
        }
        if(!TextUtils.isEmpty(bean.score)){
            holder.score.setText(String.valueOf(Double.valueOf(bean.score)));
        }else{
            holder.score.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder {
        AlwaysMarqueeTextView filmeName;
        TextView score;
        ImageView img;

        ImageView img_tip;
        RelativeLayout img_layout;
    }
}