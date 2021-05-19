package com.chug.north_outlet.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.test.espresso.core.deps.guava.base.Joiner;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import android.widget.TextView;

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

import org.xutils.db.sqlite.WhereBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditGroupActivity extends AppCompatActivity implements View.OnClickListener {

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
    @BindView(R.id.btn_delete_group)
    Button btnDeleteGroup;
    DbGroupInfo dbGroupInfo;
    TypedArray imageIDs;
    int selectedImage = -1;
    List<EFDeviceOutlet> addedDeviceList,deviceOutletList;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.primary_dark));
        setContentView(R.layout.activity_add_group);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        TextView tvTitle = (TextView) toolbar.findViewById(R.id.tv_edit_grp_title);
        tvTitle.setText("Edit Group");
        addedDeviceList = new ArrayList<>();
        btnDeleteGroup.setVisibility(View.VISIBLE);
        if (getIntent().getExtras()!=null){
            dbGroupInfo = (DbGroupInfo) getIntent().getSerializableExtra(Constant.GROUPOUTLET);
        }
        if (dbGroupInfo!=null){
            edtGroupName.setText(dbGroupInfo.getName());
            imageIDs = this.getResources().obtainTypedArray(R.array.appliancesIcon);
            if (dbGroupInfo.getIcon()!= -1){
                selectedImage = dbGroupInfo.getIcon();
                imgIcon.setImageResource(imageIDs.getResourceId(dbGroupInfo.getIcon(), -1));
            }

        }

        initData();
        btnDeleteGroup.setOnClickListener(this);
        rlBack.setOnClickListener(this);
        rlSave.setOnClickListener(this);
        imgIcon.setOnClickListener(this);
    }

    private void initData(){

        deviceOutletList = DaoUtil.getAllList(EFDeviceOutlet.class, WhereBuilder.b("sceneId", "=", EFScene.DEFAULT_SCENEID));
        if(deviceOutletList!=null && !deviceOutletList.isEmpty()){
            for (int i=0;i<deviceOutletList.size();i++){
                if(deviceOutletList.get(i).isDeviceAdded())
                    addedDeviceList.add(deviceOutletList.get(i));
            }
        }
        mOutletAdapter = new DeviceEFOutletAdapter(this, true,Util.getDeviceListFromGroup(dbGroupInfo),new ListPositionCallback() {
            @Override
            public void currentPosition(int position) {

            }
        });
        mOutletAdapter.setList(addedDeviceList);
        lstDevice.setAdapter(mOutletAdapter);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_delete_group:
                SweetAlertDialog sweetAlertDialog = Util.createSweetDeleteDialog(this,"You want to delete this Group!");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        DaoUtil.delete(dbGroupInfo);
                        sweetAlertDialog.dismissWithAnimation();

                        Intent intent = new Intent(EditGroupActivity.this,MenuActivity.class);
                        intent.putExtra("fragment", Constant.FROM_ADDGRP);
                        startActivity(intent);
                        finishAffinity();
                    }
                });

                break;
            case R.id.rl_add_grp_back:
                onBackPressed();
                break;
            case R.id.rl_grp_save:
                if(edtGroupName.getText().toString().equals("")){
                    tiGroupName.setError("Group name is required field !");
                }else{
                    tiGroupName.setError(null);
                    List<EFDeviceOutlet> selectedDeviceList = new ArrayList<>();
                    for(int i=0;i< addedDeviceList.size();i++){
                        if(addedDeviceList.get(i).isDeviceSelected()){
                            selectedDeviceList.add(addedDeviceList.get(i));
                        }

                    }
                    if(selectedDeviceList.isEmpty() || selectedDeviceList.size() < 2){
                        Util.showMessage(EditGroupActivity.this,"Please select atleast 2 device to make a group !");
                    }else {
                        Log.e("selectedList","selected device list = "+addedDeviceList.size());
                       // DbGroupInfo dbGroupInfo = new DbGroupInfo();

                        dbGroupInfo.setDeviceList(Joiner.on("~").join(selectedDeviceList));
                        Log.e("list",Joiner.on("~").join(selectedDeviceList));
                        dbGroupInfo.setName(edtGroupName.getText().toString());
                        dbGroupInfo.setIcon(selectedImage);
                        DaoUtil.saveOrUpdate(dbGroupInfo);
                        Util.hideKeyboard(this,view);

                        Intent intent = new Intent(EditGroupActivity.this,MenuActivity.class);
                        intent.putExtra("fragment", Constant.FROM_ADDGRP);
                        startActivity(intent);
                        finishAffinity();
                    }
                }
                break;
            case R.id.iv_grp:
                final Dialog dialog = new Dialog(this);
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
                gridView.setAdapter(new ImageAdapter(this));
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        imgIcon.setImageResource(imageIDs.getResourceId(position, -1));
                        selectedImage = position;
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
        }
    }
}
