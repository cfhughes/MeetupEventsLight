package me.chrishughes.meetupeventslight;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import me.chrishughes.meetupeventslight.model.Event;
import me.chrishughes.meetupeventslight.model.Rsvp;
import me.chrishughes.meetupeventslight.service.MeetupService.Results;
import me.chrishughes.meetupeventslight.view.EventPresenter;
import me.chrishughes.meetupeventslight.view.EventViewInterface;
import me.chrishughes.meetupeventslight.view.MainViewInterface.TokenProvider;

public class EventFragment extends Fragment implements EventViewInterface {


  public static final String EVENT_ID = "EVENT_ID";
  public static final String URL_NAME = "URL_NAME";
  private View view;
  private TextView groupName;
  private TextView eventName;
  private EventPresenter eventPresenter;
  private List<Rsvp> rsvps = new ArrayList<>();
  private RecyclerView rsvpView;
  private RsvpsAdapter adapter;


  public EventFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    view = inflater.inflate(R.layout.fragment_event, container, false);
    groupName = view.findViewById(R.id.group_name);
    eventName = view.findViewById(R.id.event_name);

    eventPresenter = new EventPresenter(this, ((TokenProvider) getActivity()).getToken());

    Bundle bundle = getArguments();

    eventPresenter.getEvent(bundle.getString(URL_NAME), bundle.getString(EVENT_ID));

    rsvpView = view.findViewById(R.id.rsvps_view);
    rsvpView.setHasFixedSize(true);

    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
    rsvpView.setLayoutManager(layoutManager);

    DividerItemDecoration itemDecor = new DividerItemDecoration(getActivity(), VERTICAL);
    rsvpView.addItemDecoration(itemDecor);

    if (rsvps.size() == 0) {
      eventPresenter.getRsvps(bundle.getString(EVENT_ID));
    }

    adapter = new RsvpsAdapter();
    rsvpView.setAdapter(adapter);

    return view;
  }

  @Override
  public void displayEvent(Event event) {
    groupName.setText(event.getGroup().getName());
    eventName.setText(event.toString());
  }

  @Override
  public void displayRsvps(Results<Rsvp> rsvpsResult) {
    rsvps.addAll(rsvpsResult.getResults());
    adapter.notifyDataSetChanged();
  }

  private class RsvpsAdapter extends RecyclerView.Adapter<EventFragment.RsvpsAdapter.ViewHolder> {

    @NonNull
    @Override
    public EventFragment.RsvpsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
        int i) {
      TextView v = (TextView) LayoutInflater.from(parent.getContext())
          .inflate(android.R.layout.simple_list_item_1, parent, false);
      return new EventFragment.RsvpsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventFragment.RsvpsAdapter.ViewHolder viewHolder, int i) {
      final Rsvp rsvp = rsvps.get(i);
      viewHolder.name.setText(rsvp.getMember().getName());
    }

    @Override
    public int getItemCount() {
      return rsvps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

      TextView name;

      public ViewHolder(@NonNull TextView itemView) {
        super(itemView);
        name = itemView;
      }
    }
  }
}
