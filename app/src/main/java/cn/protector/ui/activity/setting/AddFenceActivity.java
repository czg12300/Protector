
package cn.protector.ui.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
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
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;

import java.util.List;

import cn.common.utils.BitmapUtil;
import cn.protector.R;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.utils.ToastUtil;

/**
 * 描述:新增围栏页面
 *
 * @author jakechen
 * @since 2015/9/22 11:40
 */
public class AddFenceActivity extends CommonTitleActivity
        implements View.OnClickListener, AMapLocationListener {

    private MapView mMapView;

    private TextView mTvLocateTitle;

    private TextView mTvAddress;

    private AMap mAMap;

    private View mVBottom;

    private View mVTop;

    private LocationSource.OnLocationChangedListener mOnLocationChangedListener;

    private LocationManagerProxy mAMapLocationManager;

    @Override
    protected View getTitleLayoutView() {
        View vTitle = inflate(R.layout.title_add_fence);
        mIvBack = (ImageView) vTitle.findViewById(R.id.iv_back);
        mTvTitle = (TextView) vTitle.findViewById(R.id.tv_title);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                onBack();
            }
        });
        vTitle.findViewById(R.id.iv_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goActivity(SearchAreaActivity.class);
            }
        });

        return vTitle;
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
        uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        uiSettings.setZoomControlsEnabled(false);
        initLocate();
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_add_fence);
        setTitle(R.string.title_add_fence);
        mMapView = (MapView) findViewById(R.id.mv_map);
        mTvAddress = (TextView) findViewById(R.id.tv_address);
        mVBottom = findViewById(R.id.ll_bottom);
        mVTop = findViewById(R.id.ll_slide_time);
        mVBottom.setVisibility(View.VISIBLE);
        mMapView.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        findViewById(R.id.btn_save).setOnClickListener(this);
        mVBottom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mVTop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void setupBroadcastActions(List<String> actions) {
        super.setupBroadcastActions(actions);
    }

    @Override
    public void handleBroadcast(Context context, Intent intent) {
        super.handleBroadcast(context, intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_save) {
            // TODO
            finish();
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void initLocate() {
        setMyLocationStyle();
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
            mVBottom.setVisibility(View.VISIBLE);
            mTvAddress
                    .setText(aLocation.getCity() + aLocation.getAddress() + aLocation.getPoiName());
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
            mAMapLocationManager = LocationManagerProxy.getInstance(this);
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
