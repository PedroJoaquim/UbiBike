package pt.ulisboa.tecnico.cmu.ubibike;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.PeerListListener;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.GroupInfoListener;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Data;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.chats.ChatFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.chats.ChatsListFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.HomeFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.LoginFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.RegisterAccountFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.TrajectoryListFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.UserProfileFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.chats.GroupChatFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.chats.IndividualChatFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.maps.StationsNearbyMapFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.maps.TrajectoryViewMapFragment;
import pt.ulisboa.tecnico.cmu.ubibike.managers.MobileConnectionManager;
import pt.ulisboa.tecnico.cmu.ubibike.managers.SessionManager;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.tasks.IncomingCommunicationTask;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.tasks.OutgoingCommunicationTask;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.tasks.TransferDataTask;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.termite.SimWifiP2pBroadcastReceiver;
import pt.ulisboa.tecnico.cmu.ubibike.services.TrajectoryTracker;

import java.util.Date;

public class UbiBike extends AppCompatActivity implements PeerListListener, GroupInfoListener {

    private SessionManager mSessionManager;

    public static final String TAG = "UbiBike";

    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private Messenger mService = null;
    private boolean mBound = false;
    private SimWifiP2pBroadcastReceiver mReceiver;

    private NetworkChangeReceiver mNetworkChangeReceiver;
    private boolean mNetworkChangeReceiverRegistered = false;
    private boolean mInternetConnected;

    private PopupWindow mPopupWindow;
    private LayoutInflater mLayoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubi_bike);

        ApplicationContext.getInstance().setActivity(this);
        mSessionManager = new SessionManager(this);

        mNetworkChangeReceiver = new NetworkChangeReceiver();

        setViewElements();

        checkLogin();

        // initialize the WDSim API
        SimWifiP2pSocketManager.Init(getApplicationContext());

        registerBroadcastReceiver();

        //to guarantee that method is not invoked again on activity recreation
        if(savedInstanceState == null){
            wifiP2pTurnOn();
        }
    }



    private void checkLogin() {

        if(mSessionManager.isLoggedIn()){
            showHome();
        }
        else{
            showLogin();
        }

    }

    private void registerBroadcastReceiver() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        mReceiver = new SimWifiP2pBroadcastReceiver(this);
        registerReceiver(mReceiver, filter);
    }



    @Override
    protected void onResume() {
        super.onResume();

        if (!mNetworkChangeReceiverRegistered) {
            registerReceiver(mNetworkChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            mNetworkChangeReceiverRegistered = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();


        if (mNetworkChangeReceiverRegistered) {
            unregisterReceiver(mNetworkChangeReceiver);
            mNetworkChangeReceiverRegistered = false;

            if(mPopupWindow != null) mPopupWindow.dismiss();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mReceiver);
        wifiP2pTurnOff();

        ApplicationContext.getInstance().requestStopTrajectoryTrackingService();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_logout:
                ApplicationContext.getInstance().storeCurrentAppDataOnDB();
                mSessionManager.logoutUser();
                showLogin();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void setViewElements() {

        // Set a toolbar to replace the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }


    /**
     * Loads new fragment
     *
     * @param fragment - fragment to be showed
     */
    private void replaceFragment (Fragment fragment, boolean explicitReplace,  boolean addToBackStack){
        String backStateName =  fragment.getClass().getName();
        String fragmentTag = backStateName;
        boolean fragmentPopped = false;

        FragmentManager manager = getSupportFragmentManager();

        if(!explicitReplace){
            fragmentPopped = manager.popBackStackImmediate (backStateName, 0);
        }

        if(!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null){ //fragment not in back stack, create it.

            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.content_frame, fragment, fragmentTag);
            if(addToBackStack) {
                ft.addToBackStack(backStateName);
            }
            ft.commit();
        }
        else if(explicitReplace){

            manager.popBackStack();
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.content_frame, fragment, fragmentTag);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    /**
     * Creates a session to user and shows Home screen
     */
    public void finishLogin(){
        Data appData = ApplicationContext.getInstance().getData();
        mSessionManager.createLoginSession(appData.getUID());

        ApplicationContext.getInstance().getStorageManager().
                registerLoginCredentialsOnDB(appData.getUID(),
                                            appData.getUsername(),
                                            ApplicationContext.getInstance().getPassword());


        showHome();
    }

    /**
     * Shows Home screen
     */
    public void showHome(){
        Fragment fragment = new HomeFragment();
        replaceFragment(fragment, true, false);
    }

    /**
     * Shows Login screen
     */
    public void showLogin(){
        Fragment fragment = new LoginFragment();
        replaceFragment(fragment, true, false);
    }

    /**
     * Shows Register Account screen
     */
    public void showRegisterAccountFragment(){
        Fragment fragment = new RegisterAccountFragment();
        replaceFragment(fragment, false, true);
    }

    /**
     * Shows nearby bike stations on map
     */
    public void showBikeStationsNearbyOnMap(){
        Fragment fragment = new StationsNearbyMapFragment();
        replaceFragment(fragment, false, true);
    }

    public void showUserProfile() {
        Fragment fragment = new UserProfileFragment();
        replaceFragment(fragment, false, true);
    }

    /**
     * Shows past trajectories on list, most recent first
     */
    public void showPastTrajectoriesList(){

        if(ApplicationContext.getInstance().getData().getAllTrajectories().isEmpty()){
            Toast.makeText(UbiBike.this, "No trajectories registered yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        Fragment fragment = new TrajectoryListFragment();
        replaceFragment(fragment, false, true);
    }

    /**
     * Shows a given trajectory on map
     *
     * @param trajectoryID - trajectory to show
     * @param explicitReplace - not relevant here
     */
    public void showTrajectoryOnMap(int trajectoryID, boolean trackedTrajectoryView, boolean explicitReplace){
        Fragment fragment = new TrajectoryViewMapFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(TrajectoryViewMapFragment.TRAJECTORY_ID_KEY, trajectoryID);
        arguments.putBoolean(TrajectoryViewMapFragment.TRACKED_TRAJECTORY_VIEW_KEY, trackedTrajectoryView);

        fragment.setArguments(arguments);

        replaceFragment(fragment, explicitReplace, true);
    }


    /**
     * Shows nearby chats listing
     */
    public void showChats(){
        Fragment fragment = new ChatsListFragment();
        replaceFragment(fragment, false, true);
    }

    /**
     * Shows a group chat
     */
    public void showGroupChat(){
        Fragment fragment = new GroupChatFragment();
        replaceFragment(fragment, false, true);
    }

    /**
     * Shows a chat with the given user
     *
     * @param username - user chat to open
     */
    public void showIndividualChat(String username){
        Fragment fragment = new IndividualChatFragment();

        Bundle arguments = new Bundle();
        arguments.putString(ChatFragment.ARGUMENT_KEY_USERNAME, username);
        fragment.setArguments(arguments);

        replaceFragment(fragment, false, true);
    }

    /**
     * Broadcasts an intent to notify about user being near to booked bike
     * So that tracking services knows about it
     */
    public void notifyNearBookedBike(boolean bikeNearby){
        Intent sIntent = new Intent();
        sIntent.putExtra(TrajectoryTracker.NearBookedBikeReceiver.NEAR_BOOKED_BIKE, bikeNearby);
        sIntent.setAction(TrajectoryTracker.NearBookedBikeReceiver.NEAR_BOOKED_BIKE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(sIntent);
    }



    /**
     * Shows popup about inexistence of internet connection
     */
    public void showNoInternetConnectionPopupWindow() {
        mLayoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = mLayoutInflater.inflate(R.layout.popup, null);

        mPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        String lastSyncStr;


        if((ApplicationContext.getInstance().getData()  != null) &&
            (ApplicationContext.getInstance().getData().getLastUserInfoUpdated() != null)){

            lastSyncStr = "Last sync: " + ApplicationContext.getInstance().getData().getLastUpdatedRelativeString();
        }
        else{
            lastSyncStr = "";
        }


        TextView text = (TextView) popupView.findViewById(R.id.popup_content_textView);
        text.setText(lastSyncStr);
        ImageView btnDismiss = (ImageView) popupView.findViewById(R.id.popup_dismiss);
        btnDismiss.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                mPopupWindow.dismiss();
            }
        });


        //delay a bit to avoid showing popup before activity actually starts
        new Handler().postDelayed(new Runnable() {

            public void run() {
                View parent = findViewById(R.id.main);

                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }

                mPopupWindow.showAtLocation(parent, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 200);
            }
        }, 200L);
    }



    /**
     * BroadcastReceiver to update UI when internet is connected/disconnected
     */
    private class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {

            mInternetConnected = MobileConnectionManager.isOnline(context);

            if (mInternetConnected) {

                if(mPopupWindow != null){
                    mPopupWindow.dismiss();
                }


                if(ApplicationContext.getInstance().getData() != null) {

                    Date lastStationsUpdated = ApplicationContext.getInstance().getData().getLastStationUpdated();

                    //if there was no update yet, update
                    if(lastStationsUpdated == null){
                        ApplicationContext.getInstance().getServerCommunicationHandler().performStationsNearbyRequest();
                    }
                    //otherwise check if the stations were not updated for more that certain ammount of time
                    else {

                        long timeFromLastUpdate = new Date().getTime() - lastStationsUpdated.getTime();

                        if (timeFromLastUpdate > 1000 * 60 * 60 * 24 * 3) {    //3 days
                            ApplicationContext.getInstance().getServerCommunicationHandler().performStationsNearbyRequest();
                        }
                    }

                    ApplicationContext.getInstance().getServerCommunicationHandler().executeNextPendingRequest();
                }

            }
            else{
                showNoInternetConnectionPopupWindow();
            }
        }
    }


    /**
     * Termite listeners
     */
    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        //empty on purpose
    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices, SimWifiP2pInfo groupInfo) {
        SimWifiP2pBroadcastReceiver.processPeersChanged(devices);
        SimWifiP2pBroadcastReceiver.processNetworkMembership(groupInfo);
    }

    public void wifiP2pTurnOn(){

        Intent intent = new Intent(this, SimWifiP2pService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mBound = true;

        // spawn the chat server background task
        new IncomingCommunicationTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void wifiP2pTurnOff(){
            if (mBound) {
                unbindService(mConnection);
                mBound = false;
            }
    }

    public void wifiP2pConnectToPeer(String deviceName){

        new OutgoingCommunicationTask().executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR, deviceName);
    }

    public void wifiP2pSendMessageToPeer(String deviceName, String message){

        new TransferDataTask().executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR, deviceName, message);

    }

    public void wifiP2pRequestGroupsInfo(){
        if (mBound) {
            mManager.requestGroupInfo(mChannel, UbiBike.this);
        } else {
            Toast.makeText(this, "Service not bound",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void wifiP2pRequestPeersInfo(){
        if (mBound) {
            mManager.requestPeers(mChannel, UbiBike.this);
        } else {
            Toast.makeText(this, "Service not bound",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mManager = new SimWifiP2pManager(mService);
            mChannel = mManager.initialize(getApplication(), getMainLooper(), null);
            mBound = true;
            wifiP2pRequestGroupsInfo();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };


    /**
     * Get SessionManager
     */
    public SessionManager getSessionManager(){
        return mSessionManager;
    }

}
