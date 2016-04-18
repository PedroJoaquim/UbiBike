package pt.ulisboa.tecnico.cmu.ubibike.connection;

import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Data;
import pt.ulisboa.tecnico.cmu.ubibike.managers.CipherManager;
import pt.ulisboa.tecnico.cmu.ubibike.managers.StorageManager;
import pt.ulisboa.tecnico.cmu.ubibike.utils.JsonParser;


public class ServerCommunicationHandler {


    private int uid;
    private String sessionToken;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    private static String HOST_SERVER = "...";
    private static String URL_LOGIN = "...";
    private static String URL_REGISTER_ACCOUNT = "...";
    private static String URL_PUBLIC_KEY_TOKEN = "...";
    private static String URL_BIKE_PICK_DROP = "...";
    private static String URL_TRAJECTORY_POST = "...";
    private static String URL_BIKE_BOOK = "...";

    private static final int REQUEST_PUBLIC_KEY_TOKEN = 0;
    private static final int REQUEST_USER_INFO = 1;
    private static final int REQUEST_STATIONS_NEARBY = 2;
    private static final int REQUEST_BIKE_PICK_DROP = 3;
    private static final int REQUEST_TRAJECTORY_POST = 4;
    private static final int REQUEST_BIKE_BOOK = 5;


    public void performLoginRequest(String username, String password){
        String url = HOST_SERVER + URL_LOGIN;

        new LoginRequestTask(url, username, password).execute();
    }


    public void performRegisterRequest(String username, String password){
        String url = HOST_SERVER + URL_REGISTER_ACCOUNT;


        CipherManager.generatePublicPrivateKeyPair();
        publicKey  = CipherManager.getPublicKey();
        privateKey = CipherManager.getPrivateKey();

        String base64publicKey = CipherManager.encodeToBase64String(publicKey.getEncoded());

        new RegisterAccountRequestTask(url, username, password, base64publicKey).execute();

    }

    /**
     * Universal request that fits in most cases
     *
     * @param url - request url
     * @param requestType - request type
     */
    public void performGenericRequest(String url, int requestType, JSONObject json){
        try {

            String methodName = getResponseParseMethodName(requestType);
            Method parseMethod = JsonParser.class.getMethod(methodName, new Class[]{ JSONObject.class, Data.class });

            new GenericRequestTask(url, parseMethod, requestType, json).execute();

        } catch (NoSuchMethodException e) {
            new GenericRequestTask(url, null, requestType, json).execute();
        }
    }


    public void performPublicKeyTokenRequest(){
        String url = HOST_SERVER + URL_PUBLIC_KEY_TOKEN + "?uid=" + uid + "&sessionToken=" + sessionToken;

        performGenericRequest(url, REQUEST_PUBLIC_KEY_TOKEN, null);
    }

    public void performUserInfoRequest(){
        String url = HOST_SERVER + URL_PUBLIC_KEY_TOKEN + "?uid=" + uid + "&sessionToken=" + sessionToken;

        performGenericRequest(url, REQUEST_USER_INFO, null);
    }

    public void performStationsNearbyRequest(){
        String url = HOST_SERVER + URL_PUBLIC_KEY_TOKEN + "?uid=" + uid + "&sessionToken=" + sessionToken;

        performGenericRequest(url, REQUEST_STATIONS_NEARBY, null);
    }

    public void performBikePickDropRequest(int bid, int sid, boolean bikePick){
        String url = HOST_SERVER + URL_BIKE_PICK_DROP + "?uid=" + uid + "&sessionToken=" + sessionToken;

        JSONObject json = JsonParser.buildBikePickDropRequestJson(bid, sid, bikePick);

        performGenericRequest(url, REQUEST_BIKE_PICK_DROP, json);
    }

    public void performTrajectoryPostRequest(int userTid, int startSid, int endSid,
                                             ArrayList<LatLng> positions, int startTimestamp,
                                             int endTimestamp, double distance){

        String url = HOST_SERVER + URL_TRAJECTORY_POST + "?uid=" + uid + "&sessionToken=" + sessionToken;

        JSONObject json = JsonParser.buildTrajectoryPostRequestJson(userTid, startSid, endSid, positions,
                startTimestamp, endTimestamp, distance);

        performGenericRequest(url, REQUEST_TRAJECTORY_POST, json);
    }

    public void performBikeBookRequest(int sid){
        String url = HOST_SERVER + URL_TRAJECTORY_POST + "?uid=" + uid + "&sessionToken=" + sessionToken;

        JSONObject json = JsonParser.buildBikeBookRequestJson(sid);

        performGenericRequest(url, REQUEST_BIKE_BOOK, json);
    }






    public class LoginRequestTask extends AsyncTask<String, Void, String>{

        private String url;
        private String username;
        private String password;
        private Exception error;

        public LoginRequestTask(String url, String username, String password) {
            this.url = url;
            this.username = username;
            this.password = password;
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonStr = null;

            try {

                JSONObject jsonRequest = JsonParser.buildLoginRequestJson(username, password);

                jsonStr = HttpRequests.performHttpCall("POST", url, jsonRequest);
            }
            catch (Exception e) {
                error = e;
            }

            return jsonStr;
        }

        @Override
        protected void onPostExecute(String jsonStr) {

            boolean failed = false;

            if (jsonStr == null){
                failed = true;
            }
            else if(jsonStr.equals("[]")){ //empty json


                return;
            }
            else {

                try {

                    JSONObject json = new JSONObject(jsonStr);
                    int userID = JsonParser.getUserIDFromJson(json);

                    Data appData;

                    if(getStorageManager().checkClientExistsOnDB(userID) &&
                            getStorageManager().checkAppDataExistsOnDB(userID) ){

                        appData = getStorageManager().getAppDataFromDB(userID);
                    }
                    else{
                        appData = new Data(userID, username);
                    }

                    JsonParser.parseLoginResponseFromJson(json, appData);

                    ApplicationContext.getInstance().setData(appData);
                    ApplicationContext.getInstance().getActivity().finishLogin();

                } catch (Exception e) {
                    failed = true;
                }
            }

            if(failed){
                String msg = "An error ocurred requesting to log in";
                Toast.makeText(ApplicationContext.getInstance().getActivity(), msg, Toast.LENGTH_SHORT).show();
            }

        }
    }

    public class RegisterAccountRequestTask extends AsyncTask<String, Void, String>{

        private String url;
        private String username;
        private String password;
        private String base64publicKey;
        private Exception error;

        public RegisterAccountRequestTask(String url, String username, String password, String base64publicKey) {
            this.url = url;
            this.username = username;
            this.password = password;
            this.base64publicKey = base64publicKey;
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonStr = null;

            try {

                JSONObject jsonRequest = JsonParser.buildRegisterRequestJson(username, password, base64publicKey);

                jsonStr = HttpRequests.performHttpCall("POST", url, jsonRequest);
            }
            catch (Exception e) {
                error = e;
            }

            return jsonStr;
        }

        @Override
        protected void onPostExecute(String jsonStr) {

            boolean failed = false;

            if (jsonStr == null){
                failed = true;
            }
            else if(jsonStr.equals("[]")){ //empty json


                return;
            }
            else {

                try {

                    JSONObject json = new JSONObject(jsonStr);
                    int userID = JsonParser.getUserIDFromJson(json);

                    Data appData;

                    if(getStorageManager().checkClientExistsOnDB(userID) &&
                            getStorageManager().checkAppDataExistsOnDB(userID) ){

                        appData = getStorageManager().getAppDataFromDB(userID);
                    }
                    else{
                        appData = new Data(userID, username);
                    }

                    JsonParser.parseRegisterAccountResponseFromJson(json, appData);

                    ApplicationContext.getInstance().setData(appData);
                    ApplicationContext.getInstance().getActivity().finishLogin();

                } catch (Exception e) {
                    failed = true;
                }
            }

            if(failed){
                String msg = "An error occurred registering the account.";
                Toast.makeText(ApplicationContext.getInstance().getActivity(), msg, Toast.LENGTH_SHORT).show();
            }

        }
    }

    public class GenericRequestTask extends AsyncTask<String, Void, String>{

        private String url;
        private Method parseMethod;
        private int requestType;
        private JSONObject json;
        private Exception error;

        public GenericRequestTask(String url, Method parseMethod, int requestType, JSONObject json) {
            this.url = url;
            this.parseMethod = parseMethod;
            this.requestType = requestType;
            this.json = json;

        }

        @Override
        protected String doInBackground(String... params) {
            String jsonStr = null;

            try {


                jsonStr = HttpRequests.performHttpCall("GET", url, json);
            }
            catch (Exception e) {
                error = e;
            }

            return jsonStr;
        }

        @Override
        protected void onPostExecute(String jsonStr) {

            boolean failed = false;


            if (jsonStr == null){
                failed = true;
            }
            else if(jsonStr.isEmpty() || jsonStr.equals("[]") || jsonStr.equals("{}")){ //empty json

                return;
            }
            else {

                try {

                    JSONObject json = new JSONObject(jsonStr);

                    Data appData = ApplicationContext.getInstance().getData();

                    parseMethod.invoke(null, new Object[]{json, appData});

                } catch (Exception e) {
                    failed = true;
                }
            }

            if(failed){
                String msg = "Couldn't perform " + getRequestType(requestType) + " request";
                Toast.makeText(ApplicationContext.getInstance().getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Gets a instance of StorageManager
     */
    private StorageManager getStorageManager(){
        return ApplicationContext.getInstance().getStorageManager();
    }

    private String getResponseParseMethodName(int requestType){
        String methodName;

        switch (requestType){
            case REQUEST_PUBLIC_KEY_TOKEN: methodName =  "parsePublicKeyTokenResponseFromJson"; break;
            case REQUEST_USER_INFO: methodName = "parseUserInfoResponseFromJson"; break;
            case REQUEST_STATIONS_NEARBY: methodName = "parseStationsResponseFromJson"; break;
            case REQUEST_BIKE_BOOK: methodName = "parseBikeBookResponseFromJson"; break;

            default: methodName = null; //will not be used
        }

        return methodName;
    }

    private String getRequestType(int requestType){
        String request = null;

        switch (requestType){
            case REQUEST_PUBLIC_KEY_TOKEN: request =  "public key token"; break;
            case REQUEST_USER_INFO: request = "user info"; break;
            case REQUEST_STATIONS_NEARBY: request = "stations nearby"; break;
            case REQUEST_BIKE_PICK_DROP: request = "bike pick/drop"; break;
            case REQUEST_BIKE_BOOK: request = "bike book"; break;
        }

        return request;
    }




    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
}

