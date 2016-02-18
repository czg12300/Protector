
package cn.protector.logic.entity;

import org.json.JSONObject;

import java.io.Serializable;

import cn.common.http.base.BaseResponse;

/**
 * 描述：围栏信息信息实体类
 *
 * @author Created by Administrator on 2015/9/4.
 */
public class FenceInfo implements Serializable {

    private String rid;
    private String creater;
    private String name;
    private String eid;
    private String recordDate;
    private double lon;
    private double lat;
    private int radius;

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
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

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public static FenceInfo parse(JSONObject root) {
        FenceInfo info = null;
        if (root != null) {
            info = new FenceInfo();
            info.setRid(root.optString("Rid"));
            info.setCreater(root.optString("Creater"));
            info.setName(root.optString("Name"));
            info.setRecordDate(root.optString("RecordDate"));
            info.setEid(root.optString("Eid"));
            info.setLat(root.optDouble("Lat"));
            info.setLon(root.optDouble("Lon"));
            info.setRadius(root.optInt("Radius"));
        }
        return info;
    }
}
