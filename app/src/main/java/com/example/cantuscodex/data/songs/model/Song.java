package com.example.cantuscodex.data.songs.model;

import com.google.firebase.firestore.DocumentId;

public class Song {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_ORIGIN = "origin";
    public static final String FIELD_DESCRIPTION = "description";


    @DocumentId
    private String Id;
    private String Name, Content, Origin, Description;

    public Song() {
    }

    public Song(String id, String name, String content, String origin, String description) {
        Id = id;
        Name = name;
        Content = content;
        Origin = origin;
        Description = description;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
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
}
