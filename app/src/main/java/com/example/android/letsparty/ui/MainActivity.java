package com.example.android.letsparty.ui;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;

import android.os.PersistableBundle;

import androidx.fragment.app.Fragment;

import com.example.android.letsparty.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private Fragment selectedFragment;
    private Fragment trendingFragment;
    private Fragment inboxFragment;
    private Fragment meFragment;
    private Fragment myEventsFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        fragmentManager = getSupportFragmentManager();
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        if (savedInstanceState == null) {
            if (selectedFragment == null) {
                trendingFragment = new TrendingFragment();
                selectedFragment = trendingFragment;
                fragmentManager.beginTransaction().add(R.id.main_frame, selectedFragment).commit();
            }
        } else {
            selectedFragment = getSupportFragmentManager().getFragment(savedInstanceState, "SelectFragment");
            trendingFragment = getSupportFragmentManager().getFragment(savedInstanceState, "TrendingFragment");
            inboxFragment = getSupportFragmentManager().getFragment(savedInstanceState, "InboxFragment");
            meFragment = getSupportFragmentManager().getFragment(savedInstanceState, "MeFragment");
            myEventsFragment = getSupportFragmentManager().getFragment(savedInstanceState, "MyEventsFragment");
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_trending:
                    if (trendingFragment == null) {
                        trendingFragment = new TrendingFragment();
                    }
                    selectedFragment = trendingFragment;
                    break;
                case R.id.navigation_joined:
                    if (myEventsFragment == null) {
                        myEventsFragment = new MyEventsFragment();
                    }
                    selectedFragment = myEventsFragment;
                    break;
                case R.id.navigation_create:
                    if (inboxFragment == null) {
                        inboxFragment = new InboxFragment();
                    }
                    selectedFragment = inboxFragment;
                    break;
                case R.id.navigation_settings:
                    if (meFragment == null) {
                        meFragment = new MeFragment();
                    }
                    selectedFragment = meFragment;
                    break;
            }
            replaceFragment(selectedFragment);
            return true;
        }
    };

    public void replaceFragment(Fragment selectedFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, selectedFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (selectedFragment != null && selectedFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "SelectFragment", selectedFragment);
        }

        if (trendingFragment != null) {
            getSupportFragmentManager().putFragment(outState, "TrendingFragment", trendingFragment);
        }
        if (meFragment != null) {
            getSupportFragmentManager().putFragment(outState, "MeFragment", meFragment);
        }
        if (myEventsFragment != null) {
            getSupportFragmentManager().putFragment(outState, "MyEventsFragment", myEventsFragment);
        }
        if (inboxFragment != null) {
            getSupportFragmentManager().putFragment(outState, "InboxFragment", inboxFragment);
        }

    }

}
