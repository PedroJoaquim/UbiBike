package pt.ulisboa.tecnico.cmu.ubibike.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONObject;

import java.util.Date;

import pt.ulisboa.tecnico.cmu.ubibike.domain.Data;
import pt.ulisboa.tecnico.cmu.ubibike.utils.JsonParser;


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
    private static final String COLUMN_DATA = "data";
    private static final String COLUMN_DATE_UPDATED = "date_updated";
    private static final String COLUMN_PUBLIC_KEY = "public_key";
    private static final String COLUMN_PRIVATE_KEY = "private_key";


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
                        + COLUMN_CLIENT_ID + " TEXT,"
                        + COLUMN_DATA + " TEXT, "
                        + COLUMN_DATE_UPDATED + " INTEGER, "
                        + COLUMN_PUBLIC_KEY + " BLOB,"
                        + COLUMN_PRIVATE_KEY + " BLOB"
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
    public boolean checkClientExistsOnDB(String clientID){
        boolean exists = true;

        String sqlQuery = "SELECT * FROM " + APP_DATA_TABLE_NAME + " WHERE " + COLUMN_CLIENT_ID + " = \"" + clientID + "\";";
        Cursor cursor = readableDatabase.rawQuery(sqlQuery, null);

        if(cursor.getCount() == 0){
            exists = false;

            ContentValues contentValues = new ContentValues();

            contentValues.put(COLUMN_CLIENT_ID, clientID);
            contentValues.putNull(COLUMN_DATA);
            contentValues.putNull(COLUMN_DATE_UPDATED);
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
    public boolean checkAppDataExistsOnDB(String clientID){
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
    public Data getAppDataFromDB(String clientID){

        Data appData = new Data(/*clientID TODO*/);
        Date dateUpdated;

        try {

            JSONObject json = getDataJsonFromDB(clientID);
            JsonParser.parseGlobalDataFromJson(json, appData);

            dateUpdated = getLastUpdatedTimeFromDB(clientID);
            appData.setLastUpdated(dateUpdated);

        } catch (Exception e) {
            //does not happen
        }


        return appData;
    }


    /**
     * Gets application data json
     *
     * @param clientID - client ID
     * @return - json
     */
    public JSONObject getDataJsonFromDB(String clientID){

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
            //does not happen
        }

        return json;
    }


    /**
     * Gets last sync time from DB
     *
     * @param clientID - client ID
     * @return - Date object or null in case no update was done
     */
    public Date getLastUpdatedTimeFromDB(String clientID){

        String sqlQuery = "SELECT * FROM " + APP_DATA_TABLE_NAME + " WHERE " + COLUMN_CLIENT_ID + " = \"" + clientID + "\";";
        Cursor cursor = readableDatabase.rawQuery(sqlQuery, null);
        cursor.moveToFirst();

        long timestamp = -1;
        if(!cursor.isNull(cursor.getColumnIndex(COLUMN_DATE_UPDATED)))
            timestamp = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_UPDATED));

        if (!cursor.isClosed()) {
            cursor.close();
        }

        if(timestamp != -1)
            return new Date(timestamp);
        else
            return null;
    }


}