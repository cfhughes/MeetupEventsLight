package me.chrishughes.meetupeventslight.model;

import com.squareup.moshi.Json;

public class RsvpResult {

  @Json(name = "response")
  private String response;

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }
}
