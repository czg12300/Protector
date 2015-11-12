
package cn.protector.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;

import java.util.List;
import java.util.Timer;

import cn.common.AppException;
import cn.common.ui.BasePopupWindow;
import cn.common.ui.fragment.BaseWorkerFragment;
import cn.common.utils.BitmapUtil;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.entity.DeviceInfo;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.LocationResponse;
import cn.protector.logic.http.response.NowDeviceInfoResponse;
import cn.protector.ui.helper.MainTitleHelper;
import cn.protector.utils.ToastUtil;

/**
 * 描述：定位页面
 *
 * @author jakechen on 2015/8/13.
 */
public class LocateFragment extends BaseWorkerFragment
        implements View.OnClickListener {
    private static final int MSG_BACK_REFRESH_DEVICE_INFO = 0;
    private static final int MSG_BACK_LOCATE = 1;
    private static final int MSG_UI_REFRESH_DEVICE_INFO = 0;
    private static final int MSG_UI_LOCATE = 1;

    public static LocateFragment newInstance() {
        return new LocateFragment();
    }

    private MainTitleHelper mTitleHelper;

    private MapView mMapView;

    private AMap mAMap;

    private Timer timer;
    private NowDeviceInfoResponse mNowDeviceInfo;
    private View mVBottom;
    private TextView mTvAddress;
    private TextView mTvAddressTime;
    private TextView mTvAddressTip;
    private ImageButton mIbBattery;
    private ImageButton mIbStep;

    @Override
    public void initView() {
        setContentView(R.layout.fragment_locate);
        mVBottom = findViewById(R.id.ll_bottom);
        mTvAddress = (TextView) findViewById(R.id.tv_locate_title);
        mTvAddressTime = (TextView) findViewById(R.id.tv_locate_content);
        mTvAddressTip = (TextView) findViewById(R.id.tv_locate_hint);
        mIbBattery = (ImageButton) findViewById(R.id.ib_battery);
        mIbStep = (ImageButton) findViewById(R.id.ib_steps);
        mMapView = (MapView) findViewById(R.id.mv_map);
        mVBottom.setVisibility(View.GONE);
        mTitleHelper = new MainTitleHelper(findViewById(R.id.fl_title),
                MainTitleHelper.STYLE_LOCATE);
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
        mAMap.setMyLocationRotateAngle(180);
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    /**
     * 打电话
     */
    private void callCustomerService(String phoneNum) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNum));
        startActivity(intent);
    }

    @Override
    protected void initEvent() {
        mIbBattery.setOnClickListener(this);
        mIbStep.setOnClickListener(this);
        findViewById(R.id.ib_mobile).setOnClickListener(this);
        findViewById(R.id.ib_locate).setOnClickListener(this);
        findViewById(R.id.ib_minus).setOnClickListener(this);
        findViewById(R.id.ib_plus).setOnClickListener(this);
        findViewById(R.id.ib_locate).setOnClickListener(this);
        findViewById(R.id.ll_bottom).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
        sendEmptyBackgroundMessage(MSG_BACK_REFRESH_DEVICE_INFO);
//            }
//        }, 0, AppConfig.REFRESH_POSITION_DEVICE_STATUS_SPIT_TIME);

    }


    @Override
    public void handleBackgroundMessage(Message msg) {
        super.handleBackgroundMessage(msg);
        switch (msg.what) {
            case MSG_BACK_REFRESH_DEVICE_INFO:
                refreshDeviceInfoTask();
                break;
            case MSG_BACK_LOCATE:
                LocateTask();
                break;
        }
    }

    private void LocateTask() {
        HttpRequest<LocationResponse> request = new HttpRequest<>(AppConfig.COM_GEO_LOCATION, LocationResponse.class);
        request.addParam("uc", InitSharedData.getUserCode());
        if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
            request.addParam("eid", DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId());
        }
        Message message = obtainUiMessage();
        message.what = MSG_UI_LOCATE;
        try {
            message.obj = request.request();
        } catch (AppException e) {
            e.printStackTrace();
        }
        message.sendToTarget();
    }

    private void refreshDeviceInfoTask() {
        HttpRequest<NowDeviceInfoResponse> request = new HttpRequest<>(AppConfig.GET_NOW_DATA, NowDeviceInfoResponse.class);
        request.addParam("uc", InitSharedData.getUserCode());
        if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
            request.addParam("eid", DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId());
        }
        Message message = obtainUiMessage();
        message.what = MSG_UI_REFRESH_DEVICE_INFO;
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
            case MSG_UI_REFRESH_DEVICE_INFO:
                handleRefreshDeviceInfoResponse(msg);
                break;
            case MSG_UI_LOCATE:
                if (msg.obj != null) {
                    LocationResponse locationresponse = (LocationResponse) msg.obj;
                    if (locationresponse != null) {
                        ToastUtil.show(locationresponse.getInfo());
                        if (locationresponse.getResult() == LocationResponse.SUCCESS) {
                            sendEmptyBackgroundMessage(MSG_UI_REFRESH_DEVICE_INFO);
                        }
                    } else {
                        ToastUtil.showError();
                    }
                } else {
                    ToastUtil.showError();
                }
                break;
        }
    }

    private void handleRefreshDeviceInfoResponse(Message msg) {
        if (msg.obj != null) {
            mVBottom.setVisibility(View.VISIBLE);
            mNowDeviceInfo = (NowDeviceInfoResponse) msg.obj;
            addMapMark(mNowDeviceInfo);
            if (mTvAddress != null && !TextUtils.isEmpty(mNowDeviceInfo.getAddress())) {
                mTvAddress.setText(mNowDeviceInfo.getAddress());
            }
            if (mTvAddressTime != null) {
                String mode = "GPS定位";
                if (mNowDeviceInfo.getPosiMode() == 2) {
                    mode = "网络定位";
                }
                String show = "定位模式：" + mode;
                if (mNowDeviceInfo.getPosiPrecision() > 0) {
                    show = show + "(精度到" + mNowDeviceInfo.getPosiPrecision() + "米)";
                }
                mTvAddressTime.setText(show);
            }
            if (mTvAddressTip != null && !TextUtils.isEmpty(mNowDeviceInfo.getLastActiveTime())) {
                mTvAddressTip.setText("更新时间：" + mNowDeviceInfo.getLastActiveTime());
            }
        }
    }

    /**
     * 添加到地图上
     *
     * @param info
     */
    private void addMapMark(NowDeviceInfoResponse info) {

        mAMap.setMyLocationRotateAngle(mAMap.getCameraPosition().bearing);// 设置小蓝点旋转角度
        LatLng latLng = new LatLng(info.getLat(), info.getLon());

        // 设置Marker的图标样式
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapUtil.decodeResource(
                R.drawable.img_head_girl1, (int) getDimension(R.dimen.locate_baby_avator),
                (int) getDimension(R.dimen.locate_baby_avator))));
        // 设置Marker点击之后显示的标题
        markerOptions.title(info.getAddress());
        // 设置Marker的坐标，为我们点击地图的经纬度坐标
        markerOptions.position(latLng);
        // 设置Marker的可见性
        markerOptions.visible(true);
        // 设置Marker是否可以被拖拽，这里先设置为false，之后会演示Marker的拖拽功能
        markerOptions.draggable(false);
        // 将Marker添加到地图上去
        mAMap.addMarker(markerOptions).setObject(info);
        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.5f));
    }

    @Override
    public void setupBroadcastActions(List<String> actions) {
        super.setupBroadcastActions(actions);
        actions.add(BroadcastActions.ACTION_MAIN_DEVICE_CHANGE);
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
        } else if (id == R.id.ib_locate) {
            sendEmptyBackgroundMessage(MSG_BACK_LOCATE);
        } else if (id == R.id.ib_battery) {
            showBatteryPopWindow();
        } else if (id == R.id.ib_mobile) {
            callCustomerService(mNowDeviceInfo.getPhoneNum());
        } else if (id == R.id.ib_steps) {
            showStepsPopWindow();
        }
    }

    private BasePopupWindow mBatteryPopWindow;
    private BasePopupWindow mStepsPopWindow;
    private TextView mTvBatteryTip;
    private TextView mTvSpeed;
    private TextView mTvSteps;
    private TextView mTvHell;

    private void showBatteryPopWindow() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            if (mBatteryPopWindow == null) {
                mBatteryPopWindow = new BasePopupWindow(getActivity());
                mBatteryPopWindow.onCreate();
                mBatteryPopWindow.setContentView(R.layout.pop_locate_battery);
                mTvBatteryTip = (TextView) mBatteryPopWindow.findViewById(R.id.tv_content);
            }
            int[] location = new int[2];
            mIbBattery.getLocationOnScreen(location);
            if (mNowDeviceInfo != null) {
                String show = "当前电量为" + mNowDeviceInfo.getEleQuantity() + "%";
                if (mNowDeviceInfo.getEleQuantity() < 1) {
                    show = "当前电量为小于1%，请及时充电哦";
                }
                mTvBatteryTip.setText(show);
                mBatteryPopWindow.showAtLocation(mIbBattery, Gravity.NO_GRAVITY, location[0] + mIbBattery.getWidth(), location[1] - mBatteryPopWindow.getHeight());
            }
        }
    }

    private void showStepsPopWindow() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            if (mStepsPopWindow == null) {
                mStepsPopWindow = new BasePopupWindow(getActivity());
                mStepsPopWindow.onCreate();
                mStepsPopWindow.setContentView(R.layout.pop_locate_steps);
                mTvSpeed = (TextView) mStepsPopWindow.findViewById(R.id.tv_speed);
                mTvSteps = (TextView) mStepsPopWindow.findViewById(R.id.tv_step);
                mTvHell = (TextView) mStepsPopWindow.findViewById(R.id.tv_hell);
            }
            int[] location = new int[2];
            mIbStep.getLocationOnScreen(location);
            if (mNowDeviceInfo != null) {
                mTvSpeed.setText("速度：" + mNowDeviceInfo.getSpeed() + "千米/小时");
                mTvSteps.setText("步数：" + mNowDeviceInfo.getStepNum() + "步");
                mTvHell.setText("压力：" + mNowDeviceInfo.getSolePress() + "G（脚掌）/" + mNowDeviceInfo.getHellPress() + "G（脚跟）");
                mStepsPopWindow.showAtLocation(mIbStep, Gravity.NO_GRAVITY, location[0] + mIbStep.getWidth(), location[1] - mStepsPopWindow.getHeight());
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroy();
        mMapView.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
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
