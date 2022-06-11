package com.divinetechs.ebooksapp.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Adapter.TransactionAdapter;
import com.divinetechs.ebooksapp.Model.TransactionModel.Result;
import com.divinetechs.ebooksapp.Model.TransactionModel.TransactionModel;
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

public class TransactionHistory extends Fragment implements Paginate.Callbacks {

    PrefManager prefManager;
    private View root;

    RelativeLayout rlAdView;
    LinearLayout lyRecycler, lyNoData, lyFbAdView;
    ShimmerFrameLayout shimmer;
    private RecyclerView rvTransaction;

    private List<Result> transactionList;
    private TransactionAdapter transactionAdapter;

    AdView mAdView = null;
    com.facebook.ads.AdView fbAdView = null;

    private boolean loading = false;
    private int page = 1, totalPages = 1;
    private Paginate paginate;

    public TransactionHistory() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (PrefManager.getInstance(getActivity()).isNightModeEnabled() == true) {
            getActivity().setTheme(R.style.darktheme);
        } else {
            getActivity().setTheme(R.style.AppTheme);
        }
        root = inflater.inflate(R.layout.subfragment_transactionhistory, container, false);
        PrefManager.forceRTLIfSupported(getActivity().getWindow(), getActivity());

        init();
        AdInit();

        return root;
    }

    private void init() {
        try {
            prefManager = new PrefManager(getActivity());

            shimmer = root.findViewById(R.id.shimmer);
            lyNoData = root.findViewById(R.id.lyNoData);
            lyRecycler = root.findViewById(R.id.lyRecycler);
            rlAdView = root.findViewById(R.id.rlAdView);
            lyFbAdView = root.findViewById(R.id.lyFbAdView);

            rvTransaction = root.findViewById(R.id.rvTransaction);
        } catch (Exception e) {
            Log.e("TransactionHistory", "init Exception => " + e);
        }
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

    //purchaseBookList API
    private void TransactionHistory(int pageNo) {
        if (!loading) {
            Utils.shimmerShow(shimmer);
        }

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<TransactionModel> call = bookNPlayAPI.get_transaction("" + prefManager.getLoginId(), "" + pageNo);
        call.enqueue(new Callback<TransactionModel>() {
            @Override
            public void onResponse(@NonNull Call<TransactionModel> call, @NonNull Response<TransactionModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        totalPages = response.body().getTotalPage();
                        Log.e("totalPages", "" + totalPages);
                        Log.e("get_transaction", "status => " + response.body().getStatus());

                        if (response.body().getResult().size() > 0) {
                            transactionList = response.body().getResult();
                            Log.e("transactionList", "" + transactionList.size());

                            lyRecycler.setVisibility(View.VISIBLE);
                            loading = false;
                            transactionAdapter.addData(transactionList);
                            lyNoData.setVisibility(View.GONE);
                        } else {
                            lyNoData.setVisibility(View.VISIBLE);
                            lyRecycler.setVisibility(View.GONE);
                            loading = false;
                        }

                    } else {
                        Log.e("get_transaction", "massage => " + response.body().getMessage());
                        lyNoData.setVisibility(View.VISIBLE);
                        lyRecycler.setVisibility(View.GONE);
                        loading = false;
                    }
                } catch (Exception e) {
                    Log.e("get_transaction", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(@NonNull Call<TransactionModel> call, @NonNull Throwable t) {
                Utils.shimmerHide(shimmer);
                if (!loading) {
                    lyNoData.setVisibility(View.VISIBLE);
                    lyRecycler.setVisibility(View.GONE);
                }
                loading = false;
                Log.e("get_transaction", "That didn't work!!! => " + t.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        transactionList = new ArrayList<Result>();
        setupPagination();
        TransactionHistory(page);
    }

    private void setupPagination() {
        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;

        transactionAdapter = new TransactionAdapter(getActivity(), transactionList, "" + prefManager.getValue("currency_symbol"));
        rvTransaction.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        rvTransaction.setItemAnimator(new DefaultItemAnimator());
        rvTransaction.setAdapter(transactionAdapter);
        transactionAdapter.notifyDataSetChanged();

        Utils.Pagination(rvTransaction, this);
    }

    @Override
    public void onLoadMore() {
        Log.e("Paginate", "onLoadMore");
        loading = true;
        page++;
        TransactionHistory(page);
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
