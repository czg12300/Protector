package cn.protector.logic.http.response;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 描述:运动数据
 *
 * @author jakechen
 * @since 2015/10/29 15:37
 */
public class QACodeResponse extends Response {
  private String codeURL;

  public String getCodeURL() {
    return codeURL;
  }

  public void setCodeURL(String codeURL) {
    this.codeURL = codeURL;
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
      setCodeURL(object.optString("CodeURL"));
      setIsOk(true);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return this;
  }
}
