package pt.ist.cmu.ubibike.httpserver.model;


public class Booking {

    private int bookingID;
    private int uid;
    private int bid;
    private int sourceSid;
    private boolean active;
    private long bookTimestamp;

    public Booking(int bookingID, int uid, int bid, int sourceSid, long bookTimestamp, boolean active) {
        this.bookingID = bookingID;
        this.uid = uid;
        this.bid = bid;
        this.sourceSid = sourceSid;
        this.active = active;
        this.bookTimestamp = bookTimestamp;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public boolean isActive() { return active;}

    public void setActive(boolean active) { this.active = active; }

    public long getBookTimestamp() {
        return bookTimestamp;
    }

    public void setBookTimestamp(long bookTimestamp) {
        this.bookTimestamp = bookTimestamp;
    }

    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public int getSourceSid() {
        return sourceSid;
    }

    public void setSourceSid(int sourceSid) {
        this.sourceSid = sourceSid;
    }
}
