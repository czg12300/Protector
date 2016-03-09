
package cn.protector.ui.helper;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import cn.protector.R;
import cn.protector.dialog.LoadingDialog;

/**
 * 描述:提示的dialog
 *
 * @author jakechen
 * @since 2015/10/29 10:20
 */
public class TipDialogHelper {
    private Activity mActivity;

    private LoadingDialog mTipDialog;

    private ImageView mIvTip;

    private TextView mTvTip;

    private DialogInterface.OnDismissListener mOnDismissListener;

    public TipDialogHelper(Activity activity) {
        mActivity = activity;
    }

    /**
     * 显示提示的dialog
     */
    public void showErrorTip(int strId) {
        if (mActivity != null) {
            showErrorTip(mActivity.getString(strId));
        }
    }

    public void showSuccessTip(int strId) {
        if (mActivity != null) {
            showSuccessTip(mActivity.getString(strId));
        }
    }

    public void showLoadingTip(int strId) {
        if (mActivity != null) {
            showLoadingTip(mActivity.getString(strId));
        }
    }

    public void showLoadingTip(int strId, boolean canCancel) {
        if (mActivity != null) {
            showLoadingTip(mActivity.getString(strId), canCancel);
        }
    }

    public void showErrorTip(String str) {
        showTipDialog(R.drawable.ico_error_white, str);
    }

    public void showSuccessTip(String str) {
        showTipDialog(R.drawable.ico_complete_white, str);
    }

    public void showLoadingTip(String str) {
        showTipDialog(R.drawable.ico_spinner_white, str, true);
    }

    public void showLoadingTip(String str, boolean canCancel) {
        showTipDialog(R.drawable.ico_spinner_white, str, canCancel, true);
    }

    protected void showTipDialog(int imgId, String str) {
        showTipDialog(imgId, str, false, false);
    }

    protected void showTipDialog(int imgId, String str, boolean isRotate) {
        showTipDialog(imgId, str, false, isRotate);
    }

    protected void showTipDialog(int imgId, String str, boolean canCancel, boolean isRotate) {
        if (mActivity != null && !mActivity.isFinishing()) {
            if (mTipDialog == null) {
                mTipDialog = new LoadingDialog(mActivity);
                mTipDialog.setContentView(R.layout.dialog_tip);
                mTipDialog.setWindow(R.style.slide_top_bottom_animation, 0.0f);
                mIvTip = (ImageView) mTipDialog.findViewById(R.id.iv_tip);
                mTvTip = (TextView) mTipDialog.findViewById(R.id.tv_tip);
                if (mOnDismissListener != null) {
                    mTipDialog.setOnDismissListener(mOnDismissListener);
                }
            }
            mTipDialog.setCanceledOnTouchOutside(canCancel);
            mIvTip.setImageResource(imgId);
            if (!TextUtils.isEmpty(str)) {
                mTvTip.setText(str);
            }
            if (isRotate) {
                RotateAnimation animation = new RotateAnimation(0f, 359f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(1000);
                animation.setRepeatCount(-1);
                animation.setInterpolator(new LinearInterpolator());// 不停顿
                mIvTip.setAnimation(animation);
                animation.startNow();
            }else{
                mIvTip.clearAnimation();
            }
            if (!mTipDialog.isShowing()) {
                mTipDialog.show();
            }
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        mOnDismissListener = listener;
    }

    public void hideDialog() {
        if (mTipDialog != null) {
            mTipDialog.dismiss();
        }
    }
}
