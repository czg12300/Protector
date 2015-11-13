
package cn.protector.logic.entity;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 描述:点信息
 *
 * @author jakechen
 * @since 2015/11/13 14:21
 */
public class PointInfo implements Serializable {

    private String startTime;

    private String endTime;

    private String address;

    private double lon;

    private double lat;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public PointInfo parse(JSONObject root) {
        if (root == null) {
            return null;
        }
        // "StartTime": "",
        // "EndTime": "",
        // "Lon": "",
        // "Lat": "",
        // "Address": ""
        setStartTime(root.optString("StartTime"));
        setEndTime(root.optString("EndTime"));
        setAddress(root.optString("Address"));
        setLon(root.optDouble("Lon"));
        setLat(root.optDouble("Lat"));
        return this;
    }
}
