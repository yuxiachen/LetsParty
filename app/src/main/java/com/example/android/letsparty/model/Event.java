package com.example.android.letsparty.model;

import com.google.firebase.Timestamp;

public class Event {
    private String category;
    private String title;
    private Timestamp time;
    private Location location;
    public Event(String category, String title, Timestamp time, Location location) {
        this.category = category;
        this.time = time;
        this.title = title;
        this.location = location;
    }

    public Event() {}

    public String getTitle(){
        return title;
    }

    public Timestamp getTime(){
        return time;
    }

    public Location getLocation(){
        return location;
    }
}
