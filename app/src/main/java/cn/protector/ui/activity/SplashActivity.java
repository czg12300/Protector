
package cn.protector.ui.activity;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import cn.common.AppException;
import cn.common.ui.activity.BaseWorkerFragmentActivity;
import cn.protector.AppConfig;
import cn.protector.ProtectorApplication;
import cn.protector.R;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.helper.HeartBeatHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.LoginResponse;
import cn.protector.ui.activity.usercenter.LoginActivity;

/**
 * 启动页面
 */
public class SplashActivity extends BaseWorkerFragmentActivity {
    private ImageView mIvSplash;

    /**
     * 延时进入页面时间
     */
    private static final long DELAYED_TIME = 1 * 1000 - 500;

    /**
     * 进入引导页
     */
    private static final int MSG_GUIDE = 0;

    /**
     * 进入主页面
     */
    private static final int MSG_MAIN = 1;

    private static final int MSG_BACK_AUTO_LOGIN = 0;

    private static final int MSG_UI_AUTO_LOGIN = 2;

    /**
     * 进入登录页面
     */
    private static final int MSG_LOGIN = 2;

    private long lastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIvSplash = new ImageView(this);
        mIvSplash.setImageResource(R.drawable.loading_page_ishoe);
        mIvSplash.setScaleType(ImageView.ScaleType.FIT_XY);
        setContentView(mIvSplash);
        if (InitSharedData.hasLogin() && !TextUtils.isEmpty(InitSharedData.getDeviceData())) {
            lastTime = System.currentTimeMillis();
            ProtectorApplication app = (ProtectorApplication) ProtectorApplication.getInstance();
            app.setIsAutoLogin(true);
            sendEmptyBackgroundMessage(MSG_BACK_AUTO_LOGIN);
        } else {
            sendEmptyUiMessageDelayed(MSG_LOGIN, DELAYED_TIME);
        }
    }

    @Override
    public void handleUiMessage(Message msg) {
        switch (msg.what) {
            case MSG_GUIDE:
                goActivity(GuideActivity.class);
                break;
            case MSG_LOGIN:
                goActivity(LoginActivity.class);
                break;
            case MSG_MAIN:
                goActivity(MainActivity.class);
                break;
        }
        finish();
    }

    @Override
    public void handleBackgroundMessage(Message msg) {
        super.handleBackgroundMessage(msg);
        switch (msg.what) {
            case MSG_BACK_AUTO_LOGIN:
                HttpRequest<LoginResponse> request = new HttpRequest<>(AppConfig.LOGIN,
                        LoginResponse.class);
                request.addParam("u", InitSharedData.getMobile());
                request.addParam("p", InitSharedData.getPassword());
                LoginResponse response = null;
                try {
                    response = request.request();
                } catch (AppException e) {
                    e.printStackTrace();
                }
                int msgWhat = MSG_LOGIN;
                if (response != null && !TextUtils.isEmpty(response.getCode())) {
                    // 开始心跳包发送
                    HeartBeatHelper.getInstance().start();
                    InitSharedData.setUserId(response.getUserId());
                    InitSharedData.setUserCode(response.getCode());
                    msgWhat = MSG_MAIN;
                }
                if ((System.currentTimeMillis() - lastTime) >= DELAYED_TIME) {
                    sendEmptyUiMessage(msgWhat);
                } else {
                    sendEmptyUiMessageDelayed(msgWhat,
                            DELAYED_TIME - (System.currentTimeMillis() - lastTime));
                }

                break;
        }
    }
}
