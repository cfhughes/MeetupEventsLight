package me.chrishughes.meetupeventslight.model;

import com.squareup.moshi.Json;

public class Group {

    @Json(name = "name")
    private String name;

    @Json(name = "urlname")
    private String urlName;

    public String getName() {
        return name;
    }

    public String getUrlName() {
        return urlName;
    }
}
