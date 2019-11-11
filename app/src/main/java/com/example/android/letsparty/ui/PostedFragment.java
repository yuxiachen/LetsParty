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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostedFragment extends Fragment implements EventListAdapter.OnEventItemClickedListener {
    private RecyclerView recyclerView;
    private EventListAdapter mAdapter;
    private ArrayList<Event> resultEvents;
    private ArrayList<String> eventKeys;
    private ArrayList<String> eventIDs;
    private Map<String, Object> map;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending, null);
        recyclerView = view.findViewById(R.id.trending_event_list);
        resultEvents = new ArrayList<>();
        eventKeys = new ArrayList<>();
        eventIDs = new ArrayList<>();
        map = new HashMap<>();
        final Date date = new Date();
        mAdapter = new EventListAdapter(resultEvents, eventKeys, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference documentReference = db.collection("user_published_events").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    eventIDs.clear();
                    map.clear();
                    if (task.getResult().getData() != null) {
                        Map<String, Object> map = task.getResult().getData();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            eventIDs.add(entry.getKey());
                            Log.d("TAG", entry.getKey());
                        }
                    }
                }
            }
        });

        db.collection("events").orderBy("time").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    resultEvents.clear();
                    eventKeys.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (eventIDs.contains(document.getId())) {
                            if (document.getTimestamp("time").compareTo(new Timestamp(date)) > 0) {
                                Event event = document.toObject(Event.class);
                                String key = document.getId();
                                resultEvents.add(event);
                                eventKeys.add(key);
                            }
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    Log.e(PostedFragment.class.getSimpleName(), "task failed" + task.getResult());
                }
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
