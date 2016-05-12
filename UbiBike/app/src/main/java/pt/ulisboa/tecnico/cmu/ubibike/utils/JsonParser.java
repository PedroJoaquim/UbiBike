package pt.ulisboa.tecnico.cmu.ubibike.utils;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.connection.PendingRequest;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Bike;
import pt.ulisboa.tecnico.cmu.ubibike.domain.BikePickupStation;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Data;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Trajectory;
import pt.ulisboa.tecnico.cmu.ubibike.managers.CipherManager;

/**
 * Created by andriy on 13.04.2016.
 */
public class JsonParser {


    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String PUBLIC_KEY = "public_key";
    public static final String USER_ID = "uid";
    private static final String SESSION_TOKEN = "session_token";
    private static final String PUBLIC_KEY_TOKEN = "public_key_token";


    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lng";

    private static final String BIKE_ID = "bid";
    private static final String BIKE_ADDR = "bike_addr";

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

    public static final String POINTS = "points";
    private static final String GLOBAl_RANK = "rank";

    private static final String LOGICAL_CLOCK = "logical_clock";

    private static final String LAST_POSITION = "last_position";
    private static final String LAST_USER_INFO_UPDATED = "last_user_info_updated";
    private static final String LAST_STATIONS_UPDATED = "last_stations_updated";

    private static final String BIKE_PICK = "bike_pick";

    private static final String BOOKED_BIKE = "booked_bike";
    private static final String BOOKING = "booking";

    private static final String PENDING_REQUESTS = "pending_requests";
    private static final String PENDING_REQUEST_ID = "preq_id";
    private static final String PENDING_REQUEST_URL = "preq_url";
    private static final String PENDING_REQUEST_TYPE = "preq_type";
    private static final String PENDING_REQUEST_JSON = "preq_json";

    public static final String ERROR = "error";


    public static final String SOURCE_USERNAME = "source_uid";
    public static final String TARGET_USERNAME = "target_uid";
    public static final String SOURCE_LOGICAL_CLOCK = "source_logical_clock";
    public static final String TIMESTAMP = "timestamp";
    public static final String VALIDATION_TOKEN = "validation_token";
    public static final String SOURCE_PUBLIC_KEY_TOKEN = "source_public_key_token";
    public static final String ORIGINAL_JSON_BASE_64 = "original_json_base_64";
    public static final String TARGET_LOGICAL_CLOCK = "target_logical_clock";
    public static final String TTL = "ttl";
    private static final String SERVER_PUBLIC_KEY = "server_public_key";
    private static final String TRANSACTIONS = "transactions";
    private static final String TRANSACTIONS_LOG = "transactions_log";


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
        String serverPublicKey = jsonObject.getString(SERVER_PUBLIC_KEY);


        PublicKey serverPK = CipherManager.getPublicKeyFromBytes(CipherManager.decodeFromBase64String(serverPublicKey));


        appData.setUID(uid);
        appData.setSessionToken(sessionToken);
        appData.setServerPublicKey(serverPK);
    }

    public static void parseRegisterAccountResponseFromJson(JSONObject jsonObject, Data appData) throws JSONException {

        int uid = jsonObject.getInt(USER_ID);
        String sessionToken = jsonObject.getString(SESSION_TOKEN);
        String publicKeyToken = jsonObject.getString(PUBLIC_KEY_TOKEN);
        String serverPublicKey = jsonObject.getString(SERVER_PUBLIC_KEY);


        PublicKey serverPK = CipherManager.getPublicKeyFromBytes(CipherManager.decodeFromBase64String(serverPublicKey));



        appData.setUID(uid);
        appData.setSessionToken(sessionToken);
        appData.setPublicToken(publicKeyToken);
        appData.setServerPublicKey(serverPK);
    }

    public static void parsePublicKeyTokenResponseFromJson(JSONObject jsonObject, Data appData) throws JSONException {

        String publicKeyToken = jsonObject.getString(PUBLIC_KEY_TOKEN);

        appData.setPublicToken(publicKeyToken);
    }

    public static void parseUserInfoResponseFromJson(JSONObject jsonObject, Data appData) throws JSONException {

        ArrayList<Trajectory> trajectories = parseTrajectories(jsonObject);
        appData.setGlobalRank(jsonObject.getInt(GLOBAl_RANK));
        appData.setTrajectories(trajectories);
        appData.setLastUserInfoUpdated(new Date());

        if(jsonObject.has(BOOKING)){
            JSONObject bk = jsonObject.getJSONObject(BOOKING);

            int bid = bk.getInt(BIKE_ID);
            int sid = bk.getInt(STATION_ID);
            String uuid = bk.getString(BIKE_ADDR);

            Bike bike = new Bike(bid,uuid, sid);

           appData.setBikeBooked(bike);
        }
    }

    public static void parseStationsResponseFromJson(JSONObject jsonObject, Data appData) throws JSONException {

        ArrayList<BikePickupStation> bikePickupStations = parseStations(jsonObject);
        appData.setBikeStations(bikePickupStations);
        appData.setLastStationsUpdated(new Date());
    }

    public static void parseBikeBookResponseFromJson(JSONObject jsonObject, Data appData) throws JSONException {

        int bid = jsonObject.getInt(BIKE_ID);
        int sid = jsonObject.getInt(STATION_ID);
        String uuid = jsonObject.getString(BIKE_ADDR);

        Bike bike = new Bike(bid, uuid, sid);

        appData.setBikeBooked(bike);
    }


    /************************************************************************************************************************
     ******************************* Json to store on DB <->  Data object  ****************************************************
     ************************************************************************************************************************/


    public static JSONObject buildGlobalJsonData(Data appData) {

        try {

            JSONObject json = new JSONObject();

            json.put(USER_ID, appData.getUID());
            json.put(USERNAME, appData.getUsername());
            json.put(SESSION_TOKEN, appData.getSessionToken());
            json.put(PUBLIC_KEY_TOKEN, appData.getPublicToken());
            json.put(POINTS, appData.getTotalPoints());
            json.put(GLOBAl_RANK, appData.getGlobalRank());
            json.put(LOGICAL_CLOCK, appData.getLogicalClock());

            //adding booked bike
            Bike bikeBooked = appData.getBikeBooked();
            if(bikeBooked != null){

                JSONObject bk = new JSONObject();

                bk.put(BIKE_ID, bikeBooked.getBid());
                bk.put(STATION_ID, bikeBooked.getSid());
                bk.put(BIKE_ADDR, bikeBooked.getUuid());

                json.put(BOOKED_BIKE, bk);
            }

            //adding transactionsLog
            JSONArray transactionsLog = new JSONArray();
            for (Map.Entry<String, List<Long>> logEntry : appData.getTransactionLog().entrySet()) {

                JSONObject user = new JSONObject();
                JSONArray entries = new JSONArray();

                for (Long timestamp:logEntry.getValue()) {
                    entries.put(timestamp);
                }

                user.put(USERNAME, logEntry.getKey());
                user.put(TRANSACTIONS, entries);
            }

            json.put(TRANSACTIONS_LOG, transactionsLog);

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

            if(appData.getLastUserInfoUpdated() != null) {
                json.put(LAST_USER_INFO_UPDATED, appData.getLastUserInfoUpdated().getTime());
            }

            if(appData.getLastStationUpdated() != null) {
                json.put(LAST_STATIONS_UPDATED, appData.getLastStationUpdated().getTime());
            }


            //adding pending requests to json to store on DB
            ArrayList<PendingRequest> pendingRequests = ApplicationContext.getInstance().getAllPendingRequests();

            JSONArray jsnPenReqs = new JSONArray();
            for(PendingRequest pReq : pendingRequests){
                JSONObject jsnPenReq = new JSONObject();

                jsnPenReq.put(PENDING_REQUEST_ID, pReq.getID());
                jsnPenReq.put(PENDING_REQUEST_URL, pReq.getUrl());
                jsnPenReq.put(PENDING_REQUEST_TYPE, pReq.getRequestType());

                if(pReq.getJson() != null) {
                    jsnPenReq.put(PENDING_REQUEST_JSON, pReq.getJson().toString());
                }

                jsnPenReqs.put(jsnPenReq);
            }

            json.put(PENDING_REQUESTS, jsnPenReqs);


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


            //getting booked bike
            Bike bookedBike = null;
            if(json.has(BOOKED_BIKE)){
                JSONObject bk = json.getJSONObject(BOOKED_BIKE);

                int bid = bk.getInt(BIKE_ID);
                int sid = bk.getInt(STATION_ID);
                String uuid = bk.getString(BIKE_ADDR);

                 bookedBike = new Bike(bid, uuid, sid);


            }


            ArrayList<BikePickupStation> bikePickupStations = parseStations(json);
            ArrayList<Trajectory> trajectories = parseTrajectories(json);

            //getting last position
            JSONObject lst_pos = json.getJSONObject(LAST_POSITION);
            double positionLatitude = lst_pos.getDouble(LATITUDE);
            double positionLongitude = lst_pos.getDouble(LONGITUDE);

            LatLng lastPosition = new LatLng(positionLatitude, positionLongitude);

            Date lastUserInfoUpdated = null;
            if(json.has(LAST_USER_INFO_UPDATED)) {
                lastUserInfoUpdated = new Date(json.getLong(LAST_USER_INFO_UPDATED));
            }

            Date lastStationsUpdated = null;
            if(json.has(LAST_STATIONS_UPDATED)) {
                lastStationsUpdated = new Date(json.getLong(LAST_STATIONS_UPDATED));
            }

            int globalRank = json.getInt(GLOBAl_RANK);
            long totalPoints = json.getLong(POINTS);
            int logicalClock = json.getInt(LOGICAL_CLOCK);

            HashMap<String, List<Long>> transactionLog = parseTransactionLogs(json);

            return new Data(uid, username, sessionToken, publicKeyToken, bookedBike, bikePickupStations,
                    trajectories, lastPosition, lastUserInfoUpdated, lastStationsUpdated, totalPoints,
                    globalRank, logicalClock, transactionLog);

        }
        catch(Exception e){
            Log.e("Uncaught exception", e.toString());
        }
        return null;
    }

    private static HashMap<String, List<Long>> parseTransactionLogs(JSONObject json) {
        HashMap<String, List<Long>> result = new HashMap<>();

        if(!json.has(TRANSACTIONS_LOG)){
            return result;
        }

        try{

            JSONArray logs = json.getJSONArray(TRANSACTIONS_LOG);

            for(int i = 0; i < logs.length(); i++) {
                JSONObject log = logs.getJSONObject(i);

                String username = log.getString(USERNAME);

                result.put(username, new ArrayList<Long>());

                JSONArray entries = log.getJSONArray(TRANSACTIONS);
                for(int j = 0; j < entries.length(); j++){
                    result.get(username).add(entries.getLong(j));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
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
     * Parses pending requests from json
     *
     * @param json - json containing requests
     * @return - list of PendingRequest objects
     */
    public static ArrayList<PendingRequest> parsePendingRequests(JSONObject json) {
        ArrayList<PendingRequest> pendingRequests = new ArrayList<>();

        try {

            JSONArray requests = json.getJSONArray(PENDING_REQUESTS);

            for(int i = 0; i < requests.length(); i++) {
                JSONObject request = requests.getJSONObject(i);

                int id = request.getInt(PENDING_REQUEST_ID);
                String url = request.getString(PENDING_REQUEST_URL);
                int type = request.getInt(PENDING_REQUEST_TYPE);

                String jsonStr = null;
                if(request.has(PENDING_REQUEST_JSON)){
                    jsonStr = request.getString(PENDING_REQUEST_JSON);
                }

                JSONObject jsonRequest = (jsonStr == null) ? null : new JSONObject(jsonStr);
                pendingRequests.add(new PendingRequest(id, url, type, jsonRequest));
            }

            return pendingRequests;

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

    public static JSONObject buildPointsTransactionFinalJson(String originalJSONBase64, String publicKeyToken, String validationToken) {
        try{
            JSONObject json = new JSONObject();

            json.put(VALIDATION_TOKEN, validationToken);
            json.put(SOURCE_PUBLIC_KEY_TOKEN, publicKeyToken);
            json.put(ORIGINAL_JSON_BASE_64, originalJSONBase64);

            return json;
        }
        catch(Exception e){
            Log.e("Uncaught exception", e.toString());
            return null;
        }
    }

    public static JSONObject buildPointsTransactionDataJson(String sourceUsername, String targetUsername, int sourceLogicalClock, int points, long timestamp) {
        try{
            JSONObject json = new JSONObject();

            json.put(SOURCE_USERNAME, sourceUsername);
            json.put(TARGET_USERNAME, targetUsername);
            json.put(SOURCE_LOGICAL_CLOCK, sourceLogicalClock);
            json.put(POINTS, points);
            json.put(TIMESTAMP, timestamp);

            return json;
        }
        catch(Exception e){
            Log.e("Uncaught exception", e.toString());
            return null;
        }
    }

    public static JSONObject buildPointsTransactionServerJSON(JSONObject json, int targetLogicalClock) {
        try{
            json.put(TARGET_LOGICAL_CLOCK, targetLogicalClock);
            return json;
        }
        catch(Exception e){
            Log.e("Uncaught exception", e.toString());
            return null;
        }
    }

    public static JSONObject parsePointsTransaction(String json) {

        try{

            JSONObject result = new JSONObject(json);

            if(!result.has(VALIDATION_TOKEN) || !result.has(SOURCE_PUBLIC_KEY_TOKEN) || !result.has(ORIGINAL_JSON_BASE_64)){
                return null;
            }

            return result;

        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject parseBasePointsTransaction(String json) {

        try{

            JSONObject result = new JSONObject(json);

            if(!result.has(SOURCE_USERNAME) || !result.has(TARGET_USERNAME) || !result.has(SOURCE_LOGICAL_CLOCK) ||
                    !result.has(POINTS) || !result.has(TIMESTAMP)){
                return null;
            }

            return result;

        } catch (JSONException e) {
            return null;
        }

    }

    public static JSONObject parsePublicKeyToken(String json) {
        try{

            JSONObject result = new JSONObject(json);

            if(!result.has(USER_ID) || !result.has(USERNAME) || !result.has(PUBLIC_KEY) || !result.has(TTL)){
                return null;
            }

            return result;

        } catch (JSONException e) {
            return null;
        }
    }
}
