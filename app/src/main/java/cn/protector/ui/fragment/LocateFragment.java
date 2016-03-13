package cn.protector.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.common.AppException;
import cn.common.ui.BaseDialog;
import cn.common.ui.BasePopupWindow;
import cn.common.ui.fragment.BaseWorkerFragment;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.entity.ChatMessage;
import cn.protector.logic.entity.DeviceInfo;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.CommonHasLoginStatusResponse;
import cn.protector.logic.http.response.LocateInfoResponse;
import cn.protector.logic.http.response.LocateResponse;
import cn.protector.logic.http.response.NowDeviceInfoResponse;
import cn.protector.push.PushMessageReceiver;
import cn.protector.ui.helper.MainTitleHelper;
import cn.protector.ui.helper.TipDialogHelper;
import cn.protector.utils.ToastUtil;

/**
 * 描述：定位页面
 *
 * @author jakechen on 2015/8/13.
 */
public class LocateFragment extends BaseWorkerFragment implements View.OnClickListener, GeocodeSearch.OnGeocodeSearchListener {
    private static final int MSG_BACK_REFRESH_DEVICE_INFO = 0;

    private static final int MSG_BACK_LOCATE = 1;

    private static final int MSG_UI_REFRESH_DEVICE_INFO = 0;

    private static final int MSG_UI_LOCATE = 1;
    private static final int MSG_UI_HIDE_TOPTIP = 5;
    private static final int MSG_UI_AUTO_LOCATE = 4;

    private static final int MSG_BACK_AUTO_LOCATE = 3;
    private static final int MSG_UI_HIDE_TIP_DIALOG = 2;

    private GeocodeSearch geocoderSearch;
    private TipDialogHelper mTipDialogHelper;

    public static LocateFragment newInstance() {
        return new LocateFragment();
    }

    private MainTitleHelper mTitleHelper;

    private MapView mMapView;

    private AMap mAMap;

    private Timer timer;

//    private NowDeviceInfoResponse mNowDeviceInfo;

    private View mVBottom;

    private TextView mTvAddress;

    private TextView mTvAddressTime;

    private TextView mTvAddressTip;

    private ImageButton mIbBattery;

    private ImageButton mIbStep;

    private BasePopupWindow mBatteryPopWindow;

    private BasePopupWindow mStepsPopWindow;

    private TextView mTvBatteryTip;

    private TextView mTvSpeed;

    private TextView mTvSteps;

    private TextView mTvHell;

    private long geocodeSearchId;
    private View vTopTip;
    private TextView tvTip;
    private TextView tvCountdown;

    @Override
    public void initView() {
        setContentView(R.layout.fragment_locate);
        mVBottom = findViewById(R.id.ll_bottom);
        vTopTip = findViewById(R.id.ll_tip);
        mTvAddress = (TextView) findViewById(R.id.tv_locate_title);
        tvTip = (TextView) findViewById(R.id.tv_tip);
        tvCountdown = (TextView) findViewById(R.id.tv_countdown);
        mTvAddressTime = (TextView) findViewById(R.id.tv_locate_content);
        mTvAddressTip = (TextView) findViewById(R.id.tv_locate_hint);
        mIbBattery = (ImageButton) findViewById(R.id.ib_battery);
        mIbStep = (ImageButton) findViewById(R.id.ib_steps);
        mMapView = (MapView) findViewById(R.id.mv_map);
        mVBottom.setVisibility(View.GONE);
        mTitleHelper = new MainTitleHelper(findViewById(R.id.fl_title), MainTitleHelper.STYLE_LOCATE);
        initMapView();
        vTopTip.setVisibility(View.GONE);
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
        geocoderSearch = new GeocodeSearch(getActivity());
        geocoderSearch.setOnGeocodeSearchListener(this);
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
        findViewById(R.id.ib_maplisten).setOnClickListener(this);
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
        mAMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                } else {
                    marker.showInfoWindow();
                }
                return true;
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        //每隔一段时间就去获取设备信息
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                sendEmptyBackgroundMessage(MSG_BACK_AUTO_LOCATE);
//            }
//        }, 0, AppConfig.AUTO_LOCATE_TIME);
        mTipDialogHelper = new TipDialogHelper(getActivity());
        loadDeviceInfo();
    }

    private void loadDeviceInfo() {
        sendEmptyBackgroundMessage(MSG_BACK_REFRESH_DEVICE_INFO);
        mTipDialogHelper.showLoadingTip("正在获取当前设备信息");
    }

    @Override
    public void handleBackgroundMessage(Message msg) {
        super.handleBackgroundMessage(msg);
        switch (msg.what) {
            case MSG_BACK_REFRESH_DEVICE_INFO:
                refreshDeviceInfoTask();
                break;
            case MSG_BACK_LOCATE:
                locateTask();
                break;
            case MSG_BACK_AUTO_LOCATE:
                autoLocateTask();
                break;
        }
    }

    private void autoLocateTask() {
        HttpRequest<LocateInfoResponse> request = new HttpRequest<>(AppConfig.GET_POSITIONDATA, LocateInfoResponse.class);
        request.addParam("uc", InitSharedData.getUserCode());
        if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
            request.addParam("eid", DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId());
        }
        Message message = obtainUiMessage();
        message.what = MSG_UI_AUTO_LOCATE;
        try {
            message.obj = request.request();
        } catch (AppException e) {
            e.printStackTrace();
        }
        message.sendToTarget();
    }

    private void locateTask() {
        HttpRequest<LocateResponse> request = new HttpRequest<>(AppConfig.COM_GEO_LOCATION, LocateResponse.class);
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
            case MSG_UI_HIDE_TIP_DIALOG:
                if (mTipDialogHelper != null) {
                    mTipDialogHelper.hideDialog();
                }
                break;
            case MSG_UI_HIDE_TOPTIP:
                hideTopTip();
                break;
            case MSG_UI_REFRESH_DEVICE_INFO:
                handleRefreshDeviceInfoResponse(msg);
                break;
            case MSG_UI_AUTO_LOCATE:
                if (msg.obj != null) {
                    handleAutoLocate((LocateInfoResponse) msg.obj);
                }
                break;
            case MSG_UI_LOCATE:
                if (msg.obj != null) {
                    LocateResponse response = (LocateResponse) msg.obj;
                    if (response.getResult() > 0) {
                        showTopTip(response.getState());
                    } else {
                        if (!TextUtils.isEmpty(response.getInfo())) {
                            ToastUtil.show(response.getInfo());
                        } else {
                            ToastUtil.showError();
                        }
                    }
                } else {
                    ToastUtil.showError();
                }
                break;
        }
    }

    private CountDownTimer locateTimer;

    private void showTopTip(int type) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            if (type == 4 || type == 5) {
                if (vTopTip != null) {
                    vTopTip.setVisibility(View.VISIBLE);
                }
                if (type == 4) {
                    locateTimer = new CountDownTimer(60 * 1000L, 1000L) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long time = millisUntilFinished / 1000;
                            if (time < 20) {
                                if (tvTip != null) {
                                    tvTip.setText("正在上传信息");
                                }
                            } else if (time < 40) {
                                if (tvTip != null) {
                                    tvTip.setText("正在收集定位信息");
                                }
                            } else if (time < 60) {
                                if (tvTip != null) {
                                    tvTip.setText("正在呼叫设备");
                                }
                            }
                            if (tvCountdown != null) {
                                tvCountdown.setText(time + "秒");
                            }

                        }

                        @Override
                        public void onFinish() {
                            if (tvTip != null) {
                                tvTip.setText("获取失败，请重试");
                            }
                            if (tvCountdown != null) {
                                tvCountdown.setText("");
                            }
                            sendEmptyUiMessageDelayed(MSG_UI_HIDE_TOPTIP, 500);

                        }
                    };
                    locateTimer.start();
                } else if (type == 5) {
                    locateTimer = new CountDownTimer(20 * 1000L, 1000L) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long time = millisUntilFinished / 1000;
                            if (time < 10) {
                                if (tvTip != null) {
                                    tvTip.setText("正在上传信息");
                                }
                            } else if (time < 20) {
                                if (tvTip != null) {
                                    tvTip.setText("正在收集定位信息");
                                }
                            }
                            if (tvCountdown != null) {
                                tvCountdown.setText(time + "秒");
                            }
                        }

                        @Override
                        public void onFinish() {
                            if (tvTip != null) {
                                tvTip.setText("获取失败，请重试");
                            }
                            if (tvCountdown != null) {
                                tvCountdown.setText("");
                            }
                            sendEmptyUiMessageDelayed(MSG_UI_HIDE_TOPTIP, 500);
                        }
                    };
                    locateTimer.start();
                }
            } else {
                ToastUtil.show("定位失败");
            }
        }
    }

    private void hideTopTip() {
        if (vTopTip != null) {
            vTopTip.setVisibility(View.GONE);
        }
        if (locateTimer != null) {
            locateTimer.cancel();
            locateTimer = null;
        }
    }


    private void handleAutoLocate(LocateInfoResponse response) {
        NowDeviceInfoResponse info = DeviceInfoHelper.getInstance().getNowDeviceInfo();
        if (info == null) {
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
        addMapMark(info);
        updateBottomUi(info);
    }

    private void handleRefreshDeviceInfoResponse(Message msg) {
        if (msg.obj != null) {
            sendEmptyUiMessage(MSG_UI_HIDE_TIP_DIALOG);
            handleUpdateNowDeviceInfo((NowDeviceInfoResponse) msg.obj);
        } else {
            mTipDialogHelper.showErrorTip("设备信息获取失败，请点击定位重试");
            sendEmptyUiMessageDelayed(MSG_UI_HIDE_TIP_DIALOG, 500);
        }
    }

    private void handleUpdateNowDeviceInfo(NowDeviceInfoResponse info) {
        DeviceInfoHelper.getInstance().setNowDeviceInfo(info);
        if (mVBottom.getVisibility() != View.VISIBLE) {
            mVBottom.setVisibility(View.VISIBLE);
        }
        addMapMark(info);
        updateBottomUi(info);
        if (TextUtils.isEmpty(info.getAddress())) {
            searchAddressByPosi();
        }
    }


    private void updateBottomUi(NowDeviceInfoResponse info) {
        if (info == null) {
            return;
        }
        if (mTvAddress != null && !TextUtils.isEmpty(info.getAddress())) {
            mTvAddress.setText(info.getAddress());
        }
        if (mTvAddressTime != null) {
            String mode = "网络定位";
            if (info.getPosiMode() > 0) {
                mode = "GPS定位";
            }
            String show = "定位模式：" + mode;
            if (info.getPosiPrecision() > 0) {
                show = show + "(精度到" + info.getPosiPrecision() + "米)";
            }
            mTvAddressTime.setText(show);
        }
        if (mTvAddressTip != null && !TextUtils.isEmpty(info.getLastActiveTime())) {
            mTvAddressTip.setText("更新时间：" + info.getLastActiveTime());
        }
    }


    /**
     * 添加到地图上
     *
     * @param info
     */
    private void addMapMark(final NowDeviceInfoResponse info) {
        if (info == null) {
            return;
        }
        mAMap.clear();
        // 设置Marker的图标样式
        LatLng latLng = new LatLng(info.getLat(), info.getLon());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(DeviceInfoHelper.getInstance().getAvatar()));
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
        mAMap.addCircle(new CircleOptions().center(latLng)
                .radius(info.getPosiPrecision()).strokeColor(getColor(R.color.blue_03a9f4)).fillColor(getColor(R.color.blue_3003a9f4))
                .strokeWidth(6));
        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.5f));
    }


    @Override
    public void setupBroadcastActions(List<String> actions) {
        super.setupBroadcastActions(actions);
        actions.add(BroadcastActions.ACTION_MAIN_DEVICE_CHANGE);
        actions.add(BroadcastActions.ACTION_UPDATE_POSITION_DEVICE_INFO);
        actions.add(BroadcastActions.ACTION_PUSH_REAL_TIME_LOCATE_DATA);
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
            loadDeviceInfo();
        } else if (TextUtils.equals(action, BroadcastActions.ACTION_UPDATE_POSITION_DEVICE_INFO)) {
            sendEmptyBackgroundMessage(MSG_BACK_REFRESH_DEVICE_INFO);
            mTitleHelper.refreshData();
            DeviceInfo info = DeviceInfoHelper.getInstance().getPositionDeviceInfo();
            if (info != null && !TextUtils.isEmpty(info.getNikeName())) {
                mTitleHelper.setTitle(info.getNikeName());
            }
        } else if (TextUtils.equals(action, BroadcastActions.ACTION_PUSH_REAL_TIME_LOCATE_DATA)) {
            NowDeviceInfoResponse info = (NowDeviceInfoResponse) intent.getSerializableExtra(PushMessageReceiver.KEY_EVENT_MESSAGE);
            if (info != null) {
                handleUpdateNowDeviceInfo(info);
            }
            sendEmptyUiMessage(MSG_UI_HIDE_TOPTIP);
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
            showLocateDialog();
        } else if (id == R.id.ib_battery) {
            showBatteryPopWindow();
        } else if (id == R.id.ib_maplisten) {
            showCallDialog();
        } else if (id == R.id.ib_steps) {
            showStepsPopWindow();
        } else if (id == R.id.ib_mobile) {
            ToastUtil.show("完善中");
        }
    }

    private void searchAddressByPosi() {
        NowDeviceInfoResponse info = DeviceInfoHelper.getInstance().getNowDeviceInfo();
        if (info == null) {
            return;
        }
        geocodeSearchId = info.getId();
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(info.getLon(), info.getLat()), 20, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    private void showBatteryPopWindow() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            if (mBatteryPopWindow == null) {
                mBatteryPopWindow = new BasePopupWindow(getActivity());
                mBatteryPopWindow.setContentView(R.layout.pop_locate_battery);
                mTvBatteryTip = (TextView) mBatteryPopWindow.findViewById(R.id.tv_content);
            }
            int[] location = new int[2];
            mIbBattery.getLocationOnScreen(location);
            NowDeviceInfoResponse info = DeviceInfoHelper.getInstance().getNowDeviceInfo();
            if (info != null) {
                String show = "当前电量为" + info.getEleQuantity() + "%";
                if (info.getEleQuantity() < 1) {
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
                mStepsPopWindow.setContentView(R.layout.pop_locate_steps);
                mTvSpeed = (TextView) mStepsPopWindow.findViewById(R.id.tv_speed);
                mTvSteps = (TextView) mStepsPopWindow.findViewById(R.id.tv_step);
                mTvHell = (TextView) mStepsPopWindow.findViewById(R.id.tv_hell);
            }
            int[] location = new int[2];
            mIbStep.getLocationOnScreen(location);
            NowDeviceInfoResponse info = DeviceInfoHelper.getInstance().getNowDeviceInfo();
            if (info != null) {
                mTvSpeed.setText("速度：" + info.getSpeed() + "千米/小时");
                mTvSteps.setText("步数：" + info.getStepNum() + "步");
                mTvHell.setText("压力：" + info.getSolePress() + "G（脚掌）/" + info.getHellPress() + "G（脚跟）");
                mStepsPopWindow.showAtLocation(mIbStep, Gravity.NO_GRAVITY, location[0] + mIbStep.getWidth(), location[1] - mStepsPopWindow.getHeight());
            }
        }
    }

    private void showLocateDialog() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            final BaseDialog dialog = new BaseDialog(getActivity());
            dialog.setWindow(R.style.alpha_animation, 0.3f);
            dialog.setContentView(R.layout.dialog_title_content);
            ((TextView) dialog.findViewById(R.id.tv_title)).setText("系统将获取设备最新位置，定位后需要一定时间更新数据\n" + "确定要定位吗");
            dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendEmptyBackgroundMessage(MSG_BACK_LOCATE);
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        }
    }

    private void showCallDialog() {
        final NowDeviceInfoResponse info = DeviceInfoHelper.getInstance().getNowDeviceInfo();
        if (getActivity() != null && !getActivity().isFinishing() && info != null) {
            final BaseDialog dialog = new BaseDialog(getActivity());
            dialog.setWindow(R.style.alpha_animation, 0.3f);
            dialog.setContentView(R.layout.dialog_title_content);
            ((TextView) dialog.findViewById(R.id.tv_title)).setText("设备电话号码为：" + info.getPhoneNum() + "\n确定要呼叫设备吗?");
            dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callCustomerService(info.getPhoneNum());
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
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

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int rCode) {
        NowDeviceInfoResponse info = DeviceInfoHelper.getInstance().getNowDeviceInfo();
        if (info != null && geocodeSearchId == info.getId()) {
            info.setAddress(regeocodeResult.getRegeocodeAddress().getFormatAddress());
        }
        DeviceInfoHelper.getInstance().setNowDeviceInfo(info);
        updateBottomUi(info);

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
    }
}
