package pt.ulisboa.tecnico.cmu.ubibike.peercommunication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;


public class NearbyPeerCommunication {

    private String mDeviceName;
    private SimWifiP2pSocketServer mDeviceServerSocket;

    private HashMap<String, Device> mNearDevices;   //key = device name
    private HashMap<String, String> mNearDevicesUsernames;  //key = username name | value = deviceName
    private GroupChat mGroupChat;
    private HashMap<String, Chat> mIndividualChats; //key = username


    public static final String MESSAGE_TYPE_INDIVIDUAL = "[individual]";
    public static final String MESSAGE_TYPE_GROUP = "[group]";
    public static final String USERNAME_BROADCAST = "[username]";
    public static final String GROUPOWNER_BROADCAST = "[groupowner]";
    public static final String SPACE = " ";


    public NearbyPeerCommunication() {
        mNearDevices = new HashMap<>();
        mNearDevicesUsernames = new HashMap<>();
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

        for(Map.Entry entry : mNearDevicesUsernames.entrySet()){
            if(entry.getValue() == deviceName){
                mNearDevicesUsernames.remove(entry.getKey());
                return;
            }
        }
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
        return mNearDevicesUsernames.keySet();
    }

    public SimWifiP2pSocket getNearDeviceClientSocketByDeviceName(String deviceName){
        return mNearDevices.get(deviceName).getClientSocket();
    }

    public SimWifiP2pSocket getNearDeviceClientSocketByUsername(String username){
        String deviceName = mNearDevicesUsernames.get(username);

        return getNearDeviceClientSocketByDeviceName(deviceName);
    }


    public void addNearDeviceClientSocket(String deviceName, SimWifiP2pSocket clientSocket){
        mNearDevices.get(deviceName).setClientSockets(clientSocket);
    }

    public void addNearDeviceUsername(String deviceName, String username){
        Device dev = mNearDevices.get(deviceName);

        if(dev != null){
            dev.setUsername(username);
            mNearDevicesUsernames.put(username, deviceName);
        }
    }



    /**
     * Building messages to send between peers
     */
    
    public static String buildIndividualChatMessage(String senderUsername, String message){
        return MESSAGE_TYPE_INDIVIDUAL + SPACE + senderUsername + SPACE + message;
    }

    public static String buildGroupChatMessage(String senderUsername, String message){
        return MESSAGE_TYPE_GROUP + SPACE + senderUsername + SPACE + message;
    }

    public static String buildUsernameBroadcastMessage(String myDeviceName, String myUsername){
        return USERNAME_BROADCAST + SPACE +  myDeviceName + SPACE + myUsername;
    }

    public static String buildGroupOwnerBroadcastMessage(String myUsername){
        return GROUPOWNER_BROADCAST + SPACE + myUsername;
    }


    
    /**
     * Parsing messages received from peers
     */
    public static void processReceivedMessage(String received){

        switch(received.split(SPACE)[0]){
            case MESSAGE_TYPE_INDIVIDUAL: processIndividualChatMessage(received); break;
            case MESSAGE_TYPE_GROUP: processGroupChatMessage(received); break;
            case USERNAME_BROADCAST: processUsernameBroadcastMessage(received); break;
            case GROUPOWNER_BROADCAST: processGroupOwnerBroadcastMessage(received); break;
        }

    }

    public static void processIndividualChatMessage(String received){
        String[] receivedParts = received.split(SPACE);

        String senderUsername = receivedParts[1];
        String messageContent = receivedParts[2];

        ChatMessage msg = new ChatMessage(true, senderUsername, messageContent);

        ApplicationContext.getInstance().getNearbyPeerCommunication().
                                                getIndividualChat(senderUsername).addNewMessage(msg);
    }

    private static void processGroupChatMessage(String received) {
        String[] receivedParts = received.split(SPACE);

        String senderUsername = receivedParts[1];
        String messageContent = receivedParts[2];

        ChatMessage msg = new ChatMessage(true, senderUsername, messageContent);

        ApplicationContext.getInstance().getNearbyPeerCommunication().getGroupChat().getChat().
                                                                                addNewMessage(msg);

    }

    private static void processUsernameBroadcastMessage(String received) {
        String[] receivedParts = received.split(SPACE);

        String deviceName = receivedParts[1];
        String username = receivedParts[2];

        ApplicationContext.getInstance().getNearbyPeerCommunication().addNearDeviceUsername(deviceName, username);
    }

    private static void processGroupOwnerBroadcastMessage(String received) {
        String[] receivedParts = received.split(SPACE);

        String groupOwnerUsername = receivedParts[1];

        ApplicationContext.getInstance().getNearbyPeerCommunication().getGroupChat().setOwner(groupOwnerUsername);
    }





}
