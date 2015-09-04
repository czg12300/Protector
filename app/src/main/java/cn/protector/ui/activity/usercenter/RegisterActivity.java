
package cn.protector.ui.activity.usercenter;

import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import cn.protector.R;
import cn.protector.data.BroadcastActions;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.widget.ImageEditText;

import java.util.List;

/**
 * 登录页面
 */
public class RegisterActivity extends CommonTitleActivity {
    private ImageEditText mEvMobile;

    private ImageEditText mEvPw;

    private ImageEditText mEvCode;

    private Button mBtnOk;

    private Button mBtnCode;

    @Override
    protected void initView() {
        setTitle(R.string.title_register);
        setContentView(R.layout.activity_register);
        mEvMobile = (ImageEditText) findViewById(R.id.ev_mobile);
        mEvPw = (ImageEditText) findViewById(R.id.ev_pw);
        mEvCode = (ImageEditText) findViewById(R.id.ev_code);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnCode = (Button) findViewById(R.id.btn_code);

        mEvMobile.setLeftDrawable(R.drawable.img_selector_account_left_select);
        mEvPw.setLeftDrawable(R.drawable.img_selector_pw_left_select);
        mEvMobile.setInputType(InputType.TYPE_CLASS_PHONE);
        mEvPw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mEvCode.setInputType(InputType.TYPE_CLASS_TEXT);
        mEvMobile.setHint(R.string.mobile_hint);
        mEvPw.setHint(R.string.set_pw_hint);
        mEvCode.setHint(R.string.code_hint);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btn_ok) {
            showLoadingTip(R.string.login_ing);
        } else if (v.getId() == R.id.tv_register) {
        } else if (v.getId() == R.id.tv_forget) {
        }
    }

    @Override
    public void setupBroadcastActions(List<String> actions) {
        super.setupBroadcastActions(actions);
        actions.add(BroadcastActions.ACTION_FINISH_ACITIVTY_BEFORE_MAIN);
    }

    @Override
    public void handleBroadcast(Context context, Intent intent) {
        super.handleBroadcast(context, intent);
        String action = intent.getAction();
        if (TextUtils.equals(action, BroadcastActions.ACTION_FINISH_ACITIVTY_BEFORE_MAIN)) {
            finish();
        }
    }
}
