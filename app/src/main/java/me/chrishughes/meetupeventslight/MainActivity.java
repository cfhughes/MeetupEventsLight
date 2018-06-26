package me.chrishughes.meetupeventslight;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import me.chrishughes.meetupeventslight.model.Event;
import me.chrishughes.meetupeventslight.model.OAuthToken;
import me.chrishughes.meetupeventslight.service.MeetupService;
import me.chrishughes.meetupeventslight.service.OAuthService;
import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import java.lang.invoke.MethodType;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String CLIENT_ID = "gn7lm2k3ukkk74esivufdn1j6m";
    private static final String CODE = "code";
    private static final String API_SCOPE = "basic";
    private static final String REDIRECT_URI = "me.chrishughes.meetupeventslight:/oauth2redirect";
    private static final String REDIRECT_URI_ROOT = "me.chrishughes.meetupeventslight";
    private static final String ERROR_CODE = "error";
    private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    private static final String CLIENT_SECRET = "5m4eopv4ovffvtrm5iokqc8m7s";
    private String code;
    private String error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //login button
        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HttpUrl authorizeUrl = HttpUrl.parse("https://secure.meetup.com/oauth2/authorize") //
                        .newBuilder() //
                        .addQueryParameter("client_id", CLIENT_ID)
                        //.addQueryParameter("scope", API_SCOPE)
                        .addQueryParameter("redirect_uri", REDIRECT_URI)
                        .addQueryParameter("response_type", CODE)
                        .build();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(String.valueOf(authorizeUrl.url())));
                startActivity(i);
            }
        });

        //handle url
        Uri data = getIntent().getData();
        if (data != null && !TextUtils.isEmpty(data.getScheme())) {
            if (REDIRECT_URI_ROOT.equals(data.getScheme())) {
                code = data.getQueryParameter(CODE);
                error=data.getQueryParameter(ERROR_CODE);
                Log.e("Login", "onCreate: handle result of authorization with code :" + code);
                if (!TextUtils.isEmpty(code)) {
                    getTokenFormUrl();
                }
                if(!TextUtils.isEmpty(error)) {
                    //a problem occurs, the user reject our granting request or something like that
                    Toast.makeText(this, "Auth Error",Toast.LENGTH_LONG).show();
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
                Log.e("Login", "The call getRequestTokenFormCall succeed with code=" + response.code() + " and has body = " + response.body().getAccessToken());
                //ok we have the token
                String token = response.body().getAccessToken();
                Retrofit retrofit = new Retrofit.Builder()
                        .addConverterFactory(MoshiConverterFactory.create())
                        .baseUrl("https://api.meetup.com/")
                        .build();
                MeetupService meetupService = retrofit.create(MeetupService.class);
                meetupService.getEvents("Bearer " + token, "yes").enqueue(new Callback<MeetupService.Results<Event>>() {
                    @Override
                    public void onResponse(Call<MeetupService.Results<Event>> call, Response<MeetupService.Results<Event>> response) {
                        for (Event e:response.body().getResults()){
                            System.out.println(e);
                        }
                    }

                    @Override
                    public void onFailure(Call<MeetupService.Results<Event>> call, Throwable t) {
                        Log.e("Login", "===============New Call==========================");
                        Log.e("Login", "The call getEvents failed", t);
                    }
                });

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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
