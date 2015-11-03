
package cn.protector.logic.entity;

import cn.common.http.base.BaseResponse;

/**
 * 描述：宝贝信息实体类
 *
 * @author Created by Administrator on 2015/9/4.
 */
public class ChatMessage extends BaseResponse {
    public static final int TYPE_MESSAGE = 1;

    public static final int TYPE_VOICE = 2;

    public String name;

    public String avator;

    public String time;

    public String message;

    public int type;

    public String voice;

    public int id;

    @Override
    public ChatMessage parse(String json) {
        return this;
    }
}
