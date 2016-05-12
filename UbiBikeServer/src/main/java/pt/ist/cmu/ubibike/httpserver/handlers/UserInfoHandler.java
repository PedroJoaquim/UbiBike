package pt.ist.cmu.ubibike.httpserver.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import pt.ist.cmu.ubibike.httpserver.db.DBConnection;
import pt.ist.cmu.ubibike.httpserver.db.DBObjectSelector;
import pt.ist.cmu.ubibike.httpserver.model.Bike;
import pt.ist.cmu.ubibike.httpserver.model.Booking;
import pt.ist.cmu.ubibike.httpserver.model.Trajectory;

import java.sql.Connection;

/**
 * Created by Pedro Joaquim on 14-03-2016.
 */
public class UserInfoHandler extends AuthRequiredHandler {

    @Override
    protected void continueActionValidation(HttpExchange httpExchange) {

    }

    @Override
    protected void executeAction(HttpExchange httpExchange) throws Exception {

        Connection conn = DBConnection.getConnection();
        Trajectory[] trajectories = DBObjectSelector.getTrajectoriesFromUser(conn, this.user.getUid());
        this.user.setTrajectories(trajectories);

        Booking b = DBObjectSelector.getBookingFromUID(DBConnection.getConnection(), this.user.getUid());
        Bike bike;

        if(b != null){
            if(b.isActive()){
                this.user.setBooking(DBObjectSelector.getBikeFromBid(DBConnection.getConnection(), b.getBid(), -1));
            }
            else{
                this.user.setBooking(DBObjectSelector.getBikeFromBid(DBConnection.getConnection(), b.getBid(), b.getSourceSid()));
            }
        }

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
