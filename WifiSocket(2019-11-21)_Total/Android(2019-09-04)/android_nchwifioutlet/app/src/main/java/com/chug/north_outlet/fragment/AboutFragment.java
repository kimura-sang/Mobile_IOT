package com.chug.north_outlet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.chug.north_outlet.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AboutFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private Unbinder unbinder;

    @BindView(R.id.tv_north_link)TextView tvNorthLink;
    @BindView(R.id.tv_email_us)TextView tvEmailUs;
    @BindView(R.id.webview)WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_about, container, false);
        unbinder = ButterKnife.bind(this,mView);

        tvNorthLink.setOnClickListener(this);
        tvEmailUs.setOnClickListener(this);
        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
       // unbinder.unbind();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.tv_north_link:
                webView.loadUrl(getActivity().getResources().getString(R.string.weblivenorth_link));
                break;
            case R.id.tv_email_us:
                Intent mailClient = new Intent();
                mailClient.setAction(Intent.ACTION_SEND);
                mailClient.setType("text/html");
               // mailClient.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                mailClient.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] {"support@weliveupnorth.com"});
              //  mailClient.setData(Uri.parse("mailto:"));
                mailClient.putExtra(Intent.EXTRA_SUBJECT, "Questions regarding North Connected Home App");
                startActivity(Intent.createChooser(mailClient,"Send Email.."));

                break;
        }
    }
}
