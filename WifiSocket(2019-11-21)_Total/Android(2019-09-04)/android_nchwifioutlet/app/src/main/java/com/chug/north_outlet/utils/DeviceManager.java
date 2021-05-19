package com.chug.north_outlet.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chug.north_outlet.App;
import com.chug.north_outlet.Config;
import com.chug.north_outlet.R;
import com.chug.north_outlet.bean.ControlDevice;
import com.chug.north_outlet.callback.IDeviceConnectCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.listener.ConnectDeviceListener;

public class DeviceManager {

   /* private              Handler                                  mHandler  =new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                        //Log.d(TAG, "执行了吗,对话框消失了吗===========");
                    }
                    break;
            }
        }
    };*/
    private static final String                                   TAG       = "DeviceManage";
    /**
     * 存放自己定义的设备
     */
    public static        ConcurrentHashMap<String, ControlDevice> deviceMap = new ConcurrentHashMap<String, ControlDevice>();
    private static DeviceManager instance;

    public static DeviceManager getInstance() {
        if (instance == null) {
            instance = new DeviceManager();
        }
        return instance;
    }

    private DeviceManager() {
    }


    private static String PASSWORD = "password";

    // 通过静态语句快，优先初始化，避免因为线程安全，重复调用.s
    static {
        Map<String, String> stringmap = XTGlobals.getAllProperty();
        // if (stringmap == null || stringmap.size() == 0
        // ) {
        // stringmap = new HashMap<String, String>();
        // stringmap
        // .put("ACCF2356AF72",
        // "{\"port\":5987,\"dname\":\"R.drawable.ic_launcher\",\"init\":true,\"did\":0,\"pid\":\"acc4ee5e4a3f4735a3242fecc59b1d87\",\"mac\":\"ACCF2356AF72\",\"msv\":1,\"mhv\":1,\"version\":1,\"ip\":\"192.168.36.141\"}");
        // }

        Iterator<Entry<String, String>> iter = stringmap.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, String> entry = iter.next();
            JSONObject obj;
            try {
                obj = new JSONObject(entry.getValue());
                XDevice xdev = XlinkAgent.JsonToDevice(obj);
                if (xdev != null) {
                    ControlDevice device = new ControlDevice(xdev);
                    if (!obj.isNull(PASSWORD)) {
                        device.setPassword(obj.getString(PASSWORD));
                    }
                    int type = PreferenceHelper.readInt(device.getMacAddress(), -1);
                    device.setType(type);
                    deviceMap.put(xdev.getMacAddress(), device);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public static final ArrayList<ControlDevice> listDev = new ArrayList<ControlDevice>();

    public synchronized ArrayList<ControlDevice> getDevices() {
        listDev.clear();
        Iterator<Entry<String, ControlDevice>> iter = deviceMap.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, ControlDevice> entry = iter.next();
            ControlDevice device = entry.getValue();
            if (device.getType() != -1) {
                listDev.add(entry.getValue());
            }
        }
        return listDev;
    }

    /**
     * 修改设备密码
     *
     * @param mac
     * @param password
     */
    public void setAuth(String mac, String password) {
        ControlDevice device = deviceMap.get(mac);
        if (device != null) {
            device.setPassword(password);
            updateDevice(device);
        }

    }

    public ControlDevice getDevice(XDevice xd) {
        return deviceMap.get(xd.getMacAddress());
    }

    public ControlDevice getDevice(int deviceId) {
        ControlDevice dev = null;
        for (ControlDevice device : getDevices()) {
            if (device.getXDevice().getDeviceId() == deviceId) {
                dev = device;
                break;
            }
        }
        return dev;
    }

    /**
     * 保存设备到配置文件
     */
    public void saveDevice(ControlDevice device) {
        JSONObject jo = XlinkAgent.deviceToJson(device.getXDevice());
        if (jo == null) {
            return;
        }
        if (device.getPassword() != null) {
            try {
                jo.put(PASSWORD, device.getPassword());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        XTGlobals.setProperty(device.getMacAddress(), jo.toString());
    }

    public synchronized void clearAllDevice() {
        for (ControlDevice device : getDevices()) {
            XTGlobals.deleteProperty(device.getMacAddress());
        }
        deviceMap.clear();
    }

    public void addDevice(ControlDevice dev) {
        deviceMap.put(dev.getMacAddress(), dev);
        saveDevice(dev);
    }

    public void addDevice(XDevice dev) {
        ControlDevice device = deviceMap.get(dev.getMacAddress());
        if (device != null) { // 如果已经保存过设备，就不add
            device.setxDevice(dev);
            deviceMap.put(dev.getMacAddress(), device);
            saveDevice(device);
            return;
        }
        device = new ControlDevice(dev);
        deviceMap.put(dev.getMacAddress(), device);
        saveDevice(device);
    }

    public void addDevice(XDevice dev, int type) {
        ControlDevice device = deviceMap.get(dev.getMacAddress());
        PreferenceHelper.write(dev.getMacAddress(), type);
        if (device != null) { // 如果已经保存过设备，就不add
            device.setxDevice(dev);
            device.setType(type);
            deviceMap.put(dev.getMacAddress(), device);
            saveDevice(device);
            return;
        }
        device = new ControlDevice(dev);
        device.setType(type);
        deviceMap.put(dev.getMacAddress(), device);
        saveDevice(device);
    }

    public void updateDevice(ControlDevice device) {
        deviceMap.remove(device.getMacAddress());
        deviceMap.put(device.getMacAddress(), device);
        saveDevice(device);
    }

    public void updateNoSaveDevice(ControlDevice dev) {
        deviceMap.remove(dev.getMacAddress());
        deviceMap.put(dev.getMacAddress(), dev);
    }

    public void updateDevice(XDevice xdevice) {
        ControlDevice device = deviceMap.get(xdevice.getMacAddress());
        if (device == null) {
            return;
        }
        device.setxDevice(xdevice);
        updateDevice(device);
    }

    public void removeDevice(XDevice dev) {
        removeDevice(dev.getMacAddress());
    }

    public void removeDevice(String mac) {
        deviceMap.remove(mac);
        PreferenceHelper.remove(mac);
        XlinkAgent.getInstance().removeDevice(mac);
        XTGlobals.deleteProperty(mac);
    }

    public ControlDevice getDevice(String mac) {
        if (mac == null) {
            return null;
        }
        return deviceMap.get(mac);
    }


    /**
     * @Title: connectDevice
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param act
     * @param @param device
     * @param @param callBack    设定文件
     * @return void    返回类型
     * @throws
     */
    private ProgressDialog mProgressDialog;
    private ControlDevice mDevice;
    private Activity lastActivity;

    synchronized public void connectDevice(Context act, ControlDevice device, final IDeviceConnectCallback callBack) {
        mDevice = device;
        if (mDevice == null) {
            XlinkUtils.shortTips(act.getString(R.string.device_was_not_found));
            return;
        }

        int ret = XlinkAgent.getInstance().connectDevice(mDevice.getXDevice(), mDevice.getXDevice().getAccessKey(),/*deviceInfo==null*/mDevice.getType()==8?mDevice.getXDevice().getSubKey():Integer.parseInt(Config.passwrod), new ConnectDeviceListener() {
            /**
             * 连接设备回调。该回调在主程序，可直接更改ui
             */
            @Override
            public void onConnectDevice(XDevice xDevice, int result) {
               // dismissConnectDialog();
                boolean isOnline = false;
                Log.d(TAG, "控制时的.getAccessKey():" + mDevice.getXDevice().getAccessKey()+mDevice.getXDevice().getSubKey());
                Log.d(TAG, "result:" + result);
                switch (result) {
                    // 连接设备成功 设备处于内网

                    case XlinkCode.DEVICE_STATE_LOCAL_LINK:
                        isOnline = true;
                        DeviceManager.getInstance().updateDevice(xDevice);
                        // 连接设备成功，成功后
                        XlinkAgent.getInstance().sendProbe(xDevice);
                        // 保存当前连接设备
                        mDevice.setxDevice(xDevice);
                        App.getInstance().setCurrentDevice(mDevice);
                        updateRecentDevice(mDevice);
                       // XlinkUtils.shortTips(App.getAppContext().getString(R.string.is_lan_control_equipment) + "(" + xDevice.getMacAddress() + ")");
                        break;
                    // 连接设备成功 设备处于云端
                    case XlinkCode.DEVICE_STATE_OUTER_LINK:
                        isOnline = true;
                        DeviceManager.getInstance().updateDevice(xDevice);
                        // 保存当前连接设备
                        mDevice.setxDevice(xDevice);
                        App.getInstance().setCurrentDevice(mDevice);
                        updateRecentDevice(mDevice);
                      //  XlinkUtils.shortTips(App.getAppContext().getString(R.string.is_through_the_cloud_control_equipment) + "(" + xDevice.getMacAddress() + ")");
                        break;
                    // 设备授权码错误
                    case XlinkCode.CONNECT_DEVICE_INVALID_KEY:
                        isOnline = false;
                        XlinkUtils.shortTips(App.getAppContext().getString(R.string.device_authentication_failed));
                        break;
                    // 设备不在线
                    case XlinkCode.CONNECT_DEVICE_OFFLINE:
                        isOnline = false;
                        // LogUtil.e(TAG, "Device:" + device.getMacAddress() + "设备不在线");
                        XlinkUtils.shortTips(App.getAppContext().getString(R.string.equipment_not_connected));
                        break;
                    case XlinkCode.CONNECT_DEVICE_NO_ACTIVATE:
                        isOnline = false;
                        // LogUtil.e(TAG, "Device:" + device.getMacAddress() + "设备不在线");
                        XlinkUtils.shortTips(App.getAppContext().getString(R.string.equipment_not_connected));
                        break;
                    // 连接设备超时了，（设备未应答，或者服务器未应答）
                    case XlinkCode.CONNECT_DEVICE_TIMEOUT:
                        isOnline = false;
                        // //LogUtil.e(TAG, "Device:" + xDevice.getMacAddress() + "连接设备超时");
                      //  XlinkUtils.shortTips(App.getAppContext().getString(R.string.connection_timeout));
                        break;
                    case XlinkCode.CONNECT_DEVICE_SERVER_ERROR:
                        isOnline = false;
                        XlinkUtils.shortTips(App.getAppContext().getString(R.string.connection_equipment_failure_server_internal_error));
                        break;
                    case XlinkCode.CONNECT_DEVICE_OFFLINE_NO_LOGIN:
                        isOnline = false;
                        XlinkUtils.shortTips(App.getAppContext().getString(R.string.current_mobile_phone_only_lan_environment));
                        break;
                    case XlinkCode.INVALID_DEVICE_ID:
                        isOnline = false;
                        XlinkUtils.shortTips(App.getAppContext().getString(R.string.invalid_id_please_activation));
                        break;
                    case XlinkCode.NO_CONNECT_SERVER:
                        isOnline = false;
                        String phone = PreferenceHelper.readString(Config.USER_PHONE);
                        if (phone != null && XlinkUtils.isConnected()) {
                            int appid = PreferenceHelper.readLoginInt(Config.APP_ID + phone);
                            String authKey = PreferenceHelper.readLoginString(Config.AUTH_KEY + phone);
                            XlinkAgent.getInstance().start();
                            XlinkAgent.getInstance().login(appid, authKey);
                        }
                        XlinkUtils.shortTips(App.getAppContext().getString(R.string.connection_failure_phone_not_connect_server));
                        break;
                    case XlinkCode.NETWORD_UNAVAILABLE:
                        isOnline = false;
                        XlinkUtils.shortTips(App.getAppContext().getString(R.string.network_not_available_to_connected_devices));
                        break;
                    case XlinkCode.NO_DEVICE:
                        isOnline = false;
                        XlinkAgent.getInstance().initDevice(mDevice.getXDevice());
                        XlinkUtils.shortTips(App.getAppContext().getString(R.string.device_was_not_found));
                        break;
                    default:
                        isOnline = false;
                       // XlinkUtils.shortTips(App.getAppContext().getString(R.string.connection_failure_other_error_code) + result);
                        break;
                }
                setDeviceStatus(isOnline);
                if (callBack != null) {
                    callBack.connectCallback(isOnline, mDevice);
                }
            }
        });
        if (ret < 0) {
           // dismissConnectDialog();
            setDeviceStatus(false);
            if (callBack != null) {
                callBack.connectCallback(false, mDevice);
            }
        }
    }



    private void updateRecentDevice(ControlDevice dev) {
        int type = dev.getType();
        String mac = dev.getMacAddress();
        PreferenceHelper.write(Config.DEVICE_MAC, mac);
        if (type == ControlDevice.NORMAL_DEVICE) {
            PreferenceHelper.write(Config.RECENT_NORMAL_DEVICE, mac);
        } else if (type == ControlDevice.TELECONTROLLER_DEVICE) {
            PreferenceHelper.write(Config.RECENT_TELE_DEVICE, mac);
        } else if (type == ControlDevice.WIFI_BRIDGE) {
            PreferenceHelper.write(Config.RECENT_NORMAL_DEVICE, mac);
        } else if (type == ControlDevice.COMPOSITE_DEVICE) {
            PreferenceHelper.write(Config.RECENT_NORMAL_DEVICE, mac);
            PreferenceHelper.write(Config.RECENT_TELE_DEVICE, mac);
        }
    }

    /**
     * 设置设备状态
     *
     * @param status
     */
    public static void setDeviceStatus(boolean status) {
        App.getInstance().isOnline = status;
    }


    /**
     * 根据日期转化成一个byte
     *
     * @param _week
     * @return
     */
    public static byte weekToByte(ArrayList<Integer> _week) {
        byte b = 0;
        if (_week == null || _week.size() == 0) {
            return b;
        }
        for (Integer i : _week) {
            if (i > 6) { // 最多只有0-6
                continue;
            }
            b = XlinkUtils.setByteBit(i, b);
        }
        if (b == (byte) 0x00) {
            return b;
        }
        b = XlinkUtils.setByteBit(7, b);
        return b;
    }

    /**
     * 通知UI，定时器已经改变了
     */
    public void notificationTimer(ControlDevice device) {
        Intent intent = new Intent(Config.BROADCAST_TIMER_UPDATE);
        intent.putExtra(Config.DEVICE_MAC, device.getMacAddress());
        App.getInstance().sendBroadcast(intent);
    }

    /**
     * 通知UI，插座状态已经改变了
     */
    public void notificationSocket(ControlDevice device, int status) {
        Intent intent = new Intent(Config.BROADCAST_SOCKET_STATUS);
        intent.putExtra(Config.DEVICE_MAC, device.getMacAddress());
        intent.putExtra(Config.STATUS, status);
        App.getInstance().sendBroadcast(intent);
    }

    public String getWeekList(ArrayList<Integer> intlist) {
        StringBuffer sb = new StringBuffer();
        for (Integer i : intlist) {
            sb.append(i + " ");
        }
        return sb.toString();
    }

    public static boolean checkDevice(ControlDevice device) {
        boolean isZigDevice = true;
        if (device == null) {
            XlinkUtils.shortTips(App.getInstance().getString(R.string.please_select_device));
            isZigDevice = false;
        } else if (device.getType() != ControlDevice.NORMAL_DEVICE) {
            XlinkUtils.shortTips(App.getInstance().getString(R.string.zig_device_tip));
            isZigDevice = false;
        }
        return isZigDevice;
    }

    public static boolean checkTeleDevice(ControlDevice device) {
        boolean isTeleDevice = true;
        if (device == null) {
            XlinkUtils.shortTips(App.getInstance().getString(R.string.please_select_device));
            isTeleDevice = false;
        } else if (device.getType() != ControlDevice.TELECONTROLLER_DEVICE) {
            XlinkUtils.shortTips(App.getInstance().getString(R.string.tele_device_tip));
            isTeleDevice = false;
        }
        return isTeleDevice;
    }

    public ControlDevice getRecentNorDev() {
        String deviceMac = PreferenceHelper.readString(Config.RECENT_NORMAL_DEVICE);
        ControlDevice dev = getDevice(deviceMac);
        if (dev == null) {
            List<ControlDevice> list = getDevices();
            if (list == null) {
                return null;
            }
            for (int i = 0; i < list.size(); i++) {
                ControlDevice d = list.get(i);
                int type = d.getType();
                if (d.getType() == ControlDevice.NORMAL_DEVICE || type == ControlDevice.COMPOSITE_DEVICE || type == ControlDevice.WIFI_BRIDGE) {
                    dev = d;
                    break;
                }
            }
        }
        return dev;
    }

    public ControlDevice getRecentTelDev() {
        ControlDevice currentDevice = App.getInstance().getCurrentDevice();
        if (currentDevice != null && currentDevice.getType() == ControlDevice.WIFI_DEVICE_STB) {
            return currentDevice;
        }
        String deviceMac = PreferenceHelper.readString(Config.RECENT_TELE_DEVICE);
        ControlDevice dev = getDevice(deviceMac);
        if (dev == null) {
            List<ControlDevice> list = getDevices();
            if (list == null) {
                return null;
            }
            for (int i = 0; i < list.size(); i++) {
                ControlDevice d = list.get(i);
                int type = d.getType();
                if (type == ControlDevice.TELECONTROLLER_DEVICE || type == ControlDevice.COMPOSITE_DEVICE) {
                    dev = d;
                    break;
                }
            }
        }
        return dev;
    }

    public static String getMac(ControlDevice device) {
        if (device == null) {
            return "";
        } else {
            return device.getMacAddress();
        }
    }

    /*public void dismissConnectDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }*/
}
