package com.divinetechs.ebooksapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Activity.BookDetails;
import com.divinetechs.ebooksapp.Activity.ViewAllBook;
import com.divinetechs.ebooksapp.Adapter.AlsoLikeAdapter;
import com.divinetechs.ebooksapp.Adapter.AuthorAdapter;
import com.divinetechs.ebooksapp.Adapter.BannerAdapter;
import com.divinetechs.ebooksapp.Adapter.ContinueReadAdapter;
import com.divinetechs.ebooksapp.Adapter.FeatureAdapter;
import com.divinetechs.ebooksapp.Adapter.FreebookAdapter;
import com.divinetechs.ebooksapp.Adapter.NewArrivalAdapter;
import com.divinetechs.ebooksapp.Adapter.PaidBookAdapter;
import com.divinetechs.ebooksapp.Model.AuthorModel.AuthorModel;
import com.divinetechs.ebooksapp.Model.BannerModel.BannerModel;
import com.divinetechs.ebooksapp.Model.BookModel.BookModel;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.PrefManager;
import com.divinetechs.ebooksapp.Utility.Utils;
import com.divinetechs.ebooksapp.Webservice.AppAPI;
import com.divinetechs.ebooksapp.Webservice.BaseURL;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeBannerAd;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.ads.nativetemplates.TemplateView;
import com.kenilt.loopingviewpager.widget.LoopingViewPager;
import com.paginate.Paginate;
import com.squareup.picasso.Picasso;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends Fragment implements View.OnClickListener, Paginate.Callbacks {

    public Home() {
    }

    PrefManager prefManager;
    private View root;

    ShimmerFrameLayout shimmer;
    LinearLayout ly_top_reading_viewAll, ly_new_arrival_viewAll, ly_author_viewAll, ly_freebook_viewAll, ly_paidbook_viewAll, ly_continue_viewAll,
            lyNativeAdView, ly_continue, ly_paid_book, ly_free_book, ly_author, ly_New_Arrival_Book, ly_top_reading_Book, ly_category, ly_you_may_also_like,
            lyFreeOne, lyFreeTwo, lyPaidOne, lyPaidTwo, lyContinueOne, lyAds, lyFullAdView;
    ImageView iv_freeBook_1, iv_freeBook_2, iv_paidBook_1, iv_paidBook_2, iv_continue_read;
    TextView txt_free_bookname_1, txt_free_description_1, txt_free_book_cat_1, txt_free_bookname_2, txt_free_description_2, txt_free_book_cat_2,
            txt_paid_bookname_1, txt_paid_description_1, txt_paid_book_cat_1, txt_paid_bookname_2, txt_paid_description_2, txt_paid_book_cat_2,
            txt_continue_bookname, txt_continue_desc, txt_continue_read_cat;
    RecyclerView rv_newarrival, rvAlsoLike, rv_freebook, rv_paidbook, rv_feature_item, rv_author, rv_continue;
    LoopingViewPager mViewPager;
    DotsIndicator dotsIndicator;
    Timer timer;

    BannerAdapter bannerAdapter;
    NewArrivalAdapter newArrivalAdapter;
    AlsoLikeAdapter alsoLikeAdapter;
    FreebookAdapter freebookAdapter;
    PaidBookAdapter paidBookAdapter;
    FeatureAdapter featureAdapter;
    AuthorAdapter authorAdapter;
    ContinueReadAdapter continueReadAdapter;

    List<com.divinetechs.ebooksapp.Model.BannerModel.Result> bannerList;
    List<com.divinetechs.ebooksapp.Model.BookModel.Result> NewArrivalList;
    List<com.divinetechs.ebooksapp.Model.BookModel.Result> alsoLikeList;
    List<com.divinetechs.ebooksapp.Model.BookModel.Result> freebookList;
    List<com.divinetechs.ebooksapp.Model.BookModel.Result> paidbookList;
    List<com.divinetechs.ebooksapp.Model.BookModel.Result> FeatureList;
    List<com.divinetechs.ebooksapp.Model.AuthorModel.Result> AuthorList;
    List<com.divinetechs.ebooksapp.Model.BookModel.Result> ContinueList;

    private TemplateView nativeTemplate = null, nativeTemplate2 = null;
    private NativeBannerAd fbNativeBannerAd = null, fbNativeBannerAd2 = null;
    private NativeAdLayout fbNativeTemplate = null, fbNativeFullTemplate = null;
    private com.facebook.ads.NativeAd fbNativeAd = null;

    private boolean loading = false;
    private int page = 1, totalPages = 1;
    private Paginate paginate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (PrefManager.getInstance(getActivity()).isNightModeEnabled() == true) {
            getActivity().setTheme(R.style.darktheme);
        } else {
            getActivity().setTheme(R.style.AppTheme);
        }
        root = inflater.inflate(R.layout.homefragment, container, false);
        PrefManager.forceRTLIfSupported(getActivity().getWindow(), getActivity());

        init();
        AdInit();
        DisplayBanner();
        FeatureItem();
        NewArrival();
        AuthorList();
        freebook();
        paidbook();

        alsoLikeList = new ArrayList<>();
        setupPagination();
        AlsoLike(page);

        return root;
    }

    private void init() {
        try {
            prefManager = new PrefManager(getActivity());
            shimmer = root.findViewById(R.id.shimmer);

            nativeTemplate = root.findViewById(R.id.nativeTemplate);
            nativeTemplate2 = root.findViewById(R.id.nativeTemplate2);
            fbNativeTemplate = root.findViewById(R.id.fbNativeTemplate);
            fbNativeFullTemplate = root.findViewById(R.id.fbNativeFullTemplate);
            lyNativeAdView = root.findViewById(R.id.lyNativeAdView);
            lyFullAdView = root.findViewById(R.id.lyFullAdView);
            lyAds = root.findViewById(R.id.lyAds);
            mViewPager = root.findViewById(R.id.viewPager);
            dotsIndicator = root.findViewById(R.id.dotsIndicator);

            lyFreeOne = root.findViewById(R.id.lyFreeOne);
            lyFreeTwo = root.findViewById(R.id.lyFreeTwo);
            iv_freeBook_1 = root.findViewById(R.id.iv_freeBook_1);
            iv_freeBook_2 = root.findViewById(R.id.iv_freeBook_2);
            txt_free_bookname_1 = root.findViewById(R.id.txt_free_bookname_1);
            txt_free_bookname_2 = root.findViewById(R.id.txt_free_bookname_2);
            txt_free_book_cat_1 = root.findViewById(R.id.txt_free_book_cat_1);
            txt_free_book_cat_2 = root.findViewById(R.id.txt_free_book_cat_2);
            txt_free_description_1 = root.findViewById(R.id.txt_free_description_1);
            txt_free_description_2 = root.findViewById(R.id.txt_free_description_2);

            lyPaidOne = root.findViewById(R.id.lyPaidOne);
            lyPaidTwo = root.findViewById(R.id.lyPaidTwo);
            iv_paidBook_1 = root.findViewById(R.id.iv_paidBook_1);
            iv_paidBook_2 = root.findViewById(R.id.iv_paidBook_2);
            txt_paid_bookname_1 = root.findViewById(R.id.txt_paid_bookname_1);
            txt_paid_bookname_2 = root.findViewById(R.id.txt_paid_bookname_2);
            txt_paid_book_cat_1 = root.findViewById(R.id.txt_paid_book_cat_1);
            txt_paid_book_cat_2 = root.findViewById(R.id.txt_paid_book_cat_2);
            txt_paid_description_1 = root.findViewById(R.id.txt_paid_description_1);
            txt_paid_description_2 = root.findViewById(R.id.txt_paid_description_2);

            lyContinueOne = root.findViewById(R.id.lyContinueOne);
            iv_continue_read = root.findViewById(R.id.iv_continue_read);
            txt_continue_bookname = root.findViewById(R.id.txt_continue_bookname);
            txt_continue_desc = root.findViewById(R.id.txt_continue_desc);
            txt_continue_read_cat = root.findViewById(R.id.txt_continue_read_cat);

            rv_newarrival = root.findViewById(R.id.rv_newarrival);
            rv_feature_item = root.findViewById(R.id.rv_feature_item);
            rv_author = root.findViewById(R.id.rv_author);
            rv_continue = root.findViewById(R.id.rv_continue);
            rv_freebook = root.findViewById(R.id.rv_freebook);
            rv_paidbook = root.findViewById(R.id.rv_paidbook);
            rvAlsoLike = root.findViewById(R.id.rvAlsoLike);

            ly_continue = root.findViewById(R.id.ly_continue);
            ly_paid_book = root.findViewById(R.id.ly_paid_book);
            ly_free_book = root.findViewById(R.id.ly_free_book);
            ly_author = root.findViewById(R.id.ly_author);
            ly_New_Arrival_Book = root.findViewById(R.id.ly_New_Arrival_Book);
            ly_top_reading_Book = root.findViewById(R.id.ly_top_reading_Book);
            ly_category = root.findViewById(R.id.ly_category);
            ly_you_may_also_like = root.findViewById(R.id.ly_you_may_also_like);

            ly_top_reading_viewAll = root.findViewById(R.id.ly_top_reading_viewAll);
            ly_new_arrival_viewAll = root.findViewById(R.id.ly_new_arrival_viewAll);
            ly_author_viewAll = root.findViewById(R.id.ly_author_viewAll);
            ly_continue_viewAll = root.findViewById(R.id.ly_continue_viewAll);
            ly_paidbook_viewAll = root.findViewById(R.id.ly_paidbook_viewAll);
            ly_freebook_viewAll = root.findViewById(R.id.ly_freebook_viewAll);

            ly_top_reading_viewAll.setOnClickListener(this);
            ly_new_arrival_viewAll.setOnClickListener(this);
            ly_author_viewAll.setOnClickListener(this);
            ly_paidbook_viewAll.setOnClickListener(this);
            ly_freebook_viewAll.setOnClickListener(this);
            ly_continue_viewAll.setOnClickListener(this);
            lyFreeOne.setOnClickListener(this);
            lyFreeTwo.setOnClickListener(this);
            lyPaidOne.setOnClickListener(this);
            lyPaidTwo.setOnClickListener(this);
            lyContinueOne.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    private void AdInit() {
        Log.e("fb_native_status", "" + prefManager.getValue("fb_native_status"));
        Log.e("native_ad", "" + prefManager.getValue("native_ad"));
        if (prefManager.getValue("native_ad").equalsIgnoreCase("yes")) {
            lyNativeAdView.setVisibility(View.VISIBLE);
            nativeTemplate.setVisibility(View.VISIBLE);
            fbNativeTemplate.setVisibility(View.GONE);
            Utils.NativeAds(getActivity(), nativeTemplate, "" + prefManager.getValue("native_adid"));
        } else if (prefManager.getValue("fb_native_status").equalsIgnoreCase("on")) {
            lyNativeAdView.setVisibility(View.VISIBLE);
            fbNativeTemplate.setVisibility(View.VISIBLE);
            nativeTemplate.setVisibility(View.GONE);
            Utils.FacebookNativeAdSmall(getActivity(), fbNativeBannerAd, fbNativeTemplate, "" + prefManager.getValue("fb_native_id"));
        } else {
            lyNativeAdView.setVisibility(View.GONE);
            nativeTemplate.setVisibility(View.GONE);
            fbNativeTemplate.setVisibility(View.GONE);
        }

        Log.e("fb_native_full_status", "" + prefManager.getValue("fb_native_full_status"));
        if (prefManager.getValue("fb_native_full_status").equalsIgnoreCase("on")) {
            lyFullAdView.setVisibility(View.VISIBLE);
            nativeTemplate2.setVisibility(View.GONE);
            fbNativeFullTemplate.setVisibility(View.VISIBLE);
            Utils.FacebookNativeAdLarge(getActivity(), fbNativeAd, fbNativeFullTemplate, "" + prefManager.getValue("fb_native_full_id"));
        } else if (prefManager.getValue("fb_native_status").equalsIgnoreCase("on")) {
            lyFullAdView.setVisibility(View.VISIBLE);
            nativeTemplate2.setVisibility(View.GONE);
            fbNativeFullTemplate.setVisibility(View.VISIBLE);
            Utils.FacebookNativeAdSmall(getActivity(), fbNativeBannerAd, fbNativeFullTemplate, "" + prefManager.getValue("fb_native_id"));
        } else if (prefManager.getValue("native_ad").equalsIgnoreCase("yes")) {
            lyFullAdView.setVisibility(View.VISIBLE);
            nativeTemplate2.setVisibility(View.VISIBLE);
            fbNativeFullTemplate.setVisibility(View.GONE);
            Utils.NativeAds(getActivity(), nativeTemplate2, "" + prefManager.getValue("native_adid"));
        } else {
            lyFullAdView.setVisibility(View.GONE);
            nativeTemplate2.setVisibility(View.GONE);
            fbNativeFullTemplate.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("onStart", "called");
        ContinueRead();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ly_top_reading_viewAll:
                intent = new Intent(getActivity(), ViewAllBook.class);
                intent.putExtra("title", "" + getResources().getString(R.string.app_bestBook));
                startActivity(intent);
                break;

            case R.id.ly_new_arrival_viewAll:
                intent = new Intent(getActivity(), ViewAllBook.class);
                intent.putExtra("title", "" + getResources().getString(R.string.New_Arrival_Book));
                startActivity(intent);
                break;

            case R.id.ly_author_viewAll:
                intent = new Intent(getActivity(), ViewAllBook.class);
                intent.putExtra("title", "" + getResources().getString(R.string.Authors));
                startActivity(intent);
                break;

            case R.id.ly_paidbook_viewAll:
                intent = new Intent(getActivity(), ViewAllBook.class);
                intent.putExtra("title", "" + getResources().getString(R.string.Paid_Book));
                startActivity(intent);
                break;

            case R.id.ly_freebook_viewAll:
                intent = new Intent(getActivity(), ViewAllBook.class);
                intent.putExtra("title", "" + getResources().getString(R.string.Free_Book));
                startActivity(intent);
                break;

            case R.id.ly_continue_viewAll:
                intent = new Intent(getActivity(), ViewAllBook.class);
                intent.putExtra("title", "" + getResources().getString(R.string.Continue_Reading));
                startActivity(intent);
                break;

            case R.id.lyFreeOne:
                intent = new Intent(getActivity(), BookDetails.class);
                intent.putExtra("ID", freebookList.get(0).getId());
                getActivity().startActivity(intent);
                break;

            case R.id.lyFreeTwo:
                intent = new Intent(getActivity(), BookDetails.class);
                intent.putExtra("ID", freebookList.get(1).getId());
                getActivity().startActivity(intent);
                break;

            case R.id.lyPaidOne:
                intent = new Intent(getActivity(), BookDetails.class);
                intent.putExtra("ID", paidbookList.get(0).getId());
                getActivity().startActivity(intent);
                break;

            case R.id.lyPaidTwo:
                intent = new Intent(getActivity(), BookDetails.class);
                intent.putExtra("ID", paidbookList.get(1).getId());
                getActivity().startActivity(intent);
                break;

            case R.id.lyContinueOne:
                intent = new Intent(getActivity(), BookDetails.class);
                intent.putExtra("ID", ContinueList.get(0).getId());
                getActivity().startActivity(intent);
                break;
        }
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
                        bannerList = new ArrayList<>();
                        bannerList = response.body().getResult();
                        Log.e("bannerList", "" + bannerList.size());
                        SetBanner();
                        lyAds.setVisibility(View.VISIBLE);

                    } else {
                        lyAds.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e("get_ads_banner", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<BannerModel> call, Throwable t) {
                Utils.shimmerHide(shimmer);
                Log.e("get_ads_banner", "onFailure => " + t.getMessage());
                lyAds.setVisibility(View.GONE);
            }
        });
    }

    private void SetBanner() {
        bannerAdapter = new BannerAdapter(getActivity(), bannerList);
        mViewPager.setAdapter(bannerAdapter, getChildFragmentManager());
        bannerAdapter.notifyDataSetChanged();
        dotsIndicator.setViewPager(mViewPager);

        if (bannerList.size() > 0) {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    mViewPager.post(new Runnable() {
                        @Override
                        public void run() {
                            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                        }
                    });
                }
            };
            timer = new Timer();
            timer.schedule(timerTask, 10000, 10000);
        }
    }

    private void FeatureItem() {
        Utils.shimmerShow(shimmer);
        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<BookModel> call = bookNPlayAPI.popularbooklist("1");
        call.enqueue(new Callback<BookModel>() {
            @Override
            public void onResponse(Call<BookModel> call, Response<BookModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        FeatureList = new ArrayList<>();
                        FeatureList = response.body().getResult();
                        Log.e("FeatureList", "" + FeatureList.size());
                        if (FeatureList.size() > 0) {
                            featureAdapter = new FeatureAdapter(getActivity(), FeatureList,
                                    "Home", prefManager.getValue("currency_symbol"));
                            rv_feature_item.setLayoutManager(new GridLayoutManager(getActivity(), 2,
                                    LinearLayoutManager.HORIZONTAL, false));
                            rv_feature_item.setAdapter(featureAdapter);
                            featureAdapter.notifyDataSetChanged();
                            ly_top_reading_Book.setVisibility(View.VISIBLE);
                            rv_feature_item.setVisibility(View.VISIBLE);
                        } else {
                            ly_top_reading_Book.setVisibility(View.GONE);
                            rv_feature_item.setVisibility(View.GONE);
                        }
                    }

                } catch (Exception e) {
                    Log.e("popularbookList", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<BookModel> call, Throwable t) {
                Log.e("popularbookList", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
            }
        });
    }

    private void NewArrival() {
        Utils.shimmerShow(shimmer);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<BookModel> call = bookNPlayAPI.newarriaval("1");
        call.enqueue(new Callback<BookModel>() {
            @Override
            public void onResponse(Call<BookModel> call, Response<BookModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult().size() > 0) {
                            NewArrivalList = new ArrayList<>();
                            NewArrivalList = response.body().getResult();
                            Log.e("NewArrivalList", "" + NewArrivalList.size());

                            newArrivalAdapter = new NewArrivalAdapter(getActivity(), NewArrivalList, "Home");
                            rv_newarrival.setLayoutManager(new GridLayoutManager(getActivity(), 3,
                                    LinearLayoutManager.VERTICAL, false));
                            rv_newarrival.setAdapter(newArrivalAdapter);
                            newArrivalAdapter.notifyDataSetChanged();
                            rv_newarrival.setVisibility(View.VISIBLE);
                            ly_New_Arrival_Book.setVisibility(View.VISIBLE);
                        } else {
                            rv_newarrival.setVisibility(View.GONE);
                            ly_New_Arrival_Book.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    Log.e("newarriaval", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<BookModel> call, Throwable t) {
                Log.e("newarriaval", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
            }
        });
    }

    private void AuthorList() {
        Utils.shimmerShow(shimmer);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<AuthorModel> call = bookNPlayAPI.autherlist("1");
        call.enqueue(new Callback<AuthorModel>() {
            @Override
            public void onResponse(Call<AuthorModel> call, Response<AuthorModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult().size() > 0) {
                            AuthorList = new ArrayList<>();
                            AuthorList = response.body().getResult();
                            Log.e("AuthorList", "" + AuthorList.size());

                            authorAdapter = new AuthorAdapter(getActivity(), AuthorList, "Home");
                            rv_author.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                            rv_author.setAdapter(authorAdapter);
                            authorAdapter.notifyDataSetChanged();
                            rv_author.setVisibility(View.VISIBLE);
                            ly_author.setVisibility(View.VISIBLE);
                        } else {
                            rv_author.setVisibility(View.GONE);
                            ly_author.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    Log.e("authorList", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<AuthorModel> call, Throwable t) {
                Log.e("autherlist", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
            }
        });
    }

    private void freebook() {
        Utils.shimmerShow(shimmer);

        AppAPI appAPI = BaseURL.getVideoAPI();
        Call<BookModel> call = appAPI.free_paid_booklist("0", "1");
        call.enqueue(new Callback<BookModel>() {
            @Override
            public void onResponse(Call<BookModel> call, Response<BookModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult().size() > 0) {
                            freebookList = new ArrayList<>();
                            freebookList = response.body().getResult();
                            Log.e("freebookList", "" + freebookList.size());

                            txt_free_bookname_1.setText("" + freebookList.get(0).getTitle());
                            txt_free_description_1.setText(Html.fromHtml(freebookList.get(0).getDescription()));
                            txt_free_book_cat_1.setText("" + freebookList.get(0).getCategoryName());
                            Picasso.get().load(freebookList.get(0).getImage()).into(iv_freeBook_1);

                            txt_free_bookname_2.setText("" + freebookList.get(1).getTitle());
                            txt_free_description_2.setText(Html.fromHtml(freebookList.get(1).getDescription()));
                            txt_free_book_cat_2.setText("" + freebookList.get(1).getCategoryName());
                            Picasso.get().load(freebookList.get(1).getImage()).into(iv_freeBook_2);

                            if (freebookList.size() > 2) {
                                freebookAdapter = new FreebookAdapter(getActivity(), freebookList, "Home");
                                rv_freebook.setLayoutManager(new GridLayoutManager(getActivity(), 3,
                                        LinearLayoutManager.VERTICAL, false));
                                rv_freebook.setAdapter(freebookAdapter);
                                freebookAdapter.notifyDataSetChanged();
                                rv_freebook.setVisibility(View.VISIBLE);
                                ly_free_book.setVisibility(View.VISIBLE);
                            } else {
                                rv_freebook.setVisibility(View.GONE);
                            }

                        } else {
                            rv_freebook.setVisibility(View.GONE);
                            ly_free_book.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    Log.e("free_booklist =>", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<BookModel> call, Throwable t) {
                Utils.shimmerHide(shimmer);
                Log.e("free_booklist", "onFailure => " + t.getMessage());
            }
        });
    }

    private void paidbook() {
        Utils.shimmerShow(shimmer);

        AppAPI appAPI = BaseURL.getVideoAPI();
        Call<BookModel> call = appAPI.free_paid_booklist("1", "1");
        call.enqueue(new Callback<BookModel>() {
            @Override
            public void onResponse(Call<BookModel> call, Response<BookModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult().size() > 0) {
                            paidbookList = new ArrayList<>();
                            paidbookList = response.body().getResult();
                            Log.e("paidbookList", "" + paidbookList.size());

                            txt_paid_bookname_1.setText("" + paidbookList.get(0).getTitle());
                            txt_paid_description_1.setText(Html.fromHtml(paidbookList.get(0).getDescription()));
                            txt_paid_book_cat_1.setText("" + paidbookList.get(0).getCategoryName());
                            Picasso.get().load(paidbookList.get(0).getImage()).into(iv_paidBook_1);

                            txt_paid_bookname_2.setText("" + paidbookList.get(1).getTitle());
                            txt_paid_description_2.setText(Html.fromHtml(paidbookList.get(1).getDescription()));
                            txt_paid_book_cat_2.setText("" + paidbookList.get(1).getCategoryName());
                            Picasso.get().load(paidbookList.get(1).getImage()).into(iv_paidBook_2);

                            if (paidbookList.size() > 2) {
                                paidBookAdapter = new PaidBookAdapter(getActivity(), paidbookList,
                                        "Home", prefManager.getValue("currency_symbol"));
                                rv_paidbook.setLayoutManager(new GridLayoutManager(getActivity(), 3,
                                        LinearLayoutManager.VERTICAL, false));
                                rv_paidbook.setAdapter(paidBookAdapter);
                                paidBookAdapter.notifyDataSetChanged();
                                rv_paidbook.setVisibility(View.VISIBLE);
                                ly_paid_book.setVisibility(View.VISIBLE);
                            } else {
                                rv_paidbook.setVisibility(View.GONE);
                            }

                        } else {
                            rv_paidbook.setVisibility(View.GONE);
                            ly_paid_book.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    Log.e("paid_booklist", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<BookModel> call, Throwable t) {
                Utils.shimmerHide(shimmer);
                Log.e("paid_booklist", "onFailure => " + t.getMessage());
            }
        });
    }

    private void ContinueRead() {

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<BookModel> call = bookNPlayAPI.continue_read("" + prefManager.getLoginId(), "1");
        call.enqueue(new Callback<BookModel>() {
            @Override
            public void onResponse(Call<BookModel> call, Response<BookModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult().size() > 0) {
                            ContinueList = new ArrayList<>();
                            ContinueList = response.body().getResult();
                            Log.e("ContinueList", "" + ContinueList.size());

                            txt_continue_bookname.setText("" + ContinueList.get(0).getTitle());
                            txt_continue_desc.setText(Html.fromHtml(ContinueList.get(0).getDescription()));
                            txt_continue_read_cat.setText("" + ContinueList.get(0).getCategoryName());
                            Picasso.get().load(ContinueList.get(0).getImage()).into(iv_continue_read);

                            if (ContinueList.size() > 1) {
                                continueReadAdapter = new ContinueReadAdapter(getActivity(), ContinueList, "Home");
                                rv_continue.setLayoutManager(new GridLayoutManager(getActivity(), 3,
                                        LinearLayoutManager.VERTICAL, false));
                                rv_continue.setAdapter(continueReadAdapter);
                                continueReadAdapter.notifyDataSetChanged();

                                ly_continue.setVisibility(View.VISIBLE);
                                rv_continue.setVisibility(View.VISIBLE);
                            } else {
                                rv_continue.setVisibility(View.GONE);
                            }

                        } else {
                            Log.e("continue_reading", "message => " + response.body().getMessage());
                            ly_continue.setVisibility(View.GONE);
                            rv_continue.setVisibility(View.GONE);
                        }
                    } else {
                        Log.e("continue_reading", "message => " + response.body().getMessage());
                        ly_continue.setVisibility(View.GONE);
                        rv_continue.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e("continue_reading", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<BookModel> call, Throwable t) {
                Utils.shimmerHide(shimmer);
                ly_continue.setVisibility(View.GONE);
                rv_continue.setVisibility(View.GONE);
                Log.e("continue_reading", "onFailure => " + t.getMessage());
            }
        });
    }

    private void AlsoLike(int pageNo) {
        if (!loading) {
            Utils.shimmerShow(shimmer);
        }

        Log.e("pageNo ==>", "" + pageNo);
        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<BookModel> call = bookNPlayAPI.alsolike("" + pageNo);
        call.enqueue(new Callback<BookModel>() {
            @Override
            public void onResponse(Call<BookModel> call, Response<BookModel> response) {
                try {
                    Log.e("alsolike", "status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        totalPages = response.body().getTotalPage();
                        Log.e("alsolike", "totalPages => " + totalPages);

                        if (response.body().getResult().size() > 0) {
                            alsoLikeList = response.body().getResult();
                            Log.e("alsoLikeList", "" + alsoLikeList.size());

                            rvAlsoLike.setVisibility(View.VISIBLE);
                            loading = false;
                            alsoLikeAdapter.addBook(alsoLikeList);
                            ly_you_may_also_like.setVisibility(View.VISIBLE);
                        } else {
                            ly_you_may_also_like.setVisibility(View.GONE);
                            loading = false;
                        }

                    } else {
                        ly_you_may_also_like.setVisibility(View.GONE);
                        loading = false;
                    }
                } catch (Exception e) {
                    Log.e("alsoLike", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<BookModel> call, Throwable t) {
                Utils.shimmerHide(shimmer);
                if (!loading) {
                    ly_you_may_also_like.setVisibility(View.GONE);
                }
                loading = false;
                Log.e("alsoLike", "onFailure => " + t.getMessage());
            }
        });
    }

    private void setupPagination() {
        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;
        Log.e("==>page", "" + totalPages);

        alsoLikeAdapter = new AlsoLikeAdapter(getActivity(), alsoLikeList);
        rvAlsoLike.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        rvAlsoLike.setAdapter(alsoLikeAdapter);
        alsoLikeAdapter.notifyDataSetChanged();

        Utils.Pagination(rvAlsoLike, this);
    }

    @Override
    public void onLoadMore() {
        Log.e("Paginate", "onLoadMore");
        Log.e("onLoadMore", "page => " + page);
        loading = true;
        page++;
        AlsoLike(page);
    }

    @Override
    public boolean isLoading() {
        Log.e("isLoading =>", "" + loading);
        return loading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        Log.e("page =>", "" + page);
        Log.e("totalPages =>", "" + totalPages);
        if (totalPages < page) {
            return false;
        } else {
            return page == totalPages;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPause", "called");
        if (paginate != null) {
            paginate.unbind();
        }
        Utils.shimmerHide(shimmer);
        if (fbNativeBannerAd != null) {
            fbNativeBannerAd.destroy();
        }
        if (fbNativeBannerAd2 != null) {
            fbNativeBannerAd2.destroy();
        }
        if (fbNativeAd != null) {
            fbNativeAd.destroy();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy", "called");
        if (paginate != null) {
            paginate.unbind();
        }
        Utils.shimmerHide(shimmer);
        if (fbNativeBannerAd != null) {
            fbNativeBannerAd.destroy();
        }
        if (fbNativeBannerAd2 != null) {
            fbNativeBannerAd2.destroy();
        }
        if (fbNativeAd != null) {
            fbNativeAd.destroy();
        }
    }

}

