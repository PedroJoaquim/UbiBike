package pt.ulisboa.tecnico.cmu.ubibike.connection;

import org.json.JSONObject;


public class PendingRequest {

    private int mID;
    private String mUrl;
    private int mRequestType;
    private JSONObject mJson;

    public PendingRequest(int id, String url, int type, JSONObject json){
        mID = id;
        mUrl = url;
        mRequestType = type;
        mJson = json;
    }

    public int getID() {
        return mID;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public JSONObject getJson() {
        return mJson;
    }

    public void setJson(JSONObject mJson) {
        this.mJson = mJson;
    }

    public int getRequestType() {
        return mRequestType;
    }

    public void setRequestType(int mRequestType) {
        this.mRequestType = mRequestType;
    }
}
