package com.example.android.letsparty.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
    public final static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);


    // Notification types
    public final static String FRIEND_REQUEST_NOTIFICATION =  "Friend Request Notification";
    public final static String FRIEND_REQUEST_ACCEPTED_NOTIFICATION = "Friend Request Accepted Notification";
    public final static String EVENT_INVITATION_NOTIFICATION = "Event Invitation";
    public final static String EVENT_CANCEL_NOTIFICATION = "Event Cancel Notification";
    public final static String EVENT_JOIN_NOTIFICATION = "Event Join Notification";
    public final static String EVENT_QUIT_NOTIFICATION = "Event Quit Notification";
    public final static String EVENT_SET_NOTIFICATION = "Event Set Notification";
    public final static String EVENT_PENDING_NOTIFICATION = "Event Pending Notification";

    // Notification message
    public final static String FRIEND_REQUEST_NOTIFICATION_MESSAGE = " sents you a friend invitation ～";
    public final static String FRIEND_REQUEST_ACCEPTED_NOTIFICATION_MESSAGE = " has accepted your friend request. You are friends now !";
    public final static String EVENT_JOIN_NOTIFICATION_MESSAGE_1 = " has joined your event ";
    public final static String EVENT_JOIN_NOTIFICATION_MESSAGE_2 = " !";
    public final static String EVENT_QUIT_NOTIFICATION_MESSAGE_1 = " has quit your event ";
    public final static String EVENT_QUIT_NOTIFICATION_MESSAGE_2 = " !";
    public final static String EVENT_CANCEL_NOTIFICATION_MESSAGE = " has been cancelled !";
    public final static String EVENT_SET_NOTIFICATION_MESSAGE = " has been set. Please follow the location and schedule to join the event !";
    public final static String EVENT_PENDING_NOTIFICATION_MESSAGE = " has became pending due to someone quit the event. Sorry for the inconvenience ～";
}
