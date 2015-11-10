
package cn.protector.logic.http.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import cn.common.http.base.BaseResponse;
import cn.protector.logic.entity.DeviceInfo;

import java.util.ArrayList;

/**
 * 描述:登录返回数据
 *
 * @author jakechen
 * @since 2015/10/29 15:37
 */
public class GetBaseListResponse extends BaseResponse {
    private int logined;

    private ArrayList<DeviceInfo> list;

    private String json;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public int getLogined() {
        return logined;
    }

    public void setLogined(int logined) {
        this.logined = logined;
    }

    public ArrayList<DeviceInfo> getList() {
        return list;
    }

    public void setList(ArrayList<DeviceInfo> list) {
        this.list = list;
    }

    @Override
    public Object parse(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        setJson(json);
        try {
            JSONObject object = new JSONObject(json);
            JSONArray array = object.optJSONArray("Data");
            if (array != null && array.length() > 0) {
                ArrayList<DeviceInfo> deviceInfos = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    DeviceInfo info = new DeviceInfo().parse(array.optJSONObject(i));
                    if (info != null) {
                        deviceInfos.add(info);
                    }
                }
                setList(deviceInfos);
            }
            setLogined(object.optInt("Logined"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setIsOk(true);
        return this;
    }
}
