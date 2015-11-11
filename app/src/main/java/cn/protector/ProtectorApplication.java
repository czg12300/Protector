
package cn.protector;

import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;

import cn.common.AppException;
import cn.common.ui.activity.BaseApplication;
import cn.common.ui.activity.BaseWorkerApplication;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.helper.HeartBeatHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.Response.CommonResponse;
import cn.protector.ui.activity.OfflineTipActivity;

/**
 * 描述：程序入口
 *
 * @author Created by jakechen on 2015/8/6.
 */
public class ProtectorApplication extends BaseWorkerApplication {
    private boolean isShowMain = false;

    private boolean isAutoLogin = false;

    private static final int MSG_BACK_CHECK_LOGIN = 0;

    public boolean isAutoLogin() {
        return isAutoLogin;
    }

    public void setIsAutoLogin(boolean isAutoLogin) {
        this.isAutoLogin = isAutoLogin;
    }

    public void setShowMain(boolean isShow) {
        isShowMain = isShow;
    }

    public boolean isShowMain() {
        return isShowMain;
    }

    @Override
    protected BaseApplication getChildInstance() {
        return this;
    }

    @Override
    protected void onConfig() {
        HeartBeatHelper.getInstance().init(getApplicationContext());
        sendEmptyBackgroundMessageDelayed(MSG_BACK_CHECK_LOGIN, 500);
    }

    @Override
    protected void onRelease() {
    }

    @Override
    public void handleBackgroundMessage(Message msg) {
        super.handleBackgroundMessage(msg);
        switch (msg.what) {
            case MSG_BACK_CHECK_LOGIN:
                if (!isAutoLogin && InitSharedData.hasLogin()) {
                    checkLogin();
                }
                break;
        }
    }

    private void checkLogin() {
        HttpRequest<CommonResponse> request = new HttpRequest<>(AppConfig.CHECK_LOGINED,
                CommonResponse.class);
        if (!TextUtils.isEmpty(InitSharedData.getUserCode())) {
            request.addParam("uc", InitSharedData.getUserCode());
        }
        CommonResponse response = null;
        try {
            response = request.request();
        } catch (AppException e) {
            e.printStackTrace();
        }
        if (response == null || response.getResult() != CommonResponse.SUCCESS) {
            startActivity(new Intent(this, OfflineTipActivity.class));
        }
    }
}
