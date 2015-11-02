
package cn.protector;

import android.app.IntentService;
import android.content.Intent;

/**
 * 描述:
 *
 * @author jakechen
 * @since 2015/10/30 9:40
 */
public class HeartBeatService extends IntentService {
    /**
     * Creates an IntentService. Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public HeartBeatService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }
}
