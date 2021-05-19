package com.chug.north_outlet;

/**
 * @author yinhui
 * @ClassName: RequestCode
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2016-3-2 上午9:34:59
 */
public interface RequestCode {

    // 各个界面发送数据的请求码
    public static final int ALARM_FRAGMENT_CODE = 7048;
    public static final int HOME_FRAGMENT_CODE = 7049;
    public static final int MAIN_SCENE_CODE = 7050;
    public static final int SUB_SCENE_CODE = 7051;
    public static final int SETTING_FRAGMENT_CODE = 7052;
    public static final int SINGLE_LAMP_CODE = 7053;
    public static final int SINGLE_SWITCH_CODE = 7054;
    public static final int SINGLE_OUTLET_CODE = 7055;
    public static final int YYMD_CODE = 7056;
    public static final int ADD_ZSWITCH_TIMER = 7057;
    public static final int EDIT_ZSWITCH_TIMER = 7058;
    public static final int ADD_ZOUTLET_TIMER = 7059;
    public static final int EDIT_ZOUTLET_TIMER = 7060;
    public static final int ADD_WOUTLET_TIMER = 7061;
    public static final int ADD_WLAMP_TIMER = 7063;
    public static final int EDIT_WLAMP_TIMER = 7064;
    public static final int ADD_SCENE_TIMER = 7065;
    public static final int EDIT_SCENE_TIMER = 7066;
    public static final int PRIORITY_PHONE = 7067;
    public static final int TELE_MAIN = 7068;
    public static final int SINGLE_CURTAIN_CODE = 7069;
    public static final int ADD_CURTAIN_TIMER = 7070;
    public static final int EDIT_CURTAIN_TIMER = 7071;
    public static final int SINGLE_POWER_STRIP_CODE = 7072;
    public static final int SINGLE_DOORLOCK_CODE = 7073;
    public static final int SINGLE_PUSHER_CODE = 7074;

    // sys photo gallery request code
    public static final int FROM_GALLERY = 7047;
    // capture request code
    public static final int FROM_CAPTURE = 7048;

    // 启动扫描设备界面的请求码
    public static final int REQUEST_SCAN_DEVICE = 8053;
    // 启动场景界面的请求码
    public static final int REQUEST_SHOW_SCENE = 8054;
    // 请求插座控制页面
    public static final int REQUEST_SINGLE_OUTLET = 8055;
    // 请求开关控制页面
    public static final int REQUEST_SINGLE_SWITCH = 8056;
    // 请求单灯控制页面
    public static final int REQUEST_SINGLE_LIGHT = 8057;
    // 请求遥控配置页面
    public static final int REQUEST_ADD_TELECONTROLLER = 8058;
    // 请求窗帘配置页面
    public static final int REQUEST_SINGLE_CURTAINS = 8059;
    // 请求wifi配置页面
    public static final int REQUEST_WIFI_SETUP = 8060;
    // 请求门锁配置页面
    public static final int REQUEST_SINGLE_DOORLOCK = 8061;
    // 添加联动模式界面的请求码
    public static final int REQUEST_ADDLINKAGE = 8062;
    // 请求推窗器配置页面
    public static final int REQUEST_SINGLE_PUSHER = 8063;
    // 传感器报警发送间隔时间
    public static final int REQUEST_SINGLE_SENSOR = 8063;
    public static final int SINGLE_OUTLET_LEXIN_CODE = 8064;
    public static final int SINGLE_POWER_STRIP_LEXIN_CODE = 8065;
    public static final int SINGLE_LAMP_LEXIN_CODE = 8066;
    //设置选择背景图片
    public static final int FROM_DEFOUT = 8067;
    //机顶盒
    public static final int REQUEST_ZIG_STB = 8068;
}
