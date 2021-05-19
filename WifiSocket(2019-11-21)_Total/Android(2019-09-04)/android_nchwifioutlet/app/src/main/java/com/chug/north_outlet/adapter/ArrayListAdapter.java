package com.chug.north_outlet.adapter;

import android.app.Activity;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import java.util.List;

public abstract class ArrayListAdapter<T> extends BaseAdapter {

	protected Activity mContext;
	protected List<T> mList;
	protected int mCount;

	public ArrayListAdapter(Activity c) {
		this.mContext = c;
	}

	@SuppressWarnings("unchecked")
	public void setList(List<? extends T> list) {
		mList = (List<T>) list;
		notifyDataSetChanged();
	}

	public void addItem(T t) {
		mList.add(t);
		notifyDataSetChanged();
	}

	public void addItems(List<T> list) {
		for (T t : list) {
			mList.add(t);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		mCount = (mList != null) ? mList.size() : 0;
		return mCount;
	}

	@Override
	public T getItem(int position) {
		return (mList != null) ? mList.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	abstract public View getView(int position, View convertView, ViewGroup parent);
	
	@SuppressWarnings({ "unchecked", "hiding" })
	protected <T extends View> T getAdapterView(View convertView, int id) {
		SparseArray<View> viewHolder = (SparseArray<View>) convertView.getTag();
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			convertView.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = convertView.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}

	public List<T> getList() {
		return mList;
	}
	
	/** 删除单个 */
	public void remove(int position) {
		mList.remove(position);
		notifyDataSetChanged();
	}
	
	/** 删除单个 */
	public void remove(T t) {
		mList.remove(t);
		notifyDataSetChanged();
	}
	
	/**
	 * 删除所有
	 */
	public void clearItem() {
		mList.clear();
		notifyDataSetChanged();
	}
}
