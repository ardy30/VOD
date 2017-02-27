package com.ppfuns.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ppfuns.ui.view.keyborad.SkbContainer;
import com.ppfuns.ui.view.keyborad.SoftKey;
import com.ppfuns.ui.view.keyborad.SoftKeyBoardListener;
import com.ppfuns.util.LogUtils;
import com.ppfuns.util.eventbus.EventBusUtil;
import com.ppfuns.util.eventbus.EventConf;
import com.ppfuns.util.eventbus.InfoEvent;
import com.ppfuns.vod.R;
import com.ppfuns.vod.activity.SearchActivity;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zpf on 2016/6/29.
 */
public class KeyBoradFragment extends Fragment {

    public SearchActivity mActivity;

    private static final String TAG = "KeyBoradFragment";

    SkbContainer mSkbContainer;
    SoftKey      mOldSoftKey;


    @BindView(R.id.edit_query)
    EditText mEditText;


    @BindView(R.id.search_edit_bg)
    ImageView mEditBg;
    @BindView(R.id.iv_keyboard1)
    ImageView mKeyboard1;
    @BindView(R.id.search_keybroad2)
    ImageView mKeyboard2;

    @BindView(R.id.iv_number)
    ImageView mNumber;
    @BindView(R.id.iv_delete)
    ImageView mDelete;

    @BindView(R.id.iv_back)
    ImageView mBack;

    private boolean isNumber;//是否是数字键盘
    private static final int TYPE_NUMBER = R.xml.sbd_number;
    private static final int TYPE_CHAR   = R.xml.sbd_char;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (SearchActivity) context;
    }

    private SearchVoiceReceiver mReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBusUtil.register(this);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
        mActivity.unregisterReceiver(mReceiver);
    }

    private class SearchVoiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("text");
            if (!TextUtils.isEmpty(text))
                LogUtils.d(TAG, text);
            mEditText.setText(text);
        }
    }


    @Subscribe
    public void Voice(InfoEvent infoEvent) {
        LogUtils.d(TAG, "keyboard can call");
        if (infoEvent.id == EventConf.SEND_MESSAGE_BY_VOICE) {
            LogUtils.d(TAG, "infoEvent.id is SEND_MESSAGE_BY_VOICE");
            if (infoEvent.obj instanceof String) {
                LogUtils.d(TAG, "infoEvent.obj instanceof String");
                mEditText.setText((String) infoEvent.obj);
                mSkbContainer.requestFocus();
            }

        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_keyborad, null);
        ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    private ImageView mIvSearchIcon;

    private void initView(View view) {
        mSkbContainer = (SkbContainer) view.findViewById(R.id.skbContainer);
        mIvSearchIcon = (ImageView) view.findViewById(R.id.search_edit_icon);

        Glide.with(this).load(R.drawable.keyboard1).into(mKeyboard1);
        Glide.with(this).load(R.mipmap.keyboard2).into(mKeyboard2);
        Glide.with(this).load(R.drawable.input_box).into(mEditBg);

        Glide.with(this).load(R.drawable.search_icon).into(mIvSearchIcon);

        Glide.with(this).load(R.drawable.number).into(mNumber);
        Glide.with(this).load(R.drawable.back).into(mBack);
        Glide.with(this).load(R.drawable.delete).into(mDelete);
        //控制焦点
        mSkbContainer.setNextFocusDownId(R.id.skbContainer);
        mSkbContainer.setNextFocusUpId(R.id.iv_number);
        mNumber.setNextFocusUpId(R.id.iv_number);
        mNumber.setNextFocusLeftId(R.id.iv_number);
        mActivity.ivBack = mBack;
        mDelete.setNextFocusUpId(R.id.iv_delete);
        mBack.setNextFocusUpId(R.id.iv_back);

        // 设置键盘属性
        setSkbContainer(TYPE_CHAR);
    }

    /**
     * 设置键盘属性的方法
     */
    private void setSkbContainer(int keyboardType) {
        mOldSoftKey = null;
        mSkbContainer.setSkbLayout(keyboardType);

        mSkbContainer.setMoveSoftKey(true);
        mSkbContainer.setSelectSofkKeyFront(true);

        // 设置移动边框相差的间距.
        if (keyboardType == TYPE_CHAR) {
            mSkbContainer.setSoftKeySelectPadding((int) getResources().getDimension(R.dimen.px70));
        } else if (keyboardType == TYPE_NUMBER) {
            mSkbContainer.setSoftKeySelectPadding((int) getResources().getDimension(R.dimen.px60));
        }

        mSkbContainer.setMoveDuration(150);
        mSkbContainer.requestFocus();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.ppfuns.vod.search");
        filter.setPriority(Integer.MAX_VALUE);
        mReceiver = new SearchVoiceReceiver();
        mActivity.registerReceiver(mReceiver, filter);

        initEvent();
    }

    /**
     * 切换布局测试.
     * 因为布局不相同，所以属性不一样，
     * 需要重新设置
     */
    private void initEvent() {

        //切换键盘
        mNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNumber = !isNumber;
                if (isNumber) {
                    Glide.with(KeyBoradFragment.this).load(R.drawable.abc).into(mNumber);
                    setSkbContainer(TYPE_NUMBER);
                } else {
                    Glide.with(KeyBoradFragment.this).load(R.drawable.number).into(mNumber);
                    setSkbContainer(TYPE_CHAR);
                }
            }
        });

        //删除单个字符
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取Edittext光标所在位置
                String str = mEditText.getText().toString();

                //判断输入框不为空，执行删除
                if (!TextUtils.isEmpty(str)) {
                    mEditText.getText().delete(str.length() - 1, str.length());
                }
            }
        });

        //清空字符
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取Edittext光标所在位置
                String str = mEditText.getText().toString();

                //判断输入框不为空，执行清空
                if (!TextUtils.isEmpty(str)) {
                    mEditText.getText().delete(0, str.length());
                }
            }
        });

        //字符变化的监听
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!TextUtils.isEmpty(s)) {
                    EventBusUtil.postInfoEvent(EventConf.SEND_MESSAGE_BY_KEYBOARD, s.toString());
                } else {
                    EventBusUtil.postInfoEvent(EventConf.SEND_MESSAGE_BY_KEYBOARD, "");
                }
            }
        });

        mSkbContainer.setOnSoftKeyBoardListener(new SoftKeyBoardListener() {
            @Override
            public void onCommitText(SoftKey softKey) {

                mEditText.append((char) softKey.getKeyCode() + "");
            }

            @Override
            public void onBack(SoftKey key) {
                mActivity.finish();
            }

            @Override
            public void onDelete(SoftKey key) {
            }

        });
        // 键盘的焦点控制
        mSkbContainer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (mOldSoftKey != null) {
                        mSkbContainer.setKeySelected(mOldSoftKey);
                    } else {
                        mSkbContainer.setDefualtSelectKey(0, 0);
                    }
                } else {
                    mOldSoftKey = mSkbContainer.getSelectKey();
                    mSkbContainer.setKeySelected(null);
                }
            }
        });
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mSkbContainer.onSoftKeyDown(keyCode, event)||keyCode==KeyEvent.KEYCODE_SPACE)
            return true;
        return false;
    }


    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mSkbContainer.onSoftKeyUp(keyCode, event)||keyCode==KeyEvent.KEYCODE_SPACE)
            return true;
        return false;

    }


    /**
     * 处理T9键盘的按键.
     *
     * @param softKey
     */
/*    private void onCommitT9Text(SoftKey softKey) {

    }*/

}
