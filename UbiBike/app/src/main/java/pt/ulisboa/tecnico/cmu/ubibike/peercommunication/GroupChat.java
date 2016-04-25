package pt.ulisboa.tecnico.cmu.ubibike.peercommunication;


import java.util.HashMap;

public class GroupChat {

    private String mOwner;
    private HashMap<String, String> mMembers;
    private Chat mChat;


    public void addMember(String deviceName, String virtualAddress){
        mMembers.put(deviceName, virtualAddress);
    }

    public void removeMember(String deviceName){
        mMembers.remove(deviceName);
    }

}
