package com.example.android.letsparty.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.letsparty.R;
import com.example.android.letsparty.adapter.CircleTransform;
import com.example.android.letsparty.model.Notification;
import com.example.android.letsparty.model.User;
import com.example.android.letsparty.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.google.gson.Gson;

import java.util.Date;

public class FriendProfileActivity extends AppCompatActivity {
    private User user;
    private boolean isFriend;
    private String currUserKey;
    private User currUser;
    private String userKey;
    private Button btnAddOrDelete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        btnAddOrDelete = findViewById(R.id.btn_add_or_delete_friend);
        userKey = getIntent().getStringExtra("userKey");
        String userName = getIntent().getStringExtra("userName");
        isFriend = false;
        SharedPreferences myPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        currUserKey = myPrefs.getString("currUserKey", "");
        currUser = new Gson().fromJson(myPrefs.getString("currUser", ""), User.class);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(userName);
        getUser(userKey);
        getFriendStatus(userKey);
    }

    private void getUser(String key) {
        Query query = FirebaseDatabase.getInstance().getReference("users").child(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user = dataSnapshot.getValue(User.class);
                    initView(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getFriendStatus(String key) {
        Query query = FirebaseDatabase.getInstance().getReference("friends/" + currUserKey).child(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    isFriend = true;
                } else {
                    isFriend = false;
                }
                setButtonText();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initView(User user){
        ImageView ivPhoto = findViewById(R.id.iv_profile_img);
        TextView tvUsername = findViewById(R.id.username_item_user);
        TextView tvEmail = findViewById(R.id.tv_profile_email);
        TextView tvRegion = findViewById(R.id.tv_region);
        TextView tvInterest = findViewById(R.id.tv_interest);

        tvEmail.setText(user.getEmail());

        tvUsername.setText(user.getUserName());

        if (user.getLocation() != null) {
            tvRegion.setText(user.getLocation().toString());
        }

        if(user.getInterest() != null) {
            tvInterest.setText(user.getInterest());
        }

        if (user.getProfileImageUrl() != null) {
            Picasso.get().load(user.getProfileImageUrl())
                    .transform(new CircleTransform())
                    .into(ivPhoto);
        }

        btnAddOrDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFriend) {
                    deleteFriend();
                } else {
                    addFriend();
                }
            }
        });
    }

    private void setButtonText() {
        if(isFriend) {
            btnAddOrDelete.setText(getString(R.string.mDeleteFriend));
        } else {
            btnAddOrDelete.setText(getString(R.string.mAddFriend));
        }
    }

    private void addFriend() {
        User sender = new User(currUser.getUserName(), currUser.getEmail(), currUser.getProfileImageUrl());
        Notification notification = new Notification(Constants.FRIEND_REQUEST_NOTIFICATION, sender, currUserKey, new Date().getTime());
        FirebaseDatabase.getInstance().getReference("notifications").child(userKey).push().setValue(notification);
    }

    private void deleteFriend() {
        FirebaseDatabase.getInstance().getReference("friends/" + currUserKey).child(userKey).removeValue();
        FirebaseDatabase.getInstance().getReference("friends/" + userKey).child(currUserKey).removeValue();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
