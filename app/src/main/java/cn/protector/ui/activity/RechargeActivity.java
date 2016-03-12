package cn.protector.ui.activity;

import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import cn.common.AppException;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.entity.PrizeInfo;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.HuaFeiResponse;
import cn.protector.ui.adapter.RechargePrizeAdapter;
import cn.protector.ui.widget.StatusView;
import cn.protector.utils.ToastUtil;

/**
 * 描述：充值页面
 * 作者：jake on 2016/3/12 16:15
 */
public class RechargeActivity extends CommonTitleActivity {
    private static final int MSG_UI_LOAD_DATA = 0;
    private static final int MSG_BACK_LOAD_DATA = 0;
    private StatusView mStatusView;
    private TextView tvHuaFei;
    private GridView gridView;
    private double prize = -1;

    @Override
    protected void initView() {
        setTitle("话费充值");
        mStatusView = new StatusView(this);
        mStatusView.setContentView(R.layout.activity_recharge);
        setContentView(mStatusView);
        mStatusView.showContentView();
        tvHuaFei = (TextView) findViewById(R.id.tv_title);
        gridView = (GridView) findViewById(R.id.gv_prize);
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
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RechargePrizeAdapter adapter = (RechargePrizeAdapter) parent.getAdapter();
                PrizeInfo info = (PrizeInfo) adapter.getItem(position);
                if (info != null) {
                    prize = info.getPrize();
                }
                adapter.setSelect(position);

            }
        });
        findViewById(R.id.btn_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rechargeByWeChat();
            }
        });
    }

    private void rechargeByWeChat() {
        if (prize > 0) {
            ToastUtil.show("充值的金额为："+prize+"元");
        } else {
            ToastUtil.show("请选择充值类型");
        }
    }

    @Override
    protected void initData() {
        super.initData();
        gridView.setAdapter(new RechargePrizeAdapter(this));
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
        HttpRequest<HuaFeiResponse> request = new HttpRequest<>(AppConfig.GET_EQUIBALANCE, HuaFeiResponse.class);
        if (!TextUtils.isEmpty(InitSharedData.getUserCode())) {
            request.addParam("uc", InitSharedData.getUserCode());
        }
        if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
            request.addParam("eid", DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId());
        }
        Message message = obtainUiMessage();
        message.what = MSG_UI_LOAD_DATA;
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
            case MSG_UI_LOAD_DATA:
                mStatusView.showContentView();
//                if (msg.obj != null) {
//                    mStatusView.showContentView();
//                    HuaFeiResponse response = (HuaFeiResponse) msg.obj;
//                    if (tvHuaFei != null) {
//                        tvHuaFei.setText("当前话费余额" + response.getBalance() + "元，预计可使用" + response.getExpectTime() + "个月");
//                    }
//                } else {
//                    mStatusView.showFailView();
//                }
                break;
        }
    }
}
