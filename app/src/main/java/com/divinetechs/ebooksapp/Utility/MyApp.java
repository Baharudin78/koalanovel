package com.divinetechs.ebooksapp.Utility;

import android.app.Application;
import android.content.Context;

import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.onesignal.OneSignal;
import com.orhanobut.hawk.Hawk;

public class MyApp extends Application {

    private static MyApp singleton = null;
    com.divinetechs.ebooksapp.Utility.PrefManager prefManager;

    @Override
    public void onCreate() {
        super.onCreate();

        singleton = this;
        prefManager = new com.divinetechs.ebooksapp.Utility.PrefManager(this);

        // Initialize the Google Mobile Ads SDK
        MobileAds.initialize(getApplicationContext());
        // Initialize the Audience Network SDK (Facebook ads)
        AudienceNetworkAds.initialize(this);
        //Initialize the Hawk for security downloads
        Hawk.init(getApplicationContext()).build();
        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        FirebaseApp.initializeApp(this);

    }

    public void initAppLanguage(Context context) {
        com.divinetechs.ebooksapp.Utility.LocaleUtils.initialize(context, com.divinetechs.ebooksapp.Utility.LocaleUtils.getSelectedLanguageId());
    }


    public static MyApp getPhotoApp() {
        return singleton;
    }

    public Context getContext() {
        return singleton.getContext();
    }

    public static MyApp getInstance() {
        if (singleton == null) {
            singleton = new MyApp();
        }
        return singleton;
    }

    public void setConnectivityListener(com.divinetechs.ebooksapp.Utility.ConnectivityReceiver.ConnectivityReceiverListener listener) {
        com.divinetechs.ebooksapp.Utility.ConnectivityReceiver.connectivityReceiverListener = listener;
    }

}