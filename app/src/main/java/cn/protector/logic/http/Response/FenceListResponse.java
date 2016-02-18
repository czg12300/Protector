
package cn.protector.logic.http.response;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cn.protector.logic.entity.CareStaffInfo;
import cn.protector.logic.entity.FenceInfo;

/**
 * 描述:围栏列表
 *
 * @author jakechen
 * @since 2015/10/29 15:37
 */
public class FenceListResponse extends Response {
    private ArrayList<FenceInfo> list;

    public ArrayList<FenceInfo> getList() {
        return list;
    }

    public void setList(ArrayList<FenceInfo> list) {
        this.list = list;
    }

    @Override
    public Object parse(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            JSONArray array = new JSONArray(json);
            if (array != null && array.length() > 0) {
                ArrayList<FenceInfo> list = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    FenceInfo info = FenceInfo.parse(array.optJSONObject(i));
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
