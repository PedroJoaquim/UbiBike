package pt.ulisboa.tecnico.cmu.ubibike.peercommunication.termite;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.UbiBike;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Bike;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.UpdatableUI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

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
    public static void processPeersChanged(SimWifiP2pDeviceList devices) {

        checkBookedBikeInRange(devices);


        ApplicationContext.getInstance()
                .getNearbyPeerCommunication().updateNearbyDevices(devices);

    }

    /**
     *  Reacts to network membership changed action
     */
    public static void processNetworkMembership(SimWifiP2pInfo ginfo) {

        //Get my own device name
        ApplicationContext.getInstance()
                .getNearbyPeerCommunication().setDeviceName(ginfo.getDeviceName());


        ApplicationContext.getInstance().getNearbyPeerCommunication().updateGroupDevices(ginfo);


        ApplicationContext.getInstance().updateUI();
    }

    /**
     * Checks whether or not user is near to booked bike
     * Notifies Trajectory tracking service if so
     *
     * @param devices - peers in range list
     */
    private static void checkBookedBikeInRange(SimWifiP2pDeviceList devices){

        if(!ApplicationContext.getInstance().dataExists()) return;

        Bike bookedBike = ApplicationContext.getInstance().getData().getBikeBooked();

        //if there is booked bike, check whether or not is nearby
        if(bookedBike != null) {

            for (SimWifiP2pDevice device : devices.getDeviceList()) {

                //we are near booked bike
                if (device.deviceName.equals(bookedBike.getUuid())) {
                    ApplicationContext.getInstance().getActivity().notifyNearBookedBike(true);
                    return;
                }
            }

            //if we didnt return yet, booked bike isnt in range -> notify tracking service
            ApplicationContext.getInstance().getActivity().notifyNearBookedBike(false);
        }
    }
}