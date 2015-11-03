
package cn.protector.logic.entity;

import cn.common.http.base.BaseResponse;

/**
 * 描述：围栏信息信息实体类
 *
 * @author Created by Administrator on 2015/9/4.
 */
public class FenceInfo extends BaseResponse {
    public String name;

    public String address;

    public int range;

    @Override
    public FenceInfo parse(String json) {
        return this;
    }
}
