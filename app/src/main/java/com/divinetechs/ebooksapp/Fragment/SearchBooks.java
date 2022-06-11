package com.divinetechs.ebooksapp.Fragment;

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

import com.divinetechs.ebooksapp.Adapter.SearchBookAdapter;
import com.divinetechs.ebooksapp.Model.BookModel.BookModel;
import com.divinetechs.ebooksapp.Model.BookModel.Result;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.Constant;
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

public class SearchBooks extends Fragment implements Paginate.Callbacks {

    PrefManager prefManager;
    ShimmerFrameLayout shimmer;

    List<Result> bookList;
    RecyclerView rvSearchBooks;
    SearchBookAdapter searchBookAdapter;
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
        View root = inflater.inflate(R.layout.search_books, container, false);
        PrefManager.forceRTLIfSupported(getActivity().getWindow(), getActivity());

        prefManager = new PrefManager(getActivity());

        shimmer = root.findViewById(R.id.shimmer);
        rlAdView = root.findViewById(R.id.rlAdView);
        lyFbAdView = root.findViewById(R.id.lyFbAdView);
        rvSearchBooks = root.findViewById(R.id.rvSearchBooks);
        lyDataNotFound = root.findViewById(R.id.lyDataNotFound);

        AdInit();
        bookList = new ArrayList<>();
        setupPagination();
        BookSearch(page);

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

    private void BookSearch(int pageNo) {
        if (!loading) {
            Utils.shimmerShow(shimmer);
        }

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<BookModel> call = bookNPlayAPI.booksearch(Constant.strSearch, "" + pageNo);
        call.enqueue(new Callback<BookModel>() {
            @Override
            public void onResponse(Call<BookModel> call, Response<BookModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        totalPages = response.body().getTotalPage();
                        Log.e("totalPages", "" + totalPages);

                        if (response.body().getResult().size() > 0) {
                            bookList = response.body().getResult();
                            Log.e("bookList", "" + bookList.size());

                            rvSearchBooks.setVisibility(View.VISIBLE);
                            loading = false;
                            searchBookAdapter.addBook(bookList);
                            lyDataNotFound.setVisibility(View.GONE);
                        } else {
                            rvSearchBooks.setVisibility(View.GONE);
                            lyDataNotFound.setVisibility(View.VISIBLE);
                            loading = false;
                        }

                    } else {
                        rvSearchBooks.setVisibility(View.GONE);
                        lyDataNotFound.setVisibility(View.VISIBLE);
                        loading = false;
                    }
                } catch (Exception e) {
                    Log.e("booksearch", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<BookModel> call, Throwable t) {
                Utils.shimmerHide(shimmer);
                Log.e("booksearch", "onFailure => " + t.getMessage());
                if (!loading) {
                    rvSearchBooks.setVisibility(View.GONE);
                    lyDataNotFound.setVisibility(View.VISIBLE);
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

        searchBookAdapter = new SearchBookAdapter(getActivity(), bookList, "Home", "" + prefManager.getValue("currency_symbol"));
        rvSearchBooks.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        rvSearchBooks.setAdapter(searchBookAdapter);
        searchBookAdapter.notifyDataSetChanged();

        Utils.Pagination(rvSearchBooks, this);
    }

    @Override
    public void onLoadMore() {
        Log.e("Paginate", "onLoadMore");
        loading = true;
        page++;
        BookSearch(page);
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

