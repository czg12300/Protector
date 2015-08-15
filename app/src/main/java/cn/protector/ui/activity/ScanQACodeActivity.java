
package cn.protector.ui.activity;

import android.os.Message;
import android.view.View;

import cn.protector.R;

/**
 * 扫描二维码页面
 */
public class ScanQACodeActivity extends CommonTitleActivity {


    @Override
    protected void initView() {
        setContentView(R.layout.activity_add_device);
        setTitle(R.string.scan_qa_code_title);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btn_add) {
        showTipDialog(R.drawable.ico_key_input_normal,"haha");
        }
    }

    @Override
    public void handleUiMessage(Message msg) {
    }
}
