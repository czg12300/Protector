
package cn.protector.ui.activity.usercenter;

import android.view.View;

import cn.protector.R;
import cn.protector.ui.activity.CommonTitleActivity;

/**
 * 绑定设备页面
 */
public class AddDeviceActivity extends CommonTitleActivity {

    @Override
    protected void initView() {
        setContentView(R.layout.activity_add_device);
        setTitle(R.string.title_add_device);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btn_add) {
            goActivity(ScanQACodeActivity.class);
        }
    }
}
