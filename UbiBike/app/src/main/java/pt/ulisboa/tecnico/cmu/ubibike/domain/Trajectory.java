package pt.ulisboa.tecnico.cmu.ubibike.domain;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmu.ubibike.utils.SphericalUtil;

/**
 * Created by andriy on 12.03.2016.
 */
public class Trajectory {

    private int mTrajectoryID;
    private String mStartStation;
    private String mEndStation;
    private ArrayList<LatLng> mTrajectoryPositions;
    private LatLng mCameraPosition;
    private double mDistance;

    public Trajectory(int routeID, String startStation, double startLatitude, double startLongitude) {
        mTrajectoryID = routeID;
        mStartStation = startStation;
        mTrajectoryPositions = new ArrayList<>();
        mTrajectoryPositions.add(new LatLng(startLatitude, startLongitude));
    }

    public Trajectory(int routeID, String startStation, String endStation, ArrayList<LatLng> route) {
        mTrajectoryID = routeID;
        mStartStation = startStation;
        mEndStation = endStation;
        mTrajectoryPositions = route;

        LatLng startPosition = route.get(0);
        LatLng endPosition = route.get(route.size() - 1);
        double cameraLatitude;
        double cameraLongitude;

        if(startPosition.latitude > endPosition.latitude){
            cameraLatitude = startPosition.latitude  - endPosition.latitude;
        }
        else{
            cameraLatitude = endPosition.latitude - startPosition.latitude;
        }

        if(startPosition.longitude > endPosition.longitude){
            cameraLongitude = startPosition.longitude - endPosition.longitude;
        }
        else{
            cameraLongitude = endPosition.longitude - startPosition.longitude;
        }


        mCameraPosition = SphericalUtil.interpolate(startPosition, endPosition, 0.5);
    }

    /**
     * Adds route new position
     *
     * @param latitude - coordinates
     * @param longitude - coordinates
     */
    public void addRoutePosition(double latitude, double longitude, boolean lastPosition){
        mTrajectoryPositions.add(new LatLng(latitude, longitude));

        if(lastPosition){
            LatLng startPosition = mTrajectoryPositions.get(0);
            LatLng endPosition = mTrajectoryPositions.get(mTrajectoryPositions.size() - 1);
            mCameraPosition = SphericalUtil.interpolate(startPosition, endPosition, 0.5);
        }
    }

    /**
     * Sets finish station name
     *
     * @param stationName - name to set
     */
    public void setEndStationName(String stationName){
        mEndStation = stationName;
    }


    /**
     * Gets route of current ride - set of positions passed through
     *
     * @return - list of coordinates
     */
    public ArrayList<LatLng> getRoute() {
        return mTrajectoryPositions;
    }


    /**
     * @return - route start station name
     */
    public String getStartStationName() {
        return mStartStation;
    }

    /**
     * @return - route end station name
     */
    public String getEndStationName() {
        return mEndStation;
    }

    /**
     * @return - Position to point camera to when app is showed up with current trajectory
     */
    public LatLng getCameraPosition(){
        return mCameraPosition;
    }
}
