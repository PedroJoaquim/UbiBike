package pt.ulisboa.tecnico.cmu.ubibike.domain;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import pt.ulisboa.tecnico.cmu.ubibike.utils.JsonParser;

/**
 * Created by andriy on 12.03.2016.
 */
public class Data {

    private int uid;
    private String username;
    private String sessionToken;
    private String publicKeyToken;

    private ArrayList<Chat> mConversations;

    private ArrayList<BikePickupStation> mBikeStations;
    private ArrayList<Trajectory> mTrajectories;
    private ArrayList<Bike> mBikesBooked;
    private LatLng mLastPosition;
    private int totalPointsEarned;
    private Date dateUpdated;


    public Data(){  //hardcoded data below

        uid = 0;
        username = "test";
        sessionToken = "token";
        publicKeyToken = "token";
        mConversations = new ArrayList<>();
        mTrajectories = new ArrayList<>();
        mLastPosition = new LatLng(38.793017, -9.173086);
        dateUpdated = new Date();

        ArrayList<Integer> bikes = new ArrayList<>();
        bikes.add(1);
        bikes.add(4);
        bikes.add(2);

        mBikeStations = new ArrayList<>();
        mBikeStations.add(new BikePickupStation(0, "Odivelas Station", 38.793017, -9.173086, bikes));
        mBikeStations.add(new BikePickupStation(1, "Ameixoeira", 38.779865, -9.159804, new ArrayList<Integer>()));
    }


    public Data(int id, String usrn) {
        uid = id;
        username = usrn;
        mConversations = new ArrayList<>();
        mBikeStations = new ArrayList<>();
        mTrajectories = new ArrayList<>();
        mBikesBooked = new ArrayList<>();
        mLastPosition = new LatLng(0.0, 0.0); //TODO last position
        dateUpdated = new Date();
    }

    public Data(int uid, String username, String sessionToken, String publicKeyToken, ArrayList<Chat> mConversations,
                ArrayList<BikePickupStation> mBikeStations, ArrayList<Trajectory> mTrajectories,
                LatLng mLastPosition, Date dateUpdated) {

        this.uid = uid;
        this.username = username;
        this.sessionToken = sessionToken;
        this.publicKeyToken = publicKeyToken;
        this.mConversations = mConversations;
        this.mBikeStations = mBikeStations;
        this.mTrajectories = mTrajectories;
        this.mLastPosition = mLastPosition;
        this.dateUpdated = dateUpdated;
    }


    /**
     * Gets all conversations
     *
     * @return - list of chats
     */
    public ArrayList<Chat> getConversations() {
        return mConversations;
    }


    /**
     * Gets bike stations with bikes available to pickup
     *
     * @return - list of stations
     */
    public ArrayList<BikePickupStation> getBikeStations() {
        return mBikeStations;
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
        return new LatLng(38.748101, -9.148148);    //hardcoded at Entrecampos
    }

    /**
     * Adds booked bike
     *
     * @param bike - Bike object to add to collection
     */
    public void addBookedBike(Bike bike){
        mBikesBooked.add(bike);
    }


    public Date getLastUpdated() {
        return dateUpdated;
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

    public void setPublicKeyToken(String publicKeyToken) {
        this.publicKeyToken = publicKeyToken;
    }

    public void setTrajectories(ArrayList<Trajectory> trajectories) {
        mTrajectories = trajectories;
    }

    public void setBikeStations(ArrayList<BikePickupStation> bikeStations) {
        mBikeStations = bikeStations;
    }

    public int getTotalPointsEarned(){
        int total = 0;

        for(Trajectory trj: mTrajectories){
            total += trj.getPointsEarned();
        }

        return total;
    }
}
