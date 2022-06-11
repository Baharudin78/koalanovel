package com.divinetechs.ebooksapp.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Adapter.BookChapterAdapter;
import com.divinetechs.ebooksapp.Interface.ItemClickListener;
import com.divinetechs.ebooksapp.Model.ProfileModel.ProfileModel;
import com.divinetechs.ebooksapp.Model.SuccessModel.SuccessModel;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.DownloadEpub;
import com.divinetechs.ebooksapp.Utility.Functions;
import com.divinetechs.ebooksapp.Utility.PrefManager;
import com.divinetechs.ebooksapp.Utility.Utils;
import com.divinetechs.ebooksapp.Webservice.AppAPI;
import com.divinetechs.ebooksapp.Webservice.BaseURL;
import com.facebook.ads.Ad;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookChapters extends AppCompatActivity implements ItemClickListener {

    PrefManager prefManager;
    ShimmerFrameLayout shimmer;
    LayoutInflater inflater;

    TextView txtToolbarTitle;
    LinearLayout lyToolbar, lyBack, lyFbAdView, ly_dataNotFound;

    RelativeLayout rlAdView;
    private InterstitialAd mInterstitialAd = null;
    private com.facebook.ads.InterstitialAd fbInterstitialAd = null;
    private com.facebook.ads.AdView fbAdView = null;
    private AdView mAdView = null;

    RecyclerView rv_chapters;
    BookChapterAdapter bookChapterAdapter;

    String walletBalance;
    int clickPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.screenCapOff(BookChapters.this);
        if (PrefManager.getInstance(this).isNightModeEnabled() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_book_chapters);
        PrefManager.forceRTLIfSupported(getWindow(), BookChapters.this);

        init();
        GetProfile();

        Intent intent = getIntent();
        if (intent.hasExtra("Title")) {
            txtToolbarTitle.setText("" + intent.getStringExtra("Title"));
            Log.e("Chapter size ==>", "" + com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.size());
        }

        lyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookChapters.this.finish();
            }
        });

        Log.e("banner_ad", "" + prefManager.getValue("banner_ad"));
        if (prefManager.getValue("banner_ad").equalsIgnoreCase("yes")) {
            rlAdView.setVisibility(View.VISIBLE);
            Utils.Admob(BookChapters.this, mAdView, prefManager.getValue("banner_adid"), rlAdView);
        } else {
            rlAdView.setVisibility(View.GONE);
        }

        Log.e("fb_banner_ad", "" + prefManager.getValue("fb_banner_ad"));
        if (prefManager.getValue("fb_banner_ad").equalsIgnoreCase("yes")) {
            lyFbAdView.setVisibility(View.VISIBLE);
            Utils.FacebookBannerAd(BookChapters.this, fbAdView, "" + prefManager.getValue("fb_banner_id"), lyFbAdView);
        } else {
            lyFbAdView.setVisibility(View.GONE);
        }

    }

    private void init() {
        try {
            prefManager = new PrefManager(BookChapters.this);

            shimmer = findViewById(R.id.shimmer);
            rlAdView = findViewById(R.id.rlAdView);
            lyFbAdView = findViewById(R.id.lyFbAdView);
            lyToolbar = findViewById(R.id.lyToolbar);
            lyBack = findViewById(R.id.lyBack);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);

            rv_chapters = findViewById(R.id.rv_chapters);
            ly_dataNotFound = findViewById(R.id.ly_dataNotFound);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    private void AdInit() {
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

    private void GetProfile() {
        Utils.shimmerShow(shimmer);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<ProfileModel> call = bookNPlayAPI.profile("" + prefManager.getLoginId());
        call.enqueue(new Callback<ProfileModel>() {
            @Override
            public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        walletBalance = response.body().getResult().get(0).getCoinBalance();
                        Log.e("=>walletBalance", "" + walletBalance);

                        SetChapter();
                    }
                } catch (Exception e) {
                    Log.e("profile", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<ProfileModel> call, Throwable t) {
                Utils.shimmerHide(shimmer);
                Log.e("profile", "onFailure => " + t.getMessage());
            }
        });
    }

    private void SetChapter() {
        if (com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.size() > 0) {
            bookChapterAdapter = new BookChapterAdapter(BookChapters.this, com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList,
                    "" + prefManager.getValue("currency_symbol"), BookChapters.this);
            rv_chapters.setLayoutManager(new GridLayoutManager(BookChapters.this, 1));
            rv_chapters.setAdapter(bookChapterAdapter);
            bookChapterAdapter.notifyDataSetChanged();

            rv_chapters.setVisibility(View.VISIBLE);
            ly_dataNotFound.setVisibility(View.GONE);
        } else {
            rv_chapters.setVisibility(View.GONE);
            ly_dataNotFound.setVisibility(View.VISIBLE);
        }

        Utils.shimmerHide(shimmer);
    }

    @Override
    public void onItemClick(int position) {
        clickPos = position;
        Log.e("==>walletBalance", "" + walletBalance);
        Log.e("==>position", "" + position);
        Log.e("==>author_id", "" + com.divinetechs.ebooksapp.Activity.BookDetails.BookList.get(0).getAuthorId());
        Log.e("==>user_id", "" + prefManager.getLoginId());
        Log.e("==>amount", "" + com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.get(position).getPrice());
        Log.e("==>book_chapter_id", "" + com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.get(position).getId());
        Log.e("==>book_id", "" + com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.get(position).getBookId());
        Log.e("==>book_url", "" + com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.get(position).getUrl());
        Log.e("==>IsBuy", "" + com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.get(position).getIsBuy());

        if (Utils.checkLoginUser(BookChapters.this)) {
            if (Integer.parseInt(com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.get(position).getPrice()) > 0) {

                if (com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.get(position).getIsBuy() == 0) {
                    if (walletBalance.equalsIgnoreCase("0")) {
                        Log.e("walletBalance =>", "" + walletBalance);
                        Toasty.info(BookChapters.this, "" + getResources().getString(R.string.your_balance_is_low_please_top_up_your_wallet), Toasty.LENGTH_LONG).show();
                    } else {
                        AlertDialog(position);
                    }
                } else {
                    if (prefManager.getValue("fb_interstiatial_status").equalsIgnoreCase("on")) {
                        if (fbInterstitialAd != null && fbInterstitialAd.isAdLoaded()) {
                            fbInterstitialAd.show();
                        } else {
                            ReadBook(position);
                        }
                    } else {
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(BookChapters.this);
                        } else {
                            ReadBook(position);
                        }
                    }
                }

            } else {
                if (prefManager.getValue("fb_interstiatial_status").equalsIgnoreCase("on")) {
                    if (fbInterstitialAd != null && fbInterstitialAd.isAdLoaded()) {
                        fbInterstitialAd.show();
                    } else {
                        ReadBook(position);
                    }
                } else {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(BookChapters.this);
                    } else {
                        ReadBook(position);
                    }
                }
            }
        }

    }

    private void AlertDialog(int pos) {
        inflater = (LayoutInflater) BookChapters.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.alert_dialog, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setElevation(100);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        TextView txtTitle = popupView.findViewById(R.id.txtTitle);
        TextView txtPrice = popupView.findViewById(R.id.txtPrice);
        TextView txtDescription = popupView.findViewById(R.id.txtDescription);
        Button btnNegative = popupView.findViewById(R.id.btnNegative);
        Button btnPositive = popupView.findViewById(R.id.btnPositive);

        txtPrice.setText(prefManager.getValue("currency_symbol") + "" + com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.get(pos).getPrice());
        txtTitle.setText("" + com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.get(pos).getTitle());
        txtDescription.setText("" + getResources().getString(R.string.are_you_sure_want_to_purchase));

        btnPositive.setText("" + getResources().getString(R.string.purchase_now));
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                PurchaseChapter(pos);
            }
        });

        btnNegative.setText("" + getResources().getString(R.string.may_be_later));
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    private void PurchaseChapter(int pos) {
        Utils.ProgressBarShow(BookChapters.this);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<SuccessModel> call = bookNPlayAPI.add_chapter_transaction(com.divinetechs.ebooksapp.Activity.BookDetails.BookList.get(0).getAuthorId(),
                prefManager.getLoginId(), com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.get(pos).getPrice(),
                com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.get(pos).getId(), com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.get(pos).getBookId());
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                Utils.ProgressbarHide();
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Log.e("PurchaseBook", "" + response.body().getMessage());

                        AlertDialog alertDialog = new AlertDialog.Builder(BookChapters.this, R.style.AlertDialogDanger).create();
                        alertDialog.setTitle("" + getResources().getString(R.string.app_name));
                        alertDialog.setMessage("" + response.body().getMessage());
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "" + getResources().getString(R.string.okay), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.get(pos).setIsBuy(1);
                                bookChapterAdapter.notifyItemChanged(pos);
                            }
                        });
                        alertDialog.show();
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(BookChapters.this, R.style.AlertDialogDanger).create();
                        alertDialog.setTitle("" + getResources().getString(R.string.app_name));
                        alertDialog.setMessage("" + response.body().getMessage());
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "" + getResources().getString(R.string.okay), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }
                } catch (Exception e) {
                    Log.e("add_chapter_transaction", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                Log.e("add_chapter_transaction", "onFailure => " + t.getMessage());
                Utils.ProgressbarHide();
            }
        });
    }

    private void ReadBook(int position) {
        try {
            if (Functions.isConnectedToInternet(BookChapters.this)) {
                Log.e("url_data", "" + com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.get(position).getUrl().contains(".EPUB"));
                if (com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.get(position).getUrl().contains(".epub") ||
                        com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.get(position).getUrl().contains(".EPUB")) {

                    DownloadEpub downloadEpub = new DownloadEpub(BookChapters.this);
                    downloadEpub.pathEpub(com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.get(position).getUrl(),
                            com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.get(position).getId(), "book", false);

                } else if (com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.get(position).getUrl().contains(".pdf") ||
                        com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.get(position).getUrl().contains(".PDF")) {

                    startActivity(new Intent(BookChapters.this, com.divinetechs.ebooksapp.Activity.PDFShow.class)
                            .putExtra("link", com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.get(position).getUrl())
                            .putExtra("toolbarTitle", com.divinetechs.ebooksapp.Activity.BookDetails.BookChapterList.get(position).getTitle())
                            .putExtra("type", "link"));
                }
            } else {
                Toasty.error(BookChapters.this, getResources().getString(R.string.internet_connection), Toasty.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("Exception-Read", "" + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdInit();
    }

    private void InterstitialAd() {
        try {
            AdRequest adRequest = new AdRequest.Builder().build();

            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    Log.e("Ad failed to show.", "" + adError.toString());
                    mInterstitialAd = null;
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    Log.e("TAG", "Ad was shown.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    Log.e("TAG", "Ad was dismissed.");
                    mInterstitialAd = null;
                    ReadBook(clickPos);
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    Log.e("TAG", "onAdImpression.");
                }
            };

            mInterstitialAd.load(this, "" + prefManager.getValue("interstital_adid"),
                    adRequest, new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            Log.e("TAG", "onAdLoaded");
                            mInterstitialAd = interstitialAd;
                            mInterstitialAd.setFullScreenContentCallback(fullScreenContentCallback);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.e("TAG", "onAdFailedToLoad => " + loadAdError.getMessage());
                            mInterstitialAd = null;
                            ReadBook(clickPos);
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
            fbInterstitialAd.loadAd(fbInterstitialAd.buildLoadAdConfig()
                    .withAdListener(new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(Ad ad) {
                            Log.e("TAG", "fb ad displayed.");
                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {
                            fbInterstitialAd = null;
                            ReadBook(clickPos);
                            Log.e("TAG", "fb ad dismissed.");
                        }

                        @Override
                        public void onError(Ad ad, com.facebook.ads.AdError adError) {
                            Log.e("TAG", "fb ad failed to load : " + adError.getErrorMessage());
                            fbInterstitialAd = null;
                            ReadBook(clickPos);
                        }

                        @Override
                        public void onAdLoaded(Ad ad) {
                            Log.d("TAG", "fb ad is loaded and ready to be displayed!");
                        }

                        @Override
                        public void onAdClicked(Ad ad) {
                            Log.d("TAG", "fb ad clicked!");
                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {
                            Log.d("TAG", "fb ad impression logged!");
                        }
                    })
                    .build());
        } catch (Exception e) {
            Log.e("fb Interstial", "Exception =>" + e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (mInterstitialAd != null) {
            mInterstitialAd = null;
        }
        if (fbInterstitialAd != null) {
            fbInterstitialAd.destroy();
            fbInterstitialAd = null;
        }
        if (fbAdView != null) {
            fbAdView.destroy();
        }
    }

}