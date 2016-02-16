package cn.protector.logic.http.response;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 描述：当前设备状态返回数据
 *
 * @author jakechen
 * @since 2015/10/29 15:37
 */
public class NowDeviceInfoResponse extends Response {

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

    private String avatar;
    private String phoneNum;
    private int posiPrecision;
    private int posiMode;
    private int updateMode;
    private double speed;
    private int stepNum;
    private double hellPress;
    private double hellPressAvg;
    private double solePress;
    private double solePressAvg;
    private int logined;

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
        if (Double.isNaN(lon)) {
            lon = 0;
        }
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        if (Double.isNaN(lat)) {
            lat = 0;
        }
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

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public int getPosiPrecision() {
        return posiPrecision;
    }

    public void setPosiPrecision(int posiPrecision) {
        this.posiPrecision = posiPrecision;
    }

    public int getPosiMode() {
        return posiMode;
    }

    public void setPosiMode(int posiMode) {
        this.posiMode = posiMode;
    }

    public int getUpdateMode() {
        return updateMode;
    }

    public void setUpdateMode(int updateMode) {
        this.updateMode = updateMode;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        if (Double.isNaN(speed)) {
            speed = 0;
        }
        this.speed = speed;
    }

    public int getStepNum() {
        return stepNum;
    }

    public void setStepNum(int stepNum) {
        this.stepNum = stepNum;
    }

    public double getHellPress() {
        return hellPress;
    }

    public void setHellPress(double hellPress) {
        if (Double.isNaN(hellPress)) {
            hellPress = 0;
        }
        this.hellPress = hellPress;
    }

    public double getHellPressAvg() {
        return hellPressAvg;
    }

    public void setHellPressAvg(double hellPressAvg) {
        if (Double.isNaN(hellPressAvg)) {
            hellPressAvg = 0;
        }
        this.hellPressAvg = hellPressAvg;
    }

    public double getSolePress() {
        return solePress;
    }

    public void setSolePress(double solePress) {
        if (Double.isNaN(solePress)) {
            solePress = 0;
        }
        this.solePress = solePress;
    }

    public double getSolePressAvg() {
        return solePressAvg;
    }

    public void setSolePressAvg(double solePressAvg) {
        if (Double.isNaN(solePressAvg)) {
            solePressAvg = 0;
        }
        this.solePressAvg = solePressAvg;
    }

    public int getLogined() {
        return logined;
    }

    public void setLogined(int logined) {
        this.logined = logined;
    }

    @Override
    public NowDeviceInfoResponse parse(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
//        "ID": "1",
//                "EID": "E2014110110013000001",
//                "Ename": "ww",
//                "Relation": "1",
//                "OtherRelation": "0",
//                "Image": "../image/3.png",
//                "SIM": "150123123123",
//                "Logined": "1",
//                "LastActiveTime": "2015/11/12 23:20:12",
//                "Onoff": "1",
//                "Lon": "113.293348",
//                "Lat": "22.80913",
//                "Address": "广东省佛山市顺德区大良街道苏龙街五巷1号",
//                "EleQuantity": "20",
//                "PosiPrecision": "",
//                "PosiMode": "",
//                "UploadMode": "",
//                "Speed": "",
//                "StepNum": "",
//                "HeelPress": "",
//                "HeelPressAvg": "",
//                "SolePress": "",
//                "SolePressAvg": ""
        try {
            JSONObject object = new JSONObject(json);
            setId(object.optLong("ID"));
            seteId(object.optString("EID"));
            setNikeName(object.optString("Ename"));
            setRelation(object.optInt("Relation"));
            setOtherRelation(object.optInt("OtherRelation"));
            setLastActiveTime(object.optString("LastActiveTime"));
            setSwitchStatus(object.optInt("Onoff"));
            setLon(object.optDouble("Lon"));
            setLat(object.optDouble("Lat"));
            setAddress(object.optString("Address"));
            setEleQuantity(object.optInt("EleQuantity"));
            setAvatar(object.optString("Image"));
            setPhoneNum(object.optString("SIM"));
            setPosiPrecision(object.optInt("PosiPrecision"));
            setPosiMode(object.optInt("PosiMode"));
            setUpdateMode(object.optInt("UploadMode"));
            setSpeed(object.optDouble("Speed"));
            setStepNum(object.optInt("StepNum"));
            setHellPress(object.optDouble("HeelPress"));
            setHellPressAvg(object.optDouble("HeelPressAvg"));
            setSolePress(object.optDouble("SolePress"));
            setSolePressAvg(object.optDouble("SolePressAvg"));
            setLogined(object.optInt("Logined"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setIsOk(true);
        return this;
    }
}
