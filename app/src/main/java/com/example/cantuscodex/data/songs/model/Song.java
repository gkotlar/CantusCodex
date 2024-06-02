package com.example.cantuscodex.data.songs.model;

public class Song {
    String Name;
    String Content;
    String Origin;
    String Description;

    public Song() {
    }

    public Song(String name, String content, String origin, String description) {
        Name = name;
        Content = content;
        Origin = origin;
        Description = description;
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
