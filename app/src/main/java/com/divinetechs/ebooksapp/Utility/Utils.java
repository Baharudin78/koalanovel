package com.divinetechs.ebooksapp.Utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Activity.LoginActivity;
import com.divinetechs.ebooksapp.Adapter.LodingList;
import com.divinetechs.ebooksapp.Model.SuccessModel.SuccessModel;
import com.divinetechs.ebooksapp.OTPLogin.OTPVerification;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Webservice.AppAPI;
import com.divinetechs.ebooksapp.Webservice.BaseURL;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;
import com.makeramen.roundedimageview.RoundedImageView;
import com.paginate.Paginate;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class Utils {

    Context context;
    public static ProgressDialog pDialog;

    public Utils(Context context) {
        this.context = context;
    }


    //Store User Credential
    public static void storeUserCred(Activity context, String userID, String userType, String email, String userName, String mobileNumber) {
        com.divinetechs.ebooksapp.Utility.PrefManager prefManager = new com.divinetechs.ebooksapp.Utility.PrefManager(context);
        prefManager.setLoginId("" + userID);
        prefManager.setValue("Email", "" + email);
        prefManager.setValue("userType", "" + userType);
        prefManager.setValue("fullname", "" + userName);
        prefManager.setValue("Phone", "" + mobileNumber);
    }

    //check File Availability
    public static boolean checkFileAvailability(Activity activity, String fileURL, String docType) {
        if (fileURL != null) {
            Log.e("=> fileURL", "" + fileURL);

            String downloadDirectory;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (docType.equalsIgnoreCase("book")) {
                    downloadDirectory = Functions.getAppFolder(activity) + activity.getResources().getString(R.string.books) + "/";
                } else {
                    downloadDirectory = Functions.getAppFolder(activity) + activity.getResources().getString(R.string.magazines) + "/";
                }
            } else {
                if (docType.equalsIgnoreCase("book")) {
                    downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath() + "/" + activity.getResources().getString(R.string.books) + "/";
                } else {
                    downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath() + "/" + activity.getResources().getString(R.string.magazines) + "/";
                }
            }

            File file = new File(downloadDirectory);
            if (!file.exists()) {
                Log.e("checkFileAvailability", "Document directory created again");
                return false;
            }

            File checkFile;
            checkFile = new File(fileURL);
            Log.e("checkFileAvailability", "checkFile => " + checkFile);
            if (!checkFile.exists()) {
                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    //Alert Dialog
    public static void AlertDialog(Activity activity, String message, boolean isSuccess, boolean isFinish) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.exit_logout_dialog, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setElevation(100);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        RoundedImageView rivDialog = popupView.findViewById(R.id.rivDialog);
        if (isSuccess) {
            rivDialog.setImageResource(R.drawable.ic_success);
        } else {
            rivDialog.setImageResource(R.drawable.ic_warn);
        }

        TextView txtTitle = popupView.findViewById(R.id.txtTitle);
        TextView txtDescription = popupView.findViewById(R.id.txtDescription);
        Button btnNegative = popupView.findViewById(R.id.btnNegative);
        Button btnPositive = popupView.findViewById(R.id.btnPositive);

        txtTitle.setText("" + activity.getResources().getString(R.string.app_name));
        txtDescription.setText("" + message);

        btnNegative.setVisibility(View.GONE);
        btnPositive.setText("" + activity.getResources().getString(R.string.okay));
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if (isFinish) {
                    activity.finish();
                }
            }
        });
    }

    //Pagination
    public static void Pagination(RecyclerView recyclerView, Paginate.Callbacks callbacks) {
        Paginate paginate = Paginate.with(recyclerView, callbacks)
                .setLoadingTriggerThreshold(5)
                .addLoadingListItem(true)
                .setLoadingListItemCreator(false ? new LodingList() : null)
                .build();
    }

    //Facebook Small NativeAd
    public static void FacebookNativeAdSmall(Activity activity, NativeBannerAd fbNativeBannerAd, NativeAdLayout fbNativeTemplate, String fbNativeID) {
        try {
            fbNativeBannerAd = new NativeBannerAd(activity, "IMG_16_9_APP_INSTALL#" + fbNativeID);

            NativeBannerAd finalFbNativeBannerAd = fbNativeBannerAd;
            fbNativeBannerAd.loadAd(fbNativeBannerAd.buildLoadAdConfig().withAdListener(new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                    Log.e("fbNativeBannerAd", "ad finished downloading all assets.");
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    // Native ad failed to load
                    Log.e("fbNativeBannerAd", "ad failed to load: " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Native ad is loaded and ready to be displayed
                    Log.d("fbNativeBannerAd", "ad is loaded and ready to be displayed!");
                    if (finalFbNativeBannerAd == null || finalFbNativeBannerAd != ad) {
                        return;
                    }
                    // Inflate Native Banner Ad into Container
                    inflateFbSmallNativeAd(activity, finalFbNativeBannerAd, fbNativeTemplate);
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                    Log.d("fbNativeBannerAd", "ad clicked!");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                    Log.d("fbNativeBannerAd", "ad impression logged!");
                }
            }).build());

        } catch (Exception e) {
            Log.e("fbNativeBannerAd", "Exception => " + e.getMessage());
        }
    }

    //Facebook Small NativeAd layout
    public static void inflateFbSmallNativeAd(Activity activity, NativeBannerAd nativeBannerAd, NativeAdLayout nativeTemplate) {
        nativeBannerAd.unregisterView();

        // Inflate the Ad view.  The layout referenced is the one you created in the last step.
        View adView = LayoutInflater.from(activity).inflate(R.layout.fbnative_s_adview,
                nativeTemplate, false);
        nativeTemplate.addView(adView);

        // Add the AdChoices icon
        RelativeLayout adChoicesContainer = adView.findViewById(R.id.adChoicesContainer);
        AdOptionsView adOptionsView = new AdOptionsView(activity, nativeBannerAd, nativeTemplate);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        TextView txtNativeTitle = adView.findViewById(R.id.txtNativeTitle);
        TextView txtNativeAdSocialContext = adView.findViewById(R.id.txtNativeAdSocialContext);
        TextView nativeAdSponsoredLabel = adView.findViewById(R.id.nativeAdSponsoredLabel);
        MediaView nativeMediaView = adView.findViewById(R.id.nativeMediaView);
        Button nativeAdCallToAction = adView.findViewById(R.id.nativeAdCallToAction);

        // Set the Text.
        nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(
                nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        txtNativeTitle.setText(nativeBannerAd.getAdvertiserName());
        txtNativeAdSocialContext.setText(nativeBannerAd.getAdSocialContext());
        nativeAdSponsoredLabel.setText(nativeBannerAd.getSponsoredTranslation());

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(txtNativeTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeBannerAd.registerViewForInteraction(adView, nativeMediaView, clickableViews);
    }

    //Facebook Large NativeAd
    public static void FacebookNativeAdLarge(Activity activity, NativeAd fbNativeAd, NativeAdLayout fbNativeFullTemplate, String fbFullNativeID) {
        try {
            fbNativeAd = new NativeAd(activity, "IMG_16_9_APP_INSTALL#" + fbFullNativeID);

            NativeAd finalFbNativeAd = fbNativeAd;
            fbNativeAd.loadAd(fbNativeAd.buildLoadAdConfig().withAdListener(new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                    Log.e("fbNativeAd", "ad finished downloading all assets.");
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    // Native ad failed to load
                    Log.e("fbNativeAd", "ad failed to load :- " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Native ad is loaded and ready to be displayed
                    Log.d("fbNativeAd", "ad is loaded and ready to be displayed!");
                    if (finalFbNativeAd == null || finalFbNativeAd != ad) {
                        return;
                    }
                    // Inflate Native Banner Ad into Container
                    Utils.inflateFbLargeNativeAd(activity, finalFbNativeAd, fbNativeFullTemplate);
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                    Log.d("fbNativeAd", "ad clicked!");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                    Log.d("fbNativeAd", "ad impression logged!");
                }
            }).build());

        } catch (Exception e) {
            Log.e("fbNative", "Exception => " + e.getMessage());
        }
    }

    //Facebook Large NativeAd layout
    public static void inflateFbLargeNativeAd(Activity activity, NativeAd nativeAd, NativeAdLayout nativeTemplate) {

        nativeAd.unregisterView();
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        View adView = LayoutInflater.from(activity).inflate(R.layout.fbnative_l_adview, nativeTemplate, false);
        nativeTemplate.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = adView.findViewById(R.id.adChoicesContainer);
        AdOptionsView adOptionsView = new AdOptionsView(activity, nativeAd, nativeTemplate);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        MediaView nativeAdIcon = adView.findViewById(R.id.nativeAdIcon);
        TextView txtNativeTitle = adView.findViewById(R.id.txtNativeTitle);
        MediaView nativeMediaView = adView.findViewById(R.id.nativeMediaView);
        TextView txtNativeAdSocialContext = adView.findViewById(R.id.txtNativeAdSocialContext);
        TextView txtNativeAdBody = adView.findViewById(R.id.txtNativeAdBody);
        TextView nativeAdSponsoredLabel = adView.findViewById(R.id.nativeAdSponsoredLabel);
        Button nativeAdCallToAction = adView.findViewById(R.id.nativeAdCallToAction);

        // Set the Text.
        txtNativeTitle.setText(nativeAd.getAdvertiserName());
        txtNativeAdBody.setText(nativeAd.getAdBodyText());
        txtNativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        nativeAdSponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(txtNativeTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(adView, nativeMediaView, nativeAdIcon, clickableViews);
    }

    //Facebook BannerAds
    public static void FacebookBannerAd(Activity activity, com.facebook.ads.AdView fbAdView, String fbPlacementId, LinearLayout lyFbAdView) {
        try {
            fbAdView = new com.facebook.ads.AdView(activity, "IMG_16_9_APP_INSTALL#" + fbPlacementId,
                    com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            fbAdView.loadAd(fbAdView.buildLoadAdConfig().withAdListener(new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    Log.e("fb Banner", "Error=> " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    Log.e("fb Banner", "Loaded=> " + ad.getPlacementId());
                }

                @Override
                public void onAdClicked(Ad ad) {
                    Log.e("fb Banner", "AdClick=> " + ad.getPlacementId());
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    Log.e("fb Banner", "LoggingImpression=> " + ad.getPlacementId());
                }
            }).build());

            lyFbAdView.addView(fbAdView);
        } catch (Exception e) {
            Log.e("Facebook BannerAds", "Exception => " + e.getMessage());
        }
    }

    //Admob BannerAds
    public static void Admob(Activity activity, AdView mAdView, String bannerAdId, RelativeLayout rlAdView) {
        try {
            mAdView = new AdView(activity);
            mAdView.setAdSize(AdSize.SMART_BANNER);
            mAdView.setAdUnitId("" + bannerAdId);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Log.e("onAdFailedToLoad =>", "" + loadAdError.toString());
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }
            });
            mAdView.loadAd(adRequest);
            rlAdView.addView(mAdView);
        } catch (Exception e) {
            Log.e("Admob BannerAds", "Exception => " + e.getMessage());
        }
    }

    //Admob NativeAd
    public static void NativeAds(Activity activity, TemplateView nativeTemplate, String nativeAdId) {
        try {
            AdLoader adLoader = new AdLoader.Builder(activity, "" + nativeAdId)
                    .forNativeAd(new com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener() {
                        private ColorDrawable background;

                        @Override
                        public void onNativeAdLoaded(@NonNull com.google.android.gms.ads.nativead.NativeAd nativeAd) {
                            Log.e("NativeAd", "Advertiser => " + nativeAd.getAdvertiser());
                            NativeTemplateStyle styles = new
                                    NativeTemplateStyle.Builder().withMainBackgroundColor(background).build();

                            nativeTemplate.setStyles(styles);
                            nativeTemplate.setNativeAd(nativeAd);
                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(LoadAdError adError) {
                            // Handle the failure by logging, altering the UI, and so on.
                            Log.e("NativeAd", "adError => " + adError.toString());
                        }

                        @Override
                        public void onAdClicked() {
                            // Log the click event or other custom behavior.
                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder().build())
                    .build();
            adLoader.loadAd(new AdRequest.Builder().build());
        } catch (Exception e) {
            Log.e("NativeAds", "Exception => " + e);
        }
    }


    //DateFormation :
    public static String DateFormat(String date) {
        String finaldate = "";
        try {
            @SuppressLint("SimpleDateFormat")
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date input = inputFormat.parse(date);
            DateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
            assert input != null;
            finaldate = outputFormat.format(input);
        } catch (Exception e) {
            Log.e("DateFormate", "Exception => " + e);
        }
        return finaldate;
    }

    //DateFormation :
    public static String DateFormat2(String date) {
        String finaldate = "";
        try {
            @SuppressLint("SimpleDateFormat")
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date input = inputFormat.parse(date);
            DateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
            assert input != null;
            finaldate = outputFormat.format(input);
        } catch (Exception e) {
            Log.e("DateFormate2", "Exception => " + e);
        }
        return finaldate;
    }

    //DateFormation :
    public static String DateFormat3(String date) {
        String finaldate = "";
        try {
            @SuppressLint("SimpleDateFormat")
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date input = inputFormat.parse(date);
            DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            assert input != null;
            finaldate = outputFormat.format(input);
        } catch (Exception e) {
            Log.e("DateFormat3", "Exception => " + e);
        }
        return finaldate;
    }

    //Today Day :
    public static String getToday() {
        String Today = "";
        try {
            Date currentDate = Calendar.getInstance().getTime();

            Today = "" + currentDate.getDay();
            if (Today.equalsIgnoreCase("0")) {
                Today = "7";
            }
        } catch (Exception e) {
            Log.e("getToday", "Exception => " + e);
        }
        return Today;
    }

    //Date Comparition :
    public static boolean compareDate(String date) {
        boolean isToday = false;
        try {
            @SuppressLint("SimpleDateFormat")
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDate = inputFormat.parse(date);
            Date currentDate = Calendar.getInstance().getTime();

            Log.e("startDate =>", "" + startDate);
            Log.e("currentDate =>", "" + currentDate);
            if (startDate.compareTo(currentDate) == 0) {
                isToday = true;
            } else if (startDate.compareTo(currentDate) < 0) {
                isToday = true;
            } else {
                isToday = false;
            }

            Log.e("isToday =>", "" + isToday);
        } catch (Exception e) {
            Log.e("compareDate", "Exception => " + e);
        }
        return isToday;
    }

    //Date Differences :
    public static long getDifference(String date) {
        long remainingTime = 0;
        try {
            @SuppressLint("SimpleDateFormat")
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDate = inputFormat.parse(date);
            Date currentDate = Calendar.getInstance().getTime();

            Log.e("startDate =>", "" + startDate);
            Log.e("currentDate =>", "" + currentDate);

            //1 minute = 60 seconds
            //1 hour = 60 x 60 = 3600
            //1 day = 3600 x 24 = 86400
            //milliseconds
            long different = startDate.getTime() - currentDate.getTime();

            if (different > 0) {
                remainingTime = different;
            }
            Log.e("remainingTime =>", "" + remainingTime);

            //just for Information
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            System.out.printf("%d days, %d hours, %d minutes, %d seconds%n",
                    elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);

        } catch (Exception e) {
            Log.e("getDifference", "Exception => " + e);
        }
        return remainingTime;
    }

    public static String DateCheckWithToday(String savedDate) {
        String isToday = "", finaldate;
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
            Date input = inputFormat.parse(savedDate);
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            assert input != null;
            finaldate = outputFormat.format(input);

            Date exitdate = outputFormat.parse(finaldate);
            Date currdate = new Date();
            long diff = currdate.getTime() - exitdate.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            Log.e("=>days", "" + days);

            // test your condition
            if (days == 0) {
                isToday = "YES";
            } else {
                isToday = "NO";
            }
        } catch (Exception e) {
            Log.e("DateCheckWithToday", "Exception => " + e);
        }
        return isToday;
    }

    public static String covertTimeToText(String dataDate) {
        String convTime = null;
        String prefix = "";
        String suffix = "ago";

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date pasTime = dateFormat.parse(dataDate);

            Date nowTime = new Date();

            Log.e("==>pastTime", "" + pasTime.getTime());
            Log.e("==>nowTime", "" + nowTime.getTime());

            long dateDiff = nowTime.getTime() - pasTime.getTime();
            Log.e("==>dateDiff", "" + (dateDiff / 1000));

            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day = TimeUnit.MILLISECONDS.toDays(dateDiff);

            Log.e("==>second", "" + (second));
            Log.e("==>minute", "" + (minute));
            Log.e("==>hour", "" + (hour));
            Log.e("==>day", "" + (day));

            if (second < 60 && second > 0) {
                if (second == 1) {
                    convTime = second + " second " + suffix;
                } else {
                    convTime = second + " seconds " + suffix;
                }
            } else if (minute < 60 && minute > 0) {
                if (minute == 1) {
                    convTime = minute + " minute " + suffix;
                } else {
                    convTime = minute + " minutes " + suffix;
                }
            } else if (hour < 24 && hour > 0) {
                if (hour == 1) {
                    convTime = hour + " hour " + suffix;
                } else {
                    convTime = hour + " hours " + suffix;
                }
            } else if (day >= 7) {
                if (day > 360) {
                    convTime = (day / 360) + " years " + suffix;
                } else if (day > 30) {
                    convTime = (day / 30) + " months " + suffix;
                } else {
                    convTime = (day / 7) + " week " + suffix;
                }
            } else if (day < 7) {
                if (day == 1) {
                    convTime = day + " day " + suffix;
                } else {
                    convTime = day + " days " + suffix;
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("ConvTimeE", "" + e.getMessage());
        }

        return convTime;
    }

    //Image validation :
    public static boolean checkImage(String url) {
        if (!TextUtils.isEmpty(url)) {
            String[] splitedArray = url.split("\\.");
            String lastValueOfArray = splitedArray[splitedArray.length - 1];
            if (lastValueOfArray.equals("jpg") || lastValueOfArray.equals("jpeg") || lastValueOfArray.equals("JPG")
                    || lastValueOfArray.equals("JPEG") || lastValueOfArray.equals("png") || lastValueOfArray.equals("PNG")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    //check login status
    public static boolean checkLoginUser(Activity context) {
        com.divinetechs.ebooksapp.Utility.PrefManager prefManager = new com.divinetechs.ebooksapp.Utility.PrefManager(context);
        if (!prefManager.getLoginId().equalsIgnoreCase("0")) {
            return true;
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            context.overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
            return false;
        }
    }

    //Delete Folders
    public static void deleteFolder(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles()) {
                deleteFolder(child);
            }

        fileOrDirectory.delete();
    }

    //Clear Saved data From PrefManager
    public static void clearPrefManager(Activity activity) {
        com.divinetechs.ebooksapp.Utility.PrefManager prefManager = new com.divinetechs.ebooksapp.Utility.PrefManager(activity);
        /*Remove User Credentials*/
        prefManager.setLoginId("0");
        prefManager.setValue("Email", "");
        prefManager.setValue("userType", "");
        prefManager.setValue("fullname", "");
        prefManager.setValue("Phone", "");

        /*Remove Earn Rewards data*/
        prefManager.setWatch("WATCHED", "" + (new Date()) + "/0");
        prefManager.setDate("savedDate", "" + (new Date()));
        prefManager.setDay("DAY", "" + (new Date()) + "/0");

        /*Update Push Notification*/
        prefManager.setBool("PUSH", true);
    }

    // this will hide the bottom mobile navigation controll
    public static void HideNavigation(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            activity.getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = activity.getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }
    }

    public static void shimmerShow(ShimmerFrameLayout shimmer) {
        shimmer.setVisibility(View.VISIBLE);
        shimmer.showShimmer(true);
        shimmer.startShimmer();
    }

    public static void shimmerHide(ShimmerFrameLayout shimmer) {
        if (shimmer != null && shimmer.isShimmerVisible()) {
            shimmer.stopShimmer();
            shimmer.hideShimmer();
            shimmer.setVisibility(View.GONE);
        }
    }

    public static void ProgressBarShow(Context mContext) {
        if (pDialog == null) {
            pDialog = new ProgressDialog(mContext, R.style.AlertDialogDanger);
            pDialog.setMessage("" + mContext.getResources().getString(R.string.please_wait));
            pDialog.setCanceledOnTouchOutside(false);
        }
        pDialog.show();
    }

    public static void ProgressbarHide() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    /*
     * The following method we are using to generate a random string everytime
     * As we need a unique customer id and order id everytime
     * For real scenario you can implement it with your own application logic
     * */
    public static String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    //Change video views, like count , etc. to K,M,G...
    public static String changeToK(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f %c", count / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1));
    }

    public static Map<String, String> GetMap(Context context) {
        com.divinetechs.ebooksapp.Utility.PrefManager prefManager = new com.divinetechs.ebooksapp.Utility.PrefManager(context);

        Map<String, String> map = new HashMap<>();
        map.put("general_token", prefManager.getValue("general_token"));
        map.put("key", prefManager.getValue("key"));
        map.put("device_token", prefManager.getValue("device_token"));
        map.put("auth_token", prefManager.getValue_return("auth_token"));

        return map;
    }

    //Screen capture ON/OFF
    public static void screenCapOff(Activity activity) {
        //activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fcm_token", "empty");
    }

    /* Update User Profile missing data for Payments */
    //get Email, Name & Mobile Number From User
    public static void getMissingDataFromUser(Activity activity, String userType) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.add_missing_data_dialog, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setAnimationStyle(R.style.SheetDialog);
        popupWindow.setElevation(100);
        popupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0, 0);

        final LinearLayout lyClose = popupView.findViewById(R.id.lyClose);
        final LinearLayout lySubmit = popupView.findViewById(R.id.lySubmit);
        final LinearLayout lyEmail = popupView.findViewById(R.id.lyEmail);
        final LinearLayout lyMobile = popupView.findViewById(R.id.lyMobile);
        final LinearLayout lyFullName = popupView.findViewById(R.id.lyFullName);
        final TextInputEditText etEmail = popupView.findViewById(R.id.etEmail);
        final TextInputEditText etFullName = popupView.findViewById(R.id.etFullName);
        final TextInputEditText etMobile = popupView.findViewById(R.id.etMobile);
        final CountryCodePicker etCountryCodePicker = popupView.findViewById(R.id.etCountryCodePicker);
        etCountryCodePicker.setCountryForNameCode("IN");

        if (userType.equalsIgnoreCase("3")) {
            lyEmail.setVisibility(View.VISIBLE);
            lyMobile.setVisibility(View.GONE);
            lyFullName.setVisibility(View.VISIBLE);
        } else if (userType.equalsIgnoreCase("1") || userType.equalsIgnoreCase("2") || userType.equalsIgnoreCase("4")) {
            lyEmail.setVisibility(View.GONE);
            lyMobile.setVisibility(View.VISIBLE);
            lyFullName.setVisibility(View.GONE);
        } else {
            lyEmail.setVisibility(View.VISIBLE);
            lyMobile.setVisibility(View.VISIBLE);
            lyFullName.setVisibility(View.VISIBLE);
        }

        lyClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        lySubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = "" + etEmail.getText().toString().trim();
                String userName = "" + etFullName.getText().toString().trim();
                String mobileNumber = "" + etMobile.getText().toString().trim();
                String coutryCode = "" + etCountryCodePicker.getSelectedCountryCode();
                Log.e("email", "" + email);
                Log.e("mobileNumber", "" + mobileNumber);
                Log.e("coutryCode", "" + coutryCode);

                if (userType.equalsIgnoreCase("3")) {
                    if (TextUtils.isEmpty(email)) {
                        Toasty.warning(activity, "" + activity.getResources().getString(R.string.enter_email), Toasty.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(userName)) {
                        Toasty.warning(activity, "" + activity.getResources().getString(R.string.enter_your_name_profile), Toasty.LENGTH_SHORT).show();
                        return;
                    }
                } else if (userType.equalsIgnoreCase("1") || userType.equalsIgnoreCase("2") || userType.equalsIgnoreCase("4")) {
                    if (TextUtils.isEmpty(mobileNumber)) {
                        Toasty.warning(activity, "" + activity.getResources().getString(R.string.enter_your_mobile_no), Toasty.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(coutryCode)) {
                        Toasty.warning(activity, "" + activity.getResources().getString(R.string.enter_coutry_code), Toasty.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    if (TextUtils.isEmpty(email)) {
                        Toasty.warning(activity, "" + activity.getResources().getString(R.string.enter_email), Toasty.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(userName)) {
                        Toasty.warning(activity, "" + activity.getResources().getString(R.string.enter_your_name_profile), Toasty.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(mobileNumber)) {
                        Toasty.warning(activity, "" + activity.getResources().getString(R.string.enter_your_mobile_no), Toasty.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(coutryCode)) {
                        Toasty.warning(activity, "" + activity.getResources().getString(R.string.enter_coutry_code), Toasty.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (userType.equalsIgnoreCase("3")) {
                    UpdateEmailName(activity, "" + userName, "" + email, "" + mobileNumber);
                } else {
                    Intent intent = new Intent(activity, OTPVerification.class);
                    intent.putExtra("entryFrom", "Payment");
                    intent.putExtra("emailAddress", "" + email);
                    intent.putExtra("userName", "" + userName);
                    intent.putExtra("mobile", "+" + coutryCode + mobileNumber);
                    activity.startActivity(intent);
                }
                popupWindow.dismiss();

            }
        });
    }

    //updateprofile API
    public static void UpdateEmailName(Activity activity, String userName, String userEmail, String userMobile) {
        com.divinetechs.ebooksapp.Utility.PrefManager prefManager = new com.divinetechs.ebooksapp.Utility.PrefManager(activity);
        if (!((Activity) activity).isFinishing()) {
            Utils.ProgressBarShow(activity);
        }

        if (TextUtils.isEmpty(userMobile)) {
            userMobile = "";
        }

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<SuccessModel> call = bookNPlayAPI.updateMissingData("" + prefManager.getLoginId(), "" + userName, "" + userEmail, "" + userMobile);
        String finalUserMobile = userMobile;
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(@NonNull Call<SuccessModel> call, @NonNull Response<SuccessModel> response) {
                Utils.ProgressbarHide();
                try {
                    Log.e("updateMobileEmail", "status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Log.e("updateMobileEmail", "userEmail => " + userEmail);
                        Log.e("updateMobileEmail", "finalUserMobile => " + finalUserMobile);

                        prefManager.setValue("Email", "" + userEmail);

                        if (!TextUtils.isEmpty(userName)) {
                            prefManager.setValue("fullname", "" + userName);
                        }

                        if (prefManager.getValue("userType").equalsIgnoreCase("3")) {
                            prefManager.setValue("Phone", "" + finalUserMobile);
                        } else if (!TextUtils.isEmpty(finalUserMobile)) {
                            prefManager.setValue("Phone", "" + finalUserMobile);
                        }

                        Toasty.success(activity, "" + activity.getResources().getString(R.string.verification_success), Toasty.LENGTH_SHORT).show();
                    } else {
                        Utils.AlertDialog(activity, "" + response.body().getMessage(), false, true);
                    }
                } catch (Exception e) {
                    Log.e("updateMobileEmail", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<SuccessModel> call, @NonNull Throwable t) {
                Log.e("updateMobileEmail", "onFailure => " + t.getMessage());
                Utils.ProgressbarHide();
                Utils.AlertDialog(activity, "" + t.getMessage(), false, true);
            }
        });
    }

    //check Email & Mobile Number Available or Not
    public static boolean checkMissingData(Activity activity, String userType) {
        com.divinetechs.ebooksapp.Utility.PrefManager prefManager = new com.divinetechs.ebooksapp.Utility.PrefManager(activity);
        if (userType.equalsIgnoreCase("3")) {
            if (!TextUtils.isEmpty(prefManager.getValue("Email")) && !TextUtils.isEmpty(prefManager.getValue("fullname"))) {
                return true;
            } else {
                return false;
            }
        } else {
            if (!TextUtils.isEmpty(prefManager.getValue("Phone"))) {
                return true;
            } else {
                return false;
            }
        }
    }
    /* Update User Profile missing data for Payments */

}
