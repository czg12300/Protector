package cn.protector.data;

import android.content.Context;
import android.content.SharedPreferences;

import cn.protector.ProtectorApplication;

/**
 * 存放初始化的值
 * Created by Administrator on 2015/8/15.
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
    private static final String KEY_MOBILE = "keyMobile";

    public static void setMobile(String mobile) {
        getSharedPreferences().edit().putString(KEY_MOBILE, mobile).commit();
    }

    public static String getMobile() {
        return getSharedPreferences().getString(KEY_MOBILE, null);
    }

    public static void setUserId(long id) {
        getSharedPreferences().edit().putLong(KEY_USER_ID, id).commit();
    }

    public static long getUserId() {
        return getSharedPreferences().getLong(KEY_USER_ID, -1);
    }

    public static boolean isLogin() {
        boolean isNotLogin = getUserId() < 0;
        return !isNotLogin;
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
        return ProtectorApplication.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }
}
