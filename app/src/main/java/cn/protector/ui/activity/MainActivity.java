
package cn.protector.ui.activity;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import cn.common.ui.activity.BaseTitleActivity;
import cn.protector.R;
import cn.protector.ui.adapter.CommonFragmentPagerAdapter;
import cn.protector.ui.fragment.HistoryFragment;
import cn.protector.ui.fragment.LocateFragment;
import cn.protector.ui.fragment.SettingFragment;
import cn.protector.ui.widget.MapViewPager;

import java.util.ArrayList;

public class MainActivity extends BaseTitleActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private MapViewPager mVpContent;
    private RadioGroup mRgMenu;
    private RadioButton mRbLocate;
    private RadioButton mRbHistory;
    private RadioButton mRbMessage;
    private RadioButton mRbHealth;
    private RadioButton mRbSetting;
    private TextView mTvTitle;
    private ImageView mIvTitleRight;
    private ImageView mIvTitleLeft;

    @Override
    protected void setTitle(String title) {
        mTvTitle.setText(title);
    }

    @Override
    protected View getTitleLayoutView() {
        View vTitle = getLayoutInflater().inflate(R.layout.title_main, null);
        mTvTitle = (TextView) vTitle.findViewById(R.id.tv_title);
        mIvTitleRight = (ImageView) vTitle.findViewById(R.id.iv_right);
        mIvTitleLeft = (ImageView) vTitle.findViewById(R.id.iv_back);
        return vTitle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FrameLayout root = (FrameLayout) getWindow().findViewById(android.R.id.content);
        SurfaceView surfaceView = new SurfaceView(this);
        surfaceView.setVisibility(View.GONE);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(0, 0);
        root.addView(surfaceView, params);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_main);
        mVpContent = (MapViewPager) findViewById(R.id.vp_content);
        mRgMenu = (RadioGroup) findViewById(R.id.rg_menu);
        mRbLocate = (RadioButton) findViewById(R.id.rb_menu_locate);
        mRbHealth = (RadioButton) findViewById(R.id.rb_menu_health);
        mRbHistory = (RadioButton) findViewById(R.id.rb_menu_history);
        mRbMessage = (RadioButton) findViewById(R.id.rb_menu_message);
        mRbSetting = (RadioButton) findViewById(R.id.rb_menu_setting);
        mVpContent.setOffscreenPageLimit(5);
        setSwipeBackEnable(false);
    }

    @Override
    protected void initEvent() {
        mVpContent.setOnPageChangeListener(this);
        mRgMenu.setOnCheckedChangeListener(this);
        mIvTitleLeft.setOnClickListener(this);
    }

    protected void initData() {
        mVpContent.setAdapter(new CommonFragmentPagerAdapter(getSupportFragmentManager(), getFragments()));
        setTitle("小妮儿");
    }

    @Override
    public void handleUiMessage(Message msg) {
        super.handleUiMessage(msg);
    }

    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> list = new ArrayList<Fragment>();
        list.add(LocateFragment.newInstance());
        list.add(HistoryFragment.newInstance());
        list.add(SettingFragment.newInstance());
        list.add(SettingFragment.newInstance());
        list.add(SettingFragment.newInstance());
        return list;
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
                break;
            case 1:
                mIvTitleLeft.setVisibility(View.GONE);
                mIvTitleRight.setVisibility(View.VISIBLE);
                mRbHistory.setChecked(true);
                break;
            case 2:
                mRbMessage.setChecked(true);
                break;
            case 3:
                mRbHealth.setChecked(true);
                break;
            case 4:
                mIvTitleLeft.setVisibility(View.VISIBLE);
                mIvTitleRight.setVisibility(View.GONE);
                mRbSetting.setChecked(true);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            mVpContent.setCurrentItem(0, false);
        }
    }
}
