package com.divinetechs.ebooksapp.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.divinetechs.ebooksapp.Model.PointSystemModel.DailyLogin;
import com.divinetechs.ebooksapp.Model.PointSystemModel.PointSystemModel;
import com.divinetechs.ebooksapp.Model.SuccessModel.SuccessModel;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.PrefManager;
import com.divinetechs.ebooksapp.Utility.Utils;
import com.divinetechs.ebooksapp.Webservice.AppAPI;
import com.divinetechs.ebooksapp.Webservice.BaseURL;
import com.facebook.ads.Ad;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EarnRewards extends AppCompatActivity implements View.OnClickListener {

    PrefManager prefManager;
    ShimmerFrameLayout shimmer;
    LinearLayout lyToolbar, lyBack, lyWatchVideo, lyOne, lyTwo, lyThree, lyFour, lyFive, lySix, lySeven;
    TextView txtToolbarTitle, txtBack, txtWatchPoints, txtIconOne, txtIconTwo, txtIconThree, txtIconFour, txtIconFive, txtIconSix, txtIconSeven,
            txtDayOne, txtDayTwo, txtDayThree, txtDayFour, txtDayFive, txtDaySix, txtDaySeven, txtD1Points, txtD2Points, txtD3Points, txtD4Points,
            txtD5Points, txtD6Points, txtD7Points;

    private RewardedAd mRewardedAd = null;
    private RewardedVideoAd fbRewardedVideoAd = null;
    private TemplateView nativeTemplate = null;
    private com.facebook.ads.NativeAd fbNativeAd = null;
    private NativeAdLayout fbNativeTemplate = null;
    private InterstitialAd mInterstitialAd = null;
    private com.facebook.ads.InterstitialAd fbInterstitialAd = null;

    List<DailyLogin> dailyPointList;
    String rewardPoints = "0", previousDay = "", watched = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.screenCapOff(EarnRewards.this);
        if (PrefManager.getInstance(this).isNightModeEnabled() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_earn_rewards);
        PrefManager.forceRTLIfSupported(getWindow(), EarnRewards.this);

        init();
        PointSystem();

        txtToolbarTitle.setText("" + getResources().getString(R.string.Earn_Rewards));

        Log.e("native_ad", "" + prefManager.getValue("native_ad"));
        if (prefManager.getValue("native_ad").equalsIgnoreCase("yes")) {
            nativeTemplate.setVisibility(View.VISIBLE);
            Utils.NativeAds(EarnRewards.this, nativeTemplate, "" + prefManager.getValue("native_adid"));
        } else {
            nativeTemplate.setVisibility(View.GONE);
        }

        Log.e("fb_native_full_status", "" + prefManager.getValue("fb_native_full_status"));
        if (prefManager.getValue("fb_native_full_status").equalsIgnoreCase("on")) {
            fbNativeTemplate.setVisibility(View.VISIBLE);
            Utils.FacebookNativeAdLarge(EarnRewards.this, fbNativeAd, fbNativeTemplate, "" + prefManager.getValue("fb_native_full_id"));
        } else {
            fbNativeTemplate.setVisibility(View.GONE);
        }

    }

    private void init() {
        try {
            prefManager = new PrefManager(EarnRewards.this);

            shimmer = findViewById(R.id.shimmer);
            nativeTemplate = findViewById(R.id.nativeTemplate);
            fbNativeTemplate = findViewById(R.id.fbNativeTemplate);

            lyToolbar = findViewById(R.id.lyToolbar);
            lyBack = findViewById(R.id.lyBack);
            txtBack = findViewById(R.id.txtBack);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);

            lyOne = findViewById(R.id.lyOne);
            lyTwo = findViewById(R.id.lyTwo);
            lyThree = findViewById(R.id.lyThree);
            lyFour = findViewById(R.id.lyFour);
            lyFive = findViewById(R.id.lyFive);
            lySix = findViewById(R.id.lySix);
            lySeven = findViewById(R.id.lySeven);
            lyWatchVideo = findViewById(R.id.lyWatchVideo);

            txtIconOne = findViewById(R.id.txtIconOne);
            txtIconTwo = findViewById(R.id.txtIconTwo);
            txtIconThree = findViewById(R.id.txtIconThree);
            txtIconFour = findViewById(R.id.txtIconFour);
            txtIconFive = findViewById(R.id.txtIconFive);
            txtIconSix = findViewById(R.id.txtIconSix);
            txtIconSeven = findViewById(R.id.txtIconSeven);

            txtWatchPoints = findViewById(R.id.txtWatchPoints);
            txtD1Points = findViewById(R.id.txtD1Points);
            txtD2Points = findViewById(R.id.txtD2Points);
            txtD3Points = findViewById(R.id.txtD3Points);
            txtD4Points = findViewById(R.id.txtD4Points);
            txtD5Points = findViewById(R.id.txtD5Points);
            txtD6Points = findViewById(R.id.txtD6Points);
            txtD7Points = findViewById(R.id.txtD7Points);

            txtDayOne = findViewById(R.id.txtDayOne);
            txtDayTwo = findViewById(R.id.txtDayTwo);
            txtDayThree = findViewById(R.id.txtDayThree);
            txtDayFour = findViewById(R.id.txtDayFour);
            txtDayFive = findViewById(R.id.txtDayFive);
            txtDaySix = findViewById(R.id.txtDaySix);
            txtDaySeven = findViewById(R.id.txtDaySeven);

            lyWatchVideo.setOnClickListener(this);
            lyBack.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    private void AdInit() {
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

        Log.e("fb_interstiatial_ad", "" + prefManager.getValue("fb_interstiatial_ad"));
        if (prefManager.getValue("fb_interstiatial_ad").equalsIgnoreCase("yes")) {
            fbInterstitialAd = null;
            FacebookInterstitialAd();
        }
    }

    //earn_point API
    private void PointSystem() {
        Utils.shimmerShow(shimmer);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<PointSystemModel> call = bookNPlayAPI.earn_point();
        call.enqueue(new Callback<PointSystemModel>() {
            @Override
            public void onResponse(Call<PointSystemModel> call, Response<PointSystemModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Log.e("earn_point", "status => " + response.body().getStatus());

                        if (response.body().getDailyLogin().size() > 0) {
                            dailyPointList = new ArrayList<>();
                            dailyPointList = response.body().getDailyLogin();
                            Log.e("dailyPointList", "size => " + dailyPointList.size());

                            txtDayOne.setText("" + dailyPointList.get(0).getKey());
                            txtDayTwo.setText("" + dailyPointList.get(1).getKey());
                            txtDayThree.setText("" + dailyPointList.get(2).getKey());
                            txtDayFour.setText("" + dailyPointList.get(3).getKey());
                            txtDayFive.setText("" + dailyPointList.get(4).getKey());
                            txtDaySix.setText("" + dailyPointList.get(5).getKey());
                            txtDaySeven.setText("" + dailyPointList.get(6).getKey());

                            txtD1Points.setText("" + dailyPointList.get(0).getValue());
                            txtD2Points.setText("" + dailyPointList.get(1).getValue());
                            txtD3Points.setText("" + dailyPointList.get(2).getValue());
                            txtD4Points.setText("" + dailyPointList.get(3).getValue());
                            txtD5Points.setText("" + dailyPointList.get(4).getValue());
                            txtD6Points.setText("" + dailyPointList.get(5).getValue());
                            txtD7Points.setText("" + dailyPointList.get(6).getValue());
                            txtWatchPoints.setText("" + prefManager.getValue("free-Coin") + " " + getResources().getString(R.string.vouchers));

                            SetDayByDay();
                        } else {
                            Log.e("earn_point", "message =>" + response.body().getMessage());
                        }

                    } else {
                        Log.e("earn_point", "message =>" + response.body().getMessage());
                    }
                } catch (Exception e) {
                    Log.e("earn_point", "Exception =>" + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<PointSystemModel> call, Throwable t) {
                Log.e("earn_point", "Throwable => " + t.getMessage());
                Utils.shimmerHide(shimmer);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyBack:
                finish();
                break;

            case R.id.lyWatchVideo:
                if (mRewardedAd != null) {
                    prefManager.setWatch("WATCHED", "" + (new Date()) + "/1");
                    mRewardedAd.show(EarnRewards.this, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            Log.e("RewardItem amount =>", "" + rewardItem.getAmount());
                            rewardPoints = "" + prefManager.getValue("free-Coin");
                        }
                    });
                } else if (fbRewardedVideoAd != null && fbRewardedVideoAd.isAdLoaded()) {
                    prefManager.setWatch("WATCHED", "" + (new Date()) + "/1");
                    rewardPoints = "" + prefManager.getValue("free-Coin");
                    fbRewardedVideoAd.show();
                }
                break;

            case R.id.lyOne:
                ShowAdByClick("" + dailyPointList.get(0).getValue(), "1");
                break;

            case R.id.lyTwo:
                ShowAdByClick("" + dailyPointList.get(1).getValue(), "2");
                break;

            case R.id.lyThree:
                ShowAdByClick("" + dailyPointList.get(2).getValue(), "3");
                break;

            case R.id.lyFour:
                ShowAdByClick("" + dailyPointList.get(3).getValue(), "4");
                break;

            case R.id.lyFive:
                ShowAdByClick("" + dailyPointList.get(4).getValue(), "5");
                break;

            case R.id.lySix:
                ShowAdByClick("" + dailyPointList.get(5).getValue(), "6");
                break;

            case R.id.lySeven:
                ShowAdByClick("" + dailyPointList.get(6).getValue(), "7");
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AdInit();
    }

    private void SetDayByDay() {
        Log.e("=>DAY", "" + prefManager.getDay("DAY"));
        String[] splitDay = prefManager.getDay("DAY").split("/");
        String[] splitWatched = prefManager.getWatch("WATCHED").split("/");

        if (splitDay.length > 0) {
            previousDay = splitDay[1];
        } else {
            previousDay = "0";
        }
        Log.e("splitDay[0]", "" + splitDay[0]);
        Log.e("previousDay", "" + previousDay);
        Log.e("prefDate", "" + prefManager.getDate("savedDate"));

        if (splitWatched.length > 0) {
            watched = splitWatched[1];
        } else {
            watched = "0";
        }
        Log.e("watched", "" + watched);
        Log.e("Today", "WATCHED => " + Utils.DateCheckWithToday("" + splitWatched[0]));
        if (Utils.DateCheckWithToday("" + splitWatched[0]).equalsIgnoreCase("YES")) {
            if (watched.equalsIgnoreCase("0")) {
                lyWatchVideo.setVisibility(View.VISIBLE);
            } else {
                lyWatchVideo.setVisibility(View.GONE);
            }
        } else {
            lyWatchVideo.setVisibility(View.VISIBLE);
        }

        Log.e("CheckWithToday", "=> " + Utils.DateCheckWithToday("" + prefManager.getDate("savedDate")));
        if (previousDay.equalsIgnoreCase("0")) {
            lyOne.setOnClickListener(this);
            lyTwo.setOnClickListener(null);
            lyThree.setOnClickListener(null);
            lyFour.setOnClickListener(null);
            lyFive.setOnClickListener(null);
            lySix.setOnClickListener(null);
            lySeven.setOnClickListener(null);
        } else if (previousDay.equalsIgnoreCase("1")) {
            lyOne.setOnClickListener(null);
            if (Utils.DateCheckWithToday("" + prefManager.getDate("savedDate")).equalsIgnoreCase("NO")) {
                lyTwo.setOnClickListener(this);
            }
            lyThree.setOnClickListener(null);
            lyFour.setOnClickListener(null);
            lyFive.setOnClickListener(null);
            lySix.setOnClickListener(null);
            lySeven.setOnClickListener(null);
            txtIconOne.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
        } else if (previousDay.equalsIgnoreCase("2")) {
            lyOne.setOnClickListener(null);
            lyTwo.setOnClickListener(null);
            if (Utils.DateCheckWithToday("" + prefManager.getDate("savedDate")).equalsIgnoreCase("NO")) {
                lyThree.setOnClickListener(this);
            }
            lyFour.setOnClickListener(null);
            lyFive.setOnClickListener(null);
            lySix.setOnClickListener(null);
            lySeven.setOnClickListener(null);
            txtIconOne.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
            txtIconTwo.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
        } else if (previousDay.equalsIgnoreCase("3")) {
            lyOne.setOnClickListener(null);
            lyTwo.setOnClickListener(null);
            lyThree.setOnClickListener(null);
            if (Utils.DateCheckWithToday("" + prefManager.getDate("savedDate")).equalsIgnoreCase("NO")) {
                lyFour.setOnClickListener(this);
            }
            lyFive.setOnClickListener(null);
            lySix.setOnClickListener(null);
            lySeven.setOnClickListener(null);
            txtIconOne.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
            txtIconTwo.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
            txtIconThree.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
        } else if (previousDay.equalsIgnoreCase("4")) {
            lyOne.setOnClickListener(null);
            lyTwo.setOnClickListener(null);
            lyThree.setOnClickListener(null);
            lyFour.setOnClickListener(null);
            if (Utils.DateCheckWithToday("" + prefManager.getDate("savedDate")).equalsIgnoreCase("NO")) {
                lyFive.setOnClickListener(this);
            }
            lySix.setOnClickListener(null);
            lySeven.setOnClickListener(null);
            txtIconOne.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
            txtIconTwo.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
            txtIconThree.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
            txtIconFour.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
        } else if (previousDay.equalsIgnoreCase("5")) {
            lyOne.setOnClickListener(null);
            lyTwo.setOnClickListener(null);
            lyThree.setOnClickListener(null);
            lyFour.setOnClickListener(null);
            lyFive.setOnClickListener(null);
            if (Utils.DateCheckWithToday("" + prefManager.getDate("savedDate")).equalsIgnoreCase("NO")) {
                lySix.setOnClickListener(this);
            }
            lySeven.setOnClickListener(null);
            txtIconOne.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
            txtIconTwo.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
            txtIconThree.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
            txtIconFour.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
            txtIconFive.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
        } else if (previousDay.equalsIgnoreCase("6")) {
            lyOne.setOnClickListener(null);
            lyTwo.setOnClickListener(null);
            lyThree.setOnClickListener(null);
            lyFour.setOnClickListener(null);
            lyFive.setOnClickListener(null);
            lySix.setOnClickListener(null);
            if (Utils.DateCheckWithToday("" + prefManager.getDate("savedDate")).equalsIgnoreCase("NO")) {
                lySeven.setOnClickListener(this);
            }
            txtIconOne.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
            txtIconTwo.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
            txtIconThree.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
            txtIconFour.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
            txtIconFive.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
            txtIconSix.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
        } else {
            lyOne.setOnClickListener(null);
            lyTwo.setOnClickListener(null);
            lyThree.setOnClickListener(null);
            lyFour.setOnClickListener(null);
            lyFive.setOnClickListener(null);
            lySix.setOnClickListener(null);
            lySeven.setOnClickListener(null);
            txtIconOne.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
            txtIconTwo.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
            txtIconThree.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
            txtIconFour.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
            txtIconFive.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
            txtIconSix.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
            txtIconSeven.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_checked));
        }
    }

    private void AddVouchers(String points) {
        Utils.ProgressBarShow(EarnRewards.this);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<SuccessModel> call = bookNPlayAPI.add_voucher("" + prefManager.getLoginId(), "Advertisement", "" + points);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Toasty.success(EarnRewards.this, "" + response.body().getMessage(),
                                Toasty.LENGTH_SHORT).show();
                        prefManager.setDate("savedDate", "" + (new Date()));
                        SetDayByDay();
                    } else {
                        Toasty.error(EarnRewards.this, "" + response.body().getMessage(),
                                Toasty.LENGTH_SHORT).show();
                    }

                    rewardPoints = "0";

                } catch (Exception e) {
                    Log.e("get_ads_banner", "Exception =>" + e);
                }
                Utils.ProgressbarHide();
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                Log.e("get_ads_banner", "Throwable =>" + t.getMessage());
                Utils.ProgressbarHide();
                rewardPoints = "0";
            }
        });

    }

    //Showing ads
    private void ShowAdByClick(String pointEarn, String dayOfEarn) {
        Log.e("=> pointEarn", "" + pointEarn);
        Log.e("=> dayOfEarn", "" + dayOfEarn);

        if (prefManager.getValue("reward_ad").equalsIgnoreCase("yes")) {
            if (mRewardedAd != null) {
                prefManager.setDate("savedDate", "" + (new Date()));
                prefManager.setDay("DAY", "" + (new Date()) + "/" + dayOfEarn);
                mRewardedAd.show(EarnRewards.this, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        Log.e("RewardItem amount =>", "" + rewardItem.getAmount());
                        rewardPoints = pointEarn;
                    }
                });
            }

        } else if (prefManager.getValue("fb_rewardvideo_status").equalsIgnoreCase("on")) {
            if (fbRewardedVideoAd != null && fbRewardedVideoAd.isAdLoaded()) {
                prefManager.setDate("savedDate", "" + (new Date()));
                prefManager.setDay("DAY", "" + (new Date()) + "/" + dayOfEarn);
                rewardPoints = pointEarn;
                fbRewardedVideoAd.show();
            }

        } else if (prefManager.getValue("fb_interstiatial_status").equalsIgnoreCase("on")) {
            if (fbInterstitialAd != null && fbInterstitialAd.isAdLoaded()) {
                prefManager.setDate("savedDate", "" + (new Date()));
                prefManager.setDay("DAY", "" + (new Date()) + "/" + dayOfEarn);
                rewardPoints = pointEarn;
                fbInterstitialAd.show();
            }

        } else {
            if (mInterstitialAd != null) {
                prefManager.setDate("savedDate", "" + (new Date()));
                prefManager.setDay("DAY", "" + (new Date()) + "/" + dayOfEarn);
                rewardPoints = pointEarn;
                mInterstitialAd.show(EarnRewards.this);
            }
        }
    }

    private void InterstitialAd() {
        try {
            AdRequest adRequest = new AdRequest.Builder().build();
            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    Log.e("InterstitialAd", "InterstitialAd failed to show. " + adError.toString());
                    mInterstitialAd = null;
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    Log.e("InterstitialAd", "InterstitialAd was shown. ");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    Log.e("InterstitialAd", "InterstitialAd was dismissed. ");
                    mInterstitialAd = null;
                    AddVouchers(rewardPoints);
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    Log.e("InterstitialAd", "InterstitialAd onAdImpression. ");
                }
            };

            mInterstitialAd.load(this, "" + prefManager.getValue("interstital_adid"), adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            mInterstitialAd = interstitialAd;
                            Log.e("onAdLoaded", "interstitialAd => " + interstitialAd.getAdUnitId());
                            mInterstitialAd.setFullScreenContentCallback(fullScreenContentCallback);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.e("onAdFailedToLoad", "loadAdError => " + loadAdError.getMessage());
                            mInterstitialAd = null;
                        }
                    });
        } catch (Exception e) {
            Log.e("InterstitialAd", "Exception => " + e);
        }
    }

    private void FacebookInterstitialAd() {
        try {
            fbInterstitialAd = new com.facebook.ads.InterstitialAd(this,
                    "CAROUSEL_IMG_SQUARE_APP_INSTALL#" + prefManager.getValue("fb_interstiatial_id"));
            fbInterstitialAd.loadAd(fbInterstitialAd.buildLoadAdConfig().withAdListener(new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {
                    Log.e("fbInterstitialAd", "fb ad displayed.");
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    fbInterstitialAd = null;
                    AddVouchers(rewardPoints);
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
                    Log.e("fbInterstitialAd", "fb ad clicked!");
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
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    Log.e("Ad failed to show.", "" + adError.toString());
                    mRewardedAd = null;
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    Log.e("RewardedVideoAd", "Ad was shown.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    mRewardedAd = null;
                    Log.e("RewardedVideoAd", "Ad was dismissed.");
                    AddVouchers(rewardPoints);
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    Log.e("RewardedVideoAd", "onAdImpression.");
                }
            };

            mRewardedAd.load(EarnRewards.this, "" + prefManager.getValue("reward_adid"), adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            super.onAdLoaded(rewardedAd);
                            mRewardedAd = rewardedAd;
                            mRewardedAd.setFullScreenContentCallback(fullScreenContentCallback);
                            Log.e("RewardedVideoAd", "onAdLoaded => " + rewardedAd.getAdUnitId());
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            mRewardedAd = null;
                            Log.e("RewardedVideoAd", "onAdFailedToLoad => " + loadAdError.toString());
                        }
                    });

        } catch (Exception e) {
            Log.e("RewardedAd", "Exception => " + e);
        }
    }

    private void FacebookRewardAd() {
        try {
            fbRewardedVideoAd = new RewardedVideoAd(EarnRewards.this,
                    "VID_HD_16_9_15S_APP_INSTALL#" + prefManager.getValue("fb_rewardvideo_id"));

            fbRewardedVideoAd.loadAd(fbRewardedVideoAd.buildLoadAdConfig().withAdListener(new RewardedVideoAdListener() {
                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    Log.e("FacebookRewardAd", "Rewarded video adError => " + adError.getErrorMessage());
                    fbRewardedVideoAd.destroy();
                    fbRewardedVideoAd = null;
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    Log.e("FacebookRewardAd", "Rewarded video ad is loaded and ready to be displayed!");
                }

                @Override
                public void onAdClicked(Ad ad) {
                    Log.e("FacebookRewardAd", "Rewarded video ad clicked!");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    Log.e("FacebookRewardAd", "Rewarded video ad impression logged!");
                    Log.e("FacebookRewardAd", "onLoggingImpression => " + ad.getPlacementId());
                }

                @Override
                public void onRewardedVideoCompleted() {
                    Log.e("FacebookRewardAd", "Rewarded video completed!");
                }

                @Override
                public void onRewardedVideoClosed() {
                    Log.e("FacebookRewardAd", "Rewarded video ad closed!");
                    fbRewardedVideoAd.destroy();
                    fbRewardedVideoAd = null;
                    AddVouchers(rewardPoints);
                }
            }).build());

        } catch (Exception e) {
            Log.e("fbRewardedVideoAd", "Exception => " + e.getMessage());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Utils.shimmerHide(shimmer);
        Utils.ProgressbarHide();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.ProgressbarHide();
        Utils.shimmerHide(shimmer);
        if (fbNativeAd != null) {
            fbNativeAd.destroy();
        }
        if (mRewardedAd != null) {
            mRewardedAd = null;
        }
        if (fbRewardedVideoAd != null) {
            fbRewardedVideoAd.destroy();
            fbRewardedVideoAd = null;
        }
    }

}