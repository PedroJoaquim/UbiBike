package pt.ist.cmu.ubibike.httpserver.model;

import java.util.Date;

public class PointsTransaction {

    private int ptid;
    private int points;
    private int senderUID;
    private int receiverUID;
    private Date executionTimestamp;

    public PointsTransaction(int ptid, int senderUID, int receiverUID, int points, Date executionTimestamp) {
        this.ptid = ptid;
        this.senderUID = senderUID;
        this.receiverUID = receiverUID;
        this.points = points;
        this.executionTimestamp = executionTimestamp;
    }

    public int getPtid() {
        return ptid;
    }

    public void setPtid(int ptid) {
        this.ptid = ptid;
    }

    public int getSenderUID() {
        return senderUID;
    }

    public void setSenderUID(int senderUID) {
        this.senderUID = senderUID;
    }

    public int getReceiverUID() {
        return receiverUID;
    }

    public void setReceiverUID(int receiverUID) {
        this.receiverUID = receiverUID;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Date getExecutionTimestamp() {
        return executionTimestamp;
    }

    public void setExecutionTimestamp(Date executionTimestamp) {
        this.executionTimestamp = executionTimestamp;
    }
}
