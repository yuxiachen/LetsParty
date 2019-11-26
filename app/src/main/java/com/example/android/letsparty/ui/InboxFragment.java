package com.example.android.letsparty.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.letsparty.R;
import com.example.android.letsparty.adapter.NotificationAdapter;
import com.example.android.letsparty.model.Notification;
import com.example.android.letsparty.model.User;
import com.example.android.letsparty.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class InboxFragment extends Fragment implements NotificationAdapter.OnNotificationItemClickedListener {

    private ArrayList<Notification> notifications;
    private ArrayList<String> notificationKeys;
    private RecyclerView recyclerView;
    private TextView emptyResult;
    private NotificationAdapter adapter;
    private String currUserKey;
    private User currUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, null);
        recyclerView = view.findViewById(R.id.rv_list);
        emptyResult = view.findViewById(R.id.tv_no_result);
        notifications = new ArrayList<>();
        notificationKeys = new ArrayList<>();
        SharedPreferences myPrefs = getActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        currUserKey = myPrefs.getString("currUserKey", "");
        currUser = new Gson().fromJson(myPrefs.getString("currUser", ""), User.class);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        adapter = new NotificationAdapter(notifications, notificationKeys, this);
        recyclerView.setAdapter(adapter);
        showEmptyResult();
        getNotifications();
        return view;
    }

    private void getNotifications(){
        Query query = FirebaseDatabase.getInstance()
                .getReference("notifications")
                .child(FirebaseAuth.getInstance().getUid()).orderByChild("time");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    notifications.clear();
                    notificationKeys.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        Notification notification = snapshot.getValue(Notification.class);
                        notifications.add(notification);
                        notificationKeys.add(key);
                    }
                    adapter.notifyDataSetChanged();
                    showListResult();
                } else {
                    showEmptyResult();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showEmptyResult(){
        emptyResult.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void showListResult(){
        emptyResult.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showFriendRequestDialog(final Notification notification, final String notificationKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.image_dialog_layout, null);
        TextView title = view.findViewById(R.id.tv_title);
        ImageView image = view.findViewById(R.id.iv_photo);
        title.setText(Constants.FRIEND_REQUEST_NOTIFICATION);
        if (notification.getSender().getProfileImageUrl() != null) {
            Picasso.get().load(notification.getSender().getProfileImageUrl()).into(image);
        } else {
            image.setVisibility(View.GONE);
        }
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseDatabase.getInstance().getReference("friends/" + currUserKey).child(notification.getSenderKey()).setValue(notification.getSender());
                FirebaseDatabase.getInstance().getReference("friends/" + notification.getSenderKey()).child(currUserKey).setValue(new User(currUser.getUserName(), currUser.getEmail(), currUser.getProfileImageUrl()));
                deleteNotification(notificationKey);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteNotification(notificationKey);
            }
        });
        builder.setView(view);
        builder.show();
    }

    private void showFriendRequestAcceptedDialog(Notification notification, String notificationKey) {

    }

    private void deleteNotification(String notificationKey) {
        FirebaseDatabase.getInstance().getReference("notifications/" + currUserKey).child(notificationKey).removeValue();
    }

    @Override
    public void onNotificationItemClicked(Notification notification, String notificationKey) {
        String type = notification.getNotificationType();
        switch (type) {
            case Constants.FRIEND_REQUEST_NOTIFICATION:
                showFriendRequestDialog(notification, notificationKey);
                break;
            case Constants.FRIEND_REQUEST_ACCEPTED_NOTIFICATION:
                showFriendRequestAcceptedDialog(notification, notificationKey);
                break;
            case Constants.EVENT_INVITATION_NOTIFICATION:
                // todo:
                // openEventDetailActivity(notification.getEvent());
                break;
            default:
                break;
        }
    }
}
