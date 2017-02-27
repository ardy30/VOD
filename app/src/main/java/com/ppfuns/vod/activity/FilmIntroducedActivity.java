package com.ppfuns.vod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.ppfuns.util.LogUtils;
import com.ppfuns.vod.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fanshoupeng on 2016/9/8.
 */
public class FilmIntroducedActivity extends BaseActivity {
    @BindView(R.id.tv_film)
    TextView tvFilm;
    private String TAG="FilmIntroducedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.film_introduced);
        ButterKnife.bind(this);
        Intent intent=getIntent();

        String  filmDes=intent.getStringExtra("filmDes");
//        LogUtils.i(TAG,"filmDes="+filmDes);
        tvFilm.setText(filmDes);

    }
}
