
package cn.protector;

import cn.common.AppException;

/**
 * 描述:所有的配置信息
 *
 * @author jakechen
 * @since 2015/10/23 15:12
 */
public class AppConfig {
    /**
     * 当前运行版本是否为测试包
     */
    public static final boolean isDebug = true;

    /**
     * 测试服务器地址
     */
    public static final String TEST_SERVER = "http://ishoes.e-heneng.com:8005/AppHandler.ashx?Method=";

    /**
     * 正式服务器地址
     */
    public static final String RELEASE_SERVER = "";

    /**
     * 服务器地址
     */
    public static final String SERVER = isDebug ? TEST_SERVER : RELEASE_SERVER;
    public static final long REFRESH_POSITION_DEVICE_STATUS_SPIT_TIME_TEST = 5 * 1000;
    public static final long REFRESH_POSITION_DEVICE_STATUS_SPIT_TIME_RELEASE = 1 * 60 * 1000;
    public static final long REFRESH_POSITION_DEVICE_STATUS_SPIT_TIME = isDebug ? REFRESH_POSITION_DEVICE_STATUS_SPIT_TIME_TEST : REFRESH_POSITION_DEVICE_STATUS_SPIT_TIME_RELEASE;

    static {
        AppException.setDebug(isDebug);
    }

    // 登录接口
    public static final String LOGIN = "SystemLogin";

    // 注册接口
    public static final String REGISTER = "UserRegistration";

    // 获取短信验证码
    public static final String SEND_MESSAGE_CODE = "GetMsgValidateCode";

    // 心跳包
    public static final String HEART_BEAT = "LoginHeartbeat";

    // 重置密码
    public static final String MODIFY_PASSWORD = "ResetPwd";

    // 获取设备列表
    public static final String GET_BASE_LIST = "GetBaseList";

    // 检查登录
    public static final String CHECK_LOGINED = "CheckLogined";
    //获取设备当前状态信息
    public static final String GET_NOW_DATA = "GetNowData";
    //实时定位
    public static final String COM_GEO_LOCATION = "Com_GeoLocation";

    // 历史轨迹
    public static final String GET_HISTORY_POSI = "GetHistoryPosi";
}
