
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
import android.widget.TextView;

import java.util.ArrayList;

import cn.common.ui.activity.BaseTitleActivity;
import cn.common.ui.widgt.TabRadioGroup;
import cn.protector.R;
import cn.protector.ui.adapter.CommonFragmentPagerAdapter;
import cn.protector.ui.fragment.HealthFragment;
import cn.protector.ui.fragment.HistoryFragment;
import cn.protector.ui.fragment.LocateFragment;
import cn.protector.ui.fragment.MessageFragment;
import cn.protector.ui.fragment.SettingFragment;
import cn.protector.ui.widget.MapViewPager;

public class MainActivity extends BaseTitleActivity implements ViewPager.OnPageChangeListener,
        TabRadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private MapViewPager mVpContent;

    private TabRadioGroup mRgMenu;

    private RadioButton mRbLocate;

    private RadioButton mRbHistory;

    private RadioButton mRbMessage;

    private RadioButton mRbHealth;

    private RadioButton mRbSetting;

    private TextView mTvTitle;

    private ImageView mIvTitleRight;

    private ImageView mIvTitleLeft;

    private View mVTitle;

    @Override
    protected void setTitle(String title) {
        mTvTitle.setText(title);
    }

    @Override
    protected View getTitleLayoutView() {
        mVTitle = getLayoutInflater().inflate(R.layout.title_main, null);
        mTvTitle = (TextView) mVTitle.findViewById(R.id.tv_title);
        mIvTitleRight = (ImageView) mVTitle.findViewById(R.id.iv_right);
        mIvTitleLeft = (ImageView) mVTitle.findViewById(R.id.iv_back);
        return mVTitle;
    }

    public void hideTitle() {
        if (mVTitle != null) {
            mVTitle.setVisibility(View.GONE);
        }
    }

    public void showTitle() {
        if (mVTitle != null && mVTitle.getVisibility() != View.VISIBLE) {
            mVTitle.setVisibility(View.VISIBLE);
        }
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
        mRgMenu = (TabRadioGroup) findViewById(R.id.rg_menu);
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
        mVpContent.setAdapter(
                new CommonFragmentPagerAdapter(getSupportFragmentManager(), getFragments()));
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
        showTitle();
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
                hideTitle();
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
