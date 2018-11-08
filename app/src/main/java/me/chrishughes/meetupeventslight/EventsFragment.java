package me.chrishughes.meetupeventslight;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;
import static me.chrishughes.meetupeventslight.EventFragment.EVENT_ID;
import static me.chrishughes.meetupeventslight.EventFragment.URL_NAME;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import me.chrishughes.meetupeventslight.EventsFragment.EventsAdapter.ViewHolder;
import me.chrishughes.meetupeventslight.model.Event;
import me.chrishughes.meetupeventslight.model.RsvpResult;
import me.chrishughes.meetupeventslight.service.MeetupService.Results;
import me.chrishughes.meetupeventslight.view.MainPresenter;
import me.chrishughes.meetupeventslight.view.MainViewInterface;
import me.chrishughes.meetupeventslight.view.MaterialColors;

public class EventsFragment extends Fragment implements MainViewInterface {

  private static Random random = new Random();
  private MainPresenter mainPresenter;
  private EventsAdapter adapter;
  private RecyclerView recyclerView;
  private List<Event> events = new ArrayList<>();
  private Map<String, ViewHolder> eventViewsById = new HashMap<>();

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    recyclerView = (RecyclerView) inflater.inflate(R.layout.events_list, container, false);
    recyclerView.setHasFixedSize(true);

    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(layoutManager);

    DividerItemDecoration itemDecor = new DividerItemDecoration(getActivity(), VERTICAL);
    recyclerView.addItemDecoration(itemDecor);

    mainPresenter = new MainPresenter(this, ((TokenProvider) getActivity()).getToken());

    if (events.size() == 0) {
      mainPresenter.getRsvpYesEvents();
      mainPresenter.getUpcomingEvents();
    }

    adapter = new EventsAdapter();
    recyclerView.setAdapter(adapter);
    /*MeetupService meetupService = retrofit.create(MeetupService.class);
    meetupService.getRsvpYesEvents("Bearer " + token, "yes").enqueue(new Callback<Results<Event>>() {
      @Override
      public void onResponse(
          Call<Results<Event>> call, Response<Results<Event>> response) {
        for (Event e:response.body().getResults()){
          System.out.println(e);
        }
      }

      @Override
      public void onFailure(Call<MeetupService.Results<Event>> call, Throwable t) {
        Log.e("Login", "===============New Call==========================");
        Log.e("Login", "The call getRsvpYesEvents failed", t);
      }
    });*/

    return recyclerView;
  }

  @Override
  public void showToast(String s) {
    Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
  }

  @Override
  public void displayRsvpYesEvents(List<Event> eventsResponse) {
    events.addAll(0, eventsResponse);
    adapter.notifyDataSetChanged();
  }

  @Override
  public void displayError(String s) {
    showToast(s);
  }

  @Override
  public void displayUpcomingEvents(Results<Event> eventResponse) {
    events.addAll(eventResponse.getResults());
    adapter.notifyDataSetChanged();
  }

  @Override
  public void handleRsvpResult(RsvpResult eventResponse, String id) {
    ViewHolder viewHolder = eventViewsById.get(id);
    if (viewHolder.currentEvent.getId().equals(id)) {
      viewHolder.rsvpBox.setEnabled(true);
    }
  }

  class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
      LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
          .inflate(R.layout.event_list_item, parent, false);
      return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
      final Event event = events.get(i);
      eventViewsById.put(event.getId(), viewHolder);
      viewHolder.currentEvent = event;
      viewHolder.name.setText(event.toString());
      viewHolder.rsvpCount
          .setText(String.format(Locale.getDefault(), "RSVPs: %d", event.getRsvpCount()));
      viewHolder.itemView.setOnClickListener(v -> {
        EventFragment eventFragment = new EventFragment();
        Bundle bundle = new Bundle();
        bundle.putString(URL_NAME, event.getGroup().getUrlName());
        bundle.putString(EVENT_ID, event.getId());
        eventFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, eventFragment)
            .addToBackStack("ShowEvent").commit();
      });
      viewHolder.eventIcon.setBackground(new ColorDrawable(MaterialColors.colors
          .get(Math.abs(event.getGroup().getName().hashCode()) % MaterialColors.colors.size())));
      viewHolder.eventIcon.setText(String.format("%s", event.getGroup().getName().charAt(0)));

      viewHolder.rsvpBox.setChecked(event.isYesRsvp());

      viewHolder.rsvpBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
          RsvpResult rsvp = new RsvpResult();
          if (isChecked) {
            rsvp.setResponse("yes");
          } else {
            rsvp.setResponse("no");
          }
          mainPresenter.sendRsvp(rsvp, event.getGroup().getUrlName(), event.getId());
          viewHolder.rsvpBox.setEnabled(false);
        }
      });
      viewHolder.rsvpBox.setEnabled(true);
    }

    @Override
    public int getItemCount() {
      return events.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

      TextView eventIcon;
      TextView name;
      TextView rsvpCount;
      CheckBox rsvpBox;
      Event currentEvent;

      public ViewHolder(@NonNull LinearLayout itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.list_event_name);
        rsvpBox = itemView.findViewById(R.id.list_event_rsvp);
        rsvpCount = itemView.findViewById(R.id.list_event_rsvp_count);
        eventIcon = itemView.findViewById(R.id.list_event_icon);
      }
    }
  }

}
