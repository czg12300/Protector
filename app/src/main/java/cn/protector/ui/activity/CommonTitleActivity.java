
package cn.protector.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.common.ui.activity.BaseTitleActivity;
import cn.common.utils.CommonUtil;
import cn.protector.R;
import cn.protector.logic.helper.OfflineHelper;
import cn.protector.logic.helper.OfflineListener;

public abstract class CommonTitleActivity extends BaseTitleActivity implements OfflineListener {
    protected ImageView mIvBack;

    protected TextView mTvTitle;

    protected boolean needListenOffline() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (needListenOffline()) {
            OfflineHelper.getInstance().addListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (needListenOffline()) {
            OfflineHelper.getInstance().removeListener(this);
        }
    }

    @Override
    public void offline() {
        Intent it = new Intent(this, OfflineTipActivity.class);
        startActivity(it);
    }

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
