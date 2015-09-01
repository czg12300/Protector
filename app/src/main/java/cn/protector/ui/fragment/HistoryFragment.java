
package cn.protector.ui.fragment;

import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

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

import cn.common.ui.fragment.BaseWorkerFragment;
import cn.common.ui.helper.PopupWindowHelper;
import cn.common.utils.BitmapUtil;
import cn.common.utils.DisplayUtil;
import cn.protector.R;
import cn.protector.utils.ToastUtil;

/**
 * 描述：定位页面
 *
 * @author jakechen on 2015/8/13.
 */
public class HistoryFragment extends BaseWorkerFragment
        implements View.OnClickListener, AMapLocationListener {

    private PopupWindowHelper mTimeTipPop;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    private MapView mMapView;

    private AMap mAMap;

    private LocationSource.OnLocationChangedListener mOnLocationChangedListener;

    private LocationManagerProxy mAMapLocationManager;

    private TextView mTvTime;

    private SeekBar mSbTime;

    @Override
    public void initView() {
        setContentView(R.layout.fragment_history);
        mMapView = (MapView) findViewById(R.id.mv_map);
        // mTvTime = (TextView) findViewById(R.id.tv_time);
        mSbTime = (SeekBar) findViewById(R.id.sb_time);
        mMapView.onCreate(mSavedInstanceState);// 此方法必须重写
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        UiSettings uiSettings = mAMap.getUiSettings();
        uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        uiSettings.setZoomControlsEnabled(false);
        initLocate();
    }

    @Override
    protected void initEvent() {
        findViewById(R.id.ib_minus).setOnClickListener(this);
        findViewById(R.id.ib_plus).setOnClickListener(this);
        findViewById(R.id.ll_slide_time).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mSbTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showTimePop((int) event.getX(), (int) event.getY());

                return false;
            }
        });
        mSbTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // changeTimeLocation();
                if (mTvTime != null) {
                    mTvTime.setText("10:" + progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void changeTimeLocation() {
        // LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
        // mTvTime.getLayoutParams();
        float max = mSbTime.getMax();
        float progress = mSbTime.getProgress();
        float maxW = mSbTime.getWidth() - mSbTime.getPaddingRight() - mSbTime.getPaddingLeft()
                - DisplayUtil.dip(20);
        float progressW = progress * maxW / max;
        // params.leftMargin = (int) progressW;
        // mTvTime.setLayoutParams(params);
        showTimePop((int) progressW, mSbTime.getTop());
    }

    @Override
    protected void initData() {
        mSbTime.setMax(100);
        mSbTime.setProgress(0);
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
     * 设置一些amap的属性
     */
    private void initLocate() {
        setMyLocationStyle();
        mAMap.setMyLocationRotateAngle(180);
        // mAMap.setLocationSource(new LocationSource() {
        // @Override
        // public void activate(OnLocationChangedListener
        // onLocationChangedListener) {
        // mOnLocationChangedListener = onLocationChangedListener;
        // startLocate();
        // }
        //
        // @Override
        // public void deactivate() {
        // stopLocate();
        // mOnLocationChangedListener = null;
        // }
        // });
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    /**
     * 自定义定位的样式
     */
    private void setMyLocationStyle() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromBitmap(BitmapUtil.decodeResource(
                R.drawable.img_head_girl1, (int) getDimension(R.dimen.locate_baby_avator),
                (int) getDimension(R.dimen.locate_baby_avator))));// 设置小蓝点的图标
        myLocationStyle.strokeColor(getColor(R.color.blue_03a9f4));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(getColor(R.color.blue_3003a9f4));// 设置圆形的填充颜色
        myLocationStyle.strokeWidth(2f);// 设置圆形的边框粗细
        mAMap.setMyLocationStyle(myLocationStyle);
    }

    private void showTimePop(int x, int y) {
        if (mTimeTipPop == null) {
            mTimeTipPop = new PopupWindowHelper(getActivity());
            mTimeTipPop.setView(getLayoutInflater().inflate(R.layout.pop_time_tip, null),
                    DisplayUtil.dip(40), DisplayUtil.dip(25));
            mTvTime = (TextView) mTimeTipPop.findViewById(R.id.tv_time);
        }
        mTimeTipPop.showAtLocation(mSbTime, Gravity.TOP, x, y);
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
