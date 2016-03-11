package cn.protector.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.common.AppException;
import cn.common.bitmap.core.ImageLoader;
import cn.common.ui.BaseDialog;
import cn.common.ui.adapter.BaseListAdapter;
import cn.common.ui.fragment.BaseWorkerFragment;
import cn.common.utils.DisplayUtil;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.entity.DeviceInfo;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.CommonHasLoginStatusResponse;
import cn.protector.ui.activity.setting.CareStaffActivity;
import cn.protector.ui.activity.setting.DeviceManageActivity;
import cn.protector.ui.activity.setting.FenceSetActivity;
import cn.protector.ui.activity.setting.LocateModeActivity;
import cn.protector.ui.activity.setting.LocationRegulateActivity;
import cn.protector.ui.activity.setting.ModifyPwActivity;
import cn.protector.ui.activity.setting.QACodeActivity;
import cn.protector.ui.activity.usercenter.BabyInfoActivity;
import cn.protector.ui.activity.usercenter.LoginActivity;
import cn.protector.ui.helper.MainTitleHelper;
import cn.protector.utils.ToastUtil;

/**
 * 描述：设置页面
 *
 * @author jakechen on 2015/8/13.
 */
public class SettingFragment extends BaseWorkerFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
  private static final int MSG_BACK_SHUT_DOWN = 0;

  private static final int MSG_UI_SHUT_DOWN = 0;

  private MainTitleHelper mTitleHelper;


  public static SettingFragment newInstance() {
    return new SettingFragment();
  }

  private GridView mGvSetting;

  private View mVBabyInfo;

  private Button mBtnExit;

  private TextView tvName;

  private TextView tvSteps;

  private ImageView ivAvatar;

  @Override
  public void initView() {
    setContentView(R.layout.fragment_setting);
    mGvSetting = (GridView) findViewById(R.id.gv_setting);
    mVBabyInfo = findViewById(R.id.ll_baby_info);
    ivAvatar = (ImageView) findViewById(R.id.riv_avator);
    mBtnExit = (Button) findViewById(R.id.btn_exit);
    tvName = (TextView) findViewById(R.id.tv_baby_name);
    tvSteps = (TextView) findViewById(R.id.tv_baby_steps);
    mTitleHelper = new MainTitleHelper(findViewById(R.id.fl_title), MainTitleHelper.STYLE_SETTING);

  }

  @Override
  protected void initEvent() {
    mGvSetting.setOnItemClickListener(this);
    mVBabyInfo.setOnClickListener(this);
    mBtnExit.setOnClickListener(this);
    findViewById(R.id.tv_modify_pw).setOnClickListener(this);
    findViewById(R.id.tv_device_manage).setOnClickListener(this);
  }

  @Override
  protected void initData() {
    mGvSetting.setAdapter(new SettingAdapter(getActivity(), getListSetting()));
    updateTop();
  }

  private void updateTop() {
    DeviceInfo info = DeviceInfoHelper.getInstance().getPositionDeviceInfo();
    if (info != null) {
      if (info != null && !TextUtils.isEmpty(info.getNikeName())) {
        mTitleHelper.setTitle(info.getNikeName());
      }
      ImageLoader.getInstance().displayImage(info.getAvatar(), ivAvatar);
      String text = "";
      if (!TextUtils.isEmpty(info.getNikeName())) {
        text = info.getNikeName();
      }
      tvName.setText(text);
    }
  }

  /**
   * 设置grid view 的设置选项
   *
   * @return
   */
  private List<ItemInfo> getListSetting() {
    List<ItemInfo> list = new ArrayList<ItemInfo>();
    list.add(new ItemInfo(R.drawable.ico_family_setting, getString(R.string.family_setting)));
    list.add(new ItemInfo(R.drawable.ico_saftey_setting, getString(R.string.saftey_setting)));
    list.add(new ItemInfo(R.drawable.ico_qrcode_setting, getString(R.string.qrcode_setting)));
    list.add(new ItemInfo(R.drawable.ico_position_setting, getString(R.string.position_setting)));
    list.add(new ItemInfo(R.drawable.ico_location_setting, getString(R.string.location_setting)));
    list.add(new ItemInfo(R.drawable.ico_shutdown_setting, getString(R.string.shutdown_setting)));
    return list;
  }

  @Override
  public void setupBroadcastActions(List<String> actions) {
    super.setupBroadcastActions(actions);
    actions.add(BroadcastActions.ACTION_MAIN_DEVICE_CHANGE);
    actions.add(BroadcastActions.ACTION_UPDATE_POSITION_DEVICE_INFO);
  }

  @Override
  public void handleBroadcast(Context context, Intent intent) {
    super.handleBroadcast(context, intent);
    String action = intent.getAction();
    if (TextUtils.equals(action, BroadcastActions.ACTION_MAIN_DEVICE_CHANGE)) {
      updateTop();
    }else if (TextUtils.equals(action, BroadcastActions.ACTION_UPDATE_POSITION_DEVICE_INFO)) {
      mTitleHelper.refreshData();
      DeviceInfo info = DeviceInfoHelper.getInstance().getPositionDeviceInfo();
      if (info != null && !TextUtils.isEmpty(info.getNikeName())) {
        mTitleHelper.setTitle(info.getNikeName());
      }
      updateTop();
    }
  }

  @Override
  public void handleUiMessage(Message msg) {
    super.handleUiMessage(msg);
    switch (msg.what) {
      case MSG_UI_SHUT_DOWN:
        handleShutDown(msg.obj);
        break;
    }
  }

  private void handleShutDown(Object obj) {
    if (obj != null) {
      CommonHasLoginStatusResponse response = (CommonHasLoginStatusResponse) obj;
      if (response.getResult() == CommonHasLoginStatusResponse.SUCCESS) {
        ToastUtil.show("关机成功");
      } else {
        ToastUtil.show(response.getInfo());
      }
    } else {
      ToastUtil.showError();
    }
  }

  @Override
  public void handleBackgroundMessage(Message msg) {
    super.handleBackgroundMessage(msg);
    switch (msg.what) {
      case MSG_BACK_SHUT_DOWN:
        shutDownTask();
        break;
    }
  }

  private void shutDownTask() {
    HttpRequest<CommonHasLoginStatusResponse> request = new HttpRequest<>(AppConfig.COM_SHUT_DOWN, CommonHasLoginStatusResponse.class);
    request.addParam("uc", InitSharedData.getUserCode());
    if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
      request.addParam("eid", DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId());
    }
    Message message = obtainUiMessage();
    message.what = MSG_UI_SHUT_DOWN;
    try {
      message.obj = request.request();
    } catch (AppException e) {
      e.printStackTrace();
    }
    message.sendToTarget();

  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.ll_baby_info) {
      goActivity(BabyInfoActivity.class);
    } else if (id == R.id.tv_modify_pw) {
      goActivity(ModifyPwActivity.class);
    } else if (id == R.id.tv_device_manage) {
      goActivity(DeviceManageActivity.class);
    } else if (id == R.id.btn_exit) {
      InitSharedData.setUserId("");
      getActivity().finish();
      goActivity(LoginActivity.class);
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    // TODO
    switch (position) {
      case 0:
        goActivity(CareStaffActivity.class);
        break;
      case 1:
        goActivity(FenceSetActivity.class);
        break;
      case 2:
        goActivity(QACodeActivity.class);
        break;
      case 3:
        goActivity(LocationRegulateActivity.class);
        break;
      case 4:
        goActivity(LocateModeActivity.class);
        break;
      case 5:
        showShutdownDialog();
        break;
    }
  }

  /**
   * 显示远程关机的弹窗
   */
  private void showShutdownDialog() {
    if (getActivity() != null && !getActivity().isFinishing()) {
      final BaseDialog dialog = new BaseDialog(getActivity());
      dialog.setWindow(R.style.alpha_animation, 0.3f);
      dialog.setContentView(R.layout.dialog_title_content);
      ((TextView) dialog.findViewById(R.id.tv_title)).setText("确定要远程关机吗？");
      dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          sendEmptyBackgroundMessage(MSG_BACK_SHUT_DOWN);
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

  /**
   * 用于设置grid view
   */
  class ItemInfo {
    int imgId;

    String name;

    public ItemInfo(int imgId, String name) {
      this.imgId = imgId;
      this.name = name;
    }
  }

  /**
   * grid view 的适配器
   */
  class SettingAdapter extends BaseListAdapter<ItemInfo> {
    public SettingAdapter(Context context, List<ItemInfo> list) {
      super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView tv = null;
      if (convertView == null) {
        tv = new TextView(getContext());
        int padding = DisplayUtil.dip(10);
        tv.setPadding(0, padding, 0, padding);
        tv.setGravity(Gravity.CENTER);
        tv.setCompoundDrawablePadding(padding);
        tv.setTextColor(getColor(R.color.gray_757575));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        convertView = tv;
      } else {
        tv = (TextView) convertView;
      }
      ItemInfo info = mDataList.get(position);
      if (info != null) {
        tv.setText(info.name);
        tv.setCompoundDrawablesWithIntrinsicBounds(0, info.imgId, 0, 0);
      }
      return convertView;
    }
  }
}
