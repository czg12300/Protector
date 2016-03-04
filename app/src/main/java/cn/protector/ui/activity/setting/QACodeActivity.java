package cn.protector.ui.activity.setting;

import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import cn.common.AppException;
import cn.common.bitmap.core.ImageLoader;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.QACodeResponse;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.widget.StatusView;
import cn.protector.utils.ToastUtil;

/**
 * 描述：设备二维码
 *
 * @author jakechen
 */
public class QACodeActivity extends CommonTitleActivity {
  private static final int MSG_BACK_LOAD_DATA = 0;
  private static final int MSG_UI_LOAD_DATA = 0;
  private ImageView mIvCode;
  private StatusView mStatusView;

  @Override
  protected void initView() {
    mStatusView = new StatusView(this);
    mStatusView.setContentView(R.layout.activity_qa_code);
    setContentView(mStatusView);
    setTitle(R.string.title_qa_code);
    mStatusView.setNoDataTip("暂无二维码信息");
    mIvCode = (ImageView) findViewById(R.id.iv_code);
    mStatusView.showLoadingView();
    sendEmptyBackgroundMessage(MSG_BACK_LOAD_DATA);
    mStatusView.setStatusListener(new StatusView.StatusListener() {
      @Override
      public void reLoadData() {
        mStatusView.showLoadingView();
        sendEmptyBackgroundMessage(MSG_BACK_LOAD_DATA);
      }
    });
  }

  @Override
  public void handleBackgroundMessage(Message msg) {
    super.handleBackgroundMessage(msg);
    if (msg.what == MSG_BACK_LOAD_DATA) {
      HttpRequest<QACodeResponse> request = new HttpRequest<>(AppConfig.GET_QR_CODE, QACodeResponse.class);
      request.addParam("uc", InitSharedData.getUserCode());
      if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
        request.addParam("eid", DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId());
      }
      Message message = obtainUiMessage();
      try {
        message.obj = request.request();
      } catch (AppException e) {
        e.printStackTrace();
      }
      message.what = MSG_UI_LOAD_DATA;
      message.sendToTarget();
    }
  }

  @Override
  public void handleUiMessage(Message msg) {
    super.handleUiMessage(msg);
    if (msg.what == MSG_UI_LOAD_DATA) {
      if (msg.obj != null) {
        QACodeResponse response = (QACodeResponse) msg.obj;
        if (response.isOk()) {
          if (!TextUtils.isEmpty(response.getCodeURL())) {
            mStatusView.showContentView();
            ImageLoader.getInstance().displayImage(response.getCodeURL(), mIvCode);
          } else {
            if (!TextUtils.isEmpty(response.getInfo())) {
              ToastUtil.show(response.getInfo());
            }
            mStatusView.showNoDataView();
          }
        } else {
          mStatusView.showFailView();
        }
      } else {
        mStatusView.showFailView();
      }
    }
  }
}
