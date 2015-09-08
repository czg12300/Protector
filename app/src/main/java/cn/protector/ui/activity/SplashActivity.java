
package cn.protector.ui.activity;

import android.os.Bundle;
import android.os.Message;
import android.widget.ImageView;

import cn.common.ui.activity.BaseWorkerFragmentActivity;
import cn.protector.R;
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

    /**
     * 进入登录页面
     */
    private static final int MSG_LOGIN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIvSplash = new ImageView(this);
        mIvSplash.setImageResource(R.drawable.loading_page_ishoe);
        mIvSplash.setScaleType(ImageView.ScaleType.FIT_XY);
        setContentView(mIvSplash);
        sendEmptyUiMessageDelayed(MSG_LOGIN, DELAYED_TIME);
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
}
