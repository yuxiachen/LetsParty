package com.example.android.letsparty.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.letsparty.R;
import com.example.android.letsparty.adapter.EventListAdapter;
import com.example.android.letsparty.model.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;


public class HistoryFragment extends Fragment implements EventListAdapter.OnEventItemClickedListener {
    private RecyclerView recyclerView;
    private EventListAdapter mAdapter;
    private ArrayList<Event> resultEvents;
    private ArrayList<String> eventKeys;
    private ArrayList<String> eventIDs_1;
    private ArrayList<String> eventIDs_2;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, null);

        recyclerView = view.findViewById(R.id.history_event_list);

        resultEvents = new ArrayList<>();
        eventKeys = new ArrayList<>();
        eventIDs_1 = new ArrayList<>();
        eventIDs_2 = new ArrayList<>();

        mAdapter = new EventListAdapter(resultEvents, eventKeys, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Find the User Posted Event ID
        DatabaseReference postedEventRef = FirebaseDatabase.getInstance().getReference(getString(R.string.db_posted_event)).child(userId);

        postedEventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    eventIDs_1.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        eventIDs_1.add(snapshot.getKey());
                    }

                    // Find the User Joined Event ID
                    DatabaseReference joinedEventRef = FirebaseDatabase.getInstance().getReference(getString(R.string.db_joined_event)).child(userId);

                    joinedEventRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                eventIDs_2.clear();

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    eventIDs_2.add(snapshot.getKey());
                                }

                                // Find the Event
                                DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference(getString(R.string.db_event));

                                eventRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            resultEvents.clear();
                                            eventKeys.clear();

                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                if (eventIDs_1.contains(snapshot.getKey()) || eventIDs_2.contains(snapshot.getKey())) {
                                                    Long time = (Long)snapshot.child("time").getValue();
                                                    if (new Date().getTime() > time) {
                                                        Event event = snapshot.getValue(Event.class);
                                                        String key = snapshot.getKey();
                                                        resultEvents.add(event);
                                                        eventKeys.add(key);
                                                    }
                                                }
                                            }

                                            mAdapter.notifyDataSetChanged();
                                        } else {
                                            Log.e(UpcomingFragment.class.getSimpleName(), "No data exists");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                Log.e(UpcomingFragment.class.getSimpleName(), "No relation exists");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Log.e(UpcomingFragment.class.getSimpleName(), "No relation exists");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
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
