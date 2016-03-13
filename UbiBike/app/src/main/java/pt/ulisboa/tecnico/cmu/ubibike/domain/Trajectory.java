package pt.ulisboa.tecnico.cmu.ubibike.domain;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;

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
    private Date mStartTime;
    private Date mEndTime;
    private Date mTravelTime;
    private int mPointsEarned;

    /**
     * Starting a new Trajectory
     *
     * @param routeID - id
     * @param startStation - start station
     * @param startLatitude - coordinate
     * @param startLongitude - coordinate
     */
    public Trajectory(int routeID, String startStation, double startLatitude, double startLongitude) {
        mTrajectoryID = routeID;
        mStartStation = startStation;
        mTrajectoryPositions = new ArrayList<>();
        mTrajectoryPositions.add(new LatLng(startLatitude, startLongitude));
        mStartTime = new Date();
        mDistance = 0.0;
    }

    /**
     * Rebuilding a past trajectory given its details
     *
     * @param routeID - id
     * @param startStation - coordinate
     * @param endStation - coordinate
     * @param route - list of positions
     * @param startTime - start time
     * @param endTime - end time
     */
    public Trajectory(int routeID, String startStation, String endStation, ArrayList<LatLng> route, double distance, Date startTime, Date endTime) {
        mTrajectoryID = routeID;
        mStartStation = startStation;
        mEndStation = endStation;
        mTrajectoryPositions = route;
        mDistance = distance;
        mStartTime = startTime;
        mEndTime = endTime;
        mPointsEarned = (int) mDistance / 10;

        LatLng startPosition = route.get(0);
        LatLng endPosition = route.get(route.size() - 1);
        mCameraPosition = SphericalUtil.interpolate(startPosition, endPosition, 0.5);
    }

    /**
     * Adds route new position
     *
     * @param latitude - coordinates
     * @param longitude - coordinates
     */
    public void addRoutePosition(double latitude, double longitude, boolean finishPosition){

        LatLng newPosition = new LatLng(latitude, longitude);

        LatLng lastPosition = mTrajectoryPositions.get(mTrajectoryPositions.size() - 1);
        mDistance += SphericalUtil.computeDistanceBetween(lastPosition, newPosition);

        mTrajectoryPositions.add(newPosition);

        if(finishPosition){
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

    /**
     * @return - Distance, in meters, travelled in current route
     */
    public double getTraveledDistance(){
        return mDistance;
    }
}
