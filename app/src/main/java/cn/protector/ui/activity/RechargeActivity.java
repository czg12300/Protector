package cn.protector.ui.activity;

import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cn.common.AppException;
import cn.protector.AppConfig;
import cn.protector.R;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.entity.PrizeInfo;
import cn.protector.logic.helper.DeviceInfoHelper;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.HuaFeiResponse;
import cn.protector.logic.http.response.WeChatPayFormResponse;
import cn.protector.ui.adapter.RechargePrizeAdapter;
import cn.protector.ui.widget.StatusView;
import cn.protector.utils.ToastUtil;

/**
 * 描述：充值页面
 * 作者：jake on 2016/3/12 16:15
 */
public class RechargeActivity extends CommonTitleActivity implements IWXAPIEventHandler {
    private static final int MSG_UI_LOAD_DATA = 0;
    private static final int MSG_BACK_LOAD_DATA = 0;
    private static final int MSG_UI_GET_PAY_FORM = 1;
    private static final int MSG_BACK_GET_PAY_FORM = 1;
    private StatusView mStatusView;
    private TextView tvHuaFei;
    private GridView gridView;
    private double prize = -1;
    private double months;
    private String name;
    private IWXAPI api;

    @Override
    protected void initView() {
        setTitle("话费充值");
        mStatusView = new StatusView(this);
        mStatusView.setContentView(R.layout.activity_recharge);
        setContentView(mStatusView);
        mStatusView.showContentView();
        tvHuaFei = (TextView) findViewById(R.id.tv_title);
        gridView = (GridView) findViewById(R.id.gv_prize);
        api = WXAPIFactory.createWXAPI(this, AppConfig.WECHAT_APP_ID);
        api.registerApp(AppConfig.WECHAT_APP_ID);
        api.handleIntent(getIntent(), this);
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
                    months = info.getPrize();
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
                ToastUtil.show("正在创建账单");
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
            case MSG_BACK_GET_PAY_FORM:
                getPayFormTask();
                break;
        }
    }

    private void getPayFormTask() {
//        String url=AppConfig.GET_PAY_FORM+
        HttpRequest<WeChatPayFormResponse> request = new HttpRequest<>(AppConfig.GET_PAY_FORM, WeChatPayFormResponse.class);
        request.setIsGet(false);
        if (DeviceInfoHelper.getInstance().getPositionDeviceInfo() != null) {
            request.addParam("EID", DeviceInfoHelper.getInstance().getPositionDeviceInfo().geteId());
        }
        request.addParam("uer", InitSharedData.getMobile());
        request.addParam("body", name);
        request.addParam("total_fee", "" + prize);
        request.addParam("PurchaseQuantity", "" + months);

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
            case MSG_UI_GET_PAY_FORM:
                //测试
                String json = "{ \"appid\":\"wxb4ba3c02aa476ea1\", \"noncestr\":\"c575bfa543b573e096dc9f86640b2f46\", \"package\":\"Sign=WXPay\", \"partnerid\":\"10000100\", \"prepayid\":\"wx2016031300042780a9cb7b510443959618\", \"timestamp\":\"1457798667\", \"sign\":\"D7532DBF50CE31D79C36F41499000E13\" }";
                WeChatPayFormResponse response1 =new WeChatPayFormResponse();
                response1.parse(json);
                handleWeChat(response1);
                //--------
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
        req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
//        req.appId = response.getAppid();
        req.partnerId = response.getPartnerid();
        req.prepayId = response.getPrepayid();
        req.nonceStr = response.getNoncestr();
        req.timeStamp = response.getTimestamp();
        req.packageValue = response.get_package();
        req.sign = response.getSign();
        req.extData = "app data"; // optional
        api.sendReq(req);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
//        tic int	ERR_AUTH_DENIED
//
//        认证被否决
//        static int	ERR_COMM
//
//        一般错误
//        static int	ERR_OK
//
//        正确返回
//        static int	ERR_SENT_FAILED
//
//        发送失败
//        static int	ERR_UNSUPPORT
//
//        不支持错误
//        static int	ERR_USER_CANCEL
//
//        用户取消
        ToastUtil.show("errCode=" + resp.errCode);
        if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
            ToastUtil.show("支付成功");
        } else {
            ToastUtil.show("支付失败");
        }
//        }
    }
}
