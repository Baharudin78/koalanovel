package com.divinetechs.ebooksapp.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.divinetechs.ebooksapp.Fragment.DownloadBooks;
import com.divinetechs.ebooksapp.Fragment.DownloadMagazines;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.PrefManager;
import com.divinetechs.ebooksapp.Utility.Utils;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

public class MyDownloadedItems extends AppCompatActivity {

    PrefManager prefManager;

    SmartTabLayout tab_layout;
    ViewPager tab_viewpager;

    TextView txtToolbarTitle;
    LinearLayout lyToolbar, lyBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.screenCapOff(MyDownloadedItems.this);
        if (PrefManager.getInstance(this).isNightModeEnabled() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_mydownloadeditems);
        PrefManager.forceRTLIfSupported(getWindow(), MyDownloadedItems.this);

        init();

        txtToolbarTitle.setText("" + getResources().getString(R.string.my_downloaded_book));
        lyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void init() {
        try {
            prefManager = new PrefManager(MyDownloadedItems.this);

            lyToolbar = findViewById(R.id.lyToolbar);
            lyBack = findViewById(R.id.lyBack);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);

            tab_layout = findViewById(R.id.tab_layout);
            tab_viewpager = findViewById(R.id.tab_viewpager);

            setupViewPager(tab_viewpager);
            tab_layout.setViewPager(tab_viewpager);
            tab_viewpager.setOffscreenPageLimit(1);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    //Tab With ViewPager
    private void setupViewPager(ViewPager viewPager) {
        DownloadPagerAdapter adapter = new DownloadPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DownloadBooks(), "" + getResources().getString(R.string.books));
        adapter.addFragment(new DownloadMagazines(), "" + getResources().getString(R.string.magazines));
        viewPager.setAdapter(adapter);
    }

    static class DownloadPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public DownloadPagerAdapter(FragmentManager manager) {
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

}
