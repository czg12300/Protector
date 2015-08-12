
package cn.protector.ui;

import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.protector.R;
import cn.protector.ui.widget.ImageEditText;

/**
 * 登录页面
 */
public class RegisterActivity extends CommonTitleActivity implements View.OnClickListener {
    private ImageEditText mEvMobile;

    private ImageEditText mEvPw;

    private EditText mEvCode;

    private Button mBtnOk;

    private Button mBtnCode;

    @Override
    protected void initView() {
        setTitle(R.string.register_title);
        setContentView(R.layout.activity_register);
        mEvMobile = (ImageEditText) findViewById(R.id.ev_mobile);
        mEvPw = (ImageEditText) findViewById(R.id.ev_pw);
        mEvCode = (EditText) findViewById(R.id.ev_code);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnCode = (Button) findViewById(R.id.btn_code);

        mEvMobile.setLeftDrawable(R.drawable.img_selector_account_left_select);
        mEvPw.setLeftDrawable(R.drawable.img_selector_pw_left_select);
        mEvMobile.setInputType(InputType.TYPE_CLASS_PHONE);
        mEvPw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mEvMobile.setHint(R.string.mobile_hint);
        mEvPw.setHint(R.string.set_pw_hint);
        mBtnOk.setOnClickListener(this);
        mBtnCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_ok) {
            showTipDialog(R.drawable.ico_spinner_white, R.string.login_ing);
        } else if (v.getId() == R.id.tv_register) {
        } else if (v.getId() == R.id.tv_forget) {
        }
    }
}
