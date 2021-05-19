package com.chug.north_outlet.activity;

import android.annotation.TargetApi;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.chug.north_outlet.App;
import com.chug.north_outlet.Config;
import com.chug.north_outlet.Prefix;
import com.chug.north_outlet.R;
import com.chug.north_outlet.RequestCode;
import com.chug.north_outlet.adapter.ScheduleDeviceEFOutletAdapter;
import com.chug.north_outlet.bean.ControlDevice;
import com.chug.north_outlet.bean.DbScheduleInfo;
import com.chug.north_outlet.bean.EFDevice;
import com.chug.north_outlet.bean.EFDeviceOutlet;
import com.chug.north_outlet.callback.ListPositionCallback;
import com.chug.north_outlet.callback.SendCallback;
import com.chug.north_outlet.dao.DaoUtil;
import com.chug.north_outlet.http.HttpAgent;
import com.chug.north_outlet.http.XlinkClient;
import com.chug.north_outlet.utils.ByteUtils;
import com.chug.north_outlet.utils.Constant;
import com.chug.north_outlet.utils.DeviceManager;
import com.chug.north_outlet.utils.PreferenceHelper;
import com.chug.north_outlet.utils.Util;
import com.chug.north_outlet.utils.XlinkUtils;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import butterknife.OnClick;
import io.xlink.wifi.sdk.XlinkAgent;

import static android.support.test.InstrumentationRegistry.getContext;
import static com.chug.north_outlet.utils.Constant.USER_ID;
import static com.chug.north_outlet.utils.Constant.userPassword;

public class AddScheduleActivity extends AppCompatActivity {

    @BindView(R.id.txtOnTime)
    TextView txtOnTime;
    @BindView(R.id.txtOffTime)
    TextView txtOffTime;
    @BindView(R.id.rl_add_schedule_back)
    RelativeLayout rlScheduleSave;
    @BindView(R.id.rl_schedule_save)
    RelativeLayout rlScheduleBack;
    @BindView(R.id.lv_schedule_devices)
    ListView lvDevices;
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

    List<EFDeviceOutlet> deviceOutletList, addedDeviceList;
    Date offDateTime, onDateTime;
    int onDay, offDay;
    Boolean isDayDifferent;
    int onMinute, offMinute;
    String onDateTimeString, offDateString;
    boolean isDeviceSelected = false;
    private String onTime = "";
    private String offTime = "";
    private Boolean isSun = true;
    private Boolean isMon = true;
    private Boolean isTue = true;
    private Boolean isWdn = true;
    private Boolean isThu = true;
    private Boolean isFri = true;
    private Boolean isSat = true;
    private boolean checkedAll = true;

    private ScheduleDeviceEFOutletAdapter mDeviceListScheduleAdapter;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int requestCode = intent.getIntExtra(Config.REQUEST_CODE_KEY, -1);
            byte[] data = intent.getByteArrayExtra(Config.DATA);
            if (data == null) {
                return;
            }
        }
    };

    public static String formatDateToString(Date date, String format,
                                            String timeZone) {
        // null check
        if (date == null) return null;
        // create SimpleDateFormat object with input format
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        // default system timezone if passed null or empty
        if (timeZone == null || "".equalsIgnoreCase(timeZone.trim())) {
            timeZone = Calendar.getInstance().getTimeZone().getID();
        }
        // set timezone to SimpleDateFormat
        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        // return Date in required format with timezone as String
        return sdf.format(date);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.primary_dark));
        setContentView(R.layout.activity_add_schedule);
        ButterKnife.bind(this);
        login();
//        getSupportActionBar().hide();

        addedDeviceList = Util.initAddedDeviceList();

        mDeviceListScheduleAdapter = new ScheduleDeviceEFOutletAdapter(this, new ListPositionCallback() {
            @Override
            public void currentPosition(int position) {

            }
        });

        mDeviceListScheduleAdapter.setList(addedDeviceList);
       /* spinner.setAdapter(mDeviceListScheduleAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        lvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                addedDeviceList.get(i).setDeviceSelected(true);
                for (int i1 = 0; i1 < addedDeviceList.size(); i1++) {
                    if (i1 != i) {
                        addedDeviceList.get(i1).setDeviceSelected(false);
                    }
                }
                mDeviceListScheduleAdapter.notifyDataSetChanged();
            }
        });
        lvDevices.setAdapter(mDeviceListScheduleAdapter);
        txtOnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddScheduleActivity.this, new TimePickerDialog.OnTimeSetListener() {
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


                        // date = new Date(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE),selectedHour,selectedMinute);
                        /*SimpleDateFormat sf = new SimpleDateFormat("yyy-MM-dd HH:mm");
                        TimeZone tzLocal = TimeZone.getDefault();
                        sf.setTimeZone(tzLocal);
                        try {
                            date = sf.parse(calendar.get(Calendar.YEAR)+"-"+calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.DATE)+" "+selectedHour+":"+selectedMinute);
                            Log.e("time","local hour  = "+date.getHours());
                            Log.e("time","local min = "+date.getMinutes());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }*/

                        SimpleDateFormat sfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        sfLocal.setTimeZone(TimeZone.getDefault());

                            /*dateUTc = sfLocal.parse(calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DATE) + " " + selectedHour + ":" + selectedMinute);
                            SimpleDateFormat sfUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
                            sfUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
                            Date dateUtc = sfUTC.parse(sfUTC.format(dateUTc));
                            Log.e("time", "utc hour  = " + dateUtc);
                            Log.e("time", "utc min = " + dateUtc);*/


                        Date currentDate = new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), selectedHour, selectedMinute, 0);
                        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            String chineseTime = convertInChineseTimeZone(currentDate);
                            if (chineseTime!=null){
                                onDateTime = simpleDateFormat.parse(chineseTime);
//                                onDay = Integer.parseInt(chineseTime.split("-")[3].substring(1,2));
                            }


                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        /*TimeZone tz1 = TimeZone.getTimeZone("G    MT+8");
                        //  onDateTime = ByteUtils.shiftTimeZone(dateUTc,dateUtc,tz1);


                        Log.e("time", "hour = " + onDateTime.getHours());
                        Log.e("time", "min = " + onDateTime.getMinutes());
                        *//*try {
                            onDateTime = new Date(ByteUtils.convertLocalTimeToBeijingTime(df.parse(""+selectedHour+":"+selectedMinute).getTime()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
*/
//                        txtOnTime.setText("" + selectedHour + ":" + selectedMinute + " " + AM_PM);

                        strSelectedHour = (selectedHour < 10) ? "" + 0 + selectedHour  : selectedHour + "";
                        strSelectedMinute = (selectedMinute < 10) ? "" + 0 + selectedMinute  : selectedMinute + "";

                        txtOnTime.setText(strSelectedHour + ":" + strSelectedMinute );
                        onTime = strSelectedHour + ":" + strSelectedMinute;
//                        onTime = selectedHour + ":" + selectedMinute;
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Start Time");
                mTimePicker.show();
            }
        });

        txtOffTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddScheduleActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //  this.unregisterReceiver(receiver);
    }


    @OnClick(R.id.rl_schedule_save)
    public void Add(View view) {

        // if (onTime.toString().length() == 0) {
        //     Toast.makeText(AddScheduleActivity.this, "Please Select Valid on Time", Toast.LENGTH_LONG).show();
        //  } else if (offTime.toString().length() == 0) {
        //    Toast.makeText(AddScheduleActivity.this, "Please Select Valid ic_off Time", Toast.LENGTH_LONG).show();
        //  }else {

        List<EFDeviceOutlet> selectedDeviceList = new ArrayList<>();
        for (int i = 0; i < addedDeviceList.size(); i++) {
            if (addedDeviceList.get(i).isDeviceSelected()) {
                selectedDeviceList.add(addedDeviceList.get(i));
            }
        }
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

        if (onDateTime == null && offDateTime == null) {
            Toast.makeText(AddScheduleActivity.this, "Please Select either on time or off time!", Toast.LENGTH_LONG).show();
        } else if (selectedDeviceList.size() == 0) {
            Toast.makeText(AddScheduleActivity.this, "Please Select atleast one device!", Toast.LENGTH_LONG).show();
        } else if(!isSun && !isMon && !isTue && !isWdn && !isThu && !isFri && !isSat){
            Toast.makeText(AddScheduleActivity.this, "Please Select atleast one day!", Toast.LENGTH_LONG).show();
        }
        else {
            for (int i = 0; i < selectedDeviceList.size(); i++) {
                if (selectedDeviceList.get(i).isDeviceSelected()) {
                    switch (selectedDeviceList.get(i).getDeviceState()) {
                        case EFDeviceOutlet.STATE_OFF:

                            callSwitchDevice(false, selectedDeviceList.get(i));
                            break;
                        case EFDeviceOutlet.STATE_ON:

                            callSwitchDevice(true, selectedDeviceList.get(i));
                            // switchDevice(false, selectedDeviceList.get(0));
                            break;
                    }
                }
                break;
            }
            for (int j = 0; j < selectedDeviceList.size(); j++) {
                DbScheduleInfo dbScheduleInfo = new DbScheduleInfo();
                dbScheduleInfo.setDeviceId((long) selectedDeviceList.get(j).getId());
                dbScheduleInfo.setName(selectedDeviceList.get(j).getName());
                dbScheduleInfo.setSubName(selectedDeviceList.get(j).getSubName());
                dbScheduleInfo.setIcon(selectedDeviceList.get(j).getIcon());
                dbScheduleInfo.setStatus(selectedDeviceList.get(j).isStatus());
                dbScheduleInfo.setOnTime(onTime);
                dbScheduleInfo.setOffTime(offTime);
                dbScheduleInfo.setIsSun(isSun);
                dbScheduleInfo.setIsMon(isMon);
                dbScheduleInfo.setIsTue(isTue);
                dbScheduleInfo.setIsWdn(isWdn);
                dbScheduleInfo.setIsThu(isThu);
                dbScheduleInfo.setIsFri(isFri);
                dbScheduleInfo.setIsSat(isSat);
                dbScheduleInfo.setUniqueNo(Util.getMaxUniqueNo() + 1);

                DaoUtil.saveOrUpdate(dbScheduleInfo);
            }
            Util.hideKeyboard(this, view);
            Intent intent = new Intent(AddScheduleActivity.this, MenuActivity.class);
            intent.putExtra("fragment", Constant.FROM_ADDSCHEDULE);
            startActivity(intent);
            finishAffinity();
        }
    }

    private void callSwitchDevice(boolean isDeviceOn, final EFDeviceOutlet efDeviceOutlet) {

        if (onDateTime != null && offDateTime != null) {

            if (onDateTime.getTime() > offDateTime.getTime()) {
                switchDevice(false, efDeviceOutlet, 1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        switchDevice(true, efDeviceOutlet, 2);
                    }
                }, 60000);
            } else {
                switchDevice(true, efDeviceOutlet, 1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switchDevice(false, efDeviceOutlet, 2);
                    }
                }, 60000);
            }
//            if (!isDeviceOn){


            /*} else{
                switchDevice(false, efDeviceOutlet);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switchDevice(true, efDeviceOutlet);
                    }
                },5000);

            }*/
        } else if (onDateTime != null)
            switchDevice(true, efDeviceOutlet, 1);
        else if (offDateTime != null)
            switchDevice(false, efDeviceOutlet, 1);
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

         //   Date date = sdCHINA.parse(CHAINADate);

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

    /*public String convertInChineseTimeZone(Date currentDate) {

        Date CurrentDate_UTC = null;
        String CHAINADate = null;
        Date dateLocal = null;
        Calendar calender = Calendar.getInstance();


        SimpleDateFormat sdLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdLocal.setTimeZone(TimeZone.getDefault());


        try {
            Log.e("time", "currentDate  = " + currentDate);
            Log.e("time", "sdLocal1.format(currentDate)  = " + sdLocal.format(currentDate));
            dateLocal = sdLocal.parse(sdLocal.format(currentDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e("time", "local time millis  = " + dateLocal.getTime());


        SimpleDateFormat sdUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            sdUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("time", "CurrentDate_ local = " + sdLocal.format(currentDate));

        // Log.e("time", "utc time millis  = " +);

        try {
            CurrentDate_UTC = sdUTC.parse(sdLocal.format(currentDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String CurrentDate = (sdUTC.format(CurrentDate_UTC));
        Log.e("time", "CurrentDate_ utc = " + CurrentDate);


        Calendar calendarChina = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        Date currentDateChina = new Date(calendarChina.get(Calendar.YEAR), calendarChina.get(Calendar.MONTH), calendarChina.get(Calendar.DATE), calendarChina.get(Calendar.HOUR_OF_DAY), calendarChina.get(Calendar.MINUTE));


        SimpleDateFormat sdCHINA = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        sdCHINA.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));


        Date CurrentDate_CHINA = null,correctChinaTime;
        try {
            CurrentDate_CHINA = sdCHINA.parse(sdUTC.format(currentDate));

            *//*if (currentDateChina.getHours()>CurrentDate_CHINA.getHours()) {
                long millis = CurrentDate_CHINA.getTime()+3600000;

                correctChinaTime = new Date(millis);
                Log.e("time", "CurrentDate china = " + correctChinaTime);
            }*//*

        } catch (ParseException e) {
            e.printStackTrace();
        }

        CHAINADate = (sdCHINA.format(CurrentDate_CHINA));
        Log.e("time", "CurrentDate_ china = " + CHAINADate);

         *//*SimpleDateFormat sdChina = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
         sdChina.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        Date chinaDate = null;
        try {
            chinaDate = sdChina.parse(CHAINADate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.e("time", "china time millis  = " + chinaDate.getTime());

        long timeDifference = chinaDate.getTime() - dateLocal.getTime();


        Log.e("time", "time difference between beijing to local = " + timeDifference);
        calender.setTimeZone(TimeZone.getTimeZone("UTC"));
        calender.setTimeInMillis((dateLocal.getTime()+timeDifference));

        Log.e("time", " time in uct after difference calculation = " + calender.getTime());*//*

    if (CurrentDate_CHINA!=null) {
        if (currentDateChina.getHours() > CurrentDate_CHINA.getHours()) {
            long millis = CurrentDate_CHINA.getTime() + 3600000;

            correctChinaTime = new Date(millis);
            Log.e("time", "CurrentDate china = " + correctChinaTime);
        }else if (currentDateChina.getHours() < CurrentDate_CHINA.getHours()){
                long millis = CurrentDate_CHINA.getTime() - 3600000;
                correctChinaTime = new Date(millis);
        }
    }
        return CHAINADate;
    }*/

    /*public String convertInChineseTimeZone(Date currentDate) {

        Date CurrentDate_UTC = null;
        String CHAINADate = null;
        Date dateLocal;
        Calendar calender = Calendar.getInstance();

        SimpleDateFormat sdLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       // sdLocal.setTimeZone(TimeZone.getDefault());

        try {

            dateLocal = sdLocal.parse(sdLocal.format(currentDate));
            Log.e("time", "local time millis  = " +dateLocal.getTime());

            SimpleDateFormat sdUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdUTC.setTimeZone(TimeZone.getTimeZone("UTC"));

            Log.e("time", "CurrentDate_ local = " + sdLocal.format(currentDate));

            // Log.e("time", "utc time millis  = " +);

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            Date currentUtcDate = new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

            CurrentDate_UTC = sdUTC.parse(sdLocal.format(currentDate));
            String CurrentDate = (sdUTC.format(CurrentDate_UTC));

            SimpleDateFormat sdUtc = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateUtc = sdUtc.parse(CurrentDate);
            Date correct_utcDate = null;
            *//*if (dateUtc.getHours()>currentUtcDate.getHours()){
                long correctUtc = dateUtc.getTime()-(3600000);

                correct_utcDate = new Date(correctUtc);

            }*//*

            Log.e("time", "CurrentDate_ UTC = " + CurrentDate);

            SimpleDateFormat sdCHINA = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


            Date CurrentDate_CHINA;
            *//*if (correct_utcDate!=null) {
                CurrentDate_CHINA = sdCHINA.parse(sdUtc.format(correct_utcDate));
            }else
                CurrentDate_CHINA = sdCHINA.parse(sdUTC.format(CurrentDate_UTC));*//*

            CurrentDate_CHINA = sdCHINA.parse(sdUTC.format(CurrentDate_UTC));
            Log.e("time", "CurrentDate_ china = " + CurrentDate_CHINA);

            sdCHINA.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            CHAINADate = (sdCHINA.format(CurrentDate_CHINA));
            Log.e("time", "CurrentDate_ china = " + CHAINADate);

//            SimpleDateFormat sdChina = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            // sdChina.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
//
//
//            Date chinaDate = sdChina.parse(CHAINADate);
//
//            Log.e("time", "china time millis  = " +chinaDate.getTime());
//
//            long timeDifference = chinaDate.getTime()-dateLocal.getTime();
//
//
//            Log.e("time","time difference between beijing to local = "+timeDifference);
//            calender.setTimeZone(TimeZone.getTimeZone("UTC"));
//            calender.setTimeInMillis((dateLocal.getTime() + timeDifference));
//
//            Log.e("time"," time in utc after difference calculation = "+calender.getTime());


        } catch (Exception e) {
            e.printStackTrace();
        }

        return CHAINADate;
    }*/

    private void switchDevice(final boolean on, final EFDeviceOutlet mOutlet, int commandNumber) {
        byte[] phoneData = ByteUtils.getPhonenumberBytes();
        byte[] commandData = ByteUtils.int2OneByte(on ? 1 : 0);
        ControlDevice mDevice = DeviceManager.getInstance().getDevice(mOutlet.getParentMac());

        Log.e("data", "commanddata = " + commandData);

        Log.e("data", "phoneData = " + Arrays.toString(phoneData));
        Log.e("data", "command number = " + commandNumber);
        byte[] data = null;

        if (mOutlet.getDeviceType() == EFDevice.TYPE_WIF_OUTLET_LEXIN) {

            Log.e("data", "devicemac = " + mOutlet.getDeviceMac());
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
            Date currentDateChina = new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

            if (on) {
                Log.e("data", "on  = " + onDateTime.getHours() + ":" + onDateTime.getMinutes());
                Log.e("data", "current time in millis  = " + currentDateChina.getHours() + ":" + currentDateChina.getMinutes());
                data = ByteUtils.append(Config.STRIP_LENGTH,  // length of byte array which is 60 bytes
                        new byte[]{0x03},         // command No. (1 byte)
                        Prefix.REF_LEXIN_OUTLET,  // device type (2 byte)
                        ByteUtils.getSystemTimeBytes(currentDateChina.getTime()), // frame PID (4 bytes)
                        phoneData,               // USER NUMBER (20 bytes)
                        ByteUtils.hexStringToBytes(mOutlet.getDeviceMac()),  // device MAC (6 bytes)
                        new byte[]{0x01},      // operation type (1 byte)
                        new byte[]{0x00},      // total numbers of timing (1 byte)
                        new byte[]{(byte) commandNumber},         // timing command number  (1 byte)
                        new byte[]{(byte) onDateTime.getHours(), (byte) onDateTime.getMinutes()},  // timing on (2 bytes) (converting long milliseconds in bytes)
                        new byte[]{110, 111},        // timing ic_off (2 bytes) (passing ascii value of n and o --> n = 110 & o =111 to bytes)
                        new byte[]{1},             // socket position flag  (1 byte)
//                        new byte[]{(byte) 0x80},    //  repeat timing flag (1 byte)( Byte.parseByte("00000001",1) converting bit7 -> single mark to 1 byte)
                        new byte[]{(byte) getTimeScedule(onDateTime)},    //  repeat timing flag (1 byte)( Byte.parseByte("00000001",1) converting bit7 -> single mark to 1 byte)
                        new byte[]{0});            // command ON mark (1 byte)

            } else {
                Log.e("data", "ic_off  = " + offDateTime.getHours() + ":" + offDateTime.getMinutes());
                Log.e("data", "current time in millis  = " + currentDateChina.getHours() + ":" + currentDateChina.getMinutes());
                data = ByteUtils.append(Config.STRIP_LENGTH,
                        new byte[]{0x03},
                        Prefix.REF_LEXIN_OUTLET,
                        ByteUtils.getSystemTimeBytes(currentDateChina.getTime()),
                        phoneData,
                        ByteUtils.hexStringToBytes(mOutlet.getDeviceMac()),
                        new byte[]{0x01},
                        new byte[]{0x00},
                        new byte[]{(byte) commandNumber},
                        new byte[]{110, 111},
                        new byte[]{(byte) offDateTime.getHours(), (byte) offDateTime.getMinutes()},
                        new byte[]{1},
//                        new byte[]{(byte) 0x80},
                        new byte[]{(byte) getTimeScedule(offDateTime)},
                        new byte[]{0});
            }

            Log.e("byte_data", Arrays.toString(data));
            Log.e("data", "data = " + Arrays.toString(data));

            XlinkClient.getInstance().sendPipe(mDevice, data, RequestCode.SINGLE_OUTLET_CODE, new SendCallback() {
                @Override
                public void onSendEnd(boolean isSuccess) {
                    Log.e("isSuccess", "isSuccess --> " + isSuccess);
                    if (isSuccess) {
                        Log.e("isSuccess", "DeviceType --> " + mOutlet.getDeviceType());
                        if (mOutlet.getDeviceType() != EFDevice.TYPE_WIF_OUTLET_LEXIN) {

                            mOutlet.setDeviceState(on ? 1 : 0);
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

    @OnClick(R.id.rl_add_schedule_back)
    public void Cancel(View view) {
        Util.hideKeyboard(this, view);
        finish();
    }

    private void login() {
        String userInfo = "";
        String strAppId = "";
        String strAppToken = "";
        userInfo = readFromFile(this.getApplicationContext());
        if (userInfo != "" && userInfo.split(";").length == 2){
            strAppId = userInfo.split(";")[0];
            strAppToken = userInfo.split(";")[1];
        }
//        Toast.makeText(this.getApplicationContext(),"appId: " + strAppId + ", appToken: " + strAppToken, Toast.LENGTH_LONG).show();

        Constant.USER_ID = strAppId;
        userPassword = strAppToken;
        HttpAgent.getInstance().getAppId(strAppId, strAppToken, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String msg, Throwable throwable) {
                XlinkUtils.shortTips(msg);
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String msg) {
                JSONObject object;
                try {
                    object = new JSONObject(msg);
                    Log.e("tag", object.toString());
                    int status = object.getInt("status");
                    if (status == 200) {
                        JSONObject value = object.getJSONObject("user");

                        PreferenceHelper.write(Config.USER_PHONE, USER_ID);
                        PreferenceHelper.write(Config.USER_PWD, userPassword);
                        XlinkAgent.getInstance().login(value.getInt("id"), value.getString("key"));
                        // 默认appid和key，用于服务被系统kill后的重连操作
                        PreferenceHelper.write("appId", value.getInt("id"));
                        PreferenceHelper.write("authKey", value.getString("key"));
                        // 专属于单个用户的appid和key,用于局域网登录区分作用
                        PreferenceHelper.loginWrite(Config.APP_ID + USER_ID, value.getInt("id"));
                        PreferenceHelper.loginWrite(Config.AUTH_KEY + userPassword, value.getString("key"));
                    } else if (status == 403 || status == 404) {
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //<<<<mars_add_20190807
    private String readFromFile(Context context) {
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("SigninActivity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("SigninActivity", "Can not read file: " + e.toString());
        }

        return ret;
    }
    //>>>>

}