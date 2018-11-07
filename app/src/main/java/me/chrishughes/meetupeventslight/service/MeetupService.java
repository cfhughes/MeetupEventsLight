package me.chrishughes.meetupeventslight.service;

import com.squareup.moshi.Json;
import io.reactivex.Observable;
import java.util.List;
import me.chrishughes.meetupeventslight.model.Event;
import me.chrishughes.meetupeventslight.model.Rsvp;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MeetupService {

  @GET("2/events")
  Observable<Results<Event>> getRsvpYesEvents(@Header("Authorization") String authorization,
      @Query("rsvp") String rsvp);

  @GET("2/concierge")
  Observable<Results<Event>> getUpcomingEvents(@Header("Authorization") String authorization);

  @GET("/{urlname}/events/{id}")
  Observable<Event> getEvent(@Header("Authorization") String authorization,
      @Path("urlname") String urlName, @Path("id") String id);

  @GET("2/rsvps")
  Observable<Results<Rsvp>> getRsvps(@Header("Authorization") String authorization,
      @Query("event_id") String eventId);

  class Results<T> {

    private @Json(name = "results")
    List<T> results;

    public List<T> getResults() {
      return results;
    }
  }

}
