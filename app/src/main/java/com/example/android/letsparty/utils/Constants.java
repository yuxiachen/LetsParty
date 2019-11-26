package com.example.android.letsparty.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
    public final static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);


    // Notification types
    public final static String FRIEND_REQUEST_NOTIFICATION =  "Friend Request";
    public final static String FRIEND_REQUEST_ACCEPTED_NOTIFICATION = "Friend Request Accepted";
    public final static String EVENT_INVITATION_NOTIFICATION = "Event Invitation";
    public final static String EVENT_CANCEL_NOTIFICATION = "Event cancel";
    public final static String JOIN_EVENT_NOTIFICATION = "Join Event";
    public final static String QUIT_EVENT_NOTIFICATION = "Quit Event";


    // Notification message
    public final static String FRIEND_REQUEST_NOTIFICATION_MESSAGE = " sents you a friend invitation";

}
