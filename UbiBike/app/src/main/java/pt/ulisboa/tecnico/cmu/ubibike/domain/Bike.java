package pt.ulisboa.tecnico.cmu.ubibike.domain;


public class Bike {

    private int bid;
    private int sid;
    private String uuid;    //in our case the WIFI Direct peers device name

    public Bike(int bid, String uuid, int sid) {
        this.bid = bid;
        this.uuid = uuid;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}