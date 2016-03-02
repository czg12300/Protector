package cn.protector.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.common.AppException;
import cn.common.bitmap.core.ImageLoader;
import cn.common.ui.adapter.BaseListAdapter;
import cn.common.ui.fragment.BaseWorkerFragment;
import cn.common.ui.widgt.ChangeThemeUtils;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.entity.DeviceInfo;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.PressResponse;
import cn.protector.logic.http.response.SportResponse;
import cn.protector.ui.activity.WebActivity;
import cn.protector.ui.activity.usercenter.BabyInfoActivity;
import cn.protector.ui.helper.MainTitleHelper;
import cn.protector.ui.widget.pulltorefresh.HealthListView;
import cn.protector.utils.ToastUtil;

/**
 * 描述：健康页面
 *
 * @author jakechen on 2015/8/13.
 */
public class HealthFragment extends BaseWorkerFragment implements View.OnClickListener {
  private static final int MSG_BACK_LOAD_DATA = 0;
  private static final int MSG_UI_LOAD_DATA = 0;

  public static HealthFragment newInstance() {
    return new HealthFragment();
  }

  private HealthListView mLvActivity;

  private View mVTitle;
  private TextView tvRefreshTime;
  private TextView tvHH;
  private TextView tvMT;
  private TextView tvCountStep;
  private TextView tvStepAdvice;
  private TextView tvFootTop;
  private TextView tvFootBottom;
  private TextView tvFootData;
  private TextView tvFootAdvice;

  private ImageView ivRefresh;
  private MainTitleHelper mTitleHelper;
  private RotateAnimation reFreshAnimation;
  private boolean isLoadingData = false;
  private SportResponse mSportResponse;
  private PressResponse mPressResponse;
  private SettingAdapter mSettingAdapter;

  @Override
  public void initView() {
    setContentView(R.layout.fragment_health);
    mVTitle = findViewById(R.id.fl_title);
    mLvActivity = (HealthListView) findViewById(R.id.lv_activity);
    mLvActivity.setDivider(new ColorDrawable(Color.TRANSPARENT));
    mLvActivity.setDividerHeight(0);
    mLvActivity.setTitle(mVTitle);
    mTitleHelper = new MainTitleHelper(mVTitle, MainTitleHelper.STYLE_HEALTH);
    initHeader(inflate(R.layout.header_health));
    mVTitle.getBackground().setAlpha(0);
    findViews();
  }

  private void findViews() {
    tvRefreshTime = (TextView) findViewById(R.id.tv_update_time);
    tvHH = (TextView) findViewById(R.id.tv_day_num);
    tvMT = (TextView) findViewById(R.id.tv_step_num);
    tvCountStep = (TextView) findViewById(R.id.tv_total_steps);
    tvStepAdvice = (TextView) findViewById(R.id.tv_eval_tip);
    tvFootTop = (TextView) findViewById(R.id.tv_foot_top_force);
    tvFootBottom = (TextView) findViewById(R.id.tv_foot_bottom_force);
    tvFootData = (TextView) findViewById(R.id.tv_foot_data);
    tvFootAdvice = (TextView) findViewById(R.id.tv_force_tip);
    ivRefresh = (ImageView) findViewById(R.id.iv_refresh);
  }

  private void initHeader(View header) {
    View headerTop = header.findViewById(R.id.ll_header);
    int paddingTop = (int) getDimension(R.dimen.title_height) + ChangeThemeUtils.getStatusBarHeight(getActivity());
    headerTop.setPadding(0, paddingTop, 0, 0);
    mLvActivity.addHeaderView(header);
//    mLvActivity.addFooterView(inflate(R.layout.footer_load));
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
      if (info != null) {
        if (!TextUtils.isEmpty(info.getNikeName())) {
          mTitleHelper.setTitle(info.getNikeName());
        }
        loadData();
      }
    }
  }

  @Override
  protected void initEvent() {
    findViewById(R.id.ll_refresh).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!isLoadingData) {
          isLoadingData = true;
          showLoad();
          Message message = obtainBackgroundMessage();
          message.what = MSG_BACK_LOAD_DATA;
          message.arg1 = 1;
          message.sendToTarget();
        }
      }
    });
  }


  private void showLoad() {
    if (reFreshAnimation == null) {
      reFreshAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
      reFreshAnimation.setDuration(800);
      reFreshAnimation.setInterpolator(new LinearInterpolator());
    }
    reFreshAnimation.setRepeatCount(-1);
    ivRefresh.clearAnimation();
    ivRefresh.startAnimation(reFreshAnimation);
  }

  private void hideLoad() {
    if (reFreshAnimation != null) {
      reFreshAnimation.setRepeatCount(0);
    }
  }

  @Override
  protected void initData() {
    mSettingAdapter = new SettingAdapter(getActivity(), new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (v.getTag() != null) {
          String url = (String) v.getTag();
          Bundle bundle = new Bundle();
          bundle.putString(WebActivity.KEY_TITLE, "热门活动");
          bundle.putString(WebActivity.KEY_URL, url);
          WebActivity.start(v.getContext(), bundle);
        }
      }
    });
    mLvActivity.setAdapter(mSettingAdapter);
    loadData();
  }

  private void loadData() {
    Message message = obtainBackgroundMessage();
    message.what = MSG_BACK_LOAD_DATA;
    message.arg1 = 0;
    message.sendToTarget();
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.ll_baby_info) {
      goActivity(BabyInfoActivity.class);
    }
  }

  @Override
  public void handleBackgroundMessage(Message msg) {
    super.handleBackgroundMessage(msg);
    switch (msg.what) {
      case MSG_BACK_LOAD_DATA:
        loadDataTask(msg.arg1);
        break;
    }
  }

  private void loadDataTask(int isRefresh) {
    boolean isSuccess = false;
    HttpRequest<SportResponse> sportRequest = new HttpRequest<>(AppConfig.GET_SPORT_STAT_DATA, SportResponse.class);
    sportRequest.addParam("uc", InitSharedData.getUserCode());
    if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
      sportRequest.addParam("eid", DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId());
    }
    try {
      mSportResponse = sportRequest.request();
      isSuccess = true;
    } catch (AppException e) {
      e.printStackTrace();

    }
    HttpRequest<PressResponse> pressRequest = new HttpRequest<>(AppConfig.GET_PRESS_STAT_DATA, PressResponse.class);
    pressRequest.addParam("uc", InitSharedData.getUserCode());
    if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
      pressRequest.addParam("eid", DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId());
    }
    try {
      mPressResponse = pressRequest.request();
      isSuccess = true;
    } catch (AppException e) {
      e.printStackTrace();
    }
    Message message = obtainUiMessage();
    message.what = MSG_UI_LOAD_DATA;
    message.arg1 = isRefresh;
    message.arg2 = isSuccess ? 1 : 0;
    message.sendToTarget();
  }

  @Override
  public void handleUiMessage(Message msg) {
    super.handleUiMessage(msg);
    switch (msg.what) {
      case MSG_UI_LOAD_DATA:
        isLoadingData = false;
        if (msg.arg1 > 0) {
          if (msg.arg2 > 0) {
            ToastUtil.show("数据刷新成功");
            handleLoadSuccess();
          } else {
            ToastUtil.showError();
          }
          hideLoad();
        } else {
          if (msg.arg2 > 0) {
            handleLoadSuccess();
          }
        }
        break;
    }
  }

  private void handleLoadSuccess() {
    tvRefreshTime.setText(new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis())) + "更新");
    if (mSportResponse != null) {
      if (!TextUtils.isEmpty(mSportResponse.getActivityPicture())) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("pic", mSportResponse.getActivityPicture());
        map.put("url", mSportResponse.getActivityUrl());
        list.add(map);
        mSettingAdapter.setData(list);
        mSettingAdapter.notifyDataSetChanged();
      }
      tvHH.setText("" + mSportResponse.getWorkDay());
      tvMT.setText("" + mSportResponse.getAvgStepNumber());
      tvCountStep.setText("共运动" + mSportResponse.getAllStepNumber() + "步");
      if (!TextUtils.isEmpty(mSportResponse.getMessage())) {
        tvStepAdvice.setText(mSportResponse.getMessage());
      }
    }
    if (mPressResponse != null) {
      tvFootTop.setText("" + mPressResponse.getFrontAvgPress());
      tvFootBottom.setText("" + mPressResponse.getBackAvgPress());
      tvFootData.setText("脚掌正常值：" + mPressResponse.getFrontNormalPress() + "     脚跟正常值：" + mPressResponse.getBackNormalPress());
      if (!TextUtils.isEmpty(mPressResponse.getMessage())) {
        tvFootAdvice.setText(mPressResponse.getMessage());
      }

    }
  }

  /**
   * grid view 的适配器
   */
  class SettingAdapter extends BaseListAdapter<HashMap<String, String>> {
    private View.OnClickListener listener;

    public SettingAdapter(Context context, View.OnClickListener listener) {
      super(context);
      this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ImageView imageView;
      if (convertView == null) {
        convertView = inflate(R.layout.item_hot_activity);
        imageView = (ImageView) convertView.findViewById(R.id.iv_adv);
        imageView.setOnClickListener(listener);
        convertView .setTag(imageView);
      } else {
        imageView = (ImageView) convertView.getTag();
      }
      HashMap<String, String> map = mDataList.get(position);
      if (map != null) {
        imageView.setTag(map.get("url"));
        if (!TextUtils.isEmpty(map.get("pic"))) {
          ImageLoader.getInstance().displayImage(map.get("pic"), imageView);
        }
      }
      return convertView;
    }


  }
}
