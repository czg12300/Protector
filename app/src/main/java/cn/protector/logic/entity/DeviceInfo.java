package cn.protector.logic.entity;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 描述:设备信息
 *
 * @author jakechen
 * @since 2015/11/10 17:18
 */
public class DeviceInfo implements Serializable {
  // "ID": "设备资料ID",
  // "Eid": "设备条码",
  // "Ename": "昵称",
  // "Relation": "关系编号",
  // "OtherRelation": "自定义关系(Relation为0时有值)",
  // "LastActiveTime": "最后更新时间",
  // "Onoff": "开关状态(0或1)",
  // "Lon": "经度",
  // "Lat:": "纬度",
  // "Address": "所在地址",
  // "EleQuantity": "电量",
  // "Image": "头像"
  private long id;

  private String eId;

  private String nikeName;

  private int relation;

  private String otherRelation;

  private String lastActiveTime;

  private int switchStatus;

  private double lon;

  private double lat;

  private String address;

  private int eleQuantity;

  private String avatar;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String geteId() {
    return eId;
  }

  public void seteId(String eId) {
    this.eId = eId;
  }

  public String getNikeName() {
    return nikeName;
  }

  public void setNikeName(String nikeName) {
    this.nikeName = nikeName;
  }

  public int getRelation() {
    return relation;
  }

  public void setRelation(int relation) {
    this.relation = relation;
  }

  public String getOtherRelation() {
    return otherRelation;
  }

  public void setOtherRelation(String otherRelation) {
    this.otherRelation = otherRelation;
  }

  public String getLastActiveTime() {
    return lastActiveTime;
  }

  public void setLastActiveTime(String lastActiveTime) {
    this.lastActiveTime = lastActiveTime;
  }

  public int getSwitchStatus() {
    return switchStatus;
  }

  public void setSwitchStatus(int switchStatus) {
    this.switchStatus = switchStatus;
  }

  public double getLon() {
    return lon;
  }

  public void setLon(double lon) {
    this.lon = lon;
  }

  public double getLat() {
    return lat;
  }

  public void setLat(double lat) {
    this.lat = lat;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public int getEleQuantity() {
    return eleQuantity;
  }

  public void setEleQuantity(int eleQuantity) {
    this.eleQuantity = eleQuantity;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public static DeviceInfo parse(JSONObject object) {
    if (object == null) {
      return null;
    }
    // "ID": "设备资料ID",
    // "Eid": "设备条码",
    // "Ename": "昵称",
    // "Relation": "关系编号",
    // "OtherRelation": "自定义关系(Relation为0时有值)",
    // "LastActiveTime": "最后更新时间",
    // "Onoff": "开关状态(0或1)",
    // "Lon": "经度",
    // "Lat:": "纬度",
    // "Address": "所在地址",
    // "EleQuantity": "电量",
    // "Image": "头像"
    DeviceInfo info = new DeviceInfo();
    info.setId(object.optLong("ID"));
    info.seteId(object.optString("EID"));
    info.setNikeName(object.optString("Ename"));
    info.setRelation(object.optInt("Relation"));
    info.setOtherRelation(object.optString("OtherRelation"));
    info.setLastActiveTime(object.optString("LastActiveTime"));
    info.setSwitchStatus(object.optInt("Onoff"));
    info.setLon(object.optDouble("Lon"));
    info.setLat(object.optDouble("Lat"));
    info.setAddress(object.optString("Address"));
    info.setEleQuantity(object.optInt("EleQuantity"));
    info.setAvatar(object.optString("Image"));
    return info;
  }

  public static String parseRelation(int relation,String otherRelation) {
    String result = "其他";
    switch (relation) {
      case 0:
        result = otherRelation;
        break;
      case 1:
        result = "爸爸";
        break;
      case 2:
        result = "妈妈";
        break;
      case 3:
        result = "哥哥";
        break;
      case 4:
        result = "姐姐";
        break;
      case 5:
        result = "爷爷";
        break;
      case 6:
        result = "奶奶";
        break;
      case 7:
        result = "公公";
        break;
      case 8:
        result = "婆婆";
        break;
      case 9:
        result = "叔叔";
        break;
      case 10:
        result = "阿姨";
        break;
    }
    return result;
  }
}
