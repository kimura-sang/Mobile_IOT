package com.chug.north_outlet.bean;


import com.chug.north_outlet.App;
import com.chug.north_outlet.R;

import java.io.Serializable;

import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkCode;

public class ControlDevice implements Serializable {


    public static final int NORMAL_DEVICE = 0;
    public static final int WIFI_DEVICE_LIGHT = 1;
    public static final int WIFI_DEVICE_OUTLET = 2;
    public static final int TELECONTROLLER_DEVICE = 3;
    public static final int COMPOSITE_DEVICE = 4;
    public static final int WIFI_POWER_STRIP = 5;
    public static final int TYPE_WIFI_PAI_OUTLET = 7;
    public static final int WIFI_BRIDGE = 6;
    public static final int WIFI_DEVICE_STB = 8;


    /**
     *
     */
    private static final long serialVersionUID = 1L;
    // xlink 可识别的设备 实例
    private XDevice xDevice;
    // 设备授权码
    private String password;
    private int state = -1;

    private int type;
private  boolean isProtocol;

    public boolean isProtocol() {
        return isProtocol;
    }

    public void setProtocol(boolean protocol) {
        isProtocol = protocol;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }
    private boolean isSubscribe =false;

    public boolean isSubscribe() {
        return isSubscribe;
    }

    public void setSubscribe(boolean subscribe) {
        isSubscribe = subscribe;
    }

    private short th;
    private int wind;
    private boolean switch_;

    public short getTh() {
        return th;
    }

    public void setTh(short temp) {
        this.th = temp;
    }

    public int getWind() {
        return wind;
    }

    public void setWind(int wind) {
        this.wind = wind;
    }

    public boolean isSwitch_() {
        return switch_;
    }

    public void setSwitch_(boolean switch_) {
        this.switch_ = switch_;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getPassword() {
        return password;
    }

    public boolean isConnect() {

        if (xDevice.getDevcieConnectStates() == XlinkCode.DEVICE_STATE_OFFLIEN) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        return xDevice.toString() + " pwd:" + password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ControlDevice(XDevice xDevice) {
        this.xDevice = xDevice;
    }

    public String getMacAddress() {
        if (xDevice == null) {
            return "";
        }
        return xDevice.getMacAddress();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ControlDevice) {
            ControlDevice d = (ControlDevice) o;
            return xDevice.equals(d.getXDevice());
        } else if (o instanceof XDevice) {
            return xDevice.equals(o);
        }
        return super.equals(o);
    }

    public XDevice getXDevice() {
        return xDevice;
    }

    public void setxDevice(XDevice xDevice) {
        this.xDevice = xDevice;
    }

    public String getName() {
        return xDevice.getDeviceName();
    }

    public CharSequence getTypeText() {
        StringBuilder text = new StringBuilder("");
        switch (type) {
            case NORMAL_DEVICE:
                text.append(App.getAppContext().getString(R.string.control_devices));//桥接器
                break;
            case WIFI_DEVICE_LIGHT:
                text.append(App.getAppContext().getString(R.string.wifi_light));//wifi灯
                break;
            case WIFI_DEVICE_OUTLET:
                text.append(App.getAppContext().getString(R.string.wifi_outlet));//wifi插座
                break;
            case WIFI_POWER_STRIP:
            case TYPE_WIFI_PAI_OUTLET:
                text.append(App.getAppContext().getString(R.string.wifi_power_strip));//wifi插排
                break;
            case TELECONTROLLER_DEVICE:
                text.append(App.getAppContext().getString(R.string.tele_devices));//红外设备
                break;
            case COMPOSITE_DEVICE:
                text.append(App.getAppContext().getString(R.string.composite_device));//智慧中心
                break;
            case WIFI_BRIDGE:
                text.append(App.getAppContext().getString(R.string.wifi_bridge));//无线桥接器
                break;
            case  WIFI_DEVICE_STB:
                text.append(App.getAppContext().getString(R.string.stb));
                break;
            default:
                break;
        }
        text.append("(");
        text.append(getMacAddress());
        text.append(")");
        return text.toString();
    }

    // /**
    // * 是否在线（不管公网，还是内网）
    // *
    // * @return
    // */
    // public boolean isOnline() {
    // //
    // return xDevice.getState() != ResponseCode.DEVICE_STATE_OFFLINE;
    // }
}
