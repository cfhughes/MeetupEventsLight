package me.chrishughes.meetupeventslight.view;

import java.util.List;
import me.chrishughes.meetupeventslight.model.Event;
import me.chrishughes.meetupeventslight.model.RsvpResult;

public interface MainViewInterface {

  void showToast(String s);

  void displayEvents(List<Event> eventsResponse);

  void displayError(String s);

  void handleRsvpResult(RsvpResult eventResponse, Event id);

  interface TokenProvider {

    String getToken();
  }

}
