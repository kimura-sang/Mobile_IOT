package com.chug.north_outlet.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chug.north_outlet.App;
import com.chug.north_outlet.Config;
import com.chug.north_outlet.Prefix;
import com.chug.north_outlet.R;
import com.chug.north_outlet.RequestCode;
import com.chug.north_outlet.adapter.DeviceEFOutletAdapter;
import com.chug.north_outlet.bean.ControlDevice;
import com.chug.north_outlet.bean.EFDevice;
import com.chug.north_outlet.bean.EFDeviceOutlet;
import com.chug.north_outlet.fragment.AboutFragment;
import com.chug.north_outlet.fragment.DeviceFragment;
import com.chug.north_outlet.fragment.GroupFragment;
import com.chug.north_outlet.fragment.ScheduleFragment;
import com.chug.north_outlet.fragment.WiFiSetupFragment;
import com.chug.north_outlet.http.HttpAgent;
import com.chug.north_outlet.http.XlinkClient;
import com.chug.north_outlet.utils.ByteUtils;
import com.chug.north_outlet.utils.Constant;
import com.chug.north_outlet.utils.DeviceManager;
import com.chug.north_outlet.utils.PreferenceHelper;
import com.chug.north_outlet.utils.SharedPrefsUtils;
import com.chug.north_outlet.utils.Util;
import com.chug.north_outlet.utils.XlinkUtils;
import com.loopj.android.http.TextHttpResponseHandler;
import com.specyci.residemenu.ResideMenu;
import com.specyci.residemenu.ResideMenuItem;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import butterknife.ButterKnife;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.listener.SetDeviceAccessKeyListener;

import static android.support.test.InstrumentationRegistry.getContext;
import static com.chug.north_outlet.utils.Constant.USER_ID;
import static com.chug.north_outlet.utils.Constant.userPassword;

public class MenuActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = MenuActivity.class.getName();

    final static String SIDEBAR_TITLES[] = {"DEVICES", "GROUPS", "SCHEDULE", "WIFI SETUP",
            "ABOUT", "LOGOUT"};
    final static int SIDEBAR_IDS[] = {0, 1, 2,
            3,4, 5};
    private ResideMenu resideMenu;
    private MenuActivity mContext;
    private RelativeLayout rlMenu,rlLeftMove;

   // private ResideMenuItem itemDevices,itemGroups,itemSchedules,itemWiFiSetup,itemAbout,itemLogout;

    private TextView txtTitle;


    private enum Operation {
        REFRESH, UPDATE
    }

    private Operation mOperation;

    int fragment = 0;

    private List<EFDeviceOutlet> addedDeviceList;
    private DeviceEFOutletAdapter mOutletAdapter;
    /**
     * Called when the activity is first created.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.primary_dark));
        setContentView(R.layout.activity_menu);
        mContext = this;

        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                if (s.equals("api2.xlink.cn:443")){
                    Log.e("hostname","hostname = true");
                    return true;
                }else{
                    Log.e("hostname","hostname = true");
                    return false;
                }

            }
        });

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        rlMenu = (RelativeLayout) findViewById(R.id.title_bar_menu);
        rlLeftMove = (RelativeLayout) findViewById(R.id.title_bar_left_menu);
        setUpMenu();
        if(getIntent().getExtras()!=null && getIntent().getIntExtra("fragment",0)== Constant.FROM_ADDGRP)
        {
            changeFragment(new GroupFragment());
        }else if (getIntent().getExtras()!=null && getIntent().getIntExtra("fragment",0)== Constant.FROM_ADDSCHEDULE){
            changeFragment(new ScheduleFragment());
        }else if (getIntent().getExtras()!=null && getIntent().getIntExtra("fragment",0)== Constant.FROM_SCANDEVICE){
            changeFragment(new WiFiSetupFragment());
        }
        else{
            changeFragment(new DeviceFragment());
        }


        rlMenu.setOnClickListener(this);
        /*if (Util.getSoftButtonsBarHeight(this) != 0) {
            findViewById(R.id.rootLayout).setPadding(0, 0, 0, Util.getSoftButtonsBarHeight(this));
        }*/

        login();
    }

    /**
     * Access to equipment state of instruction
     */
    private void refreshStatus(ControlDevice mDevice, EFDeviceOutlet mOutlet) {
        mOperation = Operation.REFRESH;
        byte[] phoneData = ByteUtils.getPhonenumberBytes();
        byte[] macData = ByteUtils.hexStringToBytes(mOutlet.getDeviceMac());
        byte[] data = null;
        if (mOutlet.getDeviceType() == EFDevice.TYPE_WIF_OUTLET_LEXIN) {
            data = ByteUtils.append(Config.STRIP_LENGTH,
                    new byte[]{0x0C},
                    Prefix.REF_LEXIN_OUTLET,
                    ByteUtils.getSystemTimeBytes(System.currentTimeMillis()),
                    phoneData,
                    macData);
            XlinkAgent.getInstance().initDevice(mDevice.getXDevice());
            DeviceManager.getInstance().connectDevice(this, mDevice, null);
            XlinkClient.getInstance().sendPipe(mDevice, data, RequestCode.SINGLE_OUTLET_LEXIN_CODE, null);
        }
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
        if (XlinkAgent.getInstance().login(Integer.parseInt(strAppId), strAppToken) == 0) {
            // TODO: restart device connection

            addedDeviceList = Util.initAddedDeviceList();
//            mOutletAdapter.setList(addedDeviceList);
            for (int i = 0; i < addedDeviceList.size(); i++){
                EFDeviceOutlet mOutlet;
                ControlDevice mDevice;
                mOutlet = addedDeviceList.get(i);
                mDevice = DeviceManager.getInstance().getDevice(mOutlet.getParentMac());
                XDevice device = mDevice.getXDevice();
                XlinkAgent.getInstance().setDeviceAccessKey(device, Integer.parseInt(Config.passwrod),
                        new SetDeviceAccessKeyListener() {
                            @Override
                            public void onSetLocalDeviceAccessKey(XDevice xdevice, int code, int msgId) {
                                switch (code) {
                                    case XlinkCode.SUCCEED:
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                refreshStatus(mDevice, mOutlet);
            }
        } else {
            // TODO: go to login page
//            Intent mIntent;
//            mIntent = new Intent(MenuActivity.this, SignInActivity.class);
//            startActivity(mIntent);
//            finish();
        }
//        HttpAgent.getInstance().getAppId(strAppId, strAppToken, new TextHttpResponseHandler() {
//
//            @Override
//            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String msg, Throwable throwable) {
//                XlinkUtils.shortTips(msg);
//            }
//
//            @Override
//            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String msg) {
//
//                JSONObject object;
//                try {
//                    object = new JSONObject(msg);
//                    Log.e(TAG, object.toString());
//                    int status = object.getInt("status");
//                    if (status == 200) {
//                        JSONObject value = object.getJSONObject("user");
//
//                        PreferenceHelper.write(Config.USER_PHONE, USER_ID);
//                        PreferenceHelper.write(Config.USER_PWD, userPassword);
//                        XlinkAgent.getInstance().login(value.getInt("id"), value.getString("key"));
//                        // 默认appid和key，用于服务被系统kill后的重连操作
//                        PreferenceHelper.write("appId", value.getInt("id"));
//                        PreferenceHelper.write("authKey", value.getString("key"));
//                        // 专属于单个用户的appid和key,用于局域网登录区分作用
//                        PreferenceHelper.loginWrite(Config.APP_ID + USER_ID, value.getInt("id"));
//                        PreferenceHelper.loginWrite(Config.AUTH_KEY + userPassword, value.getString("key"));
//                    } else if (status == 403 || status == 404) {
//                    } else {
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

    }

    private void setUpMenu() {

        // attach to current activity;
        resideMenu = new ResideMenu(this);
        // resideMenu.setUse3D(true);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip. 
        resideMenu.setScaleValue(0.6f);

        for (int i = 0; i < SIDEBAR_TITLES.length; i++) {
            ResideMenuItem item = new ResideMenuItem(this,0, SIDEBAR_TITLES[i]);
            item.setId(SIDEBAR_IDS[i]);
            item.setOnClickListener(this);
            resideMenu.addMenuItem(item, ResideMenu.DIRECTION_LEFT);
        }

        // create menu items;
        // itemDevices = new CustomizableResideMenuItem(this, R.drawable.icon_home, "Devices");
        // itemGroups = new CustomizableResideMenuItem(this, R.drawable.icon_profile, "Groups");
        // itemSchedules = new CustomizableResideMenuItem(this, R.drawable.icon_calendar, "Schedules");

        // create menu items;

        final RelativeLayout resideMenuParent = (RelativeLayout) resideMenu.getLeftMenu().getParent().getParent();
        LayoutInflater.from(this).inflate(R.layout.sidebar_header, resideMenuParent, true);
        LayoutInflater.from(this).inflate(R.layout.sidebar_header, resideMenuParent, true);
        View header = ButterKnife.findById(resideMenuParent, R.id.rl_sidebar_top_layout);
       // header.setOnClickListener(this);

        ImageView ivProfilePic = ButterKnife.findById(header, R.id.iv_profile_pic);
        TextView tvProfileName = ButterKnife.findById(header, R.id.tv_prorile_name);

        tvProfileName.setText(SharedPrefsUtils.getStringPreference(mContext,Constant.EMAIL_ID));
        /*itemDevices = new ResideMenuItem(mContext);
        itemDevices.setTitle(getString(R.string.str_devices));

        itemWiFiSetup = new ResideMenuItem(mContext);
        itemWiFiSetup.setTitle(getString(R.string.str_wifi_setup));

        itemGroups = new ResideMenuItem(mContext);
        itemGroups.setTitle(getString(R.string.str_groups));

        itemSchedules = new ResideMenuItem(mContext);
        itemSchedules.setTitle(getString(R.string.str_schedule));

        itemAbout = new ResideMenuItem(mContext);
        itemAbout.setTitle(getString(R.string.about_s));

        itemLogout = new ResideMenuItem(mContext);
        itemLogout.setTitle(getString(R.string.str_logout));

        itemDevices.setOnClickListener(mSidebarListener);
        itemGroups.setOnClickListener(mSidebarListener);
        itemSchedules.setOnClickListener(mSidebarListener);
        itemWiFiSetup.setOnClickListener(mSidebarListener);
        itemAbout.setOnClickListener(mSidebarListener);
        itemLogout.setOnClickListener(mSidebarListener);

resideMenu.addMenuItem(itemDevices, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemGroups, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemSchedules, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemWiFiSetup, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemAbout, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemLogout, ResideMenu.DIRECTION_LEFT);

        */

        // You can disable a direction by setting ->
        // resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

        findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                if (fragment == 0) {
                    changeFragmentWithAnim(new AboutFragment());
                } else if (fragment == 1) {
                    changeFragmentWithAnim(new DeviceFragment());
                } else if (fragment == 2) {
                    changeFragmentWithAnim(new GroupFragment());
                }else if (fragment == 3){
                    changeFragmentWithAnim(new ScheduleFragment());
                }else if (fragment == 4){
                    changeFragmentWithAnim(new WiFiSetupFragment());
                }

            }

        });
        findViewById(R.id.title_bar_right_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
                if (fragment == 0) {
                    changeFragmentWithAnim(new GroupFragment());
                } else if (fragment == 1) {
                    changeFragmentWithAnim(new ScheduleFragment());
                } else if (fragment == 2) {
                    changeFragmentWithAnim(new WiFiSetupFragment());
                }else if (fragment == 3){
                    changeFragmentWithAnim(new AboutFragment());
                }else if (fragment == 4){
                    changeFragmentWithAnim(new DeviceFragment());
                }
            }
        });

        resideMenu.setDirectionDisable(ResideMenu.DIRECTION_RIGHT);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case 0:
                changeFragment(new DeviceFragment());
                resideMenu.closeMenu();
                break;
            case 1:
                changeFragment(new GroupFragment());
                resideMenu.closeMenu();
                break;
            case 2:
                changeFragment(new ScheduleFragment());
                resideMenu.closeMenu();
                break;
            case 3:
                changeFragment(new WiFiSetupFragment());
                resideMenu.closeMenu();
                break;
            case 4:
                changeFragment(new AboutFragment());
                resideMenu.closeMenu();
                break;
            case 5:
                SharedPrefsUtils.setBooleanPreference(MenuActivity.this,Constant.IS_LOGIN,false);
                Intent intent = new Intent(MenuActivity.this,SignInActivity.class);
                startActivity(intent);
                finishAffinity();
                resideMenu.closeMenu();
                break;
            case R.id.title_bar_menu:
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    /*@Override
    public void onClick(View view) {

        if (view == itemDevices) {
            changeFragment(new DeviceFragment());
        } else if (view == itemGroups) {
            changeFragment(new GroupFragment());
        } else if (view == itemSchedules) {
            changeFragment(new ScheduleFragment());
        } else if (view == itemWiFiSetup){
            changeFragment(new WiFiSetupFragment());
        }else if (view == itemAbout){
            changeFragment(new AboutFragment());
        }else if (view == itemLogout){

        }

        resideMenu.closeMenu();
    }
*/
    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            //Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void closeMenu() {
            //Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT).show();
        }
    };

    private void changeFragment(Fragment targetFragment) {

        if (targetFragment.getClass().getName().equals(DeviceFragment.class.getName())) {
            changeMenuVisibility(true);
            fragment = 0;
        } else if (targetFragment.getClass().getName().equals(GroupFragment.class.getName())) {
            changeMenuVisibility(false);
            fragment = 1;
        } else if (targetFragment.getClass().getName().equals(ScheduleFragment.class.getName())) {
            changeMenuVisibility(false);
            fragment = 2;
        } else if (targetFragment.getClass().getName().equals(WiFiSetupFragment.class.getName())) {
            changeMenuVisibility(false);
            fragment = 3;
        } else if (targetFragment.getClass().getName().equals(AboutFragment.class.getName())) {
            changeMenuVisibility(false);
            fragment = 4;
        }

        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();

        setUpToolbarName();

    }

    private void changeMenuVisibility(boolean isMenuVisible){
        if (isMenuVisible){
            rlMenu.setVisibility(View.VISIBLE);
            rlLeftMove.setVisibility(View.GONE);
        }else {
            rlLeftMove.setVisibility(View.VISIBLE);
            rlMenu.setVisibility(View.GONE);
        }
    }
    private void setUpToolbarName() {
        if (fragment == 0) {
            txtTitle.setText(getString(R.string.str_devices));
        } else if (fragment == 1) {
            txtTitle.setText(getString(R.string.str_groups));
        } else if (fragment == 2) {
            txtTitle.setText(getString(R.string.str_schedule));
        } else if (fragment == 3) {
            txtTitle.setText(getString(R.string.str_wifi_setup));
        }else if (fragment == 4) {
            txtTitle.setText(R.string.str_about_title);
        }
    }

    private void changeFragmentWithAnim(Fragment targetFragment) {

        if (targetFragment.getClass().getName().equals(DeviceFragment.class.getName())) {
            changeMenuVisibility(true);
            fragment = 0;
        } else if (targetFragment.getClass().getName().equals(GroupFragment.class.getName())) {
            changeMenuVisibility(false);
            fragment = 1;
        } else if (targetFragment.getClass().getName().equals(ScheduleFragment.class.getName())) {
            changeMenuVisibility(false);
            fragment = 2;
        }else if (targetFragment.getClass().getName().equals(WiFiSetupFragment.class.getName())) {
            changeMenuVisibility(false);
            fragment = 3;
        }else if (targetFragment.getClass().getName().equals(AboutFragment.class.getName())) {
            changeMenuVisibility(false);
            fragment = 4;
        }

        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
//                .setCustomAnimations(
//                        R.animator.card_flip_right_in, R.animator.card_flip_right_out,
//                        R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .commit();

        setUpToolbarName();

    }

    // What good method is to access resideMenu？
    public ResideMenu getResideMenu() {
        return resideMenu;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.ADD_DEVICE) {
            changeFragment(new DeviceFragment());
        } else if (requestCode == Constant.ADD_GROUP) {
            changeFragment(new GroupFragment());
        } else if (requestCode == Constant.ADD_SCHEDULE) {
            changeFragment(new ScheduleFragment());
        }
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