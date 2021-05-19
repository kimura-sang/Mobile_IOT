package com.chug.north_outlet.bean;

import android.support.annotation.NonNull;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by techno11 on 21/10/16.
 */
@Table(name = "DbDeviceInfo")
public class DbDeviceInfo extends EntityBase {

    private static final long serialVersionUID = 7752532225720252151L;

    @Column(name = "Name")
    private String Name;

    @Column(name = "SubName")
    private String SubName;

    @Column(name = "Icon")
    private int Icon;

    @Column(name = "Status")
    private boolean Status;

    public DbDeviceInfo() {

    }

    public DbDeviceInfo(String name, String subName, int icon, boolean status) {
        Name = name;
        SubName = subName;
        Icon = icon;
        Status = status;
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

    @Override
    public String toString() {
        return "DbDeviceInfo{" +
                "Name='" + Name + '\'' +
                ", SubName='" + SubName + '\'' +
                ", Icon=" + Icon +
                ", Status=" + Status +
                '}';
    }

    @Override
    public int compareTo(@NonNull Object another) {
        return 0;
    }
}
