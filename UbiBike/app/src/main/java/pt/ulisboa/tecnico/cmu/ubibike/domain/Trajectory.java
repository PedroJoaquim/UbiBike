package pt.ulisboa.tecnico.cmu.ubibike.domain;

import android.text.format.DateUtils;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import pt.ulisboa.tecnico.cmu.ubibike.utils.SphericalUtil;


/**
 * Represents a bike ride - trajectory
 * Can be compared to other Trajectory by time both trajectories were ago
 */
public class Trajectory implements Comparable<Trajectory> {

    private int mTrajectoryID;
    private int mStartStationID;
    private int mEndStationID;
    private ArrayList<LatLng> mTrajectoryPositions;
    private LatLng mCameraPosition;
    private double mDistance;
    private Date mStartTime;
    private Date mEndTime;
    private int mPointsEarned;

    /**
     * Starting a new Trajectory
     *
     * @param routeID - id
     * @param startStationID - start station ID
     * @param startLatitude - coordinate
     * @param startLongitude - coordinate
     */
    public Trajectory(int routeID, int startStationID, double startLatitude, double startLongitude) {
        mTrajectoryID = routeID;
        mStartStationID = startStationID;
        mTrajectoryPositions = new ArrayList<>();
        mTrajectoryPositions.add(new LatLng(startLatitude, startLongitude));
        mStartTime = new Date();
        mDistance = 0.0;
    }

    /**
     * Rebuilding a past trajectory given its details
     *
     * @param routeID - id
     * @param startStationID - start station ID
     * @param endStationID - end station ID
     * @param route - list of positions
     * @param startTime - start time
     * @param endTime - end time
     */
    public Trajectory(int routeID, int startStationID, int endStationID, ArrayList<LatLng> route, double distance, Date startTime, Date endTime) {
        mTrajectoryID = routeID;
        mStartStationID = startStationID;
        mEndStationID = endStationID;
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
     * @return - current Trajectory ID
     */
    public int getTrajectoryID(){
        return mTrajectoryID;
    }

    /**
     * Adds route new position
     *
     * @param latitude - coordinates
     * @param longitude - coordinates
     */
    public void addRoutePosition(double latitude, double longitude){

        LatLng newPosition = new LatLng(latitude, longitude);

        LatLng lastPosition = mTrajectoryPositions.get(mTrajectoryPositions.size() - 1);
        mDistance += SphericalUtil.computeDistanceBetween(lastPosition, newPosition);

        mTrajectoryPositions.add(newPosition);

        LatLng oldPosition = mTrajectoryPositions.get(mTrajectoryPositions.size() - 1);
        double traveledDistance = SphericalUtil.computeDistanceBetween(oldPosition, newPosition);

        mDistance += traveledDistance;
    }

    /**
     * Finishes current trajectory
     */
    public void finishRoute(){
        LatLng startPosition = mTrajectoryPositions.get(0);
        LatLng endPosition = mTrajectoryPositions.get(mTrajectoryPositions.size() - 1);
        mCameraPosition = SphericalUtil.interpolate(startPosition, endPosition, 0.5);
        mEndTime = new Date();

    }

    /**
     * Sets finish station id
     *
     * @param id -  end station id
     */
    public void setEndStationID(int id){
        mEndStationID = id;
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
     * Gets last route registered position
     *
     * @return
     */
    public LatLng getLastPosition(){
        return mTrajectoryPositions.get(mTrajectoryPositions.size() - 1);
    }

    /**
     * @return - route start station id
     */
    public int getStartStationID() {
        return mStartStationID;
    }

    /**
     * @return - route end station name
     */
    public int getEndStationID() {
        return mEndStationID;
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
    public double getTravelledDistance(){
        return mDistance;
    }

    /**
     * @return - Distance, in km, travelled in current route
     */
    public double getTravelledDistanceInKm(){
        return mDistance * 0.001;
    }

    /**
     * @return - Points earned with current ride
     */
    public int getPointsEarned(){
        return mPointsEarned;
    }


    /**
     * @return - Time when route has been started
     */
    public Date getStartTime() {
        return mStartTime;
    }

    /**
     * @return - Time when route has been finished
     */
    public Date getEndTime(){
        return mEndTime;
    }



    /**
     * @return - Travel time in hours:minutes:seconds format
     */
    public String getReadableTravelTime(){
        int time = (int) (mEndTime.getTime() - mStartTime.getTime());

        return new SimpleDateFormat("HH:mm:ss").format(new Date(time)).toString();
    }

    /**
     * @return - Time the ride was ago in readable format, ex. "2 days ago"
     */
    public String getReadableFinishTime(){
        return DateUtils.getRelativeTimeSpanString(mEndTime.getTime()).toString();
    }


    /**
     * @return - double value of the zoom to use when showing current trajectory on map
     */
    public double getOptimalZoom(){
        LatLng firstPosition = mTrajectoryPositions.get(0);
        LatLng lastPosition = mTrajectoryPositions.get(mTrajectoryPositions.size() - 1);

        //TODO find appropriate function

        double distance = SphericalUtil.computeDistanceBetween(firstPosition, lastPosition);

        return 15.0;
    }


    /**
     * Compares current trajectory to a given one by the times they have been finished
     * Oldest one is greater
     *
     * @param anotherTrajectory - trajectory to compare to
     * @return - positive number if current trajectory is older
     */
    @Override
    public int compareTo(Trajectory anotherTrajectory) {
        Date now = new Date();

        int thisAgo = (int) (now.getTime() - mEndTime.getTime());
        int anotherAgo = (int) (now.getTime() - anotherTrajectory.getEndTime().getTime());

        return thisAgo - anotherAgo;
    }

}
