
package cn.protector.ui.activity.usercenter;

import com.google.zxing.ResultPoint;
import com.helper.CaptureHelper;

import android.graphics.Bitmap;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import cn.protector.R;
import cn.protector.ui.activity.CommonTitleActivity;

/**
 * 扫描二维码页面
 */
public class ScanQACodeActivity extends CommonTitleActivity {
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
        // mCaptureHelper.setBeepRawId(R.raw.beep);
        mCaptureHelper.setCaptureListener(new CaptureHelper.CaptureListener() {
            @Override
            public void handleCodeResult(String code, Bitmap bitmap) {
                mIvScanLine.clearAnimation();
                vScanFrame.setVisibility(View.GONE);
                mIvShadowScanFrame.setVisibility(View.VISIBLE);
                // mIvShadowScanFrame.setImageBitmap(bitmap);
                showLoadingTip(R.string.bind_device_ing, false);
            }

            @Override
            public void foundPossibleResultPoint(ResultPoint point) {
            }
        });
        startScanQa();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCaptureHelper.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCaptureHelper.onDestroy();
    }
}
