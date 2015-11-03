
package cn.protector;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 描述:
 *
 * @author jakechen
 * @since 2015/10/30 9:40
 */
public class HeartBeatService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
