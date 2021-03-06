package me.chrishughes.meetupeventslight.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.analytics.FirebaseAnalytics;
import me.chrishughes.meetupeventslight.R;
import me.chrishughes.meetupeventslight.model.OAuthToken;
import me.chrishughes.meetupeventslight.service.OAuthService;
import me.chrishughes.meetupeventslight.view.MainViewInterface.TokenProvider;
import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener, TokenProvider {

  private static final String CLIENT_ID = "gn7lm2k3ukkk74esivufdn1j6m";
  private static final String CODE = "code";
  private static final String API_SCOPE = "rsvp";
  private static final String REDIRECT_URI = "me.chrishughes.meetupeventslight:/oauth2redirect";
  private static final String REDIRECT_URI_ROOT = "me.chrishughes.meetupeventslight";
  private static final String ERROR_CODE = "error";
  private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
  private static final String CLIENT_SECRET = "5m4eopv4ovffvtrm5iokqc8m7s";
  private String code;
  private String error;
  private String token;
  private MyEventsFragment myEventsFragment;
  private DiscoverEventsFragment discoverFragment;
  private FirebaseAnalytics firebaseAnalytics;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    firebaseAnalytics = FirebaseAnalytics.getInstance(this);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
    token = sharedPref.getString(getString(R.string.token_key), null);
    if (token != null) {
      findViewById(R.id.login).setVisibility(View.GONE);
      findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

      myEventsFragment = new MyEventsFragment();
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.fragment_container, myEventsFragment).commit();
    }

    //login button
    Button login = findViewById(R.id.login);
    login.setOnClickListener(view -> {
      HttpUrl authorizeUrl = HttpUrl.parse("https://secure.meetup.com/oauth2/authorize") //
          .newBuilder() //
          .addQueryParameter("client_id", CLIENT_ID)
          .addQueryParameter("scope", API_SCOPE)
          .addQueryParameter("redirect_uri", REDIRECT_URI)
          .addQueryParameter("response_type", CODE)
          .build();
      Intent i = new Intent(Intent.ACTION_VIEW);
      i.setData(Uri.parse(String.valueOf(authorizeUrl.url())));
      startActivity(i);
    });

    //handle url
    Uri data = getIntent().getData();
    if (data != null && !TextUtils.isEmpty(data.getScheme())) {
      if (REDIRECT_URI_ROOT.equals(data.getScheme())) {
        code = data.getQueryParameter(CODE);
        error = data.getQueryParameter(ERROR_CODE);
        Log.e("Login", "onCreate: handle result of authorization with code :" + code);
        if (!TextUtils.isEmpty(code)) {
          getTokenFormUrl();
        }
        if (!TextUtils.isEmpty(error)) {
          //a problem occurs, the user reject our granting request or something like that
          Toast.makeText(this, "Auth Error", Toast.LENGTH_LONG).show();
          Log.e("Login", "onCreate: handle result of authorization with error :" + error);
          //then die
          finish();
        }
      }
    }

  }

  /**
   * Retrieve the OAuth token
   */
  private void getTokenFormUrl() {
    Retrofit retrofit = new Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl("https://secure.meetup.com/")
        .build();
    OAuthService oAuthServer = retrofit.create(OAuthService.class);
    Call<OAuthToken> getRequestTokenFormCall = oAuthServer.requestTokenForm(
        code,
        CLIENT_ID,
        CLIENT_SECRET,
        REDIRECT_URI,
        GRANT_TYPE_AUTHORIZATION_CODE
    );
    getRequestTokenFormCall.enqueue(new Callback<OAuthToken>() {
      @Override
      public void onResponse(Call<OAuthToken> call, Response<OAuthToken> response) {
        Log.e("Login", "===============New Call==========================");
        Log.e("Login", "The call getRequestTokenFormCall succeed with code=" + response.code()
            + " and has body = " + response.body().getAccessToken());
        //ok we have the token
        token = response.body().getAccessToken();

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.token_key), token);
        editor.apply();

        findViewById(R.id.login).setVisibility(View.GONE);
        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

        myEventsFragment = new MyEventsFragment();
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, myEventsFragment).commit();

      }

      @Override
      public void onFailure(Call<OAuthToken> call, Throwable t) {
        Log.e("Login", "===============New Call==========================");
        Log.e("Login", "The call getRequestTokenFormCall failed", t);
      }
    });
  }


  @Override
  public void onBackPressed() {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();


    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    //Don't navigate if user isn't logged in
    if (token == null){
      return true;
    }

    // Handle navigation view item clicks here.
    int id = item.getItemId();

    Fragment fragment = null;

    if (id == R.id.nav_my_events) {
      if (myEventsFragment == null){
        myEventsFragment = new MyEventsFragment();
      }
      fragment = myEventsFragment;
    } else if (id == R.id.nav_discover) {
      if (discoverFragment == null){
        discoverFragment = new DiscoverEventsFragment();
      }
      fragment = discoverFragment;
    }
    if (fragment != null) {
      getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
          .commit();
    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  @Override
  public String getToken() {
    return token;
  }
}
