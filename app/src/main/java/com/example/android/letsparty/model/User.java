package com.example.android.letsparty.model;

import java.util.List;

public class User {
    private String username;
    private String profileImageUrl;
    private String email;
    private String userId;
    private List<User> friends;
    private Location location;
    private String interest;

    public User() {
    }

    public User(String userName, String email){
        this.email = email;
        this.username = userName;
    }

    public User(String username, String email, String profileImageUrl){
        this.username = username;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }

    public User(String username, String profileImageUrl, String email, Location location, String interest) {
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.email = email;
        this.location = location;
        this.interest = interest;
    }

    public User(String username, String email, Location location, String interest) {
        this.username = username;
        this.email = email;
        this.location = location;
        this.interest = interest;
    }

    public String getInterest(){
        return interest;
    }
    public String getUserName() {
        return username;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getEmail() {
        return email;
    }

    public Location getLocation() {
        return location;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
