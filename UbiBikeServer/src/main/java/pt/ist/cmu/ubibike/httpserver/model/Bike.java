package pt.ist.cmu.ubibike.httpserver.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by Pedro Joaquim on 15-03-2016.
 */
public class Bike {

    private int bid;

    public Bike(int bid) {
        this.bid = bid;
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
}
