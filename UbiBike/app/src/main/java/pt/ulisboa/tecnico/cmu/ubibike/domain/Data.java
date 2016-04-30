package pt.ulisboa.tecnico.cmu.ubibike.domain;

import android.text.format.DateUtils;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.GroupChatsNearby;

/**
 * Created by andriy on 12.03.2016.
 */
public class Data {

    private int uid;
    private String username;
    private String sessionToken;
    private String publicKeyToken;

    private GroupChatsNearby mGroupChatsNearby;
    private HashMap<Integer, BikePickupStation> mBikeStations;
    private ArrayList<Trajectory> mTrajectories;
    private Trajectory mLastTrackedTrajectory;
    private Bike mBikeBooked;
    private LatLng mLastPosition;
    private Date dateUpdated;

    private long mTotalPoints;
    private double mTotalDistance;
    private long mTotalTime;

    private Trajectory mLongestRide;


    public Data(int id, String usrn) {
        uid = id;
        username = usrn;

        mGroupChatsNearby =  new GroupChatsNearby();
        mBikeStations = new HashMap<>();
        mTrajectories = new ArrayList<>();
        mLastPosition = new LatLng(0.0, 0.0); //TODO last position
        dateUpdated = new Date();
        mTotalPoints = 0;
        mTotalDistance = 0.0;
        mTotalTime = 0;
    }

    public Data(int uid, String username, String sessionToken, String publicKeyToken,
                ArrayList<BikePickupStation> mBikeStations, ArrayList<Trajectory> mTrajectories,
                LatLng mLastPosition, Date dateUpdated) {

        HashMap<Integer, BikePickupStation> stations = new HashMap<>();

        for(BikePickupStation station : mBikeStations){
            stations.put(station.getSid(), station);
        }

        this.uid = uid;
        this.username = username;
        this.sessionToken = sessionToken;
        this.publicKeyToken = publicKeyToken;

        this.mBikeStations = stations;
        this.mTrajectories = mTrajectories;
        this.mLastPosition = mLastPosition;
        this.dateUpdated = dateUpdated;

        for(Trajectory t : mTrajectories) {
            this.mTotalPoints += t.getPointsEarned();
            this.mTotalDistance += t.getTravelledDistance();
            this.mTotalTime += (t.getEndTime().getTime() - t.getStartTime().getTime());
            if(mLongestRide.getTravelledDistance() < t.getTravelledDistance()) {
                mLongestRide = t;
            }
        }
    }



    /**
     * Gets bike stations with bikes available to pickup
     *
     * @return - list of stations
     */
    public ArrayList<BikePickupStation> getBikeStations() {
        return new ArrayList<>(mBikeStations.values());
    }

    /**
     * Gets bike stations with a given ID
     *
     * @param sid - station ID
     * @return - BikePickupStation object
     */
    public BikePickupStation getBikePickupStationById(int sid){
        return mBikeStations.get(sid);
    }

    /**
     * Gets a Trajectory given an id
     *
     * @param trajectoryID - id
     * @return - Trajectory
     */
    public Trajectory getTrajectory(int trajectoryID) {
        return mTrajectories.get(new Integer(trajectoryID));
    }


    /**
     * Adds a given trajectory to collection
     *
     * @param newTrajectory - Trajectory to add
     */
    public void addTrajectory(Trajectory newTrajectory){
        mTrajectories.add(newTrajectory);

        if(newTrajectory.getTravelledDistance() > mLongestRide.getTravelledDistance()){
            mLongestRide = newTrajectory;
        }

        mTotalDistance += newTrajectory.getTravelledDistance();
        mTotalPoints += newTrajectory.getPointsEarned();
        mTotalTime += (newTrajectory.getEndTime().getTime() - newTrajectory.getStartTime().getTime());
    }

    /**
     * @return - number of trajectories
     */
    public int getTrajectoriesCount() {
        return mTrajectories.size();
    }


    /**
     * Gets all past trajectories sorted by the time they finished
     *
     * @return - trajectories list
     */
    public ArrayList<Trajectory> getAllTrajectories() {

        ArrayList<Trajectory> trajectories = mTrajectories;
        Collections.sort(trajectories);

        return trajectories;
    }

    /**
     * Sets last GPS synced position to given one
     *
     * @param latitude  - coordinate
     * @param longitude - coordinate
     */
    public void setLastPosition(double latitude, double longitude) {
        mLastPosition = new LatLng(latitude, longitude);
    }

    /**
     * @return - last GPS synced position
     */
    public LatLng getLastPosition() {
        return new LatLng(38.737681, -9.138382);    //TODO hardcoded
    }

    /**
     * Sets booked bike
     *
     * @param bike - Bike object to add to collection
     */
    public void setBikeBooked(Bike bike){
        mBikeBooked = bike;
    }

    public Bike getBikeBooked(){
        return mBikeBooked;
    }

    public Date getLastUpdated() {
        return dateUpdated;
    }

    public String getLastUpdatedRelativeString(){
        return DateUtils.getRelativeTimeSpanString(dateUpdated.getTime()).toString();
    }

    public void setLastUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getPublicKeyToken() {
        return publicKeyToken;
    }

    public boolean hasPublicKeyToken(){ return publicKeyToken != null; }

    public void setPublicKeyToken(String publicKeyToken) {
        this.publicKeyToken = publicKeyToken;
    }

    public void setTrajectories(ArrayList<Trajectory> trajectories) {
        mTrajectories = trajectories;
    }

    public Trajectory getLastTrackedTrajectory() {
        return mLastTrackedTrajectory;
    }

    public void setLastTrackedTrajectory(Trajectory mLastTrackedTrajectory) {
        this.mLastTrackedTrajectory = mLastTrackedTrajectory;
    }

    public void setBikeStations(ArrayList<BikePickupStation> bikeStations) {
        HashMap<Integer, BikePickupStation> stations = new HashMap<>();

        for(BikePickupStation station : bikeStations){
            stations.put(station.getSid(), station);
        }

        mBikeStations = stations;
    }

    public int getNextTrajectoryID(){
        int maxId = 0;

        for(Trajectory t : mTrajectories){
            maxId = (t.getTrajectoryID() > maxId) ? t.getTrajectoryID() : maxId;
        }

        return ++maxId;
    }


    public GroupChatsNearby getGroupChatsNearby() {
        return mGroupChatsNearby;
    }

    public void setGroupChatsNearby(GroupChatsNearby mGroupChatsNearby) {
        this.mGroupChatsNearby = mGroupChatsNearby;
    }

    public long getTotalPoints() {
        return mTotalPoints;
    }

    public double getTotalDistance() {
        return mTotalDistance;
    }

    public double getTotalHours() {
        return mTotalTime;
    }

    public int getTotalRides() {
        return mTrajectories.size();
    }

    public Trajectory getLongestRide() {
        return mLongestRide;
    }

}
