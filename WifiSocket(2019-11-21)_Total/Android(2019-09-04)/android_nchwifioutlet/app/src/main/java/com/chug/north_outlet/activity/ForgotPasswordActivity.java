package com.chug.north_outlet.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chug.north_outlet.BuildConfig;
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
import butterknife.OnClick;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = ForgotPasswordActivity.class.getName();

    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.txtConfirmSuccess)
    TextView txtConfirmSuccess;
    @BindView(R.id.btnResetPassword)
    ActionProcessButton btnResetPassword;
    @BindView(R.id.btnConfirmSuccess)
    Button btnConfirmSuccess;
    @BindView(R.id.relativeLayoutSendEmail)
    RelativeLayout relativeLayoutSendEmail;
    @BindView(R.id.relativeLayoutSendSuccess)
    RelativeLayout relativeLayoutSendSuccess;

    Context mContext;
    int delay = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        if (BuildConfig.DEBUG) {
//            edtEmail.setText("yt000605@gmail.com");
            edtEmail.setText("testing.technostacks4@gmail.com");
        }
        mContext = this;
        setResetPasswordButton();
    }

    private void setResetPasswordButton() {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                delay = 2000;
                btnResetPassword.setEnabled(true);
                btnResetPassword.setBackgroundColor(getResources().getColor(R.color.primary_dark));

                btnResetPassword.setOnClickNormalState(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (edtEmail.getText().toString().trim().length() == 0) {
                            Util.showToast(mContext, "The email shoud not be blank.");
                        } else if (!Validator.isValidEmail(edtEmail.getText().toString())) {
                            Util.showToast(mContext, "Enter valid email.");
                        } else {
                            sendEmail();
                            return;
                        }

                        // reset password button
                        setResetPasswordButton();

                    }
                }).build();
            }
        }, delay);

    }

    private void sendEmail() {
        Log.e(TAG, "sendEmail");

        if (!Util.isOnline(mContext)) {
            Util.showMessage(mContext, getString(R.string.error_internet));
            return;
        }

        btnResetPassword.setEnabled(false);
        btnResetPassword.setProgress(1);

        HttpManageNew.getInstance().forgetPasswd(edtEmail.getText().toString().trim(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                Log.e(TAG, "onSuccess");
                btnResetPassword.setProgress(100);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        delay = 2000;

                        String strSendSuccess = "An email has been sent to " + edtEmail.getText().toString().trim()
                                + ". Click the link in that email to reset your password.";
                        txtConfirmSuccess.setText(strSendSuccess);

                        relativeLayoutSendEmail.setVisibility(View.GONE);
                        relativeLayoutSendSuccess.setVisibility(View.VISIBLE);
                    }

                }, delay);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody != null) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(new String(responseBody));
                        String errorMsg = jsonObject.getJSONObject("error").getString("msg");
                        Log.e(TAG, "onError: " + errorMsg);
                        btnResetPassword.setProgress(-1);
                        Util.showToast(mContext, "" + errorMsg);
                        setResetPasswordButton();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @OnClick(R.id.btnConfirmSuccess)
    public void onClick(View v) {
        finish();
    }
}
