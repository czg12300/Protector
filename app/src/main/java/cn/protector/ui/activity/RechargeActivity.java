package cn.protector.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.List;

import cn.common.AppException;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.entity.PrizeInfo;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.HuaFeiResponse;
import cn.protector.logic.http.response.WeChatPayFormResponse;
import cn.protector.ui.adapter.RechargePrizeAdapter;
import cn.protector.ui.widget.StatusView;
import cn.protector.utils.ToastUtil;
import cn.protector.wxapi.WXPayEntryActivity;

/**
 * 描述：充值页面
 * 作者：jake on 2016/3/12 16:15
 */
public class RechargeActivity extends CommonTitleActivity {
    private static final int MSG_UI_LOAD_DATA = 0;
    private static final int MSG_BACK_LOAD_DATA = 0;
    private static final int MSG_UI_GET_PAY_FORM = 1;
    private static final int MSG_BACK_GET_PAY_FORM = 1;
    private StatusView mStatusView;
    private TextView tvHuaFei;
    private GridView gridView;
    private double prize = -1;
    private int months;
    private String name;
    private IWXAPI api;
    private RechargePrizeAdapter mRechargePrizeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        api = WXAPIFactory.createWXAPI(this, AppConfig.WECHAT_APP_ID);
        api.registerApp(AppConfig.WECHAT_APP_ID);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setTitle("话费充值");
        mStatusView = new StatusView(this);
        mStatusView.setContentView(R.layout.activity_recharge);
        setContentView(mStatusView);
        mStatusView.showContentView();
        tvHuaFei = (TextView) findViewById(R.id.tv_recharge_hint);
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
                    months = info.getMonths();
                    name = info.getTitle();
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
            boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
            if (isPaySupported) {
                sendEmptyBackgroundMessage(MSG_BACK_GET_PAY_FORM);
            } else {
                ToastUtil.show("您的微信不支持支付，请将微信升级到最新版本");

            }
        } else {
            ToastUtil.show("请选择充值类型");
        }
    }

    @Override
    protected void initData() {
        super.initData();
        mRechargePrizeAdapter=new RechargePrizeAdapter(this);
        gridView.setAdapter(mRechargePrizeAdapter);
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
            case MSG_BACK_GET_PAY_FORM:
                getPayFormTask();
                break;
        }
    }

    private void getPayFormTask() {
        String eid = "";
        if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
            eid = DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId();
        }
        //todo
//        String url = AppConfig.GET_PAY_FORM + "uer=" + InitSharedData.getMobile() + "&body=" + name + "&total_fee=" + (int) prize + "&PurchaseQuantity=" + months + "&EID=" + eid;
        String url = AppConfig.GET_PAY_FORM + "uer=" + InitSharedData.getMobile() + "&body=" + name + "&total_fee=" + 1 + "&PurchaseQuantity=" + months + "&EID=" + eid;
        HttpRequest<WeChatPayFormResponse> request = new HttpRequest<>(url, WeChatPayFormResponse.class);
        request.setIsGet(false);
        Message message = obtainUiMessage();
        message.what = MSG_UI_GET_PAY_FORM;
        try {
            message.obj = request.request();
        } catch (AppException e) {
            e.printStackTrace();
        }
        message.sendToTarget();
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
                if (msg.obj != null) {
                    mStatusView.showContentView();
                    HuaFeiResponse response = (HuaFeiResponse) msg.obj;
                    if (tvHuaFei != null) {
                        tvHuaFei.setText("当前话费余额" + response.getBalance() + "元，预计可使用" + response.getExpectTime() + "个月");
                    }
                } else {
                    mStatusView.showFailView();
                }
                break;
            case MSG_UI_GET_PAY_FORM:
                if (msg.obj != null) {
                    WeChatPayFormResponse response = (WeChatPayFormResponse) msg.obj;
                    handleWeChat(response);
                } else {
                    ToastUtil.show("账单创建失败");
                }
                break;
        }
    }

    private void handleWeChat(WeChatPayFormResponse response) {
        PayReq req = new PayReq();
        req.appId = response.getAppid();
        req.appId = AppConfig.WECHAT_APP_ID;
        req.partnerId = response.getPartnerid();
        req.prepayId = response.getPrepayid();
        req.nonceStr = response.getNoncestr();
        req.timeStamp = response.getTimestamp();
        req.packageValue = response.get_package();
        req.sign = response.getSign();
        api.sendReq(req);
    }


    @Override
    public void setupBroadcastActions(List<String> actions) {
        super.setupBroadcastActions(actions);
        actions.add(BroadcastActions.ACTION_WECHAT_PAY_RESULT);
    }

    @Override
    public void handleBroadcast(Context context, Intent intent) {
        super.handleBroadcast(context, intent);
        String action = intent.getAction();
        if (TextUtils.equals(action, BroadcastActions.ACTION_WECHAT_PAY_RESULT)) {
            int code = intent.getExtras().getInt(WXPayEntryActivity.KEY_CODE, -1000);
            if (code < 0) {
                if (code == BaseResp.ErrCode.ERR_USER_CANCEL) {
                    ToastUtil.show("取消支付");

                } else {
                    ToastUtil.show("支付失败");
                }
            } else {
                if (mRechargePrizeAdapter != null) {
                    mRechargePrizeAdapter.setSelect(-1);
                }
                loadData();
                ToastUtil.show("支付成功");
            }
        }
    }
}
