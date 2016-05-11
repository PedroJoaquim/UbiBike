package pt.ulisboa.tecnico.cmu.ubibike.peercommunication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;


public class Chat {

    private String mUsername;
    private ChatMessage mLastReceivedMessage = null;
    private List<ChatMessage> mReceivedMessages = new ArrayList<>();
    private List<ChatMessage> mAllMessages = new ArrayList<>();

    public Chat(){}

    public Chat(String username) {
        mUsername = username;
    }

    public void addNewMessage(ChatMessage message){

        if(message.isReceived()){
            mReceivedMessages.add(message);
            mLastReceivedMessage = message;
            Collections.sort(mReceivedMessages);
        }

        mAllMessages.add(message);
        Collections.sort(mAllMessages);



        try{
            ApplicationContext.getInstance().getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ApplicationContext.getInstance().getCurrentFragment().updateUI();
                }
            });}
        catch (Exception e){
            System.out.println("");
        }
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
