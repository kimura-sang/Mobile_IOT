package com.chug.north_outlet.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chug.north_outlet.R;
import com.chug.north_outlet.bean.DbScheduleInfo;
import com.chug.north_outlet.dao.DaoUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by techno11 on 20/10/16.
 */

public class ScheduleListAdapter extends BaseAdapter {

    private Context mContext;
    private List<DbScheduleInfo> dbScheduleInfos;
    private LayoutInflater mLayoutInflater;
    private TypedArray imageIDs;

    public ScheduleListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageIDs = mContext.getResources().obtainTypedArray(R.array.appliancesIcon);
        loadData();
    }

    public void loadData() {
        dbScheduleInfos = DaoUtil.getAllList(DbScheduleInfo.class);
        if (dbScheduleInfos == null) {
            dbScheduleInfos = new ArrayList<>();
        }
    }


    @Override
    public int getCount() {
        return dbScheduleInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return dbScheduleInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();

            view = mLayoutInflater.inflate(R.layout.row_device_list_schedule_item, viewGroup, false);
            holder.txtName = (TextView) view.findViewById(R.id.txtOnTime);
            holder.txtSubName = (TextView) view.findViewById(R.id.txtOffTime);
            holder.imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
            holder.tvOnTime = (TextView) view.findViewById(R.id.tv_schedule_on_time);
            holder.tvOffTime = (TextView) view.findViewById(R.id.tv_schedule_off_time);
            holder.ivOnStatus = (ImageView) view.findViewById(R.id.iv_schedule_on_status);
            holder.ivOffStatus = (ImageView) view.findViewById(R.id.iv_schedule_off_status);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final DbScheduleInfo dbScheduleInfo = dbScheduleInfos.get(position);

        holder.txtName.setText(dbScheduleInfo.getName());
        holder.txtSubName.setText(dbScheduleInfo.getSubName());

        if(dbScheduleInfo.getIcon()!=-1)
            holder.imgIcon.setImageResource(imageIDs.getResourceId(dbScheduleInfo.getIcon(), -1));
        else
            holder.imgIcon.setImageResource(R.drawable.add_device_default);

        if(dbScheduleInfo.getOnTime()!=null && !dbScheduleInfo.getOnTime().equals(""))
            holder.ivOnStatus.setVisibility(View.VISIBLE);
        else
            holder.ivOnStatus.setVisibility(View.GONE);

        if(dbScheduleInfo.getOffTime()!=null && !dbScheduleInfo.getOffTime().equals(""))
            holder.ivOffStatus.setVisibility(View.VISIBLE);
        else
            holder.ivOffStatus.setVisibility(View.GONE);

        holder.tvOnTime.setText(dbScheduleInfo.getOnTime());
        holder.tvOffTime.setText(dbScheduleInfo.getOffTime());

        return view;
    }

    private class ViewHolder {

        protected TextView txtName,tvOnTime,tvOffTime;
        protected TextView txtSubName;
        protected ImageView imgIcon,ivOnStatus,ivOffStatus;

    }

}