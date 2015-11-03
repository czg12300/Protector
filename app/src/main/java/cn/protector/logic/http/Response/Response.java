
package cn.protector.logic.http.Response;

import cn.common.http.base.BaseResponse;

/**
 * 描述:所有请求结果的返回
 *
 * @author jakechen
 * @since 2015/11/3 11:21
 */
public abstract class Response extends BaseResponse {
    public static final int SUCCESS = 1;

    public static final int FAIL = 0;

    private int result;

    private String info;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
