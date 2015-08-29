package cn.protector.ui.fragment;

import android.location.Location;
import android.os.Bundle;
import android.view.View;

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
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

import java.util.ArrayList;

import cn.common.ui.fragment.BaseWorkerFragment;
import cn.common.utils.BitmapUtil;
import cn.protector.R;

/**
 * 描述：定位页面
 *
 * @author jakechen on 2015/8/13.
 */
public class LocateFragment extends BaseWorkerFragment implements View.OnClickListener, LocationSource,
        AMapLocationListener {
    private Marker marker;

    public static LocateFragment newInstance() {
        return new LocateFragment();
    }

    private MapView mMapView;
    private AMap mAMap;
    private OnLocationChangedListener mListener;
    private LocationManagerProxy mAMapLocationManager;
    private float mapZoom = 0;

    @Override
    public void initView() {
        setContentView(R.layout.fragment_locate);
        mMapView = (MapView) findViewById(R.id.mv_map);
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
    }

    /**
     * 设置一些amap的属性
     */
    private void initLocate() {
        ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
        giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point1));
        giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point2));
        giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point3));
        giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point4));
        giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point5));
        giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point6));
        marker = mAMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .icons(giflist).period(50));
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromBitmap(BitmapUtil.decodeResource(R.drawable.img_head_girl1, (int) getDimension(R.dimen.locate_baby_avator), (int) getDimension(R.dimen.locate_baby_avator))));// 设置小蓝点的图标
        myLocationStyle.strokeColor(getColor(R.color.blue_03a9f4));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(getColor(R.color.blue_3003a9f4));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(2f);// 设置圆形的边框粗细
        mAMap.setMyLocationStyle(myLocationStyle);
        mAMap.setMyLocationRotateAngle(180);
        mAMap.setLocationSource(this);// 设置定位监听
//        mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        //设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    /**
     * 方法必须重写
     */
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

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
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
        if (mListener != null && aLocation != null) {
            mListener.onLocationChanged(aLocation);// 显示系统小蓝点
            marker.setPosition(new LatLng(aLocation.getLatitude(), aLocation
                    .getLongitude()));// 定位雷达小图标
            float bearing = mAMap.getCameraPosition().bearing;
            mAMap.setMyLocationRotateAngle(bearing);// 设置小蓝点旋转角度
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(getActivity());
            /*
             * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
            mAMapLocationManager.requestLocationData(
                    LocationProviderProxy.AMapNetwork, 2000, 10, this);
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destroy();
            ;
        }
        mAMapLocationManager = null;
    }
}
