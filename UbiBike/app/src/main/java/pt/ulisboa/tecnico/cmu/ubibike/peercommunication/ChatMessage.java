package pt.ulisboa.tecnico.cmu.ubibike.peercommunication;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ChatMessage implements Comparable {

    private boolean mReceived;
    private String mSenderUsername;
    private String mContent;
    private Date mDate;

    public ChatMessage(boolean received, String senderUsername, String content) {
        mReceived = received;
        mSenderUsername = senderUsername;
        mContent = content;
        mDate = new Date();
    }

    @Override
    public int compareTo(Object another) {

        ChatMessage msg2;

        if(another instanceof ChatMessage){
            msg2 = (ChatMessage) another;
            return  (int) (mDate.getTime() - msg2.getDate().getTime()); // no problem with the cast
        }
        else {
            return 0;
        }
    }

    public String getHour(){
        String formattedDate = new SimpleDateFormat("HH:mm").format(mDate);

        return formattedDate;
    }

    public String getDay() {
        String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(mDate);

        return formattedDate;
    }


    /*
     * Getters and Setters
     */

    public boolean isReceived() {
        return mReceived;
    }

    public String getContent() {
        return mContent;
    }

    public String getSenderUsername() {
        return mSenderUsername;
    }

    public Date getDate() {
        return mDate;
    }

}

