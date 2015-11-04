package cn.protector.ui.activity.setting;

import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.Editable;
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
import cn.protector.logic.http.Response.CommonResponse;
import cn.protector.logic.http.Response.ModifyPwResponse;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.helper.TipDialogHelper;
import cn.protector.ui.widget.ImageEditText;
import cn.protector.utils.ToastUtil;

/**
 * 描述：修改密码
 *
 * @author jakechen
 */
public class ModifyPwActivity extends CommonTitleActivity implements View.OnClickListener, TextWatcher {
  private static final int MSG_BACK_MODIFY = 0;

  private static final int MSG_BACK_SEND_MESSAGE_CODE = 1;

  private static final int MSG_UI_MODIFY = 0;

  private static final int MSG_UI_SEND_MESSAGE_CODE = 1;

  private TextView mTvPhoneNum;

  private Button mBtnCode;

  private Button mBtnOk;

  private ImageEditText mEvCode;

  private ImageEditText mEvPw;

  private boolean isCountDown = false;
  private TipDialogHelper mTipDialogHelper;

  @Override
  protected void initView() {
    setContentView(R.layout.activity_modify_pw);
    setTitle(R.string.title_modify_pw);
    mTvPhoneNum = (TextView) findViewById(R.id.tv_phone_num);
    mBtnCode = (Button) findViewById(R.id.btn_code);
    mBtnOk = (Button) findViewById(R.id.btn_ok);
    mEvCode = (ImageEditText) findViewById(R.id.ev_code);
    mEvPw = (ImageEditText) findViewById(R.id.ev_pw);
    if (!TextUtils.isEmpty(InitSharedData.getMobile())) {
      mTvPhoneNum.setText(InitSharedData.getMobile());
    } else {
      finish();
    }
    // 设置点击页面其他地方隐藏软键盘
    setHideInputView(R.id.root);
    CommonUtil.showSoftInput(this);
    mEvCode.setHint(R.string.code_hint);
    mEvPw.setHint(R.string.new_pw_hint);
    mTipDialogHelper = new TipDialogHelper(this);
  }

  @Override
  protected void initEvent() {
    super.initEvent();
    mBtnCode.setOnClickListener(this);
    mBtnOk.setOnClickListener(this);
    mEvPw.addTextChangedListener(this);
    mEvCode.addTextChangedListener(this);
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
    int id = v.getId();
    if (id == R.id.btn_ok) {
      sendEmptyBackgroundMessage(MSG_BACK_MODIFY);
      mTipDialogHelper.showLoadingTip("正在重置");
    } else if (id == R.id.btn_code) {
      sendEmptyBackgroundMessage(MSG_BACK_SEND_MESSAGE_CODE);
      countDown();
    }
  }

  @Override
  public void handleBackgroundMessage(Message msg) {
    super.handleBackgroundMessage(msg);
    switch (msg.what) {
      case MSG_BACK_MODIFY:
        HttpRequest<ModifyPwResponse> request = new HttpRequest<>(AppConfig.MODIFY_PASSWORD, ModifyPwResponse.class);
        if (mEvCode != null) {
          request.addParam("vc", mEvCode.getText().toString());
        }
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
      case MSG_BACK_SEND_MESSAGE_CODE:
        HttpRequest<CommonResponse> rCode = new HttpRequest<>(AppConfig.SEND_MESSAGE_CODE, CommonResponse.class);
        if (mTvPhoneNum != null) {
          rCode.addParam("phone", mTvPhoneNum.getText().toString());
        }
        Message rMsg = obtainUiMessage();
        rMsg.what = MSG_UI_SEND_MESSAGE_CODE;
        try {
          rMsg.obj = rCode.request();
        } catch (AppException e) {
          e.printStackTrace();
        }
        rMsg.sendToTarget();
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
          ModifyPwResponse response = (ModifyPwResponse) msg.obj;
          if (response.getResult() == ModifyPwResponse.SUCCESS) {
            finish();
          } else {
            ToastUtil.show(response.getInfo());
          }
        } else {
          ToastUtil.showError();
        }
        break;
      case MSG_UI_SEND_MESSAGE_CODE:
        if (msg.obj != null) {
          CommonResponse response = (CommonResponse) msg.obj;
          if (response.getResult() == CommonResponse.SUCCESS) {
            ToastUtil.show(R.string.mobile_code_success_hint);
          } else {
            ToastUtil.show(response.getInfo());
          }
        }
        break;
    }
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
    if (TextUtils.isEmpty(mEvPw.getText()) || TextUtils.isEmpty(mEvCode.getText())) {
      mBtnOk.setEnabled(false);
    } else {
      mBtnOk.setEnabled(true);
    }
  }

  @Override
  public void afterTextChanged(Editable s) {
  }

  // 显示倒计时
  private void countDown() {
    isCountDown = true;
    new CountDownTimer(60 * 1000L, 1000L) {

      @Override
      public void onFinish() {
        mBtnCode.setText(R.string.code_btn);
        mBtnCode.setEnabled(true);
        isCountDown = false;
      }

      @Override
      public void onTick(long millisUntilFinished) {
        mBtnCode.setText("剩余" + millisUntilFinished / 1000 + "秒");
        mBtnCode.setEnabled(false);
      }
    }.start();
  }
}
