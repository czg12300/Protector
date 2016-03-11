package cn.protector.ui.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.common.AppException;
import cn.common.ui.BaseDialog;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.entity.DeviceInfo;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.CommonResponse;
import cn.protector.logic.http.response.GetBaseListResponse;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.activity.usercenter.AddDeviceActivity;
import cn.protector.ui.activity.usercenter.FinishInfoActivity;
import cn.protector.ui.activity.usercenter.ScanQACodeActivity;
import cn.protector.ui.adapter.DeviceManagerAdapter;
import cn.protector.ui.widget.StatusView;
import cn.protector.utils.ToastUtil;

/**
 * 描述：设备管理
 *
 * @author jake
 * @since 2015/9/21 22:07
 */
public class DeviceManageActivity extends CommonTitleActivity {
  private static final int MSG_BACK_LOAD_DATA = 0;
  private static final int MSG_UI_LOAD_DATA = 0;
  private static final int MSG_BACK_DELETE = 1;
  private static final int MSG_UI_DELETE = 1;
  private ListView mLvContent;

  private DeviceManagerAdapter mDeviceAdapter;
  private StatusView mStatusView;
  private boolean isReLoadData = false;

  @Override
  protected View getTitleLayoutView() {
    View vTitle = getLayoutInflater().inflate(R.layout.title_choose_avator, null);
    vTitle.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    mTvTitle = (TextView) vTitle.findViewById(R.id.tv_title);
    vTitle.findViewById(R.id.ib_add).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        goActivity(AddDeviceActivity.class);
      }
    });
    setBackgroundColor(getColor(R.color.background_gray));
    return vTitle;
  }

  @Override
  protected void initView() {
    mStatusView = new StatusView(this);
    setContentView(mStatusView);
    mStatusView.setContentView(R.layout.activity_device_manage);
    setTitle(R.string.title_device_manage);
    mLvContent = (ListView) findViewById(R.id.lv_content);
    mStatusView.setNoDataTip("你还没有添加任何设备");
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
    mLvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putInt(FinishInfoActivity.KEY_TYPE, FinishInfoActivity.TYPE_MODIFY);
        try {
          bundle.putString(FinishInfoActivity.KEY_EID, mDeviceAdapter.getDataList().get(position).geteId());
        } catch (Exception e) {
          e.printStackTrace();
        }
        goActivity(FinishInfoActivity.class, bundle);
      }
    });
  }

  @Override
  protected void initData() {
    super.initData();
    mDeviceAdapter = new DeviceManagerAdapter(this, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (v.getTag() != null) {
          showDeleteDialog((DeviceInfo) v.getTag());

        }
      }
    });
    mLvContent.setAdapter(mDeviceAdapter);
    loadData();
  }

  private void showDeleteDialog(final DeviceInfo info) {
    if (info != null && !isFinishing()) {
      final BaseDialog dialog = new BaseDialog(this);
      dialog.setWindow(R.style.alpha_animation, 0.3f);
      dialog.setContentView(R.layout.dialog_title_content);
      TextView text = (TextView) dialog.findViewById(R.id.tv_title);
      String show = "昵称：" + info.getNikeName() + "\n" +
              "关系：" + DeviceInfo.parseRelation(info.getRelation(), info.getOtherRelation()) + "\n地址：" + info.getAddress() + "\n\n确定要删除该设备吗？";
      text.setText(show);
      dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          dialog.dismiss();
        }
      });
      dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Message msg = obtainBackgroundMessage();
          msg.what = MSG_BACK_DELETE;
          msg.obj = info;
          msg.sendToTarget();
          dialog.dismiss();
        }
      });
      dialog.show();
    }
  }

  private void loadData() {
    mStatusView.showLoadingView();
    sendEmptyBackgroundMessage(MSG_BACK_LOAD_DATA);
  }

  @Override
  public void handleBackgroundMessage(Message msg) {
    super.handleBackgroundMessage(msg);
    switch (msg.what) {
      case MSG_BACK_LOAD_DATA:
        loadDataTask();
        break;
      case MSG_BACK_DELETE:
        deleteTask(msg.obj);
        break;
    }
  }


  private void deleteTask(Object obj) {
    if (obj == null) {
      return;
    }
    DeviceInfo info = (DeviceInfo) obj;
    HttpRequest<CommonResponse> request = new HttpRequest<>(AppConfig.DEL_ELECFENCE, CommonResponse.class);
    request.addParam("uc", InitSharedData.getUserCode());
    request.addParam("user", InitSharedData.getUserId());
    request.addParam("eid", info.geteId());
    Message uiMsg = obtainUiMessage();
    uiMsg.what = MSG_UI_DELETE;
    try {
      CommonResponse response = request.request();
      if (response != null && response.getResult() > 0) {
        uiMsg.obj = info;
      }
    } catch (AppException e) {
      e.printStackTrace();
    }
    uiMsg.sendToTarget();
  }

  private void loadDataTask() {
    HttpRequest<GetBaseListResponse> request = new HttpRequest<>(AppConfig.GET_BASELIST, GetBaseListResponse.class);
    request.addParam("uc", InitSharedData.getUserCode());
    Message uiMsg = obtainUiMessage();
    uiMsg.what = MSG_UI_LOAD_DATA;
    try {
      uiMsg.obj = request.request();
    } catch (AppException e) {
      e.printStackTrace();
    }
    uiMsg.sendToTarget();
  }

  @Override
  public void handleUiMessage(Message msg) {
    super.handleUiMessage(msg);
    switch (msg.what) {
      case MSG_UI_LOAD_DATA:
        if (msg.obj != null) {
          handleLoadData((GetBaseListResponse) msg.obj);
        } else {
          mStatusView.showFailView();
        }
        break;
      case MSG_UI_DELETE:
        if (msg.obj != null) {
          ToastUtil.show("删除成功");
          DeviceInfo info = (DeviceInfo) msg.obj;
          mDeviceAdapter.remove(info);
          mDeviceAdapter.notifyDataSetChanged();
        } else {
          ToastUtil.show("删除失败");
        }
        break;
    }
  }

  private void handleLoadData(GetBaseListResponse response) {
    if (response.getList() != null && response.getList().size() > 0) {
      mStatusView.showContentView();
      if (isReLoadData) {
        InitSharedData.setDeviceData(response.getJson());
        DeviceInfoHelper.getInstance().refreshDeviceList();
      }
      mDeviceAdapter.setDataNotifyDataSetChanged(response.getList());
    } else {
      mStatusView.showNoDataView();
    }
  }

  public void onClick(View v) {
    if (v.getId() == R.id.btn_add) {
      goActivity(ScanQACodeActivity.class);
    }
  }

  @Override
  public void setupBroadcastActions(List<String> actions) {
    super.setupBroadcastActions(actions);
    actions.add(BroadcastActions.ACTION_MODIFY_WEAR_INFO_SUCCESS);
    actions.add(BroadcastActions.ACTION_FINISH_ACTIVITY_BEFORE_MAIN);
  }

  @Override
  public void handleBroadcast(Context context, Intent intent) {
    super.handleBroadcast(context, intent);
    String action = intent.getAction();
    if (TextUtils.equals(action, BroadcastActions.ACTION_MODIFY_WEAR_INFO_SUCCESS)) {
      isReLoadData = true;
      sendEmptyBackgroundMessage(MSG_BACK_LOAD_DATA);
    } else if (TextUtils.equals(action, BroadcastActions.ACTION_FINISH_ACTIVITY_BEFORE_MAIN)) {
      finish();
    }
  }


}
