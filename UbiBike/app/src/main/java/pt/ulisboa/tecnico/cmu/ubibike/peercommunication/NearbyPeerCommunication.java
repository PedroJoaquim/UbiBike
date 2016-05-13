package pt.ulisboa.tecnico.cmu.ubibike.peercommunication;

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Data;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.tasks.OutgoingCommunicationTask;
import pt.ulisboa.tecnico.cmu.ubibike.utils.JsonParser;
import pt.ulisboa.tecnico.cmu.ubibike.utils.PointsTransactionUtils;


public class NearbyPeerCommunication {


    private String mDeviceName;

    private SimWifiP2pDeviceList mNearbyDevices;
    private HashMap<String, String> mNearDevicesUsernames;  //key = username name | value = deviceName
    private GroupChat mGroupChat;
    private HashMap<String, Chat> mIndividualChats; //key = username
    private ArrayList<String> mUsernames;

    public static final String MESSAGE_TYPE_INDIVIDUAL = "[individual]";
    public static final String MESSAGE_TYPE_GROUP = "[group]";
    public static final String USERNAME_BROADCAST = "[username]";
    public static final String GROUPOWNER_BROADCAST = "[groupowner]";
    private static final String POINTS_MESSAGE = "[points]";

    public static final String SEPARATOR = "ZABOLOTNYY";


    public NearbyPeerCommunication() {
        mNearbyDevices = new SimWifiP2pDeviceList();
        mNearDevicesUsernames = new HashMap<>();
        mGroupChat = new GroupChat();
        mIndividualChats = new HashMap<>();
        mUsernames = new ArrayList<>();
    }



    public String getDeviceName() {
        return mDeviceName;
    }

    public void setDeviceName(String deviceName) {
        mDeviceName = deviceName;
    }


    public SimWifiP2pDevice getDeviceNearbyByName(String deviceName){
        return mNearbyDevices.getByName(deviceName);
    }

    public SimWifiP2pDevice getDeviceNearbyByUsername(String username){

        if(mNearDevicesUsernames.containsKey(username)){
            return mNearbyDevices.getByName(mNearDevicesUsernames.get(username));
        }

        return null;
    }

    public String getDeviceVirtualAddressByName(String deviceName){
        SimWifiP2pDevice device = getDeviceNearbyByName(deviceName);
        return device == null ? null : device.virtDeviceAddress;
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
        if(!mIndividualChats.containsKey(username)){
            addIndividualChat(username);
        }

        return mIndividualChats.get(username);
    }


    public Set<String> getNearDevicesUsernamesSet(){
        return new HashSet<>(mNearDevicesUsernames.keySet());
    }


    public void addDeviceUsername(String deviceName, String username){
        mNearDevicesUsernames.put(username, deviceName);
        mUsernames.add(username);
    }

    public String getUsernameByDeviceName(String deviceName){
        for (Map.Entry<String, String> entry :  mNearDevicesUsernames.entrySet()) {
            if(entry.getValue().equals(deviceName)){
                return entry.getKey();
            }
        }

        return null;
    }
    /**
     * Building messages to send between peers
     */
    
    public static String buildIndividualChatMessage(String senderUsername, String message){
        return MESSAGE_TYPE_INDIVIDUAL + SEPARATOR + senderUsername + SEPARATOR + message;
    }

    public static String buildGroupChatMessage(String senderUsername, String message){
        return MESSAGE_TYPE_GROUP + SEPARATOR + senderUsername + SEPARATOR + message;
    }

    public static String buildUsernameBroadcastMessage(String myDeviceName, String myUsername, boolean isGO){

        String msg = USERNAME_BROADCAST + SEPARATOR +  myDeviceName + SEPARATOR + myUsername;

        return isGO ? GROUPOWNER_BROADCAST + SEPARATOR + msg : msg;
    }

    public static String buildPointsTransactionMessage(String transactionJSON){

        return POINTS_MESSAGE + SEPARATOR + transactionJSON;
    }

    
    /**
     * Parsing messages received from peers
     */
    public static String processReceivedMessage(String received){

        switch(received.split(SEPARATOR)[0]){
            case MESSAGE_TYPE_INDIVIDUAL: processIndividualChatMessage(received); break;
            case MESSAGE_TYPE_GROUP: processGroupChatMessage(received); break;
            case USERNAME_BROADCAST: processUsernameBroadcastMessage(received); break;
            case GROUPOWNER_BROADCAST: processGroupOwnerBroadcastMessage(received); break;
            case POINTS_MESSAGE: return processPointsTransactionMessage(received);
        }

        return "ok";
    }

    private static String processPointsTransactionMessage(String received) {

        String[] receivedParts = received.split(SEPARATOR);
        Data data = ApplicationContext.getInstance().getData();
        String json = receivedParts[1];

        JSONObject transactionJSON = JsonParser.parsePointsTransaction(json);


        if(transactionJSON == null){
            return "\n";
        }

        int points = PointsTransactionUtils.validateTransaction(transactionJSON);

        if(points == -1){
            return "\n";
        }

        int targetLogicalClock = data.getNextLogicalClock();
        data.addPoints(points);



        //send points to server
        JSONObject pointsTransactionServer = JsonParser.buildPointsTransactionServerJSON(transactionJSON, targetLogicalClock);
        ApplicationContext.getInstance().getServerCommunicationHandler().performPointsTransactionRequest(pointsTransactionServer);

        return "" + targetLogicalClock;
    }

    public static void processIndividualChatMessage(String received){

        String[] receivedParts = received.split(SEPARATOR);

        if(receivedParts.length < 3){
            return;
        }

        String senderUsername = receivedParts[1];
        String messageContent = receivedParts[2];

        ChatMessage msg = new ChatMessage(true, senderUsername, messageContent);

        ApplicationContext.getInstance().getNearbyPeerCommunication().
                                                getIndividualChat(senderUsername).addNewMessage(msg);
    }

    private static void processGroupChatMessage(String received) {
        String[] receivedParts = received.split(SEPARATOR);

        if(receivedParts.length < 3){
            return;
        }

        String senderUsername = receivedParts[1];
        String messageContent = receivedParts[2];

        ChatMessage msg = new ChatMessage(true, senderUsername, messageContent);

        ApplicationContext.getInstance().getNearbyPeerCommunication().getGroupChat().getChat().
                                                                                addNewMessage(msg);

    }

    private static void processUsernameBroadcastMessage(String received) {
        String[] receivedParts = received.split(SEPARATOR);

        String deviceName = receivedParts[1];
        String username = receivedParts[2];

        ApplicationContext.getInstance().getNearbyPeerCommunication().addDeviceUsername(deviceName, username);
    }

    private static void processGroupOwnerBroadcastMessage(String received) {
        String[] receivedParts = received.split(SEPARATOR);

        String groupOwnerUsername = receivedParts[3];
        String deviceName = receivedParts[2];

        ApplicationContext.getInstance().getNearbyPeerCommunication().getGroupChat().setOwner(groupOwnerUsername);
        ApplicationContext.getInstance().getNearbyPeerCommunication().addDeviceUsername(deviceName, groupOwnerUsername);
    }


    public void updateNearbyDevices(SimWifiP2pDeviceList nearbyDevices) {
        mNearbyDevices = nearbyDevices;
    }

    public void updateGroupDevices(SimWifiP2pInfo gInfo) {

        if(gInfo.askIsGO()){
            mGroupChat.setOwner(ApplicationContext.getInstance().getData().getUsername());
        }

        //add new members to group
        for(String device : gInfo.getDevicesInNetwork()) {

            //ignore devices that already have client socket
            if(mGroupChat.getMembers().contains(device)){
                continue;
            }

            mGroupChat.addMember(device);

            String myUsername = ApplicationContext.getInstance().getData().getUsername();
            String msg = NearbyPeerCommunication.buildUsernameBroadcastMessage(mDeviceName, myUsername, gInfo.askIsGO());


            new OutgoingCommunicationTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, device, msg);
        }

        List<String> removeDevices = new ArrayList<>();

        //remove old elements from group
        for (String device : mGroupChat.getMembers()) {
            if(gInfo.getDevicesInNetwork().contains(device)){
                continue;
            }
            removeDevices.add(device);
        }

        for (String device: removeDevices) {
            mGroupChat.removeMember(device);
            mUsernames.remove(getUsernameByDeviceName(device));
        }
    }

    public ArrayList<String> getGroupUsernameSet() {
        return mUsernames;
    }

    public boolean doesGroupMemberExistByUsername(String username){
        return mUsernames.contains(username);
    }
}
