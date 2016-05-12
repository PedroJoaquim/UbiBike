package pt.ulisboa.tecnico.cmu.ubibike;

import android.app.Application;

import java.lang.reflect.Array;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import pt.ulisboa.tecnico.cmu.ubibike.connection.PendingRequest;
import pt.ulisboa.tecnico.cmu.ubibike.connection.ServerCommunicationHandler;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Data;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.UpdatableUI;
import pt.ulisboa.tecnico.cmu.ubibike.managers.SessionManager;
import pt.ulisboa.tecnico.cmu.ubibike.managers.StorageManager;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.NearbyPeerCommunication;


public class ApplicationContext extends Application {

    private static ApplicationContext mInstance;

    private UbiBike mActivity;  //current activity instance
    private UpdatableUI mFragment;  //current opened fragment instance (null if not UI updatable)
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

                Data data = mStorageManager.getAppDataFromDB(mUid);

                PublicKey serverPK = mStorageManager.getServerPublicKeyFromDB(mUid);
                PrivateKey sKey = mStorageManager.getClientPrivateKeyFromDB(mUid);
                data.setPrivateKey(sKey);
                data.setServerPublicKey(serverPK);

                mPendingRequests = mStorageManager.getPendingRequestFromDB(mUid);

                mServerCommunicationHandler.setUid(data.getUID());
                mServerCommunicationHandler.setSessionToken(data.getSessionToken());

                setData(data);
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

    public void storeCurrentAppDataOnDB(){
        mStorageManager.updateAppDataOnDB(mUid, mData);
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
        mUid = mData.getUID();

        mServerCommunicationHandler.setUid(mData.getUID());
        mServerCommunicationHandler.setSessionToken(mData.getSessionToken());

        if(mData.getLastUserInfoUpdated() == null){
            getServerCommunicationHandler().performUserInfoRequest();
        }

        if(mData.getLastStationUpdated() == null){
            getServerCommunicationHandler().performStationsNearbyRequest();
        }

        if(!mData.hasPublicKeyToken()){
            getServerCommunicationHandler().performPublicKeyTokenRequest();
        }
    }

    public UbiBike getActivity(){
        return mActivity;
    }

    public void setActivity(UbiBike activity){
        mActivity = activity;
    }

    public UpdatableUI getCurrentFragment() {
        return mFragment;
    }

    public void setCurrentFragment(UpdatableUI mFragment) {
        this.mFragment = mFragment;
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

    public ArrayList<PendingRequest> getAllPendingRequests(){
        return mPendingRequests;
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

        for(int i = 0; i <= mPendingRequests.size()-1; i++){
            if(mPendingRequests.get(i).getID() == id){
                mPendingRequests.remove(i);
                return;
            }
        }
    }


    public void updateUI(){
        if(getCurrentFragment() != null){
            getCurrentFragment().updateUI();
        }
    }
}
