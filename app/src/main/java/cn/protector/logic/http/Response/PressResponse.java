
package cn.protector.logic.http.response;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 描述:压力数据
 *
 * @author jakechen
 * @since 2015/10/29 15:37
 */
public class PressResponse extends Response {

    private String eid;
    private int frontNormalPress;
    private int frontAvgPress;
    private int backNormalPress;
    private int backAvgPress;
    private String message;

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public int getFrontNormalPress() {
        return frontNormalPress;
    }

    public void setFrontNormalPress(int frontNormalPress) {
        this.frontNormalPress = frontNormalPress;
    }

    public int getFrontAvgPress() {
        return frontAvgPress;
    }

    public void setFrontAvgPress(int frontAvgPress) {
        this.frontAvgPress = frontAvgPress;
    }

    public int getBackNormalPress() {
        return backNormalPress;
    }

    public void setBackNormalPress(int backNormalPress) {
        this.backNormalPress = backNormalPress;
    }

    public int getBackAvgPress() {
        return backAvgPress;
    }

    public void setBackAvgPress(int backAvgPress) {
        this.backAvgPress = backAvgPress;
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
        //    {"Eid":"E20150826120345000003" , "FrontNormalPress":"20",
        // "FrontAvgPress":"12" ,"BackNormalPress":"25" ,"BackAvgPress":"30" ,
        // "Message":"脚后跟受力较多，建议穿加厚鞋垫"  }
        try {
            JSONObject object = new JSONObject(json);
            setEid(object.optString("Eid"));
            setMessage(object.optString("Message"));
            setFrontNormalPress(object.optInt("FrontNormalPress"));
            setFrontAvgPress(object.optInt("FrontAvgPress"));
            setBackNormalPress(object.optInt("BackNormalPress"));
            setBackAvgPress(object.optInt("BackAvgPress"));
            setIsOk(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }
}
