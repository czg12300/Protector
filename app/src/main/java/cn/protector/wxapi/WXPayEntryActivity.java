package cn.protector.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cn.protector.AppConfig;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.utils.ToastUtil;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    public static final String KEY_CODE = "key_code";
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, AppConfig.WECHAT_APP_ID);
        api.registerApp(AppConfig.WECHAT_APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            Intent intent = new Intent(BroadcastActions.ACTION_WECHAT_PAY_RESULT);
            intent.putExtra(KEY_CODE, resp.errCode);
            sendBroadcast(intent);
        }
        finish();
    }


}