package com.example.cantuscodex.data.events.model;

import com.example.cantuscodex.data.songs.model.Song;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Event {

    public static final String FIELD_CLASSNAME = "events";
    public static final String FIELD_ANNOUNCER = "announcer";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_ORGANIZERS = "organizers";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_START_DATE = "startDate";
    public static final String FIELD_APPLICATION_DEADLINE = "applicationDeadline";
    public static final String FIELD_PARTICIPANT_LIMIT = "participantLimit";
    public static final String FIELD_LOCATION = "location";
    public static final String FIELD_SONGS = "songs";

    private String Announcer, Name, Organizers, Description;
    private Timestamp StartDate, ApplicationDeadline;
    private Integer ParticipantLimit;
    private String Location;
   // private ArrayList<Song> Songs;

    public Event() {

    }

    public Event(String announcer,
                 String name,
                 Timestamp startDate,
                 Timestamp applicationDeadline,
                 Integer participantLimit,
                 String location,
                 String organizers,
                 String description
                 /*ArrayList<Song> songs*/) {

        Announcer = announcer;
        Name = name;
        StartDate = startDate;
        ApplicationDeadline = applicationDeadline;
        ParticipantLimit = participantLimit;
        Location = location;
        Organizers = organizers;
        Description = description;
     //   Songs = songs;
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

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
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

/*    public ArrayList<Song> getSongs() {
        return Songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        Songs = songs;
    }*/

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FIELD_ANNOUNCER, getAnnouncer());
        result.put(FIELD_NAME, getName());
        result.put(FIELD_ORGANIZERS, getOrganizers());
        result.put(FIELD_DESCRIPTION, getDescription());
        result.put(FIELD_LOCATION, getLocation());
        result.put(FIELD_APPLICATION_DEADLINE, getApplicationDeadline());
        result.put(FIELD_START_DATE, getStartDate());
        result.put(FIELD_PARTICIPANT_LIMIT, getParticipantLimit());
        //result.put(FIELD_SONGS, getSongs());

        return result;
    }
}
