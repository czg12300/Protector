
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


}
