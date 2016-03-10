
package cn.protector.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import cn.common.AppException;
import cn.common.bitmap.core.ImageLoader;
import cn.common.bitmap.core.assist.FailReason;
import cn.common.bitmap.core.listener.ImageLoadingListener;
import cn.common.ui.BasePopupWindow;
import cn.common.ui.fragment.BaseWorkerFragment;
import cn.common.utils.BitmapUtil;
import cn.common.utils.DisplayUtil;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.entity.DeviceInfo;
import cn.protector.logic.entity.HourPointsInfo;
import cn.protector.logic.entity.PointInfo;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.CommonHasLoginStatusResponse;
import cn.protector.logic.http.response.HistoryResponse;
import cn.protector.logic.http.response.NowDeviceInfoResponse;
import cn.protector.ui.helper.CalendarHelper;
import cn.protector.ui.helper.DateUtil;
import cn.protector.ui.helper.MainTitleHelper;
import cn.protector.utils.ToastUtil;

/**
 * 描述：定位页面
 *
 * @author jakechen on 2015/8/13.
 */
public class HistoryFragment extends BaseWorkerFragment implements View.OnClickListener {
    private static final float ZOOM = 18f;

    private static final int MSG_BACK_LOAD_DATA = 0;

    private static final int MSG_UI_HIDE_TIME_TIP_POP = 0;
    private static final int MSG_UI_LOAD_DATA = 1;


    private MainTitleHelper mTitleHelper;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    private MapView mMapView;

    private AMap mAMap;

    private TextView mTvTime;

    private SeekBar mSbTime;

    private CalendarHelper mCalendarHelper;

    private View mVTitle;

    private HistoryResponse mHistoryResponse;
    private boolean isFirstIn = true;

    @Override
    public void initView() {
        setContentView(R.layout.fragment_history);
        mMapView = (MapView) findViewById(R.id.mv_map);
        mTvTime = (TextView) findViewById(R.id.tv_time_tip);
        mSbTime = (SeekBar) findViewById(R.id.sb_time);
        mVTitle = findViewById(R.id.fl_title);
        initMapView();
        mTitleHelper = new MainTitleHelper(mVTitle, MainTitleHelper.STYLE_HISTORY);
        mCalendarHelper = new CalendarHelper(getActivity());
        int[] today = DateUtil.getDateInt();
        mCalendarHelper.setDataList(today[0], today[1]);
        mSbTime.setMax(24);
        mSbTime.setProgress(0);
        mCalendarHelper.setSelectItem(today[0], today[1], today[2]);
        sendEmptyBackgroundMessage(MSG_BACK_LOAD_DATA);
    }

    private void initMapView() {
        mMapView.onCreate(mSavedInstanceState);// 此方法必须重写
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        UiSettings uiSettings = mAMap.getUiSettings();
        uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        uiSettings.setZoomControlsEnabled(false);
    }

    @Override
    protected void initEvent() {
        findViewById(R.id.ib_minus).setOnClickListener(this);
        findViewById(R.id.ib_plus).setOnClickListener(this);
        mCalendarHelper.setOnCalendarListener(new CalendarHelper.OnCalendarListener() {
            @Override
            public void onItemClick(int position, String date) {
                mSbTime.setProgress(0);
                sendEmptyBackgroundMessage(MSG_BACK_LOAD_DATA);
            }
        });
        mTitleHelper.setRightButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarHelper.showCalendar(mVTitle);
            }
        });
        findViewById(R.id.ll_slide_time).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mSbTime.setOnTouchListener(new View.OnTouchListener() {
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
        mSbTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mTvTime != null) {
                    mTvTime.setText("" + progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateUi(mHistoryResponse, seekBar.getProgress());
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
    public void handleBackgroundMessage(Message msg) {
        super.handleBackgroundMessage(msg);
        switch (msg.what) {
            case MSG_BACK_LOAD_DATA:
                loadDataTask();
                break;
        }
    }

    private void loadDataTask() {
        HttpRequest<HistoryResponse> request = new HttpRequest<>(AppConfig.GET_HISTORY_POSI,
                HistoryResponse.class);
        request.addParam("uc", InitSharedData.getUserCode());
        if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
            request.addParam("eid",
                    DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId());
        }
        request.addParam("hdate", mCalendarHelper.getPositionTime());
        Message message = obtainUiMessage();
        message.what = MSG_UI_LOAD_DATA;
        try {
            HistoryResponse response = request.request();
            response = sortResponse(response);
            message.obj = response;
        } catch (AppException e) {
            e.printStackTrace();
        }
        message.sendToTarget();
    }

    private HistoryResponse sortResponse(HistoryResponse response) {
        if (response != null) {
            ArrayList<HourPointsInfo> list = response.getList();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    for (int j = i + 1; j < list.size(); j++) {
                        if (list.get(j).getHour() < list.get(i).getHour()) {
                            HourPointsInfo tempI = list.get(i);
                            HourPointsInfo tempJ = list.get(j);
                            list.set(i, tempJ);
                            list.set(j, tempI);
                        }
                    }
                    response.setList(list);
                }
            }
        }
        return response;
    }

    @Override
    public void handleUiMessage(Message msg) {
        super.handleUiMessage(msg);
        switch (msg.what) {
            case MSG_UI_HIDE_TIME_TIP_POP:
                if (mTvTime != null) {
                    mTvTime.setVisibility(View.GONE);
                }
                break;
            case MSG_UI_LOAD_DATA:
                if (msg.obj != null) {
                    mHistoryResponse = (HistoryResponse) msg.obj;
                    int hour = getFirstHour(mHistoryResponse);
                    mSbTime.setProgress(hour);
                    updateUi(mHistoryResponse, hour);
                } else {
                    if (!isFirstIn) {
                        ToastUtil.showError();
                    }
                }
                break;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            isFirstIn = false;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private int getFirstHour(HistoryResponse info) {
        if (info != null && info.getList() != null && info.getList().size() > 0) {
            HourPointsInfo hourPointsInfo = info.getList().get(0);
            if (hourPointsInfo != null) {
                return hourPointsInfo.getHour();
            }
        }
        return 0;
    }

    private void updateUi(HistoryResponse info, int endHour) {
        mAMap.clear();
        addMapMark(info, endHour);
        addPolyline(info, endHour);
    }

    /**
     * 添加到地图上
     *
     * @param info
     */
    private void addMapMark(HistoryResponse info, int endHour) {
        if (info == null) {
            return;
        }
        ArrayList<PointInfo> list = getPointList(info, endHour);
        if (list != null && list.size() > 0) {
            ArrayList<MarkerOptions> markerOptionses = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                PointInfo pointInfo = list.get(i);
                if (pointInfo != null) {
                    MarkerOptions options = new MarkerOptions();
                    options.position(new LatLng(pointInfo.getLat(), pointInfo.getLon()));
                    markerOptionses.add(options);
                    if (i == 0 || (i == 0 && i == (list.size() - 1))) {
                        if (!TextUtils.isEmpty(pointInfo.getAddress())) {
                            options.title("起始点:" + pointInfo.getAddress());
                        } else {
                            options.title("起始点");
                        }
                    } else if (i == list.size() - 1) {
                        options.icon(BitmapDescriptorFactory
                                .fromBitmap(DeviceInfoHelper.getInstance().getAvatar()));
                        if (!TextUtils.isEmpty(pointInfo.getAddress())) {
                            options.title("终点点:" + pointInfo.getAddress());
                        } else {
                            options.title("终点");
                        }
                    } else {

                        if (pointInfo.getPosiMode() > 0) {
                            options.icon(BitmapDescriptorFactory
                                    .fromBitmap(BitmapUtil.decodeResource(R.drawable.map_dot_green)));
                        } else {
                            options.icon(BitmapDescriptorFactory
                                    .fromBitmap(BitmapUtil.decodeResource(R.drawable.map_dot_blue)));
                        }
                        if (!TextUtils.isEmpty(pointInfo.getAddress())) {
                            options.title(pointInfo.getAddress());
                        } else {
                            options.title("经过");
                        }
                    }
                }
            }
            mAMap.addMarkers(markerOptionses, true);
        }

    }


    private void addPolyline(HistoryResponse info, int endHour) {
        if (info == null || info != null && info.getList() == null
                || info != null && info.getList().size() < 1 || endHour == 0 || endHour > 24) {
            return;
        }
        ArrayList<PointInfo> list = getPointList(info, endHour);
        if (list != null && list.size() > 0) {
            PolylineOptions options = new PolylineOptions();
            options.color(getColor(R.color.title_background));
            options.width(getDimension(R.dimen.line_width));
            ArrayList<LatLng> latLngs = getLatlngListByPointList(list);
            if (latLngs != null && latLngs.size() > 0) {
                options.addAll(latLngs);
            }
            mAMap.addPolyline(options);
            mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(list.size() - 1), ZOOM));
        }
    }

    private ArrayList<LatLng> getLatlngListByPointList(ArrayList<PointInfo> list) {
        if (list != null && list.size() > 0) {
            ArrayList<LatLng> latLngs = new ArrayList<>();
            for (PointInfo info : list) {
                if (info != null) {
                    latLngs.add(new LatLng(info.getLat(), info.getLon()));
                }
            }
            return latLngs;
        }
        return null;
    }

    private ArrayList<PointInfo> getPointList(HistoryResponse info, int endHour) {
        if (info == null || info != null && info.getList() == null
                || info != null && info.getList().size() < 1 || endHour < 0 || endHour > 24) {
            return null;
        }
        ArrayList<PointInfo> list = new ArrayList<>();
        for (int i = 0; i < info.getList().size(); i++) {
            HourPointsInfo hourPointsInfo = info.getList().get(i);
            if (hourPointsInfo != null && hourPointsInfo.getHour() <= endHour) {
                ArrayList<PointInfo> pointInfos = hourPointsInfo.getPointList();
                if (pointInfos != null && pointInfos.size() > 0) {
                    for (PointInfo pointInfo : pointInfos) {
                        if (pointInfo != null) {
                            list.add(pointInfo);
                        }
                    }
                }
            }
        }
        return list;
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
            sendEmptyBackgroundMessage(MSG_BACK_LOAD_DATA);
        }
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
