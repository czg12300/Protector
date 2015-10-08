
package cn.protector.ui.helper;

import android.location.Location;
import android.os.Bundle;

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
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;

import cn.protector.utils.ToastUtil;

/**
 * 描述：
 *
 * @author jake
 * @since 2015/9/27 11:53
 */
public class MapViewHelper implements AMapLocationListener {
    private AMap mAMap;

    private MapView mMapView;

    private LocationSource.OnLocationChangedListener mOnLocationChangedListener;

    private LocationManagerProxy mAMapLocationManager;

    public MapViewHelper(MapView mapView) {
        mMapView = mapView;
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        UiSettings uiSettings = mAMap.getUiSettings();
        uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        uiSettings.setZoomControlsEnabled(false);
        initLocate();
    }

    public AMap getAMap() {
        return mAMap;
    }

    /**
     * 设置一些amap的属性
     */
    private void initLocate() {
        mAMap.setMyLocationRotateAngle(180);
        mAMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                if (mOnLocationChangedListener != null) {
                    startLocate();
                }
                mOnLocationChangedListener = onLocationChangedListener;
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
            mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                    new LatLng(aLocation.getLatitude(), aLocation.getLongitude()), 15, 0, 30)), 500,
                    null);
        }
    }

    /**
     * 开始定位
     */
    public void startLocate() {
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(mMapView.getContext());
        }
        /*
         * mAMapLocManager.setGpsEnable(false);
         * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
         * API定位采用GPS和网络混合定位方式
         * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
         */
        mAMapLocationManager.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 18, this);
    }

    /**
     * 停止定位
     */
    public void stopLocate() {
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destroy();
            mAMapLocationManager = null;
        }
    }

    public void setOnMarkerClickListener(AMap.OnMarkerClickListener listener) {
        mAMap.setOnMarkerClickListener(listener);
    }

    public void setOnInfoWindowClickListener(AMap.OnInfoWindowClickListener listener) {
        mAMap.setOnInfoWindowClickListener(listener);
    }
    //
    // public void addMapMarker(List<WorkListInfo> list) {
    // if (list != null && list.size() > 0) {
    // for (int i = 0; i < list.size(); i++) {
    // addMapMark(list.get(i));
    // }
    // }
    // }
    //
    // private void addMapMark(WorkListInfo info) {
    // // 设置Marker的图标样式
    // MarkerOptions markerOptions = new MarkerOptions();
    // switch (info.getTimeType()) {
    // case WorkListInfo.TIME_TYPE_IN:
    // markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ico_point_blue));
    // break;
    // case WorkListInfo.TIME_TYPE_OUT_LESS_FIVE:
    // markerOptions
    // .icon(BitmapDescriptorFactory.fromResource(R.drawable.ico_point_violet));
    // break;
    // case WorkListInfo.TIME_TYPE_OUT_MORE_FIVE:
    // markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ico_point_red));
    // break;
    // }
    // // 设置Marker点击之后显示的标题
    // markerOptions.title(info.getAddress());
    // // 设置Marker的坐标，为我们点击地图的经纬度坐标
    // markerOptions.position(new LatLng(info.getLat(), info.getLng()));
    // // 设置Marker的可见性
    // markerOptions.visible(true);
    // // 设置Marker是否可以被拖拽，这里先设置为false，之后会演示Marker的拖拽功能
    // markerOptions.draggable(false);
    // // 将Marker添加到地图上去
    // mAMap.addMarker(markerOptions).setObject(info);
    // }

    public void zoomIn() {
        mAMap.animateCamera(CameraUpdateFactory.zoomIn(), 500, null);
    }

    public void zoomOut() {
        mAMap.animateCamera(CameraUpdateFactory.zoomOut(), 500, null);
    }
}
