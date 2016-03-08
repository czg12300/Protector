
package cn.protector.ui.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
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

import cn.common.AppException;
import cn.common.ui.BaseDialog;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.entity.FenceInfo;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.CommonResponse;
import cn.protector.logic.http.response.NowDeviceInfoResponse;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.widget.ImageEditText;
import cn.protector.ui.widget.StatusView;
import cn.protector.utils.ToastUtil;

/**
 * 描述:新增围栏页面
 *
 * @author jakechen
 * @since 2015/9/22 11:40
 */
public class AddFenceActivity extends CommonTitleActivity implements GeocodeSearch.OnGeocodeSearchListener {
    private StatusView mStatusView;
    private static final int MSG_BACK_REFRESH_DEVICE_INFO = 0;


    private static final int MSG_UI_REFRESH_DEVICE_INFO = 0;
    private static final int MSG_BACK_SAVE = 1;


    private static final int MSG_UI_SAVE = 2;

    private static final int MSG_UI_HIDE_TIME_TIP_POP = 1;
    private MapView mMapView;

    private TextView mTvHint;

    private TextView mTvAddress;

    private AMap mAMap;

    private View mVBottom;

    private View mVTop;
    private LatLng mLatLng;
    private int radius = 10;
    private SeekBar seekBar;
    private TextView mTvTime;
    private NowDeviceInfoResponse mNowDeviceInfoResponse;
    private GeocodeSearch geocoderSearch;
    private Button btnSave;
    private String name = "";

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
    }

    @Override
    public void initView() {
        mStatusView = new StatusView(this);
        mStatusView.setContentView(R.layout.activity_add_fence);
        setContentView(mStatusView);
        setTitle("新增围栏");
        mTvTime = (TextView) findViewById(R.id.tv_time_tip);
        seekBar = (SeekBar) findViewById(R.id.sb_time);
        mMapView = (MapView) findViewById(R.id.mv_map);
        mTvHint = (TextView) findViewById(R.id.tv_hint);
        mTvAddress = (TextView) findViewById(R.id.tv_address);
        btnSave = (Button) findViewById(R.id.btn_save);
        mVBottom = findViewById(R.id.ll_bottom);
        mVTop = findViewById(R.id.ll_slide_time);
        mVBottom.setVisibility(View.VISIBLE);
        mMapView.requestDisallowInterceptTouchEvent(true);
        seekBar.setProgress(10);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        mStatusView.setStatusListener(new StatusView.StatusListener() {
            @Override
            public void reLoadData() {
                loadData();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditNameDialog();
            }
        });
        mVBottom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mTvTime.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        sendEmptyUiMessageDelayed(MSG_UI_HIDE_TIME_TIP_POP, 500);
                        break;

                }
                return false;
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radius = progress * 10;
                if (mTvTime != null) {
                    mTvTime.setText(radius + "");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                refreshCircle();
            }
        });
        mVTop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);

    }

    private void showEditNameDialog() {
        if (!isFinishing()) {
            final BaseDialog dialog = new BaseDialog(this);
            dialog.setWindow(R.style.alpha_animation, 0.3f);
            dialog.setContentView(R.layout.dialog_edit_baby_nikename);
            TextView text = (TextView) dialog.findViewById(R.id.tv_title);
            text.setText("请输入围栏名称");
            final ImageEditText ev = (ImageEditText) dialog.findViewById(R.id.ev_baby_nickname);
            ev.setHint("请输入围栏名称");
            dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(ev.getText())) {
                        ToastUtil.show("请输入围栏名称");
                    } else {
                        name = ev.getText().toString();
                        btnSave.setEnabled(false);
                        btnSave.setText("保存中...");
                        sendEmptyBackgroundMessage(MSG_BACK_SAVE);
                        dialog.dismiss();
                    }

                }
            });
            dialog.show();
        }
    }

    @Override
    protected void initData() {
        super.initData();
        loadData();
    }

    private void loadData() {
        mStatusView.showLoadingView();
        sendEmptyBackgroundMessage(MSG_BACK_REFRESH_DEVICE_INFO);
    }

    @Override
    public void handleBackgroundMessage(Message msg) {
        super.handleBackgroundMessage(msg);
        switch (msg.what) {
            case MSG_BACK_REFRESH_DEVICE_INFO:
                loadDeviceInfoTask();
                break;
            case MSG_BACK_SAVE:
                saveTask();
                break;
        }
    }


    private void saveTask() {
        HttpRequest<CommonResponse> request = new HttpRequest<>(AppConfig.SET_ELECFENCE,
                CommonResponse.class);
        request.addParam("uc", InitSharedData.getUserCode());
        request.addParam("rid", "0");
        request.addParam("lon", "" + mLatLng.longitude);
        request.addParam("name", name);
        request.addParam("lat", "" + mLatLng.latitude);
        request.addParam("r", "" + radius);
        if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
            request.addParam("eid",
                    DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId());
        }
        Message message = obtainUiMessage();
        message.what = MSG_UI_SAVE;
        try {
            message.obj = request.request();
        } catch (AppException e) {
            e.printStackTrace();
        }
        message.sendToTarget();
    }

    private void loadDeviceInfoTask() {
        HttpRequest<NowDeviceInfoResponse> request = new HttpRequest<>(AppConfig.GET_NOW_DATA,
                NowDeviceInfoResponse.class);
        request.addParam("uc", InitSharedData.getUserCode());
        if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
            request.addParam("eid",
                    DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId());
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
                handleDeviceInfo(msg.obj);
                break;
            case MSG_UI_HIDE_TIME_TIP_POP:
                if (mTvTime != null) {
                    mTvTime.setVisibility(View.GONE);
                }
                break;
            case MSG_UI_SAVE:
                handleSave(msg.obj);
                break;
        }
    }

    private void handleSave(Object obj) {
        btnSave.setEnabled(true);
        btnSave.setText("保存");
        if (obj != null) {
            CommonResponse rs = (CommonResponse) obj;
            if (rs.getResult() > 0) {
                sendBroadcast(new Intent(BroadcastActions.ACTION_ADD_FENCE_SUCCESS));
                ToastUtil.show("保存成功");
                finish();
            } else {
                if (!TextUtils.isEmpty(rs.getInfo())) {
                    ToastUtil.show(rs.getInfo());
                } else {
                    ToastUtil.show("保存失败");
                }
            }
        } else {
            ToastUtil.show("保存失败");
        }
    }

    private void handleDeviceInfo(Object obj) {
        if (obj != null) {
            mStatusView.showContentView();
            mNowDeviceInfoResponse = (NowDeviceInfoResponse) obj;
            refreshCircle();
        } else {
            mStatusView.showFailView();
        }
    }


    /**
     * 添加到地图上
     */
    private void refreshCircle() {
        if (mNowDeviceInfoResponse == null) {
            return;
        }
        if (mLatLng == null) {
            mLatLng = new LatLng(mNowDeviceInfoResponse.getLat(), mNowDeviceInfoResponse.getLon());
            mTvAddress.setText("" + mNowDeviceInfoResponse.getAddress());
        }
        mTvHint.setText("(半径：" +
                radius + "米、" + "经度：" + mLatLng.latitude + "、纬度：" + mLatLng.longitude + ")");
        mAMap.clear();
        // 设置Marker的图标样式
        mAMap.setMyLocationRotateAngle(mAMap.getCameraPosition().bearing);// 设置小蓝点旋转角度
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(DeviceInfoHelper.getInstance().getAvatar()));
        // 设置Marker点击之后显示的标题
        markerOptions.title(mNowDeviceInfoResponse.getAddress());
        // 设置Marker的坐标，为我们点击地图的经纬度坐标
        markerOptions.position(mLatLng);
        // 设置Marker的可见性
        markerOptions.visible(true);
        // 设置Marker是否可以被拖拽，这里先设置为false，之后会演示Marker的拖拽功能
        markerOptions.draggable(true);
        // 将Marker添加到地图上去
        mAMap.addMarker(markerOptions).setObject(mNowDeviceInfoResponse);
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
                geocoderSearch.getFromLocationAsyn(new RegeocodeQuery(point, 200, GeocodeSearch.AMAP));
            }
        });
        mAMap.addCircle(new CircleOptions().center(mLatLng)
                .radius(radius).strokeColor(getColor(R.color.red_ef0b0b)).fillColor(getColor(R.color.trans))
                .strokeWidth(6));
        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 15.5f));
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

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        mTvAddress.setText("" + regeocodeResult.getRegeocodeAddress().getFormatAddress());
        refreshCircle();

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}
//    @Override
//    protected View getTitleLayoutView() {
//        View vTitle = inflate(R.layout.title_add_fence);
//        mIvBack = (ImageView) vTitle.findViewById(R.id.iv_back);
//        mTvTitle = (TextView) vTitle.findViewById(R.id.tv_title);
//        mIvBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//                onBack();
//            }
//        });
//        vTitle.findViewById(R.id.iv_search).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                goActivity(SearchAreaActivity.class);
//            }
//        });
//
//        return vTitle;
//    }