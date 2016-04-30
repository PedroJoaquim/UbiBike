package pt.ulisboa.tecnico.cmu.ubibike.peercommunication;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupChat {

    private String mOwner;
    private Set<String> mMembers;


    private ChatMessage mLastReceivedMessage = null;
    private List<ChatMessage> mReceivedMessages = new ArrayList<>();
    private List<ChatMessage> mAllMessages = new ArrayList<>();


    public GroupChat() {
        mMembers = new HashSet<>();
    }

    public void addNewMessage(ChatMessage message){

        if(message.isReceived()){
            mReceivedMessages.add(message);
            mLastReceivedMessage = message;
            Collections.sort(mReceivedMessages);
        }

        mAllMessages.add(message);
        Collections.sort(mAllMessages);
    }

    public List<ChatMessage> getAllMessages(){
        return mAllMessages;
    }

    public List<ChatMessage> getReceivedMessages(){
        return mReceivedMessages;
    }

    public ChatMessage getLastMessage(){
        return mAllMessages.get(mAllMessages.size() - 1);
    }

    public ChatMessage getLastReceivedMessage() {
        return mLastReceivedMessage;
    }
}
