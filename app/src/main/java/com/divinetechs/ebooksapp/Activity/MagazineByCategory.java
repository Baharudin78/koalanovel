package com.divinetechs.ebooksapp.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Adapter.AuthorAdapter;
import com.divinetechs.ebooksapp.Adapter.ContinueReadAdapter;
import com.divinetechs.ebooksapp.Adapter.FeatureAdapter;
import com.divinetechs.ebooksapp.Adapter.FreebookAdapter;
import com.divinetechs.ebooksapp.Adapter.MagazineAdapter;
import com.divinetechs.ebooksapp.Adapter.NewArrivalAdapter;
import com.divinetechs.ebooksapp.Adapter.PaidBookAdapter;
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

public class MagazineByCategory extends AppCompatActivity implements Paginate.Callbacks {

    PrefManager prefManager;
    ShimmerFrameLayout shimmer;

    RecyclerView rvCategoryMagazine;
    List<Result> magazineList;
    MagazineAdapter magazineAdapter;

    LinearLayout lyBack, lyToolbar, lyNoData, lyFbAdView;
    TextView txtToolbarTitle;
    RelativeLayout rlAdView;

    private com.facebook.ads.AdView fbAdView = null;
    private AdView mAdView = null;

    String catId, catName;
    private boolean loading = false;
    private int page = 1, totalPages = 1;
    private Paginate paginate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.screenCapOff(MagazineByCategory.this);
        if (PrefManager.getInstance(this).isNightModeEnabled() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.magazine_by_category);
        PrefManager.forceRTLIfSupported(getWindow(), MagazineByCategory.this);

        init();
        AdInit();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            catId = bundle.getString("ID");
            catName = bundle.getString("Name");
            Log.e("catId", "" + catId);
            txtToolbarTitle.setText("" + catName);

            magazineList = new ArrayList<>();
            setupPagination();
            Magazines(page);
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
            prefManager = new PrefManager(MagazineByCategory.this);

            shimmer = findViewById(R.id.shimmer);
            lyToolbar = findViewById(R.id.lyToolbar);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);
            lyBack = findViewById(R.id.lyBack);

            rlAdView = findViewById(R.id.rlAdView);
            lyFbAdView = findViewById(R.id.lyFbAdView);

            rvCategoryMagazine = findViewById(R.id.rvCategoryMagazine);
            lyNoData = findViewById(R.id.lyNoData);
        } catch (Exception e) {
            Log.e("init Exception =>", "" + e);
        }
    }

    private void AdInit() {
        Log.e("banner_ad", "" + prefManager.getValue("banner_ad"));
        if (prefManager.getValue("banner_ad").equalsIgnoreCase("yes")) {
            rlAdView.setVisibility(View.VISIBLE);
            Utils.Admob(MagazineByCategory.this, mAdView, prefManager.getValue("banner_adid"), rlAdView);
        } else {
            rlAdView.setVisibility(View.GONE);
        }

        Log.e("fb_banner_status", "" + prefManager.getValue("fb_banner_status"));
        if (prefManager.getValue("fb_banner_status").equalsIgnoreCase("on")) {
            lyFbAdView.setVisibility(View.VISIBLE);
            Utils.FacebookBannerAd(MagazineByCategory.this, fbAdView, "" + prefManager.getValue("fb_banner_id"), lyFbAdView);
        } else {
            lyFbAdView.setVisibility(View.GONE);
        }
    }

    private void Magazines(int pageNo) {
        if (!loading) {
            Utils.shimmerShow(shimmer);
        }

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<MagazineModel> call = bookNPlayAPI.magazine_by_category("" + catId, "" + pageNo);
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

                            rvCategoryMagazine.setVisibility(View.VISIBLE);
                            loading = false;
                            magazineAdapter.addBook(magazineList);
                            lyNoData.setVisibility(View.GONE);
                        } else {
                            lyNoData.setVisibility(View.VISIBLE);
                            rvCategoryMagazine.setVisibility(View.GONE);
                            loading = false;
                        }

                    } else {
                        lyNoData.setVisibility(View.VISIBLE);
                        rvCategoryMagazine.setVisibility(View.GONE);
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
                    lyNoData.setVisibility(View.VISIBLE);
                    rvCategoryMagazine.setVisibility(View.GONE);
                }
                loading = false;
            }
        });
    }

    private void setupPagination() {
        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;

        magazineAdapter = new MagazineAdapter(MagazineByCategory.this, magazineList, "viewAll");
        rvCategoryMagazine.setLayoutManager(new GridLayoutManager(MagazineByCategory.this, 3));
        rvCategoryMagazine.setAdapter(magazineAdapter);
        magazineAdapter.notifyDataSetChanged();

        Utils.Pagination(rvCategoryMagazine, this);
    }

    @Override
    public void onLoadMore() {
        Log.e("Paginate", "onLoadMore");
        loading = true;
        page++;
        Magazines(page);
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
