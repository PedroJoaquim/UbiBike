package pt.ulisboa.tecnico.cmu.ubibike.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONObject;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.Cipher;

import pt.ulisboa.tecnico.cmu.ubibike.connection.PendingRequest;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Data;
import pt.ulisboa.tecnico.cmu.ubibike.utils.JsonParser;
import pt.ulisboa.tecnico.cmu.ubibike.utils.Password;


/**
 * Class that holds all storage related (DB and filesystem) utilities/functions
 */
public class StorageManager extends SQLiteOpenHelper {


    private Context context;

    private SQLiteDatabase readableDatabase;
    private SQLiteDatabase writableDatabase;

    private static final String DATABASE_NAME = "UbiBike.db";
    private static final int DATABASE_VERSION = 1;

    private static final String APP_DATA_TABLE_NAME = "app_data";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CLIENT_ID = "client_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_SALT = "salt";
    private static final String COLUMN_DATA = "data";
    private static final String COLUMN_PUBLIC_KEY = "public_key";
    private static final String COLUMN_PRIVATE_KEY = "private_key";
    private static final String COLUMN_SERVER_PUBLIC_KEY = "server_public_key";

    public StorageManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;
        readableDatabase = getReadableDatabase();
        writableDatabase = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + APP_DATA_TABLE_NAME +
                        "(" + COLUMN_ID + " INTEGER PRIMARY KEY, "
                        + COLUMN_USERNAME + " TEXT,"
                        + COLUMN_PASSWORD + " BLOB,"
                        + COLUMN_SALT + " BLOB,"
                        + COLUMN_CLIENT_ID + " TEXT,"
                        + COLUMN_DATA + " TEXT, "
                        + COLUMN_PUBLIC_KEY + " BLOB,"
                        + COLUMN_PRIVATE_KEY + " BLOB,"
                        + COLUMN_SERVER_PUBLIC_KEY + " BLOB"
                        + ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // empty on purpose
    }



    /**
     * Checks if client is registered on DB, if not register
     *
     * @param clientID - client ID
     * @return - true in case is registered
     */
    public boolean checkClientExistsOnDB(int clientID){
        boolean exists = true;

        String sqlQuery = "SELECT * FROM " + APP_DATA_TABLE_NAME + " WHERE " + COLUMN_CLIENT_ID + " = \"" + clientID + "\";";
        Cursor cursor = readableDatabase.rawQuery(sqlQuery, null);

        if(cursor.getCount() == 0){
            exists = false;

            ContentValues contentValues = new ContentValues();

            contentValues.put(COLUMN_CLIENT_ID, clientID);
            contentValues.putNull(COLUMN_USERNAME);
            contentValues.putNull(COLUMN_PASSWORD);
            contentValues.putNull(COLUMN_SALT);
            contentValues.putNull(COLUMN_DATA);
            contentValues.putNull(COLUMN_PUBLIC_KEY);
            contentValues.putNull(COLUMN_PRIVATE_KEY);

            writableDatabase.insert(APP_DATA_TABLE_NAME, null, contentValues);
        }


        if (!cursor.isClosed()) {
            cursor.close();
        }

        return exists;
    }


    /**
     * Checks if app data is stored on DB
     *
     * @param clientID - client ID
     * @return - true if app data exists
     */
    public boolean checkAppDataExistsOnDB(int clientID){
        boolean exists = true;

        String sqlQuery = "SELECT * FROM " + APP_DATA_TABLE_NAME + " WHERE " + COLUMN_CLIENT_ID + " = \"" + clientID + "\";";
        Cursor cursor = readableDatabase.rawQuery(sqlQuery, null);
        cursor.moveToFirst();


        if(cursor.isNull(cursor.getColumnIndex(COLUMN_DATA)))
            exists = false;

        if (!cursor.isClosed()) {
            cursor.close();
        }

        return exists;
    }


    /**
     * Gets application data object from DB's stored json
     *
     * @param clientID - GooglePlus client id
     * @return - app data object
     */
    public Data getAppDataFromDB(int clientID){

        Data appData = null;
        Date dateUpdated;

        try {

            JSONObject json = getDataJsonFromDB(clientID);
            appData = JsonParser.parseGlobalDataFromJson(json);

        } catch (Exception e) {
            //does not happen
        }


        return appData;
    }

    /**
     * Gets pending requests stored on DB
     *
     * @param clientID - GooglePlus client id
     * @return - app data object
     */
    public ArrayList<PendingRequest> getPendingRequestFromDB(int clientID){

        ArrayList<PendingRequest> pendingRequests = new ArrayList<>();

        try {

            JSONObject json = getDataJsonFromDB(clientID);
            pendingRequests = JsonParser.parsePendingRequests(json);

        } catch (Exception e) {
            //does not happen
        }

        return pendingRequests;
    }


    /**
     * Gets application data json
     *
     * @param clientID - client ID
     * @return - json
     */
    private JSONObject getDataJsonFromDB(int clientID){

        JSONObject json = null;

        String sqlQuery = "SELECT * FROM " + APP_DATA_TABLE_NAME + " WHERE " + COLUMN_CLIENT_ID + " = \"" + clientID + "\";";
        Cursor cursor = readableDatabase.rawQuery(sqlQuery, null);
        cursor.moveToFirst();

        String jsonStr = cursor.getString(cursor.getColumnIndex(COLUMN_DATA));

        if (!cursor.isClosed()) {
            cursor.close();
        }

        try {

            json = new JSONObject(jsonStr);

        } catch (Exception e) {
            e.printStackTrace();
            //does not happen
        }

        return json;
    }



    /**
     * Updates current application's data json on DB
     *
     * @param clientID - user id
     * @param appData - app data object
     */
    public void updateAppDataOnDB(int clientID, Data appData){

        ContentValues contentValues = new ContentValues();

        JSONObject json = JsonParser.buildGlobalJsonData(appData);

        if(json != null) contentValues.put(COLUMN_DATA, json.toString());

        writableDatabase.update(APP_DATA_TABLE_NAME, contentValues, COLUMN_CLIENT_ID + "= \"" + clientID + "\"", null);
    }


    /**
     * Stores public & private key pair on DB
     *
     * @param clientID - user id
     * @param publicKey - PublicKey object
     * @param privateKey - PrivateKey object
     */
    public void storeClientKeyPairOnBD(int clientID, PublicKey publicKey, PrivateKey privateKey){

        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_PUBLIC_KEY, publicKey.getEncoded());
        contentValues.put(COLUMN_PRIVATE_KEY, privateKey.getEncoded());

        writableDatabase.update(APP_DATA_TABLE_NAME, contentValues, COLUMN_CLIENT_ID + "= \"" + clientID + "\"", null);
    }


    public void storeServerPublicKeyOnDB(int clientID, PublicKey serverPublicKey) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_SERVER_PUBLIC_KEY, serverPublicKey.getEncoded());

        writableDatabase.update(APP_DATA_TABLE_NAME, contentValues, COLUMN_CLIENT_ID + "= \"" + clientID + "\"", null);
    }


    /**
     * Retrieves client's public key from DB
     *
     * @param clientID - client id
     * @return - PublicKey object
     */
    public PublicKey getClientPublicKeyFromDB(int clientID){

        String sqlQuery = "SELECT * FROM " + APP_DATA_TABLE_NAME + " WHERE " + COLUMN_CLIENT_ID + " = \"" + clientID + "\";";
        Cursor cursor = readableDatabase.rawQuery(sqlQuery, null);
        cursor.moveToFirst();

        byte[] publicKeyBytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_PUBLIC_KEY));

        if (!cursor.isClosed()) {
            cursor.close();
        }

        return CipherManager.getPublicKeyFromBytes(publicKeyBytes);
    }


    public PublicKey getServerPublicKeyFromDB(int clientID){

        String sqlQuery = "SELECT * FROM " + APP_DATA_TABLE_NAME + " WHERE " + COLUMN_CLIENT_ID + " = \"" + clientID + "\";";
        Cursor cursor = readableDatabase.rawQuery(sqlQuery, null);
        cursor.moveToFirst();

        byte[] publicKeyBytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_SERVER_PUBLIC_KEY));

        if (!cursor.isClosed()) {
            cursor.close();
        }

        return CipherManager.getPublicKeyFromBytes(publicKeyBytes);
    }


    /**
     * Retrieves client's priate key from DB
     *
     * @param clientID - client id
     * @return - PrivateKey object
     */
    public PrivateKey getClientPrivateKeyFromDB(int clientID){

        String sqlQuery = "SELECT * FROM " + APP_DATA_TABLE_NAME + " WHERE " + COLUMN_CLIENT_ID + " = \"" + clientID + "\";";
        Cursor cursor = readableDatabase.rawQuery(sqlQuery, null);
        cursor.moveToFirst();

        byte[] privateKeyBytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_PRIVATE_KEY));

        if (!cursor.isClosed()) {
            cursor.close();
        }

        return CipherManager.getPrivateKeyFromBytes(privateKeyBytes);
    }


    /**
     * Registers client's login credentials on DB
     *
     * @param clientID - client id
     * @param username - username
     * @param plainPassword - password in plaintext
     */
    public void registerLoginCredentialsOnDB(int clientID, String username, String plainPassword){

        byte[] salt = Password.getNextSalt();
        byte[] hashedPassword = Password.hash(plainPassword.toCharArray(), salt);

        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASSWORD, hashedPassword);
        contentValues.put(COLUMN_SALT, salt);

        writableDatabase.update(APP_DATA_TABLE_NAME, contentValues, COLUMN_CLIENT_ID + "= \"" + clientID + "\"", null);

    }


    /**
     * Get client id from given username
     *
     * @param username - client username
     * @return - client id  or null in case client with username provided doesnt exists
     */
    public Integer getClientIDGivenUsernameFromDB(String username) {

        Integer result = null;

        String sqlQuery = "SELECT * FROM " + APP_DATA_TABLE_NAME + " WHERE " + COLUMN_USERNAME + " = \"" + username + "\";";
        Cursor cursor = readableDatabase.rawQuery(sqlQuery, null);

        if (cursor.getCount() != 0) {

            cursor.moveToFirst();

            result = cursor.getInt(cursor.getColumnIndex(COLUMN_CLIENT_ID));
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        return result;
    }




    /**
     *  Checks whether or not given password is correct
     *
     * @param username - provided username
     * @param password - provided password
     * @return - true / false regarding password correctness or null in case of no login record from the past with given user
     */
    public Boolean checkIsExpectedPassword(String username, String password){

        Boolean result = null;

        String sqlQuery = "SELECT * FROM " + APP_DATA_TABLE_NAME + " WHERE " + COLUMN_USERNAME + " = \"" + username + "\";";
        Cursor cursor = readableDatabase.rawQuery(sqlQuery, null);

        if(cursor.getCount() != 0) {

            cursor.moveToFirst();

            byte[] hashedPassword = cursor.getBlob(cursor.getColumnIndex(COLUMN_PASSWORD));
            byte[] salt = cursor.getBlob(cursor.getColumnIndex(COLUMN_SALT));

            if (hashedPassword != null) {
                result = Password.isExpectedPassword(password.toCharArray(), salt, hashedPassword);
            }

        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        return result;
    }



}