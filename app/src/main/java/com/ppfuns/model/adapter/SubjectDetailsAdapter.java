package com.ppfuns.model.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.ppfuns.model.entity.SubjectContent;
import com.ppfuns.ui.view.FancyCoverFlow;
import com.ppfuns.ui.view.FancyCoverFlowAdapter;
import com.ppfuns.ui.view.ShapeImageView;
import com.ppfuns.vod.R;

import java.util.List;

/**
 * Created by lenovo on 2016/6/23.
 */
public class SubjectDetailsAdapter extends FancyCoverFlowAdapter {
    private int mItemWidth = 540;
    private int mItemHeight = 740;

    private AnimationDrawable currentAnimDrawable;
    private Drawable defaultDrawable;

    private Context context;
    protected ImageLoader imageLoader;
    protected DisplayImageOptions options;
    private List<SubjectContent> list;
    private FancyCoverFlow fancyCoverFlow;
    public String TAG = "SubjectDetailsAdapter";

    public SubjectDetailsAdapter(Context context,  List<SubjectContent> list,
                                 ImageLoader imageLoader,FancyCoverFlow fancyCoverFlow) {
        this.context = context;
        this.list = list;
        this.imageLoader = imageLoader;

//        mItemWidth = UIUtils.dip2px(context, 350);
//        mItemHeight = UIUtils.dip2px(context, 440);
        this.fancyCoverFlow = fancyCoverFlow;

        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true).cacheInMemory(true)
                .showImageOnLoading(R.drawable.subject_details_default)
                .showImageOnFail(R.drawable.subject_details_default)
                .showImageForEmptyUri(R.drawable.subject_details_default)
                .cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

    }

    @Override
    public int getCount() {
        if(list.size()<3){
            return list.size();
        }else{
            return 50;
        }
    }

    @Override
    public SubjectContent getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

//    /**
//     * 刷新指定item
//     *
//     * @param index    用于集合取数据
//     * @param position item在listview中的位置
//     */
//    public void updateItem(int index, int position) {
//
//        // 获取当前可以看到的item位置
//        int visiblePosition = fancyCoverFlow.getFirstVisiblePosition();
//        // 如添加headerview后 firstview就是hearderview
//        // 所有索引+1 取第一个view
//        // View view = listview.getChildAt(index - visiblePosition + 1);
//        // 获取点击的view
//        View view = fancyCoverFlow.getChildAt(position - visiblePosition);
//        TextView selectBtn = (TextView) view.findViewById(R.id.select_btn);
//        // 获取mDataList.set(ids, item);更新的数据
//        Subject.HttpCommon.ResultBean.SubjectContentList bean = list.get(index);
//
//    }

    class ViewHolder {
        RelativeLayout itemView;
        ShapeImageView itemImg;
        ImageView img_fous;
        ImageView img_tip;
    }

    @Override
    public View getCoverFlowItem(final int position, View convertView,
                                 ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.view_cover_flow_item, null);
            viewHolder = new ViewHolder();
            viewHolder.itemView = (RelativeLayout) convertView
                    .findViewById(R.id.item_img_view);
            viewHolder.itemImg = (ShapeImageView) convertView
                    .findViewById(R.id.item_img);
            viewHolder.img_fous = (ImageView) convertView.findViewById(R.id.img_fous);
            viewHolder.img_tip = (ImageView) convertView.findViewById(R.id.img_tip);
            convertView.setLayoutParams(new FancyCoverFlow.LayoutParams(
                    mItemWidth, mItemHeight));
            if (convertView != null) {
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(list!=null&&list.size()>0){
            viewHolder.img_tip.setVisibility(View.VISIBLE);
            final int index = position % list.size();
           // final int index = position;
            if (list.size() > index) {
                final  SubjectContent bean = list.get(index);
                if (bean != null) {
                    imageLoader.displayImage(bean.contentPosters, viewHolder.itemImg, options);
                }
            }
        }else{
            viewHolder.img_tip.setVisibility(View.GONE);
        }
       // 设置的抗锯齿,防止图像在旋转的时候出现锯齿
//        BitmapDrawable drawable = (BitmapDrawable) viewHolder.itemImg.getDrawable();
//        drawable.setAntiAlias(true);
//        ImageView i = createReflectedImages(context,drawableToBitmap(viewHolder.itemImg.getDrawable()));
//        i.setLayoutParams(new FancyCoverFlow.LayoutParams(mItemWidth, mItemHeight));
//        i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        return convertView;
    }

    public ImageView createReflectedImages(Context mContext,Bitmap originalImage) {

       // Bitmap originalImage = BitmapFactory.decodeResource(mContext.getResources(), imageId);

        final int reflectionGap = 4;

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
                height / 2, width, height / 2, matrix, false);

        Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                (height + height / 2), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);

        canvas.drawBitmap(originalImage, 0, 0, null);

        Paint deafaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);

        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, originalImage
                .getHeight(), 0, bitmapWithReflection.getHeight()
                + reflectionGap, 0x70ffffff, 0x00ffffff, Shader.TileMode.MIRROR);

        paint.setShader(shader);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                + reflectionGap, paint);

        ImageView imageView = new ImageView(mContext);
        imageView.setImageBitmap(bitmapWithReflection);

        return imageView;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(

                drawable.getIntrinsicWidth(),

                drawable.getIntrinsicHeight(),

                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        //canvas.setBitmap(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        drawable.draw(canvas);

        return bitmap;

    }

}







