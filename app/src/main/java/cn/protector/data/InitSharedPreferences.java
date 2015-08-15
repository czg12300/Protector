package cn.protector.data;

import android.content.Context;
import android.content.SharedPreferences;

import cn.protector.ProtectorApplication;

/**
 * 存放初始化的值
 * Created by Administrator on 2015/8/15.
 */
public final class InitSharedPreferences {

    private InitSharedPreferences() {
    }

    /**
     * 文件名
     */
    private static final String FILE_NAME = "init";
    /**
     * 判断是否第一次进入
     */
    private static final String KEY_IS_FIRST_IN = "isFirstIn";
    private static final String key_has = "isFirstIn";

    public static void setIsFirstIn(boolean isFirstIn) {
        getSharedPreferences().edit().putBoolean(KEY_IS_FIRST_IN, isFirstIn).commit();
    }

    public static boolean isFirstIn() {
        return getSharedPreferences().getBoolean(KEY_IS_FIRST_IN, true);
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
