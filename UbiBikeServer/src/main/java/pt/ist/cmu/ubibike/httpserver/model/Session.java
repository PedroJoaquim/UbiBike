package pt.ist.cmu.ubibike.httpserver.model;

/**
 * Created by Pedro Joaquim on 12-03-2016.
 */
public class Session {

    private int uid;
    private int sessionID;
    private long startTimestamp;

    public Session(int uid, int sessionID, long startTimestamp) {
        this.uid = uid;
        this.sessionID = sessionID;
        this.startTimestamp = startTimestamp;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getSessionID() {
        return sessionID;
    }

    public void setSessionID(int sessionID) {
        this.sessionID = sessionID;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }
}
