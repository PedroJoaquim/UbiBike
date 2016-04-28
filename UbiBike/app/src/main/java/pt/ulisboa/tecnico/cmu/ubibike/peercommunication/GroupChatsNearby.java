package pt.ulisboa.tecnico.cmu.ubibike.peercommunication;

import java.util.ArrayList;
import java.util.HashMap;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;


public class GroupChatsNearby {

    private SimWifiP2pSocketServer mDeviceServerSocket;

    private HashMap<String, String> mDevicesNearby;
    private HashMap<String, SimWifiP2pSocket> mDevicesClientSockets;
    private HashMap<String, GroupChat> mGroupChats;


    public boolean addDeviceNearbyIfNotExists(String deviceName, String virtualAddress){
        boolean existed = mDevicesNearby.containsKey(deviceName);

        if(!existed) {
            mDevicesNearby.put(deviceName, virtualAddress);
        }

        return !existed;
    }

    public String getDeviceNearbyVirtualAddress(String deviceName){
        return mDevicesNearby.get(deviceName);
    }

    public void removeDeviceNearby(String deviceName){
        mDevicesNearby.remove(deviceName);
    }

    public void addGroupChat(String groupOwner, GroupChat groupChat){
        mGroupChats.put(groupOwner, groupChat);
    }

    public void removeGroupChat(String groupOwner){
        mGroupChats.remove(groupOwner);
    }

    public GroupChat getGroupChatByGroupOwner(String groupOwner){
        return mGroupChats.get(groupOwner);
    }

    public HashMap<String, GroupChat> getGroupChats(){
        return mGroupChats;
    }


    public SimWifiP2pSocketServer getDeviceServerSocket() {
        return mDeviceServerSocket;
    }

    public void setDeviceServerSocket(SimWifiP2pSocketServer mDeviceServerSocket) {
        mDeviceServerSocket = mDeviceServerSocket;
    }

    public SimWifiP2pSocket getNearDeviceClientSocket(String deviceName){
        return mDevicesClientSockets.get(deviceName);
    }

    public ArrayList<SimWifiP2pSocket> getAllDeviceClientSockets(){
        return new ArrayList<>(mDevicesClientSockets.values());
    }

    public void addNearDeviceClientSocket(String deviceName, SimWifiP2pSocket clientSocket){
        mDevicesClientSockets.put(deviceName, clientSocket);
    }
}
