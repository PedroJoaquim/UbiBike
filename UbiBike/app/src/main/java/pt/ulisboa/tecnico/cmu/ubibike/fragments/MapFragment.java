package pt.ulisboa.tecnico.cmu.ubibike.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;

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

    private View mView;

    private boolean mTrajectoryView;
    private boolean mTrackedTrajectoryView;
    private boolean mShowTrajectoryInfo;

    private HashMap<String, BikePickupStation> mMarkerStation = new HashMap<>(); //key = marker ID
    private int mCurrentSelectedStation;


    public static final String TRAJECTORY_ID_KEY = "trajectory_id";
    public static final String TRACKED_TRAJECTORY_VIEW_KEY = "tracked_trajectory_view";

    private UbiBike getParentActivity(){
        return (UbiBike) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getParentActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mView =  inflater.inflate(R.layout.map_fragment, null, false);

        setHasOptionsMenu(true);
        getParentActivity().invalidateOptionsMenu();

        if(getArguments() != null) { //check if we are on trajectory view

            mTrajectoryView = true;
            mShowTrajectoryInfo = false;

            mTrajectoryBeingShowed = getArguments().getInt(TRAJECTORY_ID_KEY);
            mTrackedTrajectoryView = getArguments().getBoolean(TRACKED_TRAJECTORY_VIEW_KEY);
            mTrajectoriesCount =  ApplicationContext.getInstance().getData().getTrajectoriesCount();
        }
        else{
            mTrajectoryView = false;
        }


        setMap();

        setViewElements();

        return mView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_map_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem item = menu.findItem(R.id.action_logout);
        item.setVisible(false);

        item = menu.findItem(R.id.action_upload_trajectory);
        item.setVisible(mTrackedTrajectoryView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_upload_trajectory:
                Trajectory t = ApplicationContext.getInstance().getData().getTrajectory(mTrajectoryBeingShowed);

                ApplicationContext.getInstance().getServerCommunicationHandler().
                        performTrajectoryPostRequest(mTrajectoryBeingShowed,
                                                    t.getStartStationID(),
                                                    t.getEndStationID(),
                                                    t.getRoute(),
                                                    (int) t.getStartTime().getTime(),
                                                    (int) t.getEndTime().getTime(),
                                                    t.getTravelledDistance());


                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setViewElements() {

        FrameLayout nextTrajectory = (FrameLayout) mView.findViewById(R.id.next_trajectory_frame);
        FrameLayout previousTrajectory = (FrameLayout) mView.findViewById(R.id.prev_trajectory_frame);
        FrameLayout trajectoryInfo = (FrameLayout) mView.findViewById(R.id.trajectory_info_frame);
        RelativeLayout bookBike = (RelativeLayout) mView.findViewById(R.id.book_bike);

        if(mTrajectoryView) {

            //hide prev / next trajectory buttons when showing tracked trajectory
            if(!mTrackedTrajectoryView) {
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

            trajectoryInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mShowTrajectoryInfo = !mShowTrajectoryInfo;
                    mView.findViewById(R.id.trajectory_info)
                            .setVisibility(mShowTrajectoryInfo ? View.VISIBLE : View.INVISIBLE);
                }
            });
        }
        else{
            nextTrajectory.setVisibility(View.INVISIBLE);
            previousTrajectory.setVisibility(View.INVISIBLE);
            trajectoryInfo.setVisibility(View.INVISIBLE);

            bookBike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ApplicationContext.getInstance().getServerCommunicationHandler().
                            performBikeBookRequest(mCurrentSelectedStation);

                }
            });
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


        BikePickupStation startStation = ApplicationContext.getInstance().getData().
                getBikePickupStationById(trajectory.getStartStationID());

        BikePickupStation endStation = ApplicationContext.getInstance().getData().
                getBikePickupStationById(trajectory.getEndStationID());


        //adding Start marker
        MarkerOptions startMarker = new MarkerOptions()
                .position(route.get(0))
                .title(startStation.getStationName())
                .snippet("Start")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.bike_station));

        mGoogleMap.addMarker(startMarker);


        //adding Finish marker
        MarkerOptions finishMarker = new MarkerOptions()
                .position(route.get(route.size() - 1))
                .title(endStation.getStationName())
                .snippet("Finish")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.bike_station));

        mGoogleMap.addMarker(finishMarker);

        double optimalZoom = trajectory.getOptimalZoom();

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(trajectory.getCameraPosition()).zoom((float) optimalZoom).build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mGoogleMap.moveCamera(cameraUpdate);


        LayoutInflater inflater = (LayoutInflater) getParentActivity().getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.trajectories_list_row, null);

        TextView from = (TextView) view.findViewById(R.id.from_station_name_textView);
        TextView to = (TextView) view.findViewById(R.id.to_station_name_textView);
        TextView distance = (TextView) view.findViewById(R.id.distance_textView);
        TextView points = (TextView) view.findViewById(R.id.points_textView);
        TextView time = (TextView) view.findViewById(R.id.time_textView);
        TextView timeAgo = (TextView) view.findViewById(R.id.time_ago_textView);

        from.setText(startStation.getStationName());
        to.setText(endStation.getStationName());
        distance.setText(String.format("%.3f km", trajectory.getTravelledDistanceInKm()));
        points.setText(String.valueOf(trajectory.getPointsEarned()));
        time.setText(trajectory.getReadableTravelTime());
        timeAgo.setText(trajectory.getReadableFinishTime());

        RelativeLayout trajectoryInfoFrame = (RelativeLayout) mView.findViewById(R.id.trajectory_info);
        trajectoryInfoFrame.removeAllViews();
        trajectoryInfoFrame.addView(view);

        trajectoryInfoFrame.setVisibility(mShowTrajectoryInfo ? View.VISIBLE : View.INVISIBLE);
    }

    private void showBikeStationsNearby() {

        getParentActivity().getSupportActionBar().setTitle("Stations nearby");

        ArrayList<BikePickupStation> bikeStations = ApplicationContext.getInstance()
                .getData().getBikeStations();

        LatLng lastObtainedPosition = ApplicationContext.getInstance().getData().getLastPosition();

        //adding current position to map
        MarkerOptions currentPositionMarker = new MarkerOptions()
                .position(lastObtainedPosition)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.current_position_marker));

        Marker pos = mGoogleMap.addMarker(currentPositionMarker);
        String id = pos.getId();


        //adding stations to map
        for(BikePickupStation station : bikeStations){

            MarkerOptions stationMarker = new MarkerOptions()
                    .position(station.getStationPosition())
                    .title(station.getStationName())
                    .snippet("Bikes Available: " + station.getBikesAvailableQuantity())
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.bike_station));


            Marker marker = mGoogleMap.addMarker(stationMarker);

            mMarkerStation.put(marker.getId(), station);
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(lastObtainedPosition).zoom(15.0f).build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mGoogleMap.moveCamera(cameraUpdate);


        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                BikePickupStation station = mMarkerStation.get(marker.getId());

                if(station == null) { //selected marker is not a bike station
                    return false;
                }

                mCurrentSelectedStation = station.getSid();


                RelativeLayout bookBike = (RelativeLayout) mView.findViewById(R.id.book_bike);

                if(station.getBikesAvailableQuantity() > 0){
                    bookBike.setVisibility(View.VISIBLE);
                }
                else{
                    bookBike.setVisibility(View.GONE);
                }

                return false;
            }
        });

    }

}
