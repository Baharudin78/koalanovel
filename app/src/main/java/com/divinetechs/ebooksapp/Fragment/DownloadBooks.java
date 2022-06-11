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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class DownloadBooks extends Fragment implements ItemClick, Paginate.Callbacks {

    PrefManager prefManager;
    PermissionUtils takePermissionUtils;
    ShimmerFrameLayout shimmer;

    //List<Result> bookList;
    private List<DownloadedItemModel> bookList = new ArrayList<>();
    List<DownloadedItemModel> myBooks;
    RecyclerView rvMyBooks;
    MyDownloadsAdapter myDownloadsAdapter;
    LinearLayout lyDataNotFound, lyFbAdView;

    RelativeLayout rlAdView;
    private com.facebook.ads.AdView fbAdView = null;
    private AdView mAdView = null;

    private boolean loading = false;
    private int page = 1, totalPages = 1, clickPosition;
    private Paginate paginate;
    File pdfFile = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (PrefManager.getInstance(getActivity()).isNightModeEnabled() == true) {
            getActivity().setTheme(R.style.darktheme);
        } else {
            getActivity().setTheme(R.style.AppTheme);
        }
        View root = inflater.inflate(R.layout.download_books, container, false);
        PrefManager.forceRTLIfSupported(getActivity().getWindow(), getActivity());

        takePermissionUtils = new PermissionUtils(getActivity(), mPermissionResult);
        prefManager = new PrefManager(getActivity());

        shimmer = root.findViewById(R.id.shimmer);
        rlAdView = root.findViewById(R.id.rlAdView);
        lyFbAdView = root.findViewById(R.id.lyFbAdView);
        rvMyBooks = root.findViewById(R.id.rvMyBooks);
        lyDataNotFound = root.findViewById(R.id.lyDataNotFound);

        AdInit();

        bookList.clear();
        if (myBooks == null) {
            bookList = new ArrayList<>();
        }
        myBooks = Hawk.get("my_books" + prefManager.getLoginId());
        Log.e("myBooks", "" + myBooks);

        if (myBooks != null) {
            if (myBooks.size() > 0) {
                for (int i = 0; i < myBooks.size(); i++) {
                    bookList.add(myBooks.get(i));
                }
            }
            Log.e("=>myBooks", "" + myBooks.size());

            if (myBooks.size() > 0) {
                myDownloadsAdapter = new MyDownloadsAdapter(getActivity(), bookList, "Books", "DOWNLOAD",
                        DownloadBooks.this, prefManager.getValue("currency_symbol"));
                rvMyBooks.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                rvMyBooks.setAdapter(myDownloadsAdapter);
                myDownloadsAdapter.notifyDataSetChanged();

                lyDataNotFound.setVisibility(View.GONE);
                rvMyBooks.setVisibility(View.VISIBLE);
            } else {
                lyDataNotFound.setVisibility(View.VISIBLE);
                rvMyBooks.setVisibility(View.GONE);
            }
        } else {
            lyDataNotFound.setVisibility(View.VISIBLE);
            rvMyBooks.setVisibility(View.GONE);
        }
//        setupPagination();
//        DownloadedBook(page);

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

    private void DownloadedBook(int pageNo) {
//        if (!loading) {
//            Utils.shimmerShow(shimmer);
//        }
//
//        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
//        Call<DownloadModel> call = bookNPlayAPI.alldownload("" + prefManager.getLoginId(), "book", "" + pageNo);
//        call.enqueue(new Callback<DownloadModel>() {
//            @Override
//            public void onResponse(Call<DownloadModel> call, Response<DownloadModel> response) {
//                try {
//                    if (response.code() == 200 && response.body().getStatus() == 200) {
//                        totalPages = response.body().getTotalPage();
//                        Log.e("totalPages", "" + totalPages);
//
//                        if (response.body().getResult().size() > 0) {
//                            bookList = response.body().getResult();
//                            Log.e("bookList", "" + bookList.size());
//
//                            rvMyBooks.setVisibility(View.VISIBLE);
//                            loading = false;
//                            myDownloadAdapter.addBook(bookList);
//                            lyDataNotFound.setVisibility(View.GONE);
//                        } else {
//                            lyDataNotFound.setVisibility(View.VISIBLE);
//                            rvMyBooks.setVisibility(View.GONE);
//                            loading = false;
//                        }
//                    } else {
//                        rvMyBooks.setVisibility(View.GONE);
//                        lyDataNotFound.setVisibility(View.VISIBLE);
//                        loading = false;
//                    }
//                } catch (Exception e) {
//                    Log.e("alldownload", "Exception => " + e);
//                }
//                Utils.shimmerHide(shimmer);
//            }
//
//            @Override
//            public void onFailure(Call<DownloadModel> call, Throwable t) {
//                Log.e("alldownload", "onFailure => " + t.getMessage());
//                if (!loading) {
//                    lyDataNotFound.setVisibility(View.VISIBLE);
//                    rvMyBooks.setVisibility(View.GONE);
//                }
//                Utils.shimmerHide(shimmer);
//                loading = false;
//            }
//
//        });
    }

    @Override
    public void OnClick(String id, int position) {
        clickPosition = position;
        Log.e("=>>id", "" + id);
        Log.e("=>>clickPosition", "" + clickPosition);

        if (Functions.isConnectedToInternet(getActivity())) {
            if (takePermissionUtils.isStoragePermissionGranted()) {
                ReadBook(position);
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
                        ReadBook(clickPosition);
                    }
                }
            });

    private void ReadBook(int position) {
        try {
            Log.e("url", "" + bookList.get(position).getUrl());
            Log.e("epub or not ?", "" + bookList.get(position).getUrl().contains(".epub"));

            if (bookList.get(position).getUrl().contains(".epub") || bookList.get(position).getUrl().contains(".EPUB")) {

                if (Utils.checkFileAvailability(getActivity(), "" + bookList.get(position).getUrl(), "book")) {
                    DownloadEpub downloadEpub = new DownloadEpub(getActivity());
                    Log.e("path_pr", "" + bookList.get(position).getUrl());
                    Log.e("path_pr_id", "" + bookList.get(position).getId());
                    downloadEpub.pathEpub("" + bookList.get(position).getUrl(), "" + bookList.get(position).getId(), "book", true);
                } else {
                    DownloadEpub downloadEpub = new DownloadEpub(getActivity());
                    Log.e("path_pr", "" + bookList.get(position).getOriginalUrl());
                    Log.e("path_pr_id", "" + bookList.get(position).getId());
                    downloadEpub.pathEpub("" + bookList.get(position).getOriginalUrl(), "" + bookList.get(position).getId(), "book", true);
                }

            } else if (bookList.get(position).getUrl().contains(".pdf") || bookList.get(position).getUrl().contains(".PDF")) {
                if (Utils.checkFileAvailability(getActivity(), "" + bookList.get(position).getUrl(), "book")) {
                    startActivity(new Intent(getActivity(), PDFShow.class)
                            .putExtra("link", "" + bookList.get(position).getUrl())
                            .putExtra("toolbarTitle", "" + bookList.get(position).getTitle())
                            .putExtra("type", "file"));
                } else {
                    startActivity(new Intent(getActivity(), PDFShow.class)
                            .putExtra("link", "" + bookList.get(position).getOriginalUrl())
                            .putExtra("toolbarTitle", "" + bookList.get(position).getTitle())
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

//        myDownloadAdapter = new MyDownloadAdapter(getActivity(),
//                bookList, "Books", "DOWNLOAD", DownloadBooks.this, prefManager.getValue("currency_symbol"));
//        rvMyBooks.setLayoutManager(new GridLayoutManager(getActivity(), 1));
//        rvMyBooks.setAdapter(myDownloadAdapter);
//        myDownloadAdapter.notifyDataSetChanged();

        Utils.Pagination(rvMyBooks, this);
    }

    @Override
    public void onLoadMore() {
        Log.e("Paginate", "onLoadMore");
        loading = true;
        page++;
        DownloadedBook(page);
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

