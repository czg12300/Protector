
package cn.protector.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import cn.common.ui.BaseDialog;

public class LoadingDialog extends BaseDialog {

    public LoadingDialog(Context context) {
        super(context);
    }

    public void setLoadingText(String text, int tvId) {
        if (!TextUtils.isEmpty(text)) {
            TextView tv = (TextView) findViewById(tvId);
            tv.setText(text);
        }
    }

}
