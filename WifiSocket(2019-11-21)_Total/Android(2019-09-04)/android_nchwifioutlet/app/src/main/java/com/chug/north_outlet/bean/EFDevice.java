package com.chug.north_outlet.bean;


import org.xutils.db.annotation.Column;

public class EFDevice extends EntityBase {
    public static final int STATUS_UNKNOWN = -1;

    public static final int TYPE_ZIG_LIGHT         = 1;
    public static final int TYPE_ZIG_OUTLET        = 2;
    public static final int TYPE_ZIG_SWITCH        = 3;
    public static final int TYPE_WIFI_OUTLET       = 4;
    public static final int TYPE_WIFI_LIGHT        = 5;    //wifi灯
    public static final int TYPE_TELECONTROL       = 6;
    public static final int TYPE_ZIG_CURTAINS      = 7;
    public static final int TYPE_COMPOSITE         = 8;
    public static final int TYPE_WIFI_POWER_STRIP  = 9;
    public static final int TYPE_ZIG_SWITCH_DOUBLE = 10;
    public static final int TYPE_ZIG_SWITCH_SINGLE = 11;
    public static final int TYPE_WIFI_BRIDGE       = 12;
    public static final int  TYPE_ZIG_BRIDGE        = 13;
    public static final int TYPE_ZIG_DOORLOCK      = 14;
    public static final int TYPE_MUSIC             = 15;
    public static final int TYPE_CAMERA            = 16;
    public static final int TYPE_LIGHT_FAN            = 17;
    public static final int TYPE_ZIG_Pusher         = 18;
    public static final int TYPE_ZIG_BODYIF         = 19;
    public static final int TYPE_ZIG_SMOKE         = 20;
    public static final int TYPE_ZIG_CO         = 21;
    //乐鑫
    public static final int TYPE_WIFI_LIGHT_LEXIN        = 23;
    public static final int TYPE_WIFI_PAI_OUTLET         = 24;
    public static final int TYPE_WIF_OUTLET_LEXIN         = 25;
//新机顶盒
public static final int TYPE_ZIG_NEW_STB         = 26;
    //新红外
    public static final int TYPE_ZIG_NEW_TELECONTROL         = 27;
    //新桥接器
    public static final int TYPE_ZIG_NEW_BRIDGE        = 28;
    /**
     * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
     */
    private static final long serialVersionUID = -7817904295404686067L;

    /**
     * 这个名字很重要，区分场景，默认会给一个场景名称，如果被分配到其他的场景，则修改此属性为改场景的属性
     */
    @Column(name = "sceneId")
    protected int    sceneId;
    /**
     * 单纯名字
     */
    @Column(name = "deviceName")
    protected String deviceName;
    /**
     * id(唯一标识,统一为hexString)
     */
    @Column(name = "deviceMac")
    protected String deviceMac;
    /**
     * 类型 1为灯泡，4为wifi插座
     */
    @Column(name = "deviceType")
    protected int    deviceType;
    /**
     * 状态 0为关闭，1为开启 ，-1为异常
     */
    @Column(name = "deviceState")
    protected int    deviceState;

    /**
     * 用这个来确定设备的归属（比如属于桥接器管理的设备，还是本身就是wifi设备）
     */
    @Column(name = "parentMac")
    protected String parentMac;

    //情景模式
    public  int mode;

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    //权限值0是未知,1是无,2是主,3是辅
    protected int limits;

    public int getLimits() {
        return limits;
    }

    public void setLimits(int limits) {
        this.limits = limits;
    }

    public int getSceneId() {
        return sceneId;
    }

    public void setSceneId(int sceneId) {
        this.sceneId = sceneId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getDeviceState() {
        return deviceState;
    }

    public void setDeviceState(int deviceState) {
        this.deviceState = deviceState;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getParentMac() {
        return parentMac;
    }

    public void setParentMac(String parentMac) {
        this.parentMac = parentMac;
    }

    @Override
    public int compareTo(Object obj) {
        return 0;
    }

}
