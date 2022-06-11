package com.divinetechs.ebooksapp.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Adapter.VouchersAdapter;
import com.divinetechs.ebooksapp.Model.VoucherModel.Result;
import com.divinetechs.ebooksapp.Model.VoucherModel.VoucherModel;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.PrefManager;
import com.divinetechs.ebooksapp.Utility.Utils;
import com.divinetechs.ebooksapp.Webservice.AppAPI;
import com.divinetechs.ebooksapp.Webservice.BaseURL;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VouchersHistory extends AppCompatActivity {

    PrefManager prefManager;
    ShimmerFrameLayout shimmer;

    LinearLayout lyToolbar, lyBack, lyNoData, lyFbAdView;
    RelativeLayout rlAdView;
    TextView txtToolbarTitle, txtBack, txtVouchers, txtGems, txtTips;

    RecyclerView rvVouchers;
    VouchersAdapter vouchersAdapter;
    List<Result> voucherList;

    private com.facebook.ads.AdView fbAdView = null;
    private AdView mAdView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.screenCapOff(VouchersHistory.this);
        if (PrefManager.getInstance(this).isNightModeEnabled() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_vouchers_history);
        PrefManager.forceRTLIfSupported(getWindow(), VouchersHistory.this);

        init();
        AdInit();
        GetVouchers();

        lyToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        txtToolbarTitle.setText("" + getResources().getString(R.string.vouchers_history));

        lyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void init() {
        try {
            prefManager = new PrefManager(VouchersHistory.this);

            lyToolbar = findViewById(R.id.lyToolbar);
            lyBack = findViewById(R.id.lyBack);
            txtBack = findViewById(R.id.txtBack);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);

            shimmer = findViewById(R.id.shimmer);
            rlAdView = findViewById(R.id.rlAdView);
            lyFbAdView = findViewById(R.id.lyFbAdView);
            rvVouchers = findViewById(R.id.rvVouchers);
            lyNoData = findViewById(R.id.lyNoData);
            txtVouchers = findViewById(R.id.txtVouchers);
            txtGems = findViewById(R.id.txtGems);
            txtTips = findViewById(R.id.txtTips);
        } catch (Exception e) {
            Log.e("init Exception ==>", "" + e);
        }
    }

    private void AdInit() {
        Log.e("banner_ad", "" + prefManager.getValue("banner_ad"));
        if (prefManager.getValue("banner_ad").equalsIgnoreCase("yes")) {
            rlAdView.setVisibility(View.VISIBLE);
            Utils.Admob(VouchersHistory.this, mAdView, prefManager.getValue("banner_adid"), rlAdView);
        } else {
            rlAdView.setVisibility(View.GONE);
        }

        Log.e("fb_banner_status", "" + prefManager.getValue("fb_banner_status"));
        if (prefManager.getValue("fb_banner_status").equalsIgnoreCase("on")) {
            lyFbAdView.setVisibility(View.VISIBLE);
            Utils.FacebookBannerAd(VouchersHistory.this, fbAdView, "" + prefManager.getValue("fb_banner_id"), lyFbAdView);
        } else {
            lyFbAdView.setVisibility(View.GONE);
        }
    }

    private void GetVouchers() {
        Utils.shimmerShow(shimmer);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<VoucherModel> call = bookNPlayAPI.list_voucher("" + prefManager.getLoginId());
        call.enqueue(new Callback<VoucherModel>() {
            @Override
            public void onResponse(Call<VoucherModel> call, Response<VoucherModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult().size() > 0) {
                            voucherList = new ArrayList<>();
                            voucherList = response.body().getResult();
                            Log.e("voucherList", "" + voucherList.size());

                            txtVouchers.setText("" + response.body().getVoucherBalance());

                            vouchersAdapter = new VouchersAdapter(VouchersHistory.this, voucherList);
                            rvVouchers.setLayoutManager(new GridLayoutManager(VouchersHistory.this, 1, LinearLayoutManager.VERTICAL, false));
                            rvVouchers.setAdapter(vouchersAdapter);
                            vouchersAdapter.notifyDataSetChanged();

                            rvVouchers.setVisibility(View.VISIBLE);
                            lyNoData.setVisibility(View.GONE);
                        } else {
                            rvVouchers.setVisibility(View.GONE);
                            lyNoData.setVisibility(View.VISIBLE);
                        }

                    } else {
                        rvVouchers.setVisibility(View.GONE);
                        lyNoData.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    Log.e("get_vouchers", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<VoucherModel> call, Throwable t) {
                Log.e("get_vouchers", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
                rvVouchers.setVisibility(View.GONE);
                lyNoData.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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