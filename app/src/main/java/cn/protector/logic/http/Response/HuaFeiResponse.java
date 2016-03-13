
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
public class HuaFeiResponse extends Response {


    private String eid;
    private int balance;
    private int expectTime;

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getExpectTime() {
        return expectTime;
    }

    public void setExpectTime(int expectTime) {
        this.expectTime = expectTime;
    }

    @Override
    public HuaFeiResponse parse(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            JSONObject root = new JSONObject(json);
            JSONObject object = root.optJSONObject("Data");
            setEid(object.optString("Eid"));
            setBalance(object.optInt("Balance"));
            setExpectTime(object.optInt("ExpectTime"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setIsOk(true);
        return this;
    }
}
