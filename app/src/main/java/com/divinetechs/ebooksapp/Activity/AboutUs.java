package com.divinetechs.ebooksapp.Activity;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.PrefManager;
import com.divinetechs.ebooksapp.Utility.Utils;
import com.divinetechs.ebooksapp.Webservice.BaseURL;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

public class AboutUs extends AppCompatActivity {

    TextView txtToolbarTitle, txtAboutUs, txtAppName, txtCompany, txtEmail, txtWebsite, txtMobile;
    LinearLayout lyToolbar, lyBack, lyFbAdView;

    PrefManager prefManager;

    ImageView ivAppIcon;

    RelativeLayout rlAdView;

    private com.facebook.ads.AdView fbAdView = null;
    private AdView mAdView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.screenCapOff(AboutUs.this);
        if (PrefManager.getInstance(this).isNightModeEnabled() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.about_us);
        PrefManager.forceRTLIfSupported(getWindow(), AboutUs.this);
        prefManager = new PrefManager(AboutUs.this);

        lyToolbar = findViewById(R.id.lyToolbar);
        lyBack = findViewById(R.id.lyBack);
        txtToolbarTitle = findViewById(R.id.txtToolbarTitle);
        ivAppIcon = findViewById(R.id.ivAppIcon);
        rlAdView = findViewById(R.id.rlAdView);
        lyFbAdView = findViewById(R.id.lyFbAdView);
        txtAppName = findViewById(R.id.txtAppName);
        txtCompany = findViewById(R.id.txtCompany);
        txtEmail = findViewById(R.id.txtEmail);
        txtWebsite = findViewById(R.id.txtWebsite);
        txtMobile = findViewById(R.id.txtMobile);
        txtAboutUs = findViewById(R.id.txtAboutUs);

        txtToolbarTitle.setText(getResources().getString(R.string.about_us));
        txtAppName.setText(Html.fromHtml(prefManager.getValue("app_name")));
        txtCompany.setText(prefManager.getValue("Author"));
        txtEmail.setText(prefManager.getValue("host_email"));
        txtWebsite.setText(prefManager.getValue("website"));
        txtMobile.setText(prefManager.getValue("contact"));
        txtAboutUs.setText(Html.fromHtml(prefManager.getValue("app_desripation")));
        Picasso.get().load(BaseURL.Image_URL + "" + prefManager.getValue("app_logo")).into(ivAppIcon);

        AdInit();

        lyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void AdInit() {
        Log.e("banner_ad", "" + prefManager.getValue("banner_ad"));
        if (prefManager.getValue("banner_ad").equalsIgnoreCase("yes")) {
            rlAdView.setVisibility(View.VISIBLE);
            Utils.Admob(AboutUs.this, mAdView, prefManager.getValue("banner_adid"), rlAdView);
        } else {
            rlAdView.setVisibility(View.GONE);
        }

        Log.e("fb_banner_status", "" + prefManager.getValue("fb_banner_status"));
        if (prefManager.getValue("fb_banner_status").equalsIgnoreCase("on")) {
            lyFbAdView.setVisibility(View.VISIBLE);
            Utils.FacebookBannerAd(AboutUs.this, fbAdView, "" + prefManager.getValue("fb_banner_id"), lyFbAdView);
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
