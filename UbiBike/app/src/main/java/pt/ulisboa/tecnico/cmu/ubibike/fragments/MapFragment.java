package pt.ulisboa.tecnico.cmu.ubibike.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.R;
import pt.ulisboa.tecnico.cmu.ubibike.UbiBike;
import pt.ulisboa.tecnico.cmu.ubibike.domain.BikePickupStation;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Trajectory;


/**
 * Created by andriy on 12.03.2016.
 */
public class MapFragment extends Fragment {

    private GoogleMap mGoogleMap;
    private SupportMapFragment mSupportMapFragment;

    private int mTrajectoryBeingShowed;
    private int mTrajectoriesCount;

    private boolean mTrajectoryView;

    private UbiBike getParentActivity(){
        return (UbiBike) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getParentActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final View v = inflater.inflate(R.layout.map_fragment, null, false);

        if(getArguments() != null) { //check if we are on trajectory view

            mTrajectoryView = true;

            mTrajectoryBeingShowed = getArguments().getInt("trajectoryID");
            mTrajectoriesCount = getArguments().getInt("trajectoriesCount");
        }
        else{
            mTrajectoryView = false;
        }


        setMap();

        setViewElements(v);

        return v;
    }

    private void setViewElements(View v) {

        FrameLayout nextTrajectory = (FrameLayout) v.findViewById(R.id.next_trajectory_frame);
        FrameLayout previousTrajectory = (FrameLayout) v.findViewById(R.id.prev_trajectory_frame);

        if(mTrajectoryView) {

            nextTrajectory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNextTrajectoryOnMap();
                }
            });

            previousTrajectory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPreviousTrajectoryOnMap();
                }
            });
        }
        else{
            nextTrajectory.setVisibility(View.INVISIBLE);
            previousTrajectory.setVisibility(View.INVISIBLE);
        }
    }

    private void setMap(){

        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.add(R.id.google_map, mSupportMapFragment).commit();
        }

        if (mSupportMapFragment != null) {

            mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    if (googleMap != null) {

                        mGoogleMap = googleMap;

                        googleMap.getUiSettings().setAllGesturesEnabled(true);


                        if(mTrajectoryView){    //swowing trajectory
                            showTrajectory(mTrajectoryBeingShowed);
                        }
                        else{       //showing bike stations nearby
                            showBikeStationsNearby();
                        }
                    }
                }
            });
        }

    }


    /**
     * Shows next trajectory on map
     */
    public void showNextTrajectoryOnMap(){

        mGoogleMap.clear();

        int trajectoriesCount = ApplicationContext.getInstance().getData().getTrajectoriesCount();

        if(trajectoriesCount == 1) return;

        if(mTrajectoryBeingShowed + 1 == trajectoriesCount){
            showTrajectory(0);
        }
        else{
            showTrajectory(mTrajectoryBeingShowed + 1);
        }
    }

    /**
     * Shows previous trajectory on map
     */
    public void showPreviousTrajectoryOnMap(){

        mGoogleMap.clear();

        int trajectoriesCount = ApplicationContext.getInstance().getData().getTrajectoriesCount();

        if(trajectoriesCount == 1) return;

        if(mTrajectoryBeingShowed == 0){
            showTrajectory(trajectoriesCount - 1);
        }
        else{
            showTrajectory(mTrajectoryBeingShowed - 1);
        }
    }


    public void showTrajectory(int trajectoryID){

        mTrajectoryBeingShowed = trajectoryID;

        String title = "Trajectory view (" + (mTrajectoryBeingShowed + 1) + "/"
                + mTrajectoriesCount + ")";
        getParentActivity().getSupportActionBar().setTitle(title);


        Trajectory trajectory = ApplicationContext.getInstance()
                .getData().getTrajectory(trajectoryID);

        ArrayList<LatLng> route = trajectory.getRoute();

        PolylineOptions polylineOptions =  new PolylineOptions()
                .geodesic(true).color(getResources().getColor(R.color.route_color));

        //adding route inte
        for(LatLng position : route){
            polylineOptions.add(position);
        }

        mGoogleMap.addPolyline(polylineOptions);


        //adding Start marker
        MarkerOptions startMarker = new MarkerOptions()
                .position(route.get(0))
                .title(trajectory.getStartStationName())
                .snippet("Start")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.bike_station));

        mGoogleMap.addMarker(startMarker);


        //adding Finish marker
        MarkerOptions finishMarker = new MarkerOptions()
                .position(route.get(route.size() - 1))
                .title(trajectory.getEndStationName())
                .snippet("Finish")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.bike_station));

        mGoogleMap.addMarker(finishMarker);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(trajectory.getCameraPosition()).zoom(15.0f).build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mGoogleMap.moveCamera(cameraUpdate);
    }

    private void showBikeStationsNearby() {

        getParentActivity().getSupportActionBar().setTitle("Stations nearby");

        ArrayList<BikePickupStation> bikeStations = ApplicationContext.getInstance()
                .getData().getBikeStationsNearby();

        for(BikePickupStation station : bikeStations){

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(station.getStationPosition())
                    .title(station.getStationName())
                    .snippet("Bikes Available: " + station.getBikesAvailable())
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.bike_station));

            mGoogleMap.addMarker(markerOptions);
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(38.735361, -9.142362)).zoom(15.0f).build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mGoogleMap.moveCamera(cameraUpdate);
    }

}
