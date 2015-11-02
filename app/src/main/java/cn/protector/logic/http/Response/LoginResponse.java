
package cn.protector.logic.http.Response;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import cn.common.http.base.BaseResponse;

/**
 * 描述:登录返回数据
 *
 * @author jakechen
 * @since 2015/10/29 15:37
 */
public class LoginResponse extends BaseResponse {
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
    public static final int SUCCESS = 1;

    public static final int FAIL = 0;

    private int result;

    private String info;

    private String code;

    private String userId;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

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
