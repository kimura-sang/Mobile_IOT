package com.chug.north_outlet.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chug.north_outlet.bean.EFDevice;
import com.chug.north_outlet.bean.EFDeviceOutlet;
import com.chug.north_outlet.R;


public class EFOutletAdapter extends ArrayListAdapter<EFDeviceOutlet> {

    public EFOutletAdapter(Activity c) {
        super(c);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EFDeviceOutlet ds = mList.get(position);

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.number_outlet_item, null);
        }
        TextView outletName = getAdapterView(convertView, R.id.number_outlet_tv);
        ImageView wifiIcon = getAdapterView(convertView, R.id.number_wifi_outlet_iv);
        ImageView outletIcon = getAdapterView(convertView, R.id.number_outlet_iv);



            if (ds.getDeviceType() == EFDevice.TYPE_ZIG_OUTLET) {
                wifiIcon.setVisibility(View.INVISIBLE);
            } else {
                wifiIcon.setVisibility(View.VISIBLE);
            }
            outletName.setText(ds.getDeviceMac());
            outletIcon.setImageResource(getIconRes(ds.getDeviceType(), ds.getDeviceState()));

        // 设置名字
        //String name = ds.getDeviceName() == null ? (position + 1 + "") : ds.getDeviceName();

        // 设置图标

        return convertView;
    }

    private int getIconRes(int deviceType, int deviceState) {
        int resid;
        if (deviceType == EFDevice.TYPE_WIFI_OUTLET||deviceType==EFDevice.TYPE_WIF_OUTLET_LEXIN) {
            switch (deviceState) {
                case EFDeviceOutlet.STATE_ON:
                    resid = R.drawable.single_outlet_on;
                    break;
                case EFDeviceOutlet.STATE_OFF:
                    resid = R.drawable.single_outlet_off;
                    break;
                default:
                    resid = R.drawable.single_outlet_warning;
                    break;
            }
        } else {
            switch (deviceState) {
                case EFDeviceOutlet.STATE_OFF:
                    resid = R.drawable.icon_power_strip_off;
                    break;
                case EFDevice.STATUS_UNKNOWN:
                    resid = R.drawable.icon_power_strip_unknown;
                    break;
                default:
                    resid = R.drawable.icon_power_strip;
                    break;
            }
        }
        return resid;
    }

}
