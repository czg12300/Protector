
package cn.protector.ui.activity.setting;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import cn.common.AppException;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.CommonResponse;
import cn.protector.logic.http.response.LocateInfoResponse;
import cn.protector.logic.http.response.NowDeviceInfoResponse;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.helper.TipDialogHelper;
import cn.protector.utils.ToastUtil;

/**
 * 描述：位置修订页面
 *
 * @author jake
 * @since 2015/9/21 23:15
 */
public class LocationRegulateActivity extends CommonTitleActivity implements
        GeocodeSearch.OnGeocodeSearchListener {
    private static final int MSG_UI_GET_LOCATE = 1;

    private static final int MSG_BACK_GET_LOCATE = 1;

    private static final int MSG_UI_SAVE = 0;

    private static final int MSG_BACK_SAVE = 0;

    private MapView mMapView;

    private TextView tvInfo;

    private TextView tvAddress;

    private View mVBottom;

    private AMap mAMap;

    private NowDeviceInfoResponse mNowDeviceInfoResponse;

    private TipDialogHelper mTipDialogHelper;

    private LatLng mLatLng;

    private GeocodeSearch geocoderSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        setSwipeBackEnable(false);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_locate_regulate);
        setTitle(R.string.title_locate_regulate);
        mMapView = (MapView) findViewById(R.id.mv_map);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        tvInfo = (TextView) findViewById(R.id.tv_info);
        mVBottom = findViewById(R.id.ll_bottom);
        mVBottom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mVBottom.setVisibility(View.VISIBLE);
        mMapView.requestDisallowInterceptTouchEvent(true);
        mTipDialogHelper = new TipDialogHelper(this);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.equals(tvAddress.getText().toString().trim(), mNowDeviceInfoResponse
                        .getAddress().trim())) {
                    save();
                } else {
                    ToastUtil.show("请长按头像，移动到想要修定的位置");
                }
            }
        });
    }

    private void save() {
        mTipDialogHelper.showLoadingTip("正在保存");
        sendEmptyBackgroundMessage(MSG_BACK_SAVE);
    }

    @Override
    protected void initData() {
        super.initData();
        sendEmptyBackgroundMessage(MSG_BACK_GET_LOCATE);
    }

    private void updateUi(String address) {
        if (tvAddress != null && !TextUtils.isEmpty(address)) {
            tvAddress.setText(address);
        }
        if (tvInfo != null) {
            tvInfo.setText("(经度:" + mLatLng.latitude + "、纬度:" + mLatLng.longitude + ")");
        }
    }

    private void addMarker() {
        mAMap.clear();
        // 设置Marker的图标样式
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(DeviceInfoHelper.getInstance()
                .getAvatar()));
        // 设置Marker点击之后显示的标题
        markerOptions.title(mNowDeviceInfoResponse.getAddress());
        markerOptions.anchor(0.5f, 0.5f);
        // 设置Marker的坐标，为我们点击地图的经纬度坐标
        markerOptions.position(mLatLng);
        // 设置Marker的可见性
        markerOptions.visible(true);
        // 设置Marker是否可以被拖拽，这里先设置为false，之后会演示Marker的拖拽功能
        markerOptions.draggable(true);
        // 将Marker添加到地图上去
        mAMap.addMarker(markerOptions).setObject(mNowDeviceInfoResponse);
        mAMap.addCircle(new CircleOptions().center(mLatLng)
                .radius(mNowDeviceInfoResponse.getPosiPrecision())
                .strokeColor(getColor(R.color.blue_03a9f4))
                .fillColor(getColor(R.color.blue_3003a9f4)).strokeWidth(6));
        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 18.5f));
        mAMap.setOnMarkerDragListener(new AMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                mLatLng = marker.getPosition();
                LatLonPoint point = new LatLonPoint(mLatLng.latitude, mLatLng.longitude);
                geocoderSearch.getFromLocationAsyn(new RegeocodeQuery(point, 200,
                        GeocodeSearch.AMAP));
            }
        });
    }

    @Override
    public void handleBackgroundMessage(Message msg) {
        super.handleBackgroundMessage(msg);
        switch (msg.what) {
            case MSG_BACK_SAVE:
                saveTask();
                break;
            case MSG_BACK_GET_LOCATE:
                getLocateTask();
                break;
        }
    }

    private void getLocateTask() {
        HttpRequest<LocateInfoResponse> request = new HttpRequest<>(AppConfig.GET_POSITIONDATA,
                LocateInfoResponse.class);
        request.addParam("uc", InitSharedData.getUserCode());
        if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
            request.addParam("eid", DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId());
        }
        Message message = obtainUiMessage();
        message.what = MSG_UI_GET_LOCATE;
        try {
            message.obj = request.request();
        } catch (AppException e) {
            e.printStackTrace();
        }
        message.sendToTarget();
    }

    private void saveTask() {
        HttpRequest<CommonResponse> request = new HttpRequest<>(AppConfig.SET_POSITIONCORRECT,
                CommonResponse.class);
        if (!TextUtils.isEmpty(InitSharedData.getUserCode())) {
            request.addParam("uc", InitSharedData.getUserCode());
        }
        if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
            request.addParam("eid", DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId());
        }
        request.addParam("dataid", mNowDeviceInfoResponse.getId() + "");
        request.addParam("lat", "" + mLatLng.latitude);
        request.addParam("lon", "" + mLatLng.longitude);
        Message message = obtainUiMessage();
        message.what = MSG_UI_SAVE;
        try {
            message.obj = request.request();
        } catch (AppException e) {
            e.printStackTrace();
        }
        message.sendToTarget();
    }

    @Override
    public void handleUiMessage(Message msg) {
        super.handleUiMessage(msg);
        switch (msg.what) {
            case MSG_UI_GET_LOCATE:
                handleGetLocate(msg.obj != null ? (LocateInfoResponse) msg.obj : null);
                break;
            case MSG_UI_SAVE:
                if (mTipDialogHelper != null) {
                    mTipDialogHelper.hideDialog();
                }
                if (msg.obj != null) {
                    CommonResponse response = (CommonResponse) msg.obj;
                    if (response.getResult() > 0) {
                        ToastUtil.show("保存成功");
                        finish();
                    } else {
                        if (!TextUtils.isEmpty(response.getInfo())) {
                            ToastUtil.show(response.getInfo());
                        } else {
                            ToastUtil.show("保存失败");
                        }
                    }
                } else {
                    ToastUtil.show("保存失败");
                }
                break;
        }
    }

    private void handleGetLocate(LocateInfoResponse response) {
        NowDeviceInfoResponse info = DeviceInfoHelper.getInstance().getNowDeviceInfo();
        if (info == null) {
            ToastUtil.show("初始化失败,请重试");
            finish();
            return;
        }
        if (response.getLat() > 0) {
            info.setLat(response.getLat());
        }
        if (response.getLon() > 0) {
            info.setLon(response.getLon());
        }
        if (!TextUtils.isEmpty(response.getAddress())) {
            info.setAddress(response.getAddress());
        }
        mNowDeviceInfoResponse=info;
        sendBroadcast(BroadcastActions.ACTION_UPDATE_POSITION_DEVICE_INFO);
        mLatLng = new LatLng(response.getLat(), response.getLon());
        addMarker();
        updateUi(mNowDeviceInfoResponse.getAddress());
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        updateUi(regeocodeResult.getRegeocodeAddress().getFormatAddress());
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

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
