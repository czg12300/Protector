
package cn.protector.ui.activity.setting;

import android.view.View;
import android.widget.ImageView;

import cn.protector.R;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.activity.usercenter.ScanQACodeActivity;

/**
 * 描述：监护人员页面
 *
 * @author jakechen
 */
public class LocateModeActivity extends CommonTitleActivity implements View.OnClickListener {
    private static final int MODE_SAVE = 0;
    private static final int MODE_NORMAL = 1;
    private ImageView mIvNormal;
    private ImageView mIvSave;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_locate_mode);
        setTitle(R.string.title_locate_mode);
        mIvNormal = (ImageView) findViewById(R.id.iv_mode_normal);
        mIvSave = (ImageView) findViewById(R.id.iv_mode_save);
        findViewById(R.id.ll_mode_normal).setOnClickListener(this);
        findViewById(R.id.ll_mode_save).setOnClickListener(this);
        setMode(MODE_NORMAL);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_mode_save) {
            setMode(MODE_SAVE);
        } else if (v.getId() == R.id.ll_mode_normal) {
            setMode(MODE_NORMAL);
        }
    }

    public void setMode(int mode) {
        switch (mode) {
            case MODE_NORMAL:
                mIvNormal.setVisibility(View.VISIBLE);
                mIvSave.setVisibility(View.GONE);
                break;
            case MODE_SAVE:
                mIvSave.setVisibility(View.VISIBLE);
                mIvNormal.setVisibility(View.GONE);
                break;
        }
    }

}
