package pt.ulisboa.tecnico.cmu.ubibike.peercommunication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;


public class NearbyPeerCommunication {

    private String mDeviceName;
    private SimWifiP2pSocketServer mDeviceServerSocket;

    private HashMap<String, Device> mNearDevices;   //key = device name
    private GroupChat mGroupChat;
    private HashMap<String, Chat> mIndividualChats; //key = username


    public NearbyPeerCommunication() {
        mNearDevices = new HashMap<>();
        mGroupChat = new GroupChat();
        mIndividualChats = new HashMap<>();
    }

    public String getDeviceName() {
        return mDeviceName;
    }

    public void setDeviceName(String deviceName) {
        mDeviceName = deviceName;
    }

    public void addDeviceNearby(String deviceName, String virtualAddress){
        mNearDevices.put(deviceName, new Device(deviceName, virtualAddress));
    }

    public Device getDeviceNearby(String deviceName){
        return mNearDevices.get(deviceName);
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

    public void addIndividualChat(String username){
        mIndividualChats.put(username, new Chat(username));
    }

    public boolean doesIndividualChatExist(String username){
        return mIndividualChats.containsKey(username);
    }

    public Chat getIndividualChat(String username){
        return mIndividualChats.get(username);
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

    public Set<String> getNearDevicesUsernamesSet(){
        Set<String> usernames = new HashSet<>();

        for(String deviceName : mNearDevices.keySet()){
            usernames.add(getDeviceNearby(deviceName).getUsername());
        }

        return usernames;
    }

    public SimWifiP2pSocket getNearDeviceClientSocket(String deviceName){
        return mNearDevices.get(deviceName).getClientSocket();
    }


    public void addNearDeviceClientSocket(String deviceName, SimWifiP2pSocket clientSocket){
        mNearDevices.get(deviceName).setClientSockets(clientSocket);
    }

}
