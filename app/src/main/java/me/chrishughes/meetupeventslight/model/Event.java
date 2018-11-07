package me.chrishughes.meetupeventslight.model;

import com.squareup.moshi.Json;

public class Event {

    @Json(name = "id")
    private String id;

    @Json(name = "name")
    private String name;

    @Json(name= "group")
    private Group group;

    @Json(name = "yes_rsvp_count")
    private int rsvpCount;

    private boolean yesRsvp;

    @Override
    public String toString() {
        return name;
    }

    public Group getGroup() {
        return group;
    }

    public int getRsvpCount() {
        return rsvpCount;
    }

    public boolean isYesRsvp() {
        return yesRsvp;
    }

    public void setYesRsvp(boolean yesRsvp) {
        this.yesRsvp = yesRsvp;
    }

    public String getId() {
        return id;
    }
}
