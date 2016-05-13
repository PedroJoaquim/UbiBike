package pt.ulisboa.tecnico.cmu.ubibike.fragments;


import android.graphics.Color;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;

import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.R;
import pt.ulisboa.tecnico.cmu.ubibike.UbiBike;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Data;
import pt.ulisboa.tecnico.cmu.ubibike.utils.Validator;


public class UserProfileFragment extends Fragment {

    public static final String TITLE = "Profile";

    public UserProfileFragment() {
        // Required empty public constructor
    }

    private UbiBike getParentActivity(){
        return (UbiBike) getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        setHasOptionsMenu(false);
        getParentActivity().invalidateOptionsMenu();

        setViewElements(view);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getParentActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getParentActivity().getSupportActionBar().setTitle(TITLE);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem item = menu.findItem(R.id.action_logout);
        item.setVisible(false);
    }

    private void setViewElements(View view){

        Data data = ApplicationContext.getInstance().getData();

        TextView totalPoints = (TextView) view.findViewById(R.id.total_points);
        TextView totalDistance = (TextView) view.findViewById(R.id.total_distance);
        TextView totalHours = (TextView) view.findViewById(R.id.total_hours);
        TextView totalRides = (TextView) view.findViewById(R.id.total_rides);
        TextView longestRideDistance = (TextView) view.findViewById(R.id.longest_ride_distance);
        TextView longestRidePoints = (TextView) view.findViewById(R.id.longest_ride_points);
        TextView longestRideDuration = (TextView) view.findViewById(R.id.longest_ride_duration);
        TextView rank = (TextView) view.findViewById(R.id.rank);
        TextView totalSharedPoints = (TextView) view.findViewById(R.id.total_shared_points);
        TextView longestRideAverageSpeed = (TextView) view.findViewById(R.id.longest_ride_average_speed);

        totalPoints.append(" " + data.getTotalPoints());
        totalDistance.append(" " + data.getTotalDistance());
        totalHours.append(" " + data.getTotalHours());
        totalRides.append(" " + data.getTotalRides());
        rank.append(" " + data.getGlobalRank());
        if (data.getLongestRide() != null) {
            longestRideDistance.append(" " + data.getLongestRide().getTravelledDistance());
            longestRidePoints.append(" " + data.getLongestRide().getPointsEarned());
            long diff = data.getLongestRide().getEndTime().getTime() - data.getLongestRide().getStartTime().getTime();
            long diffMinutes = diff / (60 * 1000) % 60;
            longestRideDuration.append(" " + diffMinutes + "min.");
            longestRideAverageSpeed.append(" 0 km/h");
        }
        else { // TODO: remove hardcoded below
            longestRideDistance.append(" 0.0");
            longestRidePoints.append(" 0");
            longestRideDuration.append(" 0 min.");
            longestRideAverageSpeed.append(" 0 km/h");
        }
        totalSharedPoints.append(" 0");








    }


}
