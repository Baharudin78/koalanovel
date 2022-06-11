package com.divinetechs.ebooksapp.Activity;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.PrefManager;
import com.divinetechs.ebooksapp.Utility.Utils;
import com.google.android.gms.ads.AdView;

public class Privacypolicy extends AppCompatActivity {
    PrefManager prefManager;

    LinearLayout lyToolbar, lyBack, lyFbAdView;
    TextView txtToolbarTitle, txtBack, txt_privacy_policy;
    RelativeLayout rlAdView;

    private com.facebook.ads.AdView fbAdView = null;
    private AdView mAdView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.screenCapOff(Privacypolicy.this);
        if (PrefManager.getInstance(this).isNightModeEnabled() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.privacypolicy);

        PrefManager.forceRTLIfSupported(getWindow(), Privacypolicy.this);
        prefManager = new PrefManager(Privacypolicy.this);

        txt_privacy_policy = findViewById(R.id.privacy_policy);
        lyToolbar = findViewById(R.id.lyToolbar);
        lyToolbar.setVisibility(View.VISIBLE);
        lyBack = findViewById(R.id.lyBack);
        txtBack = findViewById(R.id.txtBack);
        txtToolbarTitle = findViewById(R.id.txtToolbarTitle);
        rlAdView = findViewById(R.id.rlAdView);
        lyFbAdView = findViewById(R.id.lyFbAdView);

        Log.e("privacy_policy", "" + prefManager.getValue("privacy_policy"));

        txtToolbarTitle.setText("" + getResources().getString(R.string.Privacy_policy));
        txt_privacy_policy.setText(Html.fromHtml(prefManager.getValue("privacy_policy")));

        lyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        AdInit();

    }

    private void AdInit() {
        Log.e("banner_ad", "" + prefManager.getValue("banner_ad"));
        if (prefManager.getValue("banner_ad").equalsIgnoreCase("yes")) {
            rlAdView.setVisibility(View.VISIBLE);
            Utils.Admob(Privacypolicy.this, mAdView, prefManager.getValue("banner_adid"), rlAdView);
        } else {
            rlAdView.setVisibility(View.GONE);
        }

        Log.e("fb_banner_status", "" + prefManager.getValue("fb_banner_status"));
        if (prefManager.getValue("fb_banner_status").equalsIgnoreCase("on")) {
            lyFbAdView.setVisibility(View.VISIBLE);
            Utils.FacebookBannerAd(Privacypolicy.this, fbAdView, "" + prefManager.getValue("fb_banner_id"), lyFbAdView);
        } else {
            lyFbAdView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (fbAdView != null) {
            fbAdView.destroy();
        }
    }

}
