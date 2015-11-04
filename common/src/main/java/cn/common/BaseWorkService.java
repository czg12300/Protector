
package cn.common;

import android.app.Service;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 描述:带线程处理的service
 *
 * @author jakechen
 * @since 2015/11/4 14:55
 */
public abstract class BaseWorkService extends Service {

    private HandlerThread mHandlerThread;

    private BackgroundHandler mBackgroundHandler;

    private static class BackgroundHandler extends Handler {

        private final WeakReference<BaseWorkService> mActivityReference;

        BackgroundHandler(BaseWorkService activity, Looper looper) {
            super(looper);
            mActivityReference = new WeakReference<BaseWorkService>(activity);
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
        super.onCreate();
        mHandlerThread = new HandlerThread("activity worker:" + getClass().getSimpleName());
        mHandlerThread.start();
        mBackgroundHandler = new BackgroundHandler(this, mHandlerThread.getLooper());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
