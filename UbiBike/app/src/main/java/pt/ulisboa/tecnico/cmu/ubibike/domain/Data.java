package pt.ulisboa.tecnico.cmu.ubibike.domain;

import android.text.format.DateUtils;

import com.google.android.gms.maps.model.LatLng;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by andriy on 12.03.2016.
 */
public class Data {

    private int mUID;
    private String mUsername;
    private String mSessionToken;
    private String mPublicToken;

    private HashMap<Integer, BikePickupStation> mBikeStations;
    private ArrayList<Trajectory> mTrajectories;
    private Trajectory mLastTrackedTrajectory;

    private Bike mBikeBooked;

    private LatLng mLastPosition;
    private Date mDateUserInfoUpdated;
    private Date mDateStationsUpdated;

    private int mGlobalRank;
    private long mTotalPoints;
    private double mTotalDistance;
    private long mTotalTime;

    private Trajectory mLongestRide;
    private int mLogicalClock;
    private PrivateKey mPrivateKey;
    private PublicKey mServerPublicKey;

    private HashMap<String, List<Long>> mTransactionLog;

    public Data(int id, String usrn) {
        mUID = id;
        mUsername = usrn;

        mBikeStations = new HashMap<>();
        mTrajectories = new ArrayList<>();
        mLastPosition = new LatLng(38.737681, -9.138382);
        mTotalPoints = 500;
        mGlobalRank = -1;
        mTotalDistance = 0.0;
        mTotalTime = 0;
        mLogicalClock = 0;
        mTransactionLog = new HashMap<>();
    }

    public Data(int uid, String username, String sessionToken, String publicKeyToken, Bike bookedBike,
                ArrayList<BikePickupStation> bikeStations, ArrayList<Trajectory> trajectories,
                LatLng lastPosition, Date dateUserInfoUpdated, Date dateStationsUpdated,
                long totalPoints, int globalRank, int logicalClock, HashMap<String, List<Long>> transactionLog) {

        HashMap<Integer, BikePickupStation> stations = new HashMap<>();

        for(BikePickupStation station : bikeStations){
            stations.put(station.getSid(), station);
        }

        mUID = uid;
        mUsername = username;
        mSessionToken = sessionToken;
        mPublicToken = publicKeyToken;

        mBikeBooked = bookedBike;
        mBikeStations = stations;
        mTrajectories = trajectories;
        mLastPosition = lastPosition;
        mDateUserInfoUpdated = dateUserInfoUpdated;
        mDateStationsUpdated = dateStationsUpdated;

        for(Trajectory t : mTrajectories) {
            mTotalDistance += t.getTravelledDistance();
            mTotalTime += (t.getEndTime().getTime() - t.getStartTime().getTime());
            if(mLongestRide.getTravelledDistance() < t.getTravelledDistance()) {
                mLongestRide = t;
            }
        }

        mTotalPoints = totalPoints;
        mGlobalRank = globalRank;
        mLogicalClock = logicalClock;
        mTransactionLog = transactionLog;
    }




    public synchronized boolean doesTransactionExist(String username, long timestamp){

        if(!mTransactionLog.containsKey(username)){
            return false;
        }

        List<Long> timestamps = mTransactionLog.get(username);

        return timestamps.contains(timestamp);
    }

    public synchronized void addTransactionLog(String username, long timestamp){

        if(!mTransactionLog.containsKey(username)){
            mTransactionLog.put(username, new ArrayList<Long>());
        }

        mTransactionLog.get(username).add(timestamp);
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
        return mLastPosition;
    }

    /**
     * Sets booked bike
     *
     * @param bike - Bike object to add to collection
     */
    public void setBikeBooked(Bike bike){
        mBikeBooked = bike;
    }

    public boolean isAnyBikeBooked(){
        return mBikeBooked != null;
    }

    public Bike getBikeBooked(){
        return mBikeBooked;
    }

    public Date getLastUserInfoUpdated() {
        return mDateUserInfoUpdated;
    }

    public Date getLastStationUpdated(){
        return mDateStationsUpdated;
    }

    public String getLastUpdatedRelativeString(){
        return DateUtils.getRelativeTimeSpanString(mDateUserInfoUpdated.getTime()).toString();
    }

    public void setLastUserInfoUpdated(Date dateUpdated) {
       mDateUserInfoUpdated = dateUpdated;
    }

    public void setLastStationsUpdated(Date dateUpdated){
        mDateStationsUpdated = dateUpdated;
    }

    public int getUID() {
        return mUID;
    }

    public void setUID(int mUID) {
        this.mUID = mUID;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getSessionToken() {
        return mSessionToken;
    }

    public void setSessionToken(String mSessionToken) {
        this.mSessionToken = mSessionToken;
    }

    public String getPublicToken() {
        return mPublicToken;
    }

    public boolean hasPublicKeyToken(){ return mPublicToken != null; }

    public void setPublicToken(String mPublicToken) {
        this.mPublicToken = mPublicToken;
    }

    public void setTrajectories(ArrayList<Trajectory> trajectories) {
        mTrajectories = trajectories;
    }

    public Trajectory getLastTrackedTrajectory() {
        return mLastTrackedTrajectory;
    }

    public void setLastTrackedTrajectory(Trajectory lastTrackedTrajectory) {
        mLastTrackedTrajectory = lastTrackedTrajectory;
        addTrajectory(lastTrackedTrajectory);
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

    public void addPoints(int points) {mTotalPoints += points;}

    public void removePoints(int points) {mTotalPoints -= points;}

    public void setTotalPoints(long points){
        mTotalPoints = points;
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

    public int getGlobalRank() {
        return mGlobalRank;
    }

    public void setGlobalRank(int rank) {
        mGlobalRank = rank;
    }

    public int getLogicalClock(){
        return mLogicalClock;
    }

    public synchronized int getNextLogicalClock() {
        return ++mLogicalClock;
    }

    public PrivateKey getPrivateKey() {
        return mPrivateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        mPrivateKey = privateKey;
    }

    public PublicKey getServerPublicKey() {
        return mServerPublicKey;
    }

    public void setServerPublicKey(PublicKey mServerPublicKey) {
        this.mServerPublicKey = mServerPublicKey;
    }

    public HashMap<String, List<Long>> getTransactionLog() {
        return mTransactionLog;
    }

    public void setTransactionLog(HashMap<String, List<Long>> mTransactionLog) {
        this.mTransactionLog = mTransactionLog;
    }
}
