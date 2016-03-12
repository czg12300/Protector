
package cn.protector.logic.http.response;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 描述:微信支付的账单
 *
 * @author jakechen
 * @since 2015/11/13 10:46
 */
public class WeChatPayFormResponse extends Response {
    private String appid;
    private String noncestr;
    private String _package;
    private String partnerid;
    private String prepayid;
    private String sign;
    private String timestamp;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String get_package() {
        return _package;
    }

    public void set_package(String _package) {
        this._package = _package;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public WeChatPayFormResponse parse(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            JSONObject root = new JSONObject(json);
//            appid 公众账号ID String(32) wxc4cf91bb330bb376
//            noncestr 随机字符串 String(32) e327d161785b4844be918e940d368050
//            package 扩展字段 String(128) Sign=WXPay
//            partnerid 商户号 String(32) 1298801601
//            prepayid 预支付交易会话ID String(32) wx20151222145110d1b1e68fb40412283872
//            sign sign String(32) 4560A65B2061DE15354E3B3DCB02D786
//                    timestamp
            setAppid(root.optString("appid"));
            setNoncestr(root.optString("noncestr"));
            set_package(root.optString("package"));
            setPartnerid(root.optString("partnerid"));
            setPrepayid(root.optString("prepayid"));
            setSign(root.optString("sign"));
            setTimestamp(root.optString("timestamp"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setIsOk(true);
        return this;
    }
}
