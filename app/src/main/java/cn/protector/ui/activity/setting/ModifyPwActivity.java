
package cn.protector.ui.activity.setting;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.common.utils.CommonUtil;
import cn.protector.R;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.widget.ImageEditText;

/**
 * 描述：设备二维码
 *
 * @author jakechen
 */
public class ModifyPwActivity extends CommonTitleActivity implements View.OnClickListener, TextWatcher {
    private TextView mTvPhoneNum;
    private Button mBtnCode;
    private Button mBtnOk;
    private ImageEditText mEvCode;
    private ImageEditText mEvPw;
    private ImageEditText mEvPwAgain;
    private boolean isShowPw = false;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_modify_pw);
        setTitle(R.string.title_modify_pw);
        mTvPhoneNum = (TextView) findViewById(R.id.tv_phone_num);
        mBtnCode = (Button) findViewById(R.id.btn_code);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mEvCode = (ImageEditText) findViewById(R.id.ev_code);
        mEvPw = (ImageEditText) findViewById(R.id.ev_pw);
        mEvPwAgain = (ImageEditText) findViewById(R.id.ev_pw_again);
        isShowPw = getIntent().getBooleanExtra("isShowPw", false);
        if (isShowPw) {
            findViewById(R.id.ll_pw).setVisibility(View.VISIBLE);
            mBtnOk.setText(R.string.ok);
        } else {
            mBtnOk.setText(R.string.next_step);
            findViewById(R.id.ll_phone_num).setVisibility(View.VISIBLE);
        }
        // 设置点击页面其他地方隐藏软键盘
        setHideInputView(R.id.root);
        CommonUtil.showSoftInput(this);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        mBtnCode.setOnClickListener(this);
        mBtnOk.setOnClickListener(this);
        mEvPw.addTextChangedListener(this);
        mEvPwAgain.addTextChangedListener(this);
        mEvCode.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            if (isShowPw) {
                finish();
            } else {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isShowPw", true);
                goActivity(ModifyPwActivity.class, bundle);
                finish();
            }

        } else if (id == R.id.btn_code) {
//TODO
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isShowPw) {
            if (TextUtils.isEmpty(mEvPw.getText()) || TextUtils.isEmpty(mEvPwAgain.getText())) {
                mBtnOk.setEnabled(false);
            } else {
                mBtnOk.setEnabled(true);
            }
        } else {
            if (TextUtils.isEmpty(mEvCode.getText())) {
                mBtnOk.setEnabled(false);
            } else {
                mBtnOk.setEnabled(true);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
