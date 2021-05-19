package com.chug.north_outlet.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chug.north_outlet.R;
import com.chug.north_outlet.activity.EditGroupActivity;
import com.chug.north_outlet.bean.DbGroupInfo;
import com.chug.north_outlet.bean.EFDeviceOutlet;
import com.chug.north_outlet.callback.ListPositionCallback;
import com.chug.north_outlet.dao.DaoUtil;
import com.chug.north_outlet.utils.Constant;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by techno11 on 20/10/16.
 */

public class GroupListAdapter extends BaseAdapter {

    private Context mContext;
    private List<DbGroupInfo> dbGroupInfos;
    private LayoutInflater mLayoutInflater;
    private TypedArray imageIDs;
    private boolean on_off;
    private ListPositionCallback positionCallback;

    public GroupListAdapter(Context context,ListPositionCallback positionCallback) {
        mContext = context;
        loadData();
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageIDs = mContext.getResources().obtainTypedArray(R.array.appliancesIcon);
        this.positionCallback = positionCallback;
    }


    @Override
    public int getCount() {
        return dbGroupInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return dbGroupInfos.get(i);
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

            view = mLayoutInflater.inflate(R.layout.row_device_list_item, viewGroup, false);
            holder.txtName = (TextView) view.findViewById(R.id.txtOnTime);
            holder.imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
            holder.tvFirstDevice = (TextView) view.findViewById(R.id.tv_first_device);
            holder.tvSecondDevice = (TextView) view.findViewById(R.id.tv_second_device);
            holder.tvStatus = (TextView) view.findViewById(R.id.txtSwitchStatus);
            holder.ivStatus = (ImageView) view.findViewById(R.id.imgStatus);
            holder.rlEditGroup = (RelativeLayout) view.findViewById(R.id.tv_edit_device);
            holder.rlDeviceStatus = (RelativeLayout) view.findViewById(R.id.rl_current_device_status);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final DbGroupInfo dbGroupInfo = dbGroupInfos.get(position);

        holder.txtName.setText(dbGroupInfo.getName());
        if(dbGroupInfo.getIcon()!=-1)
            holder.imgIcon.setImageResource(imageIDs.getResourceId(dbGroupInfo.getIcon(), -1));
        else
            holder.imgIcon.setImageResource(R.drawable.add_device_default);

        String str = dbGroupInfo.getDeviceList();
        final ArrayList<String> aList = new ArrayList(Arrays.asList(str.split("~")));
        Gson gson = new Gson();



        holder.tvFirstDevice.setText(gson.fromJson(aList.get(0), EFDeviceOutlet.class).getName());
        if (aList.size() > 1)
            holder.tvSecondDevice.setText(gson.fromJson(aList.get(1), EFDeviceOutlet.class).getName());
        else
            holder.tvSecondDevice.setText("");

        EFDeviceOutlet efDeviceOutlet = gson.fromJson(aList.get(0), EFDeviceOutlet.class);

        Log.e("state","state at adapter  = "+efDeviceOutlet.getDeviceState());
        switch (efDeviceOutlet.getDeviceState()) {
            case EFDeviceOutlet.STATE_ON:
                holder.ivStatus.setImageResource(R.drawable.ic_on);
               // holder.ivStatus.setColorFilter(ContextCompat.getColor(mContext, R.color.primary_dark));
                holder.tvStatus.setText("ON");
                break;
            case EFDeviceOutlet.STATE_OFF:

                holder.ivStatus.setImageResource(R.drawable.ic_off);
               // holder.ivStatus.setColorFilter(ContextCompat.getColor(mContext, R.color.gray));
                holder.tvStatus.setText("OFF");
                break;
            default:
                holder.ivStatus.setImageResource(R.drawable.ic_off);
               // holder.ivStatus.setColorFilter(ContextCompat.getColor(mContext, R.color.gray));
                holder.tvStatus.setText("");
                break;

        }

        /*if(dbGroupInfos.size()>1)
            holder.tvSecondDevice.setText(dbGroupInfos.get(1).getName());*/
        /*view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DaoUtil.delete(dbGroupInfo);
                Toast.makeText(mContext, "Item Deleted", Toast.LENGTH_LONG).show();
                notifyDataSetChanged();
                return false;
            }
        });*/

        holder.rlEditGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EditGroupActivity.class);
                intent.putExtra(Constant.GROUPOUTLET,dbGroupInfos.get(position));
                intent.putExtra(Constant.ISEDITGROUPDEVICE,true);
                mContext.startActivity(intent);
            }
        });
        holder.rlDeviceStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                positionCallback.currentPosition(position);
            }
        });
        return view;

    }

    public void loadData() {

        dbGroupInfos = DaoUtil.getAllList(DbGroupInfo.class);
        if (dbGroupInfos == null) {
            dbGroupInfos = new ArrayList<>();
        }
        Log.e("state", "state in grouplisty adapter = " + dbGroupInfos);
    }

    private class ViewHolder {
        protected TextView txtName, tvFirstDevice, tvSecondDevice, tvStatus;
        protected TextView txtSubName;
        protected ImageView imgIcon, ivStatus;
        private RelativeLayout rlEditGroup,rlDeviceStatus;

    }

}