package com.chug.north_outlet.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.chug.north_outlet.R;


/**
 * Created by techno11 on 8/2/17.
 */

public class ImageAdapter extends BaseAdapter {
    private Context context;
    TypedArray imageIDs;

    public ImageAdapter(Context c) {
        context = c;
        imageIDs = context.getResources().obtainTypedArray(R.array.appliancesIcon);
    }

    //---returns the number of images---
    public int getCount() {
        return context.getResources().getStringArray(R.array.appliancesIcon).length;
    }

    //---returns the ID of an item---
    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    //---returns an ImageView view---
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(185, 185));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(30, 30, 30, 30);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(imageIDs.getResourceId(position, -1));
        return imageView;
    }
}
