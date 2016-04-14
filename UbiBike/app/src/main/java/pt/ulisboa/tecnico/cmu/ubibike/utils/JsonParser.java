package pt.ulisboa.tecnico.cmu.ubibike.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.ulisboa.tecnico.cmu.ubibike.cipher.CipherManager;
import pt.ulisboa.tecnico.cmu.ubibike.domain.BikePickupStation;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Data;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Trajectory;

/**
 * Created by andriy on 13.04.2016.
 */
public class JsonParser {


    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String PUBLIC_KEY = "public_key";
    private static final String USER_ID = "uid";
    private static final String SESSION_TOKEN = "session_token";

    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lng";

    private static final String BIKE_ID = "bid;"
    private static final String BIKE_STATIONS = "bike_stations";
    private static final String STATION_ID = "sid";
    private static final String STATION_NAME = "station_name";
    private static final String STATION_BIKES_AVAILABLE = "bikes_available";

    private static final String TRAJECTORIES = "trajectories";
    private static final String TRAJECTORY_ID = "tid";
    private static final String TRAJECTORY_USER_ID = "user_tid";
    private static final String COORDINATES = "coordinates";
    private static final String DISTANCE = "distance";
    private static final String POINTS_EARNED = "points_earned";
    private static final String START_TIME = "start_time";
    private static final String END_TIME = "end_time";







    /**
     * Builds json of login request
     *
     * @param username - username
     * @param password - password
     * @return - json object
     */
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

    /**
     * Builds json of register request
     *
     * @param username - username
     * @param password - password
     * @param publicKey - user's public key
     * @return - json object
     */
    public static JSONObject buildRegisterRequestJson(String username, String password, byte[] publicKey){

        try{
            JSONObject json = new JSONObject();

            json.put(USERNAME, username);
            json.put(PASSWORD, password);
            json.put(PUBLIC_KEY, CipherManager.encodeToBase64String(publicKey));

            return json;
        }
        catch(Exception e){
            return null;
        }
    }

    /**
     * Parsing received json response and applying changes on given appData object
     *
     * @param jsonObject - response json
     * @param appData - data object
     * @throws JSONException
     */
    public static void parseLoginResponseFromJson(JSONObject jsonObject, Data appData) throws JSONException {

        int uid = jsonObject.getInt(USER_ID);
        String sessionToken = jsonObject.getString(SESSION_TOKEN);

        appData.setUid(uid);
        appData.setSessionToken(sessionToken);
    }



    public static void parseGlobalDataFromJson(JSONObject json, Data appData){
        //TODO
    }

    public static JSONObject buildGlobalJsonData(Data appData){

        try{

            JSONObject json = new JSONObject();

            json.put(USER_ID, appData.getUid());
            json.put(SESSION_TOKEN, appData.getSessionToken());

            JSONArray bikeStationsJson = new JSONArray();
            for(BikePickupStation station : appData.getBikeStations()){

                JSONObject stn = new JSONObject();

                stn.put(STATION_ID, station.getSid());
                stn.put(STATION_NAME, station.getStationName());
                stn.put(LATITUDE, station.getPositionLatitude());
                stn.put(LONGITUDE, station.getPositionLongitude());

                JSONArray bikes = new JSONArray();
                for(Integer bid : station.getBikesAvailable()){

                    JSONObject bike = new JSONObject();
                    bike.put("bid", bid);

                    bikes.put(bike);
                }

                stn.put(STATION_BIKES_AVAILABLE, bikes);
            }

            json.put(BIKE_STATIONS, bikeStationsJson);

            JSONArray trajectoriesJson = new JSONArray();
            for(Trajectory trajectory : appData.getAllTrajectories()){

                JSONObject trj = new JSONObject();

                //TODO

            }

        }
        catch(Exception e){
            return null;
        }
    }
}
