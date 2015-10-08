
package cn.protector.ui.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;

import java.util.List;

import cn.common.utils.BitmapUtil;
import cn.protector.R;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.helper.MapViewHelper;

/**
 * 描述：位置修订页面
 *
 * @author jake
 * @since 2015/9/21 23:15
 */
public class LocationRegulateActivity extends CommonTitleActivity implements View.OnClickListener {

    private MapView mMapView;

    private TextView mTvLocateTitle;

    private TextView mTvAddress;

    private View mVBottom;

    private MapViewHelper mMapViewHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        mMapViewHelper = new MapViewHelper(mMapView);
        setSwipeBackEnable(false);
        setMyLocationStyle();
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_locate_regulate);
        setTitle(R.string.title_locate_regulate);
        mMapView = (MapView) findViewById(R.id.mv_map);
        mTvAddress = (TextView) findViewById(R.id.tv_address);
        mVBottom = findViewById(R.id.ll_bottom);
        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.iv_locate).setOnClickListener(this);
        mVBottom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mVBottom.setVisibility(View.GONE);
        mMapView.requestDisallowInterceptTouchEvent(true);
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
        if (id == R.id.iv_locate) {
            mMapViewHelper.startLocate();
        } else if (id == R.id.btn_save) {
            // TODO
            finish();
        }
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
        mMapViewHelper.getAMap().setMyLocationStyle(myLocationStyle);
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
