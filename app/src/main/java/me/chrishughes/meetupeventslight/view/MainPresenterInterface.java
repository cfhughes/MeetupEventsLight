package me.chrishughes.meetupeventslight.view;

import me.chrishughes.meetupeventslight.model.RsvpResult;

public interface MainPresenterInterface {

  void getRsvpYesEvents();

  void getUpcomingEvents();

  void sendRsvp(RsvpResult rsvp, String urlName, String id);
}
