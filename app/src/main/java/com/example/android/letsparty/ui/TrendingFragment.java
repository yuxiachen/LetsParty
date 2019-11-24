package com.example.android.letsparty.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.letsparty.R;
import com.example.android.letsparty.adapter.EventListAdapter;
import com.example.android.letsparty.model.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class TrendingFragment extends Fragment implements EventListAdapter.OnEventItemClickedListener{

    private RecyclerView recyclerView;
    private EventListAdapter mAdapter;
    private ArrayList<Event> resultEvents;
    private ArrayList<String> eventKeys;
    private SearchView searchInTrending;
    private ConstraintLayout frame;
    private TextView emptyResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending, null);

        frame = view.findViewById(R.id.fragment_trending);
        frame.requestFocus();
        searchInTrending = view.findViewById(R.id.search_in_trending);
        emptyResult = view.findViewById(R.id.tv_no_result);

        recyclerView = view.findViewById(R.id.trending_event_list);
        resultEvents = new ArrayList<>();
        eventKeys = new ArrayList<>();
        mAdapter = new EventListAdapter(resultEvents, eventKeys, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);
        showTrending();
        searchInTrending.setIconifiedByDefault(false);
        searchInTrending.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Query queryResult = FirebaseDatabase.getInstance().getReference("events")
                        .orderByChild("category").startAt(query).endAt(query + "\uf8ff");
                queryResult.addListenerForSingleValueEvent(resultListener);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    showTrending();
                    emptyResult.setVisibility(View.GONE);
                }
                return false;
            }
        });

        return view;

    }

    private ValueEventListener resultListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            resultEvents.clear();
            eventKeys.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);
                    String key = snapshot.getKey();
                    resultEvents.add(event);
                    eventKeys.add(key);
                }
                mAdapter.notifyDataSetChanged();
            } else {
                emptyResult.setVisibility(View.VISIBLE);
                Log.e(TrendingFragment.class.getSimpleName(), "No data exists");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private void showTrending(){
        long currTime = new Date().getTime();
        Query eventQuery = FirebaseDatabase.getInstance().getReference(getString(R.string.db_event)).orderByChild("time").startAt(currTime);
        eventQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                resultEvents.clear();
                eventKeys.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Event event = snapshot.getValue(Event.class);
                        String key = snapshot.getKey();

                        resultEvents.add(event);
                        eventKeys.add(key);
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TrendingFragment.class.getSimpleName(), "No data exists");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }


    private void openEventDetailActivity(String key){
        Intent intent = new Intent(getActivity(), EventDetailActivity.class);
        intent.putExtra("key", key);
        startActivity(intent);
    }

    @Override
    public void OnEventItemClicked(String key){
        openEventDetailActivity(key);
    }
}
