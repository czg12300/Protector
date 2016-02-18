
package cn.protector.logic.entity;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 描述:监护人
 *
 * @author jakechen
 * @since 2016/2/18 18:20
 */
public class CareStaffInfo implements Serializable {

    private String phoneNum;

    private String bindDate;

    private String relation;

    private String nickName;

    private int manager;

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getBindDate() {
        return bindDate;
    }

    public void setBindDate(String bindDate) {
        this.bindDate = bindDate;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getManager() {
        return manager;
    }

    public void setManager(int manager) {
        this.manager = manager;
    }

    public boolean isManager() {
        return manager == 1;
    }

    public static CareStaffInfo parse(JSONObject root) {
        CareStaffInfo info = null;
        if (root != null) {
            info = new CareStaffInfo();
            // { "LoginName":"监护人手机号码","Date":"绑定日期","Relation":"与设备使用者的关系" ，
            // "NickName":"使用者昵称"， "Manager":"是否管理员(1是/0不是)" }
            info.setBindDate(root.optString("Date"));
            info.setManager(root.optInt("Manager"));
            info.setNickName(root.optString("NickName"));
            info.setPhoneNum(root.optString("LoginName"));
            info.setRelation(root.optString("Relation"));
        }
        return info;
    }
}
