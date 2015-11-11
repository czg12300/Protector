
package cn.common.ui.activity;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.os.Message;
import android.os.Process;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by jakechen on 2015/8/5.
 */
public abstract class BaseApplication extends Application {
    public static BaseApplication mInstance;

    private HashMap<String, WeakReference<Activity>> mActivityMap;

    private Handler mUiHandler;

    private static class UiHandler extends Handler {
        private final WeakReference<BaseApplication> mActivityReference;

        public UiHandler(BaseApplication activity) {
            mActivityReference = new WeakReference<BaseApplication>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mActivityReference.get() != null) {
                mActivityReference.get().handleUiMessage(msg);
            }
        }

    }

    public void handleUiMessage(Message msg) {

    }

    protected void sendUiMessage(Message msg) {
        mUiHandler.sendMessage(msg);
    }

    protected void sendUiMessageDelayed(Message msg, long delayMillis) {
        mUiHandler.sendMessageDelayed(msg, delayMillis);
    }

    protected void sendEmptyUiMessage(int what) {
        mUiHandler.sendEmptyMessage(what);
    }

    protected void sendEmptyUiMessageDelayed(int what, long delayMillis) {
        mUiHandler.sendEmptyMessageDelayed(what, delayMillis);
    }

    protected void removeUiMessages(int what) {
        mUiHandler.removeMessages(what);
    }

    protected Message obtainUiMessage() {
        return mUiHandler.obtainMessage();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        onRelease();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = getChildInstance();
        mActivityMap = new HashMap<String, WeakReference<Activity>>();
        mUiHandler = new UiHandler(this);
        onConfig();
    }

    protected abstract BaseApplication getChildInstance();

    protected abstract void onConfig();

    protected abstract void onRelease();

    public void addActivity(Activity activity) {
        if (activity != null) {
            mActivityMap.put(activity.getClass().getSimpleName(),
                    new WeakReference<Activity>(activity));
        }
    }

    public void removeActivity(String activityName) {
        if (mActivityMap != null && mActivityMap.containsKey(activityName)) {
            mActivityMap.remove(activityName);
        }
    }

    public void exitApp() {
        if (mActivityMap != null && mActivityMap.size() > 0) {
            for (String key : mActivityMap.keySet()) {
                if (mActivityMap.get(key) != null) {
                    mActivityMap.get(key).get().finish();
                }
            }
        }
        mActivityMap.clear();
        android.os.Process.killProcess(Process.myPid());
        System.exit(0);
    }

    public static synchronized BaseApplication getInstance() {
        return mInstance;
    }
}
