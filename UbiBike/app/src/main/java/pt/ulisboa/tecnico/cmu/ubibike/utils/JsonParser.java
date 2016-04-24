package pt.ulisboa.tecnico.cmu.ubibike.utils;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import pt.ulisboa.tecnico.cmu.ubibike.domain.Bike;
import pt.ulisboa.tecnico.cmu.ubibike.managers.CipherManager;
import pt.ulisboa.tecnico.cmu.ubibike.domain.BikePickupStation;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Data;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Trajectory;

/**
 * Created by andriy on 13.04.2016.
 */
public class JsonParser {


    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    private static final String PUBLIC_KEY = "public_key";
    public static final String USER_ID = "uid";
    private static final String SESSION_TOKEN = "session_token";
    private static final String PUBLIC_KEY_TOKEN = "public_key_token";

    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lng";

    private static final String BIKE_ID = "bid";
    private static final String BIKE_STATIONS = "stations";
    private static final String STATION_ID = "sid";
    private static final String START_STATION = "start_sid";
    private static final String END_STATION = "end_sid";
    private static final String STATION_NAME = "station_name";
    private static final String STATION_BIKES_AVAILABLE = "bikes_available";

    private static final String TRAJECTORIES = "trajectories";
    private static final String TRAJECTORY_ID = "tid";
    private static final String TRAJECTORY_USER_ID = "user_tid";
    private static final String START_STATION_ID = "start_station_id";
    private static final String END_STATION_ID = "end_station_id";
    private static final String COORDINATES = "coords";
    private static final String DISTANCE = "distance";
    private static final String POINTS_EARNED = "points_earned";
    private static final String START_TIME = "start_timestamp";
    private static final String END_TIME = "end_timestamp";

    private static final String LAST_POSITION = "last_position";
    private static final String LAST_UPDATED = "last_updated";

    private static final String BIKE_PICK = "bike_pick";

    public static final String ERROR = "error";




    /************************************************************************************************************************
     ************************************* Building JSON requests ***********************************************************
     ************************************************************************************************************************/

    public static JSONObject buildLoginRequestJson(String username, String password){

       try{
           JSONObject json = new JSONObject();

           json.put(USERNAME, username);
           json.put(PASSWORD, password);

           return json;
       }
       catch(Exception e){
           return null;
       }
    }

    public static JSONObject buildRegisterRequestJson(String username, String password, String base64publicKey){

        try{
            JSONObject json = new JSONObject();

            json.put(USERNAME, username);
            json.put(PASSWORD, password);
            json.put(PUBLIC_KEY, base64publicKey);

            return json;
        }
        catch(Exception e){
            return null;
        }
    }

    public static JSONObject buildBikePickDropRequestJson(int bid, int sid, boolean bikePick){

        try{
            JSONObject json = new JSONObject();

            json.put(BIKE_ID, bid);
            json.put(STATION_ID, sid);
            json.put(BIKE_PICK, bikePick);

            return json;
        }
        catch(Exception e){
            return null;
        }
    }

    public static JSONObject buildTrajectoryPostRequestJson(int userTid, int startSid, int endSid,
                                                            ArrayList<LatLng> positions,
                                                            int startTimestamp,
                                                            int endTimestamp,
                                                            double distance){
        try{

            JSONObject json = new JSONObject();

            json.put(TRAJECTORY_USER_ID, userTid);
            json.put(START_STATION, startSid);
            json.put(END_STATION, endSid);


            JSONArray routeCoordinates = new JSONArray();

            for(LatLng coord : positions){
                JSONObject pos = new JSONObject();

                pos.put(LATITUDE, coord.latitude);
                pos.put(LONGITUDE, coord.longitude);

                routeCoordinates.put(coord);
            }

            json.put(COORDINATES, routeCoordinates);
            json.put(START_TIME, startTimestamp);
            json.put(END_TIME, endTimestamp);
            json.put(DISTANCE, distance);


            return json;
        }
        catch(Exception e){
            return null;
        }

    }


    /************************************************************************************************************************
     ******************* Parsing received json responses and applying changes on given appData object ***********************
     ************************************************************************************************************************/

    public static void parseLoginResponseFromJson(JSONObject jsonObject, Data appData) throws JSONException {

        int uid = jsonObject.getInt(USER_ID);
        String sessionToken = jsonObject.getString(SESSION_TOKEN);

        appData.setUid(uid);
        appData.setSessionToken(sessionToken);
    }

    public static void parseRegisterAccountResponseFromJson(JSONObject jsonObject, Data appData) throws JSONException {

        int uid = jsonObject.getInt(USER_ID);
        String sessionToken = jsonObject.getString(SESSION_TOKEN);
        String publicKeyToken = jsonObject.getString(PUBLIC_KEY_TOKEN);

        appData.setUid(uid);
        appData.setSessionToken(sessionToken);
        appData.setPublicKeyToken(publicKeyToken);
    }

    public static void parsePublicKeyTokenResponseFromJson(JSONObject jsonObject, Data appData) throws JSONException {

        String publicKeyToken = jsonObject.getString(PUBLIC_KEY_TOKEN);

        appData.setPublicKeyToken(publicKeyToken);
    }

    public static void parseUserInfoResponseFromJson(JSONObject jsonObject, Data appData) throws JSONException {

        ArrayList<Trajectory> trajectories = parseTrajectories(jsonObject);
        appData.setTrajectories(trajectories);
    }

    public static void parseStationsResponseFromJson(JSONObject jsonObject, Data appData) throws JSONException {

        ArrayList<BikePickupStation> bikePickupStations = parseStations(jsonObject);
        appData.setBikeStations(bikePickupStations);
    }

    public static void parseBikeBookResponseFromJson(JSONObject jsonObject, Data appData) throws JSONException {

        //TODO finish
        int bid = jsonObject.getInt(BIKE_ID);
        int sid = jsonObject.getInt(STATION_ID);
        String uuid = "";

        Bike bike = new Bike(bid, uuid, sid);

        appData.setBikeBooked(bike);
    }


    /************************************************************************************************************************
     ******************************* Json to store on DB <->  Data object  ****************************************************
     ************************************************************************************************************************/


    public static JSONObject buildGlobalJsonData(Data appData) {

        try {

            JSONObject json = new JSONObject();

            json.put(USER_ID, appData.getUid());
            json.put(USERNAME, appData.getUsername());
            json.put(SESSION_TOKEN, appData.getSessionToken());
            json.put(PUBLIC_KEY_TOKEN, appData.getPublicKeyToken());

            //adding bikeStations
            JSONArray bikeStations = new JSONArray();
            for (BikePickupStation station : appData.getBikeStations()) {

                JSONObject stn = new JSONObject();

                stn.put(STATION_ID, station.getSid());
                stn.put(STATION_NAME, station.getStationName());
                stn.put(LATITUDE, station.getPositionLatitude());
                stn.put(LONGITUDE, station.getPositionLongitude());

                JSONArray bikes = new JSONArray();
                for (Integer bid : station.getBikesAvailable()) {

                    JSONObject bike = new JSONObject();
                    bike.put(BIKE_ID, bid);

                    bikes.put(bike);
                }

                stn.put(STATION_BIKES_AVAILABLE, bikes);
                bikeStations.put(stn);
            }

            json.put(BIKE_STATIONS, bikeStations);

            //adding trajectories
            JSONArray trajectories = new JSONArray();
            for (Trajectory trajectory : appData.getAllTrajectories()) {

                JSONObject trj = new JSONObject();

                trj.put(TRAJECTORY_ID, trajectory.getTrajectoryID());
                trj.put(START_STATION_ID, trajectory.getStartStationID());
                trj.put(END_STATION_ID, trajectory.getEndStationID());

                JSONArray coords = new JSONArray();
                for (LatLng coordinate : trajectory.getRoute()) {

                    JSONObject position = new JSONObject();

                    position.put(LATITUDE, coordinate.latitude);
                    position.put(LONGITUDE, coordinate.longitude);

                    coords.put(position);
                }

                trj.put(COORDINATES, coords);
                trj.put(DISTANCE, trajectory.getTravelledDistance());
                trj.put(POINTS_EARNED, trajectory.getPointsEarned());
                trj.put(START_TIME, trajectory.getStartTime().getTime());
                trj.put(END_TIME, trajectory.getEndTime().getTime());

                trajectories.put(trj);
            }

            json.put(TRAJECTORIES, trajectories);


            //adding last position
            JSONObject lst_pos = new JSONObject();
            lst_pos.put(LATITUDE, appData.getLastPosition().latitude);
            lst_pos.put(LONGITUDE, appData.getLastPosition().longitude);

            json.put(LAST_POSITION, lst_pos);
            json.put(LAST_UPDATED, appData.getLastUpdated().getTime());

            return json;

        } catch (Exception e) {
            return null;
        }
    }

    public static Data parseGlobalDataFromJson(JSONObject json){

        try{

            int uid = json.getInt(USER_ID);
            String username = json.getString(USERNAME);
            String sessionToken = json.getString(SESSION_TOKEN);

            String publicKeyToken = null;
            if(json.has(PUBLIC_KEY_TOKEN)){
                publicKeyToken = json.getString(PUBLIC_KEY_TOKEN);
            }

            ArrayList<BikePickupStation> bikePickupStations = parseStations(json);
            ArrayList<Trajectory> trajectories = parseTrajectories(json);

            //getting last position
            JSONObject lst_pos = json.getJSONObject(LAST_POSITION);
            double positionLatitude = lst_pos.getDouble(LATITUDE);
            double positionLongitude = lst_pos.getDouble(LONGITUDE);

            LatLng lastPosition = new LatLng(positionLatitude, positionLongitude);
            Date lastUpdated = new Date(json.getLong(LAST_UPDATED));

            return new Data(uid, username, sessionToken, publicKeyToken, null /*TODO chat*/,  bikePickupStations,
                    trajectories, lastPosition, lastUpdated);

        }
        catch(Exception e){
            return null;
        }
    }


    /**
     * Parses stations from json
     *
     * @param json - json containing stations
     * @return - list of BikePickupStation objects
     */
    public static ArrayList<BikePickupStation> parseStations(JSONObject json) {
        ArrayList<BikePickupStation> bikePickupStations = new ArrayList<>();

        try {

            JSONArray stations = json.getJSONArray(BIKE_STATIONS);

            for(int i = 0; i < stations.length(); i++) {
                JSONObject station = stations.getJSONObject(i);

                int sid = station.getInt(STATION_ID);
                String stationName = station.getString(STATION_NAME);
                double positionLatitude = station.getDouble(LATITUDE);
                double positionLongitude = station.getDouble(LONGITUDE);

                //getting bikes available
                JSONArray bikes = station.getJSONArray(STATION_BIKES_AVAILABLE);
                ArrayList<Integer> bikesAvailable = new ArrayList<>();

                for (int j = 0; j < bikes.length(); j++) {
                    bikesAvailable.add(bikes.getJSONObject(j).getInt(BIKE_ID));
                }

                bikePickupStations.add(new BikePickupStation(sid, stationName, positionLatitude,
                        positionLongitude, bikesAvailable));
            }

            return bikePickupStations;

        }
        catch(Exception e){
            return null;
        }
    }


    /**
     * Parses trajectories from json
     *
     * @param json - json containing trajectories
     * @return - list of Trajectory objects
     */
    public static ArrayList<Trajectory> parseTrajectories(JSONObject json){
        ArrayList<Trajectory> trajectories = new ArrayList<>();

        try {

            JSONArray trajectoriesJson = json.getJSONArray(TRAJECTORIES);

            for(int i = 0; i < trajectoriesJson.length(); i++) {
                JSONObject trajectory = trajectoriesJson.getJSONObject(i);

                int tid = trajectory.getInt(TRAJECTORY_ID);

                //getting trajectory positions
                JSONArray routeCoordinates = trajectory.getJSONArray(COORDINATES);
                ArrayList<LatLng> trajectoryPositions = new ArrayList<>();

                for(int j = 0; j < routeCoordinates.length(); j++){
                    JSONObject coord = routeCoordinates.getJSONObject(j);

                    double positionLatitude = coord.getDouble(LATITUDE);
                    double positionLongitude = coord.getDouble(LONGITUDE);

                    trajectoryPositions.add(new LatLng(positionLatitude, positionLongitude));
                }

                int startStationID = 0; //TODO
                int endStationID = 0; //TODO
                double distance = trajectory.getDouble(DISTANCE);
                Date startTime = new Date(trajectory.getLong(START_TIME));
                Date endTime = new Date(trajectory.getLong(END_TIME));

                trajectories.add(new Trajectory(tid, startStationID, endStationID, trajectoryPositions, distance, startTime, endTime));
            }

            return trajectories;
        }
        catch(Exception e){
            return null;
        }
    }


    /**
     * Gets user ID
     */
    public static int getUserIDFromJson(JSONObject json) throws JSONException {
        return json.getInt(JsonParser.USER_ID);
    }


    /**
     * Checks whether or not given string is a valid json
     */
    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {

            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
