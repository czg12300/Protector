
package cn.protector.ui.activity.setting;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.common.AppException;
import cn.common.ui.BaseDialog;
import cn.common.ui.adapter.BaseListAdapter;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.entity.FenceInfo;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.CareStaffListResponse;
import cn.protector.logic.http.response.FenceListResponse;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.activity.usercenter.ScanQACodeActivity;
import cn.protector.ui.adapter.FenceAdapter;
import cn.protector.ui.widget.StatusView;

/**
 * 描述：设置围栏页面
 *
 * @author jakechen
 */
public class FenceSetActivity extends CommonTitleActivity {
    private static final int MSG_BACK_LOAD_DATA = 0;
    private static final int MSG_UI_LOAD_DATA = 0;
    private ListView mLvFence;

    private FenceAdapter mFenceAdapter;

    private BaseDialog mOperatingDialog;
    private StatusView mStatusView;

    protected void hideDialog() {
        if (mOperatingDialog != null) {
            mOperatingDialog.dismiss();
        }
    }

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
                goActivity(AddFenceActivity.class);
            }
        });
        setBackgroundColor(getColor(R.color.background_gray));
        return vTitle;
    }

    @Override
    protected void initView() {
        mStatusView = new StatusView(this);
        setContentView(mStatusView);
        mStatusView.setContentView(R.layout.activity_care_staff);
        setTitle(R.string.title_fence_set);
        mLvFence = (ListView) findViewById(R.id.lv_care_staff);

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
        mLvFence.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FenceInfo info = (FenceInfo) mFenceAdapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("FenceDetailActivity", info);
                goActivity(FenceDetailActivity.class, bundle);
//                showOperatingDialog((FenceInfo) mFenceAdapter.getItem(position));
            }
        });
    }

    /**
     * 显示操作的弹窗
     */
    private void showOperatingDialog(final FenceInfo info) {
        if (mOperatingDialog == null) {
            mOperatingDialog = new BaseDialog(this);
            mOperatingDialog.setWindow(R.style.alpha_animation, 0.3f);
            mOperatingDialog.setContentView(R.layout.dialog_fence_operating);
            mOperatingDialog.findViewById(R.id.btn_edit_info)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOperatingDialog != null) {
                                mOperatingDialog.dismiss();
                            }
                        }
                    });

            mOperatingDialog.findViewById(R.id.btn_delete)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mFenceAdapter.remove(info);
                            // TODO 删除信息
                            if (mOperatingDialog != null) {
                                mOperatingDialog.dismiss();
                            }
                        }
                    });
        }
        mOperatingDialog.show();
    }

    @Override
    protected void initData() {
        super.initData();
        mFenceAdapter = new FenceAdapter(this);
        mLvFence.setAdapter(mFenceAdapter);
        loadData();
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
        }
    }

    private void loadDataTask() {
        HttpRequest<FenceListResponse> request = new HttpRequest<>(AppConfig.GET_ELEC_FENCE_LIST,
                FenceListResponse.class);
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
                    handleLoadData((FenceListResponse) msg.obj);
                } else {
                    mStatusView.showFailView();
                }
                break;
        }
    }

    private void handleLoadData(FenceListResponse response) {
        if (response.isOk()) {
            if (response.getList() != null && response.getList().size() > 0) {
                mStatusView.showContentView();
                mFenceAdapter.setData(response.getList());
                mFenceAdapter.notifyDataSetChanged();
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
