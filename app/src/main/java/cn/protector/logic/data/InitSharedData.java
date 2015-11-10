
package cn.protector.logic.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import cn.protector.ProtectorApplication;

/**
 * 存放初始化的值 Created by Administrator on 2015/8/15.
 */
public final class InitSharedData {

    private InitSharedData() {
    }

    /**
     * 文件名
     */
    private static final String FILE_NAME = "init";

    private static final String KEY_IS_NEW_VOICE = "key_is_new_voice";

    private static final String KEY_USER_ID = "keyUserId";

    private static final String KEY_USER_CODE = "keyCode";

    private static final String KEY_MOBILE = "keyMobile";

    private static final String KEY_PASSWORD = "keyPassWord";

    private static final String KEY_DEVICE_IDS = "key_device_ids";

    public static void setDeviceIds(String code) {
        getSharedPreferences().edit().putString(KEY_DEVICE_IDS, code).commit();
    }

    public static String getDeviceIds() {
        return getSharedPreferences().getString(KEY_DEVICE_IDS, null);
    }

    public static void setUserCode(String code) {
        getSharedPreferences().edit().putString(KEY_USER_CODE, code).commit();
    }

    public static String getUserCode() {
        return getSharedPreferences().getString(KEY_USER_CODE, null);
    }

    public static void setPassword(String pw) {
        getSharedPreferences().edit().putString(KEY_PASSWORD, pw).commit();
    }

    public static String getPassword() {
        return getSharedPreferences().getString(KEY_PASSWORD, null);
    }

    public static void setMobile(String mobile) {
        getSharedPreferences().edit().putString(KEY_MOBILE, mobile).commit();
    }

    public static String getMobile() {
        return getSharedPreferences().getString(KEY_MOBILE, null);
    }

    public static void setUserId(String id) {
        getSharedPreferences().edit().putString(KEY_USER_ID, id).commit();
    }

    public static String getUserId() {
        return getSharedPreferences().getString(KEY_USER_ID, null);
    }

    public static boolean hasLogin() {
        return !TextUtils.isEmpty(getMobile()) && !TextUtils.isEmpty(getPassword());
    }

    public static boolean isNewVoice(int id) {
        String save = getSharedPreferences().getString(KEY_IS_NEW_VOICE, "");
        if (save.contains("#" + id + "#")) {
            return false;
        }
        return true;
    }

    public static void setNewVoice(int id) {
        String save = getSharedPreferences().getString(KEY_IS_NEW_VOICE, "") + "#" + id + "#";
        getSharedPreferences().edit().putString(KEY_IS_NEW_VOICE, save).commit();
    }

    /**
     * 获取sharePreference的编辑器
     *
     * @return
     */
    private static SharedPreferences getSharedPreferences() {
        return ProtectorApplication.getInstance().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
    }
}
