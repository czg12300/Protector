
package cn.protector.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;

import cn.protector.R;

/**
 * 描述:到这里去页面
 *
 * @author jakechen
 * @since 2016/3/17 17:49
 */
public class NavigateActivity extends CommonTitleActivity implements AMapNaviViewListener {
    private AMapNaviView mAmapAMapNaviView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);
        init(savedInstanceState);
    }
    @Override
    protected void initView() {
    setTitle("到这里去");
  }
    /**
     * 初始化
     * 
     * @param savedInstanceState
     */
    private void init(Bundle savedInstanceState) {
        mAmapAMapNaviView = (AMapNaviView) findViewById(R.id.map);
        mAmapAMapNaviView.onCreate(savedInstanceState);
        // 设置导航界面监听
        mAmapAMapNaviView.setAMapNaviViewListener(this);

        // 设置模拟速度
//        AMapNavi.getInstance(this).setEmulatorNaviSpeed(100);
        // 开启模拟导航
        AMapNavi.getInstance(this).startNavi(AMapNavi.GPSNaviMode);
    }

    /**
     * 导航界面左下角返回按钮回调
     */
    @Override
    public void onNaviCancel() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

  @Override
  public boolean onNaviBackClick() {
        return false;
  }

  /**
     * 导航界面右下角功能设置按钮回调
     */
    @Override
    public void onNaviSetting() {

    }

    @Override
    public void onNaviMapMode(int arg0) {
        // TODO Auto-generated method stub

    }

  @Override
  public void onNaviTurnClick() {
  }

  @Override
  public void onNextRoadClick() {
  }

  @Override
  public void onScanViewButtonClick() {
  }

  @Override
  public void onLockMap(boolean b) {
  }

  @Override
  public void onNaviViewLoaded() {
  }

  /**
     * 返回键盘监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    // ------------------------------生命周期方法---------------------------
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAmapAMapNaviView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAmapAMapNaviView.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        mAmapAMapNaviView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAmapAMapNaviView.onDestroy();
    }

}
