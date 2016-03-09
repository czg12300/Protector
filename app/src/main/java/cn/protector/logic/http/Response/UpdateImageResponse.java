
package cn.protector.logic.http.response;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 描述:通用的返回
 *
 * @author jakechen
 * @since 2015/11/3 11:29
 */
public class UpdateImageResponse extends Response {
private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
            setImageUrl(object.optString("Image"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setIsOk(true);
        return this;
    }
}
