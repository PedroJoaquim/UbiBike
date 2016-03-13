package pt.ist.cmu.ubibike.httpserver.session.tokens;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by ASUS on 10/03/2016.
 */
public class SessionToken {

    private int sessionId;

    public SessionToken(int sessionId) {
        this.sessionId = sessionId;
    }

    public SessionToken(){

    }

    @JsonGetter("session_id")
    public int getSessionId() {
        return sessionId;
    }

    @JsonSetter("session_id")
    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
}
