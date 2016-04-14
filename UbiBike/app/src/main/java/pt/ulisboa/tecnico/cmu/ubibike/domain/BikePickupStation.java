package pt.ulisboa.tecnico.cmu.ubibike.domain;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by andriy on 12.03.2016.
 */
public class BikePickupStation {

    private int sid;
    private String mStationName;
    private double mPositionLatitude;
    private double mPositionLongitude;
    private ArrayList<Integer> mBikesAvailable;

    public BikePickupStation(String sid, String stationName, double positionLatitude, double positionLongitude, ArrayList<Integer> bikesAvailable) {
        mStationName = stationName;
        mBikesAvailable = bikesAvailable;
        mPositionLongitude = positionLongitude;
        mPositionLatitude = positionLatitude;
    }

    public int getSid() {
        return sid;
    }

    public String getStationName() {
        return mStationName;
    }

    public double getPositionLatitude() {
        return mPositionLatitude;
    }

    public double getPositionLongitude() {
        return mPositionLongitude;
    }

    public LatLng getStationPosition(){
        return new LatLng(mPositionLatitude, mPositionLongitude);
    }

    public ArrayList<Integer> getBikesAvailable() {
        return mBikesAvailable;
    }

    public int getBikesAvailableQuantity() {
        return mBikesAvailable.size();
    }
}
