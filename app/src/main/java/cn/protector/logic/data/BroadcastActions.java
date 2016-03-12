
package cn.protector.logic.data;

/**
 * 描述：用于存放广播信息 Created by jakechen on 2015/8/27.
 */
public interface BroadcastActions {
    /**
     * 关闭主页之前的activity
     */
    String ACTION_FINISH_ACTIVITY_BEFORE_MAIN = "cn.protector.data.BroadcastActions.action_finish_activity_before_main";

    /**
     * 注册成功
     */
    String ACTION_REGISTER_SUCCESS = "cn.protector.data.BroadcastActions.action_register_success";

    /**
     * 登录成功
     */
    String ACTION_LOGIN_SUCCESS = "cn.protector.data.BroadcastActions.action_login_success";

    /**
     * 修改头像
     */
    String ACTION_FINISH_USER_INFO_AVATOR = "cn.protector.data.BroadcastActions.action_finish_user_info_avator";

    /**
     * 选择主页的定位tab
     */
    String ACTION_MAIN_ACTIVITY_SELECT_TAB_LOCATE = "cn.protector.data.BroadcastActions.action_main_activity_select_tab_locate";

    /**
     * 选择不同的设备
     */
    String ACTION_MAIN_DEVICE_CHANGE = "cn.protector.data.BroadcastActions.action_main_device_change";
    /**
     * 普通消息
     */
    String ACTION_PUSH_COMMON_MESSAGE = "cn.protector.data.BroadcastActions.action_push_common_message";
    /**
     * 添加围栏成功
     */
    String ACTION_ADD_FENCE_SUCCESS = "cn.protector.data.BroadcastActions.action_add_fence_success";
    /**
     * 修改穿戴者信息成功
     */
    String ACTION_MODIFY_WEAR_INFO_SUCCESS = "cn.protector.data.BroadcastActions.ACTION_MODIFY_WEAR_INFO_SUCCESS";
    /**
     * 更新当前设备数据
     */
    String ACTION_UPDATE_POSITION_DEVICE_INFO = "cn.protector.data.BroadcastActions.ACTION_UPDATE_POSITION_DEVICE_INFO";
    /**
     * 更新设备列表数据
     */
    String ACTION_UPDATE_DEVICE_LIST_INFO = "cn.protector.data.BroadcastActions.ACTION_UPDATE_DEVICE_LIST_INFO";
    /**
     * 更新实时定位的数据
     */
    String ACTION_PUSH_REAL_TIME_LOCATE_DATA = "cn.protector.data.BroadcastActions.ACTION_PUSH_REAL_TIME_LOCATE_DATA";
    /**
     * 话费充值成功
     */
    String ACTION_RECHARGE_SUCCESS = "cn.protector.data.BroadcastActions.ACTION_RECHARGE_SUCCESS";


}
