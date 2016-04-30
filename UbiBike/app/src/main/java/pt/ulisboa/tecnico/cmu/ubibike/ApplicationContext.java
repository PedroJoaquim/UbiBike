package pt.ulisboa.tecnico.cmu.ubibike;

import android.app.Application;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmu.ubibike.connection.PendingRequest;
import pt.ulisboa.tecnico.cmu.ubibike.connection.ServerCommunicationHandler;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Data;
import pt.ulisboa.tecnico.cmu.ubibike.managers.SessionManager;
import pt.ulisboa.tecnico.cmu.ubibike.managers.StorageManager;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.NearbyPeerCommunication;


public class ApplicationContext extends Application {

    private static ApplicationContext mInstance;

    private UbiBike mActivity;
    private boolean mInternetConnected;

    private int mUid;
    private String mPassword;
    private Data mData;
    private SessionManager mSessionManager;
    private StorageManager mStorageManager;

    private ServerCommunicationHandler mServerCommunicationHandler;
    private ArrayList<PendingRequest> mPendingRequests;

    private NearbyPeerCommunication mNearbyPeerCommunication;



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
        mPendingRequests = new ArrayList<>();

        mNearbyPeerCommunication = new NearbyPeerCommunication();

        if (mSessionManager.isLoggedIn()) {
            mUid = mSessionManager.getLoggedUser();

            if (mStorageManager.checkClientExistsOnDB(mUid) && mStorageManager.checkAppDataExistsOnDB(mUid)) {
                mData = mStorageManager.getAppDataFromDB(mUid);

                mServerCommunicationHandler.setUid(mData.getUid());
                mServerCommunicationHandler.setSessionToken(mData.getSessionToken());
            }
        }
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
        mUid = mData.getUid();

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

    public NearbyPeerCommunication getNearbyPeerCommunication() {
        return mNearbyPeerCommunication;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public boolean isInternetConnected() {
        return mInternetConnected;
    }

    public void setInternetConnected(boolean mInternetConnected) {
        this.mInternetConnected = mInternetConnected;
    }



    public void addPendingRequest(PendingRequest request){
        mPendingRequests.add(request);
    }

    public int getNextPendingRequestID(){
        if(mPendingRequests.isEmpty()){
            return 0;
        }
        else{
            return mPendingRequests.get(mPendingRequests.size()-1).getID() + 1;
        }
    }
    public PendingRequest getPendingRequest(){
        if(mPendingRequests.isEmpty()) {
            return null;
        }
        else {
            return mPendingRequests.get(0);
        }
    }

    public void removePendingRequest(int id){

        for(int i = 0; i < mPendingRequests.size()-1; i++){
            if(mPendingRequests.get(i).getID() == id){
                mPendingRequests.remove(i);
                return;
            }
        }
    }
}
