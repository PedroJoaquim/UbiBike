package pt.ulisboa.tecnico.cmu.ubibike.fragments.maps;


import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.R;
import pt.ulisboa.tecnico.cmu.ubibike.domain.BikePickupStation;
import pt.ulisboa.tecnico.cmu.ubibike.managers.MobileConnectionManager;

public class StationsNearbyMapFragment extends MapFragment {

    private HashMap<String, BikePickupStation> mMarkerStation = new HashMap<>(); //key = marker ID
    private int mCurrentSelectedStation;
    private Marker mCurrentPositionMarker;


    @Override
    protected void onCreateSpecific() {
        //empty on purpose
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
    protected void setUIElements() {

        FrameLayout nextTrajectory = (FrameLayout) mView.findViewById(R.id.next_trajectory_frame);
        FrameLayout previousTrajectory = (FrameLayout) mView.findViewById(R.id.prev_trajectory_frame);
        FrameLayout trajectoryInfo = (FrameLayout) mView.findViewById(R.id.trajectory_info_frame);

        RelativeLayout bookBikeLayout = (RelativeLayout) mView.findViewById(R.id.book_bike);
        ImageButton undo_booking = (ImageButton) mView.findViewById(R.id.undo_imageButton);


        nextTrajectory.setVisibility(View.INVISIBLE);
        previousTrajectory.setVisibility(View.INVISIBLE);
        trajectoryInfo.setVisibility(View.INVISIBLE);

        bookBikeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!MobileConnectionManager.isOnline(getActivity())){
                    Toast.makeText(getActivity(), "Check your internet connection first.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (ApplicationContext.getInstance().getData().isAnyBikeBooked()) {
                    Toast.makeText(getActivity(), "There is already an active booking", Toast.LENGTH_SHORT).show();
                } else {
                    ApplicationContext.getInstance().getServerCommunicationHandler().
                            performBikeBookRequest(mCurrentSelectedStation);
                }
            }
        });

        undo_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO confirm logic

                if(!MobileConnectionManager.isOnline(getActivity())){
                    Toast.makeText(getActivity(), "Check your internet connection first.", Toast.LENGTH_SHORT).show();
                    return;
                }

                ApplicationContext.getInstance().getServerCommunicationHandler().
                        performBikeUnbookRequest();
            }
        });

        updateBookingButton();
    }

    @Override
    protected void showSpecificMap() {
        showBikeStationsNearby();
    }

    @Override
    public void updateUI() {
        updateBookingButton();

        LatLng lastPosition = ApplicationContext.getInstance().getData().getLastPosition();
        mCurrentPositionMarker.setPosition(lastPosition);


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(lastPosition).zoom(15.0f).build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mGoogleMap.moveCamera(cameraUpdate);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem item = menu.findItem(R.id.action_upload_trajectory);
        item.setVisible(false); //TODO
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

        mCurrentPositionMarker = mGoogleMap.addMarker(currentPositionMarker);

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


                if(!ApplicationContext.getInstance().getData().isAnyBikeBooked()) {
                    RelativeLayout bookBike = (RelativeLayout) mView.findViewById(R.id.book_bike);

                    if (station.getBikesAvailableQuantity() > 0) {
                        bookBike.setVisibility(View.VISIBLE);
                    } else {
                        bookBike.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "No bikes available on selected station.", Toast.LENGTH_SHORT).show();
                    }

                }

                return false;
            }
        });

    }


    private void updateBookingButton(){

        RelativeLayout bookBikeLayout = (RelativeLayout) mView.findViewById(R.id.book_bike);
        RelativeLayout bookedBikeLayout = (RelativeLayout) mView.findViewById(R.id.booked_bike);

        boolean isBikeBooked = ApplicationContext.getInstance().getData().isAnyBikeBooked();

        if(isBikeBooked){
            bookBikeLayout.setVisibility(View.GONE);
            bookedBikeLayout.setVisibility(View.VISIBLE);

            int bikeSid = ApplicationContext.getInstance().getData().getBikeBooked().getSid();
            String stationName = ApplicationContext.getInstance().getData().
                    getBikePickupStationById(bikeSid).getStationName();

            TextView booked_textView = (TextView) mView.findViewById(R.id.booked_bike_textView);
            booked_textView.setText("Bike booked");

            TextView booked_station_textView = (TextView) mView.findViewById(R.id.booked_bike_station_textView);
            booked_station_textView.setText("Station: " + stationName);
        }
        else{
            bookedBikeLayout.setVisibility(View.GONE);
        }
    }
}
