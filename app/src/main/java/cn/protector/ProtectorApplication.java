
package cn.protector;

import cn.common.ui.activity.BaseApplication;

/**
 * 描述：程序入口
 *
 * @author Created by jakechen on 2015/8/6.
 */
public class ProtectorApplication extends BaseApplication {
    private boolean isShowMain = false;

    public void setShowMain(boolean isShow) {
        isShowMain = isShow;
    }

    public boolean isShowMain() {
        return isShowMain;
    }

    @Override
    protected BaseApplication getChildInstance() {
        return this;
    }

    @Override
    protected void onConfig() {
    }

    @Override
    protected void onRelease() {
    }
}
