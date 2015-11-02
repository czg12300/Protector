
package cn.protector.ui.activity;

import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.common.ui.activity.BaseTitleActivity;
import cn.common.utils.CommonUtil;
import cn.protector.R;

public abstract class CommonTitleActivity extends BaseTitleActivity {
    protected ImageView mIvBack;

    protected TextView mTvTitle;

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
        setBackgroundColor(getColor(R.color.background_gray));
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

    /**
     * 返回按键或退出按钮的回调接口
     */
    protected void onBack() {

    }
}
