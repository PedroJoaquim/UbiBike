package pt.ist.cmu.ubibike.httpserver.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class PointsTransactionBaseInfo {

    private int sourceUid;
    private int targetUid;
    private int points;
    private long timestamp;
    private int sourceLogialClock;

    public PointsTransactionBaseInfo() {
    }

    public PointsTransactionBaseInfo(int sourceUid, int targetUid, int points, long timestamp) {
        this.sourceUid = sourceUid;
        this.targetUid = targetUid;
        this.points = points;
        this.timestamp = timestamp;
    }


    @JsonGetter("source_uid")
    public int getSourceUid() {
        return sourceUid;
    }

    @JsonSetter("source_uid")
    public void setSourceUid(int sourceUid) {
        this.sourceUid = sourceUid;
    }

    @JsonGetter("target_uid")
    public int getTargetUid() {
        return targetUid;
    }

    @JsonSetter("target_uid")
    public void setTargetUid(int targetUid) {
        this.targetUid = targetUid;
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
