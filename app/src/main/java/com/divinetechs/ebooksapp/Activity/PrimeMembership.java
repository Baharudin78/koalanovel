package com.divinetechs.ebooksapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.divinetechs.ebooksapp.Adapter.PrimeBookAdapter;
import com.divinetechs.ebooksapp.Model.BookModel.BookModel;
import com.divinetechs.ebooksapp.Model.BookModel.Result;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.PrefManager;
import com.divinetechs.ebooksapp.Utility.Utils;
import com.divinetechs.ebooksapp.Webservice.AppAPI;
import com.divinetechs.ebooksapp.Webservice.BaseURL;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrimeMembership extends AppCompatActivity {

    PrefManager prefManager;

    LinearLayout lyToolbar, lyBack, ly_viewall_books, ly_for_newcomer, ly_upgrade_for_month, ly_upgrade_for_year;

    TextView txtToolbarTitle, txtBack, txt_about_prime, txt_upgrade_year, txt_upgrade_month, txt_viewall_benefits;

    RecyclerView rv_prime_book;
    List<Result> PrimeBookList;
    PrimeBookAdapter primeBookAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.screenCapOff(PrimeMembership.this);
        Log.e("=>theme", "" + PrefManager.getInstance(this).isNightModeEnabled());
        if (PrefManager.getInstance(this).isNightModeEnabled() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_prime_membership);
        PrefManager.forceRTLIfSupported(getWindow(), PrimeMembership.this);

        init();
        Prime_Item();

        lyToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        txtToolbarTitle.setText("" + getResources().getString(R.string.Prime_Membership));

        lyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        try {
            prefManager = new PrefManager(PrimeMembership.this);

            lyToolbar = findViewById(R.id.lyToolbar);
            lyBack = findViewById(R.id.lyBack);
            txtBack = findViewById(R.id.txtBack);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);

            ly_viewall_books = findViewById(R.id.ly_viewall_books);
            ly_for_newcomer = findViewById(R.id.ly_for_newcomer);
            ly_upgrade_for_month = findViewById(R.id.ly_upgrade_for_month);
            ly_upgrade_for_year = findViewById(R.id.ly_upgrade_for_year);

            txt_about_prime = findViewById(R.id.txt_about_prime);
            txt_upgrade_year = findViewById(R.id.txt_upgrade_year);
            txt_upgrade_month = findViewById(R.id.txt_upgrade_month);
            txt_viewall_benefits = findViewById(R.id.txt_viewall_benefits);

            rv_prime_book = findViewById(R.id.rv_prime_book);
        } catch (Exception e) {
            Log.e("init Exception ==>", "" + e);
        }
    }

    private void Prime_Item() {
        Utils.ProgressBarShow(PrimeMembership.this);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<BookModel> call = bookNPlayAPI.newarriaval("1");
        call.enqueue(new Callback<BookModel>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<BookModel> call, Response<BookModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        PrimeBookList = new ArrayList<>();
                        PrimeBookList = response.body().getResult();
                        Log.e("PrimeBookList", "" + PrimeBookList.size());

                        if (PrimeBookList.size() > 0) {
                            primeBookAdapter = new PrimeBookAdapter(PrimeMembership.this, PrimeBookList, "Activity");
                            rv_prime_book.setLayoutManager(new LinearLayoutManager(PrimeMembership.this,
                                    LinearLayoutManager.HORIZONTAL, false));
                            rv_prime_book.setAdapter(primeBookAdapter);
                            primeBookAdapter.notifyDataSetChanged();
                            rv_prime_book.setVisibility(View.VISIBLE);
                        } else {
                            rv_prime_book.setVisibility(View.GONE);
                        }

                    } else {
                        rv_prime_book.setVisibility(View.GONE);
                        Log.e("Prime_Item", "" + response.body().getMessage());
                    }
                } catch (Exception e) {
                    Log.e("Prime_Item Exception ==>", "" + e);
                }
                Utils.ProgressbarHide();
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<BookModel> call, Throwable t) {
                Log.e("Prime_Item onFailure ==>", "" + t.getMessage());
                Utils.ProgressbarHide();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.ProgressbarHide();
    }
}