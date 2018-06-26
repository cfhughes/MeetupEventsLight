package me.chrishughes.meetupeventslight.model;

import com.squareup.moshi.Json;

public class Group {

    private @Json(name = "name")String name;

    public String getName() {
        return name;
    }
}
