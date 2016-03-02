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
public class SportResponse extends Response {

  private String eid;
  private int workDay;
  private int allStepNumber;
  private int avgStepNumber;
  private String message;
  private String activityUrl;
  private String activityPicture;

  public String getActivityPicture() {
    return activityPicture;
  }

  public void setActivityPicture(String activityPicture) {
    this.activityPicture = activityPicture;
  }

  public String getActivityUrl() {
    return activityUrl;
  }

  public void setActivityUrl(String activityUrl) {
    this.activityUrl = activityUrl;
  }

  public String getEid() {
    return eid;
  }

  public void setEid(String eid) {
    this.eid = eid;
  }

  public int getWorkDay() {
    return workDay;
  }

  public void setWorkDay(int workDay) {
    this.workDay = workDay;
  }

  public int getAllStepNumber() {
    return allStepNumber;
  }

  public void setAllStepNumber(int allStepNumber) {
    this.allStepNumber = allStepNumber;
  }

  public int getAvgStepNumber() {
    return avgStepNumber;
  }

  public void setAvgStepNumber(int avgStepNumber) {
    this.avgStepNumber = avgStepNumber;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public Object parse(String json) {
    if (TextUtils.isEmpty(json)) {
      return null;
    }
    try {
      JSONObject object = new JSONObject(json);
      setEid(object.optString("Eid"));
      setMessage(object.optString("Message"));
      setActivityUrl(object.optString("ActivityURL"));
      setActivityPicture(object.optString("ActivityPicture"));
      setWorkDay(object.optInt("WorkDay"));
      setAllStepNumber(object.optInt("AllStepNumber"));
      setAvgStepNumber(object.optInt("AvgStepNumber"));
      setIsOk(true);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return this;
  }
}
