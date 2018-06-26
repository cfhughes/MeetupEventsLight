package me.chrishughes.meetupeventslight.model;

import com.squareup.moshi.Json;

public class Event {



    @Json(name = "name")
    private String name;

    @Json(name= "group")
    private Group group;

    @Override
    public String toString() {
        return name;
    }

    public Group getGroup() {
        return group;
    }
}
