package me.chrishughes.meetupeventslight.view;

import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import me.chrishughes.meetupeventslight.model.Event;
import me.chrishughes.meetupeventslight.model.MeetupClient;
import me.chrishughes.meetupeventslight.model.Rsvp;
import me.chrishughes.meetupeventslight.service.MeetupService;
import me.chrishughes.meetupeventslight.service.MeetupService.Results;

public class EventPresenter {

  private EventViewInterface evi;
  private String token;
  private String TAG = "MainPresenter";

  public EventPresenter(EventViewInterface evi, String token) {
    this.evi = evi;
    this.token = token;
  }

  public void getEvent(String urlName, String id) {
    getEventObservable(urlName, id).subscribeWith(getEventObserver());
  }

  public void getRsvps(String id) {
    getRsvpsObservable(id).subscribeWith(getRsvpsObserver());
  }

  private Observable<Event> getEventObservable(String urlName, String id) {
    return MeetupClient.getRetrofit().create(MeetupService.class)
        .getEvent("Bearer " + token, urlName, id)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  private Observable<Results<Rsvp>> getRsvpsObservable(String id) {
    return MeetupClient.getRetrofit().create(MeetupService.class)
        .getRsvps("Bearer " + token, id)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  public DisposableObserver<Event> getEventObserver() {
    return new DisposableObserver<Event>() {

      @Override
      public void onNext(@NonNull Event eventResponse) {
        Log.d(TAG, "OnNext" + eventResponse);
        evi.displayEvent(eventResponse);
      }

      @Override
      public void onError(@NonNull Throwable e) {
        Log.d(TAG, "Error" + e);
        e.printStackTrace();
        //evi.displayError("Error fetching Event Data");
      }

      @Override
      public void onComplete() {
        Log.d(TAG, "Completed");
      }
    };
  }

  public DisposableObserver<Results<Rsvp>> getRsvpsObserver() {
    return new DisposableObserver<Results<Rsvp>>() {

      @Override
      public void onNext(@NonNull Results<Rsvp> eventResponse) {
        Log.d(TAG, "OnNext" + eventResponse);
        evi.displayRsvps(eventResponse);
      }

      @Override
      public void onError(@NonNull Throwable e) {
        Log.d(TAG, "Error" + e);
        e.printStackTrace();
        //evi.displayError("Error fetching Event Data");
      }

      @Override
      public void onComplete() {
        Log.d(TAG, "Completed");
      }
    };
  }

}
