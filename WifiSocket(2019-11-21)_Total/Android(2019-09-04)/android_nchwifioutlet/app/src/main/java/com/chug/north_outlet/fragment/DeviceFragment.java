package com.chug.north_outlet.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.test.espresso.core.deps.guava.base.Joiner;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.chug.north_outlet.Config;
import com.chug.north_outlet.Prefix;
import com.chug.north_outlet.R;
import com.chug.north_outlet.RequestCode;
import com.chug.north_outlet.activity.MenuActivity;
import com.chug.north_outlet.activity.ScanDeviceActivity;
import com.chug.north_outlet.adapter.DeviceEFOutletAdapter;
import com.chug.north_outlet.bean.ControlDevice;
import com.chug.north_outlet.bean.DbGroupInfo;
import com.chug.north_outlet.bean.EFDevice;
import com.chug.north_outlet.bean.EFDeviceOutlet;
import com.chug.north_outlet.callback.ListPositionCallback;
import com.chug.north_outlet.callback.SendCallback;
import com.chug.north_outlet.dao.DaoUtil;
import com.chug.north_outlet.http.XlinkClient;
import com.chug.north_outlet.utils.ByteUtils;
import com.chug.north_outlet.utils.Constant;
import com.chug.north_outlet.utils.DeviceManager;
import com.chug.north_outlet.utils.Util;
import com.chug.north_outlet.utils.XlinkUtils;
import com.google.gson.Gson;
import com.leo.simplearcloader.SimpleArcDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.listener.SetDeviceAccessKeyListener;

import static com.chug.north_outlet.Config.BL_FIRST_START;
import static com.chug.north_outlet.utils.ByteUtils.arrayCopyBytes;
import static com.chug.north_outlet.utils.ByteUtils.bytesToHexString;

public class DeviceFragment extends Fragment {

    private static final String TAG = DeviceFragment.class.getName();

    private View mView;
    private Unbinder unbinder;

    private DeviceEFOutletAdapter mOutletAdapter;
    private List<EFDeviceOutlet> mOutletList;
    private List<EFDeviceOutlet> addedDeviceList;

    private ControlDevice mDevice;
    private EFDeviceOutlet mOutlet;
    private SimpleArcDialog mDialog;

    @BindView(R.id.lstDevice)
    ListView lstDevice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_device, container, false);
        unbinder = ButterKnife.bind(this, mView);
        addedDeviceList = new ArrayList<>();

        if (BL_FIRST_START) {
            BL_FIRST_START = false;
            mDialog = Util.showArcDialog(getActivity());
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDialog.dismiss();
                }
            }, 4000);
        }

        initData();
       // initListener();
        return mView;
    }

    private void initData() {

        mOutletAdapter = new DeviceEFOutletAdapter(getActivity(), false,null ,new ListPositionCallback() {
            @Override
            public void currentPosition(int position) {
                handleOnOff(position);
            }
        });

        initDeviceList();
        lstDevice.setAdapter(mOutletAdapter);
        if (addedDeviceList!=null && addedDeviceList.size() > 0) {
            init();
        }
     //   lstDevice.setOnItemLongClickListener(this);
    }

    private void initDeviceList(){

        addedDeviceList = Util.initAddedDeviceList();
        mOutletAdapter.setList(addedDeviceList);
    }

   /* private void initListener() {
        lstDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mOutlet = addedDeviceList.get(position);
                Log.e("isSuccess","outlet = "+mOutlet);
                mDevice = DeviceManager.getInstance().getDevice(mOutlet.getParentMac());
//                Intent intent = new Intent(getActivity(), SingleOutletActivity.class);
//                intent.putExtra("device", mDevice);
//                intent.putExtra("outlet", mOpOutlet);
//                startActivity(intent);
                switch (mOutlet.getDeviceState()) {
                    case EFDeviceOutlet.STATE_OFF:
                        switchDevice(true);
                        break;
                    case EFDeviceOutlet.STATE_ON:
                        switchDevice(false);
                        break;
                    default:
                        switchDevice(true);
                        break;
                }
                final SimpleArcDialog mDialog = Util.showArcDialog(getActivity());
                try {
                    getActivity().unregisterReceiver(receiver);
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(Config.BROADCAST_RECVPIPE);
                    getActivity().registerReceiver(receiver, filter);
                    // init others
                    refreshStatus();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mDialog.dismiss();
                            switch (mOutlet.getDeviceState()) {
                                case EFDeviceOutlet.STATE_OFF:
                                    switchDevice(true);
                                    break;
                                case EFDeviceOutlet.STATE_ON:
                                    switchDevice(false);
                                    break;
                                default:
                                    switchDevice(true);
                                    break;
                            }
                        }
                    }, 2000);
                } catch (Exception e) {
                    e.printStackTrace();
                    mDialog.dismiss();
                }
            }
        });
    }*/


    @OnClick(R.id.btnAdd)
    public void addDevice() {
        Intent intent = new Intent(getActivity(), ScanDeviceActivity.class);
        getActivity().startActivityForResult(intent, Constant.ADD_DEVICE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (addedDeviceList!=null && addedDeviceList.size() > 0) {
             getActivity().unregisterReceiver(receiver);
        }
    }

   /* @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

        SweetAlertDialog sweetAlertDialog = Util.createSweetDeleteDialog(getActivity(),"You want to delete this device!");
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {

                if (!addedDeviceList.isEmpty() && addedDeviceList.size()==1){

                    for(int i=0;i<mOutletList.size();i++){
                        if(mOutletList.get(i).isDeviceAdded()){
                            updateGroup(mOutletList.get(i));
                            DaoUtil.saveOrUpdate(mOutletList.get(i));
                        }
                    }
                }
                else{
                    updateGroup(mOutletList.get(i));
                    DaoUtil.saveOrUpdate(mOutletList.get(i));
                }

                addedDeviceList.clear();
                initAddedDeviceList();
                mOutletAdapter.notifyDataSetChanged();
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        return true;
    }*/

    private void updateGroup(EFDeviceOutlet efDeviceOutlet){
        List<DbGroupInfo> dbGroupInfos = DaoUtil.getAllList(DbGroupInfo.class);
        if (dbGroupInfos!=null && !dbGroupInfos.isEmpty()) {
            for (int i = 0; i < dbGroupInfos.size(); i++) {
                final ArrayList<String> aList = new ArrayList(Arrays.asList(dbGroupInfos.get(i).getDeviceList().split("~")));
                for (int j = 0; j < aList.size(); j++) {
                    Gson gson = new Gson();
                    if (gson.fromJson(aList.get(j), EFDeviceOutlet.class).getDeviceMac().equals(efDeviceOutlet.getDeviceMac())) {
                        efDeviceOutlet.setDeviceAdded(false);
                        aList.set(j, efDeviceOutlet.toString());
                        dbGroupInfos.get(i).setDeviceList(Joiner.on("~").join(aList));
                        DaoUtil.update(dbGroupInfos.get(i), new String[]{"deviceList"});
                    }
                }
            }
        }else
            efDeviceOutlet.setDeviceAdded(false);
    }

    public void handleOnOff(int position) {

        final EFDeviceOutlet mOutlet;
        final ControlDevice mDevice;

        mOutlet = addedDeviceList.get(position);
        Log.e("isSuccess","outlet = "+mOutlet);
        mDevice = DeviceManager.getInstance().getDevice(mOutlet.getParentMac());
//                Intent intent = new Intent(getActivity(), SingleOutletActivity.class);
//                intent.putExtra("device", mDevice);
//                intent.putExtra("outlet", mOpOutlet);
//                startActivity(intent);
//        switch (mOutlet.getDeviceState()) {
//            case EFDeviceOutlet.STATE_OFF:
//                switchDevice(true, mOutlet, mDevice);
//                break;
//            case EFDeviceOutlet.STATE_ON:
//                switchDevice(false, mOutlet, mDevice);
//                break;
//            default:
//                switchDevice(true, mOutlet, mDevice);
//                break;
//        }
        final SimpleArcDialog mDialog = Util.showArcDialog(getActivity());
        try {
            getActivity().unregisterReceiver(receiver);
            IntentFilter filter = new IntentFilter();
            filter.addAction(Config.BROADCAST_RECVPIPE);
            getActivity().registerReceiver(receiver, filter);
            // init others
//            refreshStatus(mDevice, mOutlet);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDialog.dismiss();
                    switch (mOutlet.getDeviceState()) {
                        case EFDeviceOutlet.STATE_OFF:
                            switchDevice(true, mOutlet, mDevice);
                            break;
                        case EFDeviceOutlet.STATE_ON:
                            switchDevice(false, mOutlet, mDevice);
                            break;
                        default:
                            switchDevice(true, mOutlet, mDevice);
                            break;
                    }
                }
            }, 3000);
        } catch (Exception e) {
            e.printStackTrace();
            mDialog.dismiss();
        }
    }

    private enum Operation {
        REFRESH, UPDATE
    }

    private Operation mOperation;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int requestCode = intent.getIntExtra(Config.REQUEST_CODE_KEY, -1);
            byte[] data = intent.getByteArrayExtra(Config.DATA);
            if (data == null) {
                return;
            }
            if (data[0] == -1) {
                String deviceMac = bytesToHexString(arrayCopyBytes(data, 27, 6));
                Log.e("deviceMac","deviceMac = "+deviceMac);

//                if (!deviceMac.equals(mOutlet.getDeviceMac())) {
//                    return;
//                }

                EFDeviceOutlet mOutlet;

                for (int i = 0; i < addedDeviceList.size(); i++){
                    if (deviceMac.equals(addedDeviceList.get(i).getDeviceMac())) {
                        mOutlet = addedDeviceList.get(i);
                        mOutlet.setDeviceState(data[43]);
                        DaoUtil.update(mOutlet, new String[]{"deviceState"});
//                        initData();
                        mOutletAdapter.notifyDataSetChanged();
                    }
                }
            }
            if (requestCode != RequestCode.SINGLE_OUTLET_CODE && requestCode != RequestCode.SINGLE_OUTLET_LEXIN_CODE) {
                return;
            }
            //receive state data(0c )
            if (requestCode == RequestCode.SINGLE_OUTLET_LEXIN_CODE) {
                byte fist = data[0];
                byte third = data[2];
                byte permission = data[33];
                if (fist == -1 || fist == 2 || fist == -16) {
                    return;
                }
                if (fist == 12 && ((third == 25) && (permission == 0 || permission == 1))) {
                    String deviceMac = bytesToHexString(arrayCopyBytes(data, 27, 6));

                    if (!deviceMac.equals(mOutlet.getDeviceMac())) {
                        return;
                    }
                    noPermissonDialog();

                } else {
                    // if have permission
                    EFDeviceOutlet outlet = ByteUtils.decodeSigOutlet(data);

                    if (outlet != null && outlet.getDeviceMac().equals(mOutlet.getDeviceMac())) {
                        if (mOutlet.getDeviceType() == EFDevice.TYPE_WIFI_OUTLET) {
                            mOutlet.setLock(outlet.getLock());
                            mOutlet.setDeviceState(outlet.getDeviceState());
                            mOutlet.setFirmwareVersion(outlet.getFirmwareVersion());
                            DaoUtil.update(mOutlet, new String[]{"deviceState", "firmwareVersion", "lock"});
                        } else {
                            mOutlet.setDeviceState(outlet.getDeviceState());
                            DaoUtil.update(mOutlet, new String[]{"deviceState"});
//                            initData();
                        }
                        Log.e("state","device state first in onreceive = "+mOutlet.getDeviceState());
                    }
                }
            } else {

                if (data[0] == -2) {
                    if (data[38] == 1) {
                        XlinkUtils.shortTips(getString(R.string.permission_to_you_to_control_the_device_be_cancelled));
                    }
                    //receive control data(02 )
                } else if (data[0] == 02 && data[1] == 00 && (data[2] == 25 || data[2] == 23)) {
                    String deviceMac = bytesToHexString(arrayCopyBytes(data, 27, 6));
                    //    Log.d("SingleOutletActivity==", deviceMac);

                    EFDeviceOutlet mOutlet;

                    for (int i = 0; i < addedDeviceList.size(); i++){
                        if (deviceMac.equals(addedDeviceList.get(i).getDeviceMac())) {
                            mOutlet = addedDeviceList.get(i);
                            mOutlet.setPosition(data[33]);
                            mOutlet.setDeviceState(data[34]);
                            DaoUtil.update(mOutlet, new String[]{"deviceState"});
//                            initData();
                        }
                    }
//                    if (!deviceMac.equals(mOutlet.getDeviceMac())) {
//                        return;
//                    }
                }

                if (mOperation == Operation.REFRESH) {
                    //Parsed into a wifi device object
                    EFDeviceOutlet outlet = ByteUtils.decodeSigOutlet(data);

                    if (outlet != null && outlet.getDeviceMac().equals(mOutlet.getDeviceMac())) {
                        if (mOutlet.getDeviceType() == EFDevice.TYPE_WIFI_OUTLET) {
                            mOutlet.setLock(outlet.getLock());
                            mOutlet.setDeviceState(outlet.getDeviceState());
                            mOutlet.setFirmwareVersion(outlet.getFirmwareVersion());
                            DaoUtil.update(mOutlet, new String[]{"deviceState", "firmwareVersion", "lock"});
                        } else {
                            mOutlet.setDeviceState(outlet.getDeviceState());
                            DaoUtil.update(mOutlet, new String[]{"deviceState"});
                            initData();
                        }
                    }
                }
            }
            mOutletAdapter.notifyDataSetChanged();
        }
    };

    /**
     * Prompt the user without permission
     */
    private void noPermissonDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(getString(R.string.remind_msg));
        dialog.setMessage(getString(R.string.no_permission_control));
        // XlinkUtils.shortTips(R.string.no_permission_control);
        dialog.setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        });
        AlertDialog alertDialog = dialog.create();
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }


    public void init() {
        //register Bordcast
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.BROADCAST_RECVPIPE);
        getActivity().registerReceiver(receiver, filter);
        // init others
//        mOutlet = addedDeviceList.get(0);
//        mDevice = DeviceManager.getInstance().getDevice(mOutlet.getParentMac());
//        refreshStatus();

        for (int i = 0; i < addedDeviceList.size(); i++){
            EFDeviceOutlet mOutlet;
            ControlDevice mDevice;
            mOutlet = addedDeviceList.get(i);
            mDevice = DeviceManager.getInstance().getDevice(mOutlet.getParentMac());
            refreshStatus(mDevice, mOutlet);
        }
    }

    /**
     * Update the switch icon
     */
    private void updateOutletView() {
        switch (mOutlet.getDeviceState()) {
            case EFDeviceOutlet.STATE_OFF:
//                sockitBox.setBackgroundResource(R.drawable.sockit_cansol);
//                wifiViewSwitchIm.setSelected(false);
                break;
            case EFDeviceOutlet.STATE_ON:
//                sockitBox.setBackgroundResource(R.drawable.sockit_open);
//                wifiViewSwitchIm.setSelected(true);
                break;

            default:
//                sockitBox.setBackgroundResource(R.drawable.sockit_unknown);
                break;
        }
    }

    //02 Switch control instruction
    // （1 byte）	DeviceType （2 byte）	PID （4 bytes）	 UserNumber	（20 bytes）	Wifi device ID MAC（6 bytes）	Data（N bytes）
    //    0	             1~2	             3~6	             7~26	                  27~32	                             33
    //  0x02	       DevType	             PID


//  note：1、APP<---->wifi device，data area as below:
//          1)、socket position flag (1 byte)：start from 1
//  2)、socket ON/OFF state (1 byte)：0 - OFF，non 0 - ON；


    /**
     * Switch control instruction
     *
     * @param on
     */
    private void switchDevice(final boolean on, final EFDeviceOutlet mOutlet, ControlDevice mDevice) {
        byte[] phoneData = ByteUtils.getPhonenumberBytes();
        byte[] commandData = ByteUtils.int2OneByte(on ? 1 : 0);

        Log.e("data","phoneData = "+ Arrays.toString(phoneData));
        Log.e("data","commanddata = "+ Arrays.toString(commandData));
        byte[] data = null;

        if (mOutlet.getDeviceType() == EFDevice.TYPE_WIF_OUTLET_LEXIN) {

            Log.e("data","devicemac = "+ mOutlet.getDeviceMac());
            data = ByteUtils.append(Config.STRIP_LENGTH, new byte[]{0x02}, Prefix.REF_LEXIN_OUTLET,
                    ByteUtils.getSystemTimeBytes(System.currentTimeMillis()), phoneData, ByteUtils.hexStringToBytes(mOutlet.getDeviceMac()), ByteUtils.int2OneByte(1), commandData);

            Log.e("data","data = "+ Arrays.toString(data));
//            mDevice = DeviceManager.getInstance().getDevice(mOutlet.getParentMac());

            XlinkClient.getInstance().sendPipe(mDevice, data, RequestCode.SINGLE_OUTLET_CODE, new SendCallback() {
                @Override
                public void onSendEnd(boolean isSuccess) {
                    if (isSuccess) {
                        Log.e("isSuccess","isSuccess --> "+isSuccess);
                        Log.e("isSuccess","DeviceType --> "+mOutlet.getDeviceType());
                        if (mOutlet.getDeviceType() == EFDevice.TYPE_WIF_OUTLET_LEXIN) {
                            mOutlet.setDeviceState(on ? 1 : 0);
                            DaoUtil.saveOrUpdate(mOutlet);
                            mOutletAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }

    /**
     * Access to equipment state of instruction
     */
//    private void refreshStatus() {
//        mOperation = Operation.REFRESH;
//        byte[] phoneData = ByteUtils.getPhonenumberBytes();
//        byte[] macData = ByteUtils.hexStringToBytes(mOutlet.getDeviceMac());
//        byte[] data = null;
//        if (mOutlet.getDeviceType() == EFDevice.TYPE_WIF_OUTLET_LEXIN) {
//            data = ByteUtils.append(Config.STRIP_LENGTH,
//                    new byte[]{0x0C},
//                    Prefix.REF_LEXIN_OUTLET,
//                    ByteUtils.getSystemTimeBytes(System.currentTimeMillis()),
//                    phoneData,
//                    macData);
//            XlinkAgent.getInstance().initDevice(mDevice.getXDevice());
//            DeviceManager.getInstance().connectDevice(getActivity(), mDevice, null);
//            XlinkClient.getInstance().sendPipe(mDevice, data, RequestCode.SINGLE_OUTLET_LEXIN_CODE, null);
//        }
//    }
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
            DeviceManager.getInstance().connectDevice(getActivity(), mDevice, null);
            XlinkClient.getInstance().sendPipe(mDevice, data, RequestCode.SINGLE_OUTLET_LEXIN_CODE, null);
        }
    }

    /**
     * Rights management instruction
     */
    private void sendPermissionManagerData() {
        byte[] data = ByteUtils.append(Config.STRIP_LENGTH,
                new byte[]{0x0a}, Prefix.REF_LEXIN_OUTLET, ByteUtils.getSystemTimeBytes(System.currentTimeMillis()),
                ByteUtils.getPhonenumberBytes(), ByteUtils.hexStringToBytes(mDevice.getMacAddress()), ByteUtils.int2OneByte(0), ByteUtils.int2OneByte(0));
        XlinkClient.getInstance().sendPipe(mDevice, data, RequestCode.SINGLE_OUTLET_CODE, null);
        Toast.makeText(getActivity(), "send permission data:" + bytesToHexString(data), Toast.LENGTH_SHORT).show();
    }

    /**
     * Firmware upgrade instruction
     */
    private void sendUpdateHardwareData() {
        byte[] verBytes = new byte[]{0x01, 0x01, 0x01};//Need to request the lastest verson no. of firmware from server,3bytes,This is false data 1.1.1
        byte[] data = ByteUtils.append(Config.STRIP_LENGTH,
                new byte[]{0x06},
                Prefix.REF_LEXIN_OUTLET,
                ByteUtils.getSystemTimeBytes(System.currentTimeMillis()),
                ByteUtils.getPhonenumberBytes(), ByteUtils.hexStringToBytes(mOutlet.getDeviceMac()),
                verBytes);
        XlinkClient.getInstance().sendPipe(mDevice, data, RequestCode.SINGLE_OUTLET_CODE, null);
        Toast.makeText(getActivity(), "send updateHardware data:" + bytesToHexString(data), Toast.LENGTH_SHORT).show();
    }

}
