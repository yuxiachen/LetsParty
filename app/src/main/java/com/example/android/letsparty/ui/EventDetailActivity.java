package com.example.android.letsparty.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.letsparty.R;
import com.example.android.letsparty.model.Event;
import com.example.android.letsparty.model.Notification;
import com.example.android.letsparty.model.User;
import com.example.android.letsparty.utils.Constants;
import com.google.android.gms.common.data.DataBufferSafeParcelable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class EventDetailActivity extends AppCompatActivity {

    private Event currEvent;
    private FirebaseDatabase db;
    private String userId;
    private String eventKey;
    private boolean isOrganizer;
    private boolean joinState;
    private boolean saveState;
    private boolean isFriendsOnly;
    private Button btn_join;
    private ToggleButton btn_save;
    private User user;
    private User currUser;
    private String organizer_name;
    private Set<String> friendList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        friendList = new HashSet<>();

        eventKey = getIntent().getStringExtra("key");
        db = FirebaseDatabase.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        btn_join = findViewById(R.id.btn_join);
        btn_save = findViewById(R.id.btn_save);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getUser(userId);
        fetchJoinState();
        fetchSaveState();
        fetchEvent();
    }

    private void fetchEvent() {
        db.getReference(getString(R.string.db_event)).child(eventKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currEvent = dataSnapshot.getValue(Event.class);

                    if (currEvent.getOrganizer().equals(userId)) {
                        isOrganizer = true;
                        setButtonText();
                    }

                    db.getReference(getString(R.string.db_friend)).child(currEvent.getOrganizer()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                    friendList.add(snapshot.getKey());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    db.getReference(getString(R.string.db_user) + "/" + currEvent.getOrganizer()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                user = dataSnapshot.getValue(User.class);

                                organizer_name = user.getUserName();
                            }

                            setupView(currEvent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
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

    private void fetchSaveState() {
        db.getReference(getString(R.string.db_saved_event) + "/" + userId).child(eventKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    saveState = true;
                } else {
                    saveState = false;
                }
                setToggleButtonState();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupView(Event event) {
        if (event == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.dateFormat));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(event.getTitle());

        ImageView iv_poster = findViewById(R.id.iv_poster);
        Picasso.get().load(event.getImgUrl()).fit().into(iv_poster);

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(event.getTitle());

        String time = getString(R.string.eventTime) + " " + sdf.format(event.getTime());
        TextView tv_time = findViewById(R.id.tv_time);
        tv_time.setText(time);

        if (new Date().getTime() > event.getTime()) {
            btn_join.setEnabled(false);
        }

        String category = getString(R.string.eventCategory) + " " + event.getCategory();
        TextView tv_category = findViewById(R.id.tv_category);
        tv_category.setText(category);

        String location = getString(R.string.eventLocation) + " " + event.getLocation().getAddressLine() + ", "
                + event.getLocation().getCity() + ", " + event.getLocation().getState() + " "
                + event.getLocation().getZipCode() + ", " + event.getLocation().getCountry();
        TextView tv_location = findViewById(R.id.tv_location);
        tv_location.setText(location);

        String minPeople = getString(R.string.eventPeople) + " " + event.getMinPeople();
        TextView tv_min_number = findViewById(R.id.tv_min_number);
        tv_min_number.setText(minPeople);

        String organizer = getString(R.string.eventOrganizer) + " " + organizer_name;
        TextView tv_publisher = findViewById(R.id.tv_organizer);
        tv_publisher.setText(organizer);

        String description = getString(R.string.eventDescription) + " " + event.getDescription();
        TextView tv_description = findViewById(R.id.tv_description);
        tv_description.setText(description);

        String friendsOnly = "";
        if (event.getFriendsOnly()) {
            friendsOnly += getString(R.string.eventFriendsOnly) + " " + getString(R.string.yes);
        }
        else {
            friendsOnly += getString(R.string.eventFriendsOnly) + " " + getString(R.string.no);
        }
        TextView tv_friendsOnly = findViewById(R.id.tv_friendsOnly);
        tv_friendsOnly.setText(friendsOnly);

        isFriendsOnly = event.getFriendsOnly();

        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOrganizer) {
                    cancelEvent();
                }
                else {
                    if (joinState) {
                        quitEvent();
                    } else {
                        if (isFriendsOnly && !friendList.contains(userId)) {
                            Toast.makeText(EventDetailActivity.this, "You have to be a Friend with the Organizer to Join in this Event", Toast.LENGTH_LONG).show();
                        }
                        else {
                            joinEvent();
                        }
                    }
                }
            }
        });

        setButtonText();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (saveState) {
                    unsaveEvent();
                }
                else {
                    saveEvent();
                }
            }
        });
    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void setButtonText() {
        if (isOrganizer) {
            btn_join.setText(getString(R.string.cancel));
        }
        else {
            if (joinState) {
                btn_join.setText(getString(R.string.quit));
            } else {
                btn_join.setText(getString(R.string.join));
            }
        }
    }

    public void setToggleButtonState() {
        if (saveState) {
            btn_save.setChecked(true);
        }
        else {
            btn_save.setChecked(false);
        }
    }

    private void getUser(String key) {
        Query query = FirebaseDatabase.getInstance().getReference("users").child(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currUser = dataSnapshot.getValue(User.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void cancelEvent() {
        isOrganizer = false;

        db.getReference(getString(R.string.db_saved_event)).orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        snapshot.getRef().child(eventKey).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        db.getReference(getString(R.string.db_joined_event)).orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        snapshot.getRef().child(eventKey).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        db.getReference(getString(R.string.db_joined_user)).child(eventKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        Event event = new Event(currEvent.getTitle(), currEvent.getImgUrl(), currEvent.getMinPeople(), currEvent.getTime(), currEvent.getFriendsOnly());
                        Notification notification = new Notification(Constants.EVENT_CANCEL_NOTIFICATION, event, eventKey, new Date().getTime());
                        FirebaseDatabase.getInstance().getReference("notifications").child(snapshot.getKey()).push().setValue(notification);

                        snapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // db.getReference(getString(R.string.db_joined_user)).child(eventKey).removeValue();
        db.getReference(getString(R.string.db_posted_event) + "/" + userId).child(eventKey).removeValue();
        db.getReference(getString(R.string.db_event)).child(eventKey).removeValue();

        finish();
    }

    private void quitEvent() {
        db.getReference(getString(R.string.db_joined_user) + "/" + eventKey).child(userId).removeValue();
        db.getReference(getString(R.string.db_joined_event) + "/" + userId).child(eventKey).removeValue();

        User sender = new User(currUser.getUserName(), currUser.getEmail(), currUser.getProfileImageUrl());
        Event event = new Event(currEvent.getTitle(), currEvent.getImgUrl(), currEvent.getMinPeople(), currEvent.getTime(), currEvent.getFriendsOnly());
        Notification notification = new Notification(Constants.EVENT_QUIT_NOTIFICATION, sender, userId, event, eventKey, new Date().getTime());
        FirebaseDatabase.getInstance().getReference("notifications").child(currEvent.getOrganizer()).push().setValue(notification);

        joinState = false;
        setButtonText();

        Toast.makeText(EventDetailActivity.this, "Quit Event Successfully", Toast.LENGTH_LONG).show();

        db.getReference(getString(R.string.db_event)).child(eventKey).child("currentPeople").setValue(currEvent.getCurrentPeople() - 1);

        currEvent.setCurrentPeople(currEvent.getCurrentPeople() - 1);

        if (currEvent.getCurrentPeople() == currEvent.getMinPeople() - 1) {
            Notification organizerNotification = new Notification(Constants.EVENT_PENDING_NOTIFICATION, event, eventKey, new Date().getTime());
            FirebaseDatabase.getInstance().getReference("notifications").child(currEvent.getOrganizer()).push().setValue(organizerNotification);

            db.getReference(getString(R.string.db_joined_user)).child(eventKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            Event event = new Event(currEvent.getTitle(), currEvent.getImgUrl(), currEvent.getMinPeople(), currEvent.getTime(), currEvent.getFriendsOnly());
                            Notification notification = new Notification(Constants.EVENT_PENDING_NOTIFICATION, event, eventKey, new Date().getTime());
                            FirebaseDatabase.getInstance().getReference("notifications").child(snapshot.getKey()).push().setValue(notification);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void joinEvent() {
        db.getReference(getString(R.string.db_joined_user) + "/" + eventKey).child(userId).setValue(true);
        db.getReference(getString(R.string.db_joined_event) + "/" + userId).child(eventKey).setValue(true);

        User sender = new User(currUser.getUserName(), currUser.getEmail(), currUser.getProfileImageUrl());
        Event event = new Event(currEvent.getTitle(), currEvent.getImgUrl(), currEvent.getMinPeople(), currEvent.getTime(), currEvent.getFriendsOnly());
        Notification notification = new Notification(Constants.EVENT_JOIN_NOTIFICATION, sender, userId, event, eventKey, new Date().getTime());
        FirebaseDatabase.getInstance().getReference("notifications").child(currEvent.getOrganizer()).push().setValue(notification);

        joinState = true;
        setButtonText();

        Toast.makeText(EventDetailActivity.this, "Join Event Successfully", Toast.LENGTH_LONG).show();

        db.getReference(getString(R.string.db_event)).child(eventKey).child("currentPeople").setValue(currEvent.getCurrentPeople() + 1);

        currEvent.setCurrentPeople(currEvent.getCurrentPeople() + 1);

        if (currEvent.getCurrentPeople() == currEvent.getMinPeople()) {
            Notification organizerNotification = new Notification(Constants.EVENT_SET_NOTIFICATION, event, eventKey, new Date().getTime());
            FirebaseDatabase.getInstance().getReference("notifications").child(currEvent.getOrganizer()).push().setValue(organizerNotification);

            db.getReference(getString(R.string.db_joined_user)).child(eventKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            Event event = new Event(currEvent.getTitle(), currEvent.getImgUrl(), currEvent.getMinPeople(), currEvent.getTime(), currEvent.getFriendsOnly());
                            Notification notification = new Notification(Constants.EVENT_SET_NOTIFICATION, event, eventKey, new Date().getTime());
                            FirebaseDatabase.getInstance().getReference("notifications").child(snapshot.getKey()).push().setValue(notification);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void saveEvent() {
        db.getReference(getString(R.string.db_saved_event) + "/" + userId).child(eventKey).setValue(true);

        saveState = true;
        setToggleButtonState();

        Toast.makeText(EventDetailActivity.this, "Save Event Successfully", Toast.LENGTH_LONG).show();
    }

    private void unsaveEvent() {
        db.getReference(getString(R.string.db_saved_event) + "/" + userId).child(eventKey).removeValue();

        saveState = false;
        setToggleButtonState();

        Toast.makeText(EventDetailActivity.this, "Unsave Event Successfully", Toast.LENGTH_LONG).show();
    }
}
