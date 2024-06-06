package com.example.cantuscodex.data.songs.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Song {
    public static final String FIELD_CLASSNAME = "songs";

    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_ORIGIN = "origin";
    public static final String FIELD_DESCRIPTION = "description";


    @DocumentId
    private String UserId;
    private String Name, Content, Origin, Description;

    public Song() {
    }

    public Song(String userId, String name, String content, String origin, String description) {
        UserId = userId;
        Name = name;
        Content = content;
        Origin = origin;
        Description = description;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getOrigin() {
        return Origin;
    }

    public void setOrigin(String origin) {
        Origin = origin;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FIELD_USER_ID, getUserId());
        result.put(FIELD_NAME, getName());
        result.put(FIELD_CONTENT, getContent());
        result.put(FIELD_DESCRIPTION, getOrigin());
        result.put(FIELD_ORIGIN, getDescription());

        return result;
    }
}
