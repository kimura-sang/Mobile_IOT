package com.chug.north_outlet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.chug.north_outlet.R;
import com.chug.north_outlet.activity.AddScheduleActivity;
import com.chug.north_outlet.activity.EditScheduleActivity;
import com.chug.north_outlet.adapter.ScheduleListAdapter;
import com.chug.north_outlet.bean.DbScheduleInfo;
import com.chug.north_outlet.dao.DaoUtil;
import com.chug.north_outlet.utils.Constant;
import com.chug.north_outlet.utils.Util;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ScheduleFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private View mView;
    private Unbinder unbinder;

    @BindView(R.id.lv_schedule)
    ListView lvSchedule;
    @BindView(R.id.btn_schedule)
    Button btnSchedule;
    ScheduleListAdapter adapter;
    private List<DbScheduleInfo> dbScheduleInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_schedule, container, false);
        unbinder = ButterKnife.bind(this, mView);
        btnSchedule.setOnClickListener(this);
        initData();
        //lvSchedule.setOnItemLongClickListener(this);
        lvSchedule.setOnItemClickListener(this);
        return mView;
    }

    private void initData(){

       dbScheduleInfo = DaoUtil.getAllList(DbScheduleInfo.class);
        if(dbScheduleInfo!=null && !dbScheduleInfo.isEmpty()){

            adapter = new ScheduleListAdapter(getActivity());
            lvSchedule.setAdapter(adapter);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), AddScheduleActivity.class);
        startActivity(intent);
    }


    /*@Override
    public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int i, long l) {

        SweetAlertDialog sweetAlertDialog = Util.createSweetDeleteDialog(getActivity(),"You want to delete this Schedule!");
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {

                DaoUtil.delete(dbScheduleInfo.get(i));
                dbScheduleInfo.remove(i);
                adapter.loadData();
                adapter.notifyDataSetChanged();
                sweetAlertDialog.dismissWithAnimation();
            }
        });

        return true;
    }*/

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent intent = new Intent(getActivity(), EditScheduleActivity.class);
        intent.putExtra(Constant.SCHEDULEOUTLET,dbScheduleInfo.get(i));
        intent.putExtra("selectedPosition",i);
        startActivity(intent);
    }
}
