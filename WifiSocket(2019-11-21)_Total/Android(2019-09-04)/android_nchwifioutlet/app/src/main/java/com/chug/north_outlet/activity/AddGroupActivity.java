package com.chug.north_outlet.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.test.espresso.core.deps.guava.base.Joiner;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chug.north_outlet.R;
import com.chug.north_outlet.adapter.DeviceEFOutletAdapter;
import com.chug.north_outlet.adapter.ImageAdapter;
import com.chug.north_outlet.bean.DbGroupInfo;
import com.chug.north_outlet.bean.EFDeviceOutlet;
import com.chug.north_outlet.bean.EFScene;
import com.chug.north_outlet.callback.ListPositionCallback;
import com.chug.north_outlet.dao.DaoUtil;
import com.chug.north_outlet.utils.Constant;
import com.chug.north_outlet.utils.Util;
import com.google.gson.Gson;

import org.xutils.db.sqlite.WhereBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddGroupActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    @BindView(R.id.iv_grp)
    ImageView imgIcon;
    @BindView(R.id.rl_grp_save)
    RelativeLayout rlSave;
    @BindView(R.id.rl_add_grp_back)
    RelativeLayout rlBack;
    @BindView(R.id.lv_grp)
    ListView lstDevice;
    @BindView(R.id.text_input_grp_name)
    TextInputLayout tiGroupName;
    @BindView(R.id.ed_grp_name)
    EditText edtGroupName;
    DeviceEFOutletAdapter mOutletAdapter;
    List<EFDeviceOutlet> deviceOutletList;
    List<EFDeviceOutlet> addedDeviceList;

    int selectedImage = -1;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.primary_dark));
        setContentView(R.layout.activity_add_group);
        ButterKnife.bind(this);
        addedDeviceList = new ArrayList<>();

            initData();

        // getSupportActionBar().hide();
       /* mDeviceListGroupAdapter = new DeviceListGroupAdapter(this);
        lstDevice.setAdapter(mDeviceListGroupAdapter);*/
        rlBack.setOnClickListener(this);
        rlSave.setOnClickListener(this);
    }

    private void initData(){

        deviceOutletList = DaoUtil.getAllList(EFDeviceOutlet.class, WhereBuilder.b("sceneId", "=", EFScene.DEFAULT_SCENEID));
        if(deviceOutletList!=null && !deviceOutletList.isEmpty()){
            for (int i=0;i<deviceOutletList.size();i++){
                if(deviceOutletList.get(i).isDeviceAdded())
                    addedDeviceList.add(deviceOutletList.get(i));
            }
        }
        mOutletAdapter = new DeviceEFOutletAdapter(this, true, null,new ListPositionCallback() {
            @Override
            public void currentPosition(int position) {

            }
        });
        mOutletAdapter.setList(addedDeviceList);
        lstDevice.setAdapter(mOutletAdapter);
        lstDevice.setOnItemClickListener(this);
    }

    @OnClick(R.id.iv_grp)
    public void image()  {
        final Dialog dialog = new Dialog(AddGroupActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_icon_select);
        GridView gridView = (GridView) dialog.findViewById(R.id.gridView);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        gridView.setAdapter(new ImageAdapter(AddGroupActivity.this));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TypedArray imageIDs = getResources().obtainTypedArray(R.array.appliancesIcon);
                imgIcon.setImageResource(imageIDs.getResourceId(position, -1));
                selectedImage = position;
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /*@OnClick(R.id.btnAdd)
    public void Add(View view) {
        if (edtGroupName.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Enter Valid Group Name", Toast.LENGTH_LONG).show();
            return;
        } else if (mDeviceListGroupAdapter.getListDevices().size() == 0) {
            Toast.makeText(this, "Select single device to continue", Toast.LENGTH_LONG).show();
            return;
        }

        DbGroupInfo dbGroupInfo = new DbGroupInfo();
        dbGroupInfo.setDeviceList(Joiner.on(",").join(mDeviceListGroupAdapter.getListDevices()));
        dbGroupInfo.setName(edtGroupName.getText().toString());
        dbGroupInfo.setIcon(selectedImage);
        DaoUtil.saveOrUpdate(dbGroupInfo);
        Util.hideKeyboard(this,view);
        finish();

    }*/

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_add_grp_back:
                onBackPressed();
                break;
            case R.id.rl_grp_save:
                boolean isGroupExists = false;
                if (selectedImage == -1){
                    Toast.makeText(this, "Please select image!", Toast.LENGTH_SHORT).show();
                }
                else if(edtGroupName.getText().toString().equals("")){
                    tiGroupName.setError("Group name is required field !");
                }else{
                    tiGroupName.setError(null);
                    List<EFDeviceOutlet> selectedDeviceList = new ArrayList<>();
                    for(int i=0;i< addedDeviceList.size();i++){
                        if(addedDeviceList.get(i).isDeviceSelected()){
                            selectedDeviceList.add(addedDeviceList.get(i));
                        }
                    }
                     if (selectedDeviceList.size()<2){
                        Util.showMessage(AddGroupActivity.this,"Please select at least two outlets to create group!");
                    }
                    else {
                        List<DbGroupInfo> dbGroupInfos = DaoUtil.getAllList(DbGroupInfo.class);
                        if (dbGroupInfos!=null && !dbGroupInfos.isEmpty()) {
                            for (int i = 0; i < dbGroupInfos.size(); i++) {
                                if (dbGroupInfos.get(i).getName().equals(edtGroupName.getText().toString().trim())) {
                                    Util.showMessage(this, "This group already exists!");
                                    isGroupExists = true;
                                    break;
                                }else{
                                    Gson gson = new Gson();
                                    if (!isGroupExists){
                                    final ArrayList<String> aList = new ArrayList(Arrays.asList(dbGroupInfos.get(i).getDeviceList().split("~")));
                                    for (int j=0;j<aList.size();j++){
                                        for (int k=0;k<selectedDeviceList.size();k++){

                                        EFDeviceOutlet efDeviceOutlet = gson.fromJson(aList.get(j), EFDeviceOutlet.class);
                                        if (efDeviceOutlet.getDeviceMac().equals(selectedDeviceList.get(k).getDeviceMac())) {
                                            Util.showMessage(this, "Group with this Outlet is already created!");
                                            isGroupExists = true;
                                            break;
                                        }
                                        }
                                    }
                                    }
                                }
                            }
                        }

                        if (!isGroupExists){
                            Log.e("selectedList","selected device list = "+addedDeviceList.size());
                            DbGroupInfo dbGroupInfo = new DbGroupInfo();

                            dbGroupInfo.setDeviceList(Joiner.on("~").join(selectedDeviceList));
                            Log.e("list",Joiner.on("~").join(selectedDeviceList));
                            dbGroupInfo.setName(edtGroupName.getText().toString());
                            dbGroupInfo.setIcon(selectedImage);
                            DaoUtil.saveOrUpdate(dbGroupInfo);
                            Util.hideKeyboard(this,view);

                            Intent intent = new Intent(AddGroupActivity.this,MenuActivity.class);
                            intent.putExtra("fragment", Constant.FROM_ADDGRP);
                            startActivity(intent);
                            finishAffinity();
                        }

                    }
                    //    Toast.makeText(AddGroupActivity.this, "You must have to select more than one device to create a group !", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


    }
}
