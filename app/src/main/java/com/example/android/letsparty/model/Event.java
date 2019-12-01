package com.example.android.letsparty.model;

public class Event {
    private String category;
    private String title;
    private String imgUrl;
    private long time;
    private Location location;
    private int minPeople;
    private String organizer;
    private String description;
    private boolean friendsOnly;
    private int currentPeople;

    public Event() {}

    public Event(String title, String imgUrl, int minPeople, long time, boolean friendsOnly) {
        this.title = title;
        this.imgUrl = imgUrl;
        this.minPeople = minPeople;
        this.time = time;
        this.friendsOnly = friendsOnly;
    }

    public Event(String category, String title, long time, Location location, String imgUrl) {
        this.category = category;
        this.time = time;
        this.title = title;
        this.location = location;
        this.imgUrl = imgUrl;
    }

    public Event(String title, String imgUrl, long time, Location location, boolean friendsOnly, String category, String description) {
        this.title = title;
        this.imgUrl = imgUrl;
        this.time = time;
        this.location = location;
        this.friendsOnly = friendsOnly;
        this.category = category;
        this.description = description;
    }

    public Event(String title, String imgUrl, long time, Location location, boolean friendsOnly, String category, String description, String organizer, int minPeople, int currentPeople) {
        this.title = title;
        this.imgUrl = imgUrl;
        this.time = time;
        this.location = location;
        this.friendsOnly = friendsOnly;
        this.category = category;
        this.description = description;
        this.organizer = organizer;
        this.minPeople = minPeople;
        this.currentPeople = currentPeople;
    }

    public String getTitle(){
        return title;
    }

    public long getTime(){
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

    public String getOrganizer() {
        return organizer;
    }

    public int getMinPeople() {
        return minPeople;
    }

    public String getDescription() {
        return description;
    }

    public boolean getFriendsOnly() {
        return friendsOnly;
    }

    public int getCurrentPeople() {
        return currentPeople;
    }

    public void setCurrentPeople(int currentPeople) {
        this.currentPeople = currentPeople;
    }
}
