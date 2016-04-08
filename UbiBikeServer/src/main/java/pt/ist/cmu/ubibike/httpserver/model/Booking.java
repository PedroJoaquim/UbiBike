package pt.ist.cmu.ubibike.httpserver.model;


public class Booking {

    private int uid;
    private int bid;
    private boolean active;
    private long bookTimestamp;

    public Booking(int uid, int bid, long bookTimestamp, boolean active) {
        this.uid = uid;
        this.bid = bid;
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
}
