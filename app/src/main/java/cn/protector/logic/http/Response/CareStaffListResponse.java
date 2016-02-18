
package cn.protector.logic.http.response;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.protector.logic.entity.CareStaffInfo;

/**
 * 描述:登录返回数据
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

        } catch (JSONException e) {
            e.printStackTrace();
        }
        setIsOk(true);
        return this;
    }
}
