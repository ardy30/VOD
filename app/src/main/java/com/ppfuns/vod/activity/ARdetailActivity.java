package com.ppfuns.vod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ppfuns.model.entity.SubjectContent;
import com.ppfuns.vod.R;

import java.util.ArrayList;

public class ARdetailActivity extends BaseActivity {

    public static final String POS  = "pos";
    public static final String LIST = "list";
    ImageView                 mImageView;
    ArrayList<SubjectContent> mDatas;
    private int mPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ardetail);
        mImageView = (ImageView) findViewById(R.id.ivshow);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            mPos = intent.getIntExtra(POS, 0);

            String search_posurl = intent.getStringExtra("search_posurl");

            if (!TextUtils.isEmpty(search_posurl)) {
                SubjectContent subjectContent = new SubjectContent();
                subjectContent.contentPosters = search_posurl;
                ArrayList<SubjectContent> sList = new ArrayList<>();
                sList.add(subjectContent);
                mDatas = sList;
            } else {

                mDatas = (ArrayList<SubjectContent>) intent.getSerializableExtra(LIST);
            }


            showImage();


        }

    }

    private void showImage() {
        if (mDatas != null)
            ImageLoader.getInstance().displayImage(mDatas.get(mPos).contentPosters, mImageView);
            /*Picasso.with(this)
                    .load(mDatas.get(mPos).contentPosters)

                    .into(mImageView);*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mPos > 0) {
                    mPos--;
                } else
                    mPos = mDatas.size() - 1;
                showImage();
                //左键
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mPos < mDatas.size() - 1) {
                    mPos++;
                } else {
                    mPos = 0;
                }
                showImage();
                //右键
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
