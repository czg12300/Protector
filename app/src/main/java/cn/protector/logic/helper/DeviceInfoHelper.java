package cn.protector.logic.helper;


import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cn.common.bitmap.core.ImageLoader;
import cn.common.bitmap.core.assist.FailReason;
import cn.common.bitmap.core.listener.ImageLoadingListener;
import cn.common.utils.BitmapUtil;
import cn.protector.ProtectorApplication;
import cn.protector.R;
import cn.protector.logic.data.InitSharedData;
import cn.protector.logic.entity.DeviceInfo;

/**
 * 描述:用于保持设备信息
 *
 * @author jakechen
 * @since 2015/11/11 16:42
 */
public class DeviceInfoHelper {
    private static DeviceInfoHelper mInstance;
    private ArrayList<DeviceInfo> list;
    private DeviceInfo positionDeviceInfo;
    private Bitmap bmAvatar;
    private String lastAvatar;
    private ArrayList<IAvatarListener> listeners;

    public void addListener(IAvatarListener listener) {
        listeners.add(listener);
    }

    public void removeListener(IAvatarListener listener) {
        listeners.remove(listener);
    }

    public Bitmap getAvatar() {
        int wh = (int) ProtectorApplication.getInstance().getResources().getDimension(R.dimen.avatar);
        if (bmAvatar == null) {
            bmAvatar = BitmapUtil.decodeResource(R.drawable.img_head_baby, wh, wh);
        }
        return bmAvatar.copy(bmAvatar.getConfig(),false);
    }

    public DeviceInfo getPositionDeviceInfo() {
        if (positionDeviceInfo == null) {
            return getDefaultDevice();
        }
        return positionDeviceInfo;
    }

    public void setPositionDeviceInfo(DeviceInfo info) {
        this.positionDeviceInfo = info;
        if (!TextUtils.equals(lastAvatar, info.getAvatar())) {
            lastAvatar = info.getAvatar();
            ImageLoader.getInstance().loadImage(info.getAvatar(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    lastAvatar = null;
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    lastAvatar = null;
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    int wh = (int) ProtectorApplication.getInstance().getResources().getDimension(R.dimen.avatar);
                    bmAvatar = BitmapUtil.scale(loadedImage, wh, wh);
                    for (IAvatarListener listener : listeners) {
                        if (listener != null) {
                            listener.loadBitmap(bmAvatar.copy(bmAvatar.getConfig(), false));
                        }
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    lastAvatar = null;
                }
            });
        }
    }

    private DeviceInfoHelper() {
        listeners = new ArrayList<>();
        if (!TextUtils.isEmpty(InitSharedData.getDeviceData())) {
            try {
                JSONArray array = new JSONArray(InitSharedData.getDeviceData());
                if (array != null && array.length() > 0) {

                    list = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        DeviceInfo info = new DeviceInfo().parse(array.optJSONObject(i));
                        if (info != null) {
                            list.add(info);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static DeviceInfoHelper getInstance() {
        if (mInstance == null) {
            mInstance = new DeviceInfoHelper();
        }
        return mInstance;
    }

    public boolean hasAnyDevice() {
        return list != null && list.size() > 0;
    }

    public DeviceInfo getDefaultDevice() {
        if (hasAnyDevice()) {
            return list.get(0);
        }
        return null;
    }

    public DeviceInfo getDeviceById(long id) {
        if (hasAnyDevice()) {
            for (DeviceInfo info : list
                    ) {
                if (info != null && info.getId() == id) {
                    return info;
                }

            }
        }
        return null;
    }

    public ArrayList<DeviceInfo> getDeviceList() {
        if (hasAnyDevice()) {
            return list;
        }
        return null;
    }

    public static interface IAvatarListener {
        void loadBitmap(Bitmap bitmap);
    }
}
