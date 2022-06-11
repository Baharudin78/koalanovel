package com.divinetechs.ebooksapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Activity.PDFShow;
import com.divinetechs.ebooksapp.Adapter.MyPurchaseAdapter;
import com.divinetechs.ebooksapp.Interface.ItemClick;
import com.divinetechs.ebooksapp.Model.DownloadModel.DownloadModel;
import com.divinetechs.ebooksapp.Model.DownloadModel.Result;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.DownloadEpub;
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

public class PurchaseMagazines extends Fragment implements ItemClick, Paginate.Callbacks {

    PrefManager prefManager;
    ShimmerFrameLayout shimmer;

    List<Result> magazineList;
    RecyclerView rvMyMagazines;
    MyPurchaseAdapter myPurchaseAdapter;
    LinearLayout lyDataNotFound, lyFbAdView;

    RelativeLayout rlAdView;
    private com.facebook.ads.AdView fbAdView = null;
    private AdView mAdView = null;

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
        View root = inflater.inflate(R.layout.purchase_magazines, container, false);
        PrefManager.forceRTLIfSupported(getActivity().getWindow(), getActivity());

        prefManager = new PrefManager(getActivity());

        shimmer = root.findViewById(R.id.shimmer);
        rlAdView = root.findViewById(R.id.rlAdView);
        lyFbAdView = root.findViewById(R.id.lyFbAdView);
        rvMyMagazines = root.findViewById(R.id.rvMyMagazines);
        lyDataNotFound = root.findViewById(R.id.lyDataNotFound);

        AdInit();
        magazineList = new ArrayList<>();
        setupPagination();
        PurchaseMagazine(page);

        return root;
    }

    private void AdInit() {
        Log.e("banner_ad", "" + prefManager.getValue("banner_ad"));
        if (prefManager.getValue("banner_ad").equalsIgnoreCase("yes")) {
            rlAdView.setVisibility(View.VISIBLE);
            Utils.Admob(getActivity(), mAdView, prefManager.getValue("banner_adid"), rlAdView);
        } else {
            rlAdView.setVisibility(View.GONE);
        }

        Log.e("fb_banner_status", "" + prefManager.getValue("fb_banner_status"));
        if (prefManager.getValue("fb_banner_status").equalsIgnoreCase("on")) {
            lyFbAdView.setVisibility(View.VISIBLE);
            Utils.FacebookBannerAd(getActivity(), fbAdView, "" + prefManager.getValue("fb_banner_id"), lyFbAdView);
        } else {
            lyFbAdView.setVisibility(View.GONE);
        }
    }

    private void PurchaseMagazine(int pageNo) {
        if (!loading) {
            Utils.shimmerShow(shimmer);
        }

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<DownloadModel> call = bookNPlayAPI.purchaseMagazineList(prefManager.getLoginId(), "magazine", "" + pageNo);
        call.enqueue(new Callback<DownloadModel>() {
            @Override
            public void onResponse(Call<DownloadModel> call, Response<DownloadModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        totalPages = response.body().getTotalPage();
                        Log.e("totalPages", "" + totalPages);

                        if (response.body().getResult().size() > 0) {
                            magazineList = response.body().getResult();
                            Log.e("magazineList", "" + magazineList.size());

                            rvMyMagazines.setVisibility(View.VISIBLE);
                            loading = false;
                            myPurchaseAdapter.addBook(magazineList);
                            lyDataNotFound.setVisibility(View.GONE);
                        } else {
                            rvMyMagazines.setVisibility(View.GONE);
                            lyDataNotFound.setVisibility(View.VISIBLE);
                            loading = false;
                        }

                    } else {
                        rvMyMagazines.setVisibility(View.GONE);
                        lyDataNotFound.setVisibility(View.VISIBLE);
                        loading = false;
                    }
                } catch (Exception e) {
                    Log.e("magazine download", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<DownloadModel> call, Throwable t) {
                Log.e("magazine download", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
                if (!loading) {
                    rvMyMagazines.setVisibility(View.GONE);
                    lyDataNotFound.setVisibility(View.VISIBLE);
                }
                loading = false;
            }
        });
    }

    @Override
    public void OnClick(String id, int position) {
        ReadBook(position);
    }

    private void ReadBook(int position) {
        try {
            Log.e("url", "" + magazineList.get(position).getUrl());
            Log.e("epub or not ?", "" + magazineList.get(position).getUrl().contains(".EPUB"));
            if (magazineList.get(position).getUrl().contains(".epub") || magazineList.get(position).getUrl().contains(".EPUB")) {

                DownloadEpub downloadEpub = new DownloadEpub(getActivity());
                Log.e("path_pr", "" + magazineList.get(position).getUrl());
                Log.e("path_pr_id", "" + magazineList.get(position).getId());
                downloadEpub.pathEpub(magazineList.get(position).getUrl(), magazineList.get(position).getId(), "magazine", false);

            } else if (magazineList.get(position).getUrl().contains(".pdf") || magazineList.get(position).getUrl().contains(".PDF")) {

                startActivity(new Intent(getActivity(), PDFShow.class)
                        .putExtra("link", "" + magazineList.get(position).getUrl())
                        .putExtra("toolbarTitle", "" + magazineList.get(position).getTitle())
                        .putExtra("type", "link"));
            }
        } catch (Exception e) {
            Log.e("Exception-Read", "" + e.getMessage());
        }
    }

    private void setupPagination() {
        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;

        myPurchaseAdapter = new MyPurchaseAdapter(getActivity(),
                magazineList, "Magazine", "TRANSACTION", PurchaseMagazines.this, prefManager.getValue("currency_symbol"));
        rvMyMagazines.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        rvMyMagazines.setAdapter(myPurchaseAdapter);
        myPurchaseAdapter.notifyDataSetChanged();

        Utils.Pagination(rvMyMagazines, this);
    }

    @Override
    public void onLoadMore() {
        Log.e("Paginate", "onLoadMore");
        loading = true;
        page++;
        PurchaseMagazine(page);
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
        }
    }

}

