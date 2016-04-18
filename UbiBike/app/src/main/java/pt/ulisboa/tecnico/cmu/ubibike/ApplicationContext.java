package pt.ulisboa.tecnico.cmu.ubibike;

import android.app.Application;

import pt.ulisboa.tecnico.cmu.ubibike.connection.ServerCommunicationHandler;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Data;
import pt.ulisboa.tecnico.cmu.ubibike.managers.SessionManager;
import pt.ulisboa.tecnico.cmu.ubibike.managers.StorageManager;


public class ApplicationContext extends Application {

    private static ApplicationContext mInstance;

    private UbiBike mActivity;

    private int mUid;
    private Data mData;
    private SessionManager mSessionManager;
    private StorageManager mStorageManager;
    private ServerCommunicationHandler mServerCommunicationHandler;


    public static ApplicationContext getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        mSessionManager = new SessionManager(this);
        mStorageManager = new StorageManager(this);
        mServerCommunicationHandler = new ServerCommunicationHandler();


        mData = new Data();

        /*if (mSessionManager.isLoggedIn()) {
            mUid = mSessionManager.getLoggedUser();

            if (mStorageManager.checkClientExistsOnDB(mUid) && mStorageManager.checkAppDataExistsOnDB(mUid)) {
                mData = mStorageManager.getAppDataFromDB(mUid);

                mServerCommunicationHandler.setUid(mData.getUid());
                mServerCommunicationHandler.setSessionToken(mData.getSessionToken());
            }
        }*/

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        if (dataExists()) {
            mStorageManager.updateAppDataOnDB(mUid, getData());
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        if (dataExists()) {
            mStorageManager.updateAppDataOnDB(mUid, getData());
        }
    }

    public StorageManager getStorageManager() {
        return mStorageManager;
    }

    public boolean dataExists() {
        return mData != null;
    }

    public Data getData() {
        return mData;
    }

    public void setData(Data appData) {
        mData = appData;

        mServerCommunicationHandler.setUid(mData.getUid());
        mServerCommunicationHandler.setSessionToken(mData.getSessionToken());
    }

    public UbiBike getActivity(){
        return mActivity;
    }

    public void setActivity(UbiBike activity){
        mActivity = activity;
    }

    public ServerCommunicationHandler getServerCommunicationHandler() {
        return mServerCommunicationHandler;
    }
}
