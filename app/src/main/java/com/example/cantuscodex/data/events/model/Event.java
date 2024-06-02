package com.example.cantuscodex.data.events.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;

public class Event {
    String Announcer;
    String Name;
    Timestamp StartDate;
    Timestamp ApplicationDeadline;
    Integer ParticipantLimit;
    GeoPoint Location;
    String Organizers;
    String Description;
    ArrayList<DocumentReference> Songs;


    public Event(String announcer,
                 String name,
                 Timestamp startDate,
                 Timestamp applicationDeadline,
                 Integer participantLimit,
                 GeoPoint location,
                 String organizers,
                 String description,
                 ArrayList<DocumentReference> songs) {

        Announcer = announcer;
        Name = name;
        StartDate = startDate;
        ApplicationDeadline = applicationDeadline;
        ParticipantLimit = participantLimit;
        Location = location;
        Organizers = organizers;
        Description = description;
        Songs = songs;
    }

    public String getAnnouncer() {
        return Announcer;
    }

    public void setAnnouncer(String announcer) {
        Announcer = announcer;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Timestamp getStartDate() {
        return StartDate;
    }

    public void setStartDate(Timestamp startDate) {
        StartDate = startDate;
    }

    public Timestamp getApplicationDeadline() {
        return ApplicationDeadline;
    }

    public void setApplicationDeadline(Timestamp applicationDeadline) {
        ApplicationDeadline = applicationDeadline;
    }

    public Integer getParticipantLimit() {
        return ParticipantLimit;
    }

    public void setParticipantLimit(Integer participantLimit) {
        ParticipantLimit = participantLimit;
    }

    public GeoPoint getLocation() {
        return Location;
    }

    public void setLocation(GeoPoint location) {
        Location = location;
    }

    public String getOrganizers() {
        return Organizers;
    }

    public void setOrganizers(String organizers) {
        Organizers = organizers;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public ArrayList<DocumentReference> getSongs() {
        return Songs;
    }

    public void setSongs(ArrayList<DocumentReference> songs) {
        Songs = songs;
    }

}
