package pt.ulisboa.tecnico.cmu.ubibike.peercommunication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;


public class NearbyPeerCommunication {

    private SimWifiP2pSocketServer mDeviceServerSocket;

    private HashMap<String, Device> mNearDevices;
    private GroupChat mGroupChat;


    public void addDeviceNearby(String deviceName, String virtualAddress){
        mNearDevices.put(deviceName, new Device(deviceName, virtualAddress));
    }

    public String getDeviceNearbyVirtualAddress(String deviceName){
        return mNearDevices.get(deviceName).getVirtualAddress();
    }

    public void removeDeviceNearby(String deviceName){
        mNearDevices.remove(deviceName);
    }

    public GroupChat getGroupChat() {
        return mGroupChat;
    }

    public void setGroupChat(GroupChat mGroupChat) {
        this.mGroupChat = mGroupChat;
    }

    public SimWifiP2pSocketServer getDeviceServerSocket() {
        return mDeviceServerSocket;
    }

    public void setDeviceServerSocket(SimWifiP2pSocketServer deviceServerSocket) {
        mDeviceServerSocket = deviceServerSocket;
    }

    public Set<String> getNearDevicesSet(){
        return mNearDevices.keySet();
    }

    public SimWifiP2pSocket getNearDeviceClientSocket(String deviceName){
        return mNearDevices.get(deviceName).getClientSocket();
    }

    public ArrayList<SimWifiP2pSocket> getNearDevicesClientSockets(){
        ArrayList<SimWifiP2pSocket> sockets = new ArrayList<>();

        for(Device d : mNearDevices.values()){
            sockets.add(d.getClientSocket());
        }

        return sockets;
    }

    public void addNearDeviceClientSocket(String deviceName, SimWifiP2pSocket clientSocket){
        mNearDevices.get(deviceName).setClientSockets(clientSocket);
    }

}
