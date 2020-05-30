package com.xiaochen.easy.frames;

import com.xiaochen.common.base.BaseApplication;
import com.xiaochen.easy.core.EasyRouter;

/**
 * <p>{d}</p>
 *
 * @author zhenglecheng
 * @date 2020/5/30
 */
public class MyApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        EasyRouter.init(this);
    }
}
