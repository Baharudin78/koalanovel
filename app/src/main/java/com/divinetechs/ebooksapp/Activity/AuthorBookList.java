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

import com.divinetechs.ebooksapp.Adapter.AuthorBookAdapter;
import com.divinetechs.ebooksapp.Model.BookModel.BookModel;
import com.divinetechs.ebooksapp.Model.BookModel.Result;
import com.divinetechs.ebooksapp.Model.ReadDowncntModel.ReadDowncntModel;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.PrefManager;
import com.divinetechs.ebooksapp.Utility.Utils;
import com.divinetechs.ebooksapp.Webservice.AppAPI;
import com.divinetechs.ebooksapp.Webservice.BaseURL;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.paginate.Paginate;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorBookList extends AppCompatActivity implements Paginate.Callbacks {

    PrefManager prefManager;
    ShimmerFrameLayout shimmer;

    TextView txtToolbarTitle, txt_author_book, txt_author_name, txt_author_location, txt_books_total, txt_nobook, txt_view_book, txt_download_book;
    CircularImageView iv_thumb;
    LinearLayout lyToolbar, lyBack, lyFbAdView;
    RecyclerView rv_booklist;

    List<Result> BookList;
    AuthorBookAdapter authorBookAdapter;

    RelativeLayout rlAdView;
    private com.facebook.ads.AdView fbAdView = null;
    private AdView mAdView = null;

    String a_id, a_name, a_image, a_bio, a_address;
    private boolean loading = false;
    private int page = 1, totalPages = 1;
    private Paginate paginate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.screenCapOff(AuthorBookList.this);
        if (PrefManager.getInstance(this).isNightModeEnabled() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.authorbooklist);
        PrefManager.forceRTLIfSupported(getWindow(), AuthorBookList.this);

        init();
        AdInit();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            a_id = bundle.getString("a_id");
            a_name = bundle.getString("a_name");
            a_image = bundle.getString("a_image");
            a_bio = bundle.getString("a_bio");
            a_address = bundle.getString("a_address");

            Log.e("a_id", "" + a_id);
            Log.e("a_address", "" + a_address);

            txtToolbarTitle.setText("" + a_name);
            txt_author_name.setText("" + a_name);
            txt_author_book.setText("" + a_name + "'s " + getResources().getString(R.string.Books));
            txt_author_location.setText("" + a_address);

            Picasso.get().load(a_image).into(iv_thumb);

            BookList = new ArrayList<>();
            setupPagination();
            books_by_author(page);

            read_download_by_author();
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
            prefManager = new PrefManager(AuthorBookList.this);

            shimmer = findViewById(R.id.shimmer);
            rlAdView = findViewById(R.id.rlAdView);
            lyFbAdView = findViewById(R.id.lyFbAdView);
            txt_author_book = findViewById(R.id.txt_author_book);
            txt_author_name = findViewById(R.id.txt_author_name);
            txt_author_location = findViewById(R.id.txt_author_location);
            txt_books_total = findViewById(R.id.txt_books_total);
            txt_nobook = findViewById(R.id.txt_nobook);
            rv_booklist = findViewById(R.id.rv_booklist);
            iv_thumb = findViewById(R.id.image);
            txt_view_book = findViewById(R.id.txt_view_book);
            txt_download_book = findViewById(R.id.txt_download_book);
            lyToolbar = findViewById(R.id.lyToolbar);
            lyBack = findViewById(R.id.lyBack);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    private void AdInit() {
        Log.e("banner_ad", "" + prefManager.getValue("banner_ad"));
        if (prefManager.getValue("banner_ad").equalsIgnoreCase("yes")) {
            rlAdView.setVisibility(View.VISIBLE);
            Utils.Admob(AuthorBookList.this, mAdView, prefManager.getValue("banner_adid"), rlAdView);
        } else {
            rlAdView.setVisibility(View.GONE);
        }

        Log.e("fb_banner_status", "" + prefManager.getValue("fb_banner_status"));
        if (prefManager.getValue("fb_banner_status").equalsIgnoreCase("on")) {
            lyFbAdView.setVisibility(View.VISIBLE);
            Utils.FacebookBannerAd(AuthorBookList.this, fbAdView, "" + prefManager.getValue("fb_banner_id"), lyFbAdView);
        } else {
            lyFbAdView.setVisibility(View.GONE);
        }
    }

    private void books_by_author(int pageNo) {
        Utils.shimmerShow(shimmer);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<BookModel> call = bookNPlayAPI.books_by_author("" + a_id, "" + pageNo);
        call.enqueue(new Callback<BookModel>() {
            @Override
            public void onResponse(Call<BookModel> call, Response<BookModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        totalPages = response.body().getTotalPage();
                        Log.e("totalPages", "" + totalPages);

                        if (response.body().getResult().size() > 0) {
                            BookList = response.body().getResult();
                            Log.e("BookList", "" + BookList.size());

                            //Total Book count show
                            txt_books_total.setText("" + BookList.size());

                            rv_booklist.setVisibility(View.VISIBLE);
                            loading = false;
                            authorBookAdapter.addBook(BookList);
                            txt_nobook.setVisibility(View.GONE);
                        } else {
                            txt_nobook.setVisibility(View.VISIBLE);
                            rv_booklist.setVisibility(View.GONE);
                            loading = false;
                        }
                    } else {
                        txt_nobook.setVisibility(View.VISIBLE);
                        rv_booklist.setVisibility(View.GONE);
                        loading = false;
                    }

                } catch (Exception e) {
                    Log.e("books_by_author", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<BookModel> call, Throwable t) {
                Log.e("books_by_author", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
                if (!loading) {
                    txt_nobook.setVisibility(View.VISIBLE);
                    rv_booklist.setVisibility(View.GONE);
                }
                loading = false;
            }
        });
    }

    private void read_download_by_author() {
        Utils.shimmerShow(shimmer);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<ReadDowncntModel> call = bookNPlayAPI.readcnt_by_author(a_id);
        call.enqueue(new Callback<ReadDowncntModel>() {
            @Override
            public void onResponse(Call<ReadDowncntModel> call, Response<ReadDowncntModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        txt_view_book.setText("" + response.body().getResult().get(0).getReadcount());
                        txt_download_book.setText("" + response.body().getResult().get(0).getDownload());
                    } else {
                        txt_view_book.setText("0");
                        txt_download_book.setText("0");
                    }
                } catch (Exception e) {
                    Log.e("readcnt_by_author", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<ReadDowncntModel> call, Throwable t) {
                Log.e("readcnt_by_author", "onFailure =>" + t.getMessage());
                Utils.shimmerHide(shimmer);
            }
        });
    }

    private void setupPagination() {
        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;

        authorBookAdapter = new AuthorBookAdapter(AuthorBookList.this, BookList);
        rv_booklist.setLayoutManager(new GridLayoutManager(AuthorBookList.this, 3));
        rv_booklist.setItemAnimator(new DefaultItemAnimator());
        rv_booklist.setAdapter(authorBookAdapter);
        authorBookAdapter.notifyDataSetChanged();

        Utils.Pagination(rv_booklist, this);
    }

    @Override
    public void onLoadMore() {
        Log.e("Paginate", "onLoadMore");
        loading = true;
        page++;
        books_by_author(page);
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
