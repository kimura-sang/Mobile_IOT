package com.chug.north_outlet.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "EFScene")
public class EFScene extends EntityBase {

    // 默认场景ID
    public static final int DEFAULT_SCENEID = 0;

    /**
     * 新搜到的设备
     */
    private static final long serialVersionUID = -6041837688123987783L;

    @Column(name = "sceneName")
    private String sceneName;

    @Column(name = "scenePic")
    private String scenePic;

    @Column(name = "zigLampCount")
    private int zigLampCount;                                    // zig灯个数

    @Column(name = "wifiLampCount")
    private int wifiLampCount;                                    // wifi灯个数

    @Column(name = "zigSwitchCount")
    private int zigSwitchCount;                                // zig开关个数

    @Column(name = "zigOutletCount")
    private int zigOutletCount;                                // zig插座个数

    @Column(name = "wifiOutletCount")
    private int wifiOutletCount;                                // wifi插座个数

    @Column(name = "courtainsCount")
    private int courtainsCount;                                // 窗帘个数
int pusherCount;
    int sensorCount;

    @Column(name = "teleCount")
    private int teleCount;                                        // 遥控个数

    public int getPusherCount() {
        return pusherCount;
    }

    public void setPusherCount(int pusherCount) {
        this.pusherCount = pusherCount;
    }

    public int getSensorCount() {
        return sensorCount;
    }

    public void setSensorCount(int sensorCount) {
        this.sensorCount = sensorCount;
    }

    public String getScenePic() {
        return scenePic;
    }

    public void setScenePic(String scenePic) {
        this.scenePic = scenePic;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public int getZigLampCount() {
        return zigLampCount;
    }

    public void setZigLampCount(int zigLampCount) {
        this.zigLampCount = zigLampCount;
    }

    public int getWifiLampCount() {
        return wifiLampCount;
    }

    public void setWifiLampCount(int wifiLampCount) {
        this.wifiLampCount = wifiLampCount;
    }

    public int getZigSwitchCount() {
        return zigSwitchCount;
    }

    public void setZigSwitchCount(int zigSwitchCount) {
        this.zigSwitchCount = zigSwitchCount;
    }

    public int getZigOutletCount() {
        return zigOutletCount;
    }

    public void setZigOutletCount(int zigOutletCount) {
        this.zigOutletCount = zigOutletCount;
    }

    public int getWifiOutletCount() {
        return wifiOutletCount;
    }

    public void setWifiOutletCount(int wifiOutletCount) {
        this.wifiOutletCount = wifiOutletCount;
    }

    public int getTeleCount() {
        return teleCount;
    }

    public void setTeleCount(int teleCount) {
        this.teleCount = teleCount;
    }

    public int getCourtainsCount() {
        return courtainsCount;
    }

    public void setCourtainsCount(int courtainsCount) {
        this.courtainsCount = courtainsCount;
    }

    public int getZigCount() {
        return zigLampCount + zigSwitchCount + zigOutletCount + courtainsCount;
    }

    public int getAllCount() {
        return zigLampCount + wifiLampCount + zigSwitchCount + zigOutletCount + wifiOutletCount + courtainsCount
                + teleCount;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public int compareTo(Object obj) {
        return 0;
    }

}
