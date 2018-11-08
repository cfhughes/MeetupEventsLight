package me.chrishughes.meetupeventslight.model;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MeetupClient {


  public static Retrofit retrofit;

  public static Retrofit getRetrofit() {

    if (retrofit == null) {
      HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
      logging.setLevel(Level.BODY);

      OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
// add your other interceptors …

// add logging as last interceptor
      httpClient.addInterceptor(logging);

      retrofit = new Retrofit.Builder()
          .addConverterFactory(MoshiConverterFactory.create())
          .baseUrl("https://api.meetup.com/")
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .client(httpClient.build())
          .build();

    }

    return retrofit;
  }

  public void NetworkClient() {

  }

}
