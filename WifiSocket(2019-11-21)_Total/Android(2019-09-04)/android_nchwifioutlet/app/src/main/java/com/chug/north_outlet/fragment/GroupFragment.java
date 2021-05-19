package com.chug.north_outlet.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.test.espresso.core.deps.guava.base.Joiner;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.chug.north_outlet.Config;
import com.chug.north_outlet.Prefix;
import com.chug.north_outlet.R;
import com.chug.north_outlet.RequestCode;
import com.chug.north_outlet.activity.AddGroupActivity;
import com.chug.north_outlet.adapter.GroupListAdapter;
import com.chug.north_outlet.bean.ControlDevice;
import com.chug.north_outlet.bean.DbGroupInfo;
import com.chug.north_outlet.bean.EFDevice;
import com.chug.north_outlet.bean.EFDeviceOutlet;
import com.chug.north_outlet.bean.EFScene;
import com.chug.north_outlet.callback.ListPositionCallback;
import com.chug.north_outlet.callback.SendCallback;
import com.chug.north_outlet.dao.DaoUtil;
import com.chug.north_outlet.http.XlinkClient;
import com.chug.north_outlet.utils.ByteUtils;
import com.chug.north_outlet.utils.DeviceManager;
import com.chug.north_outlet.utils.Util;
import com.chug.north_outlet.utils.XlinkUtils;
import com.google.gson.Gson;
import com.leo.simplearcloader.SimpleArcDialog;

import org.xutils.db.sqlite.WhereBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.xlink.wifi.sdk.XlinkAgent;

import static com.chug.north_outlet.utils.ByteUtils.arrayCopyBytes;
import static com.chug.north_outlet.utils.ByteUtils.bytesToHexString;

public class GroupFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.lstGroup)
    ListView lstGroup;
    @BindView(R.id.btnGroup)
    Button bntAddGrp;
    EFDeviceOutlet mOutlet;
    ControlDevice mDevice;
    List<DbGroupInfo> dbGroupInfo;
    GroupListAdapter groupListAdapter;
    DbGroupInfo db;
    private View mView;
    private boolean on_off;
    private Unbinder unbinder;
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
                Log.e("deviceMac", "deviceMac = " + deviceMac);
                if (!deviceMac.equals(mOutlet.getDeviceMac())) {

                    return;
                }

                mOutlet.setDeviceState(data[43]);
                updateOutletView();
                DaoUtil.update(mOutlet, new String[]{"deviceState"});
                // DaoUtil.update(db,new String[]{"deviceList"});
                updateGroup();

                Log.e("state 1", "device state first in onreceive = " + mOutlet.getDeviceState());
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
                        }
                        updateGroup();
                        // DaoUtil.update(db,new String[]{"deviceList"});

                        Log.e("state 2", "device state first in onreceive = " + mOutlet.getDeviceState());

                        updateOutletView();
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
                    if (!deviceMac.equals(mOutlet.getDeviceMac())) {
                        return;
                    }
                    mOutlet.setPosition(data[33]);
                    mOutlet.setDeviceState(data[34]);
                    updateOutletView();
                    DaoUtil.update(mOutlet, new String[]{"deviceState"});
                    updateGroup();
                    // DaoUtil.update(db,new String[]{"deviceList"});

                    Log.e("state 3", "device state second in onreceive = " + mOutlet.getDeviceState());
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
                        }
                        updateGroup();
                        //     DaoUtil.update(db,new String[]{"deviceList"});

                        updateOutletView();
                        Log.e("state 4", "device state = " + mOutlet.getDeviceState());
                    }
                }
            }
            /*mOutlet.setOnOff(on_off);
            groupListAdapter.notifyDataSetChanged();*/
            //getActivity().unregisterReceiver(receiver);
        }

    };

    @Override
    public void onResume() {
        super.onResume();
        Log.d("fragment", "onresume called !");


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("fragment", "oncreate called !");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("fragment", "onviewcreated called !");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("fragment", "oncreateview called !");
        mView = inflater.inflate(R.layout.fragment_group, container, false);
        unbinder = ButterKnife.bind(this, mView);
        bntAddGrp.setOnClickListener(this);
        initData();
        return mView;
    }

    private void initData() {

        dbGroupInfo = DaoUtil.getAllList(DbGroupInfo.class);
        if (dbGroupInfo == null) {
            dbGroupInfo = new ArrayList<>();
        }
        groupListAdapter = new GroupListAdapter(getActivity(), new ListPositionCallback() {
            @Override
            public void currentPosition(int position) {
                handleOnOff(position);
            }
        });
        lstGroup.setAdapter(groupListAdapter);

        if(!dbGroupInfo.isEmpty()) {

            init();
        }
    }

    public void init() {
        //register Bordcast
        Log.e("state","init executed !!!");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.BROADCAST_RECVPIPE);
        getActivity().registerReceiver(receiver, filter);
        // init others
        dbGroupInfo = DaoUtil.getAllList(DbGroupInfo.class);
        Gson gson = new Gson();
        String str = dbGroupInfo.get(0).getDeviceList();
        final ArrayList<String> aList = new ArrayList(Arrays.asList(str.split("~")));

        db = dbGroupInfo.get(0);
        mOutlet = gson.fromJson(aList.get(0), EFDeviceOutlet.class);

        mDevice = DeviceManager.getInstance().getDevice(mOutlet.getParentMac());

        refreshStatus();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGroup:
                List<EFDeviceOutlet> addedDeviceList = new ArrayList<>();
               List<EFDeviceOutlet> deviceOutletList = DaoUtil.getAllList(EFDeviceOutlet.class, WhereBuilder.b("sceneId", "=", EFScene.DEFAULT_SCENEID));
                if(deviceOutletList!=null && !deviceOutletList.isEmpty()){
                    for (int i=0;i<deviceOutletList.size();i++){
                        if(deviceOutletList.get(i).isDeviceAdded())
                            addedDeviceList.add(deviceOutletList.get(i));
                    }
                }

                if(!addedDeviceList.isEmpty()){
                    Intent intent = new Intent(getActivity(), AddGroupActivity.class);
                    startActivity(intent);
                }else {
                    Util.showMessage(getActivity(), "Add device first ! Then you can make a group");
                }

                break;
        }
    }


    public void handleOnOff(int position) {


       List<DbGroupInfo> dbGroupInfos =  DaoUtil.getAllList(DbGroupInfo.class);
        Log.e("state", "on item click  ");
        db = dbGroupInfos.get(position);

        // final String str = dbGroupInfo.get(i).getDeviceList();
        final ArrayList<String> aList = new ArrayList(Arrays.asList(db.getDeviceList().split("~")));


        Handler delayHandler = new Handler();


        if (on_off)
            on_off = false;
        else
            on_off = true;

        for (int k = 1; k < aList.size() + 1; k++) {

            final SimpleArcDialog mDialog = Util.showArcDialog(getActivity());

            final int finalK = k;
            delayHandler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    Gson gson = new Gson();
                    Log.e("outlet", "outlet --> " + mOutlet.getDeviceState() + "device name = " + mOutlet.getDeviceName());
                    mOutlet = gson.fromJson(aList.get(finalK - 1), EFDeviceOutlet.class);

                    mDevice = DeviceManager.getInstance().getDevice(mOutlet.getParentMac());
                    getActivity().unregisterReceiver(receiver);
                    final IntentFilter filter = new IntentFilter();
                    filter.addAction(Config.BROADCAST_RECVPIPE);
                    getActivity().registerReceiver(receiver, filter);
                    refreshStatus();
                       /* getActivity().unregisterReceiver(receiver);
                        IntentFilter filter = new IntentFilter();
                        filter.addAction(Config.BROADCAST_RECVPIPE);
                        getActivity().registerReceiver(receiver, filter);
                        refreshStatus();*/
                    if(mOutlet.isDeviceAdded()) {
                        try {
                            Log.e("state onitemClick", "mOutlet.getDeviceState() =" + mOutlet.getDeviceState());
                            switch (mOutlet.getDeviceState()) {
                                case EFDeviceOutlet.STATE_OFF:
                                    switchDevice(true);
                                    //mOutlet.setDeviceState(EFDeviceOutlet.STATE_ON);
                                    break;
                                case EFDeviceOutlet.STATE_ON:
                                    switchDevice(false);
                                    //mOutlet.setDeviceState(EFDeviceOutlet.STATE_OFF);
                                    break;
                                default:
                                    switchDevice(true);
                                    //mOutlet.setDeviceState(EFDeviceOutlet.STATE_ON);
                                    break;
                            }
                            // switchDevice(on_off);
                            mDialog.dismiss();
                            Log.e("outlet", "outlet state = " + mOutlet.getDeviceState());

                        } catch (Exception e) {
                            e.printStackTrace();
                            mDialog.dismiss();
                        }
                    }else{
                        Util.showMessage(getActivity(),"The device "+mOutlet.getName()+" is removed !! So it will no longer affected by Wifi Outlet !");
                        mDialog.dismiss();
                    }

                }
            }, 500 * k);

        }
    }

    private void refreshStatus() {
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

    private void switchDevice(final boolean on) {
        byte[] phoneData = ByteUtils.getPhonenumberBytes();
        byte[] commandData = ByteUtils.int2OneByte(on ? 1 : 0);

        byte[] data = null;
        if (mOutlet.getDeviceType() == EFDevice.TYPE_WIF_OUTLET_LEXIN) {
            data = ByteUtils.append(Config.STRIP_LENGTH, new byte[]{0x02}, Prefix.REF_LEXIN_OUTLET,
                    ByteUtils.getSystemTimeBytes(System.currentTimeMillis()), phoneData, ByteUtils.hexStringToBytes(mOutlet.getDeviceMac()), ByteUtils.int2OneByte(1), commandData);

            XlinkClient.getInstance().sendPipe(mDevice, data, RequestCode.SINGLE_OUTLET_CODE, new SendCallback() {
                @Override
                public void onSendEnd(boolean isSuccess) {
                    if (isSuccess) {
                        groupListAdapter.loadData();
                       // initData();
                        Log.e("isSuccess", "onsendEnd-->" + isSuccess);
                        if (mOutlet.getDeviceType() != EFDevice.TYPE_WIF_OUTLET_LEXIN) {
                            mOutlet.setDeviceState(on ? 1 : 0);
                            DaoUtil.saveOrUpdate(mOutlet);
                        }
                    }
                }
            });
        }
//        getActivity().unregisterReceiver(receiver);
//        refreshStatus();
    }

    public void updateGroup() {
        List<DbGroupInfo> dbGroupInfos = DaoUtil.getAllList(DbGroupInfo.class);
        if (dbGroupInfos == null) {
            dbGroupInfos = new ArrayList<>();
        }

        boolean isRefresh = false;
        for (int i = 0; i < dbGroupInfos.size(); i++) {
            final ArrayList<String> aList = new ArrayList(Arrays.asList(dbGroupInfos.get(i).getDeviceList().split("~")));
            for (int j = 0; j < aList.size(); j++) {
                Gson gson = new Gson();
                EFDeviceOutlet mOutletTemp = gson.fromJson(aList.get(j), EFDeviceOutlet.class);

                Log.e("state","moutletTemp = "+mOutletTemp.getDeviceState());
                Log.e("state","moutlet befor state changed = "+mOutlet.getDeviceState());
                if (db.getName().equals(dbGroupInfos.get(i).getName()) && mOutletTemp.getDeviceState()!=mOutlet.getDeviceState()) {
                    mOutletTemp.setDeviceState(mOutlet.getDeviceState());
                    aList.set(j, mOutletTemp.toString());
                    isRefresh = true;
                    dbGroupInfos.get(i).setDeviceList(Joiner.on("~").join(aList));
                    DaoUtil.update(dbGroupInfos.get(i),new String[]{"deviceList"});
                    Log.e("state","moutlet after state changed = "+mOutlet.getDeviceState());
                    Log.e("state","dbgroupinfos  = "+dbGroupInfos.get(i).getDeviceList());
                }
            }

           // DaoUtil.saveOrUpdate(dbGroupInfos.get(i));
        }
       if (isRefresh){
           groupListAdapter.loadData();
           groupListAdapter.notifyDataSetChanged();
       }

    }

   /* @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

        SweetAlertDialog sweetAlertDialog = Util.createSweetDeleteDialog(getActivity(),"You want to delete this Group!");
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                DaoUtil.delete(dbGroupInfo.get(i));
                dbGroupInfo.remove(i);
                groupListAdapter.loadData();
                groupListAdapter.notifyDataSetChanged();
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        return true;
    }*/

    private enum Operation {
        REFRESH, UPDATE
    }

}