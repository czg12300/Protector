package cn.protector.ui.activity.usercenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import java.util.List;

import cn.common.AppException;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.helper.HeartBeatHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.Response.CommonResponse;
import cn.protector.logic.http.Response.LoginResponse;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.activity.MainActivity;
import cn.protector.ui.helper.TipDialogHelper;
import cn.protector.ui.widget.ImageEditText;
import cn.protector.utils.ToastUtil;

/**
 * 注册页面
 */
public class RegisterActivity extends CommonTitleActivity implements View.OnClickListener, TextWatcher {
  private static final int MSG_BACK_REGISTER = 0;

  private static final int MSG_BACK_LOGIN = 1;

  private static final int MSG_BACK_SEND_MESSAGE_CODE = 2;

  private static final int MSG_UI_REGISTER = 0;

  private static final int MSG_UI_LOGIN = 1;

  private static final int MSG_UI_GO_ADD_DEVICE = 2;

  private static final int MSG_UI_GO_MAIN = 3;

  private static final int MSG_UI_SEND_MESSAGE_CODE = 4;

  private ImageEditText mEvMobile;

  private ImageEditText mEvPw;

  private ImageEditText mEvCode;

  private Button mBtnOk;

  private Button mBtnCode;

  private TipDialogHelper mTipDialogHelper;
  private boolean isCountDown = false;

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
    mEvCode.setInputType(InputType.TYPE_CLASS_NUMBER);
    mTipDialogHelper = new TipDialogHelper(this);
    mBtnOk.setEnabled(false);
    mBtnCode.setEnabled(false);
  }

  @Override
  protected void initEvent() {
    super.initEvent();
    mBtnOk.setOnClickListener(this);
    mEvMobile.addTextChangedListener(this);
    mEvPw.addTextChangedListener(this);
    mEvCode.addTextChangedListener(this);
    mBtnCode.setOnClickListener(this);
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
      mBtnOk.setEnabled(false);
      sendEmptyBackgroundMessage(MSG_BACK_REGISTER);
      mTipDialogHelper.showLoadingTip(R.string.register_ing);
    } else if (id == R.id.btn_code) {
      countDown();
      sendEmptyBackgroundMessage(MSG_BACK_SEND_MESSAGE_CODE);
    }
  }

  @Override
  public void handleBackgroundMessage(Message msg) {
    super.handleBackgroundMessage(msg);
    switch (msg.what) {
      case MSG_BACK_REGISTER:
        HttpRequest<CommonResponse> request = new HttpRequest<>(AppConfig.REGISTER, CommonResponse.class);
        if (mEvCode != null) {
          request.addParam("vc", mEvCode.getText().toString());
        }
        if (mEvMobile != null && mEvPw != null) {
          StringBuilder builder = new StringBuilder();
          builder.append("{\"LoginName\":\"");
          builder.append(mEvMobile.getText().toString());
          builder.append("\",\"Pwd\":\"");
          builder.append(mEvPw.getText().toString());
          builder.append("\"}");
          request.addParam("jsonInfo", builder.toString());
        }
        Message uiMsg = obtainUiMessage();
        uiMsg.what = MSG_UI_REGISTER;
        try {
          uiMsg.obj = request.request();
        } catch (AppException e) {
          e.printStackTrace();
        }
        uiMsg.sendToTarget();
        break;
      case MSG_BACK_LOGIN:
        HttpRequest<LoginResponse> rLogin = new HttpRequest<>(AppConfig.LOGIN, LoginResponse.class);
        if (mEvMobile != null) {
          rLogin.addParam("u", mEvMobile.getText().toString());
        }
        if (mEvPw != null) {
          rLogin.addParam("p", mEvPw.getText().toString());
          // request.addParam("p",
          // MD5Util.md5(mEvPw.getText().toString()));
        }
        Message msgLogin = obtainUiMessage();
        msgLogin.what = MSG_UI_LOGIN;
        try {
          msgLogin.obj = rLogin.request();
        } catch (AppException e) {
          e.printStackTrace();
        }
        msgLogin.sendToTarget();
        break;
      case MSG_BACK_SEND_MESSAGE_CODE:
        HttpRequest<CommonResponse> rCode = new HttpRequest<>(AppConfig.SEND_MESSAGE_CODE, CommonResponse.class);
        if (mEvMobile != null) {
          rCode.addParam("phone", mEvMobile.getText().toString());
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
      case MSG_UI_REGISTER:
        if (msg.obj != null) {
          CommonResponse response = (CommonResponse) msg.obj;
          if (response.getResult() == CommonResponse.SUCCESS) {
            sendEmptyBackgroundMessage(MSG_BACK_LOGIN);
          } else {
            mTipDialogHelper.hideDialog();
            ToastUtil.show(response.getInfo());
          }
        } else {
          ToastUtil.showError();
          mTipDialogHelper.hideDialog();
        }
        break;
      case MSG_UI_LOGIN:
        if (msg.obj != null) {
          LoginResponse response = (LoginResponse) msg.obj;
          mTipDialogHelper.hideDialog();
          ToastUtil.show(response.getInfo());
          if (response.getResult() == LoginResponse.SUCCESS) {
            if (InitSharedData.hasLogin()) {
              sendEmptyUiMessageDelayed(MSG_UI_GO_MAIN, 1000);
            } else {
              sendEmptyUiMessageDelayed(MSG_UI_GO_ADD_DEVICE, 1000);
            }
            // InitSharedData.setPassword(mEvPw.getText().toString());
            InitSharedData.setUserCode(response.getCode());
            InitSharedData.setUserId(response.getUserId());
            // InitSharedData.setMobile(mEvMobile.getText().toString());
            // 开始心跳包发送
            HeartBeatHelper.getInstance().start();
          }
        }
        break;
      case MSG_UI_GO_MAIN:
        goActivity(MainActivity.class);
        break;
      case MSG_UI_GO_ADD_DEVICE:
        goActivity(AddDeviceActivity.class);
        break;
      case MSG_UI_SEND_MESSAGE_CODE:
        if (msg.obj != null) {
          CommonResponse response = (CommonResponse) msg.obj;
          if (response.getResult() == CommonResponse.SUCCESS) {
            ToastUtil.show("短信已经成功发送到手机，请注意查收");
          } else {
            ToastUtil.show(response.getInfo());
          }
        }
        break;
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

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
    if (!TextUtils.isEmpty(mEvMobile.getText()) && !isCountDown) {
      mBtnCode.setEnabled(true);
    } else {
      mBtnCode.setEnabled(false);
    }
    if (!TextUtils.isEmpty(mEvMobile.getText()) && !TextUtils.isEmpty(mEvCode.getText()) && !TextUtils.isEmpty(mEvPw.getText())) {
      mBtnOk.setEnabled(true);
    } else {
      mBtnOk.setEnabled(false);
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
