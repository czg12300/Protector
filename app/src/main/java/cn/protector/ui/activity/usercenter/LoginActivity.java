
package cn.protector.ui.activity.usercenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import cn.common.AppException;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.helper.HeartBeatHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.Response.LoginResponse;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.activity.MainActivity;
import cn.protector.ui.helper.TipDialogHelper;
import cn.protector.ui.widget.ImageEditText;
import cn.protector.utils.ToastUtil;

/**
 * 登录页面
 */
public class LoginActivity extends CommonTitleActivity
        implements TextWatcher, View.OnClickListener {
    private static final int MSG_BACK_LOGIN = 0;

    private static final int MSG_UI_LOGIN = 0;

    private static final int MSG_UI_GO_ADD_DEVICE = 1;

    private static final int MSG_UI_GO_MAIN = 2;

    private ImageEditText mEvMobile;

    private ImageEditText mEvPw;

    private Button mBtnOk;

    private TextView mTvRegister;

    private TextView mTvForgetPw;

    private TipDialogHelper mTipDialogHelper;

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
        // 设置点击页面其他地方隐藏软键盘
        setHideInputView(R.id.root);
        mTipDialogHelper = new TipDialogHelper(this);
    }

    @Override
    protected void initEvent() {
        mEvPw.addTextChangedListener(this);
        mEvMobile.addTextChangedListener(this);
        mBtnOk.setEnabled(false);
        mBtnOk.setOnClickListener(this);
        mTvRegister.setOnClickListener(this);
        mTvForgetPw.setOnClickListener(this);
        mTipDialogHelper.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mBtnOk != null) {
                    mBtnOk.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_ok) {
            login();
        } else if (v.getId() == R.id.tv_register) {
            goActivity(RegisterActivity.class);
        } else if (v.getId() == R.id.tv_forget) {
        }
    }

    /**
     * 执行登录操作
     */
    private void login() {
        mBtnOk.setEnabled(false);
        mTipDialogHelper.showLoadingTip(R.string.login_ing);
        sendEmptyBackgroundMessage(MSG_BACK_LOGIN);
    }

    @Override
    public void setupBroadcastActions(List<String> actions) {
        super.setupBroadcastActions(actions);
        actions.add(BroadcastActions.ACTION_FINISH_ACTIVITY_BEFORE_MAIN);
        actions.add(BroadcastActions.ACTION_REGISTER_SUCCESS);
    }

    @Override
    public void handleBroadcast(Context context, Intent intent) {
        super.handleBroadcast(context, intent);
        String action = intent.getAction();
        if (TextUtils.equals(action, BroadcastActions.ACTION_FINISH_ACTIVITY_BEFORE_MAIN)) {
            finish();
        } else if (TextUtils.equals(action, BroadcastActions.ACTION_REGISTER_SUCCESS)) {
            String mobile = intent.getStringExtra("mobile");
            String password = intent.getStringExtra("password");
            if (!TextUtils.isEmpty(mobile) && mEvMobile != null) {
                mEvMobile.setText(mobile);
                mEvMobile.setSelection(mEvMobile.getText().length());
            }
            if (!TextUtils.isEmpty(password) && mEvPw != null) {
                mEvPw.setText(password);
                mEvPw.setSelection(mEvPw.getText().length());
            }
            if (mBtnOk.isEnabled()) {
                login();
            }
        }
    }

    @Override
    public void handleUiMessage(Message msg) {
        super.handleUiMessage(msg);
        switch (msg.what) {
            case MSG_UI_LOGIN:
                if (msg.obj != null) {
                    LoginResponse response = (LoginResponse) msg.obj;
                    mTipDialogHelper.hideDialog();
                    ToastUtil.show(response.getInfo());
                    if (response.getResult() == LoginResponse.SUCCESS) {
                        if (InitSharedData.hasLogin()) {
                            sendEmptyUiMessage(MSG_UI_GO_MAIN);
                        } else {
                            sendEmptyUiMessage(MSG_UI_GO_ADD_DEVICE);
                        }
                        // InitSharedData.setPassword(mEvPw.getText().toString());
                        InitSharedData.setUserCode(response.getCode());
                        InitSharedData.setUserId(response.getUserId());
                        // InitSharedData.setMobile(mEvMobile.getText().toString());
                        // 开始心跳包发送
                        HeartBeatHelper.getInstance().start();
                    }
                } else {
                    mTipDialogHelper.hideDialog();
                    ToastUtil.showError();
                }
                break;
            case MSG_UI_GO_MAIN:
                goActivity(MainActivity.class);
                break;
            case MSG_UI_GO_ADD_DEVICE:
                goActivity(AddDeviceActivity.class);
                break;
        }
    }

    @Override
    public void handleBackgroundMessage(Message msg) {
        super.handleBackgroundMessage(msg);
        switch (msg.what) {
            case MSG_BACK_LOGIN:
                HttpRequest<LoginResponse> request = new HttpRequest<>(AppConfig.LOGIN,
                        LoginResponse.class);
                if (mEvMobile != null) {
                    request.addParam("u", mEvMobile.getText().toString());
                }
                if (mEvPw != null) {
                    request.addParam("p", mEvPw.getText().toString());
                    // request.addParam("p",
                    // MD5Util.md5(mEvPw.getText().toString()));
                }
                Message uiMsg = obtainUiMessage();
                uiMsg.what = MSG_UI_LOGIN;
                try {
                    uiMsg.obj = request.request();
                } catch (AppException e) {
                    e.printStackTrace();
                }
                uiMsg.sendToTarget();
                break;
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
