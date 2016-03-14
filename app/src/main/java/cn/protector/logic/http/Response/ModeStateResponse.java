package cn.protector.logic.http.response;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 描述:模式数据
 *
 * @author jakechen
 * @since 2015/10/29 15:37
 */
public class ModeStateResponse extends Response {
public static final int MODE_SHUTDOWN=2;
public static final int MODE_SAVE=3;
public static final int MODE_NORMAL=4;
public static final int MODE_FOLLOW=5;

private int currentState;
private int switchState;
private String recordTime;

  public int getCurrentState() {
    return currentState;
  }

  public void setCurrentState(int currentState) {
    this.currentState = currentState;
  }

  public int getSwitchState() {
    return switchState;
  }

  public void setSwitchState(int switchState) {
    this.switchState = switchState;
  }

  public String getRecordTime() {
    return recordTime;
  }

  public void setRecordTime(String recordTime) {
    this.recordTime = recordTime;
  }

  @Override
  public Object parse(String json) {
    if (TextUtils.isEmpty(json)) {
      return null;
    }
    try {
      JSONObject object = new JSONObject(json);
      setResult(object.optInt("Result"));
      setCurrentState(object.optInt("CurrentState"));
      setSwitchState(object.optInt("SwitchStatus"));
      setInfo(object.getString("Info"));
      setIsOk(true);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return this;
  }
}
