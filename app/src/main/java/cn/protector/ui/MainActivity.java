
package cn.protector.ui;

import android.os.Bundle;

import cn.common.ui.activity.BaseWorkerFragmentActivity;
import cn.protector.R;

public class MainActivity extends BaseWorkerFragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
