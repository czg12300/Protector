package cn.protector.ui.fragment;

import android.os.Bundle;

import cn.common.ui.fragment.BaseWorkerFragment;
import cn.protector.R;

/**
 * Created by Administrator on 2015/8/13.
 */
public class SettingFragment extends BaseWorkerFragment {
    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public void initView() {
        setContentView(R.layout.fragment_setting);

    }
}
