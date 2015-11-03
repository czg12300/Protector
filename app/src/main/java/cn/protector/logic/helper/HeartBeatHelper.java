
package cn.protector.logic.helper;

import android.content.Context;
import android.content.Intent;

import cn.protector.HeartBeatService;

/**
 * 描述:心跳请求接口
 *
 * @author jakechen
 * @since 2015/10/29 16:42
 */
public class HeartBeatHelper {
    private static HeartBeatHelper mInstance;

    private Context mContext;

    private HeartBeatHelper() {
    }

    public void init(Context context) {
        mContext = context;
    }

    public static HeartBeatHelper getInstance() {
        if (mInstance == null) {
            mInstance = new HeartBeatHelper();
        }
        return mInstance;
    }

    public void start() {
        Intent it = new Intent(mContext, HeartBeatService.class);
        mContext.startService(it);
    }
}
