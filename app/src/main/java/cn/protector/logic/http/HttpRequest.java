
package cn.protector.logic.http;

import java.util.Hashtable;

import cn.common.http.base.BaseHttpClientRequest;

/**
 * 描述:http请求
 *
 * @author jakechen
 * @since 2015/10/23 15:09
 */
public class HttpRequest<T> extends BaseHttpClientRequest<T> {
    public HttpRequest(String svc, Class<?> clazz) {
        super(svc, clazz);
    }

    @Override
    protected void addCommonParam() {
    }

    @Override
    protected String getServerUrl() {
        return null;
    }

    @Override
    protected Hashtable<String, String> getRequestHeaders() {
        return null;
    }
}
