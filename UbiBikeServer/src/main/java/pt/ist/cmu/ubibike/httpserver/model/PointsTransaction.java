package pt.ist.cmu.ubibike.httpserver.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class PointsTransaction {

    private TransactionInfo transactionInfo;
    private int targetLogicalClock;
    private String validationToken;
    private String sourcePublicKeyToken;

    public PointsTransaction() {
    }

    @JsonGetter("transaction_info")
    public TransactionInfo getTransactionInfo() {
        return transactionInfo;
    }

    @JsonSetter("transaction_info")
    public void setTransactionInfo(TransactionInfo transactionInfo) {
        this.transactionInfo = transactionInfo;
    }

    @JsonGetter("target_logical_clock")
    public int getTargetLogicalClock() {
        return targetLogicalClock;
    }

    @JsonSetter("target_logical_clock")
    public void setTargetLogicalClock(int targetLogicalClock) {
        this.targetLogicalClock = targetLogicalClock;
    }

    @JsonGetter("validation_token")
    public String getValidationToken() {
        return validationToken;
    }

    @JsonSetter("validation_token")
    public void setValidationToken(String validationToken) {
        this.validationToken = validationToken;
    }

    @JsonGetter("source_public_key_token")
    public String getSourcePublicKeyToken() {
        return sourcePublicKeyToken;
    }

    @JsonSetter("source_public_key_token")
    public void setSourcePublicKeyToken(String sourcePublicKeyToken) {
        this.sourcePublicKeyToken = sourcePublicKeyToken;
    }

    public int getSourceUid() {
        return this.transactionInfo.getSourceUid();
    }

    public int getTargetUid() {
        return this.transactionInfo.getTargetUid();
    }

    public int getPoints() {
        return this.transactionInfo.getPoints();
    }

    public long getTimestamp() {
        return Long.valueOf(this.transactionInfo.getTimestamp());
    }

    public int getSourceLogialClock() {
        return this.transactionInfo.getSourceLogialClock();
    }

    private class TransactionInfo {

        private int sourceUid;
        private int targetUid;
        private int points;
        private String timestamp;
        private int sourceLogialClock;

        public TransactionInfo() {
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
        public String getTimestamp() {
            return timestamp;
        }

        @JsonSetter("timestamp")
        public void setTimestamp(String timestamp) {
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
}
