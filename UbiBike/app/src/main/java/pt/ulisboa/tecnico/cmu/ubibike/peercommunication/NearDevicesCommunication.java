package pt.ulisboa.tecnico.cmu.ubibike.peercommunication;

import java.util.ArrayList;
import java.util.HashMap;

import pt.inesc.termite.wifidirect.SimWifiP2pDevice;


public class NearDevicesCommunication {

    private HashMap<String, SimWifiP2pDevice> peersNearby; //nearby users
    private ArrayList<GroupChat> groupChats;


    public void addPeerNearby(SimWifiP2pDevice device){
        peersNearby.put(device.deviceName, device);
    }
}
