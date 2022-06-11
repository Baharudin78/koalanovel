package com.divinetechs.ebooksapp.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Adapter.MagazineAdapter;
import com.divinetechs.ebooksapp.Adapter.MagazineCategoryAdapter;
import com.divinetechs.ebooksapp.Model.CategoryModel.CategoryModel;
import com.divinetechs.ebooksapp.Model.MagazineModel.MagazineModel;
import com.divinetechs.ebooksapp.Model.MagazineModel.Result;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.PrefManager;
import com.divinetechs.ebooksapp.Utility.Utils;
import com.divinetechs.ebooksapp.Webservice.AppAPI;
import com.divinetechs.ebooksapp.Webservice.BaseURL;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdView;
import com.paginate.Paginate;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAllMagazine extends AppCompatActivity implements Paginate.Callbacks {

    PrefManager prefManager;
    ShimmerFrameLayout shimmer;

    LinearLayout lyBack, lyToolbar, lyNoData, lyFbAdView, lyMagazineCat, lyMagazine;
    TextView txtToolbarTitle;
    RelativeLayout rlAdView;
    RecyclerView rvMagazines;

    private com.facebook.ads.AdView fbAdView = null;
    private AdView mAdView = null;

    MagazineAdapter magazineAdapter;
    MagazineCategoryAdapter magazineCategoryAdapter;

    List<Result> magazineList;
    List<com.divinetechs.ebooksapp.Model.CategoryModel.Result> categoryList;

    String title = "", dataType = "";
    private boolean loading = false;
    private int page = 1, totalPages = 1;
    private Paginate paginate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.screenCapOff(ViewAllMagazine.this);
        if (PrefManager.getInstance(this).isNightModeEnabled() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_viewall_magazine);
        PrefManager.forceRTLIfSupported(getWindow(), ViewAllMagazine.this);

        init();
        AdInit();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            title = bundle.getString("title");
            Log.e("title", "" + title);

            txtToolbarTitle.setText("" + title);

            magazineList = new ArrayList<>();

            lyMagazineCat.setVisibility(View.GONE);
            lyMagazine.setVisibility(View.VISIBLE);

            if (title.equalsIgnoreCase("" + getResources().getString(R.string.Popular_Stories))) {
                dataType = "Popular";
                setupPagination(dataType);
                PopularStories(page);
            } else if (title.equalsIgnoreCase("" + getResources().getString(R.string.Top_dowloaded))) {
                dataType = "Download";
                setupPagination(dataType);
                TopDownloaded(page);
            } else if (title.equalsIgnoreCase("" + getResources().getString(R.string.Most_viewed))) {
                dataType = "MostView";
                setupPagination(dataType);
                MostViewed(page);
            } else if (title.equalsIgnoreCase("" + getResources().getString(R.string.Magazine_category))) {
                lyMagazine.setVisibility(View.GONE);
                lyMagazineCat.setVisibility(View.VISIBLE);
                categoryList = new ArrayList<>();
                dataType = "Category";
                setupPagination(dataType);
                GetCategory(page);
            }
        }

        lyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void init() {
        try {
            prefManager = new PrefManager(ViewAllMagazine.this);

            shimmer = findViewById(R.id.shimmer);
            lyToolbar = findViewById(R.id.lyToolbar);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);
            lyBack = findViewById(R.id.lyBack);
            rlAdView = findViewById(R.id.rlAdView);
            lyFbAdView = findViewById(R.id.lyFbAdView);

            lyMagazine = findViewById(R.id.lyMagazine);
            lyMagazineCat = findViewById(R.id.lyMagazineCat);

            rvMagazines = findViewById(R.id.rvMagazines);
            lyNoData = findViewById(R.id.lyNoData);
        } catch (Exception e) {
            Log.e("init Exception =>", "" + e);
        }
    }

    private void AdInit() {
        Log.e("banner_ad", "" + prefManager.getValue("banner_ad"));
        if (prefManager.getValue("banner_ad").equalsIgnoreCase("yes")) {
            rlAdView.setVisibility(View.VISIBLE);
            Utils.Admob(ViewAllMagazine.this, mAdView, prefManager.getValue("banner_adid"), rlAdView);
        } else {
            rlAdView.setVisibility(View.GONE);
        }

        Log.e("fb_banner_status", "" + prefManager.getValue("fb_banner_status"));
        if (prefManager.getValue("fb_banner_status").equalsIgnoreCase("on")) {
            lyFbAdView.setVisibility(View.VISIBLE);
            Utils.FacebookBannerAd(ViewAllMagazine.this, fbAdView, "" + prefManager.getValue("fb_banner_id"), lyFbAdView);
        } else {
            lyFbAdView.setVisibility(View.GONE);
        }
    }

    private void PopularStories(int pageNo) {
        if (!loading) {
            Utils.shimmerShow(shimmer);
        }

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<MagazineModel> call = bookNPlayAPI.popular_magazine("" + pageNo);
        call.enqueue(new Callback<MagazineModel>() {
            @Override
            public void onResponse(Call<MagazineModel> call, Response<MagazineModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        totalPages = response.body().getTotalPage();
                        Log.e("totalPages", "" + totalPages);

                        if (response.body().getResult().size() > 0) {
                            magazineList = response.body().getResult();
                            Log.e("magazineList", "" + magazineList.size());

                            rvMagazines.setVisibility(View.VISIBLE);
                            loading = false;
                            magazineAdapter.addBook(magazineList);
                            lyNoData.setVisibility(View.GONE);
                        } else {
                            rvMagazines.setVisibility(View.GONE);
                            lyNoData.setVisibility(View.VISIBLE);
                            loading = false;
                        }

                    } else {
                        rvMagazines.setVisibility(View.GONE);
                        lyNoData.setVisibility(View.VISIBLE);
                        loading = false;
                    }
                } catch (Exception e) {
                    Log.e("popular_magazine", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<MagazineModel> call, Throwable t) {
                Log.e("popular_magazine", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
                if (!loading) {
                    rvMagazines.setVisibility(View.GONE);
                    lyNoData.setVisibility(View.VISIBLE);
                }
                loading = false;
            }
        });
    }

    private void GetCategory(int pageNo) {
        if (!loading) {
            Utils.shimmerShow(shimmer);
        }

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<CategoryModel> call = bookNPlayAPI.categorylist("" + pageNo);
        call.enqueue(new Callback<CategoryModel>() {
            @Override
            public void onResponse(Call<CategoryModel> call, Response<CategoryModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
//                        totalPages = response.body().getTotalPage();
//                        Log.e("totalPages", "" + totalPages);

                        if (response.body().getResult().size() > 0) {
                            categoryList = response.body().getResult();
                            Log.e("categoryList", "" + categoryList.size());

                            rvMagazines.setVisibility(View.VISIBLE);
                            loading = false;
                            magazineCategoryAdapter.addBook(categoryList);
                            lyNoData.setVisibility(View.GONE);
                        } else {
                            rvMagazines.setVisibility(View.GONE);
                            lyNoData.setVisibility(View.VISIBLE);
                            loading = false;
                        }

                    } else {
                        rvMagazines.setVisibility(View.GONE);
                        lyNoData.setVisibility(View.VISIBLE);
                        loading = false;
                    }
                } catch (Exception e) {
                    Log.e("category", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<CategoryModel> call, Throwable t) {
                Log.e("category", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
                if (!loading) {
                    rvMagazines.setVisibility(View.GONE);
                    lyNoData.setVisibility(View.VISIBLE);
                }
                loading = false;
            }
        });
    }

    private void TopDownloaded(int pageNo) {
        if (!loading) {
            Utils.shimmerShow(shimmer);
        }

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<MagazineModel> call = bookNPlayAPI.top_download_magazine("" + pageNo);
        call.enqueue(new Callback<MagazineModel>() {
            @Override
            public void onResponse(Call<MagazineModel> call, Response<MagazineModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        totalPages = response.body().getTotalPage();
                        Log.e("totalPages", "" + totalPages);

                        if (response.body().getResult().size() > 0) {
                            magazineList = response.body().getResult();
                            Log.e("magazineList", "" + magazineList.size());

                            rvMagazines.setVisibility(View.VISIBLE);
                            loading = false;
                            magazineAdapter.addBook(magazineList);
                            lyNoData.setVisibility(View.GONE);
                        } else {
                            rvMagazines.setVisibility(View.GONE);
                            lyNoData.setVisibility(View.VISIBLE);
                            loading = false;
                        }

                    } else {
                        rvMagazines.setVisibility(View.GONE);
                        lyNoData.setVisibility(View.VISIBLE);
                        loading = false;
                    }
                } catch (Exception e) {
                    Log.e("top_download_magazine", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<MagazineModel> call, Throwable t) {
                Log.e("top_download_magazine", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
                if (!loading) {
                    rvMagazines.setVisibility(View.GONE);
                    lyNoData.setVisibility(View.VISIBLE);
                }
                loading = false;
            }
        });
    }

    private void MostViewed(int pageNo) {
        if (!loading) {
            Utils.shimmerShow(shimmer);
        }

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<MagazineModel> call = bookNPlayAPI.top_magazine("" + pageNo);
        call.enqueue(new Callback<MagazineModel>() {
            @Override
            public void onResponse(Call<MagazineModel> call, Response<MagazineModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        totalPages = response.body().getTotalPage();
                        Log.e("totalPages", "" + totalPages);

                        if (response.body().getResult().size() > 0) {
                            magazineList = response.body().getResult();
                            Log.e("magazineList", "" + magazineList.size());

                            rvMagazines.setVisibility(View.VISIBLE);
                            loading = false;
                            magazineAdapter.addBook(magazineList);
                            lyNoData.setVisibility(View.GONE);
                        } else {
                            rvMagazines.setVisibility(View.GONE);
                            lyNoData.setVisibility(View.VISIBLE);
                            loading = false;
                        }

                    } else {
                        rvMagazines.setVisibility(View.GONE);
                        lyNoData.setVisibility(View.VISIBLE);
                        loading = false;
                    }
                } catch (Exception e) {
                    Log.e("top_magazine", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<MagazineModel> call, Throwable t) {
                Log.e("top_magazine", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
                if (!loading) {
                    rvMagazines.setVisibility(View.GONE);
                    lyNoData.setVisibility(View.VISIBLE);
                }
                loading = false;
            }
        });
    }

    private void setupPagination(String dataType) {
        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;

        if (dataType.equalsIgnoreCase("Popular")) {
            magazineAdapter = new MagazineAdapter(ViewAllMagazine.this, magazineList, "viewAll");
            rvMagazines.setLayoutManager(new GridLayoutManager(ViewAllMagazine.this, 3));
            rvMagazines.setAdapter(magazineAdapter);
            magazineAdapter.notifyDataSetChanged();

        } else if (dataType.equalsIgnoreCase("Category")) {
            magazineCategoryAdapter = new MagazineCategoryAdapter(ViewAllMagazine.this, categoryList, "viewAll");
            rvMagazines.setLayoutManager(new GridLayoutManager(ViewAllMagazine.this, 4));
            rvMagazines.setAdapter(magazineCategoryAdapter);
            magazineCategoryAdapter.notifyDataSetChanged();

        } else if (dataType.equalsIgnoreCase("Download")) {
            magazineAdapter = new MagazineAdapter(ViewAllMagazine.this, magazineList, "viewAll");
            rvMagazines.setLayoutManager(new GridLayoutManager(ViewAllMagazine.this, 3));
            rvMagazines.setAdapter(magazineAdapter);
            magazineAdapter.notifyDataSetChanged();

        } else if (dataType.equalsIgnoreCase("MostView")) {
            magazineAdapter = new MagazineAdapter(ViewAllMagazine.this, magazineList, "viewAll");
            rvMagazines.setLayoutManager(new GridLayoutManager(ViewAllMagazine.this, 3));
            rvMagazines.setAdapter(magazineAdapter);
            magazineAdapter.notifyDataSetChanged();

        }

        Utils.Pagination(rvMagazines, this);
    }

    @Override
    public void onLoadMore() {
        Log.e("Paginate", "onLoadMore");
        loading = true;
        page++;

        if (dataType.equalsIgnoreCase("Popular")) {
            PopularStories(page);
        } else if (dataType.equalsIgnoreCase("Category")) {
            GetCategory(page);
        } else if (dataType.equalsIgnoreCase("Download")) {
            TopDownloaded(page);
        } else if (dataType.equalsIgnoreCase("MostView")) {
            MostViewed(page);
        }
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

    @Override
    public void onPause() {
        super.onPause();
        if (paginate != null) {
            paginate.unbind();
        }
        Utils.shimmerHide(shimmer);
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (fbAdView != null) {
            fbAdView.destroy();
            fbAdView = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (paginate != null) {
            paginate.unbind();
        }
        Utils.shimmerHide(shimmer);
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (fbAdView != null) {
            fbAdView.destroy();
            fbAdView = null;
        }
    }

}

