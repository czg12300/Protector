package cn.protector.logic.http.response;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.protector.logic.entity.DeviceInfo;

/**
 * 描述:设备列表数据
 *
 * @author jakechen
 * @since 2015/10/29 15:37
 */
public class DeviceListResponse extends Response {

  private ArrayList<DeviceInfo> list;

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
    try {
      JSONObject root = new JSONObject(json);
      JSONArray array = root.optJSONArray("Data");
      if (array != null && array.length() > 0) {
        ArrayList<DeviceInfo> infos = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
          DeviceInfo info = DeviceInfo.parse(array.optJSONObject(i));
          if (info != null) {
            infos.add(info);
          }
        }
        setList(infos);
      }
      if (list != null && list.size() > 0) {
        setResult(1);
      }
      setIsOk(true);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return this;
  }
}
