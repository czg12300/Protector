
package cn.protector.ui.activity.setting;

import android.widget.ImageView;

import cn.protector.R;
import cn.protector.ui.activity.CommonTitleActivity;

/**
 * 描述：设备二维码
 *
 * @author jakechen
 */
public class QACodeActivity extends CommonTitleActivity {
    private ImageView mIvCode;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_qa_code);
        setTitle(R.string.title_qa_code);
        mIvCode = (ImageView) findViewById(R.id.iv_code);
    }

}
