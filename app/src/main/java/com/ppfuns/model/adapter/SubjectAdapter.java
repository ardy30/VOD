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
import com.ppfuns.model.entity.Subjects;
import com.ppfuns.util.LogUtils;
import com.ppfuns.vod.R;

import java.util.List;

public class SubjectAdapter extends BaseAdapter{

    private Activity owner;
//    private List<Subject.HttpCommon.ResultBean> contentList;
    private List<Subjects> contentList;
    private ImageLoader imageLoader;
    protected DisplayImageOptions options = new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .showImageOnLoading(R.drawable.ju_default_730x458)
            .showImageOnFail(R.drawable.ju_default_730x458)
            .showImageForEmptyUri(R.drawable.ju_default_730x458)
            .cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
            .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new FadeInBitmapDisplayer(300)).build();
    private String TAG = "SubjectAdapter";

    public SubjectAdapter(Activity owner, List<Subjects> vec, ImageLoader imageLoader) {
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
            convertView = LayoutInflater.from(owner).inflate(R.layout.subject_grid_item, null);

            holder = new ViewHolder();
            holder.subjectName = (TextView) convertView.findViewById(R.id.subjectName);
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.img_tip = (ImageView) convertView.findViewById(R.id.img_tip);
            holder.rootlayout = (RelativeLayout) convertView.findViewById(R.id.rootlayout);
            holder.img_layout = (RelativeLayout) convertView.findViewById(R.id.img_layout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Subjects bean = contentList.get(position);
        if (!TextUtils.isEmpty(bean.subjectName)) {
            holder.subjectName.setText(bean.subjectName);
        } else {
            holder.subjectName.setText("");
        }
        imageLoader.displayImage(bean.subjectPosters,holder.img,options);
        if(bean.isLost){
            holder.img_tip.setVisibility(View.INVISIBLE);
            //holder.rootlayout.setFocusable(false);
            holder.rootlayout.setVisibility(View.INVISIBLE);
        }else{
            holder.img_tip.setVisibility(View.VISIBLE);
            //holder.rootlayout.setFocusable(false);
            holder.rootlayout.setVisibility(View.VISIBLE);
        }
       // imageLoader.displayImage(bean.getContentPosters(), holder.img, options);

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        LogUtils.i(TAG,"position: "+ position);
        if(contentList.get(position).isLost){
            return false;
        }else{
            return super.isEnabled(position);
        }
    }

    static class ViewHolder {
        TextView subjectName;
        ImageView img;
        ImageView img_tip;
        RelativeLayout rootlayout;
        RelativeLayout img_layout;

    }
}