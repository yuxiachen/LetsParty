package com.example.android.letsparty.model;

public class User {
    private String userName;
    private String profileImageUrl;
    private String email;
    private Location location;
    private String interest;

    public User() {
    }

    public User(String userName, String email){
        this.email = email;
        this.userName = userName;
    }

    public User(String userName, String email, String profileImageUrl){
        this.userName = userName;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }

    public User(String userName, String profileImageUrl, String email, Location location, String interest) {
        this.userName = userName;
        this.profileImageUrl = profileImageUrl;
        this.email = email;
        this.location = location;
        this.interest = interest;
    }

    public User(String userName, String email, Location location, String interest) {
        this.userName = userName;
        this.email = email;
        this.location = location;
        this.interest = interest;
    }

    public String getInterest(){
        return interest;
    }

    public String getUserName() {
        return userName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public Location getLocation() {
        return location;
    }

    public String getEmail(){
        return email;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
