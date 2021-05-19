package com.chug.north_outlet.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.test.espresso.core.deps.guava.base.Joiner;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chug.north_outlet.R;
import com.chug.north_outlet.adapter.ImageAdapter;
import com.chug.north_outlet.bean.DbGroupInfo;
import com.chug.north_outlet.bean.EFDeviceOutlet;
import com.chug.north_outlet.dao.DaoUtil;
import com.chug.north_outlet.utils.Constant;
import com.chug.north_outlet.utils.Util;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddScanDeviceActivity extends Activity implements View.OnClickListener {

    @BindView(R.id.rl_add_scan_back)
    RelativeLayout rlBackArrow;
    @BindView(R.id.rl_device_save)
    RelativeLayout rlSaveDevice;
    @BindView(R.id.iv_add_device)
    ImageView ivProfilePic;
    @BindView(R.id.text_input_add_device_name)
    TextInputLayout tiDeviceName;
    @BindView(R.id.text_input_add_device_type)
    TextInputLayout tiDeviceType;
    @BindView(R.id.ed_add_device_name)
    EditText edDeviceName;
    @BindView(R.id.ed_add_device_type) EditText edDeviceType;
    EFDeviceOutlet mOpOutlet;
    @BindView(R.id.btn_delete_device)
    Button btnDeleteDevice;
    int selectedImage = -1;
    boolean isEditScanDevice = false,isDevicePickChanged;
    TypedArray imageIDs;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.primary_dark));
        setContentView(R.layout.activity_add_scan_device);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView tvTitle = (TextView) toolbar.findViewById(R.id.tv_scan_device_title);

        if(getIntent().getExtras()!=null){

            mOpOutlet = (EFDeviceOutlet) getIntent().getSerializableExtra(Constant.EFDEVICEOUTLET);
            isEditScanDevice = getIntent().getBooleanExtra(Constant.ISEDITSCANDEVICE,false);
        }
        if(isEditScanDevice){
            tvTitle.setText("Edit Device");
            if(mOpOutlet.getName()!=null)
                edDeviceName.setText(mOpOutlet.getName());
            if(mOpOutlet.getSubName()!=null)
                edDeviceType.setText(mOpOutlet.getSubName());
            if (mOpOutlet.getIcon()!= -1){

                imageIDs = this.getResources().obtainTypedArray(R.array.appliancesIcon);
                ivProfilePic.setImageResource(imageIDs.getResourceId(mOpOutlet.getIcon(), -1));
            }
            btnDeleteDevice.setVisibility(View.VISIBLE);
            btnDeleteDevice.setOnClickListener(this);
        }

        rlBackArrow.setOnClickListener(this);
        rlSaveDevice.setOnClickListener(this);
        ivProfilePic.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.rl_add_scan_back:
                    onBackPressed();
                break;
            case R.id.rl_device_save:

                if (selectedImage == -1 && !isEditScanDevice){
                    Toast.makeText(this, "Please select image!", Toast.LENGTH_SHORT).show();
                }
                else if(edDeviceName.getText().toString().equals("")){
                    tiDeviceName.setError("Device name is required field !");
                }else{
                    tiDeviceName.setError(null);
                    if(edDeviceType.getText().toString().equals("")){
                        tiDeviceType.setError("Device type is required field !");
                    }else{
                        tiDeviceType.setError(null);
                        mOpOutlet.setName(edDeviceName.getText().toString());
                        mOpOutlet.setSubName(edDeviceType.getText().toString());
                        if(isEditScanDevice){
                            if(!isDevicePickChanged)
                                mOpOutlet.setIcon(mOpOutlet.getIcon());
                            else
                                mOpOutlet.setIcon(selectedImage);
                        }else
                            mOpOutlet.setIcon(selectedImage);

                        mOpOutlet.setDeviceAdded(true);

                        updateGroup(mOpOutlet,true);
                        DaoUtil.saveOrUpdate(mOpOutlet);
                        Intent intent = new Intent(AddScanDeviceActivity.this,MenuActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                }

                break;
            case R.id.iv_add_device:
                final Dialog dialog = new Dialog(AddScanDeviceActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_icon_select);
                Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                GridView gridView = (GridView) dialog.findViewById(R.id.gridView);
                gridView.setAdapter(new ImageAdapter(AddScanDeviceActivity.this));
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TypedArray imageIDs = getResources().obtainTypedArray(R.array.appliancesIcon);
                        ivProfilePic.setImageResource(imageIDs.getResourceId(position, -1));
                        selectedImage = position;
                        isDevicePickChanged = true;
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case R.id.btn_delete_device:
                    updateGroup(mOpOutlet,false);
                break;
        }
    }
    private void updateGroup(final EFDeviceOutlet efDeviceOutlet, boolean isSave){
        final List<DbGroupInfo> dbGroupInfos = DaoUtil.getAllList(DbGroupInfo.class);

        if(isSave){
            if(dbGroupInfos !=null && !dbGroupInfos.isEmpty()){

                for (int i=0;i<dbGroupInfos.size();i++){
                    final ArrayList<String> aList = new ArrayList(Arrays.asList(dbGroupInfos.get(i).getDeviceList().split("~")));
                    for (int j = 0; j < aList.size(); j++) {
                        Gson gson = new Gson();

                        if (gson.fromJson(aList.get(j), EFDeviceOutlet.class).getDeviceMac().equals(efDeviceOutlet.getDeviceMac())){
                            efDeviceOutlet.setDeviceAdded(true);
                            aList.set(j,efDeviceOutlet.toString());
                            dbGroupInfos.get(i).setDeviceList(Joiner.on("~").join(aList));
                            DaoUtil.update(dbGroupInfos.get(i),new String[]{"deviceList"});
                        }
                    }
                }
            }
        }else {
            SweetAlertDialog sweetAlertDialog = Util.createSweetDeleteDialog(this,"You want to delete this device!");
            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {

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
                    }else{
                        efDeviceOutlet.setName(null);
                        efDeviceOutlet.setSubName(null);
                        efDeviceOutlet.setDeviceAdded(false);
                    }

                    DaoUtil.saveOrUpdate(mOpOutlet);
                    Intent intent = new Intent(AddScanDeviceActivity.this,MenuActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
            });
        }
    }
}
