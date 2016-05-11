package pt.ist.cmu.ubibike.httpserver.model;


public class PendingEvent {

    public static final int TRAJECTORY_TYPE = 1;
    public static final int TRANSACTION_TYPE = 2;

    private int peID;
    private int sourceUID;
    private int sourceLogicalClock;
    private int targetUID;
    private int targetLogicalClock;
    private int points;
    private long transactionTimestamp;
    private int type;

    public PendingEvent(int peID, int sourceUID, int sourceLogicalClock, int targetUID, int targetLogicalClock, int points, long transactionTimestamp, int type) {
        this.peID = peID;
        this.sourceUID = sourceUID;
        this.sourceLogicalClock = sourceLogicalClock;
        this.targetUID = targetUID;
        this.targetLogicalClock = targetLogicalClock;
        this.points = points;
        this.transactionTimestamp = transactionTimestamp;
        this.type = type;
    }

    public int getPeID() {
        return peID;
    }

    public void setPeID(int peID) {
        this.peID = peID;
    }

    public int getSourceUID() {
        return sourceUID;
    }

    public void setSourceUID(int sourceUID) {
        this.sourceUID = sourceUID;
    }

    public int getSourceLogicalClock() {
        return sourceLogicalClock;
    }

    public void setSourceLogicalClock(int sourceLogicalClock) {
        this.sourceLogicalClock = sourceLogicalClock;
    }

    public int getTargetUID() {
        return targetUID;
    }

    public void setTargetUID(int targetUID) {
        this.targetUID = targetUID;
    }

    public int getTargetLogicalClock() {
        return targetLogicalClock;
    }

    public void setTargetLogicalClock(int targetLogicalClock) {
        this.targetLogicalClock = targetLogicalClock;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTransactionTimestamp() {
        return transactionTimestamp;
    }

    public void setTransactionTimestamp(long transactionTimestamp) {
        this.transactionTimestamp = transactionTimestamp;
    }

    public int getLogicalClockForUid(int uid){
        if(sourceUID == uid){
            return this.sourceLogicalClock;
        }
        else{
            return this.targetLogicalClock;
        }
    }
}
