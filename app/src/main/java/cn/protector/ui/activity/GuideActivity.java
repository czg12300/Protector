
package cn.protector.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.ImageView;

import cn.common.ui.activity.BaseWorkerFragmentActivity;
import cn.protector.R;

/**
 * 引导页面
 */
public class GuideActivity extends BaseWorkerFragmentActivity implements Handler.Callback {
    private ImageView mIvSplash;

    /**
     * 延时进入页面时间
     */
    private static final long delay_time = 2 * 1000;

    /**
     * 进入引导页
     */
    private static final int msg_guide = 0;

    /**
     * 进入主页面
     */
    private static final int msg_main = 1;

    /**
     * 进入登录页面
     */
    private static final int msg_login = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIvSplash = new ImageView(this);
        mIvSplash.setImageResource(R.drawable.splash);
        setContentView(mIvSplash);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBack();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isFinishing()) {
                finish();
            }
            onBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onBack() {

    }

    @Override
    public boolean handleMessage(Message msg) {
        return true;
    }
}
