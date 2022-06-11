package com.divinetechs.ebooksapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Adapter.CommentAdapter;
import com.divinetechs.ebooksapp.Adapter.MagazineCommentAdapter;
import com.divinetechs.ebooksapp.Model.CommentModel.CommentModel;
import com.divinetechs.ebooksapp.Model.CommentModel.Result;
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

public class CommentViewAll extends AppCompatActivity implements Paginate.Callbacks {

    PrefManager prefManager;
    ShimmerFrameLayout shimmer;

    RecyclerView rv_comment;
    List<Result> commentList;
    CommentAdapter commentAdapter;
    MagazineCommentAdapter magazineCommentAdapter;

    TextView txt_total_comments, txtToolbarTitle;

    LinearLayout lyToolbar, lyBack, lyFbAdView, ly_dataNotFound;

    RelativeLayout rlAdView;
    private com.facebook.ads.AdView fbAdView = null;
    private AdView mAdView = null;

    String ID, TYPE = "", dataType;
    private boolean loading = false;
    private int page = 1, totalPages = 1;
    private Paginate paginate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.screenCapOff(CommentViewAll.this);
        if (PrefManager.getInstance(this).isNightModeEnabled() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_comment_viewall);
        PrefManager.forceRTLIfSupported(getWindow(), CommentViewAll.this);

        init();
        AdInit();

        Intent intent = getIntent();
        if (intent.hasExtra("ID")) {
            ID = intent.getStringExtra("ID");
            TYPE = intent.getStringExtra("TYPE");
            Log.e("ID =>", "" + ID);

            commentList = new ArrayList<>();
            if (TYPE.equalsIgnoreCase("Book")) {
                dataType = "Book";
                setupPagination(dataType);
                BookComments(page);
            } else {
                dataType = "Magazine";
                setupPagination(dataType);
                MagazineComments(page);
            }
        }

        txtToolbarTitle.setText("" + getResources().getString(R.string.sir_we_were_meant_to_be));
        lyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentViewAll.this.finish();
            }
        });

    }

    private void init() {
        try {
            prefManager = new PrefManager(CommentViewAll.this);

            lyToolbar = findViewById(R.id.lyToolbar);
            lyBack = findViewById(R.id.lyBack);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);
            shimmer = findViewById(R.id.shimmer);

            rv_comment = findViewById(R.id.rv_comment);
            ly_dataNotFound = findViewById(R.id.ly_dataNotFound);
            txt_total_comments = findViewById(R.id.txt_total_comments);

            rlAdView = findViewById(R.id.rlAdView);
            lyFbAdView = findViewById(R.id.lyFbAdView);
        } catch (Exception e) {
            Log.e("init Exception =>", "" + e);
        }
    }

    private void AdInit() {
        Log.e("banner_ad", "" + prefManager.getValue("banner_ad"));
        if (prefManager.getValue("banner_ad").equalsIgnoreCase("yes")) {
            rlAdView.setVisibility(View.VISIBLE);
            Utils.Admob(CommentViewAll.this, mAdView, prefManager.getValue("banner_adid"), rlAdView);
        } else {
            rlAdView.setVisibility(View.GONE);
        }

        Log.e("fb_banner_status", "" + prefManager.getValue("fb_banner_status"));
        if (prefManager.getValue("fb_banner_status").equalsIgnoreCase("on")) {
            lyFbAdView.setVisibility(View.VISIBLE);
            Utils.FacebookBannerAd(CommentViewAll.this, fbAdView, "" + prefManager.getValue("fb_banner_id"), lyFbAdView);
        } else {
            lyFbAdView.setVisibility(View.GONE);
        }
    }

    private void BookComments(int pageNo) {
        if (!loading) {
            Utils.shimmerShow(shimmer);
        }

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<CommentModel> call = bookNPlayAPI.view_comment("" + ID, "" + pageNo);
        call.enqueue(new Callback<CommentModel>() {
            @Override
            public void onResponse(Call<CommentModel> call, Response<CommentModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        totalPages = response.body().getTotalPage();
                        Log.e("totalPages", "" + totalPages);

                        if (response.body().getResult().size() > 0) {
                            commentList = response.body().getResult();
                            Log.e("commentList", "" + commentList.size());

                            rv_comment.setVisibility(View.VISIBLE);
                            loading = false;
                            commentAdapter.addBook(commentList);

                            txt_total_comments.setText(commentList.size() + " " + getResources().getString(R.string.comments_in_total));
                            ly_dataNotFound.setVisibility(View.GONE);
                        } else {
                            rv_comment.setVisibility(View.GONE);
                            txt_total_comments.setText("0 " + getResources().getString(R.string.comments_in_total));
                            ly_dataNotFound.setVisibility(View.VISIBLE);
                            loading = false;
                        }

                    } else {
                        rv_comment.setVisibility(View.GONE);
                        txt_total_comments.setText("0 " + getResources().getString(R.string.comments_in_total));
                        ly_dataNotFound.setVisibility(View.VISIBLE);
                        loading = false;
                    }
                } catch (Exception e) {
                    Log.e("view_comment", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<CommentModel> call, Throwable t) {
                Log.e("view_comment", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
                if (!loading) {
                    rv_comment.setVisibility(View.GONE);
                    txt_total_comments.setText("0 " + getResources().getString(R.string.comments_in_total));
                    ly_dataNotFound.setVisibility(View.VISIBLE);
                }
                loading = false;
            }
        });
    }

    private void MagazineComments(int pageNo) {
        if (!loading) {
            Utils.shimmerShow(shimmer);
        }

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<CommentModel> call = bookNPlayAPI.view_magazine_comment("" + ID, "" + pageNo);
        call.enqueue(new Callback<CommentModel>() {
            @Override
            public void onResponse(Call<CommentModel> call, Response<CommentModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        totalPages = response.body().getTotalPage();
                        Log.e("totalPages", "" + totalPages);

                        if (response.body().getResult().size() > 0) {
                            commentList = response.body().getResult();
                            Log.e("CommentList", "" + commentList.size());

                            rv_comment.setVisibility(View.VISIBLE);
                            loading = false;
                            magazineCommentAdapter.addBook(commentList);

                            txt_total_comments.setText(commentList.size() + " " + getResources().getString(R.string.comments_in_total));
                            ly_dataNotFound.setVisibility(View.GONE);
                        } else {
                            rv_comment.setVisibility(View.GONE);
                            txt_total_comments.setText("0 " + getResources().getString(R.string.comments_in_total));
                            ly_dataNotFound.setVisibility(View.VISIBLE);
                            loading = false;
                        }

                    } else {
                        rv_comment.setVisibility(View.GONE);
                        txt_total_comments.setText("0 " + getResources().getString(R.string.comments_in_total));
                        ly_dataNotFound.setVisibility(View.VISIBLE);
                        loading = false;
                    }
                } catch (Exception e) {
                    Log.e("view_magazine_comment", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<CommentModel> call, Throwable t) {
                Log.e("view_magazine_comment", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
                if (!loading) {
                    rv_comment.setVisibility(View.GONE);
                    txt_total_comments.setText("0 " + getResources().getString(R.string.comments_in_total));
                    ly_dataNotFound.setVisibility(View.VISIBLE);
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

        if (dataType.equalsIgnoreCase("Book")) {
            commentAdapter = new CommentAdapter(CommentViewAll.this, commentList, "ViewAll");
            rv_comment.setLayoutManager(new GridLayoutManager(CommentViewAll.this, 1));
            rv_comment.setAdapter(commentAdapter);
            commentAdapter.notifyDataSetChanged();

        } else if (dataType.equalsIgnoreCase("Magazine")) {
            magazineCommentAdapter = new MagazineCommentAdapter(CommentViewAll.this, commentList, "ViewAll");
            rv_comment.setLayoutManager(new GridLayoutManager(CommentViewAll.this, 1));
            rv_comment.setAdapter(magazineCommentAdapter);
            magazineCommentAdapter.notifyDataSetChanged();
        }

        Utils.Pagination(rv_comment, this);
    }

    @Override
    public void onLoadMore() {
        Log.e("Paginate", "onLoadMore");
        loading = true;
        page++;

        if (dataType.equalsIgnoreCase("Book")) {
            BookComments(page);
        } else if (dataType.equalsIgnoreCase("Magazine")) {
            MagazineComments(page);
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