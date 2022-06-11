package com.divinetechs.ebooksapp.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.divinetechs.ebooksapp.Fragment.DownloadBooks;
import com.divinetechs.ebooksapp.Fragment.DownloadMagazines;
import com.divinetechs.ebooksapp.Fragment.SearchBooks;
import com.divinetechs.ebooksapp.Fragment.SearchMagazines;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.Constant;
import com.divinetechs.ebooksapp.Utility.PrefManager;
import com.divinetechs.ebooksapp.Utility.Utils;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    PrefManager prefManager;

    SmartTabLayout tab_layout;
    ViewPager tab_viewpager;
    EditText searchView;
    ImageButton IB_clear, IB_back;

    String str_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.screenCapOff(SearchActivity.this);
        if (PrefManager.getInstance(this).isNightModeEnabled() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.searchactivity);

        prefManager = new PrefManager(SearchActivity.this);

        IB_clear = findViewById(R.id.buttonClear);
        IB_back = findViewById(R.id.buttonBack);
        IB_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        IB_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setText("");
            }
        });

        searchView = findViewById(R.id.searchEditText);
        tab_layout = findViewById(R.id.tab_layout);
        tab_viewpager = findViewById(R.id.tab_viewpager);

        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    str_search = "" + searchView.getText().toString();
                    Log.e("str_search", "" + str_search);
                    Constant.strSearch = str_search;

                    setupViewPager(tab_viewpager);
                    tab_layout.setViewPager(tab_viewpager);
                    tab_viewpager.setOffscreenPageLimit(1);
                    return true;
                }
                return false;
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            str_search = bundle.getString("search");
            Log.e("str_search", "" + str_search);
            searchView.setText(str_search);
            Constant.strSearch = str_search;

            setupViewPager(tab_viewpager);
            tab_layout.setViewPager(tab_viewpager);
            tab_viewpager.setOffscreenPageLimit(1);
        }

    }

    //Tab With ViewPager
    private void setupViewPager(ViewPager viewPager) {
        SearchPagerAdapter adapter = new SearchPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SearchBooks(), "" + getResources().getString(R.string.books));
        adapter.addFragment(new SearchMagazines(), "" + getResources().getString(R.string.magazines));
        viewPager.setAdapter(adapter);
    }

    static class SearchPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SearchPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
//        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_search:
                // search action
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
