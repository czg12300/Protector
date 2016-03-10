
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
private int posiMode;

    public int getPosiMode() {
        return posiMode;
    }

    public void setPosiMode(int posiMode) {
        this.posiMode = posiMode;
    }

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
    public PointInfo parse(JSONObject root) {
        if (root == null) {
            return null;
        }
        setStartTime(root.optString("StartTime"));
        setEndTime(root.optString("EndTime"));
        setAddress(root.optString("Address"));
        setLon(root.optDouble("Lon"));
        setLat(root.optDouble("Lat"));
        setPosiMode(root.optInt("PosiMode"));
        return this;
    }
}
