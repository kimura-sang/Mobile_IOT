package com.chug.north_outlet.fragment;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.ConsumerIrManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.chug.north_outlet.App;
import com.chug.north_outlet.BuildConfig;
import com.chug.north_outlet.Config;
import com.chug.north_outlet.Prefix;
import com.chug.north_outlet.R;
import com.chug.north_outlet.activity.ScanDeviceActivity;
import com.chug.north_outlet.activity.VideoPlayActivity;
import com.chug.north_outlet.bean.ControlDevice;
import com.chug.north_outlet.bean.EFDevice;
import com.chug.north_outlet.bean.EFDeviceOutlet;
import com.chug.north_outlet.bean.EFScene;
import com.chug.north_outlet.dao.DaoUtil;
import com.chug.north_outlet.esptouch.EsptouchTask;
import com.chug.north_outlet.esptouch.IEsptouchListener;
import com.chug.north_outlet.esptouch.IEsptouchResult;
import com.chug.north_outlet.esptouch.task.__IEsptouchTask;
import com.chug.north_outlet.http.HttpAgent;
import com.chug.north_outlet.utils.ByteUtils;
import com.chug.north_outlet.utils.Constant;
import com.chug.north_outlet.utils.DeviceManager;
import com.chug.north_outlet.utils.PreferenceHelper;
import com.chug.north_outlet.utils.SharedPrefsUtils;
import com.chug.north_outlet.utils.UdpClient;
import com.chug.north_outlet.utils.Util;
import com.chug.north_outlet.utils.XlinkUtils;
import com.leo.simplearcloader.SimpleArcDialog;
import com.loopj.android.http.TextHttpResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.db.sqlite.WhereBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.listener.ScanDeviceListener;
import io.xlink.wifi.sdk.listener.SetDeviceAccessKeyListener;

import static com.chug.north_outlet.Config.USER_PWD;
import static com.chug.north_outlet.Config.WIFI_PRODUCTID_LEXIN;
import static com.chug.north_outlet.utils.Constant.userPassword;

public class WiFiSetupFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private Unbinder unbinder;
    private WifiManager mWifiManager;
    private List<EFDeviceOutlet> mOutletList;                                        // 插座列表数据源
    //private EFOutletAdapter mOutletAdapter;
    @BindView(R.id.text_input_layout_ssid)
    TextInputLayout tiSetupSSId;
    @BindView(R.id.text_input_layout_password) TextInputLayout tiSetupPass;
    @BindView(R.id.btn_one_key_setup)
    Button btnOneKey;
    @BindView(R.id.ed_wifi_setup_ssid) EditText edSetupSSId;
    @BindView(R.id.ed_wifi_setup_password) EditText edSetupPass;
    private EFDeviceOutlet mOpOutlet;
    private ProgressDialog dialog;
    private SimpleArcDialog mDialog;
    private int count = 50;
    private String isSsidHiddenStr = "NO";
    private EsptouchTask mEsptouchTask;

    public PopupWindow popupWindow;
    public View thisView;
    public int mWidth;
    public int mHeight;

    private int CURRENT_DIALOG = 0;
    private static final int threeConfirmDialog = 101;
    private static final int confirmRouterDialog = 111;
    private static final int confirmLedDialog = 112;
    private static final int confirmWifiSettingDialog = 113;
    private static final int routerInstructionAlertDialog = 121;
    private static final int routerAnimationAlertDialog = 122;
    private static final int chooseFrequencyAlertDialog = 123;
    private static final int unpluggingAlertDialog = 124;
    private static final int clearButtonAlertDialog = 125;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_wifisetup, container, false);
        unbinder = ButterKnife.bind(this, mView);

        if (SharedPrefsUtils.getStringPreference(getActivity(),Constant.WIFI_PASSWORD)!=null && !SharedPrefsUtils.getStringPreference(getActivity(),Constant.WIFI_PASSWORD).isEmpty() )
                edSetupPass.setText(SharedPrefsUtils.getStringPreference(getActivity(),Constant.WIFI_PASSWORD));

        if (BuildConfig.DEBUG){
    //        edSetupPass.setText("8aa79a1vpf");
            edSetupPass.setText("Qwert!2345Qwert!2345");
        }
        btnOneKey.setOnClickListener(this);
        initData();
        edSetupPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    performClickOfOneKeySetup();
                }
                return false;
            }
        });
        return mView;
    }


    private void initData() {
        login();
        startXlinkAgent();
     //   mOutletAdapter = new EFOutletAdapter(getActivity());
        //loadScanData();
    //    mOutletAdapter.setList(mOutletList);
        getWifiName();
    }

    private void getWifiName() {
        mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(getActivity().WIFI_SERVICE);
        if (!mWifiManager.isWifiEnabled()) {
            // XlinkUtils.shortTips(getString(R.string.connect_wifi));
            Toast.makeText(getActivity(), getString(R.string.connect_wifi), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isWifiConnected()) {
            // XlinkUtils.shortTips(getString(R.string.noWifiLan));
            Toast.makeText(getActivity(), getString(R.string.noWifiLan), Toast.LENGTH_SHORT).show();
            return;
        }
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        String ssidString = wifiInfo.getSSID();
        int version = getAndroidSDKVersion();
        if (version > 16 && ssidString.startsWith("\"") && ssidString.endsWith("\"")) {
            ssidString = ssidString.substring(1, ssidString.length() - 1);
        }
        final String[] spKey = new String[]{ssidString};
        Log.e("ssid","wifi ssid = "+spKey[0]);
        edSetupSSId.setText(spKey[0]);
    }

    private int getAndroidSDKVersion() {
        int version = 0;
        try {
            version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
        } catch (NumberFormatException e) {
        }
        return version;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void startXlinkAgent() {
        String phone = PreferenceHelper.readString(Config.USER_PHONE);
        if (phone != null && !XlinkAgent.getInstance().isConnectedLocal()) {
            XlinkAgent.getInstance().start();
        }
        if (phone != null && !XlinkAgent.getInstance().isConnectedOuterNet()) {
            XlinkAgent.getInstance().login(PreferenceHelper.readLoginInt(Config.APP_ID + phone), PreferenceHelper.readLoginString(Config.AUTH_KEY + phone));
        }
    }

    boolean isConfWifiComplete = false;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    handler.sendEmptyMessageDelayed(2, 500);
                    /*if (dialog != null) {
                        dialog = null;
                    }*/
                    count = 15;
                    break;
                case 2:
                    if (mDialog == null) {
                        mDialog = Util.showArcDialog(getActivity());
                    }
                    //dialog.setProgress(100 - count * 2);
                    count--;
                    handler.sendEmptyMessage(3);
                    isConfWifiComplete = true;
                    handler.sendEmptyMessageDelayed(2, 900);
                    if (count <= 0) {
                        mDialog.dismiss();
                        handler.sendEmptyMessage(5);
                    }
                    break;
                case 3:
                    if (isConfWifiComplete) {
                        initScan();
                        scanDevices();
                        isConfWifiComplete = false;
                    }
                    break;
                case 4:
                    scanWifi();
                    break;
                case 5:
                    if(mDialog.isShowing())
                        mDialog.dismiss();
                    loadScanData();
                    handler.removeMessages(2);
                    handler.removeMessages(4);

                    mEsptouchTask.interrupt();
                    break;
            }
        }
    };

    private void login() {
        String userInfo = "";
        String strAppId = "";
        String strAppToken = "";
        userInfo = readFromFile(getActivity().getApplicationContext());
        if (userInfo != "" && userInfo.split(";").length == 2){
            strAppId = userInfo.split(";")[0];
            strAppToken = userInfo.split(";")[1];
        }
//        Toast.makeText(getContext(),"appId: " + strAppId + ", appToken: " + strAppToken, Toast.LENGTH_LONG).show();

//        HttpAgent.getInstance().getAppId("18129679136", "66666666", new TextHttpResponseHandler() {
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
                    int status = object.getInt("status");
                    if (status == 200) {
                        JSONObject value = object.getJSONObject("user");

                        PreferenceHelper.write(Config.USER_PHONE, Constant.USER_ID);
                        PreferenceHelper.write(Config.USER_PWD, userPassword);
                        // 默认appid和key，用于服务被系统kill后的重连操作
                        PreferenceHelper.write("appId", value.getInt("id"));
                        PreferenceHelper.write("authKey", value.getString("key"));
                        // 专属于单个用户的appid和key,用于局域网登录区分作用
                        PreferenceHelper.loginWrite(Config.APP_ID + Constant.USER_ID, value.getInt("id"));
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

    private void scanDevices() {
        handler.sendEmptyMessage(4);
    }
    private void loadOutletData(int type) {

        WhereBuilder builder = WhereBuilder.b();
        builder.and("sceneId", "=", EFScene.DEFAULT_SCENEID);
        builder.and("Name", "==", null);
        mOutletList = DaoUtil.getAllList(EFDeviceOutlet.class, builder);
        if (mOutletList != null && !mOutletList.isEmpty()) {

            Log.d("ScanDeviceActivity", "mOutletList.size():" + mOutletList.size());
            Intent intent = new Intent(getActivity(), ScanDeviceActivity.class);
            intent.putExtra("deviceList", (Serializable) mOutletList);
            startActivity(intent);

        }else {
          //  Util.showMessage(getActivity(),"Please reset outlet and try again.");
//            Intent intent = new Intent(getActivity(), ScanDeviceActivity.class);
//            intent.putExtra("deviceList", (Serializable) mOutletList);
//            startActivity(intent);
            //<<<<mars_add_20190806
            showThreeConfirmDialog(getResources().getString(R.string.dialog_title),"Do you Know if your phone is connected to the 2.4 ghz frequency?");
            //>>>>

        }
       // mOutletAdapter.setList(mOutletList);
       // mOutletAdapter.notifyDataSetChanged();
    }

    private void loadScanData() {
        loadOutletData(0);
    }

    private ScanDeviceListener scanListener = new ScanDeviceListener() {
        @Override
        public void onGotDeviceByScan(XDevice device) {
            Log.d("scanDeviec", device.getDeviceName() + "======" + device.getDeviceId() + "======" + device);
            checkDeviceType(device);
        }
    };

    private void checkDeviceType(XDevice device) {
        int type = ControlDevice.NORMAL_DEVICE;
        String name = device.getDeviceName();
        String curMac = device.getMacAddress();
        if (name.equals(String.valueOf(EFDevice.TYPE_WIFI_OUTLET)) || name.equals(String.valueOf(EFDevice.TYPE_WIF_OUTLET_LEXIN))) {
            //updata outlet
            type = ControlDevice.WIFI_DEVICE_OUTLET;
            EFDeviceOutlet ot = new EFDeviceOutlet();
            ot.setDeviceType(Integer.parseInt(name));
            ot.setDeviceState(EFDevice.STATUS_UNKNOWN);
            ot.setDeviceMac(curMac);
            ot.setParentMac(curMac);
            ByteUtils.updateOutlet(ot);
            DeviceManager.getInstance().addDevice(device);
            if (!device.isInit()) {
                XlinkAgent.getInstance().initDevice(device);
            }
            Log.d("ScanDeviceActivity", "DeviceManager.getInstance().getDevices().size():" + DeviceManager.getInstance().getDevices().size());
            if (device.getAccessKey() < 0) {
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
            }
            if (!device.isInit()) {
                XlinkAgent.getInstance().initDevice(device);
            }
        }
    }

    private void senBindData() {
        UdpClient.getInstance().connectSocket(0);
        byte[] outletData = ByteUtils.append(Config.FORTY_LENGTH, Prefix.SCAN_WIFI_DEVICE,
                ByteUtils.getPhonenumberBytes());
        //Log.d(TAG, "outletData:" + outletData);
        UdpClient.getInstance().sendData(1, "255.255.255.255", outletData, null);
    }

    private void scanWifi() {
        //      Send the binding instruction
        senBindData();
        int ret_lexin = XlinkAgent.getInstance().scanDeviceByProductId(WIFI_PRODUCTID_LEXIN, scanListener);
        if (ret_lexin != 0) {
            switch (ret_lexin) {
                case XlinkCode.NO_CONNECT_SERVER:
                    XlinkAgent.getInstance().start();
                    break;
                case XlinkCode.NETWORD_UNAVAILABLE:
                    break;
                default:
                    break;
            }
            return;
        }
    }
    private List<ControlDevice> devices;
    private void updataDevice(List<ControlDevice> devices) {
        this.devices = devices;
    }
    private void initScan() {
        updataDevice(DeviceManager.getInstance().getDevices());
        String phone = PreferenceHelper.readString(Config.USER_PHONE);
        if (phone != null && !XlinkAgent.getInstance().isConnectedLocal()) {
            XlinkAgent.getInstance().start();
        }
        if (phone != null && !XlinkAgent.getInstance().isConnectedOuterNet()) {
            int appId = PreferenceHelper.readLoginInt(Config.APP_ID + phone);
            String authKey = PreferenceHelper.readLoginString(Config.AUTH_KEY + phone);
            XlinkAgent.getInstance().login(appId, authKey);
        }
    }
    @Override
    public void onClick(View view) {

        performClickOfOneKeySetup();
    }

   private void performClickOfOneKeySetup(){
       if(edSetupSSId.getText().toString().equals("")){
           tiSetupSSId.setError("SSID can't be Empty!");

       }else{
           tiSetupSSId.setError(null);
           if(edSetupPass.getText().toString().equals("")){
               tiSetupPass.setError("Password can't be Empty!");
           }else{
               tiSetupPass.setError(null);
               handler.sendEmptyMessage(1);

               mDialog = Util.showArcDialog(getActivity());
               btnOneKey.setClickable(false);
               SharedPrefsUtils.setStringPreference(getActivity(), Constant.WIFI_PASSWORD,edSetupPass.getText().toString());
//                Log.e("ssid","ssid = "+edSetupSSId.getText().toString().trim().substring(10, edSetupSSId.length()));
               esptouch(edSetupSSId.getText().toString().trim(), edSetupPass.getText().toString());
               btnOneKey.setClickable(true);
           }
       }
   }
    private void esptouch(String ssid, String pwd) {
        String apSsid = ssid;
        String apPassword = pwd;
        String apBssid = getWifiConnectedBssid();
        if (apBssid == null){
//            showThreeConfirmDialog(getResources().getString(R.string.dialog_title),"Do you Know if your phone is connected to the 2.4 ghz frequency?");
        }
        else{
            Boolean isSsidHidden = false;
            String taskResultCountStr = Integer.toString(-1);
            if (isSsidHidden) {
                isSsidHiddenStr = "YES";
            }

            if (__IEsptouchTask.DEBUG) {
            }
            new Thread(new EsptouchAsyncTasks(apSsid, apBssid, apPassword,
                    isSsidHiddenStr, taskResultCountStr)).start();
        }

    }
    public String getWifiConnectedBssid() {
        WifiInfo mWifiInfo = getConnectionInfo();
        String bssid = null;
        if (mWifiInfo != null && isWifiConnected()) {
            bssid = mWifiInfo.getBSSID();
        }
        return bssid;
    }
    private boolean isWifiConnected() {
        NetworkInfo mWiFiNetworkInfo = getWifiNetworkInfo();
        boolean isWifiConnected = false;
        if (mWiFiNetworkInfo != null) {
            isWifiConnected = mWiFiNetworkInfo.isConnected();
        }
        return isWifiConnected;
    }
    private NetworkInfo getWifiNetworkInfo() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWiFiNetworkInfo;
    }

    private class EsptouchAsyncTasks
            implements Runnable

    {

        String apSsid;
        String apBssid;
        String apPassword;
        String isSsidHiddenStr;
        String taskResultCountStr;

        public EsptouchAsyncTasks(String apSsid,
                                  String apBssid,
                                  String apPassword,
                                  String isSsidHiddenStr,
                                  String taskResultCountStr) {
            this.apSsid = apSsid;
            this.apBssid = apBssid;
            this.apPassword = apPassword;
            this.isSsidHiddenStr = isSsidHiddenStr;
            this.taskResultCountStr = taskResultCountStr;
        }

        @Override
        public void run() {
            if (apBssid == null) {
                while (true) {
                    String wifiConnectedBssid = getWifiConnectedBssid();
                    if (wifiConnectedBssid != null) {
                        apBssid = wifiConnectedBssid;
                        break;
                    }
                }
            }
            int taskResultCount = -1;
            synchronized (this) {
                boolean isSsidHidden = false;
                if (isSsidHiddenStr.equals("YES")) {
                    isSsidHidden = true;
                }
                taskResultCount = Integer.parseInt(taskResultCountStr);
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, isSsidHidden, getActivity());
                mEsptouchTask.setEsptouchListener(myListener);
            }
            final List<IEsptouchResult> resultList = mEsptouchTask.executeForResults(taskResultCount);
            if(getActivity()!=null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    IEsptouchResult firstResult = resultList.get(0);
                    // check whether the task is cancelled and no results received
                    if (!firstResult.isCancelled()) {
                        int count = 0;
                        // max results to be displayed, if it is more than maxDisplayCount,
                        // just show the count of redundant ones
                        final int maxDisplayCount = 5;
                        // the task received some results including cancelled while
                        // executing before receiving enough results
                        if (firstResult.isSuc()) {
                            StringBuilder sb = new StringBuilder();
                            for (IEsptouchResult resultInList : resultList) {
                                sb.append("Esptouch " +
                                        ", bssid = "
                                        + resultInList.getBssid()
                                        + ",InetAddress = "
                                        + resultInList.getInetAddress()
                                        .getHostAddress() + "\n");
                                count++;
                                if (count >= maxDisplayCount) {
                                    break;
                                }
                            }
                            if (count < resultList.size()) {
                                sb.append("\nthere's " + (resultList.size() - count)
                                        + " more result(s) without showing\n");
                            }
                            // XlinkUtils.shortTips(sb.toString());
                            //                    mProgressDialog.setMessage(sb.toString());
                        } else {
                            //                    mProgressDialog.setMessage("Esptouch fail");
                        }
                    }
                }
            });
            }
        }
    }
    private IEsptouchListener myListener = new IEsptouchListener() {

        @Override
        public void onEsptouchResultAdded(final IEsptouchResult result) {
            onEsptoucResultAddedPerform(result);
        }
    };

    private WifiInfo getConnectionInfo() {
        WifiManager mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        return wifiInfo;
    }
    private void onEsptoucResultAddedPerform(final IEsptouchResult result) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String text = result.getBssid() + " is connected to the wifi";
               /* Toast.makeText(getActivity(), text,
                               Toast.LENGTH_LONG).show();*/
            }
        });
    }

    //<<<<mars_add_20190805
    public void showConfirmDialog(String title, String messageDlgContent) {
        try {
            closeAlertDialog();
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View vPopupWindow = inflater.inflate(R.layout.dialog_confirm, null, false);
            popupWindow = new PopupWindow(vPopupWindow, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);

            //计算宽度和高度
            calWidthAndHeight(getContext());

            popupWindow.setWidth(mWidth);
            popupWindow.setHeight(mHeight);
            popupWindow.setOutsideTouchable(false);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    changeWindowAlfa(1f);//pop消失，透明度恢复
                }
            });
            popupWindow.showAtLocation(this.getView(), Gravity.CENTER|Gravity.TOP, 0, 0);

            changeWindowAlfa(0.5f);

            TextView dialog_title = (TextView) vPopupWindow.findViewById(R.id.dialog_title);
            dialog_title.setText(title);

            TextView dialog_message = (TextView) vPopupWindow.findViewById(R.id.dialog_message);
            dialog_message.setText(messageDlgContent);

            TextView btnOK = (TextView) vPopupWindow.findViewById(R.id.dialog_ok);
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeAlertDialog();
                    clickConfirmButton();
                }
            });

            TextView btnCancel = (TextView) vPopupWindow.findViewById(R.id.dialog_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeAlertDialog();
                    clickCancelButton();
                }
            });
        } catch (Exception e) {

        }
    }

    public void closeAlertDialog() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    public void calWidthAndHeight(Context context) {
        WindowManager wm= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics= new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        mWidth = metrics.widthPixels;
        mHeight = metrics.heightPixels;
    }

    public void changeWindowAlfa(float alfa) {
        WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
        params.alpha = alfa;
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getActivity().getWindow().setAttributes(params);
    }

    public void clickCancelButton() {
        closeAlertDialog();
        switch (CURRENT_DIALOG){
            case threeConfirmDialog:
                CURRENT_DIALOG = confirmRouterDialog;
                showConfirmDialog(getResources().getString(R.string.dialog_title), "Do you know how to access your routers setup screen?");
                break;
            case confirmLedDialog:
                CURRENT_DIALOG = clearButtonAlertDialog;
                showAlertDialog(getResources().getString(R.string.dialog_title), "Press and hold the clear button on the device for 30 seconds and release" +
                        " to reset the device and repeat the setup process");
                break;
            case confirmRouterDialog:
                CURRENT_DIALOG = routerAnimationAlertDialog;
                showAlertDialog(getResources().getString(R.string.dialog_title), "Please watch this animation for a way that may help force the phone to use the 2.4ghz band in your router");
                break;
            case confirmWifiSettingDialog:
                CURRENT_DIALOG = routerAnimationAlertDialog;
                showAlertDialog(getResources().getString(R.string.dialog_title), "Please watch this animation for a way that may help force the phone to use the 2.4ghz band in your router");
                break;
        }

    }

    public void clickConfirmButton() {
        closeAlertDialog();
        switch (CURRENT_DIALOG){
            case threeConfirmDialog:
                CURRENT_DIALOG = confirmLedDialog;
                showConfirmDialog(getResources().getString(R.string.dialog_title), "Is the LED light flashing red and blue on device?");
                break;
            case confirmRouterDialog:
                CURRENT_DIALOG = routerInstructionAlertDialog;
                showAlertDialog(getResources().getString(R.string.dialog_title), "Please follow your routers instructions to disable the 5ghz band." +
                        " Tou can turn it back on after the device setup is complete");
                break;
            case confirmWifiSettingDialog:
                CURRENT_DIALOG = chooseFrequencyAlertDialog;
                showAlertDialog(getResources().getString(R.string.dialog_title), "Choose the 2.4ghz name in your phones setup screen and repeat the setup process");
                break;
            case confirmLedDialog:
                CURRENT_DIALOG = unpluggingAlertDialog;
                showAlertDialog(getResources().getString(R.string.dialog_title), "Please try unplugging the device and repeat the setup process");
                break;
        }
    }

    public void clickUnsureButton() {
        closeAlertDialog();
        CURRENT_DIALOG = confirmWifiSettingDialog;
        showConfirmDialog(getResources().getString(R.string.dialog_title), "Do you see a choice in your wifi settings with numbers 2.4ghz in the wifi name?");
    }

    public void clickOKButton(){
        closeAlertDialog();
        switch (CURRENT_DIALOG){
            case routerInstructionAlertDialog:
                break;
            case routerAnimationAlertDialog:
                gotoVideoPlayActivity();
                break;
            case chooseFrequencyAlertDialog:
                break;
            case unpluggingAlertDialog:
                break;
            case clearButtonAlertDialog:
                break;
        }
        CURRENT_DIALOG = 0;
    }

    public void showThreeConfirmDialog(String title, String messageDlgContent){
        try {
            closeAlertDialog();
            CURRENT_DIALOG = threeConfirmDialog;
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View vPopupWindow = inflater.inflate(R.layout.dialog_three_confirm, null, false);
            popupWindow = new PopupWindow(vPopupWindow, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);

            //计算宽度和高度
            calWidthAndHeight(getContext());

            popupWindow.setWidth(mWidth);
            popupWindow.setHeight(mHeight);
            popupWindow.setOutsideTouchable(false);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    changeWindowAlfa(1f);//pop消失，透明度恢复
                }
            });
            popupWindow.showAtLocation(this.getView(), Gravity.CENTER|Gravity.TOP, 0, 0);

            changeWindowAlfa(0.5f);

            TextView dialog_title = (TextView) vPopupWindow.findViewById(R.id.dialog_title);
            dialog_title.setText(title);

            TextView dialog_message = (TextView) vPopupWindow.findViewById(R.id.dialog_message);
            dialog_message.setText(messageDlgContent);

            TextView btnOK = (TextView) vPopupWindow.findViewById(R.id.dialog_ok);
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeAlertDialog();
                    clickConfirmButton();
                }
            });

            TextView btnCancel = (TextView) vPopupWindow.findViewById(R.id.dialog_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeAlertDialog();
                    clickCancelButton();
                }
            });

            TextView btnUnsure = (TextView) vPopupWindow.findViewById(R.id.dialog_unsure);
            btnUnsure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeAlertDialog();
                    clickUnsureButton();
                }
            });
        } catch (Exception e) {

        }
    }

    public void showAlertDialog(String title,  String messageDlgContent) {
        try {
            closeAlertDialog();
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View vPopupWindow = inflater.inflate(R.layout.dialog_message, null, false);
            popupWindow = new PopupWindow(vPopupWindow, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);

            calWidthAndHeight(getContext());

            popupWindow.setWidth(mWidth);
            popupWindow.setHeight(mHeight);
            popupWindow.setOutsideTouchable(false);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    changeWindowAlfa(1f);//pop消失，透明度恢复
                }
            });
            popupWindow.showAtLocation(this.getView(), Gravity.CENTER|Gravity.TOP, 0, 0);
            //popupWindow.setFocusable(true);

            //popupWindow.setBackgroundDrawable(new BitmapDrawable());//与上面一起用才能实现点击外部消失
            changeWindowAlfa(0.5f);

            TextView dialog_message = (TextView) vPopupWindow.findViewById(R.id.dialog_message);
            dialog_message.setText(messageDlgContent);

            TextView dialog_title = (TextView) vPopupWindow.findViewById(R.id.dialog_title);
            dialog_title.setText(title);

            TextView btnOK = (TextView) vPopupWindow.findViewById(R.id.dialog_ok);
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickOKButton();
                }
            });
        } catch (Exception e) {

        }
    }

    public void gotoVideoPlayActivity(){
        Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
        startActivity(intent);
    }
    //>>>>

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
//                showToast(ret);
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
