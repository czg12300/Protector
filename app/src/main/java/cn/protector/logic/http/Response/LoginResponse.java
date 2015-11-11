
package cn.protector.logic.http.response;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 描述:登录返回数据
 *
 * @author jakechen
 * @since 2015/10/29 15:37
 */
public class LoginResponse extends Response {
    // {
    //
    // "Result":"1",
    //
    // "Info":"",
    //
    // Code:"授权码",
    //
    // UserID:"用户ID"
    //
    // }

    private String code;

    private String userId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
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
            setCode(object.optString("Code"));
            setUserId(object.optString("UserID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setIsOk(true);
        return this;
    }
}
