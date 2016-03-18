
package cn.protector.ui.activity;

import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
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
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.maps.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;

import cn.protector.R;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.logic.http.response.NowDeviceInfoResponse;
import cn.protector.utils.ToastUtil;

/**
 * 描述:到这里去页面
 *
 * @author jakechen
 * @since 2016/3/17 17:49
 */
public class NavigateActivity extends CommonTitleActivity
        implements View.OnClickListener, AMapLocationListener, RouteSearch.OnRouteSearchListener {

    private MapView mMapView;

    private LocationSource.OnLocationChangedListener mOnLocationChangedListener;

    private LocationManagerProxy mAMapLocationManager;

    private ImageView ivRefresh;

    private RotateAnimation reFreshAnimation;

    private TextView tvDAddress;

    private TextView tvMAddress;

    private AMap mAMap;

    private RouteSearch mRouteSearch;

    private LatLonPoint mStartPoint; // 起点，

    private LatLonPoint mEndPoint;// 终点，

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        UiSettings uiSettings = mAMap.getUiSettings();
        uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM);
        initLocate();
        setSwipeBackEnable(false);
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
        mAMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });
        mAMap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
            }
        });
    }

    @Override
    protected void initView() {
        setTitle("位置导航");
        setContentView(R.layout.activity_navigate);
        mMapView = (MapView) findViewById(R.id.mv_map);
        ivRefresh = (ImageView) findViewById(R.id.iv_refresh);
        tvDAddress = (TextView) findViewById(R.id.tv_device_address);
        tvMAddress = (TextView) findViewById(R.id.tv_my_address);
        ivRefresh.setOnClickListener(this);
        findViewById(R.id.btn_drive).setOnClickListener(this);
        findViewById(R.id.btn_walk).setOnClickListener(this);
    }


    private void update(String address) {
        NowDeviceInfoResponse info = DeviceInfoHelper.getInstance().getNowDeviceInfo();
        if (info != null && !TextUtils.isEmpty(info.getAddress())) {
            mEndPoint = new LatLonPoint(info.getLat(), info.getLon());
            tvDAddress.setText(info.getAddress());
        }
        if (!TextUtils.isEmpty(address)) {
            tvMAddress.setText(address);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_drive) {
            // 驾车路径规划
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(
                    new RouteSearch.FromAndTo(mStartPoint, mEndPoint), RouteSearch.DrivingDefault,
                    null, null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询

        } else if (id == R.id.btn_walk) {
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(
                    new RouteSearch.FromAndTo(mStartPoint, mEndPoint), RouteSearch.WalkDefault);
            mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        } else if (id == R.id.iv_refresh) {
            showLoad();
            startLocate();
        }
    }


    /**
     * 设置一些amap的属性
     */
    private void initLocate() {
        mAMap.setMyLocationRotateAngle(180);
        mAMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                mOnLocationChangedListener = onLocationChangedListener;
                startLocate();
            }

            @Override
            public void deactivate() {
                stopLocate();
                mOnLocationChangedListener = null;
            }
        });
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation aLocation) {
        hideLoad();
        if (mOnLocationChangedListener != null && aLocation != null) {
            mStartPoint = new LatLonPoint(aLocation.getLatitude(), aLocation.getLongitude());
            update(aLocation.getAddress());
            mOnLocationChangedListener.onLocationChanged(aLocation);// 显示系统小蓝点
            mAMap.setMyLocationRotateAngle(mAMap.getCameraPosition().bearing);// 设置小蓝点旋转角度
            LatLng latLng = new LatLng(aLocation.getLatitude(), aLocation.getLongitude());
            mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.5f));

        } else {
            ToastUtil.show("定位失败");
        }
    }
    /**
     * 开始定位
     */
    private void startLocate() {
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(this);
        }
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
    protected void onDestroy() {
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

    private void showLoad() {
        if (reFreshAnimation == null) {
            reFreshAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            reFreshAnimation.setDuration(800);
            reFreshAnimation.setInterpolator(new LinearInterpolator());
        }
        reFreshAnimation.setRepeatCount(-1);
        ivRefresh.clearAnimation();
        ivRefresh.startAnimation(reFreshAnimation);
    }

    private void hideLoad() {
        if (reFreshAnimation != null) {
            reFreshAnimation.setRepeatCount(0);
        }
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        mAMap.clear();// 清理地图上的所有覆盖物
//        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    final DrivePath drivePath = result.getPaths().get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(this, mAMap,
                            drivePath, result.getStartPos(), result.getTargetPos());
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show("查询没有结果");
                }

            } else {
                ToastUtil.show("查询没有结果");
            }
//        } else {
//            ToastUtil.showError();
//        }

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        mAMap.clear();// 清理地图上的所有覆盖物
//        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    final WalkPath walkPath = result.getPaths().get(0);
                    WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this, mAMap, walkPath,
                            result.getStartPos(), result.getTargetPos());
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show("查询没有结果");
                }

            } else {
                ToastUtil.show("查询没有结果");
            }
//        } else {
//            ToastUtil.showError();
//        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
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
}
