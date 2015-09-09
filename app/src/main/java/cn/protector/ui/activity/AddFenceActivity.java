
package cn.protector.ui.activity;

import android.widget.ImageView;

import cn.protector.R;

/**
 * 描述：新增围栏
 *
 * @author jakechen
 */
public class AddFenceActivity extends CommonTitleActivity {
    private ImageView mIvCode;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_qa_code);
        setTitle(R.string.title_qa_code);
        mIvCode = (ImageView) findViewById(R.id.iv_code);
    }

}
