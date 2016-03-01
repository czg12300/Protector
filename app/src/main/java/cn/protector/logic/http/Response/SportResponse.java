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
//    {"Eid":"E20150826120345000003" , "WorkDay":"100" ,"AllStepNumber":"96500","AvgStepNumber":"1000","Message":"运动少，多走动对身体发育有帮助" }
    try {
      JSONObject object = new JSONObject(json);
      setEid(object.optString("Eid"));
      setMessage(object.optString("Message"));
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
