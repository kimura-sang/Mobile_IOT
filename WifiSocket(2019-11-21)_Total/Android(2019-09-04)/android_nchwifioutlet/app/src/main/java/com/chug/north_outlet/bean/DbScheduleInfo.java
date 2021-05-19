package com.chug.north_outlet.bean;

import android.support.annotation.NonNull;

import org.hamcrest.core.Is;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by techno11 on 21/10/16.
 */
@Table(name = "DbScheduleInfo")
public class DbScheduleInfo extends EntityBase {

    private static final long serialVersionUID = 7752532225720252153L;

    @Column(name = "DeviceId")
    private Long DeviceId;

    @Column(name = "Name")
    private String Name;

    @Column(name = "SubName")
    private String SubName;

    @Column(name = "Icon")
    private int Icon;

    @Column(name = "Status")
    private boolean Status;

    @Column(name = "OnTime")
    private String OnTime;

    @Column(name = "OffTime")
    private String OffTime;

    @Column(name = "IsSun")
    private Boolean IsSun;

    @Column(name = "IsMon")
    private Boolean IsMon;

    @Column(name = "IsTue")
    private Boolean IsTue;

    @Column(name = "IsWdn")
    private Boolean IsWdn;

    @Column(name = "IsThu")
    private Boolean IsThu;

    @Column(name = "IsFri")
    private Boolean IsFri;

    @Column(name = "IsSat")
    private Boolean IsSat;

    @Column(name = "UniqueNo")
    private int UniqueNo;

    public DbScheduleInfo() {

    }

    public DbScheduleInfo(Long deviceId, String name, String subName, int icon, boolean status, String onTime,
                          String offTime, Boolean isSun, Boolean isMon, Boolean isTue, Boolean isWdn, Boolean isThu, Boolean isFri, Boolean isSat, int unitqueNo) {
        DeviceId = deviceId;
        Name = name;
        SubName = subName;
        Icon = icon;
        Status = status;
        OnTime = onTime;
        OffTime = offTime;
        IsSun = isSun;
        IsMon = isMon;
        IsTue = isTue;
        IsWdn = isWdn;
        IsThu = isThu;
        IsFri = isFri;
        IsSat = isSat;
        UniqueNo = unitqueNo;
    }

    public Long getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(Long deviceId) {
        DeviceId = deviceId;
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

    public String getOnTime() {
        return OnTime;
    }

    public void setOnTime(String onTime) {
        OnTime = onTime;
    }

    public String getOffTime() {
        return OffTime;
    }

    public void setOffTime(String offTime) {
        OffTime = offTime;
    }

    public void setIsSun(Boolean isSun){
        IsSun = isSun;
    }
    public Boolean getIsSun(){
        return IsSun;
    }

    public void setIsMon(Boolean isMon){
        IsMon = isMon;
    }
    public Boolean getIsMon(){
        return IsMon;
    }

    public void setIsTue(Boolean isTue){
        IsTue = isTue;
    }
    public Boolean getIsTue(){
        return IsTue;
    }

    public void setIsWdn(Boolean isWdn){
        IsWdn = isWdn;
    }
    public Boolean getIsWdn(){
        return IsWdn;
    }

    public void setIsThu(Boolean isThu){
        IsThu = isThu;
    }
    public Boolean getIsThu(){
        return IsThu;
    }

    public void setIsFri(Boolean isFri){
        IsFri = isFri;
    }
    public Boolean getIsFri(){
        return IsFri;
    }

    public void setIsSat(Boolean isSat){
        IsSat = isSat;
    }
    public Boolean getIsSat(){
        return IsSat;
    }

    public void setUniqueNo(int uniqueNo){
        UniqueNo = uniqueNo;
    }
    public int getUniqueNo(){
        return UniqueNo;
    }

    @Override
    public String toString() {
        return "DbScheduleInfo{" +
                "DeviceId=" + DeviceId +
                ", Name='" + Name + '\'' +
                ", SubName='" + SubName + '\'' +
                ", Icon=" + Icon +
                ", Status=" + Status +
                ", OnTime='" + OnTime + '\'' +
                ", OffTime='" + OffTime + '\'' +
                ", IsSun='" + IsSun + '\'' +
                ", IsMon='" + IsMon + '\'' +
                ", IsTue='" + IsTue + '\'' +
                ", IsWdn='" + IsWdn + '\'' +
                ", IsThu='" + IsThu + '\'' +
                ", IsFri='" + IsFri + '\'' +
                ", IsSat='" + IsSat + '\'' +
                ", UniqueNo='" + UniqueNo + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull Object another) {
        return 0;
    }
}
