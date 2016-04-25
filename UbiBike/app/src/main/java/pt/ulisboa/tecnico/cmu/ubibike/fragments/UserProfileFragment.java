package pt.ulisboa.tecnico.cmu.ubibike.fragments;


import android.graphics.Color;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
import pt.ulisboa.tecnico.cmu.ubibike.domain.Data;
import pt.ulisboa.tecnico.cmu.ubibike.utils.Validator;


public class UserProfileFragment extends Fragment {


    public UserProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        setViewElements(view);


        return view;
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

        totalPoints.append(" " + data.getTotalPoints());
        totalDistance.append(" " + data.getTotalDistance());
        totalHours.append(" " + data.getTotalHours());
        totalRides.append(" " + data.getTotalRides());
        if (data.getLongestRide() != null) {
            longestRideDistance.append(" " + data.getLongestRide().getTravelledDistance());
            longestRidePoints.append(" " + data.getLongestRide().getPointsEarned());
        }
        else {
            longestRideDistance.append(" 0.0");
            longestRidePoints.append(" 0");
        }

    }


}
