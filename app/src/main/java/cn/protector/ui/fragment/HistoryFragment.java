
package cn.protector.ui.fragment;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import cn.common.ui.BasePopupWindow;
import cn.common.ui.fragment.BaseWorkerFragment;
import cn.common.ui.helper.PopupWindowHelper;
import cn.common.utils.BitmapUtil;
import cn.common.utils.DisplayUtil;
import cn.protector.R;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.logic.entity.DeviceInfo;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.ui.helper.CalendarHelper;
import cn.protector.ui.helper.MainTitleHelper;
import cn.protector.utils.ToastUtil;

import java.util.Calendar;
import java.util.List;

/**
 * 描述：定位页面
 *
 * @author jakechen on 2015/8/13.
 */
public class HistoryFragment extends BaseWorkerFragment
        implements View.OnClickListener, AMapLocationListener {
    private static final int MSG_UI_START = 0;

    private static final int MSG_UI_HIDE_TIME_TIP_POP = MSG_UI_START + 1;

    private BasePopupWindow mTimeTipPop;

    private MainTitleHelper mTitleHelper;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    private MapView mMapView;

    private AMap mAMap;

    private LocationSource.OnLocationChangedListener mOnLocationChangedListener;

    private LocationManagerProxy mAMapLocationManager;

    private TextView mTvTime;

    private SeekBar mSbTime;
    private CalendarHelper mCalendarHelper;
    private View mVTitle;

    @Override
    public void initView() {
        setContentView(R.layout.fragment_history);
        mMapView = (MapView) findViewById(R.id.mv_map);
        // mTvTime = (TextView) findViewById(R.id.tv_time);
        mSbTime = (SeekBar) findViewById(R.id.sb_time);
        mVTitle = findViewById(R.id.fl_title);
        mTitleHelper = new MainTitleHelper(mVTitle,
                MainTitleHelper.STYLE_HISTORY);
        mCalendarHelper = new CalendarHelper(getActivity());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        mCalendarHelper.setDataList( cal.get(Calendar.YEAR),(cal.get(Calendar.MONTH) - 1));
        initMapView();
    }

    private void initMapView() {
        mMapView.onCreate(mSavedInstanceState);// 此方法必须重写
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        UiSettings uiSettings = mAMap.getUiSettings();
        uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        uiSettings.setZoomControlsEnabled(false);
    }

    @Override
    protected void initEvent() {
        findViewById(R.id.ib_minus).setOnClickListener(this);
        findViewById(R.id.ib_plus).setOnClickListener(this);
        mTitleHelper.setRightButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarHelper.showCalendar(mVTitle);
            }
        });
        findViewById(R.id.ll_slide_time).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mSbTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        showTimePop();
                        break;
                    case MotionEvent.ACTION_UP:
                        sendEmptyUiMessageDelayed(MSG_UI_HIDE_TIME_TIP_POP, 300);
                        break;

                }
                return false;
            }
        });
        mSbTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mTvTime != null) {
                    mTvTime.setText("10:" + progress);
                }
                showTimePop();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    protected void initData() {
        mSbTime.setMax(100);
        // mSbTime.setProgress(0);
    }

    @Override
    public void setupBroadcastActions(List<String> actions) {
        super.setupBroadcastActions(actions);
        actions.add(BroadcastActions.ACTION_MAIN_DEVICE_CHANGE);
    }

    @Override
    public void handleUiMessage(Message msg) {
        super.handleUiMessage(msg);
        switch (msg.what) {
            case MSG_UI_HIDE_TIME_TIP_POP:
                hideTimePop();
                break;
        }
    }

    @Override
    public void handleBroadcast(Context context, Intent intent) {
        super.handleBroadcast(context, intent);
        String action = intent.getAction();
        if (TextUtils.equals(action, BroadcastActions.ACTION_MAIN_DEVICE_CHANGE)) {
            DeviceInfo info = DeviceInfoHelper.getInstance().getPositionDeviceInfo();
            if (info != null && !TextUtils.isEmpty(info.getNikeName())) {
                mTitleHelper.setTitle(info.getNikeName());
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ib_plus) {
            mAMap.moveCamera(CameraUpdateFactory.zoomIn());
        } else if (id == R.id.ib_minus) {
            mAMap.moveCamera(CameraUpdateFactory.zoomOut());
        }
    }


    /**
     * 隐藏跟随滚动的提示语
     */
    private void hideTimePop() {
        if (mTimeTipPop != null) {
            mTimeTipPop.dismiss();
        }
    }

    /**
     * 显示跟随滚动的提示语
     */
    private void showTimePop() {
        if (mTimeTipPop == null) {
            mTimeTipPop = new BasePopupWindow(getActivity());
            mTimeTipPop.setContentView(R.layout.pop_time_tip);
            mTvTime = (TextView) mTimeTipPop.findViewById(R.id.tv_time);
        }
        mTimeTipPop.showAtLocation(mSbTime, Gravity.NO_GRAVITY, 0, 0);
        updateTimePopLocation();
    }

    /**
     * 更新跟随滚动的提示语的位置
     */
    private void updateTimePopLocation() {
        int popViewWidth = mTimeTipPop.getWidth();
        int popViewHeight = mTimeTipPop.getHeight();
        int sbWidth = mSbTime.getWidth();
        int sbHeight = mSbTime.getHeight();
        int xOffset = (int) (mSbTime.getProgress() * (sbWidth - mSbTime.getPaddingLeft()
                - mSbTime.getPaddingRight() - DisplayUtil.dip(16)) / (float) mSbTime.getMax())
                - popViewWidth / 2 + sbHeight / 2;
        int yOffset = -(mSbTime.getHeight() + popViewHeight + DisplayUtil.dip(5));
        mTimeTipPop.update(mSbTime, xOffset, yOffset, -1, -1);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation aLocation) {
        if (mOnLocationChangedListener != null && aLocation != null) {
            ToastUtil.show(aLocation.getCity() + aLocation.getAddress() + aLocation.getPoiName());
            mOnLocationChangedListener.onLocationChanged(aLocation);// 显示系统小蓝点
            mAMap.setMyLocationRotateAngle(mAMap.getCameraPosition().bearing);// 设置小蓝点旋转角度
            LatLng latLng = new LatLng(aLocation.getLatitude(), aLocation.getLongitude());
            mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.5f));

        }
    }

    /**
     * 开始定位
     */
    private void startLocate() {
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(getActivity());
        }
        /*
         * mAMapLocManager.setGpsEnable(false);
         * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
         * API定位采用GPS和网络混合定位方式
         * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
         */
        mAMapLocationManager.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 10, this);
    }

    /**
     * 停止定位
     */
    private void stopLocate() {
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destroy();
            mAMapLocationManager = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

}
