package com.divinetechs.ebooksapp.Activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Adapter.CommentAdapter;
import com.divinetechs.ebooksapp.Adapter.GiftAdapter;
import com.divinetechs.ebooksapp.Adapter.RelatedAdapter;
import com.divinetechs.ebooksapp.BuildConfig;
import com.divinetechs.ebooksapp.Model.BookModel.BookChapter;
import com.divinetechs.ebooksapp.Model.BookModel.BookModel;
import com.divinetechs.ebooksapp.Model.BookModel.Result;
import com.divinetechs.ebooksapp.Model.CommentModel.CommentModel;
import com.divinetechs.ebooksapp.Model.DownloadedItemModel;
import com.divinetechs.ebooksapp.Model.ProfileModel.ProfileModel;
import com.divinetechs.ebooksapp.Model.SuccessModel.SuccessModel;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.DownloadEpub;
import com.divinetechs.ebooksapp.Utility.Functions;
import com.divinetechs.ebooksapp.Utility.PermissionUtils;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetails extends AppCompatActivity implements View.OnClickListener, Paginate.Callbacks {

    PrefManager prefManager;
    ShimmerFrameLayout shimmer;
    LayoutInflater inflater;
    PermissionUtils takePermissionUtils;

    RecyclerView rv_related, rv_comment;
    TextView txt_title, txt_price, txt_by_author, txt_category, txt_descripation, txt_read, txtBuyNow, txt_bookmark, txt_drop_down, txt_drop_up, txt_show_all,
            txt_latest_update, txt_chapter_count, txt_sendGift_count, txt_like_count, txt_like_icon, txt_show_info, txt_hide_info, txt_publish_at, txt_author_name;
    ImageView iv_thumb;
    LinearLayout ly_showAll_desc, ly_back, ly_share, ly_book_content, ly_send_gift, ly_like, ly_comment_viewAll, ly_add_comment, ly_copyright_info,
            ly_author_publish, lyDownload;
    RelativeLayout rl_buy_read;

    RelatedAdapter relatedAdapter;
    CommentAdapter commentAdapter;
    GiftAdapter giftAdapter;

    public static List<Result> BookList;
    public static List<BookChapter> BookChapterList;
    List<Result> RelatedList;
    List<com.divinetechs.ebooksapp.Model.CommentModel.Result> CommentList;
    SimpleRatingBar ratingbar;

    private TemplateView nativeTemplate = null;
    private NativeBannerAd fbNativeBannerAd = null;
    private NativeAdLayout fbNativeTemplate = null;
    private RewardedVideoAd fbRewardedVideoAd = null;
    private RewardedAd mRewardedAd = null;
    private InterstitialAd mInterstitialAd = null;
    private com.facebook.ads.InterstitialAd fbInterstitialAd = null;

    String walletBalance = "", fileImage = "", TYPE = "", ID, fcat_id, getPermissionFor = "";
    private boolean loading = false;
    private int page = 1, totalPages = 1;
    private Paginate paginate;
    File pdfFile = null, imageFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.screenCapOff(BookDetails.this);
        if (PrefManager.getInstance(this).isNightModeEnabled() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_bookdetails);
        PrefManager.forceRTLIfSupported(getWindow(), BookDetails.this);

        takePermissionUtils = new PermissionUtils(BookDetails.this, mPermissionResult);
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
            Utils.NativeAds(BookDetails.this, nativeTemplate, "" + prefManager.getValue("native_adid"));
        } else {
            nativeTemplate.setVisibility(View.GONE);
        }

        Log.e("fb_native_status", "" + prefManager.getValue("fb_native_status"));
        if (prefManager.getValue("fb_native_status").equalsIgnoreCase("on")) {
            fbNativeTemplate.setVisibility(View.VISIBLE);
            Utils.FacebookNativeAdSmall(BookDetails.this, fbNativeBannerAd, fbNativeTemplate, "" + prefManager.getValue("fb_native_id"));
        } else {
            fbNativeTemplate.setVisibility(View.GONE);
        }

    }

    private void init() {
        try {
            prefManager = new PrefManager(BookDetails.this);

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

            txt_chapter_count = findViewById(R.id.txt_chapter_count);
            txt_latest_update = findViewById(R.id.txt_latest_update);
            ly_book_content = findViewById(R.id.ly_book_content);

            rl_buy_read = findViewById(R.id.rl_buy_read);
            ly_like = findViewById(R.id.ly_like);
            ly_send_gift = findViewById(R.id.ly_send_gift);
            lyDownload = findViewById(R.id.lyDownload);
            ly_add_comment = findViewById(R.id.ly_add_comment);
            ly_comment_viewAll = findViewById(R.id.ly_comment_viewAll);
            ly_copyright_info = findViewById(R.id.ly_copyright_info);
            ly_author_publish = findViewById(R.id.ly_author_publish);
            ly_showAll_desc = findViewById(R.id.ly_showAll_desc);
            txt_drop_down = findViewById(R.id.txt_drop_down);
            txt_drop_up = findViewById(R.id.txt_drop_up);
            txt_show_all = findViewById(R.id.txt_show_all);
            txt_sendGift_count = findViewById(R.id.txt_sendGift_count);
            txt_like_count = findViewById(R.id.txt_like_count);
            txt_like_icon = findViewById(R.id.txt_like_icon);
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
            ly_book_content.setOnClickListener(this);
            ly_comment_viewAll.setOnClickListener(this);
            lyDownload.setOnClickListener(this);
            ly_copyright_info.setOnClickListener(this);
            ly_add_comment.setOnClickListener(this);
            ly_send_gift.setOnClickListener(this);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_back:
                finish();
                break;

            case R.id.lyDownload:
            case R.id.txtBuyNow:
                if (Functions.isConnectedToInternet(BookDetails.this)) {
                    getPermissionFor = "Download";
                    if (Utils.checkLoginUser(BookDetails.this)) {
                        if (takePermissionUtils.isStoragePermissionGranted()) {
                            ShowAdByClick("Download");
                        } else {
                            takePermissionUtils.showStoragePermissionDailog(getString(R.string.we_need_storage_permission_for_save_video));
                        }
                    }
                } else {
                    Toasty.warning(BookDetails.this, getResources().getString(R.string.internet_connection), Toasty.LENGTH_SHORT).show();
                }
                break;

            case R.id.ly_share:
                if (Functions.isConnectedToInternet(BookDetails.this)) {
                    getPermissionFor = "Share";
                    if (takePermissionUtils.isStoragePermissionGranted()) {
                        ShowAdByClick("Share");
                    } else {
                        takePermissionUtils.showStoragePermissionDailog(getString(R.string.we_need_storage_permission_for_save_video));
                    }
                } else {
                    Toasty.warning(BookDetails.this, getResources().getString(R.string.internet_connection), Toasty.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_bookmark:
                if (Functions.isConnectedToInternet(BookDetails.this)) {
                    if (Utils.checkLoginUser(BookDetails.this)) {
                        AddBookMark();
                    }
                } else {
                    Toasty.warning(BookDetails.this, getResources().getString(R.string.internet_connection), Toasty.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_by_author:
                Intent intent = new Intent(BookDetails.this, com.divinetechs.ebooksapp.Activity.AuthorBookList.class);
                intent.putExtra("a_id", BookList.get(0).getId());
                intent.putExtra("a_name", BookList.get(0).getTitle());
                intent.putExtra("a_bio", BookList.get(0).getAuthorName());
                intent.putExtra("a_image", BookList.get(0).getImage());
                startActivity(intent);
                break;

            case R.id.txt_read:
                if (Functions.isConnectedToInternet(BookDetails.this)) {
                    try {
                        ShowAdByClick("Read");
                    } catch (Exception e) {
                        Log.e("Read book", "Exception" + e.getMessage());
                    }
                } else {
                    Toasty.warning(BookDetails.this, getResources().getString(R.string.internet_connection), Toasty.LENGTH_SHORT).show();
                }
                break;

            case R.id.ly_showAll_desc:
                if (txt_show_all.getText().toString().equalsIgnoreCase(getResources().getString(R.string.show_all))) {
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
                Intent cIntent = new Intent(BookDetails.this, com.divinetechs.ebooksapp.Activity.CommentViewAll.class);
                cIntent.putExtra("ID", ID);
                cIntent.putExtra("TYPE", "Book");
                startActivity(cIntent);
                break;

            case R.id.ly_book_content:
                if (BookList.get(0).getBookChapter().size() > 0) {
                    Intent contentIntent = new Intent(BookDetails.this, BookChapters.class);
                    contentIntent.putExtra("Title", BookList.get(0).getTitle());
                    startActivity(contentIntent);
                }
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
                if (Utils.checkLoginUser(BookDetails.this)) {
                    commentDialog();
                }
                break;

            case R.id.ly_send_gift:
                if (Utils.checkLoginUser(BookDetails.this)) {
                    giftDialog();
                }
                break;
        }
    }

    private ActivityResultLauncher<String[]> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear = true;
                    List<String> blockPermissionCheck = new ArrayList<>();
                    for (String key : result.keySet()) {
                        if (!(result.get(key))) {
                            allPermissionClear = false;
                            blockPermissionCheck.add(Functions.getPermissionStatus(BookDetails.this, key));
                        }
                    }
                    Log.e("blockPermissionCheck", "" + blockPermissionCheck);
                    Log.e("allPermissionClear", "" + allPermissionClear);
                    if (blockPermissionCheck.contains("blocked")) {
                        Functions.showPermissionSetting(BookDetails.this, getString(R.string.we_need_storage_permission_for_save_video));
                    } else if (allPermissionClear) {
                        Log.e("mPermissionResult", "isPermissionGranted => " + takePermissionUtils.isStoragePermissionGranted());
                        if (getPermissionFor.equalsIgnoreCase("Download")) {
                            ShowAdByClick("Download");
                        } else if (getPermissionFor.equalsIgnoreCase("Share")) {
                            ShowAdByClick("Share");
                        }
                    }
                }
            });

    @Override
    protected void onStart() {
        super.onStart();
        GetBookDetails();
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
            }

            @Override
            public void onFailure(Call<ProfileModel> call, Throwable t) {
                Log.e("profile", "onFailure => " + t.getMessage());
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

    private void GetBookDetails() {
        Utils.shimmerShow(shimmer);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<BookModel> call = bookNPlayAPI.bookdetails(ID, prefManager.getLoginId());
        call.enqueue(new Callback<BookModel>() {
            @Override
            public void onResponse(Call<BookModel> call, Response<BookModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        Log.e("bookdetails", "" + response.body());
                        if (response.body().getResult().size() > 0) {

                            BookList = new ArrayList<Result>();
                            BookList = response.body().getResult();
                            fcat_id = response.body().getResult().get(0).getCategoryId();

                            BookChapterList = new ArrayList<BookChapter>();
                            BookChapterList = BookList.get(0).getBookChapter();
                            Log.e("BookChapterList size", "" + BookChapterList.size());

                            Log.e("fcat_id", "" + fcat_id);
                            txt_title.setText("" + BookList.get(0).getTitle());

                            if (BookList.get(0).getIsPaid().equalsIgnoreCase("1")) {
                                txt_price.setText(" |  " + prefManager.getValue("currency_symbol") + " " + BookList.get(0).getPrice());
                            } else {
                                txt_price.setText(" |  " + getResources().getString(R.string.free));
                            }

                            txt_by_author.setText(getResources().getString(R.string.by) + " " + BookList.get(0).getAuthorName());
                            txt_category.setText("" + BookList.get(0).getCategoryName());
                            txt_descripation.setText(Html.fromHtml(BookList.get(0).getDescription()));
                            txt_author_name.setText("" + BookList.get(0).getAuthorName());
                            txt_publish_at.setText(Utils.DateFormat2(BookList.get(0).getUpdatedAt()));

                            txt_chapter_count.setText(BookList.get(0).getChapterCount() + " " + getResources().getString(R.string.chapters));
                            txt_latest_update.setText(" " + getResources().getString(R.string.chapter) + " " + BookList.get(0).getChapterCount());
                            ratingbar.setRating(Float.parseFloat(BookList.get(0).getAvgRating()));

                            Log.e("Avg_Ratings =>", "" + BookList.get(0).getAvgRating());
                            Log.e("Is_buy =>", "" + BookList.get(0).getIsBuy());
                            Log.e("IsPaid =>", "" + BookList.get(0).getIsPaid());
                            if (BookList.get(0).getIsBuy() == 0) {
                                if (BookList.get(0).getIsPaid().equalsIgnoreCase("1")) {
                                    txtBuyNow.setVisibility(View.VISIBLE);
                                    txt_read.setVisibility(View.GONE);
                                    lyDownload.setVisibility(View.INVISIBLE);
                                    txtBuyNow.setText("" + getResources().getString(R.string.buy_now));
                                } else {
                                    txt_read.setVisibility(View.VISIBLE);
                                    txtBuyNow.setVisibility(View.GONE);
                                    lyDownload.setVisibility(View.VISIBLE);
                                    txt_read.setText("" + getResources().getString(R.string.read_now));
                                }
                            } else {
                                txt_read.setVisibility(View.VISIBLE);
                                txtBuyNow.setVisibility(View.GONE);
                                lyDownload.setVisibility(View.VISIBLE);
                                txt_read.setText("" + getResources().getString(R.string.read_now));
                            }

                            Picasso.get().load(BookList.get(0).getImage()).into(iv_thumb);

                            RelatedList = new ArrayList<>();
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
                    Log.e("bookdetails", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<BookModel> call, Throwable t) {
                Log.e("bookdetails", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
            }
        });
    }

    private void RelatedItem(int pageNo) {
        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<BookModel> call = bookNPlayAPI.related_item("" + fcat_id, "" + pageNo);
        call.enqueue(new Callback<BookModel>() {
            @Override
            public void onResponse(Call<BookModel> call, Response<BookModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        totalPages = response.body().getTotalPage();
                        Log.e("totalPages", "" + totalPages);

                        if (response.body().getResult().size() > 0) {
                            RelatedList = response.body().getResult();
                            Log.e("Related_Item", "" + RelatedList.size());

                            rv_related.setVisibility(View.VISIBLE);
                            loading = false;
                            relatedAdapter.addBook(RelatedList);
                        } else {
                            rv_related.setVisibility(View.GONE);
                            loading = false;
                        }

                    } else {
                        rv_related.setVisibility(View.GONE);
                        loading = false;
                    }
                } catch (Exception e) {
                    Log.e("related_item", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<BookModel> call, Throwable t) {
                Log.e("related_item", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
                if (!loading) {
                    rv_related.setVisibility(View.GONE);
                }
                loading = false;
            }
        });
    }

    private void AddBookMark() {
        Utils.ProgressBarShow(BookDetails.this);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<SuccessModel> call = bookNPlayAPI.add_bookmark(prefManager.getLoginId(), ID);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                try {
                    if (response.code() == 200) {
                        Log.e("AddBookmark", "" + response.body().getMessage());
                        Toasty.success(BookDetails.this, "" + response.body().getMessage(), Toasty.LENGTH_SHORT).show();
                        if (response.body().getStatus() == 200) {
                            txt_bookmark.setText("" + getResources().getString(R.string.remove_from_library));
                        } else {
                            txt_bookmark.setText("" + getResources().getString(R.string.add_to_library));
                        }
                    }
                } catch (Exception e) {
                    Log.e("add_bookmark", "Exception => " + e);
                }
                Utils.ProgressbarHide();
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                Log.e("add_bookmark", "onFailure => " + t.getMessage());
                Utils.ProgressbarHide();
            }
        });
    }

    private void CheckBookMark() {
        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<SuccessModel> call = bookNPlayAPI.checkbookmark(prefManager.getLoginId(), ID);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                try {
                    if (response.code() == 200) {
                        Log.e("checkbookmark msg =>", "" + response.body().getMessage());
                        if (response.body().getStatus() == 200) {
                            txt_bookmark.setText("" + getResources().getString(R.string.remove_from_library));
                        } else {
                            txt_bookmark.setText("" + getResources().getString(R.string.add_to_library));
                        }
                    }
                } catch (Exception e) {
                    Log.e("checkbookmark", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
                Utils.ProgressbarHide();
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                Log.e("checkbookmark", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
                Utils.ProgressbarHide();
            }
        });
    }

    /*========= Download START =========*/
    private void CheckBookType() {
        if (Functions.isConnectedToInternet(BookDetails.this)) {
            if (Utils.checkLoginUser(BookDetails.this)) {
                if (BookList.get(0).getIsPaid().equalsIgnoreCase("1") && BookList.get(0).getIsBuy() == 0) {

                    if (Utils.checkMissingData(BookDetails.this, "" + prefManager.getValue("userType"))) {
                        Intent intent = new Intent(BookDetails.this, AllPaymentActivity.class);
                        intent.putExtra("TYPE", "Book");
                        intent.putExtra("paymentType", "1");
                        intent.putExtra("price", "" + BookList.get(0).getPrice());
                        intent.putExtra("itemId", "" + BookList.get(0).getId());
                        intent.putExtra("title", "" + BookList.get(0).getTitle());
                        intent.putExtra("desc", "" + BookList.get(0).getCategoryName());
                        intent.putExtra("date", "" + BookList.get(0).getCreatedAt());
                        intent.putExtra("author", "" + BookList.get(0).getAuthorId());
                        intent.putExtra("walletBalance", "" + walletBalance);
                        startActivity(intent);
                    } else {
                        Utils.getMissingDataFromUser(BookDetails.this, "" + prefManager.getValue("userType"));
                    }

                } else {
                    if (BookList.get(0).getUrl().contains(".epub") || BookList.get(0).getUrl().contains(".pdf")) {
                        DownloadAndSave("" + BookList.get(0).getUrl());
                    }
                }
            }
        } else {
            Toasty.error(BookDetails.this, getResources().getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void DownloadAndSave(String bookURL) {
        try {
            if (bookURL != null) {
                Log.e("=> bookURL", "" + bookURL);
                String saveBookName;
                if (bookURL.contains(".pdf")) {
                    saveBookName = "" + BookList.get(0).getTitle().replaceAll("[, ;]", "").toLowerCase() + prefManager.getLoginId() + ".pdf";
                } else {
                    saveBookName = "" + BookList.get(0).getTitle().replaceAll("[, ;]", "").toLowerCase() + prefManager.getLoginId() + ".epub";
                }
                Log.e("DownloadAndSave", "saveBookName => " + saveBookName);

                String downloadDirectory;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    downloadDirectory = Functions.getAppFolder(BookDetails.this) + getResources().getString(R.string.books) + "/";
                } else {
                    downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath() + "/" + getResources().getString(R.string.books) + "/";
                }
                Log.e("DownloadAndSave", "downloadDirectory => " + downloadDirectory);

                File file = new File(downloadDirectory);
                if (!file.exists()) {
                    Log.e("DownloadAndSave", "Document directory created again");
                    file.mkdirs();
                }

                File checkFile;
                checkFile = new File(file, saveBookName);
                Log.e("DownloadAndSave", "checkFile => " + checkFile);

                if (!checkFile.exists()) {
                    Functions.showDeterminentLoader(BookDetails.this, false, false);
                    PRDownloader.initialize(getApplicationContext());
                    DownloadRequest prDownloader = PRDownloader.download(bookURL, downloadDirectory, saveBookName)
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
                            Log.e("onDownloadComplete", "saveBookName => " + saveBookName);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                downloadBook(finalDownloadDirectory, saveBookName);
                            } else {
                                scanFile(finalDownloadDirectory, saveBookName);
                            }
                        }

                        @Override
                        public void onError(Error error) {
                            Functions.cancelDeterminentLoader();
                            Log.e("onError", "error => " + error.getServerErrorMessage());
                        }
                    });
                } else {
                    Toasty.info(BookDetails.this, "" + getResources().getString(R.string.already_download), Toasty.LENGTH_SHORT).show();
                }

            }
        } catch (Exception e) {
            Log.e("DownloadBook", "Exception => " + e);
            e.printStackTrace();
        }
    }

    public void downloadBook(String path, String bookName) {
        Log.e("=>path", "" + path);

        ParcelFileDescriptor pfd;
        try {
            pfd = getContentResolver().openFileDescriptor(Uri.fromFile(new File(path + bookName)), "w");
            Log.e("=>pfd", "" + pfd);

            FileOutputStream out = new FileOutputStream(pfd.getFileDescriptor());
            pdfFile = new File(path + bookName);
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

    public void scanFile(String downloadDirectory, String bookName) {
        MediaScannerConnection.scanFile(BookDetails.this, new String[]{downloadDirectory + bookName}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("onScanCompleted", "path => " + path);
                        Log.e("onScanCompleted", "bookName => " + bookName);
                        Log.e("onScanCompleted", "uri => " + uri.toString());
                        pdfFile = new File(path);
                        storedWithSecurity();
                    }
                });
    }

    private void storedWithSecurity() {
        if (pdfFile != null) {
            DownloadedItemModel downloadedItemModel = new DownloadedItemModel("" + prefManager.getLoginId(), "" + BookList.get(0).getId(), "" + BookList.get(0).getAuthorId(),
                    "" + BookList.get(0).getTitle(), "" + BookList.get(0).getDescription(), "" + BookList.get(0).getIsPaid(),
                    "" + BookList.get(0).getSampleUrl(), "" + pdfFile.getPath(), "" + BookList.get(0).getUrl(), "" + BookList.get(0).getPrice(),
                    "" + BookList.get(0).getCategoryId(), "" + BookList.get(0).getImage(), "" + BookList.get(0).getReadcnt(),
                    "" + BookList.get(0).getDownload(), "" + BookList.get(0).getIsFeature(), "" + BookList.get(0).getStatus(),
                    "" + BookList.get(0).getCreatedAt(), "" + BookList.get(0).getUpdatedAt(), "" + BookList.get(0).getCategoryName(),
                    "" + BookList.get(0).getCategoryImage(), "" + BookList.get(0).getAuthorName(), "" + BookList.get(0).getAuthorImage(),
                    BookList.get(0).getIsDownload(), "" + BookList.get(0).getAvgRating(), "" + BookList.get(0).getTransactionDate(),
                    "Books", 0, "my_books" + prefManager.getLoginId());

            List<DownloadedItemModel> myBooks = Hawk.get("my_books" + prefManager.getLoginId());
            if (myBooks == null) {
                myBooks = new ArrayList<>();
            }
            for (int i = 0; i < myBooks.size(); i++) {
                if (myBooks.get(i).getId().equals("" + BookList.get(0).getId())) {
                    myBooks.remove(myBooks.get(i));
                    Hawk.put("my_books" + prefManager.getLoginId(), myBooks);
                }
            }
            myBooks.add(downloadedItemModel);
            Hawk.put("my_books" + prefManager.getLoginId(), myBooks);

            Log.e("myBooks", "bookName => " + myBooks.get(0).getTitle());
            AddDownload();
        } else {
            Toasty.warning(BookDetails.this, "" + getResources().getString(R.string.something_went_wrong), Toasty.LENGTH_SHORT).show();
            Utils.ProgressbarHide();
        }
    }

    private void AddDownload() {
        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<SuccessModel> call = bookNPlayAPI.add_download("" + prefManager.getLoginId(), "" + ID);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                try {
                    Log.e("add_download", "" + response.body().getMessage());
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Toasty.success(BookDetails.this, "" + response.body().getMessage(), Toasty.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("add_download", "Exception => " + e);
                }
                Utils.ProgressbarHide();
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                Log.e("add_download", "onFailure => " + t.getMessage());
                Utils.ProgressbarHide();
            }
        });
    }
    /*========= Download END =========*/

    private void ReadBook() {
        try {
            if (Functions.isConnectedToInternet(BookDetails.this)) {
                Log.e("url_data", "" + BookList.get(0).getUrl().contains(".EPUB"));

                if (BookList.get(0).getUrl().contains(".epub") || BookList.get(0).getUrl().contains(".EPUB")) {

                    if (txt_read.getText().toString().equalsIgnoreCase("" + getResources().getString(R.string.read_now))) {
//                        if (Utils.checkLoginUser(BookDetails.this)) {
//                            DownloadEpub downloadEpub = new DownloadEpub(BookDetails.this);
//                            downloadEpub.pathEpub(BookList.get(0).getUrl(), BookList.get(0).getId(), "book");
//                        }
//                    } else {
                        if (Utils.checkLoginUser(BookDetails.this)) {
                            AddContinue(2);
                        }
                    }

                } else if (BookList.get(0).getUrl().contains(".pdf") || BookList.get(0).getUrl().contains(".PDF")) {

                    if (txt_read.getText().toString().equalsIgnoreCase("" + getResources().getString(R.string.read_now))) {
//                        if (Utils.checkLoginUser(BookDetails.this)) {
//                            startActivity(new Intent(BookDetails.this, PDFShow.class)
//                                    .putExtra("link", BookList.get(0).getUrl())
//                                    .putExtra("toolbarTitle", BookList.get(0).getTitle())
//                                    .putExtra("type", "link"));
//                        }
//                    } else {
                        if (Utils.checkLoginUser(BookDetails.this)) {
                            AddContinue(1);
                        }
                    }

                }

            } else {
                Toasty.error(BookDetails.this, getResources().getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("Exception-Read", "" + e.getMessage());
        }
    }

    private void AddContinue(int status) {
        Utils.ProgressBarShow(BookDetails.this);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<SuccessModel> call = bookNPlayAPI.add_continue_read(prefManager.getLoginId(), ID);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                Utils.ProgressbarHide();
                if (status == 1) {
                    startActivity(new Intent(BookDetails.this, com.divinetechs.ebooksapp.Activity.PDFShow.class)
                            .putExtra("link", BookList.get(0).getUrl())
                            .putExtra("toolbarTitle", BookList.get(0).getTitle())
                            .putExtra("type", "link"));
                } else {
                    DownloadEpub downloadEpub = new DownloadEpub(BookDetails.this);
                    downloadEpub.pathEpub(BookList.get(0).getUrl(), BookList.get(0).getId(), "book", false);
                }
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                Utils.ProgressbarHide();
                if (status == 1) {
                    startActivity(new Intent(BookDetails.this, com.divinetechs.ebooksapp.Activity.PDFShow.class)
                            .putExtra("link", BookList.get(0).getUrl())
                            .putExtra("toolbarTitle", BookList.get(0).getTitle())
                            .putExtra("type", "link"));
                } else {
                    DownloadEpub downloadEpub = new DownloadEpub(BookDetails.this);
                    downloadEpub.pathEpub(BookList.get(0).getUrl(), BookList.get(0).getId(), "book", false);
                }
            }
        });
    }

    private void giftDialog() {
        inflater = (LayoutInflater) BookDetails.this.getSystemService(LAYOUT_INFLATER_SERVICE);
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

        giftAdapter = new GiftAdapter(BookDetails.this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(BookDetails.this, 4,
                LinearLayoutManager.VERTICAL, false);
        rv_gift.setLayoutManager(gridLayoutManager);
        rv_gift.setItemAnimator(new DefaultItemAnimator());
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

    private void Comments() {
        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<CommentModel> call = bookNPlayAPI.view_comment(ID, "1");
        call.enqueue(new Callback<CommentModel>() {
            @Override
            public void onResponse(Call<CommentModel> call, Response<CommentModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult().size() > 0) {
                            CommentList = new ArrayList<>();
                            CommentList = response.body().getResult();
                            Log.e("CommentList", "" + CommentList.size());

                            if (CommentList.size() > 5) {
                                commentAdapter = new CommentAdapter(BookDetails.this, CommentList, "Max_5");
                            } else {
                                commentAdapter = new CommentAdapter(BookDetails.this, CommentList, "");
                            }
                            rv_comment.setLayoutManager(new LinearLayoutManager(BookDetails.this, LinearLayoutManager.VERTICAL, false));
                            rv_comment.setItemAnimator(new DefaultItemAnimator());
                            rv_comment.setAdapter(commentAdapter);
                            commentAdapter.notifyDataSetChanged();

                            rv_comment.setVisibility(View.VISIBLE);
                        } else {
                            rv_comment.setVisibility(View.GONE);
                        }

                    } else {
                        rv_comment.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e("view_comment", "Exception =>" + e);
                }
                Utils.ProgressbarHide();
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<CommentModel> call, Throwable t) {
                Log.e("view_comment", "onFailure =>" + t.getMessage());
                Utils.ProgressbarHide();
                Utils.shimmerHide(shimmer);
                rv_comment.setVisibility(View.GONE);
            }
        });
    }

    private void AddComments(String comment) {
        Utils.ProgressBarShow(BookDetails.this);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<SuccessModel> call = bookNPlayAPI.add_comment(ID, prefManager.getLoginId(), comment);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                Utils.ProgressbarHide();
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Log.e("Add Comments", "" + response.body().getMessage());
                        Toasty.success(BookDetails.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        Comments();
                    } else {
                        Log.e("Add Comments", "" + response.body().getMessage());
                    }
                } catch (Exception e) {
                    Log.e("add_comment", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                Log.e("Add Comments", "onFailure => " + t.getMessage());
                Utils.ProgressbarHide();
            }
        });
    }

    private void Addrating(Float aFloat) {
        Utils.ProgressBarShow(BookDetails.this);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<SuccessModel> call = bookNPlayAPI.give_rating(prefManager.getLoginId(), ID, "" + aFloat);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Log.e("AddRating", "" + response.body().getMessage());
                        Toasty.success(BookDetails.this, "" + response.body().getMessage(), Toasty.LENGTH_SHORT).show();
                    } else {
                        Toasty.info(BookDetails.this, "" + response.body().getMessage(), Toasty.LENGTH_SHORT).show();
                        Log.e("AddRating", "status => " + response.body().getStatus());
                    }
                } catch (Exception e) {
                    Log.e("AddRating", "Exception => " + e);
                }
                Utils.ProgressbarHide();
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                Log.e("AddRating", "onFailure => " + t.getMessage());
                Utils.ProgressbarHide();
            }
        });
    }

    private void commentDialog() {
        inflater = (LayoutInflater) BookDetails.this.getSystemService(LAYOUT_INFLATER_SERVICE);
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
                    Toasty.warning(BookDetails.this, "" + getResources().getString(R.string.please_give_feedback_to_author), Toasty.LENGTH_SHORT).show();
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

    private void CheckBookImage() {
        Log.e("CheckBookImage", "image => " + BookList.get(0).getImage());
//        if (Utils.checkImage(BookList.get(0).getImage()) == true && !TextUtils.isEmpty(BookList.get(0).getImage())) {
//            //new DownloadImage().execute(BookList.get(0).getImage());
//            DownloadAndSaveImage("" + BookList.get(0).getImage());
//        } else {
        imageFile = null;
        ShareBook();
//        }
    }

    private void ShareBook() {
        String shareMessage = "\n\n" + getResources().getString(R.string.let_me_recommend_you_this_application_to_read_full_book)
                + "\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "" + getResources().getString(R.string.information_of_book));
        shareIntent.putExtra(Intent.EXTRA_TEXT, "\n" + getResources().getString(R.string.book) + " " + BookList.get(0).getTitle() + "\n" + getResources().getString(R.string.book_author)
                + " " + BookList.get(0).getAuthorName() /*+ "\n\n" + BookList.get(0).getDescription()*/ + shareMessage);

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
                String saveBookImageName = "" + BookList.get(0).getId() + ".jpeg";
                Log.e("DownloadAndSaveImage", "saveBookImageName => " + saveBookImageName);

                String downloadDirectory;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    downloadDirectory = Functions.getAppFolder(BookDetails.this) + getResources().getString(R.string.books) + "/Images/";
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
                    Functions.showDeterminentLoader(BookDetails.this, false, false);
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
                    ShareBook();
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
        valuesimage.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + Functions.getAppFolder(BookDetails.this) + getResources().getString(R.string.books) + "/Images/");
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
        ShareBook();
    }

    public void scanFileImage(String downloadDirectory, String bookImageName) {
        MediaScannerConnection.scanFile(BookDetails.this, new String[]{downloadDirectory + bookImageName}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("onScanCompleted", "path => " + path);
                        Log.e("onScanCompleted", "bookImageName => " + bookImageName);
                        Log.e("onScanCompleted", "uri => " + uri.toString());
                        imageFile = new File(path);
                        Log.e("onScanCompleted", "imageFile => " + imageFile);
                        ShareBook();
                    }
                });
    }

    /*========= Pagination START =========*/
    private void setupPagination() {
        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;

        relatedAdapter = new RelatedAdapter(BookDetails.this, RelatedList);
        rv_related.setLayoutManager(new GridLayoutManager(BookDetails.this, 3));
        rv_related.setItemAnimator(new DefaultItemAnimator());
        rv_related.setAdapter(relatedAdapter);
        relatedAdapter.notifyDataSetChanged();

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
                mRewardedAd.show(BookDetails.this, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        Log.e("RewardItem amount =>", "" + rewardItem.getAmount());
                    }
                });
            } else {
                Log.e("mRewardedAd=>", "The ad wasn't ready yet.");
                if (TYPE.equalsIgnoreCase("Download")) {
                    CheckBookType();
                } else if (TYPE.equalsIgnoreCase("Read")) {
                    ReadBook();
                } else if (TYPE.equalsIgnoreCase("Share")) {
                    CheckBookImage();
                }
            }

        } else if (prefManager.getValue("fb_rewardvideo_status").equalsIgnoreCase("on")) {
            if (fbRewardedVideoAd != null && fbRewardedVideoAd.isAdLoaded()) {
                fbRewardedVideoAd.show();
            } else {
                Log.e("fbRewardedVideoAd=>", "The ad wasn't ready yet.");
                if (TYPE.equalsIgnoreCase("Download")) {
                    CheckBookType();
                } else if (TYPE.equalsIgnoreCase("Read")) {
                    ReadBook();
                } else if (TYPE.equalsIgnoreCase("Share")) {
                    CheckBookImage();
                }
            }

        } else if (prefManager.getValue("fb_interstiatial_status").equalsIgnoreCase("on")) {
            if (fbInterstitialAd != null && fbInterstitialAd.isAdLoaded()) {
                fbInterstitialAd.show();
            } else {
                Log.e("fbInterstitialAd=>", "The ad wasn't ready yet.");
                if (TYPE.equalsIgnoreCase("Download")) {
                    CheckBookType();
                } else if (TYPE.equalsIgnoreCase("Read")) {
                    ReadBook();
                } else if (TYPE.equalsIgnoreCase("Share")) {
                    CheckBookImage();
                }
            }

        } else {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(BookDetails.this);
            } else {
                Log.e("mInterstitialAd=>", "The ad wasn't ready yet.");
                if (TYPE.equalsIgnoreCase("Download")) {
                    CheckBookType();
                } else if (TYPE.equalsIgnoreCase("Read")) {
                    ReadBook();
                } else if (TYPE.equalsIgnoreCase("Share")) {
                    CheckBookImage();
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
                        CheckBookType();
                    } else if (TYPE.equalsIgnoreCase("Read")) {
                        ReadBook();
                    } else if (TYPE.equalsIgnoreCase("Share")) {
                        CheckBookImage();
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
                        CheckBookType();
                    } else if (TYPE.equalsIgnoreCase("Read")) {
                        ReadBook();
                    } else if (TYPE.equalsIgnoreCase("Share")) {
                        CheckBookImage();
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
                                CheckBookType();
                            } else if (TYPE.equalsIgnoreCase("Read")) {
                                ReadBook();
                            } else if (TYPE.equalsIgnoreCase("Share")) {
                                CheckBookImage();
                            }
                            Log.e("TAG", "fb ad dismissed.");
                        }

                        @Override
                        public void onError(Ad ad, com.facebook.ads.AdError adError) {
                            Log.e("TAG", "fb ad failed to load : " + adError.getErrorMessage());
                            fbInterstitialAd = null;
                            if (TYPE.equalsIgnoreCase("Download")) {
                                CheckBookType();
                            } else if (TYPE.equalsIgnoreCase("Read")) {
                                ReadBook();
                            } else if (TYPE.equalsIgnoreCase("Share")) {
                                CheckBookImage();
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

            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    Log.e("Ad failed to show.", "" + adError.toString());
                    mRewardedAd = null;
                    if (TYPE.equalsIgnoreCase("Download")) {
                        CheckBookType();
                    } else if (TYPE.equalsIgnoreCase("Read")) {
                        ReadBook();
                    } else if (TYPE.equalsIgnoreCase("Share")) {
                        CheckBookImage();
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
                        CheckBookType();
                    } else if (TYPE.equalsIgnoreCase("Read")) {
                        ReadBook();
                    } else if (TYPE.equalsIgnoreCase("Share")) {
                        CheckBookImage();
                    }
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    Log.e("TAG", "onAdImpression.");
                }
            };

            mRewardedAd.load(BookDetails.this, "" + prefManager.getValue("reward_adid"), adRequest, new RewardedAdLoadCallback() {
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
            Log.e("mRewardedAd", "Exception => " + e);
        }
    }

    private void FacebookRewardAd() {
        try {
            fbRewardedVideoAd = new RewardedVideoAd(BookDetails.this,
                    "VID_HD_16_9_15S_APP_INSTALL#" + prefManager.getValue("fb_rewardvideo_id"));

            fbRewardedVideoAd.loadAd(fbRewardedVideoAd.buildLoadAdConfig().withAdListener(new RewardedVideoAdListener() {
                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    Log.e("TAG", "Rewarded video adError => " + adError.getErrorMessage());
                    fbRewardedVideoAd.destroy();
                    fbRewardedVideoAd = null;
                    if (TYPE.equalsIgnoreCase("Download")) {
                        CheckBookType();
                    } else if (TYPE.equalsIgnoreCase("Read")) {
                        ReadBook();
                    } else if (TYPE.equalsIgnoreCase("Share")) {
                        CheckBookImage();
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
                        CheckBookType();
                    } else if (TYPE.equalsIgnoreCase("Read")) {
                        ReadBook();
                    } else if (TYPE.equalsIgnoreCase("Share")) {
                        CheckBookImage();
                    }
                }
            }).build());

        } catch (Exception e) {
            Log.e("fbRewardedVideoAd", "Exception => " + e.getMessage());
        }
    }
    //Admob & Facebook Ads END

    @Override
    protected void onPause() {
        super.onPause();
        if (paginate != null) {
            paginate.unbind();
        }
        Utils.ProgressbarHide();
        Utils.shimmerHide(shimmer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPermissionResult.unregister();
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
