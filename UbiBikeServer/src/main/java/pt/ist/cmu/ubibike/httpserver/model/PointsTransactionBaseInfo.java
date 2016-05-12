package pt.ist.cmu.ubibike.httpserver.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class PointsTransactionBaseInfo {

    private String sourceUsername;
    private String targetUsername;
    private int points;
    private long timestamp;
    private int sourceLogialClock;

    public PointsTransactionBaseInfo() {
    }

    public PointsTransactionBaseInfo(String sourceUsername, String targetUsername, int points, long timestamp) {
        this.sourceUsername = sourceUsername;
        this.targetUsername = targetUsername;
        this.points = points;
        this.timestamp = timestamp;
    }


    @JsonGetter("source_uid")
    public String getSourceUsername() {
        return sourceUsername;
    }

    @JsonSetter("source_uid")
    public void setSourceUsername(String sourceUsername) {
        this.sourceUsername = sourceUsername;
    }

    @JsonGetter("target_uid")
    public String getTargetUsername() {
        return targetUsername;
    }

    @JsonSetter("target_uid")
    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
    }

    @JsonGetter("points")
    public int getPoints() {
        return points;
    }

    @JsonSetter("points")
    public void setPoints(int points) {
        this.points = points;
    }

    @JsonGetter("timestamp")
    public long getTimestamp() {
        return timestamp;
    }

    @JsonSetter("timestamp")
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @JsonGetter("source_logical_clock")
    public int getSourceLogialClock() {
        return sourceLogialClock;
    }

    @JsonSetter("source_logical_clock")
    public void setSourceLogialClock(int sourceLogialClock) {
        this.sourceLogialClock = sourceLogialClock;
    }
}
