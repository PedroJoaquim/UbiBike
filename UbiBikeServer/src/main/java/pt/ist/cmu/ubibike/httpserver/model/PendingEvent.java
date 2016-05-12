package pt.ist.cmu.ubibike.httpserver.model;


public class PendingEvent {

    public static final int TRAJECTORY_TYPE = 1;
    public static final int TRANSACTION_TYPE = 2;

    private int peID;
    private String sourceUsername;
    private int sourceLogicalClock;
    private String targetUsername;
    private int targetLogicalClock;
    private int points;
    private long transactionTimestamp;
    private int type;

    public PendingEvent(int peID, String sourceUsername, int sourceLogicalClock, String targetUsername, int targetLogicalClock, int points, long transactionTimestamp, int type) {
        this.peID = peID;
        this.sourceUsername = sourceUsername;
        this.sourceLogicalClock = sourceLogicalClock;
        this.targetUsername = targetUsername;
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

    public String getSourceUsername() {
        return sourceUsername;
    }

    public void setSourceUsername(String sourceUsername) {
        this.sourceUsername = sourceUsername;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
    }

    public int getSourceLogicalClock() {
        return sourceLogicalClock;
    }

    public void setSourceLogicalClock(int sourceLogicalClock) {
        this.sourceLogicalClock = sourceLogicalClock;
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

    public int getLogicalClockForUid(String username){
        if(sourceUsername.equals(username)){
            return this.sourceLogicalClock;
        }
        else{
            return this.targetLogicalClock;
        }
    }
}
