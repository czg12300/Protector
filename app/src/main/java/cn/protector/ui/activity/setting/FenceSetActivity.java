
package cn.protector.ui.activity.setting;

import android.content.Context;
import android.content.Intent;
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
import cn.protector.logic.data.BroadcastActions;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.entity.FenceInfo;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.CareStaffListResponse;
import cn.protector.logic.http.response.CommonResponse;
import cn.protector.logic.http.response.FenceListResponse;
import cn.protector.ui.activity.CommonTitleActivity;
import cn.protector.ui.activity.usercenter.ScanQACodeActivity;
import cn.protector.ui.adapter.FenceAdapter;
import cn.protector.ui.widget.StatusView;
import cn.protector.utils.ToastUtil;

/**
 * 描述：设置围栏页面
 *
 * @author jakechen
 */
public class FenceSetActivity extends CommonTitleActivity {
    private static final int MSG_BACK_LOAD_DATA = 0;
    private static final int MSG_UI_LOAD_DATA = 0;
    private static final int MSG_BACK_DELETE = 1;
    private static final int MSG_UI_DELETE = 1;
    private ListView mLvFence;

    private FenceAdapter mFenceAdapter;

    private StatusView mStatusView;


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
            }
        });
        mLvFence.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog((FenceInfo) mFenceAdapter.getItem(position));
                return true;
            }
        });
    }

    private void showDeleteDialog(final FenceInfo info) {
        if (info != null && !isFinishing()) {
            final BaseDialog dialog = new BaseDialog(this);
            dialog.setWindow(R.style.alpha_animation, 0.3f);
            dialog.setContentView(R.layout.dialog_delete_care_staff);
            TextView text = (TextView) dialog.findViewById(R.id.tv_title);
            text.setText("确定要删除围栏“" + info.getName() + "”吗？");
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
            case MSG_BACK_DELETE:
                deleteTask(msg.obj);
                break;
        }
    }

    private void deleteTask(Object obj) {
        if (obj == null) {
            return;
        }
        FenceInfo info = (FenceInfo) obj;
        HttpRequest<CommonResponse> request = new HttpRequest<>(AppConfig.DEL_ELECFENCE,
                CommonResponse.class);
        request.addParam("uc", InitSharedData.getUserCode());
        request.addParam("rid", info.getRid());
        if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
            request.addParam("eid",
                    DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId());
        }
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
            case MSG_UI_DELETE:
                if (msg.obj != null) {
                    ToastUtil.show("删除成功");
                    FenceInfo info = (FenceInfo) msg.obj;
                    mFenceAdapter.remove(info);
                    mFenceAdapter.notifyDataSetChanged();
                } else {
                    ToastUtil.show("删除失败");
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

    @Override
    public void setupBroadcastActions(List<String> actions) {
        super.setupBroadcastActions(actions);
        actions.add(BroadcastActions.ACTION_ADD_FENCE_SUCCESS);
    }

    @Override
    public void handleBroadcast(Context context, Intent intent) {
        super.handleBroadcast(context, intent);
        String action=intent.getAction();
        if (TextUtils.equals(BroadcastActions.ACTION_ADD_FENCE_SUCCESS,action)){
            sendEmptyBackgroundMessage(MSG_BACK_LOAD_DATA);
        }
    }
}
