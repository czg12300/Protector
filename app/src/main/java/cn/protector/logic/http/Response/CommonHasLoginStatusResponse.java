
package cn.protector.logic.http.response;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

/**
 * 描述:实时定位返回
 *
 * @author jakechen
 * @since 2015/11/12 15:37
 */
public class CommonHasLoginStatusResponse extends Response {
    private int logined;

    public int getLogined() {
        return logined;
    }

    public void setLogined(int logined) {
        this.logined = logined;
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
            setLogined(object.optInt("Logined"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setIsOk(true);
        return this;
    }
}
