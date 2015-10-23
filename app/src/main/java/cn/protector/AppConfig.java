
package cn.protector;

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
    public static final boolean isDebug = false;

    /**
     * 测试服务器地址
     */
    public static final String TEST_SERVER = "";

    /**
     * 正式服务器地址
     */
    public static final String RELEASE_SERVER = "";

    /**
     * 服务器地址
     */
    public static final String SERVER = isDebug ? TEST_SERVER : RELEASE_SERVER;

}
