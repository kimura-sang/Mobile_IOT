package com.chug.north_outlet.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chug.north_outlet.R;
import com.chug.north_outlet.activity.AddScanDeviceActivity;
import com.chug.north_outlet.bean.DbDeviceInfo;
import com.chug.north_outlet.bean.EFDeviceOutlet;
import com.chug.north_outlet.callback.ListPositionCallback;
import com.chug.north_outlet.utils.Constant;

import java.util.List;

public class DeviceEFOutletAdapter extends ArrayListAdapter<EFDeviceOutlet>  {

    private Context mContext;
    private List<DbDeviceInfo> dbDeviceInfos;
    private LayoutInflater mLayoutInflater;
    ViewHolder holder;
    private TypedArray imageIDs;
    private boolean isGroup;
    EFDeviceOutlet ds;
    boolean isEditGroupFirstTime = true;
    ListPositionCallback positionCallback;
    List<EFDeviceOutlet> groupDeviceList;

    public DeviceEFOutletAdapter(Activity context,boolean isGroup,List<EFDeviceOutlet> groupDeviceList,ListPositionCallback positionCallback) {
        super(context);
        mContext = context;
        this.isGroup = isGroup;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageIDs = mContext.getResources().obtainTypedArray(R.array.appliancesIcon);
        this.positionCallback = positionCallback;
        this.groupDeviceList = groupDeviceList;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        ds = mList.get(position);
        if (view == null) {
            holder = new ViewHolder();

            if(!isGroup){
                view = mLayoutInflater.inflate(R.layout.row_device_list_item, viewGroup, false);
                holder.imgStatus = (ImageView) view.findViewById(R.id.imgStatus);
                holder.txtSwitchStatus = (TextView) view.findViewById(R.id.txtSwitchStatus);
                holder.tvEditDevice = (RelativeLayout) view.findViewById(R.id.tv_edit_device);
                holder.rlCurrentDeviceStatus = (RelativeLayout) view.findViewById(R.id.rl_current_device_status);
                holder.rlCurrentDeviceStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        positionCallback.currentPosition(position);
                    }
                });
                holder.tvEditDevice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, AddScanDeviceActivity.class);
                        intent.putExtra(Constant.EFDEVICEOUTLET,mList.get(position));
                        intent.putExtra(Constant.ISEDITSCANDEVICE,true);
                        mContext.startActivity(intent);
                    }
                });
            }else{
                view = mLayoutInflater.inflate(R.layout.row_device_grp_list_item, viewGroup, false);
                holder.ivSelectDeviceTick = (ImageView) view.findViewById(R.id.iv_select_device_tick);
                view.setTag(position);
            }

            holder.txtName = (TextView) view.findViewById(R.id.txtOnTime);
            holder.txtSubName = (TextView) view.findViewById(R.id.txtOffTime);
            holder.imgIcon = (ImageView) view.findViewById(R.id.imgIcon);

            Log.d("converview","from convertview ="+position);
            view.setTag(holder);

        } else {
            Log.d("converview","from outside of convertview ="+position);
            holder = (ViewHolder) view.getTag();
        }

        if(isGroup){
            if (groupDeviceList!=null && isEditGroupFirstTime){
                for(int j=0;j<mList.size();j++){
                    for (int i=0;i<groupDeviceList.size();i++){
                        if(groupDeviceList.get(i).getDeviceMac().equals(mList.get(j).getDeviceMac())){
                            mList.get(j).setDeviceSelected(true);
                            isEditGroupFirstTime = false;
                            break;
                        }
                    }
                }

            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ds = mList.get(position);
                    if(ds.isDeviceSelected()){
                        ds.setDeviceSelected(false);
                    }else {
                        ds.setDeviceSelected(true);
                    }
                    notifyDataSetChanged();
                }
            });

            ds = mList.get(position);
            if(ds.isDeviceSelected()){
                holder.ivSelectDeviceTick.setVisibility(View.VISIBLE);
            }else {
                holder.ivSelectDeviceTick.setVisibility(View.GONE);
            }
        }

        // 设置名字
        //String name = ds.getDeviceName() == null ? (position + 1 + "") : ds.getDeviceName();
        if(ds.getName()!=null && !ds.getName().isEmpty()){
            holder.txtName.setText(ds.getName());

             holder.txtSubName.setText(ds.getSubName());
        }
        else{
            holder.txtName.setText(ds.getDeviceMac());
        }

        // 设置图标

        if(ds.getIcon()!=-1)
            holder.imgIcon.setImageResource(imageIDs.getResourceId(ds.getIcon(), -1));
        else
            holder.imgIcon.setImageResource(R.drawable.add_device_default);

        if(!isGroup){

            Log.e("state","STATE = "+ds.getDeviceState()+" ds = "+ds.getName());
                switch (ds.getDeviceState()){
                    case EFDeviceOutlet.STATE_ON:
                        holder.imgStatus.setImageResource(R.drawable.ic_on);
                       // holder.imgStatus.setColorFilter(ContextCompat.getColor(mContext,R.color.primary_dark));
                        holder.txtSwitchStatus.setText("ON");
                    break;
                    case EFDeviceOutlet.STATE_OFF:
                        holder.imgStatus.setImageResource(R.drawable.ic_off);
                       // holder.imgStatus.setColorFilter(ContextCompat.getColor(mContext,R.color.gray));
                        holder.txtSwitchStatus.setText("OFF");
                        break;
                    default:
                        holder.imgStatus.setImageResource(R.drawable.ic_off);
                       // holder.imgStatus.setColorFilter(ContextCompat.getColor(mContext,R.color.gray));
                        holder.txtSwitchStatus.setText("");
                        break;

                         }
        /*if (ds.getDeviceType() == EFDevice.TYPE_WIFI_OUTLET || ds.getDeviceState() == EFDevice.TYPE_WIF_OUTLET_LEXIN) {
            switch (ds.getDeviceState()) {
                case EFDeviceOutlet.STATE_ON:
                    holder.imgStatus.setImageResource(R.drawable.power);
                    holder.txtSwitchStatus.setText("OFF");

                    break;
                case EFDeviceOutlet.STATE_OFF:
                    holder.imgStatus.setImageResource(R.drawable.power);
                    holder.imgStatus.setColorFilter(ContextCompat.getColor(mContext,R.color.primary_dark));
                    holder.txtSwitchStatus.setText("ON");
                    break;
                default:
                    holder.imgStatus.setVisibility(View.GONE);
                    holder.txtSwitchStatus.setText("");
                    break;
            }
        } else {
            switch (ds.getDeviceState()) {

                case EFDeviceOutlet.STATE_ON:
                    holder.imgStatus.setImageResource(R.drawable.power);
                    holder.imgStatus.setColorFilter(ContextCompat.getColor(mContext,R.color.gray));
                    holder.txtSwitchStatus.setText("OFF");
                    break;
                case EFDeviceOutlet.STATE_OFF:
                    holder.imgStatus.setImageResource(R.drawable.power);
                    holder.imgStatus.setColorFilter(ContextCompat.getColor(mContext,R.color.primary_dark));
                    holder.txtSwitchStatus.setText("ON");
                    break;
                default:
                    holder.imgStatus.setVisibility(View.GONE);
                    holder.txtSwitchStatus.setText("");
                    break;

                *//*case EFDeviceOutlet.STATE_OFF:
                    holder.imgStatus.setImageResource(R.drawable.power);
                    holder.txtSwitchStatus.setText("");
                    break;
                case EFDevice.STATUS_UNKNOWN:
                    //holder.imgStatus.setImageResource(R.drawable.icon_power_strip_unknown);
                    holder.txtSwitchStatus.setText("");
                    break;
                default:
                    //holder.imgStatus.setVisibility(View.GONE);
                    holder.txtSwitchStatus.setText("");
                    break;*//*
            }
        }*/

        }
        return view;
    }


    private class ViewHolder {

        protected TextView txtName;
        protected TextView txtSubName;
        protected TextView txtSwitchStatus;
        protected ImageView imgIcon;
        protected ImageView imgStatus;
        private ImageView ivSelectDeviceTick;
        private RelativeLayout tvEditDevice;
        private RelativeLayout rlCurrentDeviceStatus;

    }

}
