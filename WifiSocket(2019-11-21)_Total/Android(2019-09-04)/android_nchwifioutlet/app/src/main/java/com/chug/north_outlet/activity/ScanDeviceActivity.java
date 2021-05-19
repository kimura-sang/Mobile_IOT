package com.chug.north_outlet.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.chug.north_outlet.Config;
import com.chug.north_outlet.Prefix;
import com.chug.north_outlet.R;
import com.chug.north_outlet.adapter.ScanDeviceAdapter;
import com.chug.north_outlet.bean.ControlDevice;
import com.chug.north_outlet.bean.EFDevice;
import com.chug.north_outlet.bean.EFDeviceOutlet;
import com.chug.north_outlet.dao.DaoUtil;
import com.chug.north_outlet.esptouch.EsptouchTask;
import com.chug.north_outlet.esptouch.IEsptouchListener;
import com.chug.north_outlet.esptouch.IEsptouchResult;
import com.chug.north_outlet.utils.ByteUtils;
import com.chug.north_outlet.utils.Constant;
import com.chug.north_outlet.utils.DeviceManager;
import com.chug.north_outlet.utils.PreferenceHelper;
import com.chug.north_outlet.utils.UdpClient;
import com.chug.north_outlet.utils.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.listener.ScanDeviceListener;
import io.xlink.wifi.sdk.listener.SetDeviceAccessKeyListener;

import static com.chug.north_outlet.Config.WIFI_PRODUCTID_LEXIN;


public class ScanDeviceActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    /*@BindView(R.id.wifiName)
    TextView wifiName;
    @BindView(R.id.ed_wifiPassword)
    EditText edWifiPassword;
    @BindView(R.id.text_input_wifiPassword)
    TextInputLayout tiWifiPass;
    @BindView(R.id.configuration)
    Button configuration;
    @BindView(R.id.outlet_gridview_ctl)
    TextView outletGridviewCtl;
    @BindView(R.id.outlet_scan_gridview)
    DragGridView outletGridview;
    @BindView(R.id.tv_wifi_name)
    TextView tvWifiName;*/
    @BindView(R.id.lv_scanned_devices) ListView lvScannedDevices;

    private WifiManager mWifiManager;
    // 插座相关
    private List<EFDeviceOutlet> mOutletList,deviceOutlets;                                        // 插座列表数据源
   // private EFOutletAdapter mOutletAdapter;
    private ScanDeviceAdapter scanDeviceAdapter;
    private EFDeviceOutlet mOpOutlet;
    private ProgressDialog dialog;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.primary_dark));

        deviceOutlets = DaoUtil.getAllList(EFDeviceOutlet.class);
        if(getIntent().getExtras()!=null){

            mOutletList = (List<EFDeviceOutlet>) getIntent().getSerializableExtra("deviceList");


//            Log.e("ScanDeviceActivity","list === "+mOutletList.size());
        }else{
            mOutletList = new ArrayList<>();

            if(deviceOutlets!=null){

            for(int i=0;i<deviceOutlets.size();i++){
                if(!deviceOutlets.get(i).isDeviceAdded())
                    mOutletList.add(deviceOutlets.get(i));
            }
            }
        }
        initView();
        //initData();
        //initListener();
    }

   /* private void initListener() {
        outletGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOpOutlet = (EFDeviceOutlet) parent.getItemAtPosition(position);
                ControlDevice device = DeviceManager.getInstance().getDevice(mOpOutlet.getParentMac());
                *//*Intent intent = new Intent(ScanDeviceActivity.this, SingleOutletActivity.class);
                intent.putExtra("device", device);
                intent.putExtra("outlet", mOpOutlet);*//*
                Intent intent = new Intent(ScanDeviceActivity.this,AddScanDeviceActivity.class);
                intent.putExtra(Constant.EFDEVICEOUTLET,mOpOutlet);
                startActivity(intent);
            }
        });
        outletGridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView mac = (TextView) view.findViewById(R.id.number_outlet_tv);
                mac.setText(mOutletList.get(position).getDeviceMac());
                return false;
            }
        });
    }*/


    private void initView() {
        setContentView(R.layout.activity_scan_device);
        ButterKnife.bind(this);

        if (mOutletList != null && !mOutletList.isEmpty())
            scanDeviceAdapter = new ScanDeviceAdapter(mOutletList, this);
        lvScannedDevices.setAdapter(scanDeviceAdapter);
        lvScannedDevices.setOnItemClickListener(this);

        if(deviceOutlets!=null && !deviceOutlets.isEmpty()) {
            Log.e("data","deviceOutlets = "+deviceOutlets.size());

            if (mOutletList != null && !mOutletList.isEmpty())
                Toast.makeText(ScanDeviceActivity.this, "click on device to add it!", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(ScanDeviceActivity.this, "No new devices to add!", Toast.LENGTH_SHORT).show();
            }
        }else {
//            SweetAlertDialog sweetAlertDialog = Util.showAlertMessage(ScanDeviceActivity.this, "No new devices to add! You need to setup wifi first!");
            SweetAlertDialog sweetAlertDialog = Util.showAlertMessage(ScanDeviceActivity.this, getString(R.string.no_device_found));
            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    Intent intent = new Intent(ScanDeviceActivity.this, MenuActivity.class);
                    intent.putExtra("fragment", Constant.FROM_SCANDEVICE);
                    startActivity(intent);
                    finishAffinity();
                }
            });
        }

           // Toast.makeText(ScanDeviceActivity.this, "No new devices to add!", Toast.LENGTH_SHORT).show();
    }
    /*private void initData() {
        login();
        startXlinkAgent();
        mOutletAdapter = new EFOutletAdapter(this);
        loadScanData();
        mOutletAdapter.setList(mOutletList);
        outletGridview.setAdapter(mOutletAdapter);
        getWifiName();
    }
*/
    /**
     * get wifi name
     */
    private void getWifiName() {
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (!mWifiManager.isWifiEnabled()) {
            // XlinkUtils.shortTips(getString(R.string.connect_wifi));
            Toast.makeText(this, getString(R.string.connect_wifi), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isWifiConnected()) {
            // XlinkUtils.shortTips(getString(R.string.noWifiLan));
            Toast.makeText(this, getString(R.string.noWifiLan), Toast.LENGTH_SHORT).show();
            return;
        }
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        String ssidString = wifiInfo.getSSID();
        int version = getAndroidSDKVersion();
        if (version > 16 && ssidString.startsWith("\"") && ssidString.endsWith("\"")) {
            ssidString = ssidString.substring(1, ssidString.length() - 1);
        }
        final String[] spKey = new String[]{ssidString};
//        tvWifiName.setText(spKey[0]);
    }

    /**
     * start Xlink  service
     */
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
                    handler.sendEmptyMessageDelayed(2, 1000);
                    if (dialog != null) {
                        dialog = null;
                    }
                    count = 50;
                    break;
                case 2:
                    if (dialog == null) {

                        dialog = new ProgressDialog(ScanDeviceActivity.this);
                        dialog.setMax(100);
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setMessage("Please wait...");
                        dialog.show();
                    }
                    dialog.setProgress(100 - count * 2);
                    count--;
                    handler.sendEmptyMessage(3);
                    isConfWifiComplete = true;
                    handler.sendEmptyMessageDelayed(2, 900);
                    if (count <= 0) {
                        dialog.dismiss();
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
                    loadScanData();
                    handler.removeMessages(2);
                    handler.removeMessages(4);

                    mEsptouchTask.interrupt();
                    break;
            }
        }
    };
    private int count = 50;
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

    private void scanDevices() {
        handler.sendEmptyMessage(4);
    }

    private int getAndroidSDKVersion() {
        int version = 0;
        try {
            version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
        } catch (NumberFormatException e) {
        }
        return version;
    }

    private String isSsidHiddenStr = "NO";

    /**
     * 配网接口
     *
     * @param ssid wifi  Name
     * @param pwd  wifi  Password
     */
    /*private void esptouch(String ssid, String pwd) {
        String apSsid = ssid;
        String apPassword = pwd;
        String apBssid = getWifiConnectedBssid();
        Boolean isSsidHidden = false;
        String taskResultCountStr = Integer.toString(-1);
        if (isSsidHidden) {
            isSsidHiddenStr = "YES";
        }


        if (__IEsptouchTask.DEBUG) {
        }
        new Thread(new EsptouchAsyncTasks(apSsid, apBssid, apPassword,
                isSsidHiddenStr, taskResultCountStr)).start();
    }*/

    private EsptouchTask mEsptouchTask;

    /*@OnClick({R.id.configuration, R.id.ed_wifiPassword})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.configuration:
                if (!edWifiPassword.getText().toString().isEmpty()) {

                    tiWifiPass.setError(null);
                    handler.sendEmptyMessage(1);
                    Log.e("ssid","ssid = "+tvWifiName.getText().toString().substring(10, tvWifiName.length()));
                    esptouch(tvWifiName.getText().toString().substring(10, tvWifiName.length()), edWifiPassword.getText().toString());
                }else {
                tiWifiPass.setError("Wifi password should not be blank!!");
                }

                break;

            case R.id.wifiPassword:

                break;
        }
    }
*/
    /**
     * 登录账号为18129679136,密码为66666666
     */

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        EFDeviceOutlet outletDevice = mOutletList.get(i);
        Intent intent = new Intent(ScanDeviceActivity.this,AddScanDeviceActivity.class);
        intent.putExtra(Constant.EFDEVICEOUTLET,outletDevice);
        startActivity(intent);

    }


  /*  private class EsptouchAsyncTasks
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
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword,
                        isSsidHidden, ScanDeviceActivity.this);
                mEsptouchTask.setEsptouchListener(myListener);
            }
            final List<IEsptouchResult> resultList = mEsptouchTask.executeForResults(taskResultCount);
            runOnUiThread(new Runnable() {
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
    }*/

    private IEsptouchListener myListener = new IEsptouchListener() {

        @Override
        public void onEsptouchResultAdded(final IEsptouchResult result) {
            onEsptoucResultAddedPerform(result);
        }
    };


    private void onEsptoucResultAddedPerform(final IEsptouchResult result) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String text = result.getBssid() + " is connected to the wifi";
               /* Toast.makeText(getActivity(), text,
                               Toast.LENGTH_LONG).show();*/
            }
        });
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
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWiFiNetworkInfo;
    }

    private WifiInfo getConnectionInfo() {
        WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        return wifiInfo;
    }

    /**
     * scanWifi
     */
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

    /**
     * Send the binding instruction
     */
    private void senBindData() {
        UdpClient.getInstance().connectSocket(0);
        byte[] outletData = ByteUtils.append(Config.FORTY_LENGTH, Prefix.SCAN_WIFI_DEVICE,
                ByteUtils.getPhonenumberBytes());
        //Log.d(TAG, "outletData:" + outletData);
        UdpClient.getInstance().sendData(1, "255.255.255.255", outletData, null);
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

    private void loadScanData() {
    //    loadOutletData(0);
    }

    /*private void loadOutletData(int type) {

        WhereBuilder builder = WhereBuilder.b();
        builder.and("sceneId", "=", EFScene.DEFAULT_SCENEID);
        builder.and("Name", "==", null);
        mOutletList = DaoUtil.getAllList(EFDeviceOutlet.class, builder);
        if (mOutletList != null) {

            Log.d("ScanDeviceActivity", "mOutletList.size():" + mOutletList.size());
        }
        mOutletAdapter.setList(mOutletList);
        mOutletAdapter.notifyDataSetChanged();
    }*/

}
