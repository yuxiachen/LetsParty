package com.example.android.letsparty.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
    public final static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);


    // Notification types
    public final static String FRIEND_REQUEST_NOTIFICATION =  "Friend Request Notification";
    public final static String FRIEND_REQUEST_ACCEPTED_NOTIFICATION = "Friend Request Accepted Notification";
    public final static String EVENT_INVITATION_NOTIFICATION = "Event Invitation Notification";
    public final static String EVENT_CANCEL_NOTIFICATION = "Event Cancel Notification";
    public final static String EVENT_JOIN_NOTIFICATION = "Event Join Notification";
    public final static String EVENT_QUIT_NOTIFICATION = "Event Quit Notification";
    public final static String EVENT_SET_NOTIFICATION = "Event Set Notification";
    public final static String EVENT_PENDING_NOTIFICATION = "Event Pending Notification";

    // Notification message
    public final static String FRIEND_REQUEST_NOTIFICATION_MESSAGE = " Sent you a Friend Request";
    public final static String FRIEND_REQUEST_ACCEPTED_NOTIFICATION_MESSAGE = " has Accepted your Friend Request";
    public final static String EVENT_JOIN_NOTIFICATION_MESSAGE = " has Joined your Event ";
    public final static String EVENT_QUIT_NOTIFICATION_MESSAGE = " has Quit your Event ";
    public final static String EVENT_CANCEL_NOTIFICATION_MESSAGE = " has been Cancelled";
    public final static String EVENT_SET_NOTIFICATION_MESSAGE = " has been Set";
    public final static String EVENT_PENDING_NOTIFICATION_MESSAGE = " has became Pending";
    public final static String EVENT_PREFIX_NOTIFICATION_MESSAGE = "Event ";
    public final static String EVENT_INVITATION_NOTIFICATION_MESSAGE = " Invited you to Join the Event ";
}
