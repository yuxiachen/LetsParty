package com.example.android.letsparty.model;

public class Notification {
    private User sender;
    private Event event;
    private String senderKey;
    private String eventKey;
    private String notificationType;
    private long time;

    public Notification(){}

    public Notification(String notificationType, User sender, String senderKey, long time) {
        this.notificationType = notificationType;
        this.sender = sender;
        this.senderKey = senderKey;
        this.time = time;
    }

    public Notification(String notificationType, Event event, String eventKey, long time) {
        this.notificationType = notificationType;
        this.event = event;
        this.eventKey = eventKey;
        this.time = time;
    }

    public Notification(String notificationType, User sender, String senderKey, Event event, String eventKey, long time) {
        this.notificationType = notificationType;
        this.sender = sender;
        this.senderKey = senderKey;
        this.event = event;
        this.eventKey = eventKey;
        this.time = time;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getSenderKey() {
        return senderKey;
    }

    public void setSenderKey(String senderKey) {
        this.senderKey = senderKey;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }
}
