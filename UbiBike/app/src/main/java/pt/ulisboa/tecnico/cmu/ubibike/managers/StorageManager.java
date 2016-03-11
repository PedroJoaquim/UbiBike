package pt.ulisboa.tecnico.cmu.ubibike.managers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


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
                        + COLUMN_CLIENT_ID + " TEXT"
                        + ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // empty on purpose
    }


}