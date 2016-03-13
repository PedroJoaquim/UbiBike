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

    private SupportMapFragment mSupportMapFragment;

    private int mTrajectoryBeingShowed;

    private UbiBike getParentActivity(){
        return (UbiBike) getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.map_fragment, null, false);

        setViewElements(v);

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

                        googleMap.getUiSettings().setAllGesturesEnabled(true);


                        CameraPosition cameraPosition;

                        if(getArguments() != null){ //check if we are on trajectory view

                            mTrajectoryBeingShowed = getArguments().getInt("trajectoryID");
                            Trajectory trajectory = ApplicationContext.getInstance()
                                                    .getData().getTrajectory(mTrajectoryBeingShowed);

                            ArrayList<LatLng> route = trajectory.getRoute();

                            PolylineOptions polylineOptions =  new PolylineOptions()
                                    .geodesic(true).color(getResources().getColor(R.color.route_color));

                            //adding route inte
                            for(LatLng position : route){
                                polylineOptions.add(position);
                            }

                            googleMap.addPolyline(polylineOptions);


                            //adding Start marker
                            MarkerOptions startMarker = new MarkerOptions()
                                    .position(route.get(0))
                                    .title(trajectory.getStartStationName())
                                    .snippet("Start")
                                    .icon(BitmapDescriptorFactory
                                            .fromResource(R.drawable.bike_station));

                            googleMap.addMarker(startMarker);


                            //adding Finish marker
                            MarkerOptions finishMarker = new MarkerOptions()
                                    .position(route.get(route.size() - 1))
                                    .title(trajectory.getEndStationName())
                                    .snippet("Finish")
                                    .icon(BitmapDescriptorFactory
                                            .fromResource(R.drawable.bike_station));

                            googleMap.addMarker(finishMarker);

                            cameraPosition = new CameraPosition.Builder()
                                    .target(trajectory.getCameraPosition()).zoom(15.0f).build();

                        }
                        else{

                            ArrayList<BikePickupStation> bikeStations = ApplicationContext.getInstance()
                                    .getData().getBikeStationsNearby();

                            for(BikePickupStation station : bikeStations){

                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(station.getStationPosition())
                                        .title(station.getStationName())
                                        .snippet("Bikes Available: " + station.getBikesAvailable())
                                        .icon(BitmapDescriptorFactory
                                                .fromResource(R.drawable.bike_station));

                                googleMap.addMarker(markerOptions);
                            }

                             cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(38.735361, -9.142362)).zoom(15.0f).build();
                        }

                        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

                        googleMap.moveCamera(cameraUpdate);

                    }
                }
            });
        }

        return v;
    }

    private void setViewElements(View v) {

        FrameLayout nextTrajectory = (FrameLayout) v.findViewById(R.id.next_trajectory_frame);
        FrameLayout previousTrajectory = (FrameLayout) v.findViewById(R.id.prev_trajectory_frame);

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


    /**
     * Shows next trajectory on map
     */
    public void showNextTrajectoryOnMap(){

        int trajectoriesCount = ApplicationContext.getInstance().getData().getTrajectoriesCount();

        if(trajectoriesCount == 1) return;

        if(mTrajectoryBeingShowed + 1 == trajectoriesCount){
            getParentActivity().showTrajectoryOnMap(0, false);
        }
        else{
            getParentActivity().showTrajectoryOnMap(mTrajectoryBeingShowed + 1, false);
        }
    }

    /**
     * Shows previous trajectory on map
     */
    public void showPreviousTrajectoryOnMap(){

        int trajectoriesCount = ApplicationContext.getInstance().getData().getTrajectoriesCount();

        if(trajectoriesCount == 1) return;

        if(mTrajectoryBeingShowed == 0){
            getParentActivity().showTrajectoryOnMap(trajectoriesCount - 1, false);
        }
        else{
            getParentActivity().showTrajectoryOnMap(mTrajectoryBeingShowed - 1, false);
        }
    }

}
