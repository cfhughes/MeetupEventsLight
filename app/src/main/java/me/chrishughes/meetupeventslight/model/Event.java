package me.chrishughes.meetupeventslight.model;

import com.squareup.moshi.Json;
import java.util.Objects;

public class Event {

    @Json(name = "id")
    private String id;

    @Json(name = "name")
    private String name;

    @Json(name= "group")
    private Group group;

    @Json(name = "yes_rsvp_count")
    private int rsvpCount;

    @Json(name = "self")
    private Self self;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event event = (Event) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    public Self getSelf() {
        return self;
    }
}
