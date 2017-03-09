package com.ppfuns.util;

import android.os.Handler;
import android.os.Message;

/**
 * Created by hmc on 2016/9/1.
 */
public abstract class AdCountTimer {

    private final long mCountdownInterval;

    private long mTotalTime;

    private long mRemainTime;

    private boolean isStart = false;
    private boolean isPause = false;


    public AdCountTimer(long millisInFuture, long countDownInterval) {
        mTotalTime = millisInFuture;
        mCountdownInterval = countDownInterval;

        mRemainTime = millisInFuture;
    }

    public final void seek(int value) {
        synchronized (AdCountTimer.this) {
            mRemainTime = ((100 - value) * mTotalTime) / 100;
        }
    }

    public final void cancel() {
        isStart = false;
        mHandler.removeMessages(MSG_RUN);
        mHandler.removeMessages(MSG_PAUSE);
    }

    public final void resume() {
        LogUtils.i("Decode", "timecounter_resume");
        if (!isPause) {
            return;
        }
        isPause = false;
        mHandler.removeMessages(MSG_PAUSE);
        mHandler.sendMessageAtFrontOfQueue(mHandler.obtainMessage(MSG_RUN));
    }

    public final void pause() {
        LogUtils.i("Decode", "timecounter_pause");
        if (isPause) {
            return;
        }
        isPause = true;
        mHandler.removeMessages(MSG_RUN);
        mHandler.sendMessageAtFrontOfQueue(mHandler.obtainMessage(MSG_PAUSE));
    }

    public synchronized final AdCountTimer start() {
        if (mRemainTime <= 0) {
            onFinish();
            return this;
        }
        if (isStart) {
            resume();
            return this;
        }
        isStart = true;
        mRemainTime = mRemainTime + mCountdownInterval;
        mHandler.sendMessage(mHandler.obtainMessage(MSG_RUN));
        return this;
    }

    public abstract void onTick(long millisUntilFinished, int percent);

    public abstract void onFinish();

    public void onRelease() {
		isStart = false;
        isPause = false;
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    private static final int MSG_RUN = 1;
    private static final int MSG_PAUSE = 2;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            synchronized (AdCountTimer.this) {
                if (msg.what == MSG_RUN) {
                    mRemainTime = mRemainTime - mCountdownInterval;

                    if (mRemainTime <= 0) {
                        onFinish();
                    } else if (mRemainTime < mCountdownInterval) {
                        sendMessageDelayed(obtainMessage(MSG_RUN), mRemainTime);
                    } else {
                        onTick(mRemainTime, new Long(100
                                * (mTotalTime - mRemainTime) / mTotalTime).intValue());
                        sendMessageDelayed(obtainMessage(MSG_RUN), mCountdownInterval);
                    }
                } else if (msg.what == MSG_PAUSE) {

                }
            }
        }
    };
}
