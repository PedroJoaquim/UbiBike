package pt.ulisboa.tecnico.cmu.ubibike.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by andriy on 12.03.2016.
 */
public class ChatMessage implements Comparable {

    private boolean mReceived;
    private boolean mRead;
    private String mSenderDeviceName;
    private String mReceiverDeviceName;
    private String mContent;
    private Date mDate;

    public ChatMessage(boolean received, String senderDeviceName, String receiverDeviceName, String content) {
        mReceived = received;
        mRead = false;
        mSenderDeviceName = senderDeviceName;
        mReceiverDeviceName = receiverDeviceName;
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

    public String getReceiverDeviceName() {
        return mReceiverDeviceName;
    }

    public String getSenderDeviceName() {
        return mSenderDeviceName;
    }

    public Date getDate() {
        return mDate;
    }

    public void setRead(boolean mRead) {
        this.mRead = mRead;
    }
}
