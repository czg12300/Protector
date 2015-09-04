
package cn.protector.ui.activity.usercenter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import java.util.List;

import cn.protector.R;
import cn.protector.data.BroadcastActions;
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
}
