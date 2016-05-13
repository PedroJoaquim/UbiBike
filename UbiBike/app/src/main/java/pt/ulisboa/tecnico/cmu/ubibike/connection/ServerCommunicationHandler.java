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
import pt.ulisboa.tecnico.cmu.ubibike.managers.MobileConnectionManager;
import pt.ulisboa.tecnico.cmu.ubibike.managers.StorageManager;
import pt.ulisboa.tecnico.cmu.ubibike.utils.JsonParser;


public class ServerCommunicationHandler {



    private int uid;
    private String sessionToken;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    private static String HOST_SERVER = "http://85.246.101.218:8000";
    private static String URL_LOGIN = "/auth";                               //[POST] json_schema = authentication.json
    private static String URL_REGISTER_ACCOUNT = "/registration";            //[POST] json_schema = register.json
    private static String URL_PUBLIC_KEY_TOKEN = "/PublicKeyToken";          //[GET]  url com session_token & uid
    private static String URL_BIKE_PICK_DROP = "/BikePickDrop";              //[POST] url com session_token & uid json_schema = bike_pick_drop.json
    private static String URL_TRAJECTORY_POST = "/Trajectory";               //[POST] url com session_token & uid json_schema = new_trajectory.json
    private static String URL_BIKE_BOOK = "/BikeBooking";                    //[GET] url com session_token & uid & sid
    private static String URL_USER_INFO = "/User";                           //[GET] url com session_token & uid
    private static String URL_STATIONS_INFO = "/Stations";                   //[GET] url com session_token & uid
    private static String URL_BIKE_UNBOOKING = "/BikeUnbooking";             //[GET] url com session_token & uid
    private static String URL_POINTS_TRANSACTION = "/PointsTransaction";     //TODO

    private static final int REQUEST_PUBLIC_KEY_TOKEN = 0;
    private static final int REQUEST_USER_INFO = 1;
    private static final int REQUEST_STATIONS_NEARBY = 2;
    private static final int REQUEST_BIKE_PICK_DROP = 3;
    private static final int REQUEST_TRAJECTORY_POST = 4;
    private static final int REQUEST_BIKE_BOOK = 5;
    private static final int REQUEST_BIKE_UNBOOK = 6;
    private static final int REQUEST_POINTS_TRANSACTION = 7;

    private static final boolean AUTH_REQUEST = true;
    private static final boolean NON_AUTH_REQUEST = false;


    private String buildUrl(String requestURL, boolean authRequest){
        String url;

        if(authRequest) {
            url = HOST_SERVER + requestURL + "?uid=" + uid + "&session_token=" + sessionToken;
        }
        else{
            url = HOST_SERVER + requestURL;
        }

        return url;
    }


    public void performLoginRequest(String username, String password){
        String url = buildUrl(URL_LOGIN, NON_AUTH_REQUEST);

        new LoginRequestTask(url, username, password).execute();
    }


    public void performRegisterRequest(String username, String password){
        String url = buildUrl(URL_REGISTER_ACCOUNT, NON_AUTH_REQUEST);


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
     * @param store - store for future rerun?   (false in case of pending request re-execution to
     *                                                                      avoid duplicate storing)
     * @param pendingRequestID - pending request ID in case of request re-execution, null otherwise
     */
    public void performGenericRequest(String url, int requestType, JSONObject json, boolean store, Integer pendingRequestID ){
        try {

            String methodName = getResponseParseMethodName(requestType);
            Method parseMethod = null;

            if(methodName != null) {
               parseMethod = JsonParser.class.getMethod(methodName, new Class[]{JSONObject.class, Data.class});
            }

            if(ApplicationContext.getInstance().isInternetConnected()) {
                new GenericRequestTask(url, parseMethod, requestType, json, pendingRequestID).execute();
            }
            else if(store){
                storePendingRequest(url, requestType, json);
            }

        } catch (NoSuchMethodException e) {
            //won't happen, I promise :)
        }
    }

    /**
     * Stores request for future re-execution
     *
     * @param url - request url
     * @param requestType - type
     * @param json - json to be sent in request or null
     */
    public void storePendingRequest(String url, int requestType, JSONObject json){
        int id = ApplicationContext.getInstance().getNextPendingRequestID();
        PendingRequest pReq = new PendingRequest(id, url, requestType, json);
        ApplicationContext.getInstance().addPendingRequest(pReq);

        String msg = "Pending request [id=" + pReq.getID() + "] stored.";
        Toast.makeText(ApplicationContext.getInstance(), msg , Toast.LENGTH_SHORT).show();
    }

    /**
     * Gets next pending request and re-executes it
     */
    public void executeNextPendingRequest(){
        PendingRequest pReq = ApplicationContext.getInstance().getPendingRequest();

        //if pReq == null, there are no more pending requests to execute
        if(pReq != null) {
            performGenericRequest(pReq.getUrl(), pReq.getRequestType(), pReq.getJson(), false, pReq.getID());

            String msg = "Pending request [id=" + pReq.getID() + "] execution attempt.";
            Toast.makeText(ApplicationContext.getInstance(), msg , Toast.LENGTH_SHORT).show();
        }
    }


    public void performPointsTransactionRequest(JSONObject jsonContent){
        String url = buildUrl(URL_POINTS_TRANSACTION, AUTH_REQUEST);

        performGenericRequest(url, REQUEST_POINTS_TRANSACTION, jsonContent, true, null);
    }

    public void performPublicKeyTokenRequest(){
        String url = buildUrl(URL_PUBLIC_KEY_TOKEN, AUTH_REQUEST);

        performGenericRequest(url, REQUEST_PUBLIC_KEY_TOKEN, null, true, null);
    }

    public void performUserInfoRequest(){
        String url = buildUrl(URL_USER_INFO, AUTH_REQUEST);

        performGenericRequest(url, REQUEST_USER_INFO, null, true, null);
    }

    public void performStationsNearbyRequest(){
        String url = buildUrl(URL_STATIONS_INFO, AUTH_REQUEST);

        performGenericRequest(url, REQUEST_STATIONS_NEARBY, null, true, null);
    }

    public void performBikePickDropRequest(int bid, int sid, boolean bikePick){
        String url = buildUrl(URL_BIKE_PICK_DROP, AUTH_REQUEST);

        JSONObject json = JsonParser.buildBikePickDropRequestJson(bid, sid, bikePick);

        performGenericRequest(url, REQUEST_BIKE_PICK_DROP, json, true, null);
    }

    public void performTrajectoryPostRequest(int userTid, int startSid, int endSid,
                                             ArrayList<LatLng> positions, int startTimestamp,
                                             int endTimestamp, double distance){

        String url = buildUrl(URL_TRAJECTORY_POST, AUTH_REQUEST);

        JSONObject json = JsonParser.buildTrajectoryPostRequestJson(userTid, startSid, endSid, positions,
                startTimestamp, endTimestamp, distance);

        performGenericRequest(url, REQUEST_TRAJECTORY_POST, json, true, null);
    }

    public void performBikeBookRequest(int sid) {
        String url = buildUrl(URL_BIKE_BOOK, AUTH_REQUEST) + "&sid=" + sid;

        performGenericRequest(url, REQUEST_BIKE_BOOK, null, true, null);
    }

    public void performBikeUnbookRequest(){
        String url = buildUrl(URL_BIKE_UNBOOKING, AUTH_REQUEST);

        performGenericRequest(url, REQUEST_BIKE_UNBOOK, null, true, null);
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
            String response = null;

            try {

                JSONObject jsonRequest = JsonParser.buildLoginRequestJson(username, password);

                response = HttpRequests.performHttpCall("POST", url, jsonRequest);

                if(response != null && !JsonParser.isJSONValid(response)){ // Request error message received
                    JSONObject json = new JSONObject();
                    json.put(JsonParser.ERROR, response);
                    response = json.toString();
                }
            }
            catch (Exception e) {
                error = e;
            }

            return response;
        }

        @Override
        protected void onPostExecute(String jsonStr) {

            if (jsonStr == null || jsonStr.equals("[]") || jsonStr.equals("{}")){   //null or empty
                return;
            }
            else {

                try {

                    JSONObject json = new JSONObject(jsonStr);

                    if(json.has(JsonParser.ERROR)){
                        String errorMsg = json.getString(JsonParser.ERROR);
                        Toast.makeText(ApplicationContext.getInstance(), errorMsg, Toast.LENGTH_SHORT).show();
                        return;
                    }

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

                    ApplicationContext.getInstance().getStorageManager().updateAppDataOnDB(userID, appData);

                } catch (Exception e) {
                    String msg = "An error ocurred while logging in";
                    Toast.makeText(ApplicationContext.getInstance(), msg, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            Toast.makeText(ApplicationContext.getInstance(), "Login successful.", Toast.LENGTH_SHORT).show();

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
            String response = null;

            try {

                JSONObject jsonRequest = JsonParser.buildRegisterRequestJson(username, password, base64publicKey);

                response = HttpRequests.performHttpCall("POST", url, jsonRequest);

                if(response != null && !JsonParser.isJSONValid(response)){ // Request error message received
                    JSONObject json = new JSONObject();
                    json.put(JsonParser.ERROR, response);
                    response = json.toString();
                }
            }
            catch (Exception e) {
                error = e;
            }

            return response;
        }

        @Override
        protected void onPostExecute(String jsonStr) {

            if (jsonStr == null || jsonStr.equals("[]") || jsonStr.equals("{}")){   //null or empty
                return;
            }
            else {

                try {

                    JSONObject json = new JSONObject(jsonStr);

                    if(json.has(JsonParser.ERROR)){
                        String errorMsg = json.getString(JsonParser.ERROR);
                        Toast.makeText(ApplicationContext.getInstance(), errorMsg, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int userID = JsonParser.getUserIDFromJson(json);

                    Data appData;

                    if(getStorageManager().checkClientExistsOnDB(userID) &&
                            getStorageManager().checkAppDataExistsOnDB(userID) ){

                        appData = getStorageManager().getAppDataFromDB(userID);
                    }
                    else{
                        appData = new Data(userID, username);
                    }

                    appData.setPrivateKey(privateKey);

                    JsonParser.parseRegisterAccountResponseFromJson(json, appData);

                    ApplicationContext.getInstance().getStorageManager().storeClientKeyPairOnBD(userID, publicKey, privateKey);

                    ApplicationContext.getInstance().getStorageManager().storeServerPublicKeyOnDB(userID, appData.getServerPublicKey());

                    ApplicationContext.getInstance().setData(appData);
                    ApplicationContext.getInstance().getActivity().finishLogin();

                    ApplicationContext.getInstance().getStorageManager().updateAppDataOnDB(userID, appData);


                } catch (Exception e) {
                    String msg = "An error occurred registering the account.";
                    Toast.makeText(ApplicationContext.getInstance(), msg, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            Toast.makeText(ApplicationContext.getInstance(), "Account registered.", Toast.LENGTH_SHORT).show();

        }
    }

    public class GenericRequestTask extends AsyncTask<String, Void, String>{

        private String url;
        private Method parseMethod;
        private int requestType;
        private JSONObject json;
        private Integer pendentRequestID;
        private Exception error;

        public GenericRequestTask(String url, Method parseMethod, int requestType, JSONObject json, Integer pReqID) {
            this.url = url;
            this.parseMethod = parseMethod;
            this.requestType = requestType;
            this.json = json;
            this.pendentRequestID = pReqID;

        }

        @Override
        protected String doInBackground(String... params) {
            String response = null;

            try {

                String requestType = json != null ? "POST" : "GET";

                response = HttpRequests.performHttpCall(requestType, url, json);

                if(response != null && !JsonParser.isJSONValid(response)){ // Request error message received
                    JSONObject json = new JSONObject();
                    json.put(JsonParser.ERROR, response);
                    response = json.toString();
                }


            }
            catch (Exception e) {
                error = e;
            }

            return response;
        }

        @Override
        protected void onPostExecute(String jsonStr) {

            if(jsonStr == null){
                String msg = "Couldn't perform " + getRequestType(requestType) + " request.";
                Toast.makeText(ApplicationContext.getInstance(), msg, Toast.LENGTH_SHORT).show();
                return;
            }
            else if (jsonStr.equals("{}")){   //null or empty
                finishSuccessfulRequest(requestType);
                ApplicationContext.getInstance().updateUI();
            }
            else {

                try {

                    JSONObject json = new JSONObject(jsonStr);

                    if(json.has(JsonParser.ERROR)){
                        String errorMsg = json.getString(JsonParser.ERROR);
                        Toast.makeText(ApplicationContext.getInstance(), errorMsg, Toast.LENGTH_SHORT).show();
                    }
                    else {

                        Data appData = ApplicationContext.getInstance().getData();

                        parseMethod.invoke(null, new Object[]{json, appData});

                        ApplicationContext.getInstance().updateUI();

                        Toast.makeText(ApplicationContext.getInstance(),
                                "Success at " + getRequestType(requestType) + " request.", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    String msg = "Couldn't perform " + getRequestType(requestType) + " request.";
                    Toast.makeText(ApplicationContext.getInstance(), msg, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            //check if current finished request is a pending request re-execution
            //if so, remove it from pending request collection
            if(pendentRequestID != null) {
                ApplicationContext.getInstance().removePendingRequest(pendentRequestID);
                executeNextPendingRequest();

                String msg = "Pending request [id=" + pendentRequestID + "] executed with success.";
                Toast.makeText(ApplicationContext.getInstance(), msg , Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void finishSuccessfulRequest(int requestType) {
        if(requestType == REQUEST_BIKE_UNBOOK){
            ApplicationContext.getInstance().getData().setBikeBooked(null);
            Toast.makeText(ApplicationContext.getInstance(), "Success at bike unbooking request.", Toast.LENGTH_SHORT).show();
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
            case REQUEST_BIKE_UNBOOK: request = "bike unbook"; break;
            case REQUEST_POINTS_TRANSACTION: request = "points transaction"; break;
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