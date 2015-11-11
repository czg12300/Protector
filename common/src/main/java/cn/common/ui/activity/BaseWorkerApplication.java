
package cn.common.ui.activity;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 描述:带后台线程的application
 *
 * @author jakechen
 * @since 2015/11/11 10:08
 */
public abstract class BaseWorkerApplication extends BaseApplication {

    private HandlerThread mHandlerThread;

    private BackgroundHandler mBackgroundHandler;

    private static class BackgroundHandler extends Handler {

        private final WeakReference<BaseWorkerApplication> mActivityReference;

        BackgroundHandler(BaseWorkerApplication activity, Looper looper) {
            super(looper);
            mActivityReference = new WeakReference<BaseWorkerApplication>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mActivityReference.get() != null) {
                mActivityReference.get().handleBackgroundMessage(msg);
            }
        }
    }

    @Override
    public void onCreate() {
        mHandlerThread = new HandlerThread("activity worker:" + getClass().getSimpleName());
        mHandlerThread.start();
        mBackgroundHandler = new BackgroundHandler(this, mHandlerThread.getLooper());
        super.onCreate();
    }

    @Override
    protected void onRelease() {
        if (mBackgroundHandler != null && mBackgroundHandler.getLooper() != null) {
            mBackgroundHandler.getLooper().quit();
        }
    }

    public void handleBackgroundMessage(Message msg) {
    }

    protected void sendBackgroundMessage(Message msg) {
        mBackgroundHandler.sendMessage(msg);
    }

    protected void sendBackgroundMessageDelayed(Message msg, long delay) {
        mBackgroundHandler.sendMessageDelayed(msg, delay);
    }

    protected void sendEmptyBackgroundMessage(int what) {
        mBackgroundHandler.sendEmptyMessage(what);
    }

    protected void sendEmptyBackgroundMessageDelayed(int what, long delay) {
        mBackgroundHandler.sendEmptyMessageDelayed(what, delay);
    }

    protected void removeBackgroundMessages(int what) {
        mBackgroundHandler.removeMessages(what);
    }

    protected Message obtainBackgroundMessage() {
        return mBackgroundHandler.obtainMessage();
    }
}
