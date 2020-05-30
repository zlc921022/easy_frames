package com.xiaochen.common.base;

/**
 * <p>arouter对应path常量类</p >
 *
 * @author zhenglecheng
 * @date 2020/4/22
 */
@SuppressWarnings("all")
public class RouterPathConstant {
    /**
     * 模块之间页面跳转路由
     */
    public static final String MVP_ACTIVITY = "/mvp/mainActivity";
    public static final String MVVM_ACTIVITY = "/mvvm/mainActivity";
    public static final String JETPACK_ACTIVITY = "/jetpack/mainActivity";
    public static final String BLUETOOTH_ACTIVITY = "/bluoth/maActivity";
    public static final String WIDGET_ACTIVITY = "/widget/mainActivity";
    public static final String AROUTER_ACTIVITY = "/router1/mainActivity";
    public static final String DAGGER2_ACTIVITY = "/dagger12/dagger2Activity";

    public static final String TEST_ACTIVITY = "/router2/testActivity";
    public static final String TEST_ACTIVITY2 = "/router2/testActivity2";
    public static final String TEST_ACTIVITY3 = "/router2/testActivity3";
    public static final String TEST_FRAGMENT = "/router2/fragment";

    /**
     * 服务
     */
    public static final String BLUETOOTH_SERVICE = "/bluetooth/bluetoothInfoService";
    public static final String DAGGER2_SERVICE = "/dagger/dagger2InfoService";
    public static final String JETPACK_SERVICE = "/jetpack/jetpackInfoService";
    public static final String MVP_SERVICE = "/mvp/mvpInfoService";
    public static final String MVVM_SERVICE = "/mvvm/mvvmInfoService";
    public static final String AROUTER_SERVICE = "/router1/arouterInfoService";
    public static final String WIDGET_SERVICE = "/widget/widgetInfoService";
}
