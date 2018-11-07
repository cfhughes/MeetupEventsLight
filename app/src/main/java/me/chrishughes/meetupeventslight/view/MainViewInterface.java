package me.chrishughes.meetupeventslight.view;

import java.util.List;
import me.chrishughes.meetupeventslight.model.Event;
import me.chrishughes.meetupeventslight.service.MeetupService.Results;

public interface MainViewInterface {

  void showToast(String s);

  void displayRsvpYesEvents(List<Event> eventsResponse);

  void displayError(String s);

  void displayUpcomingEvents(Results<Event> eventResponse);

  interface TokenProvider {

    String getToken();
  }

}
