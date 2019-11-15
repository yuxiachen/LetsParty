package com.example.android.letsparty.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_events, null);

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.tablayout);

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

        return view;
    }
}
