package com.chug.north_outlet.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.chug.north_outlet.R;

import com.chug.north_outlet.http.HttpManageNew;
import com.chug.north_outlet.utils.Util;
import com.chug.north_outlet.utils.Validator;
import com.hkm.ui.processbutton.iml.ActionProcessButton;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterAccountActivity extends AppCompatActivity {

    private static final String TAG = RegisterAccountActivity.class.getName();

    @BindView(R.id.edtName)
    EditText edtName;
    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.edtConfirmPassword)
    EditText edtConfirmPassword;
    @BindView(R.id.btnRegister)
    ActionProcessButton btnRegister;

    Context mContext;
    int delay = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);
        ButterKnife.bind(this);
        mContext = this;
        setRegisterButton();
    }

    private void setRegisterButton() {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                delay = 1000;
                btnRegister.setEnabled(true);
                btnRegister.setText("Create Account");
                btnRegister.setBackgroundColor(getResources().getColor(R.color.primary_dark));

                btnRegister.setOnClickNormalState(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (edtName.getText().toString().trim().length() == 0) {
                            Util.showToast(mContext, "The Name shoud not be blank.");
                        } else if (edtEmail.getText().toString().trim().length() == 0) {
                            Util.showToast(mContext, "The email shoud not be blank.");
                        } else if (edtPassword.getText().toString().trim().length() == 0) {
                            Util.showToast(mContext, "The password shoud not be blank.");
                        } else if (!Validator.isValidEmail(edtEmail.getText().toString())) {
                            Util.showToast(mContext, "Enter valid email.");
                        } else if (!(6 <= edtPassword.getText().toString().trim().length() && edtPassword.getText().toString().trim().length() <= 16)) {
                            Util.showToast(mContext, "The password shoud be 6 to 16 characters in length.");
                        }  /*else if (!Validator.isValidPassword(edtPassword.getText().toString())) {
                            Util.showToast(mContext, "Password shoud contain at least single digit, lower and upper case character.");
                        }*/ else if (!edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
                            Util.showToast(mContext, "Confirm password must match with Password!");
                        } else {
                            register();
                            return;
                        }

                        // reset login button
                        setRegisterButton();

                    }
                }).build();
            }
        }, delay);

    }

    private void register() {
        Log.e(TAG, "register");

        if (!Util.isOnline(mContext)) {
            Util.showMessage(mContext, getString(R.string.error_internet));
            return;
        }

        btnRegister.setEnabled(false);
        btnRegister.setProgress(1);

        HttpManageNew.getInstance().registerUserByMail(edtEmail.getText().toString().trim(), edtName.getText().toString().trim(), edtPassword.getText().toString().trim(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                Log.e(TAG, "onSuccess");
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    Log.e(TAG, "onSuccess: " + jsonObject.toString());
                    Util.showToast(mContext, "Account created successfully.");
                    btnRegister.setProgress(100);
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            delay = 1000;
                            finish();
                        }

                    }, delay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    if (responseBody != null) {
                        JSONObject jsonObject = new JSONObject(new String(responseBody));
                        String errorMsg = jsonObject.getJSONObject("error").getString("msg");
                        Log.e(TAG, "onError: " + errorMsg);
                        btnRegister.setProgress(-1);
                        Util.showToast(mContext, "" + errorMsg);
                        setRegisterButton();
                    }
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
            }
        });

    }

}
