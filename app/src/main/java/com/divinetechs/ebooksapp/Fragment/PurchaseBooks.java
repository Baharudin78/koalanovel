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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Activity.PDFShow;
import com.divinetechs.ebooksapp.Adapter.MyPurchaseAdapter;
import com.divinetechs.ebooksapp.Model.DownloadModel.DownloadModel;
import com.divinetechs.ebooksapp.Model.DownloadModel.Result;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.DownloadEpub;
import com.divinetechs.ebooksapp.Interface.ItemClick;
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

public class PurchaseBooks extends Fragment implements ItemClick, Paginate.Callbacks {

    PrefManager prefManager;
    ShimmerFrameLayout shimmer;

    List<Result> bookList;
    RecyclerView rvMyBooks;
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
        View root = inflater.inflate(R.layout.purchase_books, container, false);
        PrefManager.forceRTLIfSupported(getActivity().getWindow(), getActivity());

        prefManager = new PrefManager(getActivity());

        shimmer = root.findViewById(R.id.shimmer);
        rlAdView = root.findViewById(R.id.rlAdView);
        lyFbAdView = root.findViewById(R.id.lyFbAdView);
        rvMyBooks = root.findViewById(R.id.rvMyBooks);
        lyDataNotFound = root.findViewById(R.id.lyDataNotFound);

        AdInit();
        bookList = new ArrayList<>();
        setupPagination();
        PurchaseBook(page);

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

    private void PurchaseBook(int pageNo) {
        if (!loading) {
            Utils.shimmerShow(shimmer);
        }

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<DownloadModel> call = bookNPlayAPI.purchaseBookList("" + prefManager.getLoginId(), "book", "" + pageNo);
        call.enqueue(new Callback<DownloadModel>() {
            @Override
            public void onResponse(Call<DownloadModel> call, Response<DownloadModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        totalPages = response.body().getTotalPage();
                        Log.e("totalPages", "" + totalPages);

                        if (response.body().getResult().size() > 0) {
                            bookList = response.body().getResult();
                            Log.e("bookList", "" + bookList.size());

                            rvMyBooks.setVisibility(View.VISIBLE);
                            loading = false;
                            myPurchaseAdapter.addBook(bookList);
                            lyDataNotFound.setVisibility(View.GONE);
                        } else {
                            lyDataNotFound.setVisibility(View.VISIBLE);
                            rvMyBooks.setVisibility(View.GONE);
                            loading = false;
                        }

                    } else {
                        rvMyBooks.setVisibility(View.GONE);
                        lyDataNotFound.setVisibility(View.VISIBLE);
                        loading = false;
                    }
                } catch (Exception e) {
                    Log.e("alldownload", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<DownloadModel> call, Throwable t) {
                Log.e("alldownload", "onFailure => " + t.getMessage());
                if (!loading) {
                    lyDataNotFound.setVisibility(View.VISIBLE);
                    rvMyBooks.setVisibility(View.GONE);
                }
                Utils.shimmerHide(shimmer);
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
            Log.e("url", "" + bookList.get(position).getUrl());
            Log.e("epub or not ?", "" + bookList.get(position).getUrl().contains(".epub"));
            if (bookList.get(position).getUrl().contains(".epub") || bookList.get(position).getUrl().contains(".EPUB")) {

                DownloadEpub downloadEpub = new DownloadEpub(getActivity());
                Log.e("path_pr", "" + bookList.get(position).getUrl());
                Log.e("path_pr_id", "" + bookList.get(position).getId());
                downloadEpub.pathEpub(bookList.get(position).getUrl(), bookList.get(position).getId(), "book", false);

            } else if (bookList.get(position).getUrl().contains(".pdf") || bookList.get(position).getUrl().contains(".PDF")) {

                startActivity(new Intent(getActivity(), PDFShow.class)
                        .putExtra("link", "" + bookList.get(position).getUrl())
                        .putExtra("toolbarTitle", "" + bookList.get(position).getTitle())
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
                bookList, "Books", "TRANSACTION", PurchaseBooks.this, prefManager.getValue("currency_symbol"));
        rvMyBooks.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        rvMyBooks.setItemAnimator(new DefaultItemAnimator());
        rvMyBooks.setAdapter(myPurchaseAdapter);
        myPurchaseAdapter.notifyDataSetChanged();

        Utils.Pagination(rvMyBooks, this);
    }

    @Override
    public void onLoadMore() {
        Log.e("Paginate", "onLoadMore");
        loading = true;
        page++;
        PurchaseBook(page);
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

