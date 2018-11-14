package me.chrishughes.meetupeventslight.model;

import com.squareup.moshi.Json;

public class Self {

  @Json(name = "rsvp")
  private RsvpResult rsvp;

  public RsvpResult getRsvp() {
    return rsvp;
  }
}
