package com.example.android.letsparty.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import androidx.viewpager.widget.ViewPager;

import com.example.android.letsparty.R;
import com.example.android.letsparty.adapter.TabAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MyEventsFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private PostedFragment postedFragment;
    private UpcomingFragment upcomingFragment;
    private SavedFragment savedFragment;
    private HistoryFragment historyFragment;

    private TabAdapter tabAdapter;

    private List<Fragment> fragments;
    private List<String> titles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_my_events, container, false);

        viewPager = (ViewPager) inflateView.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) inflateView.findViewById(R.id.tablayout);

        postedFragment = new PostedFragment();
        upcomingFragment = new UpcomingFragment();
        savedFragment = new SavedFragment();
        historyFragment = new HistoryFragment();

        fragments = new ArrayList<>();

        fragments.add(postedFragment);
        fragments.add(upcomingFragment);
        fragments.add(savedFragment);
        fragments.add(historyFragment);

        titles = new ArrayList<>();

        titles.add("Posted");
        titles.add("Upcoming");
        titles.add("Saved");
        titles.add("History");

        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        tabLayout.addTab(tabLayout.newTab().setText(titles.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(titles.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(titles.get(2)));
        tabLayout.addTab(tabLayout.newTab().setText(titles.get(3)));

        tabAdapter = new TabAdapter(getChildFragmentManager(), fragments, titles);

        viewPager.setAdapter(tabAdapter);

        tabLayout.setupWithViewPager(viewPager);

        return inflateView;
    }
}
