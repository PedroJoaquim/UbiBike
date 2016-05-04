package pt.ist.cmu.ubibike.httpserver.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import pt.ist.cmu.ubibike.httpserver.db.DBConnection;
import pt.ist.cmu.ubibike.httpserver.db.DBObjectSelector;
import pt.ist.cmu.ubibike.httpserver.model.Trajectory;

import java.sql.Connection;

/**
 * Created by Pedro Joaquim on 14-03-2016.
 */
public class UserInfoHandler extends AuthRequiredHandler {

    @Override
    protected void continueActionValidation(HttpExchange httpExchange) {
        //nothing more todo
    }

    @Override
    protected void executeAction(HttpExchange httpExchange) throws Exception {

        Connection conn = DBConnection.getConnection();
        Trajectory[] trajectories = DBObjectSelector.getTrajectoriesFromUser(conn, this.user.getUid());
        this.user.setTrajectories(trajectories);

        if(this.user.getPoints() > 0){
            this.user.setGlobalRank(DBObjectSelector.calcUserGlobalRank(conn, user.getUid()));
        }
    }

    @Override
    protected String produceAnswer(HttpExchange httpExchange) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return  mapper.writeValueAsString(this.user);
    }
}
