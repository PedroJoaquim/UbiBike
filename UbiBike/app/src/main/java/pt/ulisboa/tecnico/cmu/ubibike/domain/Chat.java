package pt.ulisboa.tecnico.cmu.ubibike.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by andriy on 12.03.2016.
 */
public class Chat {

    private String mClientID;
    private String mClientUsername;

    private ChatMessage mLastReceivedMessage = null;
    private List<ChatMessage> mReceivedMessages = new ArrayList<>();
    private List<ChatMessage> mAllMessages = new ArrayList<>();

    public Chat(String clientID, String clientUsername){
        mClientID = clientID;
        mClientUsername = clientUsername;
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

    public ChatMessage getLastReceivedMessage() {
        return mLastReceivedMessage;
    }

    public ChatMessage getLastMessage(){
        return mAllMessages.get(mAllMessages.size() - 1);
    }

    public void setAllMessagesRead(){

        for (ChatMessage msg: mAllMessages)
            msg.setRead(true);
    }
}

