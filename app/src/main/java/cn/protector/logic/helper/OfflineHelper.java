package cn.protector.logic.helper;

import java.util.ArrayList;

/**
 * 描述：监听离线通知
 *
 * @author jake on 2015/11/11.
 */
public class OfflineHelper {
    private static OfflineHelper mInstance;
    private ArrayList<OfflineListener> mListenerList;

    private OfflineHelper() {
        mListenerList = new ArrayList<>();
    }

    public static OfflineHelper getInstance() {
        if (mInstance == null) {
            mInstance = new OfflineHelper();
        }
        return mInstance;
    }

    public void addListener(OfflineListener listener) {
        if (listener != null && mListenerList != null) {
            mListenerList.add(listener);
        }
    }

    public void removeListener(OfflineListener listener) {
        if (listener != null && mListenerList != null) {
            mListenerList.remove(listener);
        }
    }

    public void notifyAllListener() {
        if (mListenerList != null && mListenerList.size() > 0) {
            for (OfflineListener listener : mListenerList) {
                if (listener != null) {
                    listener.offline();
                }
            }
        }
    }
}
