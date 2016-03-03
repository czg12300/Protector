package cn.protector.ui.activity.setting;

import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import cn.common.AppException;
import cn.common.ui.BaseDialog;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.entity.CareStaffInfo;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.CareStaffListResponse;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.activity.usercenter.ScanQACodeActivity;
import cn.protector.ui.adapter.CareStaffAdapter;
import cn.protector.ui.widget.StatusView;
import cn.protector.utils.ToastUtil;

/**
 * 描述：监护人员页面
 *
 * @author jakechen
 */
public class CareStaffActivity extends CommonTitleActivity {
  private static final int MSG_BACK_LOAD_DATA = 0;
  private static final int MSG_BACK_DELETE = 1;
  private static final int MSG_UI_LOAD_DATA = 0;
  private static final int MSG_UI_DELETE = 1;
  private ListView mLvCareStaff;

  private CareStaffAdapter mCareStaffAdapter;
  private StatusView mStatusView;

  @Override
  protected void initView() {
    mStatusView = new StatusView(this);
    setContentView(mStatusView);
    setTitle(R.string.title_care_staff);
    mStatusView.setContentView(R.layout.activity_care_staff);
    mLvCareStaff = (ListView) findViewById(R.id.lv_care_staff);
//        mLvCareStaff.addHeaderView(inflate(R.layout.view_empty_divider));
//        mLvCareStaff.addFooterView(inflate(R.layout.view_empty_divider));
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
    mLvCareStaff.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      }
    });
  }

  private void loadData() {
    mStatusView.showLoadingView();
    sendEmptyBackgroundMessage(MSG_BACK_LOAD_DATA);
  }

  @Override
  protected void initData() {
    super.initData();
    mCareStaffAdapter = new CareStaffAdapter(this, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (v.getTag() != null) {
          CareStaffInfo info = (CareStaffInfo) v.getTag();
          if (!info.isManager()) {
            showDeleteDialog(info);
            ToastUtil.showError();
          }
        }
      }
    });
    mLvCareStaff.setAdapter(mCareStaffAdapter);
    loadData();
    mStatusView.setNoDataTip("暂无监护人员");
  }

  private void showDeleteDialog(final CareStaffInfo info) {
    if (info != null && !isFinishing()) {
      final BaseDialog dialog = new BaseDialog(this);
      dialog.setContentView(R.layout.dialog_delete_care_staff);
      TextView text = (TextView) dialog.findViewById(R.id.tv_title);
      text.setText("确定要删除“" + info.getNickName() + "”吗？");
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

    }
  }

  @Override
  public void handleBackgroundMessage(Message msg) {
    super.handleBackgroundMessage(msg);
    switch (msg.what) {
      case MSG_BACK_LOAD_DATA:
        loadDataTask();
        break;
      case MSG_BACK_DELETE:
        if (msg.obj != null) {
          deleteDataTask((CareStaffInfo) msg.obj);
        }
        break;
    }
  }

  private void deleteDataTask(CareStaffInfo info) {
    HttpRequest<CareStaffListResponse> request = new HttpRequest<>(AppConfig.GET_EQUI_USER_LIST, CareStaffListResponse.class);
    request.addParam("uc", InitSharedData.getUserCode());
    if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
      request.addParam("eid", DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId());
    }
    Message uiMsg = obtainUiMessage();
    uiMsg.what = MSG_UI_LOAD_DATA;
    try {
      uiMsg.obj = request.request();
    } catch (AppException e) {
      e.printStackTrace();
    }
    uiMsg.sendToTarget();
  }

  private void loadDataTask() {
    HttpRequest<CareStaffListResponse> request = new HttpRequest<>(AppConfig.GET_EQUI_USER_LIST, CareStaffListResponse.class);
    request.addParam("uc", InitSharedData.getUserCode());
    if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
      request.addParam("eid", DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId());
    }
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
          handleLoadData((CareStaffListResponse) msg.obj);
        } else {
          mStatusView.showFailView();
        }
        break;
    }
  }

  private void handleLoadData(CareStaffListResponse response) {
    if (response.isOk()) {
      if (response.getList() != null && response.getList().size() > 0) {
        mStatusView.showContentView();
        mCareStaffAdapter.setData(response.getList());
        mCareStaffAdapter.notifyDataSetChanged();
      } else {
        mStatusView.showNoDataView();
      }
    } else {
      mStatusView.showFailView();
    }

  }

  public void onClick(View v) {
    if (v.getId() == R.id.btn_add) {
      goActivity(ScanQACodeActivity.class);
    }
  }


}
