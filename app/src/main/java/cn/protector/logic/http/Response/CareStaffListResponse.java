package cn.protector.logic.http.response;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.protector.logic.entity.CareStaffInfo;

/**
 * 描述:监护人列表
 *
 * @author jakechen
 * @since 2015/10/29 15:37
 */
public class CareStaffListResponse extends Response {
  // {"Data":[记录内容, 记录内容, 记录内容] ,"Logined":"0/1"}
  private ArrayList<CareStaffInfo> list;

  public ArrayList<CareStaffInfo> getList() {
    return list;
  }

  private int isAdmin;

  public int getIsAdmin() {
    return isAdmin;
  }

  public boolean isAdmin() {
    return isAdmin == 1;
  }

  public void setIsAdmin(int isAdmin) {
    this.isAdmin = isAdmin;
  }

  public void setList(ArrayList<CareStaffInfo> list) {
    this.list = list;
  }

  @Override
  public Object parse(String json) {
    if (TextUtils.isEmpty(json)) {
      return null;
    }
    try {
      JSONObject root = new JSONObject(json);
      setIsAdmin(root.optInt("IsAdmin"));
      JSONArray array = root.optJSONArray("Data");
      if (array != null && array.length() > 0) {
        ArrayList<CareStaffInfo> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
          CareStaffInfo info = CareStaffInfo.parse(array.optJSONObject(i));
          if (info != null) {
            list.add(info);
          }
        }
        if (list.size() > 0) {
          setList(list);
        }
      }
      setIsOk(true);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return this;
  }
}
