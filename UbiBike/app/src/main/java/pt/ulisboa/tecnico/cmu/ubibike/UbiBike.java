package pt.ulisboa.tecnico.cmu.ubibike;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;


import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.PeerListListener;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.GroupInfoListener;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Data;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Trajectory;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.HomeFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.LoginFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.MapFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.RegisterAccountFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.TrajectoryListFragment;
import pt.ulisboa.tecnico.cmu.ubibike.managers.MobileConnectionManager;
import pt.ulisboa.tecnico.cmu.ubibike.managers.SessionManager;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.termite.SimWifiP2pBroadcastReceiver;
import pt.ulisboa.tecnico.cmu.ubibike.services.TrajectoryTracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

public class UbiBike extends AppCompatActivity /*implements PeerListListener, GroupInfoListener*/ {

    private SessionManager mSessionManager;

    public static final String TAG = "UbiBike";

    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private SimWifiP2pSocketServer mSrvSocket = null;
    private SimWifiP2pSocket mCliSocket = null;
    private Messenger mService = null;
    private boolean mBound = false;
    private SimWifiP2pBroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubi_bike);

        ApplicationContext.getInstance().setActivity(this);
        mSessionManager = new SessionManager(this);

        setViewElements();

        checkLogin();

        // initialize the WDSim API
        SimWifiP2pSocketManager.Init(getApplicationContext());

        registerBroadcastReceiver();


        if(savedInstanceState == null){
            Intent i = new Intent(this, TrajectoryTracker.class);
            i.putExtra(TrajectoryTracker.TRAJECTORY_ID, 0);
            i.putExtra(TrajectoryTracker.START_STATION_ID, 0);
            i.putExtra(TrajectoryTracker.START_STATION_LATITUDE, 0.0);
            i.putExtra(TrajectoryTracker.START_STATION_LONGITUDE, 0.0);
            startService(i);
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

        getSupportActionBar().show();
        getSupportActionBar().setTitle("UbiBike");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_logout:
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
        mSessionManager.createLoginSession(appData.getUid());

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
    public void showBikeStationsNearbyOnMap(boolean afterRequest){


        if(!afterRequest && MobileConnectionManager.isOnline(this)){    //perform request
            ApplicationContext.getInstance().getServerCommunicationHandler().performStationsNearbyRequest();
            return;
        }

        Fragment fragment = new MapFragment();
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
        Fragment fragment = new MapFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(MapFragment.TRAJECTORY_ID_KEY, trajectoryID);
        arguments.putBoolean(MapFragment.TRACKED_TRAJECTORY_VIEW_KEY, trackedTrajectoryView);
        fragment.setArguments(arguments);

        replaceFragment(fragment, explicitReplace, true);
    }

    public void startTrajectoryTracking(){

        int maxTrajectoryID = 0;

        for(Trajectory t : ApplicationContext.getInstance().getData().getAllTrajectories()){
            maxTrajectoryID = (maxTrajectoryID < t.getTrajectoryID()) ? t.getTrajectoryID() : maxTrajectoryID;
        }
        maxTrajectoryID++;



        Intent i = new Intent(this, TrajectoryTracker.class);
        i.putExtra(TrajectoryTracker.TRAJECTORY_ID, maxTrajectoryID);
        i.putExtra(TrajectoryTracker.START_STATION_ID, 0);
        i.putExtra(TrajectoryTracker.START_STATION_LATITUDE, 0.0);
        i.putExtra(TrajectoryTracker.START_STATION_LONGITUDE, 0.0);
        startService(i);
    }

    /**
     * Broadcasts an intent to stop the trajectory tracking
     */
    public void requestStopTrajectoryTracking(){
        Intent sIntent = new Intent();
        sIntent.setAction(TrajectoryTracker.StopTrajectoryTrackingReceiver.ACTION_STOP);
        sendBroadcast(sIntent);
    }


    /**
     * Finishes trajectory tracking and shows it to the user
     */
    public void finishTrajectoryTracking(){
        Trajectory trackedTrajectory = ApplicationContext.getInstance().getData().getLastTrackedTrajectory();

        showTrajectoryOnMap(trackedTrajectory.getTrajectoryID(), true, false);
    }



//
//    private OnClickListener listenerWifiOffButton = new OnClickListener() {
//        public void onClick(View v){
//            if (mBound) {
//                unbindService(mConnection);
//                mBound = false;
//                //guiUpdateInitState();
//            }
//        }
//    };
//
//    private OnClickListener listenerInRangeButton = new OnClickListener() {
//        public void onClick(View v){
//            if (mBound) {
//                mManager.requestPeers(mChannel, UbiBike.this);
//            } else {
//                Toast.makeText(v.getContext(), "Service not bound",
//                        Toast.LENGTH_SHORT).show();
//            }
//        }
//    };
//
//    private OnClickListener listenerInGroupButton = new OnClickListener() {
//        public void onClick(View v){
//            if (mBound) {
//                mManager.requestGroupInfo(mChannel, UbiBike.this);
//            } else {
//                Toast.makeText(v.getContext(), "Service not bound",
//                        Toast.LENGTH_SHORT).show();
//            }
//        }
//    };
//
//    private OnClickListener listenerConnectButton = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            findViewById(R.id.idConnectButton).setEnabled(false);
//            new OutgoingCommTask().executeOnExecutor(
//                    AsyncTask.THREAD_POOL_EXECUTOR,
//                    mTextInput.getText().toString());
//        }
//    };
//
//    private OnClickListener listenerSendButton = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            findViewById(R.id.idSendButton).setEnabled(false);
//
//            new SendCommTask().executeOnExecutor(
//                    AsyncTask.THREAD_POOL_EXECUTOR,
//                    mTextInput.getText().toString());
//        }
//    };
//
//    private OnClickListener listenerDisconnectButton = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            findViewById(R.id.idDisconnectButton).setEnabled(false);
//            if (mCliSocket != null) {
//                try {
//                    mCliSocket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            mCliSocket = null;
//            guiUpdateDisconnectedState();
//        }
//    };
//
//    private ServiceConnection mConnection = new ServiceConnection() {
//        // callbacks for service binding, passed to bindService()
//
//        @Override
//        public void onServiceConnected(ComponentName className, IBinder service) {
//            mService = new Messenger(service);
//            mManager = new SimWifiP2pManager(mService);
//            mChannel = mManager.initialize(getApplication(), getMainLooper(), null);
//            mBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            mService = null;
//            mManager = null;
//            mChannel = null;
//            mBound = false;
//        }
//    };
//
//
//    /*
//	 * Asynctasks implementing message exchange
//	 */
//
//    public class IncommingCommTask extends AsyncTask<Void, String, Void> {
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            Log.d(TAG, "IncommingCommTask started (" + this.hashCode() + ").");
//
//            try {
//                mSrvSocket = new SimWifiP2pSocketServer(
//                        Integer.parseInt(getString(R.string.port)));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            while (!Thread.currentThread().isInterrupted()) {
//                try {
//                    SimWifiP2pSocket sock = mSrvSocket.accept();
//                    try {
//                        BufferedReader sockIn = new BufferedReader(
//                                new InputStreamReader(sock.getInputStream()));
//                        String st = sockIn.readLine();
//                        publishProgress(st);
//                        sock.getOutputStream().write(("\n").getBytes());
//                    } catch (IOException e) {
//                        Log.d("Error reading socket:", e.getMessage());
//                    } finally {
//                        sock.close();
//                    }
//                } catch (IOException e) {
//                    Log.d("Error socket:", e.getMessage());
//                    break;
//                    //e.printStackTrace();
//                }
//            }
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(String... values) {
//            mTextOutput.append(values[0] + "\n");
//        }
//    }
//
//    public class OutgoingCommTask extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected void onPreExecute() {
//            mTextOutput.setText("Connecting...");
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            try {
//                mCliSocket = new SimWifiP2pSocket(params[0],
//                        Integer.parseInt(getString(R.string.port)));
//            } catch (UnknownHostException e) {
//                return "Unknown Host:" + e.getMessage();
//            } catch (IOException e) {
//                return "IO error:" + e.getMessage();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            if (result != null) {
//                guiUpdateDisconnectedState();
//                mTextOutput.setText(result);
//            } else {
//                findViewById(R.id.idDisconnectButton).setEnabled(true);
//                findViewById(R.id.idConnectButton).setEnabled(false);
//                findViewById(R.id.idSendButton).setEnabled(true);
//                mTextInput.setHint("");
//                mTextInput.setText("");
//                mTextOutput.setText("");
//            }
//        }
//    }
//
//    public class SendCommTask extends AsyncTask<String, String, Void> {
//
//        @Override
//        protected Void doInBackground(String... msg) {
//            try {
//                mCliSocket.getOutputStream().write((msg[0] + "\n").getBytes());
//                BufferedReader sockIn = new BufferedReader(
//                        new InputStreamReader(mCliSocket.getInputStream()));
//                sockIn.readLine();
//                mCliSocket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            mCliSocket = null;
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            mTextInput.setText("");
//            //guiUpdateDisconnectedState();
//        }
//    }
//
//
//    /**
//     * Termite listeners
//     */
//    @Override
//    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
//
//        for(SimWifiP2pDevice peer : peers.getDeviceList()){
//            ApplicationContext.getInstance().getData().addPeerNearby(peer);
//        }
//
//    }
//
//    @Override
//    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices, SimWifiP2pInfo groupInfo) {
//
//        groupInfo.askIsClient();
//
//        // compile list of network members
//        StringBuilder peersStr = new StringBuilder();
//        for (String deviceName : groupInfo.getDevicesInNetwork()) {
//            SimWifiP2pDevice device = devices.getByName(deviceName);
//            String devstr = "" + deviceName + " (" +
//                    ((device == null)?"??":device.getVirtIp()) + ")\n";
//            peersStr.append(devstr);
//        }
//
//        // display list of network members
//        new AlertDialog.Builder(this)
//                .setTitle("Devices in WiFi Network")
//                .setMessage(peersStr.toString())
//                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                })
//                .show();
//
//    }

}
