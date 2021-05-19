package com.chug.north_outlet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.chug.north_outlet.R;
import com.chug.north_outlet.bean.EFDeviceOutlet;
import com.chug.north_outlet.bean.EFScene;
import com.chug.north_outlet.dao.DaoUtil;
import com.chug.north_outlet.utils.Constant;
import com.chug.north_outlet.utils.SharedPrefsUtils;

import org.xutils.db.sqlite.WhereBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class    SplashActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getTimezoneDiff();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent;
                if(SharedPrefsUtils.getBooleanPreference(SplashActivity.this, Constant.IS_APPINTRO,false)){
                    if (SharedPrefsUtils.getBooleanPreference(SplashActivity.this, Constant.IS_LOGIN, false)) {

                        List<EFDeviceOutlet> addedDeviceList = new ArrayList<>();
                        List<EFDeviceOutlet> deviceOutletList = DaoUtil.getAllList(EFDeviceOutlet.class, WhereBuilder.b("sceneId", "=", EFScene.DEFAULT_SCENEID));
                        if(deviceOutletList!=null && !deviceOutletList.isEmpty()){
                            for (int i=0;i<deviceOutletList.size();i++){
                                if(deviceOutletList.get(i).isDeviceAdded())
                                    addedDeviceList.add(deviceOutletList.get(i));
                            }
                        }
                        if(addedDeviceList.isEmpty()){
//                            intent = new Intent(SplashActivity.this, AppIntroActivity.class);
                            intent = new Intent(SplashActivity.this, SignInActivity.class);
//                            intent.putExtra("fragment", Constant.FROM_SCANDEVICE );

                        }else {
//                            intent = new Intent(SplashActivity.this, MenuActivity.class);
                            intent = new Intent(SplashActivity.this, SignInActivity.class);
                        }
                    }else{
                        intent = new Intent(SplashActivity.this,SignInActivity.class);
                    }

                }else{
                    intent = new Intent(SplashActivity.this,SignInActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void getTimezoneDiff(){
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        Date now = new Date();
        int mGMTOffset = tz.getRawOffset();
        String mark = "";
        String strHour = "";
        String strMinute = "";
        long hour;
        Constant.serverTimeOffsetMills = 8 * 3600000 - mGMTOffset;
        if (Constant.serverTimeOffsetMills < 0){
            mark = "-";
        }
        else {
            mark = "+";
        }
        if (tz.inDaylightTime(now)){
            hour = TimeUnit.MILLISECONDS.toHours(Math.abs(Constant.serverTimeOffsetMills)) - 1;
        }
        else {
            hour = TimeUnit.MILLISECONDS.toHours(Math.abs(Constant.serverTimeOffsetMills));
        }
        long minute = TimeUnit.MILLISECONDS.toMinutes(Math.abs(Constant.serverTimeOffsetMills)) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(Math.abs(Constant.serverTimeOffsetMills)));
        strHour = (hour < 10) ?  "" + 0 + hour : "" + hour;
        strMinute = (minute < 10) ? "" + 0 + minute: "" + minute;
        Constant.strServerTimeDiff = " " + mark + strHour + ":" + strMinute;
    }
}
