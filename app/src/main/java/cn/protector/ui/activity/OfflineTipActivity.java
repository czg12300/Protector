
package cn.protector.ui.activity;

import android.os.Bundle;
import android.view.View;

import cn.common.ui.activity.BaseWorkerFragmentActivity;
import cn.protector.R;
import cn.protector.ui.activity.usercenter.LoginActivity;

/**
 * 描述:离线提示页面
 *
 * @author jakechen
 * @since 2015/11/11 10:21
 */
public class OfflineTipActivity extends BaseWorkerFragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_tip);
        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goActivity(LoginActivity.class);
                finish();
            }
        });
    }
}
