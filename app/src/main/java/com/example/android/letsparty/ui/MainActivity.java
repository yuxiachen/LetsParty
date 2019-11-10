package com.example.android.letsparty.ui;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;

import com.example.android.letsparty.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                switch (menuItem.getItemId()) {
                    case R.id.navigation_trending:
                        if (fm.findFragmentById(R.id.main_frame) != null) {
                            ft.remove(fm.findFragmentById(R.id.main_frame));

                            ft.commit();
                        }
                        return true;
                    case R.id.navigation_joined:
                        ft.replace(R.id.main_frame, new MyEventsFragment());
                        ft.commit();
                        return true;
                    case R.id.navigation_create:
                        if (fm.findFragmentById(R.id.main_frame) != null) {
                            ft.remove(fm.findFragmentById(R.id.main_frame));

                            ft.commit();
                        }
                        return true;
                    case R.id.navigation_settings:
                        if (fm.findFragmentById(R.id.main_frame) != null) {
                            ft.remove(fm.findFragmentById(R.id.main_frame));

                            ft.commit();
                        }
                        return true;
                }

                return false;
            }
        });
    }



}
