package pt.ist.cmu.ubibike.httpserver.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by Pedro Joaquim on 15-03-2016.
 */
public class Station {

    private int sid;
    private String stationName;
    private double lat;
    private double lng;
    private Bike[] availableBikes;

    public Station(int sid, String stationName, double lat, double lng, Bike[] availableBikes) {
        this.sid = sid;
        this.stationName = stationName;
        this.lat = lat;
        this.lng = lng;
        this.availableBikes = availableBikes;
    }

    public Station(){

    }

    @JsonGetter("sid")
    public int getSid() {
        return sid;
    }

    @JsonSetter("sid")
    public void setSid(int sid) {
        this.sid = sid;
    }

    @JsonGetter("station_name")
    public String getStationName() {
        return stationName;
    }

    @JsonSetter("station_name")
    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    @JsonGetter("lat")
    public double getLat() {
        return lat;
    }

    @JsonSetter("lat")
    public void setLat(double lat) {
        this.lat = lat;
    }

    @JsonGetter("lng")
    public double getLng() {
        return lng;
    }

    @JsonSetter("lng")
    public void setLng(double lng) {
        this.lng = lng;
    }

    @JsonGetter("bikes_available")
    public Bike[] getAvailableBikes() {
        return availableBikes;
    }

    @JsonSetter("bikes_available")
    public void setAvailableBikes(Bike[] availableBikes) {
        this.availableBikes = availableBikes;
    }
}
