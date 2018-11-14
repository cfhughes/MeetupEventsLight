package me.chrishughes.meetupeventslight.view;

import java.util.List;
import me.chrishughes.meetupeventslight.model.Event;
import me.chrishughes.meetupeventslight.model.Rsvp;

public interface EventViewInterface {

  void displayEvent(Event event);

  void displayRsvps(List<Rsvp> rsvps);
}
