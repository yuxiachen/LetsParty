package com.example.android.letsparty.ui;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.letsparty.R;
import com.example.android.letsparty.model.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EventDetailActivity extends AppCompatActivity {

    private Event currEvent;
    private FirebaseFirestore db;
    private String userId;
    private String eventKey;
    private boolean joinState;
    private Button btn_join;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        eventKey = getIntent().getStringExtra("key");
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fetchJoinState();
        fetchEvent();
    }

    private void fetchEvent() {
        db.collection("events").document(eventKey).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            currEvent = documentSnapshot.toObject(Event.class);
                            setupView(currEvent);
                        } else {
                            Log.e(EventDetailActivity.class.getSimpleName(), "data not exist" + documentSnapshot.toString());
                        }
                    }
                });
    }

    private void fetchJoinState() {
        db.collection("users/" + userId + "/joinedEvents").document(eventKey).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {
                            joinState = true;
                        } else {
                            joinState = false;
                        }
                    }
                });
    }

    private void setupView(Event event) {
        if (event == null) return;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(event.getTitle());
        ImageView iv_poster = findViewById(R.id.iv_poster);
        Picasso.get().load(event.getImgUrl()).fit().into(iv_poster);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(event.getTitle());
        TextView tv_time = findViewById(R.id.tv_time);
        tv_time.setText(event.getTime().toDate().toString());
        TextView tv_category = findViewById(R.id.tv_category);
        tv_category.setText(event.getCategory());
        TextView tv_location = findViewById(R.id.tv_location);
        tv_location.setText(event.getLocation().getAddressLine());
        TextView tv_min_number = findViewById(R.id.tv_min_number);
        TextView tv_publisher = findViewById(R.id.tv_organizer);
        tv_min_number.setText(Integer.toString(event.getMinPeople()));
        tv_publisher.setText(event.getOrganizer());
        TextView tv_description = findViewById(R.id.tv_description);
        tv_description.setText(event.getDescription());
        btn_join = findViewById(R.id.btn_join);
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (joinState) {
                    quitEvent();
                } else {
                    joinEvent();
                }
            }
        });
        setButtonText();
    }

    public void setButtonText() {
        if (joinState) {
            btn_join.setText(getString(R.string.quit));
        } else {
            btn_join.setText(getString(R.string.join));
        }
    }

    private void quitEvent() {
        db.collection("events/" + eventKey + "/joinedUsers").document(userId).delete();
        db.collection("users/" + userId + "/joinedEvents").document(eventKey).delete();
        joinState = false;
        setButtonText();
    }

    private void joinEvent() {
        Map<String, Object> data = new HashMap<>();
        data.put(userId, db.document("users/" + userId));
        db.collection("events/" + eventKey + "/joinedUsers").document(userId).set(data);
        data.clear();
        data.put(eventKey, db.document("users/" + eventKey));
        db.collection("users/" + userId + "/joinedEvents").document(eventKey).set(data);
        joinState = true;
        setButtonText();
    }
}
