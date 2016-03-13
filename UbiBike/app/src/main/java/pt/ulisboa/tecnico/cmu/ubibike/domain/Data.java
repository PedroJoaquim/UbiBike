package pt.ulisboa.tecnico.cmu.ubibike.domain;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by andriy on 12.03.2016.
 */
public class Data {

    private ArrayList<Chat> mConversations;
    private ArrayList<BikePickupStation> mBikeStationsNearby;
    private HashMap<Integer, Trajectory> mTrajectories;

    public Data(){
        mConversations = new ArrayList<>();
        mTrajectories = new HashMap<>();

        //hardcoded data below

        mBikeStationsNearby = new ArrayList<>();
        mBikeStationsNearby.add(new BikePickupStation("Alameda Station", 10, 38.737073, -9.133582));
        mBikeStationsNearby.add(new BikePickupStation("Arco do Cego Station", 10, 38.735361, -9.142362));


        ArrayList<LatLng> route = new ArrayList<>();
        route.add(new LatLng(38.737073, -9.133582));
        route.add(new LatLng(38.736954, -9.133817));
        route.add(new LatLng(38.736656, -9.133849));
        route.add(new LatLng(38.736533, -9.136403));
        route.add(new LatLng(38.736517, -9.136567));
        route.add(new LatLng(38.736059, -9.136690));
        route.add(new LatLng(38.735578, -9.137629));
        route.add(new LatLng(38.735578, -9.137629));
        route.add(new LatLng(38.735270, -9.139541));
        route.add(new LatLng(38.735270, -9.139541));
        route.add(new LatLng(38.735361, -9.142362));

        Trajectory trajectory = new Trajectory(0, "Alameda Station", "Arco do Cego Station", route);

        mTrajectories.put(0, trajectory);
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
     * Gets bike stations  nearby with bikes available to pickup
     *
     * @return - list of stations
     */
    public ArrayList<BikePickupStation> getBikeStationsNearby(){
        return mBikeStationsNearby;
    }

    /**
     * Gets a Trajectory given an id
     *
     * @param trajectoryID - id
     * @return - Trajectory
     */
    public Trajectory getTrajectory(int trajectoryID){
        return mTrajectories.get(new Integer(trajectoryID));
    }

}
