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
import me.chrishughes.meetupeventslight.model.RsvpResult;
import me.chrishughes.meetupeventslight.service.MeetupService;
import me.chrishughes.meetupeventslight.service.MeetupService.Results;

public class MainPresenter implements MainPresenterInterface {

  MainViewInterface mvi;
  private String token;
  private String TAG = "MainPresenter";
  private MeetupService meetupService;

  public MainPresenter(MainViewInterface mvi, String token) {
    this.mvi = mvi;
    this.token = token;
    meetupService = MeetupClient.getRetrofit().create(MeetupService.class);
  }

  @Override
  public void getRsvpYesEvents() {
    getRsvpYesObservable().subscribeWith(getObserver());
  }

  @Override
  public void getUpcomingEvents() {
    getUpcomingEventsObservable().subscribeWith(getUpcomingEventsObserver());
  }

  @Override
  public void sendRsvp(RsvpResult rsvp, String urlName, String id) {
    getRsvpSendObservable(rsvp, urlName, id).subscribeWith(getRsvpSendObserver(id));
  }

  private Observable<Results<Event>> getUpcomingEventsObservable() {
    return meetupService
        .getUpcomingEvents("Bearer " + token)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  private Observable<Results<Event>> getRsvpYesObservable() {
    return meetupService
        .getRsvpYesEvents("Bearer " + token, "yes")
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  private Observable<RsvpResult> getRsvpSendObservable(RsvpResult rsvp, String urlName, String id) {
    Log.i("REQUEST", "Sent to: " + urlName + "," + id + "," + rsvp.getResponse());
    return meetupService
        .sendRsvp("Bearer " + token, urlName, id, rsvp.getResponse())
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

  public DisposableObserver<RsvpResult> getRsvpSendObserver(String id) {
    return new DisposableObserver<RsvpResult>() {

      @Override
      public void onNext(@NonNull RsvpResult eventResponse) {
        Log.d(TAG, "OnNext" + eventResponse);
        mvi.handleRsvpResult(eventResponse, id);
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
