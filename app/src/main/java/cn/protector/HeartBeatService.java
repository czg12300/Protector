
package cn.protector;

import android.content.Intent;
import android.os.IBinder;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

import cn.common.AppException;
import cn.common.BaseWorkService;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.http.HttpRequest;
import cn.protector.logic.http.response.CommonResponse;
import cn.protector.utils.LogUtil;

/**
 * 描述:心跳包服务
 *
 * @author jakechen
 * @since 2015/10/30 9:40
 */
public class HeartBeatService extends BaseWorkService {
    private static final String TAG = HeartBeatService.class.getSimpleName();

    private static final int MSG_BACK_SEND_HEART_BEAT = 1;

    private static final int MAX_RETRY_TIME = 3;

    /**
     * 每次心跳包之间的间隔时间
     */
    private static final long TIME_SPIT_TEST = 30 * 1000;

    private static final long TIME_SPIT_RELEASE =  30 * 1000;

    private static final long TIME_SPIT = AppConfig.isDebug ? TIME_SPIT_TEST : TIME_SPIT_RELEASE;

    private Timer timer;

    private int reTryTime = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        start();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 开始发送心跳包
     */
    private void start() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timer == null) {
            timer = new Timer();
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendEmptyBackgroundMessage(MSG_BACK_SEND_HEART_BEAT);
            }
        }, 0, TIME_SPIT);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
               System.out.print("如来神掌");
            }
        }, 0, 5*1000);
    }

    @Override
    public void handleBackgroundMessage(Message msg) {
        super.handleBackgroundMessage(msg);
        switch (msg.what) {
            case MSG_BACK_SEND_HEART_BEAT:
                HttpRequest<CommonResponse> httpRequest = new HttpRequest<>(AppConfig.HEART_BEAT,
                        CommonResponse.class);
                httpRequest.addParam("uc", InitSharedData.getUserCode());
                try {
                    CommonResponse response = httpRequest.request();
                    boolean isSuccess = response != null
                            && response.getResult() == CommonResponse.SUCCESS;
                    if (!isSuccess) {
                        LogUtil.d(TAG, "send heart beat fail");
                        reTry();
                    } else {
                        LogUtil.d(TAG, "send heart beat success");
                    }
                } catch (AppException e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, "send heart beat fail");
                    reTry();
                }
                break;
        }
    }

    /**
     * 失败了重试，最多重试3次
     */
    private void reTry() {
        removeBackgroundMessages(MSG_BACK_SEND_HEART_BEAT);
        if (reTryTime < MAX_RETRY_TIME) {
            start();
            reTryTime++;
        }
    }
}
