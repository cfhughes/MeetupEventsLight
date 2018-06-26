package me.chrishughes.meetupeventslight.service;

import com.squareup.moshi.Json;
import me.chrishughes.meetupeventslight.model.Event;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface MeetupService {

    @GET("2/events")
    Call<Results<Event>> getEvents(@Header("Authorization") String authorization,
                                @Query("rsvp") String rsvp);

    static class Results<T>{

        private @Json(name = "results")List<T> results;

        public List<T> getResults() {
            return results;
        }
    }

}
