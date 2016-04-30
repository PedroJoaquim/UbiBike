package pt.ulisboa.tecnico.cmu.ubibike.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import pt.ulisboa.tecnico.cmu.ubibike.R;
import pt.ulisboa.tecnico.cmu.ubibike.UbiBike;

public class HomeFragment extends Fragment {

    private static final String TITLE = "UbiBike";

    public HomeFragment() {
        // Required empty public constructor
    }

    private UbiBike getParentActivity(){
        return (UbiBike) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();

        getParentActivity().getSupportActionBar().show();
        getParentActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getParentActivity().getSupportActionBar().setTitle(TITLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_logout:
                getParentActivity().getSessionManager().logoutUser();
                getParentActivity().showLogin();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        setHasOptionsMenu(true);
        getParentActivity().invalidateOptionsMenu();

        ImageButton trajectories = (ImageButton) v.findViewById(R.id.trajectories_icon);
        ImageButton stationsNearby = (ImageButton) v.findViewById(R.id.bike_stations_icon);
        ImageButton userProfile = (ImageButton) v.findViewById(R.id.user_icon);
        ImageButton groupChats = (ImageButton) v.findViewById(R.id.message_groups_icon);

        trajectories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentActivity().showPastTrajectoriesList();
            }
        });

        stationsNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentActivity().showBikeStationsNearbyOnMap();
            }
        });

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentActivity().showUserProfile();
            }
        });

        return v;
    }

}
