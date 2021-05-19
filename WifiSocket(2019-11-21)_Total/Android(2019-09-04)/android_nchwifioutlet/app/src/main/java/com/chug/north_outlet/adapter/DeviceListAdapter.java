package com.chug.north_outlet.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chug.north_outlet.R;
import com.chug.north_outlet.bean.DbDeviceInfo;
import com.chug.north_outlet.dao.DaoUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by techno11 on 20/10/16.
 */

public class DeviceListAdapter extends BaseAdapter {

    private Context mContext;
    private List<DbDeviceInfo> dbDeviceInfos;
    private LayoutInflater mLayoutInflater;
    private TypedArray imageIDs;

    public DeviceListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageIDs = mContext.getResources().obtainTypedArray(R.array.appliancesIcon);
        readDeviceInfo();
    }

    public void readDeviceInfo() {
        dbDeviceInfos = DaoUtil.getAllList(DbDeviceInfo.class);
        if (dbDeviceInfos == null) {
            dbDeviceInfos = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return dbDeviceInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();

            view = mLayoutInflater.inflate(R.layout.row_device_list_item, viewGroup, false);
            holder.txtName = (TextView) view.findViewById(R.id.txtOnTime);
            holder.txtSubName = (TextView) view.findViewById(R.id.txtOffTime);
            holder.imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
            holder.imgStatus = (ImageView) view.findViewById(R.id.imgStatus);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final DbDeviceInfo dbDeviceInfo = dbDeviceInfos.get(position);

        holder.txtName.setText(dbDeviceInfo.getName());
        holder.txtSubName.setText(dbDeviceInfo.getSubName());
        holder.imgIcon.setImageResource(imageIDs.getResourceId(dbDeviceInfo.getIcon(), -1));

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DaoUtil.delete(dbDeviceInfo);
                deleteItem();
                Toast.makeText(mContext, "Item Deleted", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        return view;

    }

    public void deleteItem() {
        readDeviceInfo();
        notifyDataSetChanged();
    }

    private class ViewHolder {

        protected TextView txtName;
        protected TextView txtSubName;
        protected ImageView imgIcon;
        protected ImageView imgStatus;

    }

}