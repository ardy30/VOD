package com.ppfuns.model.adapter;

import android.app.Activity;
import android.content.Context;
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
import com.ppfuns.model.entity.RecordingContents;
import com.ppfuns.ui.view.AlwaysMarqueeTextView;
import com.ppfuns.ui.view.LabelView;
import com.ppfuns.util.LogUtils;
import com.ppfuns.vod.R;

import java.util.List;

public class CollectAdapter extends BaseAdapter {

    private Context mContext;
    private List<RecordingContents> contentList;
    private ImageLoader imageLoader;
    private int picWidth,picHeight;

    /**
     * 记录类型 1表示播放历史记录,2表示收藏记录
     */
    private int mRecordType = 1;
    protected DisplayImageOptions options = new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .showImageOnLoading(R.drawable.index_big_default2)
            .showImageOnFail(R.drawable.index_big_default2)
            .showImageForEmptyUri(R.drawable.index_big_default2)
            .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
            .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new FadeInBitmapDisplayer(300)).build();


    public CollectAdapter(Context context, List<RecordingContents> vec, ImageLoader imageLoader, int picHeight) {

        this.mContext = context;
        this.contentList = vec;
        this.imageLoader = imageLoader;
        this.picWidth = picWidth;
        this.picHeight = (int)(picWidth*1.4031);
    }

    public void setRecordType(int recordType) {
        mRecordType = recordType;
    }

    public int getRecordType() {
        return mRecordType;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.collect_detail_grid_item, parent, false);

            holder = new ViewHolder();
            holder.filmeName = (AlwaysMarqueeTextView) convertView.findViewById(R.id.filmeName);
            holder.score = (TextView) convertView.findViewById(R.id.score);
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.img_layout = (RelativeLayout) convertView.findViewById(R.id.img_layout);
            holder.img_tip = (ImageView) convertView.findViewById(R.id.img_tip);
            holder.playStatus = (TextView) convertView.findViewById(R.id.playStatus);
           // holder.tips = (LabelView) convertView.findViewById(R.id.tips);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RecordingContents bean = contentList.get(position);
        if(!TextUtils.isEmpty(bean.posterUrl)){
            LogUtils.i("CollectAdapter","posterUrl: "+ bean.posterUrl);
            String[] array = bean.posterUrl.split("\\|");

            if(array!=null&&array.length>0){
                LogUtils.i("CollectAdapter","array: "+ array[0]);
                if(array.length==1){
                    imageLoader.displayImage(bean.posterUrl, holder.img, options);
                }else if(array.length==2){
                    imageLoader.displayImage(array[1], holder.img, options);
                }
            }else{
                imageLoader.displayImage(bean.posterUrl, holder.img, options);
            }
        }
        if (!TextUtils.isEmpty(bean.resourceName)) {
            holder.filmeName.setText(bean.resourceName);
        } else {
            holder.filmeName.setText("");
        }
        if (!TextUtils.isEmpty(bean.resourceName)) {
            holder.filmeName.setText(bean.resourceName);
        } else {
            holder.filmeName.setText("");
        }
        if (mRecordType == 1) {
            holder.playStatus.setVisibility(View.VISIBLE);
            if (bean.playStatus == 1) {
                holder.playStatus.setText(mContext.getString(R.string.record_play_end));
            } else {
                long time = bean.timePosition;
                long duration = bean.duration;
                holder.playStatus.setText(mContext.getString(R.string.record_play_time) + parseTime(time) + "/" + parseTime(duration));
            }
        } else {
            holder.playStatus.setVisibility(View.GONE);
        }

        if(bean.userType==1){
            holder.img_tip.setVisibility(View.VISIBLE);
            holder.img_tip.setImageResource(R.mipmap.cell_box);
        }else if(bean.userType==2){
            holder.img_tip.setVisibility(View.VISIBLE);
            holder.img_tip.setImageResource(R.mipmap.cell_phone);
        }else{
            holder.img_tip.setVisibility(View.GONE);
        }
    //    if(bean.getStore()>0){
            //holder.score.setText(""+bean.store);
//        }else{
//            holder.score.setText("");
//        }
        return convertView;
    }
    static class ViewHolder {
        AlwaysMarqueeTextView filmeName;
        TextView playStatus;
        TextView score;
        ImageView img;
        LabelView tips;
        ImageView img_tip;
        RelativeLayout img_layout;
    }

    public static String parseTime(long time) {
        StringBuffer buffer = new StringBuffer();
        long second = time / 1000;
        int min = (int) (second / 60);
        int sec = (int) (second % 60);
        buffer.append((min < 10) ? "0" + min + ":" : "" + min + ":");
        buffer.append((sec < 10) ? "0" + sec : "" + sec);
        return buffer.toString();
    }
}