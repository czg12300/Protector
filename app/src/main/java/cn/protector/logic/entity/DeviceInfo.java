
package cn.protector.logic.entity;

import org.json.JSONObject;

/**
 * 描述:设备信息
 *
 * @author jakechen
 * @since 2015/11/10 17:18
 */
public class DeviceInfo {
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

    private int otherRelation;

    private String lastActiveTime;

    private int switchStatus;

    private double lon;

    private double lat;

    private String address;

    private int eleQuantity;

    private String avator;

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

    public int getOtherRelation() {
        return otherRelation;
    }

    public void setOtherRelation(int otherRelation) {
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

    public String getAvator() {
        return avator;
    }

    public void setAvator(String avator) {
        this.avator = avator;
    }

    public DeviceInfo parse(JSONObject object) {
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
        setId(object.optLong("ID"));
        seteId(object.optString("Eid"));
        setNikeName(object.optString("Ename"));
        setRelation(object.optInt("Relation"));
        setOtherRelation(object.optInt("OtherRelation"));
        setLastActiveTime(object.optString("LastActiveTime"));
        setSwitchStatus(object.optInt("Onoff"));
        setLon(object.optDouble("Lon"));
        setLat(object.optDouble("Lat"));
        setAddress(object.optString("Address"));
        setEleQuantity(object.optInt("EleQuantity"));
        setAvator(object.optString("Image"));
        return this;
    }
}
