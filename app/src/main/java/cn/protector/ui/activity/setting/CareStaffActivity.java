
package cn.protector.ui.activity.setting;

import android.os.Message;
import android.view.View;
import android.widget.ListView;

import cn.common.AppException;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.CareStaffListResponse;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.activity.usercenter.ScanQACodeActivity;
import cn.protector.ui.adapter.CareStaffAdapter;
import cn.protector.ui.widget.StatusView;

/**
 * 描述：监护人员页面
 *
 * @author jakechen
 */
public class CareStaffActivity extends CommonTitleActivity {
    private static final int MSG_BACK_LOAD_DATA = 0;
    private static final int MSG_UI_LOAD_DATA = 0;
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
    }

    private void loadData() {
        mStatusView.showLoadingView();
        sendEmptyBackgroundMessage(MSG_BACK_LOAD_DATA);
    }

    @Override
    protected void initData() {
        super.initData();
        mCareStaffAdapter = new CareStaffAdapter(this);
        mLvCareStaff.setAdapter(mCareStaffAdapter);
        loadData();
        mStatusView.setNoDataTip("暂无监护人员");
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
        HttpRequest<CareStaffListResponse> request = new HttpRequest<>(AppConfig.GET_EQUI_USER_LIST,
                CareStaffListResponse.class);
        request.addParam("uc", InitSharedData.getUserCode());
        if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
            request.addParam("eid",
                    DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId());
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
