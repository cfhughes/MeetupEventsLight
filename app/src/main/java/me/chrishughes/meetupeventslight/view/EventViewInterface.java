package me.chrishughes.meetupeventslight.view;

import me.chrishughes.meetupeventslight.model.Event;
import me.chrishughes.meetupeventslight.model.Rsvp;
import me.chrishughes.meetupeventslight.service.MeetupService.Results;

public interface EventViewInterface {

  void displayEvent(Event event);

  void displayRsvps(Results<Rsvp> rsvps);
}
