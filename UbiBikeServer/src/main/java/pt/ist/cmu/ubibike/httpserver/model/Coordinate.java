package pt.ist.cmu.ubibike.httpserver.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by Pedro Joaquim on 15-03-2016.
 */
public class Coordinate {

    private double lat;
    private double lng;

    public Coordinate(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Coordinate() { }

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

    @Override
    public boolean equals(Object obj) {
            return (obj instanceof Coordinate) &&
                    ((Coordinate) obj).getLat() == this.getLat() &&
                    ((Coordinate) obj).getLng() == this.getLng();

    }
}
