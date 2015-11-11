
package cn.protector.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.widget.RadioButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.common.ui.activity.BaseWorkerFragmentActivity;
import cn.common.ui.widgt.MainTabViewPager;
import cn.common.ui.widgt.TabRadioGroup;
import cn.protector.ProtectorApplication;
import cn.protector.R;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.ui.adapter.CommonFragmentPagerAdapter;
import cn.protector.ui.fragment.HealthFragment;
import cn.protector.ui.fragment.HistoryFragment;
import cn.protector.ui.fragment.LocateFragment;
import cn.protector.ui.fragment.MessageFragment;
import cn.protector.ui.fragment.SettingFragment;
import cn.protector.ui.helper.MainTitleHelper;
import cn.protector.utils.ToastUtil;

public class MainActivity extends BaseWorkerFragmentActivity
        implements ViewPager.OnPageChangeListener, TabRadioGroup.OnCheckedChangeListener {
    private static final int MSG_UI_INIT_DATA = 0;


    private MainTabViewPager mVpContent;

    private TabRadioGroup mRgMenu;

    private RadioButton mRbLocate;

    private RadioButton mRbHistory;

    private RadioButton mRbMessage;

    private RadioButton mRbHealth;

    private RadioButton mRbSetting;

    private long lastClickTime;

    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();
        if (now - lastClickTime > 2000) {
            ToastUtil.show("再按一次退出");
            lastClickTime = now;
        } else {
            ProtectorApplication.getInstance().exitApp();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProtectorApplication app = (ProtectorApplication) ProtectorApplication.getInstance();
        app.setShowMain(true);
        initView();
        initEvent();
        sendEmptyUiMessage(MSG_UI_INIT_DATA);
        mVpContent.setCanScroll(false);
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        mVpContent = (MainTabViewPager) findViewById(R.id.vp_content);
        mRgMenu = (TabRadioGroup) findViewById(R.id.rg_menu);
        mRbLocate = (RadioButton) findViewById(R.id.rb_menu_locate);
        mRbHealth = (RadioButton) findViewById(R.id.rb_menu_health);
        mRbHistory = (RadioButton) findViewById(R.id.rb_menu_history);
        mRbMessage = (RadioButton) findViewById(R.id.rb_menu_message);
        mRbSetting = (RadioButton) findViewById(R.id.rb_menu_setting);
        mVpContent.setOffscreenPageLimit(5);
    }

    private void initEvent() {
        mVpContent.setOnPageChangeListener(this);
        mRgMenu.setOnCheckedChangeListener(this);
    }

    private void initData() {
        mVpContent.setAdapter(
                new CommonFragmentPagerAdapter(getSupportFragmentManager(), getFragments()));
        //初始化设备信息
        DeviceInfoHelper.getInstance();
    }

    @Override
    public void handleUiMessage(Message msg) {
        super.handleUiMessage(msg);
        switch (msg.what) {
            case MSG_UI_INIT_DATA:
                initData();
                break;
        }
    }

    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> list = new ArrayList<Fragment>();
        list.add(LocateFragment.newInstance());
        list.add(HistoryFragment.newInstance());
        list.add(MessageFragment.newInstance());
        list.add(HealthFragment.newInstance());
        list.add(SettingFragment.newInstance());
        return list;
    }

    @Override
    public void onPageScrollStateChanged(int status) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (mVpContent.getCurrentItem()) {
            case 0:
                mRbLocate.setChecked(true);
                mVpContent.setCanScroll(false);
                break;
            case 1:
                mVpContent.setCanScroll(false);
                mRbHistory.setChecked(true);
                break;
            case 2:
                mVpContent.setCanScrollRight(true);
                mRbMessage.setChecked(true);
                break;
            case 3:
                mVpContent.setCanScroll(true);
                mRbHealth.setChecked(true);
                break;
            case 4:
                mVpContent.setCanScroll(true);
                mRbSetting.setChecked(true);
                break;
        }
    }

    @Override
    public void setupBroadcastActions(List<String> actionList) {
        super.setupBroadcastActions(actionList);
        actionList.add(BroadcastActions.ACTION_MAIN_ACTIVITY_SELECT_TAB_LOCATE);
    }

    @Override
    public void handleBroadcast(Context context, Intent intent) {
        super.handleBroadcast(context, intent);
        String action = intent.getAction();
        if (TextUtils.equals(action, BroadcastActions.ACTION_MAIN_ACTIVITY_SELECT_TAB_LOCATE)) {
            if (mVpContent != null) {
                mVpContent.setCurrentItem(0, false);
            }
        }
    }

    @Override
    public void onCheckedChanged(TabRadioGroup group, int checkedId) {
        int position = 0;
        switch (checkedId) {
            case R.id.rb_menu_locate:
                position = 0;
                break;
            case R.id.rb_menu_history:
                position = 1;
                break;
            case R.id.rb_menu_message:
                position = 2;
                break;
            case R.id.rb_menu_health:
                position = 3;
                break;
            case R.id.rb_menu_setting:
                position = 4;
                break;
        }
        mVpContent.setCurrentItem(position, false);
    }
}
