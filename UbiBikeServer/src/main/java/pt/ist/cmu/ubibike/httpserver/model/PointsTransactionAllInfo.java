package pt.ist.cmu.ubibike.httpserver.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class PointsTransactionAllInfo {

    private PointsTransactionBaseInfo transactionInfo;
    private int targetLogicalClock;
    private String validationToken;
    private String sourcePublicKeyToken;
    private String originalJSONBase64;


    public PointsTransactionAllInfo() {
    }

    public PointsTransactionBaseInfo getTransactionInfo() {
        return transactionInfo;
    }

    public void setTransactionInfo(PointsTransactionBaseInfo transactionInfo) {
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

    @JsonGetter("original_json_base_64")
    public String getOriginalJSONBase64() {
        return originalJSONBase64;
    }

    @JsonSetter("original_json_base_64")
    public void setOriginalJSONBase64(String originalJSONBase64) {
        this.originalJSONBase64 = originalJSONBase64;
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

    public PendingEvent toPendingEvent() {
        return new PendingEvent(-1, getSourceUid(), getSourceLogialClock(), getTargetUid(), getTargetLogicalClock(),
                                getPoints(), getTimestamp(), PendingEvent.TRANSACTION_TYPE);
    }


}
