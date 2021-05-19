package com.chug.north_outlet.bean;

import android.support.annotation.NonNull;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by techno11 on 21/10/16.
 */
@Table(name = "DbGroupInfo")
public class DbGroupInfo extends EntityBase {

    private static final long serialVersionUID = 7752532225720252152L;

    @Column(name = "Name")
    private String Name;

    @Column(name = "Icon")
    private int Icon;

    @Column(name = "deviceList")
    private String deviceList;



    public DbGroupInfo() {

    }

    public DbGroupInfo(String name, int icon, String deviceList) {
        Name = name;
        Icon = icon;
        this.deviceList = deviceList;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getIcon() {
        return Icon;
    }

    public void setIcon(int icon) {
        Icon = icon;
    }

    public String getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(String deviceList) {
        this.deviceList = deviceList;
    }

    @Override
    public String toString() {
        return "DbGroupInfo{" +
                "Name='" + Name + '\'' +
                ", Icon=" + Icon +
                ", deviceList='" + deviceList + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull Object another) {
        return 0;
    }
}