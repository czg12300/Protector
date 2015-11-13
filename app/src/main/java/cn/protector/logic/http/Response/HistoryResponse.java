
package cn.protector.logic.http.response;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.protector.logic.entity.HourPointsInfo;

/**
 * 描述:历史轨迹返回数据
 *
 * @author jakechen
 * @since 2015/11/13 10:46
 */
public class HistoryResponse extends Response {
    private ArrayList<HourPointsInfo> list;

    private String eid;

    private int logined;

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public int getLogined() {
        return logined;
    }

    public void setLogined(int logined) {
        this.logined = logined;
    }

    public ArrayList<HourPointsInfo> getList() {
        return list;
    }

    public void setList(ArrayList<HourPointsInfo> list) {
        this.list = list;
    }

    @Override
    public HistoryResponse parse(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            JSONObject object = new JSONObject(json);
            setEid(object.optString("eid"));
            setLogined(object.optInt("Logined"));
            JSONArray array = object.optJSONArray("Data");
            if (array != null && array.length() > 0) {
                ArrayList<HourPointsInfo> listTemp = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    HourPointsInfo info = new HourPointsInfo().parse(array.optJSONObject(i));
                    if (info != null) {
                        listTemp.add(info);
                    }
                }
                setList(listTemp);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        setIsOk(true);
        return this;
    }
}
