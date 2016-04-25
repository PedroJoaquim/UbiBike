package pt.ulisboa.tecnico.cmu.ubibike.services;

import android.Manifest;
import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.logging.Handler;

import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.domain.BikePickupStation;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Trajectory;
import pt.ulisboa.tecnico.cmu.ubibike.utils.JsonParser;
import pt.ulisboa.tecnico.cmu.ubibike.utils.SphericalUtil;

public class TrajectoryTracker extends Service implements LocationListener {


    public static final String TRACKING_FINISH_INTENT_FILTER= "TRACKING_FINISH_FILTER";
    public static final String TRAJECTORY_INFO_JSON = "trajectory_info_json";
    public static final String TRAJECTORY_ID = "trajectory_id";
    public static final String START_STATION_ID = "start_station_id";
    public static final String START_STATION_LATITUDE = "start_station_latitude";
    public static final String START_STATION_LONGITUDE= "start_station_longitude";

    private static final long TRACKING_TIME_INTERVAL = 1000;    //in milisseconds
    private static final float TRACKING_MINIMUM_DISTANCE = 5;   //in meters

    private Intent mIntent;

    private Location mLastPosition;
    private StopTrajectoryTrackingReceiver mStopTrackingReceiver;
    private boolean mStopTracking = false;
    private boolean mPositionChanged;
    private boolean mInformationReceived = false;



    @Override
    public void onCreate() {
        super.onCreate();


        LocationManager lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //ignore, as permissions are set
            return;
        }
        lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TRACKING_TIME_INTERVAL,TRACKING_MINIMUM_DISTANCE, this);

        mLastPosition = lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(mLastPosition != null) {
            Toast.makeText(TrajectoryTracker.this, "Last Position: " + mLastPosition.toString(), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(TrajectoryTracker.this, "Last Position = null", Toast.LENGTH_SHORT).show();
            mLastPosition = new Location(LocationManager.GPS_PROVIDER);
        }


        //registering receiver
        IntentFilter filter = new IntentFilter(StopTrajectoryTrackingReceiver.ACTION_STOP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        mStopTrackingReceiver = new StopTrajectoryTrackingReceiver();
        registerReceiver(mStopTrackingReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mStopTrackingReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mIntent = intent;

        MyRunnable mainBackgroundRunnable = new MyRunnable();
        Thread thread = new Thread(mainBackgroundRunnable);
        thread.start();
        Toast.makeText(TrajectoryTracker.this, "Thread started", Toast.LENGTH_SHORT).show();

        return START_NOT_STICKY;
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d("UbiBike", "NewLocation");

        if(mLastPosition != location){
            mLastPosition = location;
            mPositionChanged = true;
        }
        else{
            mPositionChanged = false;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(TrajectoryTracker.this, "OnStatusChanged", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(TrajectoryTracker.this, "OnProviderEnabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(TrajectoryTracker.this, "OnProviderDisabled", Toast.LENGTH_SHORT).show();
    }



    public class StopTrajectoryTrackingReceiver extends BroadcastReceiver {

        public static final String ACTION_STOP = "stop";

        @Override
        public void onReceive(Context context, Intent intent) {
            mStopTracking = true;
        }
    }

    public class InformationReceivedReceiver extends BroadcastReceiver {

        public static final String ACTION_RECEIVED = "received";

        @Override
        public void onReceive(Context context, Intent intent) {
            mInformationReceived = true;
        }
    }


    private class MyRunnable implements Runnable {

        public void run() {

            Bundle extras = mIntent.getExtras();
            int trajectoryID = extras.getInt(TRAJECTORY_ID);
            int startStationID = extras.getInt(START_STATION_ID);
            double startLatitude = extras.getDouble(START_STATION_LATITUDE);
            double startLongitude = extras.getDouble(START_STATION_LONGITUDE);

            Trajectory trajectory = new Trajectory(trajectoryID, startStationID, startLatitude, startLongitude);

            //main tracking loop
            while(!mStopTracking){

                if(mPositionChanged) {

                    trajectory.addRoutePosition(mLastPosition.getLatitude(), mLastPosition.getLongitude());

                    Log.d("UbiBike", "Trajectory position added [" + trajectory.getRoute().size() + "]");

                    mPositionChanged = false;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }



            trajectory.finishRoute();


            //finding which station is the nearest station to register as "end station"
            //the closer the station to last registered position
            //the lower is the value: abs(pos.latitude - station.latitude) + abs(pos.longitude - station.longitude)
            double min_lat_long_sum = 0.0;
            int closer_sid = -1;
            LatLng last_pos = trajectory.getLastPosition();

            for(BikePickupStation station : ApplicationContext.getInstance().getData().getBikeStations()){

                double abs_lat = Math.abs(last_pos.latitude - station.getStationPosition().latitude);
                double abs_long = Math.abs(last_pos.longitude - station.getStationPosition().longitude);
                double current_lat_long_sum = abs_lat + abs_long;

                if(closer_sid == -1){
                    closer_sid = station.getSid();
                    min_lat_long_sum = current_lat_long_sum;
                    continue;
                }
                else if(current_lat_long_sum < min_lat_long_sum){
                    min_lat_long_sum = current_lat_long_sum;
                    closer_sid = station.getSid();
                }
            }

            trajectory.setEndStationID(closer_sid);

            JSONObject json = JsonParser.buildTrajectoryPostRequestJson(
                    trajectory.getTrajectoryID(),
                    trajectory.getStartStationID(),
                    trajectory.getEndStationID(),
                    trajectory.getRoute(),
                    (int) trajectory.getStartTime().getTime(),
                    (int) trajectory.getEndTime().getTime(),
                    trajectory.getTravelledDistance());

            Log.d("UbiBike", json.toString());


            Log.d("UbiBike", "Thread stopped");

            ApplicationContext.getInstance().getData().setLastTrackedTrajectory(trajectory);

            stopSelf();

        }
    };
}
