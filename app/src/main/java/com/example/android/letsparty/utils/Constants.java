package com.example.android.letsparty.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
    public final static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);


    // Notification types
    public final static String FRIEND_REQUEST_NOTIFICATION =  "Friend Request";
    public final static String FRIEND_REQUEST_ACCEPTED_NOTIFICATION = "Friend Request Accepted";
    public final static String EVENT_INVITATION_NOTIFICATION = "Event Invitation";
    public final static String EVENT_CANCEL_NOTIFICATION = "Event Cancel";
    public final static String EVENT_JOIN_NOTIFICATION = "Event Join";
    public final static String EVENT_QUIT_NOTIFICATION = "Event Quit";

    // Notification message
    public final static String FRIEND_REQUEST_NOTIFICATION_MESSAGE = " sents you a friend invitation";
    public final static String FRIEND_REQUEST_ACCEPTED_NOTIFICATION_MESSAGE = " has accepted your friend request. You are firend now!";

}
