package pt.ulisboa.tecnico.cmu.ubibike.domain;


public class Bike {

    private int mBid;
    private int mSid;
    private String mUuid;    //in our case the WIFI Direct peers device name

    public Bike(int bid, String uuid, int sid) {
        mBid = bid;
        mSid = sid;
        mUuid = uuid;
    }

    public int getBid() {
        return mBid;
    }

    public int getSid() {
        return mSid;
    }

    public String getUuid() {
        return mUuid;
    }
}