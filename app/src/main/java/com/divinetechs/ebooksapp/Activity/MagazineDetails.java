package com.divinetechs.ebooksapp.Activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Adapter.GiftAdapter;
import com.divinetechs.ebooksapp.Adapter.MagazineCommentAdapter;
import com.divinetechs.ebooksapp.Adapter.MagazineRelatedAdapter;
import com.divinetechs.ebooksapp.BuildConfig;
import com.divinetechs.ebooksapp.Model.CommentModel.CommentModel;
import com.divinetechs.ebooksapp.Model.DownloadedItemModel;
import com.divinetechs.ebooksapp.Model.MagazineModel.MagazineModel;
import com.divinetechs.ebooksapp.Model.MagazineModel.Result;
import com.divinetechs.ebooksapp.Model.ProfileModel.ProfileModel;
import com.divinetechs.ebooksapp.Model.SuccessModel.SuccessModel;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.DownloadEpub;
import com.divinetechs.ebooksapp.Utility.Functions;
import com.divinetechs.ebooksapp.Utility.PrefManager;
import com.divinetechs.ebooksapp.Utility.Utils;
import com.divinetechs.ebooksapp.Webservice.AppAPI;
import com.divinetechs.ebooksapp.Webservice.BaseURL;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.facebook.ads.Ad;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeBannerAd;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
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
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.orhanobut.hawk.Hawk;
import com.paginate.Paginate;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MagazineDetails extends AppCompatActivity implements View.OnClickListener, Paginate.Callbacks {

    PrefManager prefManager;
    ShimmerFrameLayout shimmer;
    LayoutInflater inflater;

    TextView txt_title, txt_price, txt_by_author, txt_category, txt_descripation, txt_read, txtBuyNow, txt_bookmark, txt_drop_down,
            txt_drop_up, txt_show_all, txt_show_info, txt_hide_info, txt_publish_at, txt_author_name;
    ImageView iv_thumb;
    LinearLayout ly_showAll_desc, ly_back, ly_share, ly_comment_viewAll, ly_add_comment, ly_copyright_info, ly_author_publish, lyDownload;
    RecyclerView rv_comment, rv_related;
    SimpleRatingBar ratingbar;

    List<Result> magazineList;
    List<com.divinetechs.ebooksapp.Model.CommentModel.Result> commentList;
    List<Result> relatedList;

    MagazineCommentAdapter magazineCommentAdapter;
    GiftAdapter giftAdapter;
    MagazineRelatedAdapter magazineRelatedAdapter;

    RelativeLayout rl_buy_read;
    private TemplateView nativeTemplate = null;
    private NativeBannerAd fbNativeBannerAd = null;
    private NativeAdLayout fbNativeTemplate = null;
    private InterstitialAd mInterstitialAd = null;
    private RewardedVideoAd fbRewardedVideoAd = null;
    private RewardedAd mRewardedAd = null;
    private com.facebook.ads.InterstitialAd fbInterstitialAd = null;

    String ID, fcat_id, TYPE = "", walletBalance = "", fileImage = "";
    private boolean loading = false;
    private int page = 1, totalPages = 1;
    private Paginate paginate;
    File pdfFile = null, imageFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.screenCapOff(MagazineDetails.this);
        if (PrefManager.getInstance(this).isNightModeEnabled() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.magazinedetails);
        PrefManager.forceRTLIfSupported(getWindow(), MagazineDetails.this);

        init();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ID = bundle.getString("ID");
            Log.e("ID", "" + ID);
        }

        //Advertisement
        Log.e("native_ad", "" + prefManager.getValue("native_ad"));
        if (prefManager.getValue("native_ad").equalsIgnoreCase("yes")) {
            nativeTemplate.setVisibility(View.VISIBLE);
            Utils.NativeAds(MagazineDetails.this, nativeTemplate, "" + prefManager.getValue("native_adid"));
        } else {
            nativeTemplate.setVisibility(View.GONE);
        }

        Log.e("fb_native_status", "" + prefManager.getValue("fb_native_status"));
        if (prefManager.getValue("fb_native_status").equalsIgnoreCase("on")) {
            fbNativeTemplate.setVisibility(View.VISIBLE);
            Utils.FacebookNativeAdSmall(MagazineDetails.this, fbNativeBannerAd, fbNativeTemplate, "" + prefManager.getValue("fb_native_id"));
        } else {
            fbNativeTemplate.setVisibility(View.GONE);
        }

    }

    private void init() {
        try {
            prefManager = new PrefManager(MagazineDetails.this);

            shimmer = findViewById(R.id.shimmer);
            ratingbar = findViewById(R.id.ratingbar);

            nativeTemplate = findViewById(R.id.nativeTemplate);
            fbNativeTemplate = findViewById(R.id.fbNativeTemplate);
            txt_title = findViewById(R.id.txt_title);
            txt_price = findViewById(R.id.txt_price);
            txt_by_author = findViewById(R.id.txt_by_author);
            txt_category = findViewById(R.id.txt_category);
            txt_descripation = findViewById(R.id.txt_descripation);
            iv_thumb = findViewById(R.id.iv_thumb);
            txt_bookmark = findViewById(R.id.txt_bookmark);

            rl_buy_read = findViewById(R.id.rl_buy_read);
            lyDownload = findViewById(R.id.lyDownload);
            ly_add_comment = findViewById(R.id.ly_add_comment);
            ly_comment_viewAll = findViewById(R.id.ly_comment_viewAll);
            ly_copyright_info = findViewById(R.id.ly_copyright_info);
            ly_author_publish = findViewById(R.id.ly_author_publish);
            ly_showAll_desc = findViewById(R.id.ly_showAll_desc);
            txt_drop_down = findViewById(R.id.txt_drop_down);
            txt_drop_up = findViewById(R.id.txt_drop_up);
            txt_show_all = findViewById(R.id.txt_show_all);
            txt_show_info = findViewById(R.id.txt_show_info);
            txt_hide_info = findViewById(R.id.txt_hide_info);
            txt_author_name = findViewById(R.id.txt_author_name);
            txt_publish_at = findViewById(R.id.txt_publish_at);

            rv_comment = findViewById(R.id.rv_comment);
            rv_related = findViewById(R.id.rv_related);
            txt_read = findViewById(R.id.txt_read);
            txtBuyNow = findViewById(R.id.txtBuyNow);
            ly_back = findViewById(R.id.ly_back);
            ly_share = findViewById(R.id.ly_share);

            ly_back.setOnClickListener(this);
            ly_share.setOnClickListener(this);
            txt_bookmark.setOnClickListener(this);
            txt_by_author.setOnClickListener(this);
            txt_read.setOnClickListener(this);
            txtBuyNow.setOnClickListener(this);
            ly_showAll_desc.setOnClickListener(this);
            ly_comment_viewAll.setOnClickListener(this);
            ly_copyright_info.setOnClickListener(this);
            ly_add_comment.setOnClickListener(this);
            lyDownload.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init Exception =>", "" + e);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_back:
                finish();
                break;

            case R.id.ly_share:
                ShowAdByClick("Share");
                break;

            case R.id.txt_bookmark:
                if (Functions.isConnectedToInternet(MagazineDetails.this)) {
                    if (Utils.checkLoginUser(MagazineDetails.this)) {
                        AddBookMark();
                    }
                } else {
                    Toasty.warning(MagazineDetails.this, getResources().getString(R.string.internet_connection), Toasty.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_by_author:
                Intent intent = new Intent(MagazineDetails.this, AuthorBookList.class);
                intent.putExtra("a_id", magazineList.get(0).getId());
                intent.putExtra("a_name", magazineList.get(0).getTitle());
                intent.putExtra("a_bio", magazineList.get(0).getAuthorName());
                intent.putExtra("a_image", magazineList.get(0).getImage());
                startActivity(intent);
                break;

            case R.id.txt_read:
                if (Functions.isConnectedToInternet(MagazineDetails.this)) {
                    try {
                        ShowAdByClick("Read");
                    } catch (Exception e) {
                        Log.e("Exception", "" + e.getMessage());
                    }
                } else {
                    Toasty.warning(MagazineDetails.this, getResources().getString(R.string.internet_connection), Toasty.LENGTH_SHORT).show();
                }
                break;

            case R.id.lyDownload:
            case R.id.txtBuyNow:
                if (Functions.isConnectedToInternet(MagazineDetails.this)) {
                    ShowAdByClick("Download");
                } else {
                    Toasty.warning(MagazineDetails.this, getResources().getString(R.string.internet_connection), Toasty.LENGTH_SHORT).show();
                }
                break;

            case R.id.ly_showAll_desc:
                if (txt_show_all.getText().toString().equalsIgnoreCase("" + getResources().getString(R.string.show_all))) {
                    ObjectAnimator animation = ObjectAnimator.ofInt(txt_descripation, "maxLines", txt_descripation.getLineCount());
                    animation.setDuration(200).start();
                    txt_show_all.setText("" + getResources().getString(R.string.pack_up));
                    txt_drop_down.setVisibility(View.GONE);
                    txt_drop_up.setVisibility(View.VISIBLE);
                } else {
                    ObjectAnimator animation = ObjectAnimator.ofInt(txt_descripation, "maxLines", 3);
                    animation.setDuration(200).start();
                    txt_show_all.setText("" + getResources().getString(R.string.show_all));
                    txt_drop_down.setVisibility(View.VISIBLE);
                    txt_drop_up.setVisibility(View.GONE);
                }
                break;

            case R.id.ly_comment_viewAll:
                Intent cIntent = new Intent(MagazineDetails.this, CommentViewAll.class);
                cIntent.putExtra("ID", ID);
                cIntent.putExtra("TYPE", "Magazine");
                startActivity(cIntent);
                break;

            case R.id.ly_copyright_info:
                if (txt_show_info.getVisibility() == View.VISIBLE) {
                    txt_hide_info.setVisibility(View.VISIBLE);
                    txt_show_info.setVisibility(View.GONE);
                    ly_author_publish.setVisibility(View.VISIBLE);
                } else {
                    txt_hide_info.setVisibility(View.GONE);
                    txt_show_info.setVisibility(View.VISIBLE);
                    ly_author_publish.setVisibility(View.GONE);
                }
                break;

            case R.id.ly_add_comment:
                if (Utils.checkLoginUser(MagazineDetails.this)) {
                    commentDialog();
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        MagazineDetails();
        GetProfile();
    }

    private void GetProfile() {
        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<ProfileModel> call = bookNPlayAPI.profile("" + prefManager.getLoginId());
        call.enqueue(new Callback<ProfileModel>() {
            @Override
            public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        walletBalance = response.body().getResult().get(0).getCoinBalance();
                    }
                } catch (Exception e) {
                    Log.e("profile", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<ProfileModel> call, Throwable t) {
                Utils.shimmerHide(shimmer);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        TYPE = "";
        AdInit();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("ID", "" + ID);
        Log.e("Save-ID", "" + ID);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ID = savedInstanceState.getString("ID");
        Log.e("Restore-ID", "" + ID);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    private void MagazineDetails() {
        Utils.shimmerShow(shimmer);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<MagazineModel> call = bookNPlayAPI.magazinedetails(ID, prefManager.getLoginId());
        call.enqueue(new Callback<MagazineModel>() {
            @Override
            public void onResponse(Call<MagazineModel> call, Response<MagazineModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult().size() > 0) {

                            magazineList = new ArrayList<Result>();
                            magazineList = response.body().getResult();
                            fcat_id = response.body().getResult().get(0).getCategoryId();

                            Log.e("fcat_id", "" + fcat_id);
                            txt_title.setText("" + magazineList.get(0).getTitle());

                            if (magazineList.get(0).getIsPaid().equalsIgnoreCase("1")) {
                                txt_price.setText(" |  " + prefManager.getValue("currency_symbol")
                                        + " " + magazineList.get(0).getPrice());
                            } else {
                                txt_price.setText(" |  " + getResources().getString(R.string.free));
                            }

                            txt_by_author.setText(getResources().getString(R.string.by) + " " + magazineList.get(0).getAuthorName());
                            txt_category.setText("" + magazineList.get(0).getCategoryName());
                            txt_descripation.setText("" + Html.fromHtml(magazineList.get(0).getDescription()));
                            txt_author_name.setText("" + magazineList.get(0).getAuthorName());
                            txt_publish_at.setText(Utils.DateFormat2(magazineList.get(0).getUpdatedAt()));

                            Log.e("Avg_Ratings =>", "" + magazineList.get(0).getAvgRating());
                            ratingbar.setRating(Float.parseFloat(magazineList.get(0).getAvgRating()));

                            Log.e("IsBuy =>", "" + magazineList.get(0).getIsBuy());
                            Log.e("IsPaid =>", "" + magazineList.get(0).getIsPaid());
                            if (magazineList.get(0).getIsBuy() == 0) {
                                if (magazineList.get(0).getIsPaid().equalsIgnoreCase("1")) {
                                    txtBuyNow.setVisibility(View.VISIBLE);
                                    txt_read.setVisibility(View.GONE);
                                    lyDownload.setVisibility(View.INVISIBLE);
                                    txtBuyNow.setText("" + getResources().getString(R.string.buy_now));
                                } else {
                                    txt_read.setVisibility(View.VISIBLE);
                                    txtBuyNow.setVisibility(View.GONE);
                                    txt_read.setText("" + getResources().getString(R.string.read_now));
                                    lyDownload.setVisibility(View.VISIBLE);
                                }
                            } else {
                                lyDownload.setVisibility(View.VISIBLE);
                                txt_read.setVisibility(View.VISIBLE);
                                txtBuyNow.setVisibility(View.GONE);
                                txt_read.setText("" + getResources().getString(R.string.read_now));
                            }

                            Picasso.get().load(magazineList.get(0).getImage()).into(iv_thumb);

                            relatedList = new ArrayList<>();
                            setupPagination();
                            RelatedItem(page);
                            Comments();
                            CheckBookMark();
                        } else {
                            Utils.shimmerHide(shimmer);
                        }
                    } else {
                        Utils.shimmerHide(shimmer);
                    }
                } catch (Exception e) {
                    Log.e("magazineDetails", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<MagazineModel> call, Throwable t) {
                Log.e("magazineDetails", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
            }
        });
    }

    private void RelatedItem(int pageNo) {
        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<MagazineModel> call = bookNPlayAPI.magazine_by_category(fcat_id, "" + pageNo);
        call.enqueue(new Callback<MagazineModel>() {
            @Override
            public void onResponse(Call<MagazineModel> call, Response<MagazineModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        totalPages = response.body().getTotalPage();
                        Log.e("totalPages", "" + totalPages);

                        if (response.body().getResult().size() > 0) {
                            relatedList = response.body().getResult();
                            Log.e("Related_Item", "" + relatedList.size());

                            rv_related.setVisibility(View.VISIBLE);
                            loading = false;
                            magazineRelatedAdapter.addBook(relatedList);
                        } else {
                            rv_related.setVisibility(View.GONE);
                            loading = false;
                        }

                    } else {
                        rv_related.setVisibility(View.GONE);
                        loading = false;
                    }
                } catch (Exception e) {
                    Log.e("magazine_by_category", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<MagazineModel> call, Throwable t) {
                Log.e("magazine_by_category", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
                if (!loading) {
                    rv_related.setVisibility(View.GONE);
                }
                loading = false;
            }
        });
    }

    private void Comments() {
        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<CommentModel> call = bookNPlayAPI.view_magazine_comment(ID, "1");
        call.enqueue(new Callback<CommentModel>() {
            @Override
            public void onResponse(Call<CommentModel> call, Response<CommentModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult().size() > 0) {
                            commentList = new ArrayList<>();
                            commentList = response.body().getResult();
                            Log.e("CommentList", "" + commentList.size());

                            if (commentList.size() > 5) {
                                magazineCommentAdapter = new MagazineCommentAdapter(MagazineDetails.this, commentList, "Max_5");
                            } else {
                                magazineCommentAdapter = new MagazineCommentAdapter(MagazineDetails.this, commentList, "");
                            }
                            rv_comment.setLayoutManager(new GridLayoutManager(MagazineDetails.this, 1));
                            rv_comment.setAdapter(magazineCommentAdapter);
                            magazineCommentAdapter.notifyDataSetChanged();

                            rv_comment.setVisibility(View.VISIBLE);
                        } else {
                            rv_comment.setVisibility(View.GONE);
                        }

                    } else {
                        rv_comment.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e("view_magazine_comment", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
                Utils.ProgressbarHide();
            }

            @Override
            public void onFailure(Call<CommentModel> call, Throwable t) {
                Log.e("view_magazine_comment", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
                Utils.ProgressbarHide();
                rv_comment.setVisibility(View.GONE);
            }
        });
    }

    private void AddBookMark() {
        Utils.ProgressBarShow(MagazineDetails.this);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<SuccessModel> call = bookNPlayAPI.add_magazine_bookmark(prefManager.getLoginId(), ID);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Log.e("AddBookmark", "" + response.body().getMessage());
                        txt_bookmark.setText("" + getResources().getString(R.string.remove_from_library));
                    } else {
                        txt_bookmark.setText("" + getResources().getString(R.string.add_to_library));
                    }
                    Toasty.success(MagazineDetails.this, "" + response.body().getMessage(), Toasty.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("add_magazine_bookmark", "Exception => " + e);
                }
                Utils.ProgressbarHide();
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                Log.e("add_magazine_bookmark", "onFailure => " + t.getMessage());
                Utils.ProgressbarHide();
            }
        });
    }

    private void CheckBookMark() {
        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<SuccessModel> call = bookNPlayAPI.check_magazine_bookmark(prefManager.getLoginId(), ID);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Log.e("check_magazine_bookmark", "" + response.body().getMessage());
                        txt_bookmark.setText("" + getResources().getString(R.string.remove_from_library));
                    } else {
                        txt_bookmark.setText("" + getResources().getString(R.string.add_to_library));
                    }
                } catch (Exception e) {
                    Log.e("check_magazine_bookmark", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
                Utils.ProgressbarHide();
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                Log.e("check_magazine_bookmark", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
                Utils.ProgressbarHide();
            }
        });
    }

    private void AddComments(String comment) {
        Utils.ProgressBarShow(MagazineDetails.this);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<SuccessModel> call = bookNPlayAPI.add_magazine_comment(ID,
                prefManager.getLoginId(), comment);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                Utils.ProgressbarHide();
                try {
                    Log.e("Add Comments", "" + response.body().getMessage());
                    Toasty.success(MagazineDetails.this, "" + response.body().getMessage(), Toasty.LENGTH_SHORT).show();
                    Comments();
                } catch (Exception e) {
                    Log.e("add_magazine_comment", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                Log.e("add_magazine_comment", "onFailure => " + t.getMessage());
                Utils.ProgressbarHide();
                Utils.shimmerHide(shimmer);
            }
        });
    }

    private void Addrating(Float aFloat) {
        Utils.ProgressBarShow(MagazineDetails.this);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<SuccessModel> call = bookNPlayAPI.add_magazine_rating(prefManager.getLoginId(), ID, "" + aFloat);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Log.e("AddRating", "" + response);
                        Toasty.success(MagazineDetails.this, "" + response.body().getMessage(), Toasty.LENGTH_SHORT).show();
                    } else {
                        Toasty.info(MagazineDetails.this, "" + response.body().getMessage(), Toasty.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("add_magazine_rating", "Exception => " + e);
                }
                Utils.ProgressbarHide();
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                Log.e("add_magazine_rating", "onFailure => " + t.getMessage());
                Utils.ProgressbarHide();
                Utils.shimmerHide(shimmer);
            }
        });
    }

    private void ReadMagazine() {
        try {
            if (Functions.isConnectedToInternet(MagazineDetails.this)) {
                Log.e("url_data", "" + magazineList.get(0).getUrl().contains(".EPUB"));
                if (magazineList.get(0).getUrl().contains(".epub") || magazineList.get(0).getUrl().contains(".EPUB")) {

                    if (txt_read.getText().toString().equalsIgnoreCase("" + getResources().getString(R.string.read_now))) {
                        if (Utils.checkLoginUser(MagazineDetails.this)) {
                            DownloadEpub downloadEpub = new DownloadEpub(MagazineDetails.this);
                            downloadEpub.pathEpub(magazineList.get(0).getUrl(), magazineList.get(0).getId(), "magazine", false);
                        }
                    } else {
                        if (Utils.checkLoginUser(MagazineDetails.this)) {
                            DownloadEpub downloadEpub = new DownloadEpub(MagazineDetails.this);
                            downloadEpub.pathEpub(magazineList.get(0).getUrl(), magazineList.get(0).getId(), "magazine", false);
                        }
                    }

                } else if (magazineList.get(0).getUrl().contains(".pdf") || magazineList.get(0).getUrl().contains(".PDF")) {
                    if (txt_read.getText().toString().equalsIgnoreCase("" + getResources().getString(R.string.read_now))) {
                        if (Utils.checkLoginUser(MagazineDetails.this)) {
                            startActivity(new Intent(MagazineDetails.this, com.divinetechs.ebooksapp.Activity.PDFShow.class)
                                    .putExtra("link", magazineList.get(0).getUrl())
                                    .putExtra("toolbarTitle", magazineList.get(0).getTitle())
                                    .putExtra("type", "link"));
                        }
                    } else {
                        if (Utils.checkLoginUser(MagazineDetails.this)) {
                            startActivity(new Intent(MagazineDetails.this, com.divinetechs.ebooksapp.Activity.PDFShow.class)
                                    .putExtra("link", magazineList.get(0).getUrl())
                                    .putExtra("toolbarTitle", magazineList.get(0).getTitle())
                                    .putExtra("type", "link"));
                        }
                    }
                }
            } else {
                Toasty.info(MagazineDetails.this, getResources().getString(R.string.internet_connection), Toasty.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("Exception-Read", "" + e.getMessage());
        }
    }

    /*========= Download START =========*/
    private void CheckMagazineType() {
        if (Functions.isConnectedToInternet(MagazineDetails.this)) {
            if (Utils.checkLoginUser(MagazineDetails.this)) {

                if (magazineList.get(0).getIsPaid().equalsIgnoreCase("1") && magazineList.get(0).getIsBuy() == 0) {

                    if (Utils.checkMissingData(MagazineDetails.this, "" + prefManager.getValue("userType"))) {
                        Intent intent = new Intent(MagazineDetails.this, AllPaymentActivity.class);
                        intent.putExtra("TYPE", "Magazine");
                        intent.putExtra("paymentType", "1");
                        intent.putExtra("price", "" + magazineList.get(0).getPrice());
                        intent.putExtra("itemId", "" + magazineList.get(0).getId());
                        intent.putExtra("title", "" + magazineList.get(0).getTitle());
                        intent.putExtra("desc", "" + magazineList.get(0).getCategoryName());
                        intent.putExtra("date", "" + magazineList.get(0).getCreatedAt());
                        intent.putExtra("author", "" + magazineList.get(0).getAuthorId());
                        intent.putExtra("walletBalance", "" + walletBalance);
                        startActivity(intent);
                    } else {
                        Utils.getMissingDataFromUser(MagazineDetails.this, "" + prefManager.getValue("userType"));
                    }

                } else {
                    if (magazineList.get(0).getUrl().contains(".epub") || magazineList.get(0).getUrl().contains(".pdf")) {
                        DownloadAndSave("" + magazineList.get(0).getUrl());
                    }
                }

            }
        } else {
            Toasty.info(MagazineDetails.this, getResources().getString(R.string.internet_connection), Toasty.LENGTH_SHORT).show();
        }
    }

    private void DownloadAndSave(String magazineURL) {
        try {
            if (magazineURL != null) {
                Log.e("=> magazineURL", "" + magazineURL);
                String saveMagazineName;
                if (magazineURL.contains(".pdf")) {
                    saveMagazineName = "" + magazineList.get(0).getTitle().replaceAll("[, ;]", "").toLowerCase() + prefManager.getLoginId() + ".pdf";
                } else {
                    saveMagazineName = "" + magazineList.get(0).getTitle().replaceAll("[, ;]", "").toLowerCase() + prefManager.getLoginId() + ".epub";
                }
                Log.e("DownloadAndSave", "saveMagazineName => " + saveMagazineName);

                String downloadDirectory;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    downloadDirectory = Functions.getAppFolder(MagazineDetails.this) + getResources().getString(R.string.magazines) + "/";
                } else {
                    downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath() + "/" + getResources().getString(R.string.magazines) + "/";
                }
                Log.e("DownloadAndSave", "downloadDirectory => " + downloadDirectory);

                File file = new File(downloadDirectory);
                if (!file.exists()) {
                    Log.e("DownloadAndSave", "Document directory created again");
                    file.mkdirs();
                }

                File checkFile;
                checkFile = new File(file, saveMagazineName);
                Log.e("DownloadAndSave", "checkFile => " + checkFile);

                if (!checkFile.exists()) {
                    Functions.showDeterminentLoader(MagazineDetails.this, false, false);
                    PRDownloader.initialize(getApplicationContext());
                    DownloadRequest prDownloader = PRDownloader.download(magazineURL, downloadDirectory, saveMagazineName)
                            .build()
                            .setOnProgressListener(new OnProgressListener() {
                                @Override
                                public void onProgress(Progress progress) {
                                    int prog = (int) ((progress.currentBytes * 100) / progress.totalBytes);
                                    Functions.showLoadingProgress(prog);
                                }
                            });

                    String finalDownloadDirectory = downloadDirectory;
                    prDownloader.start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            Functions.cancelDeterminentLoader();
                            Log.e("onDownloadComplete", "finalDownloadDirectory => " + finalDownloadDirectory);
                            Log.e("onDownloadComplete", "saveMagazineName => " + saveMagazineName);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                downloadMagazine(finalDownloadDirectory, saveMagazineName);
                            } else {
                                scanFile(finalDownloadDirectory, saveMagazineName);
                            }
                        }

                        @Override
                        public void onError(Error error) {
                            Functions.cancelDeterminentLoader();
                            Log.e("onError", "error => " + error.getServerErrorMessage());
                        }
                    });
                } else {
                    Toasty.info(MagazineDetails.this, "" + getResources().getString(R.string.already_download), Toasty.LENGTH_SHORT).show();
                }

            }
        } catch (Exception e) {
            Log.e("DownloadBook", "Exception => " + e);
            e.printStackTrace();
        }
    }

    public void downloadMagazine(String path, String magazineName) {
        Log.e("=>path", "" + path);

        ParcelFileDescriptor pfd;
        try {
            pfd = getContentResolver().openFileDescriptor(Uri.fromFile(new File(path + magazineName)), "w");
            Log.e("=>pfd", "" + pfd);

            FileOutputStream out = new FileOutputStream(pfd.getFileDescriptor());
            pdfFile = new File(path + magazineName);
            FileInputStream in = new FileInputStream(pdfFile);

            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
            pfd.close();
        } catch (Exception e) {
            Log.e("downloadBook", "Exception => " + e);
            e.printStackTrace();
            Utils.ProgressbarHide();
        }

        Log.e("=>pdfFile", "" + pdfFile);
        storedWithSecurity();
    }

    public void scanFile(String downloadDirectory, String magazineName) {
        MediaScannerConnection.scanFile(MagazineDetails.this, new String[]{downloadDirectory + magazineName}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("onScanCompleted", "path => " + path);
                        Log.e("onScanCompleted", "bookName => " + magazineName);
                        Log.e("onScanCompleted", "uri => " + uri.toString());
                        pdfFile = new File(path);
                        storedWithSecurity();
                    }
                });
    }

    private void storedWithSecurity() {
        if (pdfFile != null) {
            DownloadedItemModel downloadedItemModel = new DownloadedItemModel("" + prefManager.getLoginId(), "" + magazineList.get(0).getId(),
                    "" + magazineList.get(0).getAuthorId(), "" + magazineList.get(0).getTitle(), "" + magazineList.get(0).getDescription(),
                    "" + magazineList.get(0).getIsPaid(), "" + magazineList.get(0).getSampleUrl(), "" + pdfFile.getPath(), "" + magazineList.get(0).getUrl(),
                    "" + magazineList.get(0).getPrice(), "" + magazineList.get(0).getCategoryId(), "" + magazineList.get(0).getImage(),
                    "" + magazineList.get(0).getReadcnt(), "" + magazineList.get(0).getDownload(), "" + magazineList.get(0).getIsFeature(),
                    "" + magazineList.get(0).getStatus(), "" + magazineList.get(0).getCreatedAt(), "" + magazineList.get(0).getUpdatedAt(),
                    "" + magazineList.get(0).getCategoryName(), "" + magazineList.get(0).getCategoryImage(), "" + magazineList.get(0).getAuthorName(),
                    "" + magazineList.get(0).getAuthorImage(), 1, "" + magazineList.get(0).getAvgRating(),
                    "" + magazineList.get(0).getTransactionDate(),
                    "Magazines", 0, "my_magazines" + prefManager.getLoginId());

            List<DownloadedItemModel> myMagazines = Hawk.get("my_magazines" + prefManager.getLoginId());
            if (myMagazines == null) {
                myMagazines = new ArrayList<>();
            }
            for (int i = 0; i < myMagazines.size(); i++) {
                if (myMagazines.get(i).getId().equals("" + magazineList.get(0).getId())) {
                    myMagazines.remove(myMagazines.get(i));
                    Hawk.put("my_magazines" + prefManager.getLoginId(), myMagazines);
                }
            }
            myMagazines.add(downloadedItemModel);
            Hawk.put("my_magazines" + prefManager.getLoginId(), myMagazines);

            Log.e("myMagazines", "password => " + myMagazines.get(0).getFilePassword());
            Log.e("myMagazines", "magazineName => " + myMagazines.get(0).getTitle());
            AddDownload();
        } else {
            Toasty.warning(MagazineDetails.this, "" + getResources().getString(R.string.something_went_wrong), Toasty.LENGTH_SHORT).show();
            Utils.ProgressbarHide();
        }
    }

    private void AddDownload() {
        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<SuccessModel> call = bookNPlayAPI.add_magazine_download("" + prefManager.getLoginId(), "" + ID);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                try {
                    Log.e("add_magazine_download", "" + response.body().getMessage());
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Toasty.success(MagazineDetails.this, "" + response.body().getMessage(), Toasty.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("add_magazine_download", "Exception => " + e);
                }
                Utils.ProgressbarHide();
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                Log.e("add_magazine_download", "Exception => " + t.getMessage());
                Utils.ProgressbarHide();
                Utils.shimmerHide(shimmer);
            }
        });
    }
    /*========= Download END =========*/

    private void giftDialog() {
        inflater = (LayoutInflater) MagazineDetails.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.gift_dialog, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setAnimationStyle(R.style.SheetDialog);
        popupWindow.setElevation(100);
        popupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0, 0);

        final TextView txt_user_count = popupView.findViewById(R.id.txt_user_count);
        final LinearLayout ly_close_dialog = popupView.findViewById(R.id.ly_close_dialog);
        final LinearLayout ly_get_coin = popupView.findViewById(R.id.ly_get_coin);
        final RecyclerView rv_gift = popupView.findViewById(R.id.rv_gift);

        giftAdapter = new GiftAdapter(MagazineDetails.this);
        rv_gift.setLayoutManager(new GridLayoutManager(MagazineDetails.this, 4,
                LinearLayoutManager.VERTICAL, false));
        rv_gift.setAdapter(giftAdapter);
        giftAdapter.notifyDataSetChanged();
        rv_gift.setVisibility(View.VISIBLE);

        ly_close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        ly_get_coin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void commentDialog() {
        inflater = (LayoutInflater) MagazineDetails.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.comment_dialog, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setAnimationStyle(R.style.SheetDialog);
        popupWindow.setElevation(100);
        popupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0, 0);

        final SimpleRatingBar simple_rating_bar = popupView.findViewById(R.id.simple_rating_bar);
        final LinearLayout ly_close_dialog = popupView.findViewById(R.id.ly_close_dialog);
        final LinearLayout ly_submit = popupView.findViewById(R.id.ly_submit);
        final EditText edt_comment = popupView.findViewById(R.id.edt_comment);

        ly_close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        ly_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edt_comment.getText().toString()) && simple_rating_bar.getRating() == 0.0) {
                    Toasty.warning(MagazineDetails.this, "" + getResources().getString(R.string.please_give_feedback_to_author), Toasty.LENGTH_SHORT).show();
                    return;
                }

                popupWindow.dismiss();
                if (simple_rating_bar.getRating() != 0.0) {
                    Log.e("rating ==>", "" + simple_rating_bar.getRating());
                    Addrating(simple_rating_bar.getRating());
                }
                if (!TextUtils.isEmpty(edt_comment.getText().toString())) {
                    AddComments(edt_comment.getText().toString());
                }
            }
        });
    }

    private void CheckMagazineImage() {
        Log.e("CheckBookImage", "image => " + magazineList.get(0).getImage());
//        if (Utils.checkImage(BookList.get(0).getImage()) == true && !TextUtils.isEmpty(BookList.get(0).getImage())) {
//            //new DownloadImage().execute(BookList.get(0).getImage());
//            DownloadAndSaveImage("" + BookList.get(0).getImage());
//        } else {
        imageFile = null;
        ShareMagazine();
//        }
    }

    private void ShareMagazine() {
        String shareMessage = "\n\n" + getResources().getString(R.string.let_me_recommend_you_this_application_to_read_full_book)
                + "\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "" + getResources().getString(R.string.information_of_book));
        shareIntent.putExtra(Intent.EXTRA_TEXT, "\n" + getResources().getString(R.string.magazine) + " " + magazineList.get(0).getTitle()
                + "\n" + getResources().getString(R.string.magazine_author) + " " + magazineList.get(0).getAuthorName()
                /*+ "\n\n" + magazineList.get(0).getDescription()*/ + shareMessage);

//        if (imageFile != null) {
//            Log.e("ShareBook", "imageFile Uri => " + Uri.parse(imageFile.getPath()));
//            shareIntent.setType("image/*");
//            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageFile.getPath()));
//            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        }

        try {
            startActivity(Intent.createChooser(shareIntent, "" + getResources().getString(R.string.share_with)));
        } catch (android.content.ActivityNotFoundException ex) {
            Log.e("ShareBook", "Exception => " + ex.getMessage());
        }
    }

    private void DownloadAndSaveImage(String bookImgURL) {
        try {
            if (bookImgURL != null) {
                Log.e("=> bookImgURL", "" + bookImgURL);
                String saveBookImageName = "" + magazineList.get(0).getId() + ".jpeg";
                Log.e("DownloadAndSaveImage", "saveBookImageName => " + saveBookImageName);

                String downloadDirectory;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    downloadDirectory = Functions.getAppFolder(MagazineDetails.this) + getResources().getString(R.string.books) + "/Images/";
                } else {
                    downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/" + getResources().getString(R.string.books) + "/Images/";
                }
                Log.e("DownloadAndSaveImage", "downloadDirectory => " + downloadDirectory);

                File file = new File(downloadDirectory);
                if (!file.exists()) {
                    Log.e("DownloadAndSaveImage", "Image directory created again");
                    file.mkdirs();
                }

                File checkFile;
                checkFile = new File(file, saveBookImageName);
                Log.e("DownloadAndSaveImage", "checkFile => " + checkFile);

                if (!checkFile.exists()) {
                    Functions.showDeterminentLoader(MagazineDetails.this, false, false);
                    PRDownloader.initialize(getApplicationContext());
                    DownloadRequest prDownloader = PRDownloader.download(bookImgURL, downloadDirectory, saveBookImageName)
                            .build()
                            .setOnProgressListener(new OnProgressListener() {
                                @Override
                                public void onProgress(Progress progress) {
                                    int prog = (int) ((progress.currentBytes * 100) / progress.totalBytes);
                                    Functions.showLoadingProgress(prog);
                                }
                            });

                    String finalDownloadDirectory = downloadDirectory;
                    prDownloader.start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            Functions.cancelDeterminentLoader();
                            Log.e("onDownloadComplete", "finalDownloadDirectory => " + finalDownloadDirectory);
                            Log.e("onDownloadComplete", "saveBookImageName => " + saveBookImageName);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                downloadBookImage(finalDownloadDirectory, saveBookImageName);
                            } else {
                                scanFileImage(finalDownloadDirectory, saveBookImageName);
                            }
                        }

                        @Override
                        public void onError(Error error) {
                            Functions.cancelDeterminentLoader();
                            Log.e("onError", "error => " + error.getServerErrorMessage());
                        }
                    });
                } else {
                    imageFile = checkFile;
                    Log.e("isExists", "imageFile => " + imageFile);
                    ShareMagazine();
                }

            }
        } catch (Exception e) {
            Log.e("DownloadAndSaveImage", "Exception => " + e);
            e.printStackTrace();
        }
    }

    public void downloadBookImage(String path, String bookImageName) {
        Log.e("=>path", "" + path);
        ContentValues valuesimage;
        valuesimage = new ContentValues();
        valuesimage.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + Functions.getAppFolder(MagazineDetails.this) + getResources().getString(R.string.books) + "/Images/");
        valuesimage.put(MediaStore.MediaColumns.TITLE, bookImageName);
        valuesimage.put(MediaStore.MediaColumns.DISPLAY_NAME, bookImageName);
        valuesimage.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        valuesimage.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
        valuesimage.put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis());
        valuesimage.put(MediaStore.MediaColumns.IS_PENDING, 1);
        ContentResolver resolver = getContentResolver();
        Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri uriSavedImage = resolver.insert(collection, valuesimage);

        ParcelFileDescriptor pfd;
        try {
            pfd = getContentResolver().openFileDescriptor(uriSavedImage, "w");
            Log.e("=>pfd", "" + pfd);

            FileOutputStream out = new FileOutputStream(pfd.getFileDescriptor());
            imageFile = new File(path + bookImageName);
            FileInputStream in = new FileInputStream(imageFile);

            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
            pfd.close();
        } catch (Exception e) {
            Log.e("downloadBook", "Exception => " + e);
            e.printStackTrace();
            Utils.ProgressbarHide();
        }

        valuesimage.clear();
        valuesimage.put(MediaStore.MediaColumns.IS_PENDING, 0);
        getContentResolver().update(uriSavedImage, valuesimage, null, null);

        Log.e("=>imageFile", "" + imageFile);
        ShareMagazine();
    }

    public void scanFileImage(String downloadDirectory, String bookImageName) {
        MediaScannerConnection.scanFile(MagazineDetails.this, new String[]{downloadDirectory + bookImageName}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("onScanCompleted", "path => " + path);
                        Log.e("onScanCompleted", "bookImageName => " + bookImageName);
                        Log.e("onScanCompleted", "uri => " + uri.toString());
                        imageFile = new File(path);
                        Log.e("onScanCompleted", "imageFile => " + imageFile);
                        ShareMagazine();
                    }
                });
    }

    /*========= Pagination START =========*/
    private void setupPagination() {
        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;

        magazineRelatedAdapter = new MagazineRelatedAdapter(MagazineDetails.this, relatedList);
        rv_related.setLayoutManager(new GridLayoutManager(MagazineDetails.this, 3));
        rv_related.setAdapter(magazineRelatedAdapter);
        magazineRelatedAdapter.notifyDataSetChanged();

        Utils.Pagination(rv_related, this);
    }

    @Override
    public void onLoadMore() {
        Log.e("Paginate", "onLoadMore");
        loading = true;
        page++;
        RelatedItem(page);
    }

    @Override
    public boolean isLoading() {
        Log.e("isLoading", "" + loading);
        return loading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        Log.e("page => ", "" + page);
        Log.e("totalPages => ", "" + totalPages);
        if (totalPages < page) {
            return false;
        } else {
            return page == totalPages;
        }
    }
    /*========= Pagination END =========*/

    //Showing ad by TYPE
    private void ShowAdByClick(String Type) {
        TYPE = Type;
        Log.e("=>TYPE", "" + TYPE);

        if (prefManager.getValue("reward_ad").equalsIgnoreCase("yes")) {
            if (mRewardedAd != null) {
                mRewardedAd.show(MagazineDetails.this, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        Log.e("RewardItem amount =>", "" + rewardItem.getAmount());
                    }
                });
            } else {
                Log.e("mRewardedAd=>", "The ad wasn't ready yet.");
                if (TYPE.equalsIgnoreCase("Download")) {
                    CheckMagazineType();
                } else if (TYPE.equalsIgnoreCase("Read")) {
                    ReadMagazine();
                } else if (TYPE.equalsIgnoreCase("Share")) {
                    CheckMagazineImage();
                }
            }

        } else if (prefManager.getValue("fb_rewardvideo_status").equalsIgnoreCase("on")) {
            if (fbRewardedVideoAd != null && fbRewardedVideoAd.isAdLoaded()) {
                fbRewardedVideoAd.show();
            } else {
                Log.e("fbRewardedVideoAd=>", "The ad wasn't ready yet.");
                if (TYPE.equalsIgnoreCase("Download")) {
                    CheckMagazineType();
                } else if (TYPE.equalsIgnoreCase("Read")) {
                    ReadMagazine();
                } else if (TYPE.equalsIgnoreCase("Share")) {
                    CheckMagazineImage();
                }
            }

        } else if (prefManager.getValue("fb_interstiatial_status").equalsIgnoreCase("on")) {
            if (fbInterstitialAd != null && fbInterstitialAd.isAdLoaded()) {
                fbInterstitialAd.show();
            } else {
                Log.e("fbInterstitialAd=>", "The ad wasn't ready yet.");
                if (TYPE.equalsIgnoreCase("Download")) {
                    CheckMagazineType();
                } else if (TYPE.equalsIgnoreCase("Read")) {
                    ReadMagazine();
                } else if (TYPE.equalsIgnoreCase("Share")) {
                    CheckMagazineImage();
                }
            }

        } else {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(MagazineDetails.this);
            } else {
                Log.e("mInterstitialAd=>", "The ad wasn't ready yet.");
                if (TYPE.equalsIgnoreCase("Download")) {
                    CheckMagazineType();
                } else if (TYPE.equalsIgnoreCase("Read")) {
                    ReadMagazine();
                } else if (TYPE.equalsIgnoreCase("Share")) {
                    CheckMagazineImage();
                }
            }
        }
    }

    //Admob & Facebook Ads START
    private void InterstitialAd() {
        try {
            AdRequest adRequest = new AdRequest.Builder().build();

            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    Log.e("Ad failed to show.", "" + adError.toString());
                    mInterstitialAd = null;
                    if (TYPE.equalsIgnoreCase("Download")) {
                        CheckMagazineType();
                    } else if (TYPE.equalsIgnoreCase("Read")) {
                        ReadMagazine();
                    } else if (TYPE.equalsIgnoreCase("Share")) {
                        CheckMagazineImage();
                    }
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
                    if (TYPE.equalsIgnoreCase("Download")) {
                        CheckMagazineType();
                    } else if (TYPE.equalsIgnoreCase("Read")) {
                        ReadMagazine();
                    } else if (TYPE.equalsIgnoreCase("Share")) {
                        CheckMagazineImage();
                    }
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
                            if (TYPE.equalsIgnoreCase("Download")) {
                                CheckMagazineType();
                            } else if (TYPE.equalsIgnoreCase("Read")) {
                                ReadMagazine();
                            } else if (TYPE.equalsIgnoreCase("Share")) {
                                CheckMagazineImage();
                            }
                            Log.e("TAG", "fb ad dismissed.");
                        }

                        @Override
                        public void onError(Ad ad, com.facebook.ads.AdError adError) {
                            Log.e("TAG", "fb ad failed to load : " + adError.getErrorMessage());
                            fbInterstitialAd = null;
                            if (TYPE.equalsIgnoreCase("Download")) {
                                CheckMagazineType();
                            } else if (TYPE.equalsIgnoreCase("Read")) {
                                ReadMagazine();
                            } else if (TYPE.equalsIgnoreCase("Share")) {
                                CheckMagazineImage();
                            }
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

    private void RewardedVideoAd() {
        try {
            AdRequest adRequest = new AdRequest.Builder().build();

            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            Log.e("Ad failed to show.", "" + adError.toString());
                            mRewardedAd = null;
                            if (TYPE.equalsIgnoreCase("Download")) {
                                CheckMagazineType();
                            } else if (TYPE.equalsIgnoreCase("Read")) {
                                ReadMagazine();
                            } else if (TYPE.equalsIgnoreCase("Share")) {
                                CheckMagazineImage();
                            }
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();
                            Log.e("TAG", "Ad was shown.");
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            mRewardedAd = null;
                            Log.e("TAG", "Ad was dismissed.");
                            if (TYPE.equalsIgnoreCase("Download")) {
                                CheckMagazineType();
                            } else if (TYPE.equalsIgnoreCase("Read")) {
                                ReadMagazine();
                            } else if (TYPE.equalsIgnoreCase("Share")) {
                                CheckMagazineImage();
                            }
                        }

                        @Override
                        public void onAdImpression() {
                            super.onAdImpression();
                            Log.e("TAG", "onAdImpression.");
                        }
                    };

            mRewardedAd.load(MagazineDetails.this, "" + prefManager.getValue("reward_adid"),
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            super.onAdLoaded(rewardedAd);
                            mRewardedAd = rewardedAd;
                            mRewardedAd.setFullScreenContentCallback(fullScreenContentCallback);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            mRewardedAd = null;
                        }
                    });

        } catch (Exception e) {
            Log.e("RewardAd Exception =>", "" + e);
        }
    }

    private void FacebookRewardAd() {
        try {
            fbRewardedVideoAd = new RewardedVideoAd(MagazineDetails.this,
                    "VID_HD_16_9_15S_APP_INSTALL#" + prefManager.getValue("fb_rewardvideo_id"));

            fbRewardedVideoAd.loadAd(fbRewardedVideoAd.buildLoadAdConfig().withAdListener(new RewardedVideoAdListener() {
                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    Log.e("TAG", "Rewarded video adError => " + adError.getErrorMessage());
                    fbRewardedVideoAd.destroy();
                    fbRewardedVideoAd = null;
                    if (TYPE.equalsIgnoreCase("Download")) {
                        CheckMagazineType();
                    } else if (TYPE.equalsIgnoreCase("Read")) {
                        ReadMagazine();
                    } else if (TYPE.equalsIgnoreCase("Share")) {
                        CheckMagazineImage();
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
                    if (TYPE.equalsIgnoreCase("Download")) {
                        CheckMagazineType();
                    } else if (TYPE.equalsIgnoreCase("Read")) {
                        ReadMagazine();
                    } else if (TYPE.equalsIgnoreCase("Share")) {
                        CheckMagazineImage();
                    }
                }
            }).build());

        } catch (Exception e) {
            Log.e("AdView Exception=>", "" + e.getMessage());
        }
    }
    //Admob & Facebook Ads END

    @Override
    public void onPause() {
        super.onPause();
        if (paginate != null) {
            paginate.unbind();
        }
        Utils.ProgressbarHide();
        Utils.shimmerHide(shimmer);
        if (mInterstitialAd != null) {
            mInterstitialAd = null;
        }
        if (mRewardedAd != null) {
            mRewardedAd = null;
        }
        if (fbInterstitialAd != null) {
            fbInterstitialAd.destroy();
            fbInterstitialAd = null;
        }
        if (fbNativeBannerAd != null) {
            fbNativeBannerAd.destroy();
        }
        if (fbRewardedVideoAd != null) {
            fbRewardedVideoAd.destroy();
            fbRewardedVideoAd = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (paginate != null) {
            paginate.unbind();
        }
        Utils.ProgressbarHide();
        Utils.shimmerHide(shimmer);
        if (mInterstitialAd != null) {
            mInterstitialAd = null;
        }
        if (mRewardedAd != null) {
            mRewardedAd = null;
        }
        if (fbInterstitialAd != null) {
            fbInterstitialAd.destroy();
            fbInterstitialAd = null;
        }
        if (fbNativeBannerAd != null) {
            fbNativeBannerAd.destroy();
        }
        if (fbRewardedVideoAd != null) {
            fbRewardedVideoAd.destroy();
            fbRewardedVideoAd = null;
        }
    }

}
