package pt.ulisboa.tecnico.cmu.ubibike;

import android.app.Application;

import pt.ulisboa.tecnico.cmu.ubibike.connection.ServerCommunicationHandler;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Data;
import pt.ulisboa.tecnico.cmu.ubibike.managers.StorageManager;

/**
 * Created by andriy on 10.03.2016.
 */
public class ApplicationContext extends Application {

    private static ApplicationContext mInstance;
    private Data mData;
    private StorageManager mStorageManager;
    private ServerCommunicationHandler mServerCommunicationHandler;


    public static ApplicationContext getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mStorageManager = new StorageManager(this);
        mData = new Data();     //TODO change
    }

    public StorageManager getStorageManager(){
        return mStorageManager;
    }
    public Data getData(){
        return mData;
    }

}
