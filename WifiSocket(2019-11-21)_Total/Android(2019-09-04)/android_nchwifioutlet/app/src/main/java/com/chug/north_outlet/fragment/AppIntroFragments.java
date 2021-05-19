package com.chug.north_outlet.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chug.north_outlet.R;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AppIntroFragments extends Fragment {

    private View mView;
    private Unbinder unbinder;
    @BindView(R.id.iv_appintro)ImageView ivAppIntro;
    private int layoutResId,ivDrawable;

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private static final String ARG_LAYOUT_IV_DRAWABLE = "ivDrawable";

    public static AppIntroFragments newInstance(int layoutResId, int ivDrawable) {
        AppIntroFragments sampleSlide = new AppIntroFragments();

        Bundle bundleArgs = new Bundle();
        bundleArgs.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        bundleArgs.putInt(ARG_LAYOUT_IV_DRAWABLE, ivDrawable);
        sampleSlide.setArguments(bundleArgs);

        return sampleSlide;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID) && getArguments().containsKey(ARG_LAYOUT_IV_DRAWABLE))
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
            ivDrawable = getArguments().getInt(ARG_LAYOUT_IV_DRAWABLE);
    }



    public AppIntroFragments() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        mView = inflater.inflate(layoutResId, container, false);
        unbinder = ButterKnife.bind(this,mView);
        ivAppIntro.setImageResource(ivDrawable);
//        Picasso.with(getActivity()).load(ivDrawable).into(ivAppIntro);
        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
