package com.divinetechs.ebooksapp.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.divinetechs.ebooksapp.Activity.AboutUs;
import com.divinetechs.ebooksapp.Activity.EarnRewards;
import com.divinetechs.ebooksapp.Activity.LoginActivity;
import com.divinetechs.ebooksapp.Activity.MainActivity;
import com.divinetechs.ebooksapp.Activity.MyDownloadedItems;
import com.divinetechs.ebooksapp.Activity.MyPurchase;
import com.divinetechs.ebooksapp.Activity.Notifications;
import com.divinetechs.ebooksapp.Activity.Package;
import com.divinetechs.ebooksapp.Activity.PrimeMembership;
import com.divinetechs.ebooksapp.Activity.Privacypolicy;
import com.divinetechs.ebooksapp.Activity.Profile;
import com.divinetechs.ebooksapp.Activity.VouchersHistory;
import com.divinetechs.ebooksapp.Adapter.BannerAdapter;
import com.divinetechs.ebooksapp.BuildConfig;
import com.divinetechs.ebooksapp.Model.BannerModel.BannerModel;
import com.divinetechs.ebooksapp.Model.BannerModel.Result;
import com.divinetechs.ebooksapp.Model.NotificationModel.NotificationModel;
import com.divinetechs.ebooksapp.Model.ProfileModel.ProfileModel;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.Constant;
import com.divinetechs.ebooksapp.Utility.LocaleUtils;
import com.divinetechs.ebooksapp.Utility.PrefManager;
import com.divinetechs.ebooksapp.Utility.Utils;
import com.divinetechs.ebooksapp.Webservice.AppAPI;
import com.divinetechs.ebooksapp.Webservice.BaseURL;
import com.facebook.ads.Ad;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeBannerAd;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
import com.facebook.login.LoginManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.kenilt.loopingviewpager.widget.LoopingViewPager;

import com.makeramen.roundedimageview.RoundedImageView;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Settings extends Fragment implements View.OnClickListener {

    PrefManager prefManager;
    ShimmerFrameLayout shimmer;
    View root;

    List<Result> bannerList;
    BannerAdapter bannerAdapter;

    RelativeLayout ryBanner;
    LoopingViewPager viewPager;
    private DotsIndicator dotsIndicator;
    Timer swipeTimer;
    TimerTask timerTask;

    LinearLayout ly_profile, lyNotification, ly_my_library, ly_my_transacation, ly_prime, ly_my_voucher, ly_package, ly_clear_cache,
            ly_privacy_policy, ly_about_us, ly_share_app, ly_rate_app, ly_login, ly_earn_reward, ly_vouchers, ly_gems;
    RoundedImageView riv_user;
    SwitchCompat switch_push, switch_theme;
    Spinner spinner;
    TextView txt_login, txt_user_name, txt_coins, txt_vouchers, txtNotifyCount;

    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;

    private InterstitialAd mInterstitialAd = null;
    private RewardedAd mRewardedAd = null;
    private RewardedInterstitialAd mRewardedInterstitialAd = null;
    private com.facebook.ads.InterstitialAd fbInterstitialAd = null;
    private RewardedVideoAd fbRewardedVideoAd = null;
    private TemplateView nativeTemplate = null;
    private NativeBannerAd fbNativeBannerAd = null;
    private NativeAdLayout fbNativeTemplate = null;

    String currentLanguage = "en", TYPE = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (PrefManager.getInstance(getActivity()).isNightModeEnabled() == true) {
            getActivity().setTheme(R.style.darktheme);
        } else {
            getActivity().setTheme(R.style.AppTheme);
        }
        root = inflater.inflate(R.layout.settings, container, false);
        PrefManager.forceRTLIfSupported(getActivity().getWindow(), getActivity());
        prefManager = new PrefManager(getActivity());

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        init();

        if (prefManager.getValue("reward_ad").equalsIgnoreCase("yes")
                || prefManager.getValue("fb_rewardvideo_status").equalsIgnoreCase("on")
                || prefManager.getValue("fb_interstiatial_status").equalsIgnoreCase("on")
                || prefManager.getValue("interstital_ad").equalsIgnoreCase("yes")) {
            ly_earn_reward.setVisibility(View.VISIBLE);
        } else {
            ly_earn_reward.setVisibility(View.GONE);
        }

        if (prefManager.getBool("PUSH")) {
            switch_push.setChecked(true);
        } else {
            switch_push.setChecked(false);
        }

        switch_push.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    OneSignal.setSubscription(true);
                } else {
                    OneSignal.setSubscription(false);
                }
                prefManager.setBool("PUSH", isChecked);
            }
        });

        if (PrefManager.getInstance(getActivity()).isNightModeEnabled() == true) {
            switch_theme.setChecked(true);
        }

        switch_theme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    prefManager.setIsNightModeEnabled(true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Intent i = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } else {
                    prefManager.setIsNightModeEnabled(false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    Intent i = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                getActivity().finish();
            }
        });

        spinner_onclick();
        currentLanguage = LocaleUtils.getSelectedLanguageId();
        Log.e("currentLanguage", "" + currentLanguage);

        //Advertisement
        Log.e("native_ad", "" + prefManager.getValue("native_ad"));
        Log.e("fb_native_status", "" + prefManager.getValue("fb_native_status"));
        if (prefManager.getValue("native_ad").equalsIgnoreCase("yes")) {
            fbNativeTemplate.setVisibility(View.GONE);
            nativeTemplate.setVisibility(View.VISIBLE);
            Utils.NativeAds(getActivity(), nativeTemplate, "" + prefManager.getValue("native_adid"));
        } else if (prefManager.getValue("fb_native_status").equalsIgnoreCase("on")) {
            nativeTemplate.setVisibility(View.GONE);
            fbNativeTemplate.setVisibility(View.VISIBLE);
            Utils.FacebookNativeAdSmall(getActivity(), fbNativeBannerAd, fbNativeTemplate, "" + prefManager.getValue("fb_native_id"));
        } else {
            fbNativeTemplate.setVisibility(View.GONE);
            nativeTemplate.setVisibility(View.GONE);
            ryBanner.setVisibility(View.VISIBLE);
            DisplayBanner();
        }

        return root;
    }

    private void init() {
        try {
            shimmer = root.findViewById(R.id.shimmer);

            MainActivity.appbar.setVisibility(View.GONE);

            spinner = root.findViewById(R.id.spinner);
            switch_push = root.findViewById(R.id.switch_push);
            switch_theme = root.findViewById(R.id.switch_theme);

            fbNativeTemplate = root.findViewById(R.id.fbNativeTemplate);
            nativeTemplate = root.findViewById(R.id.nativeTemplate);
            ryBanner = root.findViewById(R.id.ryBanner);
            viewPager = root.findViewById(R.id.viewPager);
            dotsIndicator = root.findViewById(R.id.dotsIndicator);

            riv_user = root.findViewById(R.id.riv_user);

            txt_login = root.findViewById(R.id.txt_login);
            txt_user_name = root.findViewById(R.id.txt_user_name);
            txt_coins = root.findViewById(R.id.txt_coins);
            txt_vouchers = root.findViewById(R.id.txt_vouchers);
            txtNotifyCount = root.findViewById(R.id.txtNotifyCount);

            lyNotification = root.findViewById(R.id.lyNotification);
            ly_profile = root.findViewById(R.id.ly_profile);
            ly_prime = root.findViewById(R.id.ly_prime);
            ly_my_library = root.findViewById(R.id.ly_my_library);
            ly_my_transacation = root.findViewById(R.id.ly_my_transacation);
            ly_my_voucher = root.findViewById(R.id.ly_my_voucher);
            ly_gems = root.findViewById(R.id.ly_gems);
            ly_vouchers = root.findViewById(R.id.ly_vouchers);
            ly_package = root.findViewById(R.id.ly_package);
            ly_clear_cache = root.findViewById(R.id.ly_clear_cache);
            ly_about_us = root.findViewById(R.id.ly_about_us);
            ly_share_app = root.findViewById(R.id.ly_share_app);
            ly_rate_app = root.findViewById(R.id.ly_rate_app);
            ly_login = root.findViewById(R.id.ly_login);
            ly_privacy_policy = root.findViewById(R.id.ly_privacy_policy);
            ly_earn_reward = root.findViewById(R.id.ly_earn_reward);

            lyNotification.setOnClickListener(this);
            ly_login.setOnClickListener(this);
            ly_profile.setOnClickListener(this);
            ly_prime.setOnClickListener(this);
            ly_my_library.setOnClickListener(this);
            ly_my_transacation.setOnClickListener(this);
            ly_my_voucher.setOnClickListener(this);
            ly_gems.setOnClickListener(this);
            ly_vouchers.setOnClickListener(this);
            ly_package.setOnClickListener(this);
            ly_clear_cache.setOnClickListener(this);
            ly_about_us.setOnClickListener(this);
            ly_share_app.setOnClickListener(this);
            ly_rate_app.setOnClickListener(this);
            ly_privacy_policy.setOnClickListener(this);
            ly_earn_reward.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    private void AdInit() {
        TYPE = "";
        Log.e("reward_ad", "" + prefManager.getValue("reward_ad"));
        if (prefManager.getValue("reward_ad").equalsIgnoreCase("yes")) {
            mRewardedAd = null;
            RewardedVideoAd();
        }

        Log.e("fb_rewardvideo_status", "" + prefManager.getValue("fb_rewardvideo_status"));
        if (prefManager.getValue("fb_rewardvideo_status").equalsIgnoreCase("on")) {
            fbRewardedVideoAd = null;
            FacebookRewardAd();
        }

        Log.e("interstital_ad", "" + prefManager.getValue("interstital_ad"));
        if (prefManager.getValue("interstital_ad").equalsIgnoreCase("yes")) {
            mInterstitialAd = null;
            InterstitialAd();
        }

        Log.e("fb_interstiatial_status", "" + prefManager.getValue("fb_interstiatial_status"));
        if (prefManager.getValue("fb_interstiatial_status").equalsIgnoreCase("on")) {
            fbInterstitialAd = null;
            FacebookInterstitialAd();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("onStart", "Called");
        if (!prefManager.getLoginId().equalsIgnoreCase("0")) {
            txt_login.setText(getActivity().getResources().getString(R.string.logout));
            GetProfile();
            GetNotification();
        } else {
            txt_login.setText(getActivity().getResources().getString(R.string.login));
            txt_user_name.setText("" + getResources().getString(R.string.guest_user));
            txt_coins.setText("0");
            txt_vouchers.setText("0");
            txtNotifyCount.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_no_user).placeholder((R.drawable.ic_no_user)).into(riv_user);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyNotification:
                if (Utils.checkLoginUser(getActivity())) {
                    startActivity(new Intent(getActivity(), Notifications.class));
                }
                break;

            case R.id.ly_login:
                ShowAdByClick("LOGOUT");
                break;

            case R.id.ly_rate_app:
                ShowAdByClick("RATEAPP");
                break;

            case R.id.ly_share_app:
                ShowAdByClick("SHAREAPP");
                break;

            case R.id.ly_about_us:
                ShowAdByClick("ABOUTUS");
                break;

            case R.id.ly_privacy_policy:
                ShowAdByClick("PRIVACY");
                break;

            case R.id.ly_clear_cache:
                String root = getActivity().getExternalCacheDir().getAbsolutePath();
                Log.e("cache root =>", "" + root);
                File file = new File(root);

                if (file != null && file.isDirectory()) {
                    String[] children = file.list();
                    for (String aChildren : children) {
                        new File(file, aChildren).delete();
                    }
                    Toasty.success(getActivity(), "" + getResources().getString(R.string.locally_cached_data), Toasty.LENGTH_SHORT).show();
                } else if (file != null && file.isFile()) {
                    file.delete();
                    Toasty.success(getActivity(), "" + getResources().getString(R.string.locally_cached_data), Toasty.LENGTH_SHORT).show();
                }
                break;

            case R.id.ly_package:
                if (Utils.checkLoginUser(getActivity())) {
                    if (Utils.checkMissingData(getActivity(), "" + prefManager.getValue("userType"))) {
                        Package();
                    } else {
                        Utils.getMissingDataFromUser(getActivity(), "" + prefManager.getValue("userType"));
                    }
                }
                break;

            case R.id.ly_earn_reward:
                if (Utils.checkLoginUser(getActivity())) {
                    startActivity(new Intent(getActivity(), EarnRewards.class));
                }
                break;

            case R.id.ly_prime:
                if (Utils.checkLoginUser(getActivity())) {
                    startActivity(new Intent(getActivity(), PrimeMembership.class));
                }
                break;

            case R.id.ly_my_voucher:
            case R.id.ly_vouchers:
            case R.id.ly_gems:
                if (Utils.checkLoginUser(getActivity())) {
                    startActivity(new Intent(getActivity(), VouchersHistory.class));
                }
                break;

            case R.id.ly_my_transacation:
                if (Utils.checkLoginUser(getActivity())) {
                    Intent intent = new Intent(getActivity(), MyPurchase.class);
                    intent.putExtra("page", "TRANSACTION");
                    startActivity(intent);
                }
                break;

            case R.id.ly_my_library:
                if (Utils.checkLoginUser(getActivity())) {
                    Intent intent = new Intent(getActivity(), MyDownloadedItems.class);
                    intent.putExtra("page", "LIBRARY");
                    startActivity(intent);
                }
                break;

            case R.id.ly_profile:
                ShowAdByClick("USERPROFILE");
                break;
        }
    }

    private void GetProfile() {
        Utils.shimmerShow(shimmer);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<ProfileModel> call = bookNPlayAPI.profile("" + prefManager.getLoginId());
        call.enqueue(new Callback<ProfileModel>() {
            @Override
            public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        txt_user_name.setText(response.body().getResult().get(0).getFullname());
                        txt_coins.setText(response.body().getResult().get(0).getCoinBalance());
                        txt_vouchers.setText("" + response.body().getResult().get(0).getVoucherBalance());

                        if (!response.body().getResult().get(0).getImage().isEmpty()) {
                            Picasso.get().load("" + response.body().getResult().get(0).getImage())
                                    .placeholder(R.drawable.ic_no_user)
                                    .into(riv_user);
                        }
                        Utils.storeUserCred(getActivity(),
                                "" + response.body().getResult().get(0).getId(),
                                "" + response.body().getResult().get(0).getType(),
                                "" + response.body().getResult().get(0).getEmail(),
                                "" + response.body().getResult().get(0).getFullname(),
                                "" + response.body().getResult().get(0).getMobile());

                    }
                } catch (Exception e) {
                    Log.e("profile", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<ProfileModel> call, Throwable t) {
                Log.e("profile", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
            }
        });
    }

    //get_notification API
    private void GetNotification() {
        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<NotificationModel> call = bookNPlayAPI.get_notification("" + prefManager.getLoginId());
        call.enqueue(new Callback<NotificationModel>() {
            @Override
            public void onResponse(@NonNull Call<NotificationModel> call, @NonNull Response<NotificationModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Log.e("get_notification", "status => " + response.body().getStatus());

                        if (response.body().getResult().size() > 0) {
                            txtNotifyCount.setVisibility(View.VISIBLE);
                            txtNotifyCount.setText("" + response.body().getResult().size());
                        } else {
                            txtNotifyCount.setVisibility(View.GONE);
                        }

                    } else {
                        Log.e("get_notification", "message => " + response.body().getStatus());
                        txtNotifyCount.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e("get_notification", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationModel> call, @NonNull Throwable t) {
                Log.e("get_notification", "That didn't work!!! => " + t.getMessage());
                txtNotifyCount.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        TYPE = "";
        AdInit();
    }

    private void DisplayBanner() {
        Utils.shimmerShow(shimmer);
        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<BannerModel> call = bookNPlayAPI.get_ads_banner();
        call.enqueue(new Callback<BannerModel>() {
            @Override
            public void onResponse(Call<BannerModel> call, Response<BannerModel> response) {
                Utils.shimmerHide(shimmer);
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult().size() > 0) {
                            bannerList = new ArrayList<>();
                            bannerList = response.body().getResult();
                            Log.e("bannerList", "" + bannerList.size());
                            SetBanner();
                            ryBanner.setVisibility(View.VISIBLE);
                        } else {
                            ryBanner.setVisibility(View.GONE);
                        }

                    } else {
                        ryBanner.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e("get_ads_banner", "Exception =>" + e);
                }
            }

            @Override
            public void onFailure(Call<BannerModel> call, Throwable t) {
                Log.e("get_ads_banner", "Throwable =>" + t.getMessage());
                ryBanner.setVisibility(View.GONE);
                Utils.shimmerHide(shimmer);
            }
        });

    }

    private void SetBanner() {
        bannerAdapter = new BannerAdapter(getActivity(), bannerList);
        viewPager.setAdapter(bannerAdapter, getChildFragmentManager());
        bannerAdapter.notifyDataSetChanged();
        dotsIndicator.setViewPager(viewPager);

        if (bannerList.size() > 0) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    viewPager.post(new Runnable() {
                        @Override
                        public void run() {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        }
                    });
                }
            };
            swipeTimer = new Timer();
            swipeTimer.schedule(timerTask, 5000, 5000);
        }
    }

    private void spinner_onclick() {
        List<String> list = new ArrayList<String>();
        list.add("English");
        list.add("عربى");
        list.add("français");
        list.add("हिंदी");
        list.add("Kiswahili");
        list.add("தமிழ்");
        list.add("తెలుగు");
        list.add("Zulu");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, list);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (LocaleUtils.getSelectedLanguageId().equalsIgnoreCase("en")) {
            Log.e("selected_eng", "english");
            spinner.setSelection(0);
        }
        if (LocaleUtils.getSelectedLanguageId().equalsIgnoreCase("ar")) {
            Log.e("select_Arabic", "Arabic");
            spinner.setSelection(1);
        }
        if (LocaleUtils.getSelectedLanguageId().equalsIgnoreCase("fr")) {
            Log.e("select_Franch", "fr");
            spinner.setSelection(2);
        }
        if (LocaleUtils.getSelectedLanguageId().equalsIgnoreCase("hi")) {
            Log.e("select_Hindi", "hi");
            spinner.setSelection(3);
        }
        if (LocaleUtils.getSelectedLanguageId().equalsIgnoreCase("sw")) {
            Log.e("select_Swahili", "sw");
            spinner.setSelection(4);
        }
        if (LocaleUtils.getSelectedLanguageId().equalsIgnoreCase("ta")) {
            Log.e("select_Tamil", "ta");
            spinner.setSelection(5);
        }
        if (LocaleUtils.getSelectedLanguageId().equalsIgnoreCase("te")) {
            Log.e("select_Telugu", "te");
            spinner.setSelection(6);
        }
        if (LocaleUtils.getSelectedLanguageId().equalsIgnoreCase("zu")) {
            Log.e("select_Zulu", "zu");
            spinner.setSelection(7);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                Log.e("pos", "" + position);
                switch (position) {
                    case 0:
                        setLocale("en");
                        break;
                    case 1:
                        setLocale("ar");
                        break;
                    case 2:
                        setLocale("fr");
                        break;
                    case 3:
                        setLocale("hi");
                        break;
                    case 4:
                        setLocale("sw");
                        break;
                    case 5:
                        setLocale("ta");
                        break;
                    case 6:
                        setLocale("te");
                        break;
                    case 7:
                        setLocale("zu");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setLocale(String localeName) {
        try {
            Log.e("lan_name", "" + localeName);
            Log.e("currentLanguage2", "" + currentLanguage);
            if (!localeName.equals(currentLanguage)) {
                LocaleUtils.setSelectedLanguageId(localeName);
                Intent i = getActivity().getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                getActivity().finish();
            } else {
//                Toast.makeText(getActivity(), "Language already selected!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("error_msg", "" + e.getMessage());
        }
    }

    private void rateMe() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
        }
    }

    private void ShareMe() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "" + getResources().getString(R.string.app_name));
            String shareMessage = "\n" + getResources().getString(R.string.let_me_recommend_you_this_application) + "\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "" + getResources().getString(R.string.share_with)));
        } catch (Exception e) {
            //e.toString();
        }
    }

    private void logout() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.exit_logout_dialog, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setElevation(100);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        RoundedImageView rivDialog = popupView.findViewById(R.id.rivDialog);
        rivDialog.setImageResource(R.drawable.app_icon);

        TextView txtTitle = popupView.findViewById(R.id.txtTitle);
        TextView txtDescription = popupView.findViewById(R.id.txtDescription);
        Button btnNegative = popupView.findViewById(R.id.btnNegative);
        Button btnPositive = popupView.findViewById(R.id.btnPositive);

        txtTitle.setText(getResources().getString(R.string.app_name));
        txtDescription.setText("" + getResources().getString(R.string.are_you_sure_you_want_to_logout));

        btnPositive.setText("" + getResources().getString(R.string.logout));
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                Utils.clearPrefManager(getActivity());
                LoginManager.getInstance().logOut();
                mGoogleSignInClient.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        btnNegative.setText("" + getResources().getString(R.string.cancel));
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    public void Package() {
        Package openBottomSheet = Package.newInstance();
        openBottomSheet.show(getChildFragmentManager(), "Subscription.TAG");
    }

    //Showing ad by TYPE
    private void ShowAdByClick(String Type) {
        TYPE = Type;
        Log.e("=>TYPE", "" + TYPE);

        if (prefManager.getValue("reward_ad").equalsIgnoreCase("yes")) {
            if (mRewardedAd != null) {
                mRewardedAd.show(getActivity(), new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        Log.e("RewardItem amount =>", "" + rewardItem.getAmount());
                    }
                });
            } else {
                Log.e("mRewardedAd=>", "The ad wasn't ready yet.");
                if (TYPE.equalsIgnoreCase("LOGOUT")) {
                    if (Utils.checkLoginUser(getActivity())) {
                        logout();
                    }
                } else if (TYPE.equalsIgnoreCase("SHAREAPP")) {
                    ShareMe();
                } else if (TYPE.equalsIgnoreCase("RATEAPP")) {
                    rateMe();
                } else if (TYPE.equalsIgnoreCase("PRIVACY")) {
                    Intent intentPrivacy = new Intent(getActivity(), Privacypolicy.class);
                    intentPrivacy.putExtra("type", "Privacy Policy");
                    startActivity(intentPrivacy);
                } else if (TYPE.equalsIgnoreCase("ABOUTUS")) {
                    startActivity(new Intent(getActivity(), AboutUs.class));
                } else if (TYPE.equalsIgnoreCase("USERPROFILE")) {
                    if (Utils.checkLoginUser(getActivity())) {
                        Constant.isSelectPic = false;
                        Intent intent = new Intent(getActivity(), Profile.class);
                        intent.putExtra("from", "Profile");
                        startActivity(intent);
                    }
                }
            }

        } else if (prefManager.getValue("fb_rewardvideo_status").equalsIgnoreCase("on")) {
            if (fbRewardedVideoAd != null && fbRewardedVideoAd.isAdLoaded()) {
                fbRewardedVideoAd.show();
            } else {
                Log.e("fbRewardedVideoAd=>", "The ad wasn't ready yet.");
                if (TYPE.equalsIgnoreCase("LOGOUT")) {
                    if (Utils.checkLoginUser(getActivity())) {
                        logout();
                    }
                } else if (TYPE.equalsIgnoreCase("SHAREAPP")) {
                    ShareMe();
                } else if (TYPE.equalsIgnoreCase("RATEAPP")) {
                    rateMe();
                } else if (TYPE.equalsIgnoreCase("PRIVACY")) {
                    Intent intentPrivacy = new Intent(getActivity(), Privacypolicy.class);
                    startActivity(intentPrivacy);
                } else if (TYPE.equalsIgnoreCase("ABOUTUS")) {
                    startActivity(new Intent(getActivity(), AboutUs.class));
                } else if (TYPE.equalsIgnoreCase("USERPROFILE")) {
                    if (Utils.checkLoginUser(getActivity())) {
                        Constant.isSelectPic = false;
                        Intent intent = new Intent(getActivity(), Profile.class);
                        intent.putExtra("from", "Profile");
                        startActivity(intent);
                    }
                }
            }

        } else if (prefManager.getValue("fb_interstiatial_status").equalsIgnoreCase("on")) {
            if (fbInterstitialAd != null && fbInterstitialAd.isAdLoaded()) {
                fbInterstitialAd.show();
            } else {
                Log.e("fbInterstitialAd=>", "The ad wasn't ready yet.");
                if (TYPE.equalsIgnoreCase("LOGOUT")) {
                    if (Utils.checkLoginUser(getActivity())) {
                        logout();
                    }
                } else if (TYPE.equalsIgnoreCase("SHAREAPP")) {
                    ShareMe();
                } else if (TYPE.equalsIgnoreCase("RATEAPP")) {
                    rateMe();
                } else if (TYPE.equalsIgnoreCase("PRIVACY")) {
                    Intent intentPrivacy = new Intent(getActivity(), Privacypolicy.class);
                    startActivity(intentPrivacy);
                } else if (TYPE.equalsIgnoreCase("ABOUTUS")) {
                    startActivity(new Intent(getActivity(), AboutUs.class));
                } else if (TYPE.equalsIgnoreCase("USERPROFILE")) {
                    if (Utils.checkLoginUser(getActivity())) {
                        Constant.isSelectPic = false;
                        Intent intent = new Intent(getActivity(), Profile.class);
                        intent.putExtra("from", "Profile");
                        startActivity(intent);
                    }
                }
            }

        } else {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(getActivity());
            } else {
                Log.e("mInterstitialAd=>", "The ad wasn't ready yet.");
                if (TYPE.equalsIgnoreCase("LOGOUT")) {
                    if (Utils.checkLoginUser(getActivity())) {
                        logout();
                    }
                } else if (TYPE.equalsIgnoreCase("SHAREAPP")) {
                    ShareMe();
                } else if (TYPE.equalsIgnoreCase("RATEAPP")) {
                    rateMe();
                } else if (TYPE.equalsIgnoreCase("PRIVACY")) {
                    Intent intentPrivacy = new Intent(getActivity(), Privacypolicy.class);
                    startActivity(intentPrivacy);
                } else if (TYPE.equalsIgnoreCase("ABOUTUS")) {
                    startActivity(new Intent(getActivity(), AboutUs.class));
                } else if (TYPE.equalsIgnoreCase("USERPROFILE")) {
                    if (Utils.checkLoginUser(getActivity())) {
                        Constant.isSelectPic = false;
                        Intent intent = new Intent(getActivity(), Profile.class);
                        intent.putExtra("from", "Profile");
                        startActivity(intent);
                    }
                }
            }
        }
    }

    private void InterstitialAd() {
        try {
            AdRequest adRequest = new AdRequest.Builder().build();
            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    Log.e("InterstitialAd", "failed to show. => " + adError.toString());
                    mInterstitialAd = null;
                    if (TYPE.equalsIgnoreCase("LOGOUT")) {
                        if (Utils.checkLoginUser(getActivity())) {
                            logout();
                        }
                    } else if (TYPE.equalsIgnoreCase("SHAREAPP")) {
                        ShareMe();
                    } else if (TYPE.equalsIgnoreCase("RATEAPP")) {
                        rateMe();
                    } else if (TYPE.equalsIgnoreCase("PRIVACY")) {
                        Intent intentPrivacy = new Intent(getActivity(), Privacypolicy.class);
                        startActivity(intentPrivacy);
                    } else if (TYPE.equalsIgnoreCase("ABOUTUS")) {
                        startActivity(new Intent(getActivity(), AboutUs.class));
                    } else if (TYPE.equalsIgnoreCase("USERPROFILE")) {
                        if (Utils.checkLoginUser(getActivity())) {
                            Constant.isSelectPic = false;
                            Intent intent = new Intent(getActivity(), Profile.class);
                            intent.putExtra("from", "Profile");
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    Log.e("InterstitialAd", "ShowedFullScreen");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    Log.e("InterstitialAd", "DismissedFullScreen");
                    mInterstitialAd = null;
                    if (TYPE.equalsIgnoreCase("LOGOUT")) {
                        if (Utils.checkLoginUser(getActivity())) {
                            logout();
                        }
                    } else if (TYPE.equalsIgnoreCase("SHAREAPP")) {
                        ShareMe();
                    } else if (TYPE.equalsIgnoreCase("RATEAPP")) {
                        rateMe();
                    } else if (TYPE.equalsIgnoreCase("PRIVACY")) {
                        Intent intentPrivacy = new Intent(getActivity(), Privacypolicy.class);
                        startActivity(intentPrivacy);
                    } else if (TYPE.equalsIgnoreCase("ABOUTUS")) {
                        startActivity(new Intent(getActivity(), AboutUs.class));
                    } else if (TYPE.equalsIgnoreCase("USERPROFILE")) {
                        if (Utils.checkLoginUser(getActivity())) {
                            Constant.isSelectPic = false;
                            Intent intent = new Intent(getActivity(), Profile.class);
                            intent.putExtra("from", "Profile");
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    Log.e("InterstitialAd", "onAdImpression.");
                }
            };

            mInterstitialAd.load(getActivity(), "" + prefManager.getValue("interstital_adid"),
                    adRequest, new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            mInterstitialAd = interstitialAd;
                            Log.e("InterstitialAd", "onAdLoaded");
                            mInterstitialAd.setFullScreenContentCallback(fullScreenContentCallback);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.e("InterstitialAd", "loadAdError => " + loadAdError.getMessage());
                            mInterstitialAd = null;
                        }
                    });
        } catch (Exception e) {
            Log.e("InterstitialAd", "Exception => " + e);
        }
    }

    private void FacebookInterstitialAd() {
        try {
            fbInterstitialAd = new com.facebook.ads.InterstitialAd(getActivity(),
                    "CAROUSEL_IMG_SQUARE_APP_INSTALL#" + prefManager.getValue("fb_interstiatial_id"));
            fbInterstitialAd.loadAd(fbInterstitialAd.buildLoadAdConfig()
                    .withAdListener(new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(Ad ad) {
                            Log.e("fbInterstitialAd", "fb ad displayed.");
                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {
                            fbInterstitialAd = null;
                            if (TYPE.equalsIgnoreCase("LOGOUT")) {
                                if (Utils.checkLoginUser(getActivity())) {
                                    logout();
                                }
                            } else if (TYPE.equalsIgnoreCase("SHAREAPP")) {
                                ShareMe();
                            } else if (TYPE.equalsIgnoreCase("RATEAPP")) {
                                rateMe();
                            } else if (TYPE.equalsIgnoreCase("PRIVACY")) {
                                Intent intentPrivacy = new Intent(getActivity(), Privacypolicy.class);
                                startActivity(intentPrivacy);
                            } else if (TYPE.equalsIgnoreCase("ABOUTUS")) {
                                startActivity(new Intent(getActivity(), AboutUs.class));
                            } else if (TYPE.equalsIgnoreCase("USERPROFILE")) {
                                if (Utils.checkLoginUser(getActivity())) {
                                    Constant.isSelectPic = false;
                                    Intent intent = new Intent(getActivity(), Profile.class);
                                    intent.putExtra("from", "Profile");
                                    startActivity(intent);
                                }
                            }
                            Log.e("fbInterstitialAd", "fb ad dismissed.");
                        }

                        @Override
                        public void onError(Ad ad, com.facebook.ads.AdError adError) {
                            Log.e("fbInterstitialAd", "fb ad failed to load : " + adError.getErrorMessage());
                            fbInterstitialAd = null;
                        }

                        @Override
                        public void onAdLoaded(Ad ad) {
                            Log.e("fbInterstitialAd", "fb ad is loaded and ready to be displayed!");
                        }

                        @Override
                        public void onAdClicked(Ad ad) {
                            Log.e("TAG", "fb ad clicked!");
                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {
                            Log.e("fbInterstitialAd", "fb ad impression logged!");
                        }
                    })
                    .build());
        } catch (Exception e) {
            Log.e("fbInterstitialAd", "Exception =>" + e);
        }
    }

    private void RewardedVideoAd() {
        try {
            AdRequest adRequest = new AdRequest.Builder().build();

            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    Log.e("RewardedAd", "Ad failed to show. => " + adError.toString());
                    mRewardedAd = null;
                    if (TYPE.equalsIgnoreCase("LOGOUT")) {
                        if (Utils.checkLoginUser(getActivity())) {
                            logout();
                        }
                    } else if (TYPE.equalsIgnoreCase("SHAREAPP")) {
                        ShareMe();
                    } else if (TYPE.equalsIgnoreCase("RATEAPP")) {
                        rateMe();
                    } else if (TYPE.equalsIgnoreCase("PRIVACY")) {
                        Intent intentPrivacy = new Intent(getActivity(), Privacypolicy.class);
                        startActivity(intentPrivacy);
                    } else if (TYPE.equalsIgnoreCase("ABOUTUS")) {
                        startActivity(new Intent(getActivity(), AboutUs.class));
                    } else if (TYPE.equalsIgnoreCase("USERPROFILE")) {
                        if (Utils.checkLoginUser(getActivity())) {
                            Constant.isSelectPic = false;
                            Intent intent = new Intent(getActivity(), Profile.class);
                            intent.putExtra("from", "Profile");
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    Log.e("RewardedAd", "Ad was shown.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    mRewardedAd = null;
                    Log.e("RewardedAd", "Ad was dismissed.");
                    if (TYPE.equalsIgnoreCase("LOGOUT")) {
                        if (Utils.checkLoginUser(getActivity())) {
                            logout();
                        }
                    } else if (TYPE.equalsIgnoreCase("SHAREAPP")) {
                        ShareMe();
                    } else if (TYPE.equalsIgnoreCase("RATEAPP")) {
                        rateMe();
                    } else if (TYPE.equalsIgnoreCase("PRIVACY")) {
                        Intent intentPrivacy = new Intent(getActivity(), Privacypolicy.class);
                        startActivity(intentPrivacy);
                    } else if (TYPE.equalsIgnoreCase("ABOUTUS")) {
                        startActivity(new Intent(getActivity(), AboutUs.class));
                    } else if (TYPE.equalsIgnoreCase("USERPROFILE")) {
                        if (Utils.checkLoginUser(getActivity())) {
                            Constant.isSelectPic = false;
                            Intent intent = new Intent(getActivity(), Profile.class);
                            intent.putExtra("from", "Profile");
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    Log.e("RewardedAd", "onAdImpression.");
                }
            };
            //TEST reward_adid ==>  ca-app-pub-3940256099942544/5224354917 ==OR== + prefManager.getValue("reward_adid")
            mRewardedAd.load(getActivity(), "" + prefManager.getValue("reward_adid"),
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            super.onAdLoaded(rewardedAd);
                            Log.e("RewardedAd", "onAdLoaded");
                            mRewardedAd = rewardedAd;
                            mRewardedAd.setFullScreenContentCallback(fullScreenContentCallback);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            Log.e("RewardedAd", "onAdFailedToLoad");
                            mRewardedAd = null;
                        }
                    });

        } catch (Exception e) {
            Log.e("RewardedVideoAd", "Exception => " + e);
        }
    }

    private void FacebookRewardAd() {
        try {
            fbRewardedVideoAd = new RewardedVideoAd(getActivity(),
                    "VID_HD_16_9_15S_APP_INSTALL#" + prefManager.getValue("fb_rewardvideo_id"));

            fbRewardedVideoAd.loadAd(fbRewardedVideoAd.buildLoadAdConfig().withAdListener(new RewardedVideoAdListener() {
                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    Log.e("TAG", "Rewarded video adError => " + adError.getErrorMessage());
                    fbRewardedVideoAd.destroy();
                    fbRewardedVideoAd = null;
                    if (TYPE.equalsIgnoreCase("LOGOUT")) {
                        if (Utils.checkLoginUser(getActivity())) {
                            logout();
                        }
                    } else if (TYPE.equalsIgnoreCase("SHAREAPP")) {
                        ShareMe();
                    } else if (TYPE.equalsIgnoreCase("RATEAPP")) {
                        rateMe();
                    } else if (TYPE.equalsIgnoreCase("PRIVACY")) {
                        Intent intentPrivacy = new Intent(getActivity(), Privacypolicy.class);
                        startActivity(intentPrivacy);
                    } else if (TYPE.equalsIgnoreCase("ABOUTUS")) {
                        startActivity(new Intent(getActivity(), AboutUs.class));
                    } else if (TYPE.equalsIgnoreCase("USERPROFILE")) {
                        if (Utils.checkLoginUser(getActivity())) {
                            Constant.isSelectPic = false;
                            Intent intent = new Intent(getActivity(), Profile.class);
                            intent.putExtra("from", "Profile");
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    Log.e("TAG", "Rewarded video ad is loaded and ready to be displayed!");
                }

                @Override
                public void onAdClicked(Ad ad) {
                    Log.e("TAG", "Rewarded video ad clicked!");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    Log.e("TAG", "Rewarded video ad impression logged!");
                }

                @Override
                public void onRewardedVideoCompleted() {
                    Log.e("TAG", "Rewarded video completed!");
                }

                @Override
                public void onRewardedVideoClosed() {
                    Log.e("TAG", "Rewarded video ad closed!");
                    fbRewardedVideoAd.destroy();
                    fbRewardedVideoAd = null;
                    if (TYPE.equalsIgnoreCase("LOGOUT")) {
                        if (Utils.checkLoginUser(getActivity())) {
                            logout();
                        }
                    } else if (TYPE.equalsIgnoreCase("SHAREAPP")) {
                        ShareMe();
                    } else if (TYPE.equalsIgnoreCase("RATEAPP")) {
                        rateMe();
                    } else if (TYPE.equalsIgnoreCase("PRIVACY")) {
                        Intent intentPrivacy = new Intent(getActivity(), Privacypolicy.class);
                        startActivity(intentPrivacy);
                    } else if (TYPE.equalsIgnoreCase("ABOUTUS")) {
                        startActivity(new Intent(getActivity(), AboutUs.class));
                    } else if (TYPE.equalsIgnoreCase("USERPROFILE")) {
                        if (Utils.checkLoginUser(getActivity())) {
                            Constant.isSelectPic = false;
                            Intent intent = new Intent(getActivity(), Profile.class);
                            intent.putExtra("from", "Profile");
                            startActivity(intent);
                        }
                    }
                }
            }).build());

        } catch (Exception e) {
            Log.e("FacebookRewardAd", "Exception => " + e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.shimmerHide(shimmer);
        if (mInterstitialAd != null) {
            mInterstitialAd = null;
        }
        if (mRewardedAd != null) {
            mRewardedAd = null;
        }
        if (fbRewardedVideoAd != null) {
            fbRewardedVideoAd.destroy();
            fbRewardedVideoAd = null;
        }
        if (fbNativeBannerAd != null) {
            fbNativeBannerAd.destroy();
        }
        if (fbInterstitialAd != null) {
            fbInterstitialAd.destroy();
            fbInterstitialAd = null;
        }
    }

}