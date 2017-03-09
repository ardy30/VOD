package com.ppfuns.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ppfuns.vod.R;

/**
 * Created by hmcxunxi on 2016/7/19.
 */
public class TipsDialog extends Dialog {

    protected TipsDialog(Context context) {
        super(context);
    }

    public TipsDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static boolean isshowAlbum;

    private boolean mDisableBackKey = true;

    public void setDisableBackKey(boolean disableBackKey) {
        mDisableBackKey = disableBackKey;
    }

    public static class Builder {
        private Context mContext;

        private TextView mMessage;
        private Button mBtnRetry;
        private Button mBtnCancel;
        private Button mBtnConfirm;

        public static final int CONFIRM_BUTTON_FLAG = 0;
        public static final int RETRY_BUTTON_FLAG = 1;
        public static final int CANCEL_BUTTON_FLAG = 2;

        private String mMessageText = null;
        private String mRetryText = null;
        private String mConfirmText = null;
        private String mCancelText = null;
        private String mOnlyMessageText = null;

        private View.OnClickListener mConfirmListener;
        private View.OnClickListener mRetryListener;
        private View.OnClickListener mCanacelListener;



        public Builder(Context context) {
            mContext = context;
        }

        public TipsDialog create() {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final TipsDialog dialog = new TipsDialog(mContext, R.style.DialogStyle);
            View layout = inflater.inflate(R.layout.tips_dialog, null);
            mMessage = (TextView) layout.findViewById(R.id.tv_message);
            if (isshowAlbum){

                mMessage.setGravity(Gravity.CENTER_VERTICAL);
            }
            mBtnRetry = (Button) layout.findViewById(R.id.btn_retry);
            mBtnCancel = (Button) layout.findViewById(R.id.btn_cancel);
            mBtnConfirm = (Button) layout.findViewById(R.id.btn_confirm);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            if (!TextUtils.isEmpty(mOnlyMessageText)) {
                mMessage.setText(mOnlyMessageText);
                mMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, 26);
                mMessage.setPadding(10, 10, 10, 10);
                dialog.setOnKeyListener(new OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                                if (mOnlyMessageText != null) {
                                    mOnlyMessageText = null;
                                    dialog.dismiss();
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                });
                return dialog;
            }
            if (!TextUtils.isEmpty(mMessageText)) {
                mMessage.setText(mMessageText);
            }
            if (TextUtils.isEmpty(mConfirmText) && TextUtils.isEmpty(mRetryText) && TextUtils.isEmpty(mCancelText)) {
                //builder不设置button默认显示重试和取消按钮
                mBtnRetry.setVisibility(View.VISIBLE);
                mCanacelListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                };
                mBtnCancel.setVisibility(View.VISIBLE);
            } else {
                if (!TextUtils.isEmpty(mConfirmText)) {
                    mBtnConfirm.setText(mConfirmText);
                    if (mConfirmListener != null) {
                        mBtnConfirm.setOnClickListener(mConfirmListener);
                    }
                    mBtnConfirm.setVisibility(View.VISIBLE);
                }
                if (!TextUtils.isEmpty(mRetryText)) {
                    if (mRetryListener != null) {
                        mBtnRetry.setOnClickListener(mRetryListener);
                    }
                    mBtnRetry.setText(mRetryText);
                    mBtnRetry.setVisibility(View.VISIBLE);
                }
                if (!TextUtils.isEmpty(mCancelText)) {
                    if (mCanacelListener != null) {
                        mBtnCancel.setOnClickListener(mCanacelListener);
                    }
                    mBtnCancel.setText(mCancelText);
                    mBtnCancel.setVisibility(View.VISIBLE);
                }
            }
            return dialog;
        }


        public Builder setMessage(String message) {
            mMessageText = message;

            return this;
        }

        public Builder setOnlyMessage(String onlyMessage) {
            mOnlyMessageText = onlyMessage;

            return this;
        }

        public Builder setConfirmButton(String text, View.OnClickListener listener) {
            setButton(CONFIRM_BUTTON_FLAG, text, listener);
            return this;
        }

        public Builder setRetryButton(String text, View.OnClickListener listener) {
            setButton(RETRY_BUTTON_FLAG, text, listener);
            return this;
        }

        public Builder setCancelButton(String text, View.OnClickListener listener) {
            setButton(CANCEL_BUTTON_FLAG, text, listener);
            return this;
        }

        public Builder setButton(int whichButton, String text, View.OnClickListener listener) {
            switch (whichButton) {
                case CONFIRM_BUTTON_FLAG:
                    mConfirmText = text;
                    mConfirmListener = listener;
                    break;
                case RETRY_BUTTON_FLAG:
                    mRetryText = text;
                    mRetryListener = listener;
                    break;
                case CANCEL_BUTTON_FLAG:
                    mCancelText = text;
                    mCanacelListener = listener;
                    break;
            }
            return this;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN)
            if (keyCode == KeyEvent.KEYCODE_BACK && mDisableBackKey) return true;
        return super.onKeyDown(keyCode, event);
    }
}
