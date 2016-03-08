package cn.protector.ui.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolygonOptions;

import java.util.List;

import cn.common.utils.BitmapUtil;
import cn.protector.R;
import cn.protector.logic.entity.FenceInfo;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.utils.ToastUtil;

/**
 * 描述：围栏展示页面
 * 作者：jake on 2016/2/18 23:02
 */
public class FenceDetailActivity extends CommonTitleActivity {
    private FenceInfo mFenceInfo;
    private AMap mAMap;
    private MapView mMapView;

    @Override
    protected void initView() {
        mFenceInfo = (FenceInfo) getIntent().getSerializableExtra("FenceDetailActivity");
        if (mFenceInfo == null) {
            ToastUtil.show("初始化失败，请重试");
            finish();
        }
        setContentView(R.layout.activity_fence_detail);
        setTitle("围栏详情");
        ((TextView) findViewById(R.id.tv_name)).setText("" + mFenceInfo.getName());
        ((TextView) findViewById(R.id.tv_radius)).setText(mFenceInfo.getRadius() + "米");
        ((TextView) findViewById(R.id.tv_lat_lon)).setText("经度（" +
                mFenceInfo.getLat() + "）、" + "纬度（" +
                mFenceInfo.getLon() + ")");
        mMapView = (MapView) findViewById(R.id.mv_map);
        mMapView.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        setSwipeBackEnable(false);
        UiSettings uiSettings = mAMap.getUiSettings();
        uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM);
        uiSettings.setZoomControlsEnabled(true);
        mAMap.clear();
        // 设置Marker的图标样式
        mAMap.setMyLocationRotateAngle(mAMap.getCameraPosition().bearing);// 设置小蓝点旋转角度
        LatLng latLng = new LatLng(mFenceInfo.getLat(), mFenceInfo.getLon());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(DeviceInfoHelper.getInstance().getAvatar()));
        // 设置Marker的坐标，为我们点击地图的经纬度坐标
        markerOptions.position(latLng);
        // 设置Marker的可见性
        markerOptions.visible(true);
        // 设置Marker是否可以被拖拽，这里先设置为false，之后会演示Marker的拖拽功能
        markerOptions.draggable(false);
        // 将Marker添加到地图上去
        mAMap.addMarker(markerOptions);
        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.5f));
        // 绘制一个圆形
        mAMap.addCircle(new CircleOptions().center(latLng)
                .radius(mFenceInfo.getRadius()).strokeColor(getColor(R.color.red_ef0b0b)).fillColor(getColor(R.color.trans))
                .strokeWidth(6));

    }


    @Override
    public void onDestroy() {
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
