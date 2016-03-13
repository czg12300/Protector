
package cn.protector.logic.http.response;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 描述:实时定位返回
 *
 * @author jakechen
 * @since 2015/11/12 15:37
 */
public class LocateResponse extends Response {
    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public Object parse(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            JSONObject object = new JSONObject(json);
            setResult(object.optInt("Result"));
            setInfo(object.optString("Info"));
            setState(object.optInt("State"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setIsOk(true);
        return this;
    }
}