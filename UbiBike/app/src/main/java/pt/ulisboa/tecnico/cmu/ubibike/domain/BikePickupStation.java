package pt.ulisboa.tecnico.cmu.ubibike.domain;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by andriy on 12.03.2016.
 */
public class BikePickupStation {

    private String mStationName;
    private double mPositionLatitude;
    private double mPositionLongitude;
    private int mBikesAvailable;

    public BikePickupStation(String mStationName, int mBikesAvailable, double mPositionLatitude, double mPositionLongitude) {
        this.mStationName = mStationName;
        this.mBikesAvailable = mBikesAvailable;
        this.mPositionLongitude = mPositionLongitude;
        this.mPositionLatitude = mPositionLatitude;
    }

    public String getStationName() {
        return mStationName;
    }

    public LatLng getStationPosition(){
        return new LatLng(mPositionLatitude, mPositionLongitude);
    }

    public int getBikesAvailable() {
        return mBikesAvailable;
    }
}
