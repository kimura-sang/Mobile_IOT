package com.chug.north_outlet;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;


import com.chug.north_outlet.bean.ControlDevice;
import com.chug.north_outlet.utils.ByteUtils;
import com.chug.north_outlet.utils.XlinkUtils;
import com.chug.north_outlet.utils.DeviceManager;
import com.chug.north_outlet.utils.PreferenceHelper;

import org.xutils.x;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.bean.DataPoint;
import io.xlink.wifi.sdk.bean.EventNotify;
import io.xlink.wifi.sdk.listener.XlinkNetListener;


/**
 * @author yinhui
 * @ClassName: APP
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2015-12-4 下午5:13:34
 */
public class App extends Application implements XlinkNetListener {

    public static final String MAIN_SERVICE_START = Constants.PACKAGE_NAME + "service.MAINSERVICE";
    public static final int NOTIFICATION_DOWN_ID = 0x53256562;
    public static App app;
    public static boolean isActive;
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private RemoteViews cur_down_view;
    private static final String TAG = "APP";

    private static App instance;

    private String accessToken;

    // 全局登录的 appId 和auth
    private int appid;
    private String authKey;

    public String versionName;
    public int versionCode;
    // public String packageName;

    // 判断程序是否正常启动
    public boolean auth;
    public boolean isOnline;

    // 当前activity
    private Stack<Activity> activties;

    // 当前连接的设备
    private ControlDevice mCurrentDevice = null;

    // 数据返回凭据
    private int requestCode = -1;


    private Activity mCurActivity;
    public static Map<String, Long> map;

    public void setCurAct(Activity act) {
        mCurActivity = act;
    }

    static Context ctx;
    public static HashMap<String,Integer> hashMap =new HashMap();
    public static Context getCtx() {
        return ctx;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        Log.e("app","oncreate of application is called ");
        app = this;
        isActive = true;
        App.instance = this;
        app = App.instance;

        // init xutils
        x.Ext.init(app);
        org.xutils.x.Ext.init(this);
        // init xlink
        XlinkAgent.init(this);
        XlinkAgent.debug(Config.DEBUG);
        XlinkAgent.getInstance().addXlinkListener(this);

        appid = PreferenceHelper.readInt("appId");
        authKey = PreferenceHelper.readString("authKey", "");

        for (ControlDevice device : DeviceManager.getInstance().getDevices()) {
            XlinkAgent.getInstance().initDevice(device.getXDevice());
        }

        try {
            PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = pinfo.versionCode;
            versionName = pinfo.versionName;
            // packageName = pinfo.packageName;

        } catch (NameNotFoundException e) {
        }

        auth = false;
    }

    public static App getApp() {
        return app;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Stack<Activity> getActivities() {
        if (activties == null) {
            activties = new Stack<Activity>();
        }
        return activties;
    }

    /**
     * @return the main context of the Application
     */
    public static Context getAppContext() {
        return instance;
    }



    /**
     * @return the Application
     */
    public static App getInstance() {
        return instance;
    }

    /**
     * @return the main resources from the Application
     */
    public static Resources getAppResources() {
        return instance.getResources();
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public Activity getCurrentActivity() {
        Stack<Activity> stack = getActivities();
        if(!stack.isEmpty())
            return stack.peek();
        return null;
    }

    public String getCurrentDeviceMac() {
        String mac = "";
        if (mCurrentDevice != null) {
            mac = mCurrentDevice.getMacAddress();
        }
        return mac;
    }

    public ControlDevice getCurrentDevice() {
        return mCurrentDevice;
    }

    public void setCurrentDevice(ControlDevice device) {
        this.mCurrentDevice = device;
    }


// Device status change: dropped / reconnected / online
    @Override
    public void onDeviceStateChanged(XDevice xdevice, int state) {
        Log.e("app","ondevice state changed called ");
        ControlDevice device = DeviceManager.getInstance().getDevice(xdevice.getMacAddress());
        if (device != null) {
            if (state == XlinkCode.DEVICE_CHANGED_CONNECTING) {
            } else if (state == XlinkCode.DEVICE_CHANGED_CONNECT_SUCCEED) {
                isOnline = true;
                mCurrentDevice = device;
            } else if (state == XlinkCode.DEVICE_CHANGED_OFFLINE) {
                isOnline = false;
                if (appid != 0 && !TextUtils.isEmpty(authKey)) {
                    XlinkAgent.getInstance().login(appid, authKey);
                }
            }
            device.setxDevice(xdevice);
            DeviceManager.getInstance().updateDevice(device);
            Intent intent = new Intent(Config.BROADCAST_DEVICE_CHANGED);
            intent.putExtra(Config.DEVICE_MAC, device.getMacAddress());
            intent.putExtra(Config.STATUS, state);
            sendBroadcast(intent);
        }
    }

    @Override
    public void onDataPointUpdate(XDevice xDevice, List<DataPoint> dataPionts, int i) {
        Log.d(TAG, "dataPionts:" + dataPionts);
        Log.e("app","on Data Point Update called ");
        ControlDevice device = DeviceManager.getInstance().getDevice(xDevice.getMacAddress());
        if (device != null) {
            Intent intent = new Intent(Config.BROADCAST_DATAPOINT_RECV);
            intent.putExtra(Config.DEVICE_MAC, device.getMacAddress());
            if (dataPionts != null) {
                intent.putExtra(Config.DATA, (Serializable) dataPionts);
            }
            App.this.sendBroadcast(intent);
        }
    }

    public void connectDeviceAgan() {
        if (appid != 0 && !TextUtils.isEmpty(authKey)) {
            XlinkAgent.getInstance().login(appid, authKey);
        }
    }
    @Override
    public void onDisconnect(int code) {
        Log.e("app","on disconnect called ");
        if (code == XlinkCode.CLOUD_SERVICE_KILL) {
            // 这里是服务被异常终结了（第三方清理软件，或者进入应用管理被强制停止服务）
            if (appid != 0 && !TextUtils.isEmpty(authKey)) {
                XlinkAgent.getInstance().login(appid, authKey);
            }
        } else if (code == XlinkCode.CLOUD_USER_EXTRUSION) {
            // XlinkUtils.shortTips(getString(R.string.cloud_user_extrusion_tip));
        }
    }
    /**
     *  With the cloud-easy interface, received the device to send Pip data

     */
    @Override
    public void onRecvPipeData(short i, XDevice xDevice, byte[] bytes) {
        Log.e("app","on RecvPipe data called ");
        ControlDevice device = DeviceManager.getInstance().getDevice(xDevice.getMacAddress());
        if (device != null) {
            // Send the broadcast, the activity needs the data to listen to the broadcast, and obtain the data, and then respond to the processing
            sendPipeBroad(Config.BROADCAST_RECVPIPE, device, bytes);
            if (BuildConfig.DEBUG) Log.e(TAG, ByteUtils.bytesToHexString(bytes));
        }
    }
    /**
     * Receives the pipe data pushed by the device through the cloud server
     */
    @Override
    public void onRecvPipeSyncData(short i, XDevice xDevice, byte[] bytes) {
        Log.e("app","on RecvPipeSyncData called ");
        ControlDevice device = DeviceManager.getInstance().getDevice(xDevice.getMacAddress());
        Log.d(TAG, "device:" + device+"=========="+bytes);
        if (device != null) {
            // The received device sends the broadcast through the pipe data pushed by the cloud server, which requires the data to listen to the broadcast, obtain the data, and then process the response
            sendPipeBroad(Config.BROADCAST_RECVPIPE, device, bytes);
            Log.d(TAG, "onRecvPipeSyncData: " +ByteUtils.bytesToHexString(bytes));
        }
    }

    @Override
    public void onLocalDisconnect(int code) {
        Log.e("app","on local Disconnect called ");
        if (code == XlinkCode.LOCAL_SERVICE_KILL) {
            // Here is the xlink service being ended abnormally (third party cleanup software, or entering application management is forced to stop application / service
            // Never end the service
            // Unless called XlinkAgent.getInstance().stop（）;
            XlinkAgent.getInstance().start();
        }
    }


    @Override
    public void onLogin(int code) {
        Log.e("app","on login called !");
        sendBroad(Config.BROADCAST_ON_LOGIN, code);
        if (code == XlinkCode.SUCCEED) {
           // XlinkUtils.shortTips(getString(R.string.cloud_network_available));
        } else if (code == XlinkCode.CLOUD_CONNECT_NO_NETWORK || !XlinkUtils.isConnected()) {
            XlinkUtils.shortTips(getString(R.string.network_not_available_please_check_network_connection));
        }
    }



    @Override
    public void onStart(int code) {
        Log.e("app","on start called ");
        sendBroad(Config.BROADCAST_ON_START, code);
    }

    /**
     * @param ：start/login   Broadcast
     */
    public void sendBroad(String action, int code) {
        Intent intent = new Intent(action);
        intent.putExtra(Config.STATUS, code);
        sendBroadcast(intent);
    }

    /**
     * @param
     */
    public void sendPipeBroad(String action, ControlDevice device, byte[] data) {
        Intent intent = new Intent(action);
        intent.putExtra(Config.DEVICE_MAC, device.getMacAddress());
        intent.putExtra(Config.REQUEST_CODE_KEY, requestCode);
        if (data != null) {
            intent.putExtra(Config.DATA, data);
        } else {
        }

        sendBroadcast(intent);
    }

    public int getAppid() {
        return appid;
    }

    public void setAppid(int appid) {
        this.appid = appid;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    // xlink notifiaction
    @Override
    public void onEventNotify(EventNotify eventNotify) {
    }

    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return mNotificationManager;
    }
}
