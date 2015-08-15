
package cn.protector.ui.activity;

import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cn.common.ui.activity.BaseTitleActivity;
import cn.protector.R;
import cn.protector.ui.adapter.CommonFragmentPagerAdapter;
import cn.protector.ui.fragment.SettingFragment;

public class MainActivity extends BaseTitleActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {
    private CommonFragmentPagerAdapter mMainPageAdapter;
    private ViewPager mVpContent;
    private RadioGroup mRgMenu;
    private static final int UI_MESSAGE_INIT = 0x1f;
    private TextView mTvTitle;
    private static final int[] TITLE_IDS = {R.string.main_location_title, R.string.main_history_title, R.string.main_message_title, R.string.main_health_title, R.string.main_setting_title};

    @Override
    protected void setTitle(String title) {
        mTvTitle.setText(title);
    }

    @Override
    protected View getTitleLayoutView() {
        View vTitle = getLayoutInflater().inflate(R.layout.title_main, null);
        mTvTitle = (TextView) vTitle.findViewById(R.id.tv_title);
        return vTitle;
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
        mVpContent = (ViewPager) findViewById(R.id.vp_content);
        mRgMenu = (RadioGroup) findViewById(R.id.rg_menu);
        mMainPageAdapter = new CommonFragmentPagerAdapter(getSupportFragmentManager());
        mVpContent.setAdapter(mMainPageAdapter);
        mVpContent.setOnPageChangeListener(this);
        mRgMenu.setOnCheckedChangeListener(this);
        mVpContent.setOffscreenPageLimit(4);
        setSwipeBackEnable(false);
        sendEmptyUiMessage(UI_MESSAGE_INIT);
    }

    @Override
    public void handleUiMessage(Message msg) {
        switch (msg.what) {
            case UI_MESSAGE_INIT:
                initMainData();
                break;

            default:
                break;
        }
    }

    private void initMainData() {
        ArrayList<Fragment> list = new ArrayList<Fragment>();
        list.add(SettingFragment.newInstance());
        list.add(SettingFragment.newInstance());
        list.add(SettingFragment.newInstance());
        list.add(SettingFragment.newInstance());
        list.add(SettingFragment.newInstance());
        mMainPageAdapter.setData(list);
        setTitle(TITLE_IDS[0]);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
        int position = 0;
        switch (checkId) {
            case R.id.rb_menu_location:
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
        setTitle(TITLE_IDS[position]);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                mRgMenu.check(R.id.rb_menu_location);
                break;
            case 1:
                mRgMenu.check(R.id.rb_menu_history);
                break;
            case 2:
                mRgMenu.check(R.id.rb_menu_message);
                break;
            case 3:
                mRgMenu.check(R.id.rb_menu_health);
                break;
            case 4:
                mRgMenu.check(R.id.rb_menu_setting);
                break;
        }
        setTitle(TITLE_IDS[position]);
    }
}
