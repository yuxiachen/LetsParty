package com.example.android.letsparty.model;

import java.util.List;

public class User {
    private String userName;
    private String profileImageUrl;
    private String email;
    private String userId;
    private List<User> friends;

    public User() {
    }

    public User(String userName, String email){
        this.email = email;
        this.userName = userName;
    }

    public User(String userId, String userName, String email) {
        this.userId = userId;
        this.email = email;
        this.userName = userName;
    }

    public String getEmail(){
        return email;
    }

    public String getUserName() {
        return userName;
    }

}
