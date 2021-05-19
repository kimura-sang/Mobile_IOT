package com.chug.north_outlet.http;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

import com.chug.north_outlet.App;
import com.chug.north_outlet.R;
import com.chug.north_outlet.activity.ScanDeviceActivity;
import com.chug.north_outlet.bean.ControlDevice;
import com.chug.north_outlet.callback.IDeviceConnectCallback;
import com.chug.north_outlet.callback.ISendCallback;
import com.chug.north_outlet.utils.DeviceManager;
import com.chug.north_outlet.utils.XlinkUtils;

import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.listener.SendPipeListener;

public class XlinkClient implements Callback, IDeviceConnectCallback {
    private static final String TAG = "XlinkClient";

    private static final int SEND_PRE = 2221;
    private static final int SEND_START = 2222;
    private static final int SEND_LINKAGE = 9999999;
    private static final int SEND_END = 2223;
    private static final int SEND_CANCEL = 2224;
    private static final int RESULT_MSG = 2225;
    private static final int UPDATE_DEVICE_STATUS = 2226;
    private static final int SEND_TWINK = 2227;
    private static final int CONNECT_DEV = 2228;
    private static final int SEND_REPEAT = 2229;

    private static XlinkClient instance = null;

    private byte[] data;



    public ISendCallback mSendCallback;// 发送数据回调

    private Handler mHandler;

    private ControlDevice mDevice;
    private int requestCode;

    private Worker mWorker;
    private boolean mShowTip = false;

    private int repeatCount = 0;// 重发次数计数
    private int repeat_time = 1;// 默认重发次数

    private XlinkClient() {
        mHandler = new Handler(this);
    }

    public static XlinkClient getInstance() {
        synchronized (ScanDeviceActivity.class){
            if (instance == null) {
                synchronized (ScanDeviceActivity.class) {
                    instance = new XlinkClient();
                }
            }

        }
        return instance;
    }

    /**
     * 发送数据
     *
     * @param bs
     * @return
     */
    synchronized public void sendPipe(ControlDevice device, byte[] bs, int requestCode, ISendCallback sCallBack) {
        data = bs;
        mShowTip = true;
        this.mSendCallback = sCallBack;
        mDevice = device;
        this.requestCode = requestCode;
        mHandler.sendEmptyMessage(SEND_PRE);
     //  Log.d(TAG, "mDevice:" + mDevice);
         if (mDevice == null) {
            mHandler.sendEmptyMessage(SEND_CANCEL);
            mHandler.obtainMessage(RESULT_MSG, App.getAppResources().getString(R.string.please_select_device)).sendToTarget();
            return;
        }

        /*if (mDevice.getXDevice().getDevcieConnectStates() == -1 ||mDevice.getXDevice().getDevcieConnectStates() == 3 ) {
            DeviceManager.getInstance().connectDevice(App.getAppContext(), device, this);
            return;
        }*/
        mHandler.sendEmptyMessage(SEND_START);
    }


    class Worker implements Runnable {

        @Override
        public void run() {
            send(mShowTip);
        }

    }

    private void send(final boolean showTip) {
        int resultID = XlinkAgent.getInstance().sendPipeData(mDevice.getXDevice(), data, new SendPipeListener() {
            @Override
            public void onSendLocalPipeData(XDevice device, int code, int messageId) {
                // setDeviceStatus(false);
                boolean isSuccess = false;
                switch (code) {
                    case XlinkCode.SUCCEED:
                        isSuccess = true;
                        if (showTip) {
                            /**
                             * 屏蔽发送成功
                             */
                            //						mHandler.obtainMessage(RESULT_MSG, App.getAppResources().getString(R.string.send_success)).sendToTarget();
                        }
                        mHandler.obtainMessage(UPDATE_DEVICE_STATUS, true).sendToTarget();

                        Log.e(TAG,  "发送数据" + XlinkUtils.getHexBinString(data) + "成功");
                        break;
                    case XlinkCode.SERVER_CODE_UNAUTHORIZED:
                        isSuccess = false;
                        //mHandler.obtainMessage(RESULT_MSG, App.getAppResources().getString(R.string.please_subscribe_again)).sendToTarget();
                        mHandler.sendEmptyMessage(CONNECT_DEV);
                        break;
                    case XlinkCode.SERVER_DEVICE_OFFLIEN:// 如果设备不在线就发出警告
                        isSuccess = false;
                        mHandler.obtainMessage(RESULT_MSG, App.getAppResources().getString(R.string.equipment_not_connected)).sendToTarget();
                        mHandler.obtainMessage(UPDATE_DEVICE_STATUS, false).sendToTarget();
                        break;
                    default:
                        isSuccess = false;
                        //   mHandler.obtainMessage(RESULT_MSG, App.getAppResources().getString(R.string.control_equipment_other_error_code, code)).sendToTarget();
                        mHandler.sendEmptyMessage(CONNECT_DEV);
                        mHandler.obtainMessage(UPDATE_DEVICE_STATUS, false).sendToTarget();
                        break;
                }
                repeatCount = 0;
                mHandler.obtainMessage(SEND_END, isSuccess).sendToTarget();
            }

        });


    }


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case SEND_PRE:
                if (mSendCallback != null) {
                    mSendCallback.onSendPre();
                }
                break;
            case SEND_START:
                // 设置接收数据requestCode
                App.getInstance().setRequestCode(requestCode);
                mWorker = new Worker();
                ThreadManger.execute(mWorker);
                if (mSendCallback != null) {
                    mSendCallback.onSendStart();
                }
                break;
            case SEND_CANCEL:
                if (mSendCallback != null) {
                    mSendCallback.onSendCancel();
                }
                break;
            case SEND_END:
                if (mSendCallback != null) {
                    mSendCallback.onSendEnd((Boolean) msg.obj);
                }
                break;
            case UPDATE_DEVICE_STATUS:
                DeviceManager.setDeviceStatus((Boolean) msg.obj);
                break;
            case RESULT_MSG:
                XlinkUtils.shortTips((String) msg.obj);
                break;
            case CONNECT_DEV:
                DeviceManager.getInstance().connectDevice(App.getInstance().getCurrentActivity(), mDevice, this);
                break;
            case SEND_REPEAT:
                mWorker = new Worker();
                ThreadManger.execute(mWorker);
                break;

            default:
                break;
        }
        return false;
    }

    @Override
    public void connectCallback(boolean isSuccess, ControlDevice device) {
        if (isSuccess) {
            mHandler.sendEmptyMessage(SEND_START);
        } else {
            mHandler.sendEmptyMessage(SEND_CANCEL);
        }
    }
}
