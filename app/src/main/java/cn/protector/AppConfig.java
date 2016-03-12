
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
    public static final String RELEASE_SERVER = "http://ishoes.e-heneng.com:8005/AppHandler.ashx?Method=";

    /**
     * 服务器地址
     */
    public static final String SERVER = isDebug ? TEST_SERVER : RELEASE_SERVER;
    public static final long AUTO_LOCATE_TIME =  5 * 1000;

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

    // 远程关机
    public static final String COM_SHUT_DOWN = "Com_Shutdown";
    // 上传图片
    public static final String UPLOAD_IMAGE = "UploadImage";
    //  运动数据
    public static final String GET_SPORT_STAT_DATA = "GetSportStatData";
    // 受压数据
    public static final String GET_PRESS_STAT_DATA = "GetPressStatData";
    // 监护人列表
    public static final String GET_EQUI_USER_LIST = "GetEquiUserList";
    //获取围栏列表
    public static final String GET_ELEC_FENCE_LIST = "GetElecFenceList";
    //删除围栏
    public static final String DEL_ELECFENCE = "DelElecFence";
    //设置围栏
    public static final String SET_ELECFENCE = "SetElecFence";
    //获取二维码
    public static final String GET_QR_CODE = "GetQRCode";
    //删除监护人
    public static final String DEL_CUSTODIAN = "DelCustodian";
    //获取设备当前模式
    public static final String COM_GETEQUIPMENTSTATE = "Com_GetEquipmentState";
    //取消切换设备模式
    public static final String CANCEL_UPLOADMODE = "Com_CancelUploadMode";
    //切换设备模式
    public static final String COM_SETUPLOADMODE = "Com_SetUploadMode";
    //获取设备列表
    public static final String GET_BASELIST = "GetBaseList";
    //获取穿戴者信息
    public static final String GET_WEARERINFO = "GetWearerInfo";
    //更新穿戴者信息
    public static final String SET_WEARERINFO = "SetWearerInfo";
    //获取最新位置
    public static final String GET_POSITIONDATA = "GetPositionData";
    //检查二维码是否有效
    public static final String CHECK_EQUIMENTEXIST = "CheckEquimentExist";
    //获取话费余额
    public static final String GET_EQUIBALANCE = "GetEquiBalance";
    //获取微信支付的账单
    public static final String GET_PAY_FORM = "http://ishoes.wxpay.e-heneng.com:8007/JsApiPayPage.aspx";
    public static final String WECHAT_APP_ID = "wxb4ba3c02aa476ea1";
}
