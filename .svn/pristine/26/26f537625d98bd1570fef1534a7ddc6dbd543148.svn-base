package com.ppfuns.ui.view.player;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ppfuns.util.LogUtils;
import com.ppfuns.util.TimeUtils;
import com.ppfuns.vod.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by lenovo on 2016/6/17.
 */
public class MediaController extends RelativeLayout implements IMediaControl, SeekBar.OnSeekBarChangeListener, View.OnClickListener {


    @BindView(R.id.seekBar)
    public PopupSeekBar mProgressSeekBar;
    //    private RecyclerView mDrama;
    @BindView(R.id.rl_rfcontrol)
    public RelativeLayout mRfcontrol;
    @BindView(R.id.tv_time)
    public TextView mTimeTxt;//播放时间
    @BindView(R.id.tv_title)
    public TextView mTitle;
    @BindView(R.id.tv_info)
    public TextView mInfo;
    @BindView(R.id.ib_pause_play)
    public ImageView mPauseAndPlay;
    private ControlCallback mMediaControl;
    private Context mContext;
    private static final String TAG = "MediaController";


    public MediaController(Context context) {
        this(context, null);
    }

    public MediaController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        View.inflate(context, R.layout.layout_media_controller, this);
        ButterKnife.bind(this);
        initData();
    }


    @Override
    public void setTitleText(@NonNull CharSequence string) {
        if (!TextUtils.isEmpty(string)) {
            mTitle.setText(string);
        }
    }

    @Override
    public void setInfoText(@NonNull CharSequence string) {
        if (!TextUtils.isEmpty(string)) {
            mInfo.setText(string);
        } else {
            mInfo.setVisibility(GONE);
        }
    }


    private void initData() {

        Glide.with(mContext).load(R.drawable.ic_pause_circle_outline).into(mPauseAndPlay);
//        mPauseAndPlay.setOnClickListener(this);
//        mProgressSeekBar.getHintDelegate().setHintAdapter(mSeekBarHintAdapter);
        hideDramaSelect();
    }


    @Override
    public void setProgressBar(@IntRange(from = 0, to = 1000) int progress, @IntRange(from = 0, to = 1000) int loadProgress) {
//        if (progress < 0) progress = 0;
//        if (progress > 100) progress = 100;
//        if (loadProgress < 0) loadProgress = 0;
//        if (loadProgress > 100) loadProgress = 100;
        LogUtils.i(TAG, "progress :" + progress);
        mProgressSeekBar.setProgress(progress);

        mProgressSeekBar.setSecondaryProgress(loadProgress);
    }

    @Override
    public int getProgressFromSeekBar() {
        return mProgressSeekBar.getProgress();
    }

    @Override
    public void setPlayState(@PlayState int playState) {


        if (playState == PLAY) {
            Glide.with(mContext).load(R.drawable.ic_play_circle_outline).into(mPauseAndPlay);
        } else {
            Glide.with(mContext).load(R.drawable.ic_pause_circle_outline).into(mPauseAndPlay);
        }


    }

    @Override
    public void nextPlay() {

    }

    @Override
    public void lastPlay() {

    }

    @Override
    public void setProgressBarMax(int max) {
        mProgressSeekBar.setMax(max);
    }

    @Override
    public int getProgressBarMax() {
        return mProgressSeekBar.getMax();
    }

    @Override
    public boolean isShowControl() {
        return getVisibility() == View.VISIBLE;
    }

    @Override
    public void showControl() {
        setVisibility(VISIBLE);
        showRFControl();
    }

    @Override
    public void hideControl() {
        setVisibility(GONE);
        hideRFControl();
    }

    @Override
    public void showDramaSelect() {
        hideRFControl();
    }

    @Override
    public void hideDramaSelect() {
    }



    @Override
    public void playFinish(int duration) {
        mProgressSeekBar.setProgress(0);
        setPlayProgressTxt(0, duration);
        setPlayState(PAUSE);
    }

    @Override
    public void hideRFControl() {
        mRfcontrol.setFocusable(false);
        mRfcontrol.setVisibility(GONE);
        mProgressSeekBar.getHintDelegate().hidePopup();
        mProgressSeekBar.setVisibility(INVISIBLE);
    }

    @Override
    public void showRFControl() {
        mRfcontrol.setVisibility(VISIBLE);
        hideDramaSelect();
        mProgressSeekBar.setVisibility(VISIBLE);
        mProgressSeekBar.getHintDelegate().showPopup();
    }

    @Override
    public void setMediaControl(ControlCallback mMediaControl) {
        this.mMediaControl = mMediaControl;
    }

    @Override
    public void setPlayProgressTxt(int nowSecond, int allSecond) {
        setPopupText(nowSecond);
//        getPlayTime(nowSecond, allSecond);
        mTimeTxt.setText(getPlayTime(nowSecond, allSecond));
    }

    @Override
    public void forceLandscapeMode() {

    }

    @Override
    public void initTrimmedMode() {

    }


    public void setPopupText(int playSecond) {
        String playSecondStr =getResources().getString(R.string.pop_time_init);
        if (playSecond > 0) {
            playSecondStr = DateUtils.formatElapsedTime(playSecond / 1000);
        }
        mProgressSeekBar.getHintDelegate().setPopupText(playSecondStr);
    }

    public String getPlayTime(int playSecond, int allSecond) {
        String playSecondStr =  getResources().getString(R.string.play_time_init);
        String allSecondStr = getResources().getString(R.string.play_time_init);
        if (playSecond > 0) {
            playSecondStr = TimeUtils.secToTime(playSecond / 1000);
        }
        if (allSecond > 0) {
            allSecondStr = TimeUtils.secToTime(allSecond / 1000);
        }
        LogUtils.i(TAG, "playSecondStr = " + playSecondStr);
        LogUtils.i(TAG, "allSecondStr = " + allSecondStr);
        return playSecondStr + " / " + allSecondStr;
    }

    @Override
    public void onClick(View view) {
//        if (view.getId() == R.id.ib_pause_play) {
//            mMediaControl.onPlayTurn();
//        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mMediaControl.onProgressTurn(DOING, progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mMediaControl.onProgressTurn(START, 0);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mMediaControl.onProgressTurn(STOP, 0);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        hideControl();
//        mProgressSeekBar.getHintDelegate().onDetachedFromWindow();
        Glide.clear(mPauseAndPlay);
        mMediaControl = null;
    }


}
