
package cn.protector.logic.entity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 描述:一小时内的所有点信息
 *
 * @author jakechen
 * @since 2015/11/13 14:30
 */
public class HourPointsInfo implements Serializable {
    private int hour;

    private ArrayList<PointInfo> pointList;

    public ArrayList<PointInfo> getPointList() {
        return pointList;
    }

    public void setPointList(ArrayList<PointInfo> pointList) {
        this.pointList = pointList;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public HourPointsInfo parse(JSONObject root) {
        if (root == null) {
            return null;
        }
        setHour(root.optInt("hour"));
        JSONArray array = root.optJSONArray("Point");
        if (array != null && array.length() > 0) {
            ArrayList<PointInfo> list = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                PointInfo info = new PointInfo().parse(array.optJSONObject(i));
                if (info != null) {
                    list.add(info);
                }
            }
            setPointList(list);
        }
        return this;
    }
}
