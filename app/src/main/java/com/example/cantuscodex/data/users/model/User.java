package com.example.cantuscodex.data.users.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {
    public static final String FIELD_CLASSNAME = "users";
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_IS_ADMIN = "admin";

    private String username;
    private String email;
    private boolean admin;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public User(String username, String email, boolean admin) {
        this.username = username;
        this.email = email;
        this.admin = admin;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FIELD_USERNAME, getUsername());
        result.put(FIELD_EMAIL, getEmail());
        result.put(FIELD_IS_ADMIN, isAdmin());

        return result;
    }

}

