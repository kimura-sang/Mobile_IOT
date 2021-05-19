package com.chug.north_outlet.activity;

import android.annotation.TargetApi;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.chug.north_outlet.Config;
import com.chug.north_outlet.Prefix;
import com.chug.north_outlet.R;
import com.chug.north_outlet.RequestCode;
import com.chug.north_outlet.bean.ControlDevice;
import com.chug.north_outlet.bean.DbGroupInfo;
import com.chug.north_outlet.bean.DbScheduleInfo;
import com.chug.north_outlet.bean.EFDevice;
import com.chug.north_outlet.bean.EFDeviceOutlet;
import com.chug.north_outlet.callback.SendCallback;
import com.chug.north_outlet.dao.DaoUtil;
import com.chug.north_outlet.http.XlinkClient;
import com.chug.north_outlet.utils.ByteUtils;
import com.chug.north_outlet.utils.Constant;
import com.chug.north_outlet.utils.DeviceManager;
import com.chug.north_outlet.utils.Util;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditScheduleActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_schedule_device_icon)
    ImageView ivScheduleDeviceIcon;
    @BindView(R.id.ed_schedule_device_name)
    EditText edScheduleDeviceName;
    @BindView(R.id.text_input_add_device_name)
    TextInputLayout textInputAddDeviceName;
    @BindView(R.id.ed_schedule_device_type)
    EditText edScheduleDeviceType;
    @BindView(R.id.text_input_add_device_type)
    TextInputLayout textInputAddDeviceType;
    @BindView(R.id.iv_on_status)
    ImageView ivOnStatus;
    @BindView(R.id.tv_on_status)
    TextView tvOnStatus;
    @BindView(R.id.iv_off_status)
    ImageView ivOffStatus;
    @BindView(R.id.tv_off_status)
    TextView tvOffStatus;
    @BindView(R.id.btn_delete_schedule)
    Button btnDeleteSchedule;
    @BindView(R.id.rl_edit_schedule_back)
    RelativeLayout ivBack;
    @BindView(R.id.chb_sunday)
    AppCompatCheckBox chbSunday;
    @BindView(R.id.chb_monday)
    AppCompatCheckBox chbMonday;
    @BindView(R.id.chb_tuesday)
    AppCompatCheckBox chbTuesday;
    @BindView(R.id.chb_wednesday)
    AppCompatCheckBox chbWednesday;
    @BindView(R.id.chb_thursday)
    AppCompatCheckBox chbThursday;
    @BindView(R.id.chb_friday)
    AppCompatCheckBox chbFriday;
    @BindView(R.id.chb_saturday)
    AppCompatCheckBox chbSaturday;
    @BindView(R.id.txt_server_time)
    TextView txtServerTime;
    @BindView(R.id.txtOnTime)
    TextView txtOnTime;
    @BindView(R.id.txtOffTime)
    TextView txtOffTime;
    @BindView(R.id.rl_schedule_save)
    RelativeLayout relSaveSchedule;

    DbScheduleInfo dbScheduleInfo;
    Date offDateTime, onDateTime;
    private String onTime = "";
    private String offTime = "";
    int selectedPosition;
    private Boolean isSun;
    private Boolean isMon;
    private Boolean isTue;
    private Boolean isWdn;
    private Boolean isThu;
    private Boolean isFri;
    private Boolean isSat;
    private boolean checkedAll;
    int onDay, offDay;
    Boolean isDayDifferent;
    private Long deviceId;
    private String deviceMac;
    private int DELETE_OPERATION = 3;
    private int CHANGE_OPERATION = 2;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.primary_dark));
        setContentView(R.layout.activity_edit_schedule);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getIntent().getExtras()!=null){
            dbScheduleInfo = (DbScheduleInfo) getIntent().getSerializableExtra(Constant.SCHEDULEOUTLET);
            selectedPosition = getIntent().getIntExtra("selectedPosition",0);
        }

        if (dbScheduleInfo!=null){
            edScheduleDeviceName.setText(dbScheduleInfo.getName());
            edScheduleDeviceType.setText(dbScheduleInfo.getSubName());
            deviceId = dbScheduleInfo.getDeviceId();
            if (dbScheduleInfo.getOnTime()!=null && !dbScheduleInfo.getOnTime().isEmpty()){
                onTime = dbScheduleInfo.getOnTime();
                ivOnStatus.setVisibility(View.VISIBLE);
                tvOnStatus.setText(dbScheduleInfo.getOnTime());
                txtOnTime.setText(dbScheduleInfo.getOnTime());
            }else
                ivOnStatus.setVisibility(View.GONE);

            if (dbScheduleInfo.getOffTime()!=null && !dbScheduleInfo.getOffTime().isEmpty()){
                offTime = dbScheduleInfo.getOffTime();
                ivOffStatus.setVisibility(View.VISIBLE);
                tvOffStatus.setText(dbScheduleInfo.getOffTime());
                txtOffTime.setText(dbScheduleInfo.getOffTime());
            }else
                ivOffStatus.setVisibility(View.GONE);

            if (dbScheduleInfo.getIcon()!= -1){

               TypedArray imageIDs = this.getResources().obtainTypedArray(R.array.appliancesIcon);
               ivScheduleDeviceIcon.setImageResource(imageIDs.getResourceId(dbScheduleInfo.getIcon(), -1));
            }else
                ivScheduleDeviceIcon.setImageResource(R.drawable.add_device_default);

                chbSunday.setChecked(dbScheduleInfo.getIsSun());
                chbMonday.setChecked(dbScheduleInfo.getIsMon());
                chbTuesday.setChecked(dbScheduleInfo.getIsTue());
                chbWednesday.setChecked(dbScheduleInfo.getIsWdn());
                chbThursday.setChecked(dbScheduleInfo.getIsThu());
                chbFriday.setChecked(dbScheduleInfo.getIsFri());
                chbSaturday.setChecked(dbScheduleInfo.getIsSat());
                txtServerTime.setText("Server " + Constant.strServerTimeDiff);
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnDeleteSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSchedule();
            }
        });

        txtOnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnTime();
            }
        });

        txtOffTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOffTime();
            }
        });

        relSaveSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSchedule();
            }
        });
    }

    private void deleteSchedule(){
        final List<DbScheduleInfo> dbScheduleInfos = DaoUtil.getAllList(DbScheduleInfo.class);

        List<EFDeviceOutlet> selectedDeviceList = new ArrayList<>();
        selectedDeviceList = Util.getDeviceListFromId(deviceId);
        for (int i = 0; i < selectedDeviceList.size(); i++){
            switchDevice(selectedDeviceList.get(i), 1, DELETE_OPERATION, dbScheduleInfos.get(selectedPosition).getUniqueNo());
        }

        DaoUtil.delete(dbScheduleInfos.get(selectedPosition));
        dbScheduleInfos.remove(selectedPosition);
        Intent intent = new Intent(EditScheduleActivity.this,MenuActivity.class);
        intent.putExtra("fragment", Constant.FROM_ADDSCHEDULE);
        startActivity(intent);
        finishAffinity();
    }

    private void setOnTime(){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(EditScheduleActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String AM_PM;
                String strSelectedHour;
                String strSelectedMinute;
                if (selectedHour < 12) {
                    AM_PM = "AM";
                } else {
                    AM_PM = "PM";
                }

                Calendar calendar = Calendar.getInstance();

                SimpleDateFormat sfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                sfLocal.setTimeZone(TimeZone.getDefault());

                Date currentDate = new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), selectedHour, selectedMinute, 0);
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    String chineseTime = convertInChineseTimeZone(currentDate);
                    if (chineseTime!=null){
                        onDateTime = simpleDateFormat.parse(chineseTime);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                strSelectedHour = (selectedHour < 10) ? "" + 0 + selectedHour  : selectedHour + "";
                strSelectedMinute = (selectedMinute < 10) ? "" + 0 + selectedMinute  : selectedMinute + "";

                txtOnTime.setText(strSelectedHour + ":" + strSelectedMinute );
                onTime = strSelectedHour + ":" + strSelectedMinute;
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Start Time");
        mTimePicker.show();
    }

    private void setOffTime(){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(EditScheduleActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String AM_PM;
                String strSelectedHour = "";
                String strSelectedMinute = "";
                if (selectedHour < 12) {
                    AM_PM = "AM";
                } else {
                    AM_PM = "PM";
                }
                Calendar calendar = Calendar.getInstance();
                Date currentDate = new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), selectedHour, selectedMinute);
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    String chineseTime = convertInChineseTimeZone(currentDate);
                    if (chineseTime!=null)
                        offDateTime = simpleDateFormat.parse(chineseTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                strSelectedHour = (selectedHour < 10) ? "" + 0 + selectedHour  : selectedHour + "";
                strSelectedMinute = (selectedMinute < 10) ? "" + 0 + selectedMinute  : selectedMinute + "";
                txtOffTime.setText( strSelectedHour + ":" + strSelectedMinute);
                offTime = strSelectedHour + ":" + strSelectedMinute;
//                        offTime = selectedHour + ":" + selectedMinute;
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select End Time");
        mTimePicker.show();

    }

    public String convertInChineseTimeZone(Date currentDate) {

        Date CurrentDate_UTC = null,correctChinaTime;
        String CHAINADate = null;
        SimpleDateFormat sdLocal = new SimpleDateFormat("yyy-MM-dd HH:mm:ss zzz");
        sdLocal.setTimeZone(TimeZone.getDefault());
        SimpleDateFormat sdUTC = new SimpleDateFormat("yyy-MM-dd HH:mm:ss zzz");
        sdUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {

            Calendar calendarChina = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
            Date currentDateChina = new Date(calendarChina.get(Calendar.YEAR), calendarChina.get(Calendar.MONTH), calendarChina.get(Calendar.DATE), calendarChina.get(Calendar.HOUR_OF_DAY), calendarChina.get(Calendar.MINUTE));

            Log.e("time", "CurrentDate_UTC = " + sdLocal.format(currentDate));
            CurrentDate_UTC = sdUTC.parse(sdLocal.format(currentDate));
            String CurrentDate = (sdUTC.format(CurrentDate_UTC));
            Log.e("time", "CurrentDate_UTC = " + CurrentDate);

            SimpleDateFormat sdCHINA = new SimpleDateFormat("yyy-MM-dd HH:mm:ss zzz");
            sdCHINA.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            Date CurrentDate_CHINA = sdCHINA.parse(sdUTC.format(CurrentDate_UTC));
            Log.e("time", "CurrentDate_UTC = " + CurrentDate_CHINA);

            SimpleDateFormat sdChineHour = new SimpleDateFormat("HH");
            sdChineHour.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            String hour = sdChineHour.format(CurrentDate_CHINA);

            Log.e("time", "CurrentDate_UTC = " + CHAINADate);

            if (CurrentDate_CHINA!=null) {
                if (currentDateChina.getHours() < Integer.parseInt(hour)) {
                    long millis = CurrentDate_CHINA.getTime() - 3600000;

                    correctChinaTime = new Date(millis);

                    CHAINADate = (sdCHINA.format(correctChinaTime));
                    Log.e("time", "CurrentDate china = " + correctChinaTime);
                }else if (currentDateChina.getHours() > Integer.parseInt(hour)){
                    long millis = CurrentDate_CHINA.getTime() + 3600000;
                    correctChinaTime = new Date(millis);

                    CHAINADate = (sdCHINA.format(correctChinaTime));
                    Log.e("time", "CurrentDate china = " + correctChinaTime);
                }
                else {
                    CHAINADate = (sdCHINA.format(CurrentDate_CHINA));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("time", "final string date = " +CHAINADate );
        return CHAINADate;
    }

    private void editSchedule(){
        List<EFDeviceOutlet> selectedDeviceList = new ArrayList<>();
        selectedDeviceList = Util.getDeviceListFromId(deviceId);

        isSun = chbSunday.isChecked();
        isMon = chbMonday.isChecked();
        isTue = chbTuesday.isChecked();
        isWdn = chbWednesday.isChecked();
        isThu = chbThursday.isChecked();
        isFri = chbFriday.isChecked();
        isSat = chbSaturday.isChecked();

        if(isSun && isMon && isTue && isWdn && isThu && isFri && isSat){
            checkedAll = true;
        }
        else{
            checkedAll = false;
        }

        if (selectedDeviceList.size() == 0) {
            Toast.makeText(EditScheduleActivity.this, "Please Select atleast one device!", Toast.LENGTH_LONG).show();
        } else if(!isSun && !isMon && !isTue && !isWdn && !isThu && !isFri && !isSat){
            Toast.makeText(EditScheduleActivity.this, "Please Select atleast one day!", Toast.LENGTH_LONG).show();
        }
        else {
            for (int i = 0; i < selectedDeviceList.size(); i++) {
                switchDevice(selectedDeviceList.get(i), 1, CHANGE_OPERATION, dbScheduleInfo.getUniqueNo());
            }

            for (int j = 0; j < selectedDeviceList.size(); j++) {
//                DbScheduleInfo dbScheduleInfo = new DbScheduleInfo();
                dbScheduleInfo.setDeviceId((long) selectedDeviceList.get(j).getId());
                dbScheduleInfo.setName(selectedDeviceList.get(j).getName());
                dbScheduleInfo.setSubName(selectedDeviceList.get(j).getSubName());
                dbScheduleInfo.setIcon(selectedDeviceList.get(j).getIcon());
                dbScheduleInfo.setStatus(selectedDeviceList.get(j).isStatus());
                if (onTime != ""){
                    dbScheduleInfo.setOnTime(onTime);
                }
                if (offTime != ""){
                    dbScheduleInfo.setOffTime(offTime);
                }
                dbScheduleInfo.setIsSun(isSun);
                dbScheduleInfo.setIsMon(isMon);
                dbScheduleInfo.setIsTue(isTue);
                dbScheduleInfo.setIsWdn(isWdn);
                dbScheduleInfo.setIsThu(isThu);
                dbScheduleInfo.setIsFri(isFri);
                dbScheduleInfo.setIsSat(isSat);

                DaoUtil.saveOrUpdate(dbScheduleInfo);
            }
//            Util.hideKeyboard(this, view);
            Intent intent = new Intent(EditScheduleActivity.this, MenuActivity.class);
            intent.putExtra("fragment", Constant.FROM_ADDSCHEDULE);
            startActivity(intent);
            finishAffinity();
        }
    }
//
//    private void callSwitchDevice(boolean isDeviceOn, final EFDeviceOutlet efDeviceOutlet) {
//
//        if (onDateTime != null && offDateTime != null) {
//
//            if (onDateTime.getTime() > offDateTime.getTime()) {
//                switchDevice(false, efDeviceOutlet, 1);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        switchDevice(true, efDeviceOutlet, 2);
//                    }
//                }, 60000);
//            } else {
//                switchDevice(true, efDeviceOutlet, 1);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        switchDevice(false, efDeviceOutlet, 2);
//                    }
//                }, 60000);
//            }
//        } else if (onDateTime != null)
//            switchDevice(true, efDeviceOutlet, 1);
//        else if (offDateTime != null)
//            switchDevice(false, efDeviceOutlet, 1);
//    }


    private String getChineseTime(String time, boolean on) {
        int selectedHour, selectedMinute;
        String returnValue = "";

        if (!time.equals("")) {
            String[] strArray = time.split(":");
            if (strArray.length == 2) {
                selectedHour = Integer.parseInt(strArray[0]);
                selectedMinute = Integer.parseInt(strArray[1]);
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
                Date currentDate = new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), selectedHour, selectedMinute, 0);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    String chineseTime = convertInChineseTimeZone(currentDate);
                    if (chineseTime!=null){
                        if (on) {
                            onDateTime = simpleDateFormat.parse(chineseTime);
                            if (onDateTime != null) {
                                String strSelectedHour = (onDateTime.getHours() < 10) ? "" + 0 + onDateTime.getHours()  : onDateTime.getHours() + "";
                                String strSelectedMinute = (onDateTime.getMinutes() < 10) ? "" + 0 + onDateTime.getMinutes()  : onDateTime.getMinutes() + "";
                                returnValue = strSelectedHour + ":" + strSelectedMinute;
                            }
                        } else {
                            offDateTime = simpleDateFormat.parse(chineseTime);
                            if (offDateTime != null) {
                                String strSelectedHour = (offDateTime.getHours() < 10) ? "" + 0 + offDateTime.getHours()  : offDateTime.getHours() + "";
                                String strSelectedMinute = (offDateTime.getMinutes() < 10) ? "" + 0 + offDateTime.getMinutes()  : offDateTime.getMinutes() + "";
                                returnValue = strSelectedHour + ":" + strSelectedMinute;
                            }
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return returnValue;
    }

    private void switchDevice(final EFDeviceOutlet mOutlet, int commandNumber, int operationByte, int uniqueNo) {
        byte[] phoneData = ByteUtils.getPhonenumberBytes();
        ControlDevice mDevice = DeviceManager.getInstance().getDevice(mOutlet.getParentMac());

        Log.e("data", "phoneData = " + Arrays.toString(phoneData));
        Log.e("data", "command number = " + commandNumber);
        byte[] data = null;

        if (mOutlet.getDeviceType() == EFDevice.TYPE_WIF_OUTLET_LEXIN) {

            Log.e("data", "devicemac = " + mOutlet.getDeviceMac());
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
            Date currentDateChina = new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

//            Log.e("data", "on  = " + onDateTime.getHours() + ":" + onDateTime.getMinutes());
//            Log.e("data", "current time in millis  = " + currentDateChina.getHours() + ":" + currentDateChina.getMinutes());

            byte[] onByte, offByte;
            if (onTime == null){
                onByte = new byte[]{110, 111};
            }
            else {
                String chinaTime = getChineseTime(onTime, true);
                onByte = getTimeArrayBybytes(chinaTime);
            }
            if (offTime == null){
                offByte = new byte[]{110, 111};
            }
            else {
                String chinaTime = getChineseTime(offTime, false);
                offByte = getTimeArrayBybytes(chinaTime);
            }

            data = ByteUtils.append(Config.STRIP_LENGTH,  // length of byte array which is 60 bytes
                    new byte[]{0x03},         // command No. (1 byte)
                    Prefix.REF_LEXIN_OUTLET,  // device type (2 byte)
                    ByteUtils.getSystemTimeBytes(currentDateChina.getTime()), // frame PID (4 bytes)
                    phoneData,               // USER NUMBER (20 bytes)
                    ByteUtils.hexStringToBytes(mOutlet.getDeviceMac()),  // device MAC (6 bytes)
                    new byte[]{(byte) operationByte},      // operation type (1 byte)
                    new byte[]{0x00},      // total numbers of timing (1 byte)
                    new byte[]{0x00},      // timing command number  (1 byte)
                    onByte,  // timing on (2 bytes) (converting long milliseconds in bytes)
                    offByte,        // timing ic_off (2 bytes) (passing ascii value of n and o --> n = 110 & o =111 to bytes)
                    new byte[]{1},             // socket position flag  (1 byte)
//                        new byte[]{(byte) 0x80},    //  repeat timing flag (1 byte)( Byte.parseByte("00000001",1) converting bit7 -> single mark to 1 byte)
                    new byte[]{(byte) getTimeScedule(onDateTime)},    //  repeat timing flag (1 byte)( Byte.parseByte("00000001",1) converting bit7 -> single mark to 1 byte)
                    new byte[]{1},
                    new byte[]{(byte) uniqueNo});            // command ON mark (1 byte)

            Log.e("byte_data", Arrays.toString(data));
            Log.e("data", "data = " + Arrays.toString(data));

            XlinkClient.getInstance().sendPipe(mDevice, data, RequestCode.SINGLE_OUTLET_CODE, new SendCallback() {
                @Override
                public void onSendEnd(boolean isSuccess) {
                    Log.e("isSuccess", "isSuccess --> " + isSuccess);
                    if (isSuccess) {
                        Log.e("isSuccess", "DeviceType --> " + mOutlet.getDeviceType());
                        if (mOutlet.getDeviceType() != EFDevice.TYPE_WIF_OUTLET_LEXIN) {
//                            mOutlet.setDeviceState(on ? 1 : 0);
                            DaoUtil.saveOrUpdate(mOutlet);
                        }
                    }
                }
            });
        }
    }

    private int getTimeScedule(Date serverDateTime){
        String temp = "0";
        int timeScedule;
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.setTime(serverDateTime);
        offDay = calendar.get(Calendar.DAY_OF_MONTH);

        if (offDay != currentDay){
            isDayDifferent = true;
//            Toast.makeText(getApplicationContext(), "off day is different, current day:" + currentDay + "server day: " + offDay , Toast.LENGTH_LONG).show();
        }
        else{
//            Toast.makeText(getApplicationContext(), "off day is same, current day:" + currentDay + "server day: " + offDay, Toast.LENGTH_LONG).show();
            isDayDifferent = false;
        }
        if (checkedAll){
            temp = "10000000";
        }
        else{
            if (isDayDifferent){
                if (Constant.serverTimeOffsetMills > 0){
                    temp += (isSat) ? "1" : "0";
                    temp += (isFri) ? "1" : "0";
                    temp += (isThu) ? "1" : "0";
                    temp += (isWdn) ? "1" : "0";
                    temp += (isTue) ? "1" : "0";
                    temp += (isMon) ? "1" : "0";
                    temp += (isSun) ? "1" : "0";
                }
                else{
                    temp += (isMon) ? "1" : "0";
                    temp += (isSun) ? "1" : "0";
                    temp += (isSat) ? "1" : "0";
                    temp += (isFri) ? "1" : "0";
                    temp += (isThu) ? "1" : "0";
                    temp += (isWdn) ? "1" : "0";
                    temp += (isTue) ? "1" : "0";
                }
            }
            else {
                temp += (isSun) ? "1" : "0";
                temp += (isSat) ? "1" : "0";
                temp += (isFri) ? "1" : "0";
                temp += (isThu) ? "1" : "0";
                temp += (isWdn) ? "1" : "0";
                temp += (isTue) ? "1" : "0";
                temp += (isMon) ? "1" : "0";
            }
        }
        timeScedule = Integer.parseInt(temp, 2);

        return timeScedule;
    }

    private byte[] getTimeArrayBybytes(String time){
        byte[] timeArray = {0, 0};
        if (time.split(":").length == 2){
            String strHour = time.split(":")[0];
            String strMinute = time.split(":")[1];
            timeArray = new byte[]{(byte) Integer.parseInt(strHour), (byte) Integer.parseInt(strMinute)};
        }
        else{

        }
        return timeArray;
    }


}
