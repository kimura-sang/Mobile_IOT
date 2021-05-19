package com.chug.north_outlet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chug.north_outlet.R;
import com.chug.north_outlet.bean.EFDeviceOutlet;

import java.util.List;

/**
 * Created by techno-110 on 18/4/17.
 */
public class ScanDeviceAdapter extends BaseAdapter {

    private List<EFDeviceOutlet> deviceOutletList;
    private Context context;
    public ScanDeviceAdapter(List<EFDeviceOutlet> deviceOutletList, Context context) {
        this.deviceOutletList = deviceOutletList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return deviceOutletList.size();
    }

    @Override
    public Object getItem(int i) {
        return deviceOutletList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater;
        ViewHolder viewHolder;
        if (view==null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.single_scan_device_item,viewGroup,false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.ivScanIcon.setImageResource(R.drawable.single_outlet_warning);
        viewHolder.tvWifiName.setText(deviceOutletList.get(i).getDeviceMac());
        return view;
    }

    private class ViewHolder{
        private ImageView ivScanIcon;
        private TextView tvWifiName;
        public ViewHolder(View view) {
            ivScanIcon = (ImageView) view.findViewById(R.id.iv_scan_device);
            tvWifiName = (TextView) view.findViewById(R.id.tv_scan_wifi_name);
            }
    }
}
