
package cn.protector.ui.activity;

import android.graphics.Paint;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.protector.R;
import cn.protector.ui.widget.ImageEditText;

/**
 * 登录页面
 */
public class LoginActivity extends CommonTitleActivity
        implements TextWatcher, View.OnClickListener {
    private ImageEditText mEvMobile;

    private ImageEditText mEvPw;

    private Button mBtnOk;

    private TextView mTvRegister;

    private TextView mTvForgetPw;

    @Override
    protected void initView() {
        setTitle(R.string.login_title);
        mIvBack.setVisibility(View.GONE);
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_login);
        mEvMobile = (ImageEditText) findViewById(R.id.ev_mobile);
        mEvPw = (ImageEditText) findViewById(R.id.ev_pw);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mTvRegister = (TextView) findViewById(R.id.tv_register);
        mTvForgetPw = (TextView) findViewById(R.id.tv_forget);
        mTvRegister.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);// 下划线
        mTvForgetPw.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);// 下划线
        mEvMobile.setLeftDrawable(R.drawable.img_selector_account_left_select);
        mEvPw.setLeftDrawable(R.drawable.img_selector_pw_left_select);
        mEvMobile.setInputType(InputType.TYPE_CLASS_PHONE);
        mEvPw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mEvMobile.setHint(R.string.mobile_hint);
        mEvPw.setHint(R.string.pw_hint);
        mEvPw.addTextChangedListener(this);
        mEvMobile.addTextChangedListener(this);
        mBtnOk.setEnabled(false);
        mBtnOk.setOnClickListener(this);
        mTvRegister.setOnClickListener(this);
        mTvForgetPw.setOnClickListener(this);
        // 设置点击页面其他地方隐藏软键盘
        setHideInputView(R.id.root);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_ok) {
            showLoadingTip( R.string.login_ing);
            goActivity(AddDeviceActivity.class);
        } else if (v.getId() == R.id.tv_register) {
            goActivity(RegisterActivity.class);
        } else if (v.getId() == R.id.tv_forget) {
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(mEvPw.getText()) || TextUtils.isEmpty(mEvMobile.getText())) {
            mBtnOk.setEnabled(false);
        } else {
            mBtnOk.setEnabled(true);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
