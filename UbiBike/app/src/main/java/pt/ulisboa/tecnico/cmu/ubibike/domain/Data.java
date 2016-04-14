package pt.ulisboa.tecnico.cmu.ubibike.domain;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by andriy on 12.03.2016.
 */
public class Data {

    private int uid;
    private String username;
    private String sessionToken;

    private ArrayList<Chat> mConversations;

    private ArrayList<BikePickupStation> mBikeStations;
    private ArrayList<Trajectory> mTrajectories;
    private LatLng mLastPosition;

    private Date dateUpdated;



    public Data(){
        mConversations = new ArrayList<>();
        mTrajectories = new ArrayList<>();

        //hardcoded data below

        mBikeStations = new ArrayList<>();
        mBikeStations.add(new BikePickupStation("Odivelas Station", 10, 38.793017, -9.173086));
        mBikeStations.add(new BikePickupStation("Ameixoeira", 10, 38.779865, -9.159804));
        mBikeStations.add(new BikePickupStation("Campo Grande Station", 10, 38.759601, -9.157925));
        mBikeStations.add(new BikePickupStation("Alvalade Station", 11, 38.753040, -9.143829));
        mBikeStations.add(new BikePickupStation("Entrecampos Station", 10, 38.747692, -9.148506));
        mBikeStations.add(new BikePickupStation("Alameda Station", 10, 38.737073, -9.133582));
        mBikeStations.add(new BikePickupStation("Arco do Cego Station", 6, 38.735361, -9.142362));
        mBikeStations.add(new BikePickupStation("Parque Station", 10, 38.729628, -9.150012));
        mBikeStations.add(new BikePickupStation("Avenida Station", 10, 38.719981, -9.145588));
        mBikeStations.add(new BikePickupStation("Rossio Station", 10, 38.719981, -9.145588));
        mBikeStations.add(new BikePickupStation("Indendente Station", 10, 38.722029, -9.135263));
        mBikeStations.add(new BikePickupStation("Arroios Station", 10, 38.737073, -9.133582));
        mBikeStations.add(new BikePickupStation("Chelas Station", 10, 38.755019, -9.114212));
        mBikeStations.add(new BikePickupStation("Oriente Station", 10, 38.768527, -9.099648));
        mBikeStations.add(new BikePickupStation("Moscavide Station", 10, 38.768527, -9.099648));
        mBikeStations.add(new BikePickupStation("Laranjeiras", 10, 38.748300, -9.172612));
        mBikeStations.add(new BikePickupStation("Colegio Militar Station", 10, 38.753195, -9.188162));
        mBikeStations.add(new BikePickupStation("Pontinha Station", 10, 38.762259, -9.196830));
        mBikeStations.add(new BikePickupStation("Cais do Sodré Station", 10, 38.705734, -9.144241));
        mBikeStations.add(new BikePickupStation("Rossio Station", 10, 38.713863, -9.139069));
        mBikeStations.add(new BikePickupStation("Martim Moniz Station", 10, 38.716798, -9.135628));


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

        Date start1 =  new Date(1457794692 * 1000L); // 12/03/2016  14:58:12
        Date end1 =  new Date(1457796849 * 1000L); // 12/03/2016  15:34:09


        Trajectory trajectory = new Trajectory(0, "Alameda Station", "Arco do Cego Station", route, 2398, start1, end1);

        mTrajectories.add(trajectory);


        ArrayList<LatLng> route2 = new ArrayList<>();
        route2.add(new LatLng(38.774883, -9.097268));
        route2.add(new LatLng(38.762047, -9.098372));

        Date start2 =  new Date(1457882169 * 1000L); // 13/03/2016  15:16:09
        Date end2 =  new Date(1457882471 * 1000L); // 13/03/2016  15:21:11

        Trajectory trajectory2 = new Trajectory(1, "Station1", "Station2", route2, 519, start2, end2);

        mTrajectories.add(trajectory2);


        ArrayList<LatLng> route3 = new ArrayList<>();
        route3.add(new LatLng(38.741828, -9.133448));
        route3.add(new LatLng(38.717370, -9.135922));

        Date start3 =  new Date(1457860871 * 1000L); // 13/03/2016  09:21:11
        Date end3 =  new Date(1457862011 * 1000L); // 13/03/2016  09:40:11

        Trajectory trajectory3 = new Trajectory(2, "Station3", "Station4", route3, 600, start3, end3);

        mTrajectories.add(trajectory3);
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
    public ArrayList<BikePickupStation> getBikeStations(){
        return mBikeStations;
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

    /**
     * @return - number of trajectories
     */
    public int getTrajectoriesCount(){
        return mTrajectories.size();
    }


    /**
     * Gets all past trajectories sorted by the time they finished
     *
     * @return - trajectories list
     */
    public ArrayList<Trajectory> getAllTrajectories(){

        ArrayList<Trajectory> trajectories = mTrajectories;
        Collections.sort(trajectories);

        return trajectories;
    }

    /**
     * Sets last GPS synced position to given one
     *
     * @param latitude - coordinate
     * @param longitude - coordinate
     */
    public void setLastPosition(double latitude, double longitude){
        mLastPosition = new LatLng(latitude, longitude);
    }

    /**
     * @return - last GPS synced position
     */
    public LatLng getLastPosition(){
        return new LatLng(38.748101, -9.148148);    //hardcoded at Entrecampos
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
}
