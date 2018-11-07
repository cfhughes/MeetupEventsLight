package me.chrishughes.meetupeventslight.model;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MeetupClient {


  public static Retrofit retrofit;

  public static Retrofit getRetrofit() {

    if (retrofit == null) {

      retrofit = new Retrofit.Builder()
          .addConverterFactory(MoshiConverterFactory.create())
          .baseUrl("https://api.meetup.com/")
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .build();

    }

    return retrofit;
  }

  public void NetworkClient() {

  }

}
