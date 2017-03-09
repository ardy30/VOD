package com.ppfuns.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ppfuns.vod.R;

/**
 * Created by zpf on 2016/9/20.
 */
public class FreeSeeDialog extends Dialog {

    public FreeSeeDialog(Context context) {
        super(context);
    }

    public FreeSeeDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context mContext;

        private Button mBtnCancel;
        private Button mBtnConfirm;

        public static final int CONFIRM_BUTTON_FLAG = 0;
        public static final int RETRY_BUTTON_FLAG = 1;
        public static final int CANCEL_BUTTON_FLAG = 2;

        private View.OnClickListener mConfirmListener;
        private View.OnClickListener mRetryListener;
        private View.OnClickListener mCanacelListener;

        public Builder(Context context) {
            mContext = context;
        }

        public FreeSeeDialog create() {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final FreeSeeDialog dialog = new FreeSeeDialog(mContext, R.style.DialogStyle);
            View layout = inflater.inflate(R.layout.free_see_dialog, null);
            mBtnCancel = (Button) layout.findViewById(R.id.btn_cancel);
            mBtnConfirm = (Button) layout.findViewById(R.id.btn_confirm);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            if (mConfirmListener != null) {
                mBtnConfirm.setOnClickListener(mConfirmListener);
            }
            if (mCanacelListener != null) {
                mBtnCancel.setOnClickListener(mCanacelListener);
            }
            return dialog;
        }

        public Builder setConfirmButton(View.OnClickListener listener) {
            setButton(CONFIRM_BUTTON_FLAG, listener);
            return this;
        }

        public Builder setCancelButton(View.OnClickListener listener) {
            setButton(CANCEL_BUTTON_FLAG, listener);
            return this;
        }

        public Builder setButton(int whichButton, View.OnClickListener listener) {
            switch (whichButton) {
                case CONFIRM_BUTTON_FLAG:
                    mConfirmListener = listener;
                    break;
                case CANCEL_BUTTON_FLAG:
                    mCanacelListener = listener;
                    break;
            }
            return this;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN)
            if (keyCode == KeyEvent.KEYCODE_BACK) return true;
        return super.onKeyDown(keyCode, event);
    }
}
