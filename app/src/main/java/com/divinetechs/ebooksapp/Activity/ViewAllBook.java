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

import com.divinetechs.ebooksapp.Adapter.AuthorAdapter;
import com.divinetechs.ebooksapp.Adapter.ContinueReadAdapter;
import com.divinetechs.ebooksapp.Adapter.FeatureAdapter;
import com.divinetechs.ebooksapp.Adapter.FreebookAdapter;
import com.divinetechs.ebooksapp.Adapter.NewArrivalAdapter;
import com.divinetechs.ebooksapp.Adapter.PaidBookAdapter;
import com.divinetechs.ebooksapp.Model.AuthorModel.AuthorModel;
import com.divinetechs.ebooksapp.Model.BookModel.BookModel;
import com.divinetechs.ebooksapp.Model.BookModel.Result;
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

public class ViewAllBook extends AppCompatActivity implements Paginate.Callbacks {

    PrefManager prefManager;
    ShimmerFrameLayout shimmer;

    LinearLayout lyBack, lyToolbar, lyNoData, lyFbAdView, lyShimBook, lyShimWithDetails, lyShimAuthor;
    TextView txtToolbarTitle;
    RelativeLayout rlAdView;
    RecyclerView rvBooks;

    private com.facebook.ads.AdView fbAdView = null;
    private AdView mAdView = null;

    ContinueReadAdapter continueReadAdapter;
    FeatureAdapter featureAdapter;
    FreebookAdapter freebookAdapter;
    PaidBookAdapter paidBookAdapter;
    NewArrivalAdapter newArrivalAdapter;
    AuthorAdapter authorAdapter;

    List<Result> bookList;
    List<com.divinetechs.ebooksapp.Model.AuthorModel.Result> authorList;

    String title = "", dataType = "";
    private boolean loading = false;
    private int page = 1, totalPages = 1;
    private Paginate paginate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.screenCapOff(ViewAllBook.this);
        if (PrefManager.getInstance(this).isNightModeEnabled() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_viewall_book);
        PrefManager.forceRTLIfSupported(getWindow(), ViewAllBook.this);

        init();
        AdInit();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            title = bundle.getString("title");
            Log.e("title", "" + title);

            txtToolbarTitle.setText("" + title);

            bookList = new ArrayList<>();

            if (title.equalsIgnoreCase("" + getResources().getString(R.string.app_bestBook))) {
                dataType = "Feature";
                setupPagination(dataType);
                FeatureItem(page);
            } else if (title.equalsIgnoreCase("" + getResources().getString(R.string.Authors))) {
                dataType = "Author";
                authorList = new ArrayList<>();
                setupPagination(dataType);
                Authors(page);
            } else if (title.equalsIgnoreCase("" + getResources().getString(R.string.Free_Book))) {
                dataType = "Free";
                setupPagination(dataType);
                FreeBooks(page);
            } else if (title.equalsIgnoreCase("" + getResources().getString(R.string.Paid_Book))) {
                dataType = "Paid";
                setupPagination(dataType);
                PaidBooks(page);
            } else if (title.equalsIgnoreCase("" + getResources().getString(R.string.Continue_Reading))) {
                dataType = "Continue";
                setupPagination(dataType);
                ContinueRead(page);
            } else if (title.equalsIgnoreCase("" + getResources().getString(R.string.New_Arrival_Book))) {
                dataType = "Newarrival";
                setupPagination(dataType);
                NewArrival(page);
            }
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
            prefManager = new PrefManager(ViewAllBook.this);

            shimmer = findViewById(R.id.shimmer);
            lyToolbar = findViewById(R.id.lyToolbar);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);
            lyBack = findViewById(R.id.lyBack);
            rlAdView = findViewById(R.id.rlAdView);
            lyFbAdView = findViewById(R.id.lyFbAdView);

            lyShimBook = findViewById(R.id.lyShimBook);
            lyShimAuthor = findViewById(R.id.lyShimAuthor);
            lyShimWithDetails = findViewById(R.id.lyShimWithDetails);

            rvBooks = findViewById(R.id.rvBooks);
            lyNoData = findViewById(R.id.lyNoData);
        } catch (Exception e) {
            Log.e("init Exception =>", "" + e);
        }
    }

    private void AdInit() {
        Log.e("banner_ad", "" + prefManager.getValue("banner_ad"));
        if (prefManager.getValue("banner_ad").equalsIgnoreCase("yes")) {
            rlAdView.setVisibility(View.VISIBLE);
            Utils.Admob(ViewAllBook.this, mAdView, prefManager.getValue("banner_adid"), rlAdView);
        } else {
            rlAdView.setVisibility(View.GONE);
        }

        Log.e("fb_banner_status", "" + prefManager.getValue("fb_banner_status"));
        if (prefManager.getValue("fb_banner_status").equalsIgnoreCase("on")) {
            lyFbAdView.setVisibility(View.VISIBLE);
            Utils.FacebookBannerAd(ViewAllBook.this, fbAdView, "" + prefManager.getValue("fb_banner_id"), lyFbAdView);
        } else {
            lyFbAdView.setVisibility(View.GONE);
        }
    }

    private void FeatureItem(int pageNo) {
        if (!loading) {
            lyShimBook.setVisibility(View.GONE);
            lyShimWithDetails.setVisibility(View.VISIBLE);
            lyShimAuthor.setVisibility(View.GONE);
            Utils.shimmerShow(shimmer);
        }

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<BookModel> call = bookNPlayAPI.popularbooklist("" + pageNo);
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

                            rvBooks.setVisibility(View.VISIBLE);
                            loading = false;
                            featureAdapter.addBook(bookList);
                            lyNoData.setVisibility(View.GONE);
                        } else {
                            rvBooks.setVisibility(View.GONE);
                            lyNoData.setVisibility(View.VISIBLE);
                            loading = false;
                        }
                    } else {
                        rvBooks.setVisibility(View.GONE);
                        lyNoData.setVisibility(View.VISIBLE);
                        loading = false;
                    }
                } catch (Exception e) {
                    Log.e("feature_item", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<BookModel> call, Throwable t) {
                Log.e("feature_item", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
                if (!loading) {
                    rvBooks.setVisibility(View.GONE);
                    lyNoData.setVisibility(View.VISIBLE);
                }
                loading = false;
            }
        });
    }

    private void Authors(int pageNo) {
        if (!loading) {
            lyShimBook.setVisibility(View.GONE);
            lyShimWithDetails.setVisibility(View.GONE);
            lyShimAuthor.setVisibility(View.VISIBLE);
            Utils.shimmerShow(shimmer);
        }

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<AuthorModel> call = bookNPlayAPI.autherlist("" + pageNo);
        call.enqueue(new Callback<AuthorModel>() {
            @Override
            public void onResponse(Call<AuthorModel> call, Response<AuthorModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        totalPages = response.body().getTotalPage();
                        Log.e("totalPages", "" + totalPages);

                        if (response.body().getResult().size() > 0) {
                            authorList = response.body().getResult();
                            Log.e("authorList", "" + authorList.size());

                            rvBooks.setVisibility(View.VISIBLE);
                            loading = false;
                            authorAdapter.addBook(authorList);
                            lyNoData.setVisibility(View.GONE);
                        } else {
                            rvBooks.setVisibility(View.GONE);
                            lyNoData.setVisibility(View.VISIBLE);
                            loading = false;
                        }

                    } else {
                        rvBooks.setVisibility(View.GONE);
                        lyNoData.setVisibility(View.VISIBLE);
                        loading = false;
                    }
                } catch (Exception e) {
                    Log.e("authorlist", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<AuthorModel> call, Throwable t) {
                Log.e("authorlist", "onFailure => " + t.getMessage());
                Utils.shimmerHide(shimmer);
                if (!loading) {
                    rvBooks.setVisibility(View.GONE);
                    lyNoData.setVisibility(View.VISIBLE);
                }
                loading = false;
            }
        });
    }

    private void FreeBooks(int pageNo) {
        if (!loading) {
            lyShimBook.setVisibility(View.GONE);
            lyShimWithDetails.setVisibility(View.VISIBLE);
            lyShimAuthor.setVisibility(View.GONE);
            Utils.shimmerShow(shimmer);
        }

        AppAPI appAPI = BaseURL.getVideoAPI();
        Call<BookModel> call = appAPI.free_paid_booklist("0", "" + pageNo);
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

                            rvBooks.setVisibility(View.VISIBLE);
                            loading = false;
                            freebookAdapter.addBook(bookList);
                            lyNoData.setVisibility(View.GONE);
                        } else {
                            rvBooks.setVisibility(View.GONE);
                            lyNoData.setVisibility(View.VISIBLE);
                            loading = false;
                        }

                    } else {
                        rvBooks.setVisibility(View.GONE);
                        lyNoData.setVisibility(View.VISIBLE);
                        loading = false;
                    }
                } catch (Exception e) {
                    Log.e("free_booklist", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<BookModel> call, Throwable t) {
                Utils.shimmerHide(shimmer);
                if (!loading) {
                    rvBooks.setVisibility(View.GONE);
                    lyNoData.setVisibility(View.VISIBLE);
                }
                loading = false;
                Log.e("free_booklist", "onFailure => " + t.getMessage());
            }
        });
    }

    private void PaidBooks(int pageNo) {
        if (!loading) {
            lyShimBook.setVisibility(View.GONE);
            lyShimWithDetails.setVisibility(View.VISIBLE);
            lyShimAuthor.setVisibility(View.GONE);
            Utils.shimmerShow(shimmer);
        }

        AppAPI appAPI = BaseURL.getVideoAPI();
        Call<BookModel> call = appAPI.free_paid_booklist("1", "" + pageNo);
        call.enqueue(new Callback<BookModel>() {
            @Override
            public void onResponse(Call<BookModel> call, Response<BookModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        totalPages = response.body().getTotalPage();
                        Log.e("totalPages", "" + totalPages);

                        if (response.body().getResult().size() > 0) {
                            bookList = response.body().getResult();
                            Log.e("bookList", "size => " + bookList.size());

                            rvBooks.setVisibility(View.VISIBLE);
                            loading = false;
                            paidBookAdapter.addBook(bookList);
                            lyNoData.setVisibility(View.GONE);
                        } else {
                            rvBooks.setVisibility(View.GONE);
                            lyNoData.setVisibility(View.VISIBLE);
                            loading = false;
                        }
                    } else {
                        rvBooks.setVisibility(View.GONE);
                        lyNoData.setVisibility(View.VISIBLE);
                        loading = false;
                    }
                } catch (Exception e) {
                    Log.e("paid_booklist", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<BookModel> call, Throwable t) {
                Utils.shimmerHide(shimmer);
                if (!loading) {
                    rvBooks.setVisibility(View.GONE);
                    lyNoData.setVisibility(View.VISIBLE);
                }
                loading = false;
                Log.e("paid_booklist", "onFailure => " + t.getMessage());
            }
        });
    }

    private void NewArrival(int pageNo) {
        if (!loading) {
            lyShimBook.setVisibility(View.VISIBLE);
            lyShimWithDetails.setVisibility(View.GONE);
            lyShimAuthor.setVisibility(View.GONE);
            Utils.shimmerShow(shimmer);
        }

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<BookModel> call = bookNPlayAPI.newarriaval("" + pageNo);
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

                            rvBooks.setVisibility(View.VISIBLE);
                            loading = false;
                            newArrivalAdapter.addBook(bookList);
                            lyNoData.setVisibility(View.GONE);
                        } else {
                            rvBooks.setVisibility(View.GONE);
                            lyNoData.setVisibility(View.VISIBLE);
                            loading = false;
                        }
                    } else {
                        rvBooks.setVisibility(View.GONE);
                        lyNoData.setVisibility(View.VISIBLE);
                        loading = false;
                    }
                } catch (Exception e) {
                    Log.e("newarriaval", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<BookModel> call, Throwable t) {
                Log.e("newarriaval", "onFailure => " + t.getMessage());
                if (!loading) {
                    rvBooks.setVisibility(View.GONE);
                    lyNoData.setVisibility(View.VISIBLE);
                }
                loading = false;
                Utils.shimmerHide(shimmer);
            }
        });
    }

    private void ContinueRead(int pageNo) {
        if (!loading) {
            lyShimBook.setVisibility(View.GONE);
            lyShimWithDetails.setVisibility(View.VISIBLE);
            lyShimAuthor.setVisibility(View.GONE);
            Utils.shimmerShow(shimmer);
        }

        AppAPI appAPI = BaseURL.getVideoAPI();
        Call<BookModel> call = appAPI.continue_read("" + prefManager.getLoginId(), "" + pageNo);
        call.enqueue(new Callback<BookModel>() {
            @Override
            public void onResponse(Call<BookModel> call, Response<BookModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        totalPages = response.body().getTotalPage();
                        Log.e("totalPages", "" + totalPages);

                        if (response.body().getResult().size() > 0) {
                            bookList = response.body().getResult();

                            rvBooks.setVisibility(View.VISIBLE);
                            loading = false;
                            continueReadAdapter.addBook(bookList);
                            lyNoData.setVisibility(View.GONE);
                        } else {
                            rvBooks.setVisibility(View.GONE);
                            lyNoData.setVisibility(View.VISIBLE);
                            loading = false;
                        }
                    } else {
                        rvBooks.setVisibility(View.GONE);
                        lyNoData.setVisibility(View.VISIBLE);
                        loading = false;
                    }
                } catch (Exception e) {
                    Log.e("continue_read", "Exception => " + e);
                }
                Utils.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<BookModel> call, Throwable t) {
                Utils.shimmerHide(shimmer);
                if (!loading) {
                    rvBooks.setVisibility(View.GONE);
                    lyNoData.setVisibility(View.VISIBLE);
                }
                loading = false;
                Log.e("continue_read", "onFailure => " + t.getMessage());
            }
        });
    }

    private void setupPagination(String dataType) {
        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;

        if (dataType.equalsIgnoreCase("Feature")) {
            featureAdapter = new FeatureAdapter(ViewAllBook.this, bookList, "ViewAll", prefManager.getValue("currency_symbol"));
            rvBooks.setLayoutManager(new GridLayoutManager(ViewAllBook.this, 1));
            rvBooks.setAdapter(featureAdapter);
            featureAdapter.notifyDataSetChanged();

        } else if (dataType.equalsIgnoreCase("Author")) {
            authorAdapter = new AuthorAdapter(ViewAllBook.this, authorList, "ViewAll");
            rvBooks.setLayoutManager(new GridLayoutManager(ViewAllBook.this, 3));
            rvBooks.setItemAnimator(new DefaultItemAnimator());
            rvBooks.setAdapter(authorAdapter);
            authorAdapter.notifyDataSetChanged();

        } else if (dataType.equalsIgnoreCase("Free")) {
            freebookAdapter = new FreebookAdapter(ViewAllBook.this, bookList, "");
            rvBooks.setLayoutManager(new GridLayoutManager(ViewAllBook.this, 1));
            rvBooks.setAdapter(freebookAdapter);
            freebookAdapter.notifyDataSetChanged();

        } else if (dataType.equalsIgnoreCase("Paid")) {
            paidBookAdapter = new PaidBookAdapter(ViewAllBook.this, bookList, "", prefManager.getValue("currency_symbol"));
            rvBooks.setLayoutManager(new GridLayoutManager(ViewAllBook.this, 1));
            rvBooks.setAdapter(paidBookAdapter);
            paidBookAdapter.notifyDataSetChanged();

        } else if (dataType.equalsIgnoreCase("Continue")) {
            continueReadAdapter = new ContinueReadAdapter(ViewAllBook.this, bookList, "");
            rvBooks.setLayoutManager(new GridLayoutManager(ViewAllBook.this, 1));
            rvBooks.setAdapter(continueReadAdapter);
            continueReadAdapter.notifyDataSetChanged();

        } else if (dataType.equalsIgnoreCase("Newarrival")) {
            newArrivalAdapter = new NewArrivalAdapter(ViewAllBook.this, bookList, "ViewAll");
            rvBooks.setLayoutManager(new GridLayoutManager(ViewAllBook.this, 3));
            rvBooks.setAdapter(newArrivalAdapter);
            newArrivalAdapter.notifyDataSetChanged();
        }

        Utils.Pagination(rvBooks, this);
    }

    @Override
    public void onLoadMore() {
        Log.e("Paginate", "onLoadMore");
        loading = true;
        page++;

        if (dataType.equalsIgnoreCase("Feature")) {
            FeatureItem(page);
        } else if (dataType.equalsIgnoreCase("Author")) {
            Authors(page);
        } else if (dataType.equalsIgnoreCase("Free")) {
            FreeBooks(page);
        } else if (dataType.equalsIgnoreCase("Paid")) {
            PaidBooks(page);
        } else if (dataType.equalsIgnoreCase("Continue")) {
            ContinueRead(page);
        } else if (dataType.equalsIgnoreCase("Newarrival")) {
            NewArrival(page);
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

