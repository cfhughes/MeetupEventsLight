package me.chrishughes.meetupeventslight.view;

import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import me.chrishughes.meetupeventslight.model.Event;
import me.chrishughes.meetupeventslight.model.MeetupClient;
import me.chrishughes.meetupeventslight.service.MeetupService;
import me.chrishughes.meetupeventslight.service.MeetupService.Results;

public class MainPresenter implements MainPresenterInterface {

  MainViewInterface mvi;
  private String token;
  private String TAG = "MainPresenter";

  public MainPresenter(MainViewInterface mvi, String token) {
    this.mvi = mvi;
    this.token = token;
  }

  @Override
  public void getRsvpYesEvents() {
    getRsvpYesObservable().subscribeWith(getObserver());
  }

  @Override
  public void getUpcomingEvents() {
    getUpcomingEventsObservable().subscribeWith(getUpcomingEventsObserver());
  }

  private Observable<Results<Event>> getUpcomingEventsObservable() {
    return MeetupClient.getRetrofit().create(MeetupService.class)
        .getUpcomingEvents("Bearer " + token)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  private Observable<Results<Event>> getRsvpYesObservable() {
    return MeetupClient.getRetrofit().create(MeetupService.class)
        .getRsvpYesEvents("Bearer " + token, "yes")
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  public DisposableObserver<Results<Event>> getObserver() {
    return new DisposableObserver<Results<Event>>() {

      @Override
      public void onNext(@NonNull Results<Event> eventResponse) {
        Log.d(TAG, "OnNext" + eventResponse.getResults());
        List<Event> events = eventResponse.getResults();
        for (int i = 0; i < events.size(); i++) {
          events.get(i).setYesRsvp(true);
        }
        mvi.displayRsvpYesEvents(events);
      }

      @Override
      public void onError(@NonNull Throwable e) {
        Log.d(TAG, "Error" + e);
        e.printStackTrace();
        mvi.displayError("Error fetching Event Data");
      }

      @Override
      public void onComplete() {
        Log.d(TAG, "Completed");
      }
    };
  }

  public DisposableObserver<Results<Event>> getUpcomingEventsObserver() {
    return new DisposableObserver<Results<Event>>() {

      @Override
      public void onNext(@NonNull Results<Event> eventResponse) {
        Log.d(TAG, "OnNext" + eventResponse.getResults());
        mvi.displayUpcomingEvents(eventResponse);
      }

      @Override
      public void onError(@NonNull Throwable e) {
        Log.d(TAG, "Error" + e);
        e.printStackTrace();
        mvi.displayError("Error fetching Event Data");
      }

      @Override
      public void onComplete() {
        Log.d(TAG, "Completed");
      }
    };
  }
}
