package pt.ulisboa.tecnico.cmu.ubibike.fragments.maps;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.R;
import pt.ulisboa.tecnico.cmu.ubibike.domain.BikePickupStation;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Trajectory;

public class TrajectoryViewMapFragment extends MapFragment {

    private int mTrajectoryBeingShowed;
    private int mTrajectoriesCount;

    private boolean mShowTrajectoryInfo;

    public static final String TRAJECTORY_ID_KEY = "trajectory_id";

    @Override
    protected void onCreateSpecific(){

        mShowTrajectoryInfo = false;

        mTrajectoryBeingShowed = getArguments().getInt(TRAJECTORY_ID_KEY);
        mTrajectoriesCount =  ApplicationContext.getInstance().getData().getTrajectoriesCount();

    }

    @Override
    public void onResume(){
        super.onResume();

        ApplicationContext.getInstance().setCurrentFragment(this);
    }

    @Override
    public void onPause(){
        super.onPause();

        ApplicationContext.getInstance().setCurrentFragment(null);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem item = menu.findItem(R.id.action_upload_trajectory);
        item.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_upload_trajectory:

                Trajectory t = ApplicationContext.getInstance().getData().getTrajectory(mTrajectoryBeingShowed);

                ApplicationContext.getInstance().getServerCommunicationHandler().
                        performTrajectoryPostRequest(t);


                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    protected void setUIElements(){
        FrameLayout nextTrajectory = (FrameLayout) mView.findViewById(R.id.next_trajectory_frame);
        FrameLayout previousTrajectory = (FrameLayout) mView.findViewById(R.id.prev_trajectory_frame);
        FrameLayout trajectoryInfo = (FrameLayout) mView.findViewById(R.id.trajectory_info_frame);


        //hide prev / next trajectory buttons when showing tracked trajectory

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


        trajectoryInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowTrajectoryInfo = !mShowTrajectoryInfo;
                mView.findViewById(R.id.trajectory_info)
                        .setVisibility(mShowTrajectoryInfo ? View.VISIBLE : View.INVISIBLE);
            }
        });

    }

    @Override
    protected void showSpecificMap() {
        showTrajectory(mTrajectoryBeingShowed);
    }

    @Override
    public void updateUI() {

    }


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


}
