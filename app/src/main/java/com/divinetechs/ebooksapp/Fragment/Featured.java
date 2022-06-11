package com.divinetechs.ebooksapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.divinetechs.ebooksapp.Activity.MainActivity;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.PrefManager;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

public class Featured extends Fragment {

    private View root;

    SmartTabLayout tab_layout;
    ViewPager tab_viewpager;

    public Featured() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (PrefManager.getInstance(getActivity()).isNightModeEnabled() == true) {
            getActivity().setTheme(R.style.darktheme);
        } else {
            getActivity().setTheme(R.style.AppTheme);
        }
        root = inflater.inflate(R.layout.featuredfragment, container, false);
        PrefManager.forceRTLIfSupported(getActivity().getWindow(), getActivity());

        MainActivity.appbar.setVisibility(View.VISIBLE);
        tab_layout = root.findViewById(R.id.tab_layout);
        tab_viewpager = root.findViewById(R.id.tab_viewpager);

        setupViewPager(tab_viewpager);
        tab_layout.setViewPager(tab_viewpager);
        tab_viewpager.setOffscreenPageLimit(2);

        return root;
    }

    //Tab With ViewPager
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new com.divinetechs.ebooksapp.Fragment.Home(), "" + getResources().getString(R.string.title_home));
        adapter.addFragment(new com.divinetechs.ebooksapp.Fragment.Genres(), "" + getResources().getString(R.string.title_genres));
        adapter.addFragment(new com.divinetechs.ebooksapp.Fragment.Magazines(), "" + getResources().getString(R.string.title_magazine));
        viewPager.setAdapter(adapter);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
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
