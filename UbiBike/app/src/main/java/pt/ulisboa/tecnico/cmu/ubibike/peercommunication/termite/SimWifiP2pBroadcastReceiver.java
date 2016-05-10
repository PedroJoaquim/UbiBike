package pt.ulisboa.tecnico.cmu.ubibike.peercommunication.termite;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.UbiBike;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Bike;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.UpdatableUI;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.NearbyPeerCommunication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

public class SimWifiP2pBroadcastReceiver extends  BroadcastReceiver{
    private UbiBike mActivity;

    public SimWifiP2pBroadcastReceiver(UbiBike activity) {
        super();
        mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            int state = intent.getIntExtra(SimWifiP2pBroadcast.EXTRA_WIFI_STATE, -1);

            if (state == SimWifiP2pBroadcast.WIFI_P2P_STATE_ENABLED) {
                Toast.makeText(mActivity, "WiFi Direct enabled",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mActivity, "WiFi Direct disabled",
                        Toast.LENGTH_SHORT).show();
            }

        } else if (SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            Toast.makeText(mActivity, "Peer list changed",
                    Toast.LENGTH_SHORT).show();


            SimWifiP2pDeviceList devices = (SimWifiP2pDeviceList) intent.getSerializableExtra(
                                                            SimWifiP2pBroadcast.EXTRA_DEVICE_LIST);

            processPeersChanged(devices);


        } else if (SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION.equals(action)) {

            Toast.makeText(mActivity, "Network membership changed",
                    Toast.LENGTH_SHORT).show();

            SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
                    SimWifiP2pBroadcast.EXTRA_GROUP_INFO);


            processNetworkMembership(ginfo);



        } else if (SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION.equals(action)) {

            SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
                    SimWifiP2pBroadcast.EXTRA_GROUP_INFO);


            //TODO what about this?

        }
    }


    /**
     * Reacts to peers in range changed action
     */
    private void processPeersChanged(SimWifiP2pDeviceList devices) {

        checkBookedBikeInRange(devices);

        Set<String> devicesBeforeUpdate = ApplicationContext.getInstance().
                getNearbyPeerCommunication().getNearDevicesSet();

        Set<String> devicesAfterUpdate = new HashSet<>();

        for(SimWifiP2pDevice device : devices.getDeviceList()){
            devicesAfterUpdate.add(device.deviceName);
        }

        //getting new devices
        Set<String>  newDevices = new HashSet(devicesAfterUpdate);
        newDevices.removeAll(devicesBeforeUpdate);

        //register new devices,
        //establish socket connection with each of them and
        //send my username so that they know
        for(String newDevice : newDevices){

            if(newDevice.toLowerCase().startsWith("bike")){
                continue;
            }

            String newDeviceVirtAddr = devices.getByName(newDevice).virtDeviceAddress;

            ApplicationContext.getInstance().
                    getNearbyPeerCommunication().addDeviceNearby(newDevice, newDeviceVirtAddr);

            //creating and saving client socket
            ApplicationContext.getInstance().getActivity().getCommunicationTasks().
                    new OutgoingCommunicationTask().
                    executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, newDevice);


            String myDeviceName = ApplicationContext.getInstance().
                    getNearbyPeerCommunication().getDeviceName();

            //if I know my device name, send my username to other
            //otherwise delay this announcemet
            if(myDeviceName != null) {

                String myUsername = ApplicationContext.getInstance().getData().getUsername();
                String msg =  NearbyPeerCommunication.buildUsernameBroadcastMessage(myDeviceName,myUsername);

                //sending my username
                ApplicationContext.getInstance().getActivity().getCommunicationTasks().
                        new TransferDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        newDevice, msg);
            }
        }

        devicesBeforeUpdate.removeAll(devicesAfterUpdate);

        //removing them
        for(String notInRangeDevice : devicesBeforeUpdate){
            ApplicationContext.getInstance().getNearbyPeerCommunication().
                    removeDeviceNearby(notInRangeDevice);

        }


        UpdatableUI currentFragment =  ApplicationContext.getInstance().getCurrentFragment();

        //check if there is visible UI updatable fragment to update
        if(currentFragment != null){
            currentFragment.updateUI();
        }
    }


    /**
     *  Reacts to network membership changed action
     */
    private void processNetworkMembership(SimWifiP2pInfo ginfo) {

        String myDeviceName = ApplicationContext.getInstance()
                .getNearbyPeerCommunication().getDeviceName();


        //I haven't done my username broadcast yet
        if(myDeviceName == null){
            myDeviceName = ginfo.getDeviceName();

            ApplicationContext.getInstance().
                    getNearbyPeerCommunication().setDeviceName(myDeviceName);

            Set<String> nearDevices = ApplicationContext.getInstance().
                    getNearbyPeerCommunication().getNearDevicesSet();

            String myUsername = ApplicationContext.getInstance().getData().getUsername();


            for(String device : nearDevices){

                String msg = NearbyPeerCommunication.buildUsernameBroadcastMessage(myDeviceName, myUsername);

                ApplicationContext.getInstance().getActivity().getCommunicationTasks().
                        new TransferDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        device, msg);
            }
        }

        //setting group members to Set<String> containing device names
        ApplicationContext.getInstance().getNearbyPeerCommunication().
                getGroupChat().setMembers(new HashSet<>(ginfo.getDevicesInNetwork()));


        //If I'm the group owner
        //I announce that to peers nearby
        if(ginfo.askIsGO()){

            ApplicationContext.getInstance().getNearbyPeerCommunication().
                    getGroupChat().setOwner(ginfo.getDeviceName());


            //announce group members that I'm the group owner
            for(String groupMember : ginfo.getDevicesInNetwork()){

                String myUsername = ApplicationContext.getInstance().getData().getUsername();

                String msg = NearbyPeerCommunication.buildGroupOwnerBroadcastMessage(myUsername);

                ApplicationContext.getInstance().getActivity().getCommunicationTasks().
                        new TransferDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        groupMember, msg);
            }
        }


        UpdatableUI currentFragment =  ApplicationContext.getInstance().getCurrentFragment();

        //check if there is visible UI updatable fragment to update
        if(currentFragment != null){
            currentFragment.updateUI();
        }

    }

    /**
     * Checks whether or not user is near to booked bike
     * Notifies Trajectory tracking service if so
     *
     * @param devices - peers in range list
     */
    private void checkBookedBikeInRange(SimWifiP2pDeviceList devices){

        Bike bookedBike = ApplicationContext.getInstance().getData().getBikeBooked();

        //if there is booked bike, check whether or not is nearby
        if(bookedBike != null) {

            for (SimWifiP2pDevice device : devices.getDeviceList()) {

                //we are near booked bike
                if (device.deviceName.equals(bookedBike.getUuid())) {
                    mActivity.notifyNearBookedBike(true);
                    return;
                }
            }

            //if we didnt return yet, booked bike isnt in range -> notify tracking service
            mActivity.notifyNearBookedBike(false);
        }
    }
}