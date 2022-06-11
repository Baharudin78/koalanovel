package com.divinetechs.ebooksapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Activity.PDFShow;
import com.divinetechs.ebooksapp.Adapter.MyDownloadsAdapter;
import com.divinetechs.ebooksapp.Interface.ItemClick;
import com.divinetechs.ebooksapp.Model.DownloadedItemModel;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.DownloadEpub;
import com.divinetechs.ebooksapp.Utility.Functions;
import com.divinetechs.ebooksapp.Utility.PermissionUtils;
import com.divinetechs.ebooksapp.Utility.PrefManager;
import com.divinetechs.ebooksapp.Utility.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdView;
import com.orhanobut.hawk.Hawk;
import com.paginate.Paginate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class DownloadMagazines extends Fragment implements ItemClick, Paginate.Callbacks {

    PrefManager prefManager;
    PermissionUtils takePermissionUtils;
    ShimmerFrameLayout shimmer;

    //List<Result> magazineList;
    private List<DownloadedItemModel> magazineList = new ArrayList<>();
    List<DownloadedItemModel> myMagazines;
    RecyclerView rvMyMagazines;
    MyDownloadsAdapter myDownloadsAdapter;
    LinearLayout lyDataNotFound, lyFbAdView;

    RelativeLayout rlAdView;
    private com.facebook.ads.AdView fbAdView = null;
    private AdView mAdView = null;

    private boolean loading = false;
    private int page = 1, totalPages = 1, clickPosition;
    private Paginate paginate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (PrefManager.getInstance(getActivity()).isNightModeEnabled() == true) {
            getActivity().setTheme(R.style.darktheme);
        } else {
            getActivity().setTheme(R.style.AppTheme);
        }
        View root = inflater.inflate(R.layout.download_magazines, container, false);
        PrefManager.forceRTLIfSupported(getActivity().getWindow(), getActivity());

        takePermissionUtils = new PermissionUtils(getActivity(), mPermissionResult);
        prefManager = new PrefManager(getActivity());

        shimmer = root.findViewById(R.id.shimmer);
        rlAdView = root.findViewById(R.id.rlAdView);
        lyFbAdView = root.findViewById(R.id.lyFbAdView);
        rvMyMagazines = root.findViewById(R.id.rvMyMagazines);
        lyDataNotFound = root.findViewById(R.id.lyDataNotFound);

        AdInit();

        magazineList.clear();
        if (myMagazines == null) {
            magazineList = new ArrayList<>();
        }
        myMagazines = Hawk.get("my_magazines" + prefManager.getLoginId());
        Log.e("myMagazines", "" + myMagazines);

        if (myMagazines != null) {
            if (myMagazines.size() > 0) {
                for (int i = 0; i < myMagazines.size(); i++) {
                    magazineList.add(myMagazines.get(i));
                }
            }
            Log.e("=>myMagazines", "" + myMagazines.size());

            if (myMagazines.size() > 0) {
                myDownloadsAdapter = new MyDownloadsAdapter(getActivity(), magazineList, "Magazine", "DOWNLOAD",
                        DownloadMagazines.this, prefManager.getValue("currency_symbol"));
                rvMyMagazines.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                rvMyMagazines.setAdapter(myDownloadsAdapter);
                myDownloadsAdapter.notifyDataSetChanged();

                lyDataNotFound.setVisibility(View.GONE);
                rvMyMagazines.setVisibility(View.VISIBLE);
            } else {
                lyDataNotFound.setVisibility(View.VISIBLE);
                rvMyMagazines.setVisibility(View.GONE);
            }
        } else {
            lyDataNotFound.setVisibility(View.VISIBLE);
            rvMyMagazines.setVisibility(View.GONE);
        }

//        setupPagination();
//        DownloadedMagazine(page);

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

    private void DownloadedMagazine(int pageNo) {
//        if (!loading) {
//            Utils.shimmerShow(shimmer);
//        }
//
//        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
//        Call<DownloadModel> call = bookNPlayAPI.alldownload(prefManager.getLoginId(), "magazine", "" + pageNo);
//        call.enqueue(new Callback<DownloadModel>() {
//            @Override
//            public void onResponse(Call<DownloadModel> call, Response<DownloadModel> response) {
//                try {
//                    if (response.code() == 200 && response.body().getStatus() == 200) {
//                        totalPages = response.body().getTotalPage();
//                        Log.e("totalPages", "" + totalPages);
//
//                        if (response.body().getResult().size() > 0) {
//                            magazineList = response.body().getResult();
//                            Log.e("magazineList", "" + magazineList.size());
//
//                            rvMyMagazines.setVisibility(View.VISIBLE);
//                            loading = false;
//                            myDownloadAdapter.addBook(magazineList);
//                            lyDataNotFound.setVisibility(View.GONE);
//                        } else {
//                            rvMyMagazines.setVisibility(View.GONE);
//                            lyDataNotFound.setVisibility(View.VISIBLE);
//                            loading = false;
//                        }
//
//                    } else {
//                        rvMyMagazines.setVisibility(View.GONE);
//                        lyDataNotFound.setVisibility(View.VISIBLE);
//                        loading = false;
//                    }
//                } catch (Exception e) {
//                    Log.e("magazine download", "Exception => " + e);
//                }
//                Utils.shimmerHide(shimmer);
//            }
//
//            @Override
//            public void onFailure(Call<DownloadModel> call, Throwable t) {
//                Log.e("magazine download", "onFailure => " + t.getMessage());
//                Utils.shimmerHide(shimmer);
//                if (!loading) {
//                    rvMyMagazines.setVisibility(View.GONE);
//                    lyDataNotFound.setVisibility(View.VISIBLE);
//                }
//                loading = false;
//            }
//        });
    }

    @Override
    public void OnClick(String id, int position) {
        clickPosition = position;
        Log.e("=>>id", "" + id);
        Log.e("=>>clickPosition", "" + clickPosition);

        if (Functions.isConnectedToInternet(getActivity())) {
            if (takePermissionUtils.isStoragePermissionGranted()) {
                ReadMagazines(position);
            } else {
                takePermissionUtils.showStoragePermissionDailog(getString(R.string.we_need_storage_permission_for_save_video));
            }
        } else {
            Toasty.warning(getActivity(), "" + getResources().getString(R.string.internet_connection), Toasty.LENGTH_SHORT).show();
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
                            blockPermissionCheck.add(Functions.getPermissionStatus(getActivity(), key));
                        }
                    }
                    Log.e("blockPermissionCheck", "" + blockPermissionCheck);
                    Log.e("allPermissionClear", "" + allPermissionClear);
                    if (blockPermissionCheck.contains("blocked")) {
                        Functions.showPermissionSetting(getActivity(), getString(R.string.we_need_storage_permission_for_save_video));
                    } else if (allPermissionClear) {
                        Log.e("mPermissionResult", "isPermissionGranted => " + takePermissionUtils.isStoragePermissionGranted());
                        ReadMagazines(clickPosition);
                    }
                }
            });

    private void ReadMagazines(int position) {
        try {
            Log.e("url", "" + magazineList.get(position).getUrl());
            Log.e("epub or not ?", "" + magazineList.get(position).getUrl().contains(".EPUB"));
            if (magazineList.get(position).getUrl().contains(".epub") || magazineList.get(position).getUrl().contains(".EPUB")) {

                if (Utils.checkFileAvailability(getActivity(), "" + magazineList.get(position).getUrl(), "book")) {
                    DownloadEpub downloadEpub = new DownloadEpub(getActivity());
                    Log.e("path_pr", "" + magazineList.get(position).getUrl());
                    Log.e("path_pr_id", "" + magazineList.get(position).getId());
                    downloadEpub.pathEpub("" + magazineList.get(position).getUrl(), "" + magazineList.get(position).getId(), "magazine", true);
                } else {
                    DownloadEpub downloadEpub = new DownloadEpub(getActivity());
                    Log.e("path_pr", "" + magazineList.get(position).getOriginalUrl());
                    Log.e("path_pr_id", "" + magazineList.get(position).getId());
                    downloadEpub.pathEpub("" + magazineList.get(position).getOriginalUrl(), "" + magazineList.get(position).getId(), "magazine", true);
                }

            } else if (magazineList.get(position).getUrl().contains(".pdf") || magazineList.get(position).getUrl().contains(".PDF")) {
                if (Utils.checkFileAvailability(getActivity(), "" + magazineList.get(position).getUrl(), "magazine")) {
                    startActivity(new Intent(getActivity(), PDFShow.class)
                            .putExtra("link", "" + magazineList.get(position).getUrl())
                            .putExtra("toolbarTitle", "" + magazineList.get(position).getTitle())
                            .putExtra("type", "file"));
                } else {
                    startActivity(new Intent(getActivity(), PDFShow.class)
                            .putExtra("link", "" + magazineList.get(position).getOriginalUrl())
                            .putExtra("toolbarTitle", "" + magazineList.get(position).getTitle())
                            .putExtra("type", "link"));
                }
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

//        myDownloadNewAdapter = new MyDownloadAdapter(getActivity(),
//                magazineList, "Magazine", "DOWNLOAD", DownloadMagazines.this, prefManager.getValue("currency_symbol"));
//        rvMyMagazines.setLayoutManager(new GridLayoutManager(getActivity(), 1));
//        rvMyMagazines.setAdapter(myDownloadNewAdapter);
//        myDownloadNewAdapter.notifyDataSetChanged();

        Utils.Pagination(rvMyMagazines, this);
    }

    @Override
    public void onLoadMore() {
        Log.e("Paginate", "onLoadMore");
        loading = true;
        page++;
        DownloadedMagazine(page);
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

