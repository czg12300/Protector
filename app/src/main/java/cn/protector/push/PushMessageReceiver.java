package cn.protector.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

import org.json.JSONException;
import org.json.JSONObject;

import cn.common.utils.ThreadPoolUtil;
import cn.protector.AppConfig;
import cn.protector.logic.data.BroadcastActions;
import cn.protector.logic.entity.ChatMessage;
import cn.protector.logic.http.response.NowDeviceInfoResponse;
import cn.protector.utils.ToastUtil;

public class PushMessageReceiver extends BroadcastReceiver {

    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView ==
     * null)
     */
    public static StringBuilder payloadData = new StringBuilder();
    public static final String KEY_COMMON_MESSAGE = "key_common_message";
    public static final String KEY_EVENT_MESSAGE = "key_event_message";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");
                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");
                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));
                if (payload != null) {
                    String data = new String(payload);
                    handleMessage(context, data);
                    payloadData.append(data);
                    payloadData.append("\n");
                    if (AppConfig.isDebug) {
                        Log.d("GetuiSdkDemo", "receiver payload : " + data);
                        ToastUtil.show(payloadData.toString());
                    }
                }
                break;
            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
                break;
            case PushConsts.THIRDPART_FEEDBACK:
                /*
                 * String appid = bundle.getString("appid"); String taskid =
                 * bundle.getString("taskid"); String actionid =
                 * bundle.getString("actionid"); String result =
                 * bundle.getString("result"); long timestamp =
                 * bundle.getLong("timestamp"); Log.d("GetuiSdkDemo", "appid = "
                 * + appid); Log.d("GetuiSdkDemo", "taskid = " + taskid);
                 * Log.d("GetuiSdkDemo", "actionid = " + actionid);
                 * Log.d("GetuiSdkDemo", "result = " + result);
                 * Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
                 */
                break;
            default:
                break;
        }
    }

    private void handleMessage(final Context context, final String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        ThreadPoolUtil.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject root = new JSONObject(message);
                    int type = root.optInt("type",-1);
                    if (type == 0) {
                        JSONObject object = root.optJSONObject("message");
                        if (object != null) {
                            ChatMessage chatMessage = new ChatMessage();
                            chatMessage.setTime(object.optInt("timestamp"));
                            chatMessage.setImage(object.optString("image"));
                            chatMessage.setContent(object.optString("content"));
                            chatMessage.setSender(object.optString("sender"));
                            chatMessage.setUrl(object.optString("url"));
                            Intent it = new Intent(BroadcastActions.ACTION_PUSH_COMMON_MESSAGE);
                            it.putExtra(KEY_COMMON_MESSAGE, chatMessage);
                            context.sendBroadcast(it);
                            DbHelper.getInstance().insertTable(chatMessage);
                        }
                    } else if (type == 1) {
                        JSONObject object = root.optJSONObject("message");
                        if (object!=null){
                            NowDeviceInfoResponse response=new NowDeviceInfoResponse();
                            response.parse(object.toString());
                            Intent it = new Intent(BroadcastActions.ACTION_PUSH_REAL_TIME_LOCATE_DATA);
                            it.putExtra(KEY_EVENT_MESSAGE, response);
                            context.sendBroadcast(it);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
