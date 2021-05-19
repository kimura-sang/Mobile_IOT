package com.chug.north_outlet.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.chug.north_outlet.App;
import com.chug.north_outlet.BuildConfig;
import com.chug.north_outlet.R;
import com.chug.north_outlet.bean.EFDeviceOutlet;
import com.chug.north_outlet.bean.EFScene;
import com.chug.north_outlet.dao.DaoUtil;
import com.chug.north_outlet.http.HttpManageNew;
import com.chug.north_outlet.utils.Constant;
import com.chug.north_outlet.utils.SharedPrefsUtils;
import com.chug.north_outlet.utils.Util;
import com.chug.north_outlet.utils.Validator;
import com.hkm.ui.processbutton.iml.ActionProcessButton;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.db.sqlite.WhereBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = SignInActivity.class.getName();

    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.btnLogin)
    ActionProcessButton btnLogin;
    @BindView(R.id.chk_remember_me)
    CheckBox chkRemember;

    Context mContext;

    int delay = 10;

    boolean showLoginResult = false;

    private static final int LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        tryToReadSSID();

        ButterKnife.bind(this);
        if (BuildConfig.DEBUG) {
//            edtEmail.setText("testing.technostacks4@gmail.com");
//            edtPassword.setText("welcome123");
            edtEmail.setText("yt000605@gmail.com");
            edtPassword.setText("123456");
        }
        mContext = this;
        setLoginInfoAccordingRemember();
        setLoginButton();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == LOCATION){
            //User allowed the location and you can read it now
            tryToReadSSID();
        }
    }

    private void tryToReadSSID() {
        //If requested permission isn't Granted yet
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Request permission from user
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION);
        }else{//Permission already granted
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if(wifiInfo.getSupplicantState() == SupplicantState.COMPLETED){
                String ssid = wifiInfo.getSSID();//Here you can access your SSID
                System.out.println(ssid);
            }
        }
    }

    private void setLoginInfoAccordingRemember(){
        if (SharedPrefsUtils.getBooleanPreference(SignInActivity.this, Constant.IS_LOGIN, false)){
            String savedEmail = SharedPrefsUtils.getStringPreference(SignInActivity.this, Constant.EMAIL_ID);
            String savedPassword =  SharedPrefsUtils.getStringPreference(SignInActivity.this, Constant.LOGIN_PASSWORDD);
            edtEmail.setText(savedEmail);
            edtPassword.setText(savedPassword);
//            Toast.makeText(SignInActivity.this, "remember is checked: email id: " + savedEmail + " password: " + savedPassword , Toast.LENGTH_LONG).show();
        }
        else{
//            Toast.makeText(SignInActivity.this, "remember is unchecked", Toast.LENGTH_LONG).show();
        }
    }

    private void setLoginButton() {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                delay = 2000;
                btnLogin.setEnabled(true);
                btnLogin.setText("Sign In");
                btnLogin.setBackgroundColor(getResources().getColor(R.color.primary_dark));

                btnLogin.setOnClickNormalState(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (edtEmail.getText().toString().trim().length() == 0) {
                            Util.showToast(mContext, "The email shoud not be blank.");
                        } else if (edtPassword.getText().toString().trim().length() == 0) {
                            Util.showToast(mContext, "The password shoud not be blank.");
                        } else if (!Validator.isValidEmail(edtEmail.getText().toString())) {
                            Util.showToast(mContext, "Enter valid email.");
                        } else if (8 <= edtPassword.getText().toString().trim().length() && edtPassword.getText().toString().trim().length() >= 16) {
                            Util.showToast(mContext, "The password should be 8 to 16 characters in length.");
                        } else {
                            login();
                            return;
                        }

                        // reset login button
                        setLoginButton();

                    }
                }).build();

            }
        }, delay);

    }

    @OnClick(R.id.tv_link_signup)
    public void signUpOnClick(View view) {
        // TODO submit data to server...
        Intent mIntent = new Intent(SignInActivity.this, RegisterAccountActivity.class);
        startActivity(mIntent);
    }

    @OnClick(R.id.txtForgotPassword)
    public void forgotPasswordOnClick(View view) {
        Intent mIntent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
        startActivity(mIntent);
    }

    private void login() {
        Log.e(TAG, "login");

        if (!Util.isOnline(mContext)) {
            Util.showMessage(mContext, getString(R.string.error_internet));
            return;
        }
        btnLogin.setEnabled(false);
        btnLogin.setProgress(1);
        HttpManageNew.getInstance().login(edtEmail.getText().toString().trim(), edtPassword.getText().toString().trim(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                Log.e(TAG, "onSuccess");
                try {
                    if(chkRemember.isChecked()){
                        SharedPrefsUtils.setBooleanPreference(mContext, Constant.IS_LOGIN, true);
                    }
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    // for test
                    if (showLoginResult) {
                        Toast.makeText(getApplicationContext(), "onSuccess: " + jsonObject.toString(), Toast.LENGTH_SHORT).show();
                    }
                    Log.e(TAG, "onSuccess: " + jsonObject.toString());
                    SharedPrefsUtils.setStringPreference(mContext,Constant.EMAIL_ID,edtEmail.getText().toString());
                    SharedPrefsUtils.setStringPreference(mContext,Constant.LOGIN_PASSWORDD,edtPassword.getText().toString());
                    SharedPrefsUtils.setStringPreference(mContext, Constant.USER_ID, jsonObject.getString("user_id"));
                    SharedPrefsUtils.setStringPreference(mContext, Constant.REFRESH_TOKEN, jsonObject.getString("refresh_token"));
                    SharedPrefsUtils.setStringPreference(mContext, Constant.ACCESS_TOKEN, jsonObject.getString("access_token"));
                    SharedPrefsUtils.setStringPreference(mContext, Constant.AUTHORIZE, jsonObject.getString("authorize"));
                    SharedPrefsUtils.setStringPreference(mContext, Constant.EXPIRE_IN, jsonObject.optString("exe_in"));
                    SharedPrefsUtils.setStringPreference(mContext, Constant.LOGIN_USER, jsonObject.toString());
                    SharedPrefsUtils.setStringPreference(mContext, Constant.userPassword, edtPassword.getText().toString().trim());
                    Log.e("login","LOGIN_USER = "+jsonObject.toString());
                    App.getApp().setAccessToken(jsonObject.getString("authorize"));
                    App.getApp().setAppid(jsonObject.getInt("user_id"));
                    Constant.userIdFromServer = jsonObject.getString("user_id");
                    App.getApp().setAuthKey(jsonObject.getString("access_token"));
                    saveUserInfoInStorage(jsonObject.getInt("user_id") + "", jsonObject.getString("authorize"));
                    btnLogin.setProgress(100);
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            delay = 2000;
                            List<EFDeviceOutlet> addedDeviceList = new ArrayList<>();
                            List<EFDeviceOutlet> deviceOutletList = DaoUtil.getAllList(EFDeviceOutlet.class, WhereBuilder.b("sceneId", "=", EFScene.DEFAULT_SCENEID));
                            if(deviceOutletList!=null && !deviceOutletList.isEmpty()){
                                for (int i=0;i<deviceOutletList.size();i++){
                                    if(deviceOutletList.get(i).isDeviceAdded())
                                        addedDeviceList.add(deviceOutletList.get(i));
                                }
                            }
                            Intent mIntent;

                            if(!addedDeviceList.isEmpty()){
                                Toast.makeText(SignInActivity.this, "go to menu", Toast.LENGTH_LONG).show();
                                mIntent = new Intent(SignInActivity.this, MenuActivity.class);
                            }else {
                                Toast.makeText(SignInActivity.this, "go to introduction", Toast.LENGTH_LONG).show();
                                mIntent = new Intent(SignInActivity.this, AppIntroActivity.class);
                            }
                            startActivity(mIntent);
                            finishAffinity();
                        }

                    }, delay);
                } catch (Exception e) {
                    e.printStackTrace();
                    // for test
                    if (showLoginResult) {
                        Toast.makeText(getApplicationContext(), "onSuccess(error): " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                if (responseBody != null) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(new String(responseBody));
                        // for test
                        if (showLoginResult) {
                            Toast.makeText(getApplicationContext(), "onFailure: " + jsonObject.toString(), Toast.LENGTH_SHORT).show();
                        }
                        String errorMsg = jsonObject.getJSONObject("error").getString("msg");
                        Log.e(TAG, "onError: " + errorMsg);
                        btnLogin.setProgress(-1);
                        Util.showToast(mContext, "" + errorMsg);
                        setLoginButton();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // for test
                        if (showLoginResult) {
                            Toast.makeText(getApplicationContext(), "onFailure(error): " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    //<<<< mars_add_20190806
    private void saveUserInfoInStorage(String appId, String appToken){
        try {
            String data = "";
            data = appId + ";" + appToken;
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}
