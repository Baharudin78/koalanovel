package com.divinetechs.ebooksapp.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Adapter.BookmarkAdapter;
import com.divinetechs.ebooksapp.Interface.ItemClick;
import com.divinetechs.ebooksapp.Model.BookmarkModel.BookmarkModel;
import com.divinetechs.ebooksapp.Model.BookmarkModel.Result;
import com.divinetechs.ebooksapp.Model.SuccessModel.SuccessModel;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.PrefManager;
import com.divinetechs.ebooksapp.Utility.Utils;
import com.divinetechs.ebooksapp.Webservice.AppAPI;
import com.divinetechs.ebooksapp.Webservice.BaseURL;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.paginate.Paginate;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookMarkMagazine extends Fragment implements ItemClick, Paginate.Callbacks {

    PrefManager prefManager;
    ShimmerFrameLayout shimmer;

    List<Result> BookmarkList;
    RecyclerView ry_bookmark;
    BookmarkAdapter bookmarkAdapter;
    LinearLayout ly_dataNotFound, ly_recycle;

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
        View root = inflater.inflate(R.layout.bookmark_magazine, container, false);
        PrefManager.forceRTLIfSupported(getActivity().getWindow(), getActivity());

        shimmer = root.findViewById(R.id.shimmer);
        ry_bookmark = root.findViewById(R.id.ry_bookmark);
        ly_dataNotFound = root.findViewById(R.id.ly_dataNotFound);
        ly_recycle = root.findViewById(R.id.ly_recycle);

        prefManager = new PrefManager(getActivity());

        BookmarkList = new ArrayList<>();
        setupPagination();
        BookmarkMagazines(page);

        return root;
    }

    private void BookmarkMagazines(int pageNo) {
        if (!loading) {
            Utils.shimmerShow(shimmer);
        }

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<BookmarkModel> call = bookNPlayAPI.allBookmark(prefManager.getLoginId(), "magazine", "" + pageNo);
        call.enqueue(new Callback<BookmarkModel>() {
            @Override
            public void onResponse(Call<BookmarkModel> call, Response<BookmarkModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        totalPages = response.body().getTotalPage();
                        Log.e("totalPages", "" + totalPages);

                        if (response.body().getResult().size() > 0) {
                            BookmarkList = response.body().getResult();
                            Log.e("BookmarkList", "" + BookmarkList.size());

                            ly_recycle.setVisibility(View.VISIBLE);
                            loading = false;
                            bookmarkAdapter.addBook(BookmarkList);
                            ly_dataNotFound.setVisibility(View.GONE);
                        } else {
                            ly_recycle.setVisibility(View.GONE);
                            ly_dataNotFound.setVisibility(View.VISIBLE);
                            loading = false;
                        }

                    } else {
                        ly_recycle.setVisibility(View.GONE);
                        ly_dataNotFound.setVisibility(View.VISIBLE);
                        loading = false;
                    }
                } catch (Exception e) {
                    Log.e("magazine bookmark", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<BookmarkModel> call, Throwable t) {
                Log.e("magazine bookmark", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
                if (!loading) {
                    ry_bookmark.setVisibility(View.GONE);
                    ly_dataNotFound.setVisibility(View.VISIBLE);
                }
                loading = false;
            }
        });
    }

    @Override
    public void OnClick(String id, int position) {
        Log.e("id", "" + id);
        Log.e("position", "" + position);

        RemoveBookMark(id);
    }

    private void RemoveBookMark(String ID) {
        Utils.ProgressBarShow(getActivity());


        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<SuccessModel> call = bookNPlayAPI.add_magazine_bookmark("" + prefManager.getLoginId(), "" + ID);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                try {
                    Toasty.success(getActivity(), "" + response.body().getMessage(), Toasty.LENGTH_SHORT).show();
                    BookmarkList = new ArrayList<>();
                    setupPagination();
                    BookmarkMagazines(1);
                } catch (Exception e) {
                    Log.e("add_bookmark", "Exception => " + e);
                }
                Utils.ProgressbarHide();
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                Log.e("add_bookmark", "onFailure => " + t.getMessage());
                Utils.ProgressbarHide();
            }
        });
    }

    private void setupPagination() {
        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;

        bookmarkAdapter = new BookmarkAdapter(getActivity(), BookmarkList, "ViewAll", BookMarkMagazine.this);
        ry_bookmark.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        ry_bookmark.setAdapter(bookmarkAdapter);
        bookmarkAdapter.notifyDataSetChanged();

        Utils.Pagination(ry_bookmark, this);
    }

    @Override
    public void onLoadMore() {
        Log.e("Paginate", "onLoadMore");
        loading = true;
        page++;
        BookmarkMagazines(page);
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
        Utils.ProgressbarHide();
        Utils.shimmerHide(shimmer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (paginate != null) {
            paginate.unbind();
        }
        Utils.ProgressbarHide();
        Utils.shimmerHide(shimmer);
    }

}

