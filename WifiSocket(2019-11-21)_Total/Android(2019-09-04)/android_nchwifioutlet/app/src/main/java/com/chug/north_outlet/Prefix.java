package com.chug.north_outlet;

public class Prefix {

    /**
     * 指令前缀
     */
    public static byte[] BIND_DEVICES = {0x01, 0x62, 0x69, 0x6E, 0x64};// ("1bind")
    public static byte[] CHANGE_COLOR_DEVICES = {0x02, 0x63, 0x74, 0x6C};// ("2ctl")
    public static byte[] TIME_DEVICES = {0x03, 0x74, 0x69, 0x6D, 0x65, 0x72};// ByteUtils.strToByte("3timer")
    public static byte[] LOOP_DEVICES = {0x04, 0x6C, 0x6F, 0x6F, 0x70};// ByteUtils.strToByte("4loop")
    public static byte[] GROUP_DEVICES = {0x05, 0x67, 0x72, 0x6F, 0x75, 0x70};// ByteUtils.strToByte("5group")
    public static byte[] UNGROUP_DEVICES = {0x06, 0x75, 0x6E, 0x67, 0x72, 0x6F, 0x75, 0x70};// ByteUtils.strToByte("6ungroup")
    public static byte[] REG_PHONE = {0x07, 0x72, 0x65, 0x67};// ByteUtils.strToByte("7reg");
    public static byte[] UNREG_PHONE = {0x08, 0x75, 0x6E, 0x72, 0x65, 0x67};// ByteUtils.strToByte("8unreg")
    public static byte[] SIG_BIND = {0x09, 0x73, 0x69, 0x67, 0x62, 0x69, 0x6E, 0x64};// ByteUtils.strToByte("9sigbind")
    public static byte[] MATCH_PHONE = {0xa, 0x6D, 0x61, 0x74, 0x63, 0x68};// ByteUtils.strToByte("amatch")
    public static byte[] EXCHANGE_PHONE = {0xb, 0x65, 0x78, 0x63, 0x68, 0x61, 0x6E, 0x67, 0x65};// ByteUtils.strToByte("bexchange")
    public static byte[] UPDATE_LIGHT = {0xc, 0x75, 0x70, 0x64, 0x61, 0x74, 0x65};// ByteUtils.strToByte("cexchange")
    public static byte[] RESET_RECONFIG = {0xe, 0x72, 0x65, 0x63, 0x6f, 0x6e, 0x66, 0x69, 0x67};// ByteUtils.strToByte("cexchange")
    public static byte[] SEARCH_DEVICES_RETURN = {0xd, 0x73, 0x65, 0x61, 0x72, 0x63, 0x68};// ByteUtils.strToByte("dexchange")
    public static byte[] DEVICES_ALARM = {0x16, 0x61, 0x6C, 0x61, 0x72, 0x6D};// 616c61726d(报警信息获取)
    public static byte[] BROACAST_DATA = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};// 广播

    // 0x15 + lightversion 6c 69 67 68 74 76 65 72 73 69 6f 6e
    public static byte[] GET_LIGHT_VER = {0x15, 0x6C, 0x69, 0x67, 0x68, 0x74, 0x76, 0x65, 0x72, 0x73, 0x69, 0x6F, 0x6E};
    public static byte[] ADD_NEW_TIMER = {0x13, 0x74, 0x6D, 0x72};// ("13tmr");
    public static byte[] DEL_NEW_TIMER = {0x14, 0x64, 0x65, 0x6C};// ("14del");

    // zigbee插座命令前缀
    public static byte[] CTL_ZIG_OUTLET = CHANGE_COLOR_DEVICES;// ("2ctl");
    public static byte[] TMR_ZIG_OUTLET = {0x0F, 0x74, 0x6D, 0x72};// ("15tmr");
    public static byte[] DEL_ZIG_OUTLET = {0x11, 0x64, 0x65, 0x6C};// ("17del");

    // zigbee开关命令前缀
    public static byte[] CTL_ZIG_SWITCH = CHANGE_COLOR_DEVICES;// ("2ctl");
    public static byte[] TMR_ZIG_SWITCH = {0x10, 0x74, 0x6D, 0x72};// ("16tmr");
    public static byte[] DEL_ZIG_SWITCH = {0x12, 0x64, 0x65, 0x6C};// ("18del");

    // zigbee窗帘命令前缀
    public static byte[] CTL_ZIG_CUR = CHANGE_COLOR_DEVICES;// ("2ctl");
    public static byte[] TMR_ZIG_CUR = TMR_ZIG_OUTLET;//{ 0x16, 0x74, 0x6D, 0x72 };// ("22tmr");
    public static byte[] DEL_ZIG_CUR = DEL_ZIG_OUTLET;//{ 0x13, 0x64, 0x65, 0x6C };// ("19del");

    // zigbee门锁命令前缀
    public static byte[] CTL_ZIG_DOOR = CHANGE_COLOR_DEVICES;// ("2ctl");
    public static byte[] TMR_ZIG_DOOR = TMR_ZIG_OUTLET;//{ 0x16, 0x74, 0x6D, 0x72 };// ("22tmr");
    public static byte[] DEL_ZIG_DOOR = DEL_ZIG_OUTLET;//{ 0x13, 0x64, 0x65, 0x6C };// ("19del");

    // zigbee推窗器命令前缀
    public static byte[] CTL_ZIG_PUSHER = CHANGE_COLOR_DEVICES;// ("2ctl");
    public static byte[] TMR_ZIG_PUSHER = TMR_ZIG_OUTLET;//{ 0x16, 0x74, 0x6D, 0x72 };// ("22tmr");
    public static byte[] DEL_ZIG_PUSHER = DEL_ZIG_OUTLET;//{ 0x13, 0x64, 0x65, 0x6C };// ("19del");

    //传感器
    public static byte[] TMR_ZIG_SENSOR = {0x17, 0x00, 0x0D};


    // wifi设备扫描命令
    public static byte[] SCAN_WIFI_DEVICE = {0x01, 0x77, 0x62, 0x69, 0x6E, 0x64};// ("119bind");
    public static byte[] SCAN_WIFI_DEVICE_1 = {0x01, 0x04, 0x62, 0x69, 0x6E, 0x64};// ("119bind");
    // wifi插座命令前缀
    public static byte[] CTL_OUTLET = {0x02, 0x04, 0x63, 0x74, 0x6C};// ("24ctl");//控制wifi插座
    public static byte[] TMR_OUTLET = {0x03, 0x04, 0x74, 0x6D, 0x72};// ("43tmr");746d72
    public static byte[] GTMR_OUTLET = {0x04, 0x04, 0x67, 0x74, 0x6D, 0x72};// ("44gtmr");

    public static byte[] REF_OUTLET = {0x0C, 0x04, 0x75, 0x70, 0x64, 0x61, 0x74, 0x65};// ("0c update");757064617465

    public static byte[] CTL_POWER_STRIP = {0x02, 0x09, 0x63, 0x74, 0x6C};// ("24ctl");//控制wifi插座
    public static byte[] TMR_POWER_STRIP = {0x03, 0x09, 0x74, 0x6D, 0x72};// ("43tmr");746d72
    public static byte[] GTMR_POWER_STRIP = {0x04, 0x09, 0x67, 0x74, 0x6D, 0x72};// ("44gtmr");
    public static byte[] REF_POWER_STRIP = {0x0C, 0x09, 0x75, 0x70, 0x64, 0x61, 0x74, 0x65};// ("0cupdate");757064617465
    public static byte[] CHECK_POWER = {0x10, 0x09, 0x70, 0x6F, 0x77, 0x65, 0x72};// power(706f776572)
    public static byte[] RESET_POWER = {0x11, 0x09, 0x7A, 0x65, 0x72, 0x6F};// zero(7a65726f)

    //乐鑫wifi排插

    public static byte[] REF_PAI_OUTLET = {0x00, 0x18};//
    //乐鑫wifi插座
    public static byte[] REF_LEXIN_OUTLET = {0x00, 0x19};//
    //乐鑫wifi灯泡

    public static byte[] REF_LIGHT_LEXIN = {0x00, 0x17};//
    //新机顶盒

    public static byte[] REF_NEW_STB = {0x00, 0x1a};//
    public static byte[] REF_NEW_STB_TYPE = {0x00, 0x01, 0x00};//
    public static char[] STB_KEY_DATA_ONE={'!','@','#','$','%','^','&','<','>','~','_','{','}','|',':','"','?'};
    public static int[] STB_KEY_DATA_ONE_NUMBER={8,9,10,11,12,13,14,55,56,68,69,71,72,73,74,75,76};
    public static char[] STB_KEY_DATA_ZERO={'`','*','(',')','-','+','=','[',']','\\',';', ',','.','/',' ','\''};
    public static char[] STB_KEY_DATA_ZERO_NUMBER={68,17,162,163,69,81,70,71,72,73,74, 55,56,76,62,75};

    //新红外

    public static byte[] REF_NEW_TELECONTROLER = {0x00, 0x1b};//
    public static byte[] REF_NEW_TELECONTROLER_TYPE = {0x01};//
    //新桥接器

    public static byte[] REF_NEW_ZIGBEE = {0x00, 0x1c};//
    public static byte[] REF_NEW_ZIGBEE_TYPE = {0x02};//
    public  static String ZIGBEE_MAC_NULL="00000000";

    // wifi灯命令前缀
    public static byte[] CTL_WIFI_LIGHT = {0x02, 0x05, 0x63, 0x74, 0x6C};// ("25ctl");
    public static byte[] UPDATE_WIFI_LIGHT = {0x0c, 0x05, 0x75, 0x70, 0x64, 0x61, 0x74, 0x65};// ("c5update"75																						// 65);
    public static byte[] GTMR_WIFI_LIGHT = {0x04, 0x05, 0x67, 0x74, 0x6D, 0x72};// ("45gtmr");
    public static byte[] TMR_WIFI_LIGHT = {0x03, 0x05, 0x74, 0x6D, 0x72};// ("35tmr");
    // 红外设备命令前缀
    public static byte[] CTL_TELECONTROLLER = {0x02, 0x06, 0x63, 0x74, 0x6C};// ("26ctl");
    public static byte[] GET_TELECONTROLLER = {0x0c, 0x06, 0x75, 0x70, 0x64, 0x61, 0x74, 0x65};// ("26update");
    public static byte[] GET_COMPOSITECONTROLLER = {0x0c, 0x08, 0x75, 0x70, 0x64, 0x61, 0x74, 0x65};// ("28update");智慧中心

    //桥接器
    public static byte[] CONTROLLER_BRIDGE = {0x0c, 0x0d, 0x75, 0x70, 0x64, 0x61, 0x74, 0x65};// ("213update");有限桥接器


    // 升级
    public static byte[] FWUP_OUTLET = {0x06, 0x04, 0x66, 0x77, 0x75, 0x70};// ("64fwup");66777570
    public static byte[] FWUP_POWER_STRIP = {0x06, 0x09, 0x66, 0x77, 0x75, 0x70};// ("64fwup");66777570
    public static byte[] FWUP_COMPOSITE_DEVICE = {0x06, 0x08, 0x66, 0x77, 0x75, 0x70};// ("64fwup");66777570
    public static byte[] FWUP_WIFI_BRIDGE = {0x06, 0x0C, 0x66, 0x77, 0x75, 0x70};// ("64fwup");66777570
    public static byte[] FWUP_TELECONTROLLER = {0x06, 0x06, 0x66, 0x77, 0x75, 0x70};// ("64fwup");66777570
    public static byte[] FWUP_WIFI_LAMP = {0x06, 0x05, 0x66, 0x77, 0x75, 0x70};// ("64fwup");66777570

    /**
     * 一些固定的指令
     */
    public static final byte[] COLOR_ON = {(byte) 0x00, (byte) 0x00, (byte) 0x00};// 开
    public static final byte[] COLOR_OFF = {(byte) 0xff, (byte) 0xff, (byte) 0xff};// 关
    public static final byte[] NONE = {(byte) 0x01, (byte) 0x01, (byte) 0x01};// 场景模式_无
    public static final byte[] READ_MODEL = {(byte) 0x01, (byte) 0x01, (byte) 0xff};// 阅读
    public static final byte[] READ_MODEL_YELLOW = {(byte) 0x01, (byte) 0x01, (byte) 0x01};// 阅读暖黄
    public static final byte[] RELAX_MODEL = {(byte) 0x01, (byte) 0xc8, (byte) 0xff,};// 放松
    public static final byte[] ROMANTIC_MODEL = {(byte) 0x01, (byte) 0xff, (byte) 0x01,};// 浪漫
    public static final byte[] VITILAY_MODEL = {(byte) 0xc8, (byte) 0x37, (byte) 0xf3,};// 活力
    //乐兴
    public static final byte[] NEW_ZIGB_COLOR_ON = {(byte) 0x01};// 开
    public static final byte[] NEW_ZIGB_COLOR_OFF = {(byte) 0x00};// 关
    public static final byte[] NONE_LEXIN = {(byte) 0xff, (byte) 0xff, (byte) 0xff};// 场景模式_无
    public static final byte[] READ_MODEL_LEXIN = {(byte) 0xff, (byte) 0xff, (byte) 0x01};// 阅读
    public static final byte[] RELAX_MODEL_LEXIN = {(byte) 0xff, (byte) 0x32, (byte) 0x01,};// 放松
    public static final byte[] ROMANTIC_MODEL_LEXIN = {(byte) 0xff, (byte) 0x01, (byte) 0xff,};// 浪漫
    public static final byte[] ALL_MAC = {(byte) 0xff, (byte) 0xff, (byte) 0xff,(byte) 0xff, (byte) 0xff, (byte) 0xff};// 广播mac地址
    public static final byte[] NEW_ZIGBEE_REFLASH = {(byte) 0xff, (byte) 0xff, (byte) 0xff,(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,(byte) 0xff, (byte) 0xff, (byte) 0xff};// 10FF
    public static final byte[] NEW_ZIGBEE_LIGHT = {(byte) 0x00, (byte) 0x01, (byte) 0xff,(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,(byte) 0xff, (byte) 0xff, (byte) 0xff};// 10FF

}
