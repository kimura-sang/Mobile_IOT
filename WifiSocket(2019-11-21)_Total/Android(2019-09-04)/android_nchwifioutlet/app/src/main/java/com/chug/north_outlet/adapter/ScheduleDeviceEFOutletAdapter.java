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

public class ScheduleDeviceEFOutletAdapter extends ArrayListAdapter<EFDeviceOutlet>  {

    private Context mContext;
    private List<DbDeviceInfo> dbDeviceInfos;
    private LayoutInflater mLayoutInflater;
    ViewHolder holder;
    private TypedArray imageIDs;
    EFDeviceOutlet ds;
    boolean isEditGroupFirstTime = true;
    ListPositionCallback positionCallback;
    List<EFDeviceOutlet> groupDeviceList;
    boolean isSelected = false;

    public ScheduleDeviceEFOutletAdapter(Activity context, ListPositionCallback positionCallback) {
        super(context);
        mContext = context;
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

            view = mLayoutInflater.inflate(R.layout.row_device_grp_list_item, viewGroup, false);
            holder.ivSelectDeviceTick = (ImageView) view.findViewById(R.id.iv_select_device_tick);
            holder.txtName = (TextView) view.findViewById(R.id.txtOnTime);
            holder.txtSubName = (TextView) view.findViewById(R.id.txtOffTime);
            holder.imgIcon = (ImageView) view.findViewById(R.id.imgIcon);

            holder.ivSelectDeviceTick.setTag(position);
            Log.d("converview","from convertview ="+position);
            view.setTag(holder);

        } else {
            Log.d("converview","from outside of convertview ="+position);
            holder = (ViewHolder) view.getTag();
        }

//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
        Log.e("schedule","position = "+mList.get(position).getName()+" device selected = "+mList.get(position).isDeviceSelected()+" position = "+position);
                    if (mList.get(position).isDeviceSelected()) {
                        holder.ivSelectDeviceTick.setVisibility(View.VISIBLE);

                    } else {
                        holder.ivSelectDeviceTick.setVisibility(View.GONE);
                    }
//                }
//            });

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
