package com.chug.north_outlet.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * @author yinhui
 * @ClassName: EFDeviceOutlet
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2016-3-9 上午11:05:46
 */
@Table(name = "EFDeviceOutlet")
public class EFDeviceOutlet extends EFDevice {

    // 状态 0为关闭，1为开启 ，-1为异常
    public static final int STATE_OFF = 0;
    public static final int STATE_ON = 1;

    /**
     *
     */
    private static final long serialVersionUID = 7752532925770258157L;

    @Column(name = "lock")
    private int lock;
    @Column(name = "firmwareVersion")
    private String firmwareVersion;
    int position;
    //乐鑫插座是否有权限
    boolean haveAuthority;

    @Column(name = "Name")
    private String Name;
    @Column(name = "SubName")
    private String SubName;
    @Column(name = "Icon")
    private int Icon;
    @Column(name = "Status")
    private boolean Status;
    private boolean isDeviceSelected;

    @Column(name = "isDeviceAdded")
    private boolean isDeviceAdded;

    @Column(name = "isOnOff")
    private boolean isOnOff;

    public boolean isOnOff() {
        return isOnOff;
    }

    public void setOnOff(boolean onOff) {
        isOnOff = onOff;
    }

    public boolean isDeviceAdded() {
        return isDeviceAdded;
    }

    public void setDeviceAdded(boolean deviceAdded) {
        isDeviceAdded = deviceAdded;
    }

    public boolean isHaveAuthority() {
        return haveAuthority;
    }

    public void setHaveAuthority(boolean haveAuthority) {
        this.haveAuthority = haveAuthority;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getLock() {
        return lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSubName() {
        return SubName;
    }

    public void setSubName(String subName) {
        SubName = subName;
    }

    public int getIcon() {
        return Icon;
    }

    public void setIcon(int icon) {
        Icon = icon;
    }

    public boolean isStatus() {
        return Status;
    }

    public void setStatus(boolean status) {
        Status = status;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }


    @Override
    public String toString() {
        return "{" +
                "lock=" + lock +
                ", firmwareVersion='" + firmwareVersion + '\'' +
                ", position=" + position +
                ", haveAuthority=" + haveAuthority +
                ", Name='" + Name + '\'' +
                ", SubName='" + SubName + '\'' +
                ", Icon=" + Icon +
                ", Status=" + Status +
                ", deviceType=" + deviceType +
                ", deviceMac=" + deviceMac +
                ", parentMac=" + parentMac +
                ", deviceState=" + deviceState +
                ", id=" + id +
                ", sceneId=" + sceneId +
                ", isOnOff=" + isOnOff +
                ", isDeviceAdded=" + isDeviceAdded +
                ", isDeviceSelected=" + isDeviceSelected +
                '}';
    }

    public boolean isDeviceSelected() {
        return isDeviceSelected;
    }

    public void setDeviceSelected(boolean deviceSelected) {
        isDeviceSelected = deviceSelected;
    }
}
