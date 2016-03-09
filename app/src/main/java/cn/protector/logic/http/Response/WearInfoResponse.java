package cn.protector.logic.http.response;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 描述:重置密码返回数据
 *
 * @author jakechen
 * @since 2015/11/4 19:38
 */
public class WearInfoResponse extends Response {

  private String eid;
  private String name;
  private String sex;
  private String birthday;
  private int weight;
  private int shoeSize;
  private int relationship;
  private String avatar;
  private String otherRelationship;

  public int getRelationship() {
    return relationship;
  }

  public void setRelationship(int relationship) {
    this.relationship = relationship;
  }

  public String getOtherRelationship() {
    return otherRelationship;
  }

  public void setOtherRelationship(String otherRelationship) {
    this.otherRelationship = otherRelationship;
  }

  public String getEid() {
    return eid;
  }

  public void setEid(String eid) {
    this.eid = eid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public String getBirthday() {
    return birthday;
  }

  public void setBirthday(String birthday) {
    this.birthday = birthday;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public int getShoeSize() {
    return shoeSize;
  }

  public void setShoeSize(int shoeSize) {
    this.shoeSize = shoeSize;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  @Override
  public Object parse(String json) {
    if (TextUtils.isEmpty(json)) {
      return null;
    }
    try {
      JSONObject root = new JSONObject(json);
      setEid(root.optString("Eid"));
      setName(root.optString("Name"));
      setSex(root.optString("Sex"));
      setBirthday(root.optString("Birthday"));
      setAvatar(root.optString("Image"));
      setWeight(root.optInt("Weight"));
      setShoeSize(root.optInt("ShoeSize"));
      setRelationship(root.optInt("Relation"));
      setOtherRelationship(root.optString("OtherRelation"));
    } catch (JSONException e) {
      e.printStackTrace();
    }
    setIsOk(true);
    return this;
  }
}
