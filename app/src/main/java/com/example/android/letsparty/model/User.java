package com.example.android.letsparty.model;

import java.util.List;

public class User {
    private String username;
    private String profileImageUrl;
    private String email;
    private String userId;
    private List<User> friends;

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

    //public User(String userId, String userName, String email) {
        //this.userId = userId;
        //this.email = email;
        //this.username = userName;
    //}

    public String getUserName() {
        return username;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getEmail() {
        return email;
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
