package pt.ulisboa.tecnico.cmu.ubibike.utils;

import org.json.JSONException;
import org.json.JSONObject;

import pt.ulisboa.tecnico.cmu.ubibike.cipher.CipherManager;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Data;

/**
 * Created by andriy on 13.04.2016.
 */
public class JsonParser {


    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String PUBLIC_KEY = "public_key";
    private static final String USER_ID = "uid";
    private static final String SESSION_TOKEN = "session_token";





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
}
