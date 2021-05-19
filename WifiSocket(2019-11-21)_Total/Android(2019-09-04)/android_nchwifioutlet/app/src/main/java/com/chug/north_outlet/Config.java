package com.chug.north_outlet;

public class Config {

    // ShareSDK
    public static final String SMS_APPKEY = "de495571a6ae"/* "cd8f8b85fb14" */;// 以前开发公司的Key:8e03dae3cd90
    public static final String SMS_APPSECRET = "f5798f6721bc70055098dbf92318310c"/* "571eafd519695f2edb6700ab1c03370c" */;// 以前开发公司的28dcf0e3a313b8e225daed14df010867

    public static final String VERSION_APK = "https://www.lingansmart.cn/APP_VER/eFamily/Android/IntelligentLight.apk";// apk下载
    public static final String VERSION_APP = "https://www.lingansmart.cn/APP_VER/eFamily/Android/version.txt";// app版本
    public static final String VERSION_URL_7 = "https://www.lingansmart.cn/ESP/LinGan/SWB1/version.txt";//乐兴 插座
    public static final String VERSION_URL_8 = "https://www.lingansmart.cn/ESP/LinGan/SWB12/version.txt";//乐兴 排插
    public static final String VERSION_URL_1 = "https://www.lingansmart.cn/WM_fwup/SWA1/version.txt";// 插座
    public static final String VERSION_URL_2 = "https://www.lingansmart.cn/WM_fwup/SWA2/version.txt";// 插排
    public static final String VERSION_URL_3 = "https://www.lingansmart.cn/WM_fwup/UCC10/version.txt";// 控制中心
    public static final String VERSION_URL_4 = "https://www.lingansmart.cn/WM_fwup/UCC11/version.txt";// wifi桥接器
    public static final String VERSION_URL_5 = "https://www.lingansmart.cn/WM_fwup/UCC12/version.txt";// 红外
    public static final String VERSION_URL_6 = "https://www.lingansmart.cn/WM_fwup/LWE3/version.txt";// wifi灯
    public static final String VERSION_URL_9 = "https://www.lingansmart.cn/ESP/LinGan/LAMP/version.txt";// 乐兴暖白wifi灯
    public static final String VERSION_URL_10 = "https://www.lingansmart.cn/ESP/LinGan/LIGHT/version.txt";// 乐兴彩色wifi灯
    public static final String VERSION_URL_11 = "https://www.lingansmart.cn/ESP/LinGan/LED_STRIP/version.txt";// 乐兴灯带
    public static final String VERSION_URL_12 = "https://www.lingansmart.cn/ESP/LinGan/IrDA/version.txt";// 乐兴红外

    /**
     * debug开关
     */
    public static final boolean DEBUG = true;
    public static final boolean SQL_DEBUG = DEBUG;

    /* 数据库名字 */
    public static final String DBNAME = "intelligent.db";

    // 默认密码
    public static final String account = "0000";
    public static final String passwrod = "8888";
    public static String WIFI_PRODUCTID = "5471177bba474be2a92b4eef51cbc9cc";
    public static String WIFI_PRODUCTID_LEXIN = "160fa2afb49eaa00160fa2afb49eaa01";
    public static String ZIG_PRODUCTID = "bd82ce55a3894ae38761fab3bfcaf94d";
//	public static String ZIG_PRODUCTID = "5471177bba474be2a92b4eef51cbc9cc";
    public static String PRODUCTID = "160fa2b1bc4903e9160fa2b1bc494601";


    /* 配置私有文件名字 */
    public static final String CONFIG_FILE_NAME = "config_prefences";
    public static final String PREF_USERNAME = "pref_username";
    public static final String PREF_PWD = "pref_pwd";

    /* 语言切换 */
    public static final String LANGUAGE = "language";

    // ------------启动监听
    public static final String PACKAGE_NAME = App.getInstance().getPackageName();
    public static final String BROADCAST_ON_START = PACKAGE_NAME + ".onStart"; //
    public static final String BROADCAST_ON_LOGIN = PACKAGE_NAME + ".xlinkonLogin";
    public static final String BROADCAST_CLOUD_DISCONNECT = PACKAGE_NAME + ".clouddisconnect";
    public static final String BROADCAST_LOCAL_DISCONNECT = PACKAGE_NAME + ".localdisconnect";
    public static final String BROADCAST_RECVPIPE = PACKAGE_NAME + ".recv-pipe";
    public static final String BROADCAST_DEVICE_CHANGED = PACKAGE_NAME + ".device-changed";
    public static final String BROADCAST_DEVICE_SYNC = PACKAGE_NAME + ".device-sync";
    public static final String BROADCAST_EXIT = PACKAGE_NAME + ".exit";
    public static final String BROADCAST_TIMER_UPDATE = PACKAGE_NAME + "timer-update";
    public static final String BROADCAST_SOCKET_STATUS = PACKAGE_NAME + "socket-status";
    public static final String BROADCAST_DEVICE_DATAPOINT_RECV = PACKAGE_NAME + ".device_recv_datapoint";
    public static final String BROADCAST_DATAPOINT_RECV = PACKAGE_NAME + ".recv_datapoint";

    // key
    public static final String DEVICE_MAC = "device-mac";
    public static final String STATUS = "status";
    public static final String TYPE = "type";
    public static final String KEY = "key";
    public static final String DATA = "data";

    /* Preference */
    public static final String EHOME_PREFRENCE = "ehome_prefernce";
    public static final String LOGIN_PREFRENCE = "login_prefernce";


    /* user */
    public static final String USER_PHONE = "user_phone";
    public static final String USER_PWD = "user_pwd";

    public static final String APP_ID = "appid_";
    public static final String AUTH_KEY = "authkey_";

    // 暂存灯泡信息
    public static final String TEMPORARILY_SAVE_LIGHT_MSG = "light_msg";

    // 数据接收
    public static final String REQUEST_CODE_KEY = "request_code_key";

    // 发送数据长度
    public static final int FORTY_LENGTH = 40;
    public static final int STRIP_LENGTH = 60;
    public static final int FIFTY_LENGTH = 50;
    public static final int FIFTY_LENGTH_ZIGBEE = 80;
    public static final int ONE_HUNDRED_LENGTH = 100;

    // wifi设备物理地址存放的key值
    public static final String KEY_WIFI_LIGHT = "key_wifi_light";
    public static final String KEY_WIFI_OUTLET = "key_wifi_outlet";
    public static final String KEY_WIFI_PAI_OUTLET = "key_wifi_pai_outlet";
    public static final String KEY_POWER_STRIP = "key_wifi_power_strip";
    public static final String KEY_PAI_OUTLET = "key_wifi_pai_outlet";
    // wifi红外设备物理地址存放的key值
    public static final String KEY_TELE_DEVICE = "key_tele_device";
    // 组合设备物理地址存放的key值
    public static final String KEY_COMPOSITE_DEVICE = "key_composite_device";


    public static final String RECENT_NORMAL_DEVICE = "recent_normal_device";
    public static final String RECENT_TELE_DEVICE = "recent_tele_device";

    public static boolean BL_FIRST_START = true;

}
