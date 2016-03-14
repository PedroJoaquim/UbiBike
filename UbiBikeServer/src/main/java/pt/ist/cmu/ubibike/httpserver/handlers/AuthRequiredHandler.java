package pt.ist.cmu.ubibike.httpserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import pt.ist.cmu.ubibike.httpserver.db.DBConnection;
import pt.ist.cmu.ubibike.httpserver.model.User;
import pt.ist.cmu.ubibike.httpserver.session.SessionManager;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pedro Joaquim on 13-03-2016.
 */
public abstract class AuthRequiredHandler extends BaseHandler{

    protected User user;
    protected Map<String, String> urlQueyParams;


    private static final String SESSION_TOKEN_PARAM = "session_token";
    private static final String UID_PARAM = "uid";

    @Override
    protected void validateAction(HttpExchange httpExchange) throws Exception {

        Connection conn = DBConnection.getConnection();

        this.urlQueyParams = parseUrlQuery(httpExchange);

        if(!urlQueyParams.containsKey(SESSION_TOKEN_PARAM) || !urlQueyParams.containsKey(UID_PARAM)){
            throw new RuntimeException("This actions requires authentication");
        }

        int uid = Integer.valueOf(this.urlQueyParams.get(UID_PARAM));

        this.user = SessionManager.getUserFromSessionToken(this.urlQueyParams.get(SESSION_TOKEN_PARAM));

        if(this.user.getUid() != uid){
            throw new RuntimeException("Invalid session token");
        }


        continueActionValidation(httpExchange);
    }

    protected Map<String, String> parseUrlQuery(HttpExchange httpExchange){

        String query =  httpExchange.getRequestURI().getQuery();
        Map<String, String> result = new HashMap<String, String>();

        if(query != null){
            for (String param : query.split("&")) {
                String pair[] = param.split("=");
                if (pair.length>1) {
                    result.put(pair[0], pair[1]);
                }else{
                    result.put(pair[0], "");
                }
            }
        }

        return result;
    }

    protected abstract void continueActionValidation(HttpExchange httpExchange);
}
