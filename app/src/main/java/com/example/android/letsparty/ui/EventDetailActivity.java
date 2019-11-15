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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

public class EventDetailActivity extends AppCompatActivity {

    private Event currEvent;
    private FirebaseDatabase db;
    private String userId;
    private String eventKey;
    private boolean joinState;
    private Button btn_join;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        eventKey = getIntent().getStringExtra("key");
        db = FirebaseDatabase.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        btn_join = findViewById(R.id.btn_join);
        fetchJoinState();
        fetchEvent();
    }

    private void fetchEvent() {
        db.getReference(getString(R.string.db_event)).child(eventKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currEvent = dataSnapshot.getValue(Event.class);
                    setupView(currEvent);
                } else {
                    Log.e(EventDetailActivity.class.getSimpleName(), "data not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchJoinState() {
        db.getReference(getString(R.string.db_joined_user) + "/" + eventKey).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    joinState = true;
                } else {
                    joinState = false;
                }
                setButtonText();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupView(Event event) {
        if (event == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(event.getTitle());
        ImageView iv_poster = findViewById(R.id.iv_poster);
        Picasso.get().load(event.getImgUrl()).fit().into(iv_poster);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(event.getTitle());
        TextView tv_time = findViewById(R.id.tv_time);
        tv_time.setText(sdf.format(event.getTime()));
        TextView tv_category = findViewById(R.id.tv_category);
        tv_category.setText(event.getCategory());
        TextView tv_location = findViewById(R.id.tv_location);
        tv_location.setText(event.getLocation().getAddressLine());
        TextView tv_min_number = findViewById(R.id.tv_min_number);
        tv_min_number.setText(Integer.toString(event.getMinPeople()));
        TextView tv_publisher = findViewById(R.id.tv_organizer);
        tv_publisher.setText(event.getOrganizer());
        TextView tv_description = findViewById(R.id.tv_description);
        tv_description.setText(event.getDescription());

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
        db.getReference(getString(R.string.db_joined_user) + "/" + eventKey).child(userId).removeValue();
        db.getReference(getString(R.string.db_joined_event) + "/" + userId).child(eventKey).removeValue();
        joinState = false;
        setButtonText();
    }

    private void joinEvent() {
        db.getReference(getString(R.string.db_joined_user) + "/" + eventKey).child(userId).setValue(userId);
        db.getReference(getString(R.string.db_joined_event) + "/" + userId).child(eventKey).setValue(eventKey);
        joinState = true;
        setButtonText();
    }
}
