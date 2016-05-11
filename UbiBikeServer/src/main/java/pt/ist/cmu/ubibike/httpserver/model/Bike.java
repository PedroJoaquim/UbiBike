package pt.ist.cmu.ubibike.httpserver.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by Pedro Joaquim on 15-03-2016.
 */
public class Bike {

    private int bid;
    private String bikeAddr;
    private int sid;

    public Bike(int bid, String bikeAddr, int sid) {

        this.bid = bid;
        this.bikeAddr = bikeAddr;
        this.sid = sid;
    }

    public Bike() {}

    @JsonGetter("bid")
    public int getBid() {
        return bid;
    }

    @JsonSetter("bid")
    public void setBid(int bid) {
        this.bid = bid;
    }

    @JsonGetter("bike_addr")
    public String getBikeAddr() {
        return bikeAddr;
    }

    @JsonSetter("bike_addr")
    public void setBikeAddr(String bikeAddr) {
        this.bikeAddr = bikeAddr;
    }

    @JsonGetter("sid")
    public int getSid() {
        return sid;
    }

    @JsonSetter("sid")
    public void setSid(int sid) {
        this.sid = sid;
    }
}
