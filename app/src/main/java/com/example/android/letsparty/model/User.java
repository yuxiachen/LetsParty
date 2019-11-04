package com.example.android.letsparty.model;

import java.util.List;

public class User {
    private String userName;
    private String profileImageUrl;
    private String email;
    private String userId;
    private List<User> friends;
    private List<Event> publishedEvents;
    public User(String userId, String userName, String email) {
        this.userId = userId;
        this.email = email;
        this.userName = userName;
    }

}
