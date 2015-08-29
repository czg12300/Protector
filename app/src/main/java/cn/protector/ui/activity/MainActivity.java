
package cn.protector.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import cn.common.ui.activity.BaseTitleActivity;
import cn.protector.R;
import cn.protector.ui.adapter.CommonFragmentPagerAdapter;
import cn.protector.ui.fragment.LocateFragment;
import cn.protector.ui.fragment.SettingFragment;
import cn.protector.ui.widget.MapViewPager;

import java.util.ArrayList;

public class MainActivity extends BaseTitleActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {
    private CommonFragmentPagerAdapter mMainPageAdapter;
    private MapViewPager mVpContent;
    private RadioGroup mRgMenu;
    private static final int UI_MESSAGE_INIT = 0x1f;
    private TextView mTvTitle;

    private static final int[] TITLE_IDS = {
            R.string.title_main_locate, R.string.title_main_history, R.string.title_main_message,
            R.string.title_main_health, R.string.title_main_setting
    };

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
        mVpContent = (MapViewPager) findViewById(R.id.vp_content);
        mRgMenu = (RadioGroup) findViewById(R.id.rg_menu);
        mMainPageAdapter = new CommonFragmentPagerAdapter(getSupportFragmentManager());
        mVpContent.setAdapter(mMainPageAdapter);
        mVpContent.setOnPageChangeListener(this);
        mRgMenu.setOnCheckedChangeListener(this);
        mVpContent.setOffscreenPageLimit(4);
        setSwipeBackEnable(false);
    }

    @Override
    protected void initEvent() {
    }

    protected void initData() {
        ArrayList<Fragment> list = new ArrayList<Fragment>();
        list.add(LocateFragment.newInstance());
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
                mRgMenu.check(R.id.rb_menu_locate);
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
