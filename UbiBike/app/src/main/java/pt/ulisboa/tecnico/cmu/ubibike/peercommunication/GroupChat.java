package pt.ulisboa.tecnico.cmu.ubibike.peercommunication;


import java.util.HashMap;
import java.util.List;

public class GroupChat {

    private String mOwner;
    private HashMap<String, String> mMembers;
    private Chat mChat;

    public GroupChat() {
        mMembers = new HashMap<>();
        mChat = new Chat();
    }

    public void addMember(String deviceName, String virtualAddress){
        mMembers.put(deviceName, virtualAddress);
    }

    public void removeMember(String deviceName){
        mMembers.remove(deviceName);
    }

    public Chat getChat() {
        return mChat;
    }

    public List<ChatMessage> getAllMessages(){
        return mChat.getAllMessages();
    }

    public List<ChatMessage> getReceivedMessages(){
        return mChat.getReceivedMessages();
    }

    public ChatMessage getLastReceivedMessage(){
        return mChat.getLastReceivedMessage();
    }

}
