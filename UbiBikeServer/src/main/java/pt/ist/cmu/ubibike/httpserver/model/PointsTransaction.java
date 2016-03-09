package pt.ist.cmu.ubibike.httpserver.model;

/**
 * Created by ASUS on 09/03/2016.
 */
public class PointsTransaction {

    private int ptid;
    private String senderUID;
    private String receiverUID;
    private int points;

    public PointsTransaction(int ptid, String senderUID, String receiverUID, int points) {
        this.ptid = ptid;
        this.senderUID = senderUID;
        this.receiverUID = receiverUID;
        this.points = points;
    }

    public int getPtid() {
        return ptid;
    }

    public void setPtid(int ptid) {
        this.ptid = ptid;
    }

    public String getSenderUID() {
        return senderUID;
    }

    public void setSenderUID(String senderUID) {
        this.senderUID = senderUID;
    }

    public String getReceiverUID() {
        return receiverUID;
    }

    public void setReceiverUID(String receiverUID) {
        this.receiverUID = receiverUID;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
