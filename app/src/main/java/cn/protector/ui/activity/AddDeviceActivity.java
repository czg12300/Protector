
package cn.protector.ui.activity;

import android.os.Message;
import android.view.View;

import cn.protector.R;

/**
 * 绑定设备页面
 */
public class AddDeviceActivity extends CommonTitleActivity {


    @Override
    protected void initView() {
        setContentView(R.layout.activity_add_device);
        setTitle(R.string.add_device_title);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btn_add) {
            goActivity(ScanQACodeActivity.class);
        }
    }
}
