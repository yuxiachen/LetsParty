package com.example.android.letsparty.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.android.letsparty.adapter.CircleTransform;
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
import java.util.Date;

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

        emptyResult.setText(getString(R.string.noMessage));

        showEmptyResult();
        getNotifications();

        return view;
    }

    private void getNotifications(){
        Query query = FirebaseDatabase.getInstance()
                .getReference("notifications")
                .child(FirebaseAuth.getInstance().getUid()).orderByChild("time");
        query.addValueEventListener(new ValueEventListener() {
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
        TextView message = view.findViewById(R.id.tv_message);
        title.setText(Constants.FRIEND_REQUEST_NOTIFICATION);

        message.setText(notification.getSender().getUserName() + getString(R.string.friend_request));

        if (notification.getSender().getProfileImageUrl() != null) {
            Picasso.get().load(notification.getSender().getProfileImageUrl()).transform(new CircleTransform()).into(image);
        } else {
            image.setVisibility(View.GONE);
        }

        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // add friend
                User temp = new User(currUser.getUserName(), currUser.getEmail(), currUser.getProfileImageUrl());
                FirebaseDatabase.getInstance().getReference("friends/" + currUserKey).child(notification.getSenderKey()).setValue(notification.getSender());
                FirebaseDatabase.getInstance().getReference("friends/" + notification.getSenderKey()).child(currUserKey).setValue(temp);
                // send friend request accepted notification to sender
                Notification acceptedNotification = new Notification(Constants.FRIEND_REQUEST_ACCEPTED_NOTIFICATION, temp, currUserKey, new Date().getTime());
                FirebaseDatabase.getInstance().getReference("notifications").child(notification.getSenderKey()).push().setValue(acceptedNotification);
                deleteNotification(notificationKey);
            }
        });

        builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteNotification(notificationKey);
            }
        });

        builder.setView(view);
        builder.show();
    }

    private void showFriendRequestAcceptedDialog(Notification notification, final String notificationKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.image_dialog_layout, null);

        TextView title = view.findViewById(R.id.tv_title);
        ImageView image = view.findViewById(R.id.iv_photo);
        title.setText(Constants.FRIEND_REQUEST_ACCEPTED_NOTIFICATION);
        TextView message = view.findViewById(R.id.tv_message);

        message.setText(notification.getSender().getUserName() + getString(R.string.friend_request_accepted));

        if (notification.getSender().getProfileImageUrl() != null) {
            Picasso.get().load(notification.getSender().getProfileImageUrl()).transform(new CircleTransform()).into(image);
        } else {
            image.setVisibility(View.GONE);
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNotification(notificationKey);
            }
        });

        builder.setView(view);
        builder.show();
    }

    private void showEventQuitDialog(Notification notification, final String notificationKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.image_dialog_layout, null);

        TextView title = view.findViewById(R.id.tv_title);
        ImageView image = view.findViewById(R.id.iv_photo);
        title.setText(Constants.EVENT_QUIT_NOTIFICATION);
        TextView message = view.findViewById(R.id.tv_message);

        message.setText(notification.getSender().getUserName() + getString(R.string.event_quit_1) + notification.getEvent().getTitle() + getString(R.string.event_quit_2));

        if (notification.getEvent().getImgUrl() != null) {
            Picasso.get().load(notification.getEvent().getImgUrl()).transform(new CircleTransform()).into(image);
        } else {
            image.setVisibility(View.GONE);
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNotification(notificationKey);
            }
        });

        builder.setView(view);
        builder.show();
    }

    private void showEventJoinDialog(Notification notification, final String notificationKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.image_dialog_layout, null);

        TextView title = view.findViewById(R.id.tv_title);
        ImageView image = view.findViewById(R.id.iv_photo);
        title.setText(Constants.EVENT_JOIN_NOTIFICATION);
        TextView message = view.findViewById(R.id.tv_message);

        message.setText(notification.getSender().getUserName() + getString(R.string.event_join_1) + notification.getEvent().getTitle() + getString(R.string.event_join_2));

        if (notification.getEvent().getImgUrl() != null) {
            Picasso.get().load(notification.getEvent().getImgUrl()).transform(new CircleTransform()).into(image);
        } else {
            image.setVisibility(View.GONE);
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNotification(notificationKey);
            }
        });

        builder.setView(view);
        builder.show();
    }

    private void showEventCancelDialog(Notification notification, final String notificationKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.image_dialog_layout, null);

        TextView title = view.findViewById(R.id.tv_title);
        ImageView image = view.findViewById(R.id.iv_photo);
        title.setText(Constants.EVENT_CANCEL_NOTIFICATION);
        TextView message = view.findViewById(R.id.tv_message);

        message.setText(notification.getEvent().getTitle() + getString(R.string.event_cancel));

        if (notification.getEvent().getImgUrl() != null) {
            Picasso.get().load(notification.getEvent().getImgUrl()).transform(new CircleTransform()).into(image);
        } else {
            image.setVisibility(View.GONE);
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNotification(notificationKey);
            }
        });

        builder.setView(view);
        builder.show();
    }

    private void showEventSetDialog(Notification notification, final String notificationKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.image_dialog_layout, null);

        TextView title = view.findViewById(R.id.tv_title);
        ImageView image = view.findViewById(R.id.iv_photo);
        title.setText(Constants.EVENT_SET_NOTIFICATION);
        TextView message = view.findViewById(R.id.tv_message);

        message.setText(notification.getEvent().getTitle() + getString(R.string.event_set));

        if (notification.getEvent().getImgUrl() != null) {
            Picasso.get().load(notification.getEvent().getImgUrl()).transform(new CircleTransform()).into(image);
        } else {
            image.setVisibility(View.GONE);
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNotification(notificationKey);
            }
        });

        builder.setView(view);
        builder.show();
    }

    private void showEventPendingDialog(Notification notification, final String notificationKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.image_dialog_layout, null);

        TextView title = view.findViewById(R.id.tv_title);
        ImageView image = view.findViewById(R.id.iv_photo);
        title.setText(Constants.EVENT_PENDING_NOTIFICATION);
        TextView message = view.findViewById(R.id.tv_message);

        message.setText(notification.getEvent().getTitle() + getString(R.string.event_pending));

        if (notification.getEvent().getImgUrl() != null) {
            Picasso.get().load(notification.getEvent().getImgUrl()).transform(new CircleTransform()).into(image);
        } else {
            image.setVisibility(View.GONE);
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNotification(notificationKey);
            }
        });

        builder.setView(view);
        builder.show();
    }
    private void openEventDetailActivity(Notification notification, final String notificationKey){
        String eventKey = notification.getEventKey();
        Intent intent = new Intent(getActivity(), EventDetailActivity.class);
        intent.putExtra("key", eventKey);
        deleteNotification(notificationKey);
        startActivity(intent);
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
            case Constants.EVENT_JOIN_NOTIFICATION:
                showEventJoinDialog(notification, notificationKey);
                break;
            case Constants.EVENT_QUIT_NOTIFICATION:
                showEventQuitDialog(notification, notificationKey);
                break;
            case Constants.EVENT_CANCEL_NOTIFICATION:
                showEventCancelDialog(notification, notificationKey);
                break;
            case Constants.EVENT_SET_NOTIFICATION:
                showEventSetDialog(notification, notificationKey);
                break;
            case Constants.EVENT_PENDING_NOTIFICATION:
                showEventPendingDialog(notification, notificationKey);
                break;
            case Constants.EVENT_INVITATION_NOTIFICATION:
                openEventDetailActivity(notification, notificationKey);
                break;
            default:
                break;
        }
    }
}
