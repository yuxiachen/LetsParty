package com.example.android.letsparty.model;

import com.google.firebase.Timestamp;

public class Event {
    private String category;
    private String title;
    private String imgUrl;
    private Timestamp time;
    private Location location;
    private String description;
    private int minPeople;
    private String organizer;

    public Event(String category, String title, Timestamp time, Location location, String imgUrl) {
        this.category = category;
        this.time = time;
        this.title = title;
        this.location = location;
        this.imgUrl = imgUrl;
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


    public String getCategory() {
        return category;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getDescription() {
        return description;
    }

    public int getMinPeople() {
        return minPeople;
    }

    public String getOrganizer() {
        return organizer;
    }
}
