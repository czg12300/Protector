package cn.protector.logic.helper;


import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

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

    public DeviceInfo getPositionDeviceInfo() {
        if (positionDeviceInfo==null){
            return getDefaultDevice();
        }
        return positionDeviceInfo;
    }

    public void setPositionDeviceInfo(DeviceInfo positionDeviceInfo) {
        this.positionDeviceInfo = positionDeviceInfo;
    }

    private DeviceInfoHelper() {
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
}
