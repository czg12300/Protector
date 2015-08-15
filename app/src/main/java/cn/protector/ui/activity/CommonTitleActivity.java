
package cn.protector.ui.activity;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import cn.common.ui.activity.BaseTitleActivity;
import cn.common.utils.CommonUtil;
import cn.protector.R;
import cn.protector.ui.widget.LoadingDialog;

public abstract class CommonTitleActivity extends BaseTitleActivity {
    protected ImageView mIvBack;

    private TextView mTvTitle;

    @Override
    protected View getTitleLayoutView() {
        View vTitle = getLayoutInflater().inflate(R.layout.title_common_back, null);
        mIvBack = (ImageView) vTitle.findViewById(R.id.iv_back);
        mTvTitle = (TextView) vTitle.findViewById(R.id.tv_title);
        mIvBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isFinishing()) {
                    finish();
                }
                onBack();
            }
        });
        return vTitle;
    }

    /**
     * 设置点击隐藏软键盘
     */
    public void setHideInputView(int id) {
        findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.hideSoftInput(CommonTitleActivity.this);
            }
        });
    }

    @Override
    protected void setTitle(String title) {
        mTvTitle.setText(title);
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

    private LoadingDialog mTipDialog;

    private ImageView mIvTip;

    private TextView mTvTip;

    /**
     * 显示提示的dialog
     * 
     * @param imgId
     * @param strId
     */

    public void showTipDialog(int imgId, int strId) {
        showTipDialog(imgId, strId, false);
    }

    public void showTipDialog(int imgId, String str) {
        showTipDialog(imgId, str, false);
    }

    protected void showTipDialog(int imgId, int strId, boolean isRotate) {
        showTipDialog(imgId, getString(strId), isRotate);
    }

    protected void showTipDialog(int imgId, String str, boolean isRotate) {
        if (mTipDialog == null) {
            mTipDialog = new LoadingDialog(this);
            mTipDialog.setContentView(R.layout.dialog_tip);
            mTipDialog.setWindow(R.style.slide_top_bottom_animation, 0.0f);
            mIvTip = (ImageView) mTipDialog.findViewById(R.id.iv_tip);
            mTvTip = (TextView) mTipDialog.findViewById(R.id.tv_tip);
        }
        mIvTip.setImageResource(imgId);
        if (!TextUtils.isEmpty(str)) {
            mTvTip.setText(str);
        }
        if (isRotate) {
            RotateAnimation animation = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF,
                    0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(1000);
            animation.setRepeatCount(-1);
            animation.setInterpolator(new LinearInterpolator());// 不停顿
            mIvTip.setAnimation(animation);
            animation.startNow();
        }
        mTipDialog.show();
    }

    /**
     * 隐藏提示窗口
     */
    protected void dismiss() {
        if (mTipDialog != null && mTipDialog.isShowing()) {
            mTipDialog.dismiss();
        }
    }

    /**
     * 返回按键或退出按钮的回调接口
     */
    protected void onBack() {

    }
}
