
package cn.protector.ui.activity.setting;

import android.content.DialogInterface;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.common.AppException;
import cn.common.utils.CommonUtil;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.Response.ResetPwResponse;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.helper.TipDialogHelper;
import cn.protector.ui.widget.ImageEditText;
import cn.protector.utils.ToastUtil;

/**
 * 描述：修改密码
 *
 * @author jakechen
 */
public class ModifyPwActivity extends CommonTitleActivity implements TextWatcher {

    private static final int MSG_BACK_MODIFY = 0;

    private static final int MSG_UI_MODIFY = 0;

    private TextView mTvPhoneNum;

    private Button mBtnOk;

    private ImageEditText mEvPw;

    private TipDialogHelper mTipDialogHelper;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_modify_pw);
        setTitle(R.string.title_modify_pw);
        mTvPhoneNum = (TextView) findViewById(R.id.tv_phone_num);
        if (!TextUtils.isEmpty(InitSharedData.getMobile())) {
            mTvPhoneNum.setText(InitSharedData.getMobile());
        } else {
            finish();
        }
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mEvPw = (ImageEditText) findViewById(R.id.ev_pw);
        // 设置点击页面其他地方隐藏软键盘
        setHideInputView(R.id.root);
        CommonUtil.showSoftInput(this);
        mEvPw.setHint(R.string.new_pw_hint);
        mEvPw.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        mTipDialogHelper = new TipDialogHelper(this);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmptyBackgroundMessage(MSG_BACK_MODIFY);
                mTipDialogHelper.showLoadingTip("正在重置");
            }
        });
        mEvPw.addTextChangedListener(this);
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
    public void handleBackgroundMessage(Message msg) {
        super.handleBackgroundMessage(msg);
        switch (msg.what) {
            case MSG_BACK_MODIFY:
                HttpRequest<ResetPwResponse> request = new HttpRequest<>(AppConfig.MODIFY_PASSWORD,
                        ResetPwResponse.class);
                request.addParam("uc", InitSharedData.getUserCode());
                if (mTvPhoneNum != null && mEvPw != null) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("{\"LoginName\":\"");
                    builder.append(mTvPhoneNum.getText().toString());
                    builder.append("\",\"NewPwd\":\"");
                    builder.append(mEvPw.getText().toString());
                    builder.append("\"}");
                    request.addParam("jsonInfo", builder.toString());
                }
                Message uiMsg = obtainUiMessage();
                uiMsg.what = MSG_UI_MODIFY;
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
    public void handleUiMessage(Message msg) {
        super.handleUiMessage(msg);
        switch (msg.what) {
            case MSG_UI_MODIFY:
                if (mTipDialogHelper != null) {
                    mTipDialogHelper.hideDialog();
                }
                if (msg.obj != null) {
                    ResetPwResponse response = (ResetPwResponse) msg.obj;
                    ToastUtil.show(response.getInfo());
                    if (response.getResult() == ResetPwResponse.SUCCESS) {
                        finish();
                    }
                } else {
                    ToastUtil.showError();
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(mEvPw.getText())) {
            mBtnOk.setEnabled(false);
        } else {
            mBtnOk.setEnabled(true);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
