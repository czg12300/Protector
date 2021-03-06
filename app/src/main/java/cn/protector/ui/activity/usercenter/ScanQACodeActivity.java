
package cn.protector.ui.activity.usercenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import cn.protector.R;
import cn.protector.data.BroadcastActions;
import cn.protector.ui.activity.CommonTitleActivity;

/**
 * 描述： 扫描二维码页面
 *
 * @author jakechen
 */
public class ScanQACodeActivity extends CommonTitleActivity {
    private static final int MSG_UI_START = 0;
    private static final int MSG_UI_BIND_SUCCESS = MSG_UI_START + 1;
    private ImageView mIvScanLine;

    private ImageView mIvShadowScanFrame;

    private SurfaceView mSvCamera;

    private CaptureHelper mCaptureHelper;

    private View vScanFrame;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_scan_qa_code);
        setTitle(R.string.title_scan_qa_code);
        mIvScanLine = (ImageView) findViewById(R.id.iv_scan_line);
        mIvShadowScanFrame = (ImageView) findViewById(R.id.iv_shadow_frame_scan);
        mSvCamera = (SurfaceView) findViewById(R.id.sv_camera);
        vScanFrame = findViewById(R.id.rl_frame_scan);
        mCaptureHelper = new CaptureHelper(mSvCamera, this);
    }

    @Override
    protected void initEvent() {
        mCaptureHelper.setCaptureListener(new CaptureHelper.CaptureListener() {
            @Override
            public void handleCodeResult(String code, Bitmap bitmap) {
                mIvScanLine.clearAnimation();
                vScanFrame.setVisibility(View.GONE);
                mIvShadowScanFrame.setVisibility(View.VISIBLE);
                // mIvShadowScanFrame.setImageBitmap(bitmap);
                showLoadingTip(R.string.bind_device_ing, false);
                sendEmptyUiMessageDelayed(MSG_UI_BIND_SUCCESS, 1000);
            }

            @Override
            public void foundPossibleResultPoint(ResultPoint point) {
            }
        });
    }

    @Override
    public void handleUiMessage(Message msg) {
        super.handleUiMessage(msg);
        switch (msg.what) {
            case MSG_UI_BIND_SUCCESS:
                goActivity(FinishInfoActivity.class);
                finish();
                break;
        }
    }

    private void startScanQa() {
        if (mIvScanLine != null) {
            TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
                    0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.9f);
            animation.setDuration(4500);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(-1);
            animation.setRepeatMode(Animation.RESTART);
            mIvScanLine.startAnimation(animation);
            mCaptureHelper.restartPreview();
        }
    }

    @Override
    protected void onTipDismiss() {
        if (vScanFrame != null && mIvShadowScanFrame != null) {
            vScanFrame.setVisibility(View.VISIBLE);
            mIvShadowScanFrame.setVisibility(View.GONE);
            startScanQa();
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
    protected void onDestroy() {
        super.onDestroy();
        mCaptureHelper.onDestroy();
    }
}
