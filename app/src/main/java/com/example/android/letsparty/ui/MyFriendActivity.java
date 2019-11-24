package com.example.android.letsparty.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.letsparty.R;
import com.example.android.letsparty.adapter.FriendListAdapter;
import com.example.android.letsparty.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyFriendActivity extends AppCompatActivity implements FriendListAdapter.onFriendItemClickedListener {
    private ArrayList<User> friends;
    private ArrayList<String> friendKeys;
    private FriendListAdapter adapter;
    private TextView emptyResult;
    private LinearLayout llAddNewFriend;
    private LinearLayout llUserList;
    private String searchText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        initSearchView();
        initFriendListView();
        initAddNewFriendView();
    }

    private void initSearchView(){
        SearchView search = findViewById(R.id.sv_search);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String query = newText.trim().toLowerCase();
                if (query.length() != 0) {
                    adapter.filter(query);
                    if (adapter.getItemCount() == 0) {
                        searchText = query;
                        showSearchNewFriend();
                    }
                } else {
                    getMyFriends();
                }
                return false;
            }
        });
    }

    private void initFriendListView() {
        llUserList = findViewById(R.id.ll_user_list);
        RecyclerView rvFriends = findViewById(R.id.rv_list);
        emptyResult = findViewById(R.id.tv_no_result);
        friendKeys = new ArrayList<>();
        friends = new ArrayList<>();
        adapter = new FriendListAdapter(friends, friendKeys, this);
        rvFriends.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvFriends.setAdapter(adapter);
        getMyFriends();
    }

    private void initAddNewFriendView(){
        llAddNewFriend = findViewById(R.id.ll_add_new_friend);
        llAddNewFriend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                searchNewFriend();
            }
        });
    }

    private void getMyFriends(){
        Query query = FirebaseDatabase.getInstance()
                .getReference("friends/" + FirebaseAuth.getInstance().getUid())
                .orderByChild("username");
        query.addValueEventListener(resultListener);
    }

    private void searchNewFriend(){
        Query query = FirebaseDatabase.getInstance().getReference("users")
                .orderByChild("username")
                .startAt(searchText).endAt(searchText + "\uf8ff");
        query.addValueEventListener(resultListener);
        showFriendList();
    }

    private void showSearchNewFriend() {
        llAddNewFriend.setVisibility(View.VISIBLE);
        llUserList.setVisibility(View.GONE);
    }

    private void showFriendList(){
        llAddNewFriend.setVisibility(View.GONE);
        llUserList.setVisibility(View.VISIBLE);
    }

    private ValueEventListener resultListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            friends.clear();
            friendKeys.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User friend = snapshot.getValue(User.class);
                    String key = snapshot.getKey();
                    friends.add(friend);
                    friendKeys.add(key);
                }
                adapter.notifyDataSetChanged();
            } else {
                emptyResult.setVisibility(View.VISIBLE);
                Log.e(MyFriendActivity.class.getSimpleName(), "No data exists");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void openFriendProfileActivity(String key) {
        Intent intent = new Intent(this, FriendProfileActivity.class);
        intent.putExtra("userKey", key);
        startActivity(intent);
    }

    @Override
    public void onFriendItemClicked(String userKey) {
        openFriendProfileActivity(userKey);
    }
}
