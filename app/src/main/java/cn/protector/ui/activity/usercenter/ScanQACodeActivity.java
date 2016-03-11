package cn.protector.ui.activity.usercenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.google.zxing.ResultPoint;
import com.helper.CaptureHelper;

import java.util.List;

import cn.common.AppException;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.CommonResponse;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.helper.TipDialogHelper;
import cn.protector.utils.ToastUtil;

/**
 * 描述： 扫描二维码页面
 *
 * @author jakechen
 */
public class ScanQACodeActivity extends CommonTitleActivity {
  private static final int MSG_UI_CHECK_SUCCESS = 0;
  private static final int MSG_UI_CHECK_FAIL = 1;
  private static final int MSG_BACK_CHECK = 0;
  // private static final int MSG_UI_BIND_SUCCESS = MSG_UI_START + 1;

  private ImageView mIvScanLine;

  private ImageView mIvShadowScanFrame;

  private SurfaceView mSvCamera;

  private CaptureHelper mCaptureHelper;

  private View vScanFrame;
  private TipDialogHelper mTipDialogHelper;
  private String mEid;

  @Override
  protected void initView() {
    setContentView(R.layout.activity_scan_qa_code);
    setTitle(R.string.title_scan_qa_code);
    mIvScanLine = (ImageView) findViewById(R.id.iv_scan_line);
    mIvShadowScanFrame = (ImageView) findViewById(R.id.iv_shadow_frame_scan);
    mSvCamera = (SurfaceView) findViewById(R.id.sv_camera);
    vScanFrame = findViewById(R.id.rl_frame_scan);
    mCaptureHelper = new CaptureHelper(mSvCamera, this);
    mTipDialogHelper = new TipDialogHelper(this);
    setSwipeBackEnable(false);
  }

  @Override
  protected void initEvent() {
    mCaptureHelper.setCaptureListener(new CaptureHelper.CaptureListener() {
      @Override
      public void handleCodeResult(String code, Bitmap bitmap) {
        mTipDialogHelper.showLoadingTip("正在检测扫描的结果");
        mEid = code;
        sendEmptyBackgroundMessage(MSG_BACK_CHECK);
      }

      @Override
      public void foundPossibleResultPoint(ResultPoint point) {
      }
    });
  }

  private void startScanQa() {
    if (mIvScanLine != null) {
      TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.9f);
      animation.setDuration(4500);
      animation.setInterpolator(new LinearInterpolator());
      animation.setRepeatCount(-1);
      animation.setRepeatMode(Animation.RESTART);
      mIvScanLine.startAnimation(animation);
      mCaptureHelper.restartPreview();
    }
  }

  @Override
  public void handleBackgroundMessage(Message msg) {
    super.handleBackgroundMessage(msg);
    switch (msg.what) {
      case MSG_BACK_CHECK:
        checkCode();
        break;
    }
  }

  private void checkCode() {
    HttpRequest<CommonResponse> request = new HttpRequest<>(AppConfig.CHECK_EQUIMENTEXIST, CommonResponse.class);
    request.addParam("uc", InitSharedData.getUserCode());
    request.addParam("eid", mEid);
    Message uiMsg = obtainUiMessage();
    try {
      CommonResponse response = request.request();
      if (response != null) {
        if (response.getResult() > 0) {
          uiMsg.what = MSG_UI_CHECK_SUCCESS;
        } else {
          uiMsg.what = MSG_UI_CHECK_FAIL;
          uiMsg.obj = response.getInfo();
        }
      } else {
        uiMsg.what = MSG_UI_CHECK_FAIL;
      }
    } catch (AppException e) {
      e.printStackTrace();
      uiMsg.what = MSG_UI_CHECK_FAIL;
    }
    uiMsg.sendToTarget();

  }

  @Override
  public void handleUiMessage(Message msg) {
    super.handleUiMessage(msg);
    switch (msg.what) {
      case MSG_UI_CHECK_SUCCESS:
        mTipDialogHelper.hideDialog();
        Bundle bundle = new Bundle();
        bundle.putString(FinishInfoActivity.KEY_EID, mEid);
        goActivity(FinishInfoActivity.class, bundle);
        finish();
        break;
      case MSG_UI_CHECK_FAIL:
        mTipDialogHelper.hideDialog();
        if (msg.obj != null) {
          String error = (String) msg.obj;
          if (!TextUtils.isEmpty(error)) {
            ToastUtil.show(error);
          } else {
            ToastUtil.showError();
          }
        } else {
          ToastUtil.showError();
        }
        finish();
        break;
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    mCaptureHelper.onResume();
    startScanQa();
  }

  @Override
  protected void onPause() {
    super.onPause();
    mCaptureHelper.onPause();
  }

  @Override
  public void setupBroadcastActions(List<String> actions) {
    super.setupBroadcastActions(actions);
    actions.add(BroadcastActions.ACTION_FINISH_ACTIVITY_BEFORE_MAIN);
  }

  @Override
  public void handleBroadcast(Context context, Intent intent) {
    super.handleBroadcast(context, intent);
    String action = intent.getAction();
    if (TextUtils.equals(action, BroadcastActions.ACTION_FINISH_ACTIVITY_BEFORE_MAIN)) {
      finish();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mCaptureHelper.onDestroy();
  }
}
