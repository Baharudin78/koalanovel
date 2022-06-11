package com.divinetechs.ebooksapp.Utility;

import com.divinetechs.ebooksapp.BuildConfig;
import com.paypal.android.sdk.payments.PayPalConfiguration;

public class Constant {

    public static String BASE_URL = "https://koalanovel.id";

    public static String PURCHASED_CODE = "";


    /* Paypal Credintial */
    public static String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    public static final String PAYPAL_CLIENT_ID = "";

    /* Test FlutterWave*/
    public static final String FWPublic_Key = "";
    public static final String FWEncryption_Key = "";

    /* Live FlutterWave*/
    //public static final String FWPublic_Key = "";
    //public static final String FWEncryption_Key = "";

    /* Test PayUMoney*/
    public static final String PayUMerchant_ID = "";
    public static final String PayUMerchant_Key = "";
    public static final String PayUMerchant_Salt = "";
    public static final boolean PayU_isTest = true;

    /* Live PayUMoney*/
    //public static final String PayUMerchant_ID = "";
    //public static final String PayUMerchant_Key = "";
    //public static final String PayUMerchant_Salt = "";
    //public static final boolean PayU_isTest=false;

    /* Test PayTm*/
    public static final String PayTmMerchant_ID = "";
    public static final String PayTmMerchant_Key = "";

    /* Live PayTm*/
    //public static final String PayTmMerchant_ID = "";
    //public static final String PayTmMerchant_Key = "";

    /* PayTm Parameters */
    public static final String M_ID = PayTmMerchant_ID; //Paytm Merchand Id (Test/Live), get it from paytm credentials
    public static final String CHANNEL_ID = "WAP"; //Paytm Channel Id (Test/Live), get it from paytm credentials
    public static final String INDUSTRY_TYPE_ID = "Retail"; //Paytm industry type, get it from paytm credential
    public static final String WEBSITE = "DEFAULT";
    public static final String CALLBACK_URL = BuildConfig.PAYTM_CALLBACK_URL;

    public static String strSearch = "";

    // set the profile image max size for now it is 1000 * 1000
    public static final int PROFILE_IMAGE_SIZE = 1000;
    //Select pic or not
    public static boolean isSelectPic = false;

}
