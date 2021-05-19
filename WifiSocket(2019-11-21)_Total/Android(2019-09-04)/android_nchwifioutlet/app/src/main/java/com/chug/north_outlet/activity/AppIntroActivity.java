package com.chug.north_outlet.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.chug.north_outlet.R;
import com.chug.north_outlet.fragment.AppIntroFragments;
import com.chug.north_outlet.utils.Constant;
import com.chug.north_outlet.utils.SharedPrefsUtils;
import com.github.paolorotolo.appintro.AppIntro;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.chug.north_outlet.utils.Constant.FROM_ADDSCHEDULE;
import static com.chug.north_outlet.utils.Constant.FROM_SCANDEVICE;

public class AppIntroActivity extends AppIntro {

    private String strUserInfo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_app_intro);

        addSlide(AppIntroFragments.newInstance(R.layout.fragment_app_intro_fragment1,R.drawable.app_intro_slide_0));
        addSlide(AppIntroFragments.newInstance(R.layout.fragment_app_intro_fragment1,R.drawable.app_intro_slide_1));
        addSlide(AppIntroFragments.newInstance(R.layout.fragment_app_intro_fragment1,R.drawable.app_intro_slide_2));
        addSlide(AppIntroFragments.newInstance(R.layout.fragment_app_intro_fragment1,R.drawable.app_intro_slide_3));
        addSlide(AppIntroFragments.newInstance(R.layout.fragment_app_intro_fragment1,R.drawable.app_intro_slide_6));
        addSlide(AppIntroFragments.newInstance(R.layout.fragment_app_intro_fragment1,R.drawable.app_intro_slide_7));

        setFlowAnimation();
        setBarColor(Color.parseColor("#88E1CD"));
        setSeparatorColor(Color.parseColor("#A9A9A9"));

        showSkipButton(true);
        setProgressButtonEnabled(true);

//        Toast.makeText(this.getApplicationContext(), strUserInfo, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        SharedPrefsUtils.setBooleanPreference(this, Constant.IS_APPINTRO,true);
        startSignInActivity();
    }

    private void startSignInActivity(){
        Intent intent = new Intent(this,MenuActivity.class);
        intent.putExtra("fragment", FROM_SCANDEVICE);
        startActivity(intent);
        finish();
    }
    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        SharedPrefsUtils.setBooleanPreference(this, Constant.IS_APPINTRO,true);
        startSignInActivity();
        finish();
    }

}
