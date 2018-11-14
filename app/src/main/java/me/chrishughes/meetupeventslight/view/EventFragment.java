package me.chrishughes.meetupeventslight.view;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import me.chrishughes.meetupeventslight.R;
import me.chrishughes.meetupeventslight.model.Event;
import me.chrishughes.meetupeventslight.model.Rsvp;
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
      eventPresenter.getRsvps(bundle.getString(URL_NAME), bundle.getString(EVENT_ID));
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
  public void displayRsvps(List<Rsvp> rsvpsResult) {
    rsvps = rsvpsResult;
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
      if (rsvp.getMemberPhoto() != null) {
        Glide.with(getActivity())
            .load(rsvp.getMemberPhoto().getPhotoLink())
            .apply(RequestOptions.circleCropTransform())
            .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background))
            .into(new SimpleTarget<Drawable>(128,128) {
              @Override
              public void onResourceReady(@NonNull Drawable resource,
                  @Nullable Transition<? super Drawable> transition) {
                /* Set a drawable to the left of textView */
                viewHolder.name.setCompoundDrawablesWithIntrinsicBounds(resource, null, null, null);
              }
            });
      }
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
