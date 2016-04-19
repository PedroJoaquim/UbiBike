package pt.ist.cmu.ubibike.httpserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import pt.ist.cmu.ubibike.httpserver.db.DBConnection;
import pt.ist.cmu.ubibike.httpserver.db.DBObjectRemove;
import pt.ist.cmu.ubibike.httpserver.db.DBObjectSelector;
import pt.ist.cmu.ubibike.httpserver.model.Booking;

import java.sql.SQLException;

public class UnbookingHandler extends AuthRequiredHandler {

    @Override
    protected void continueActionValidation(HttpExchange httpExchange) throws SQLException {

        Booking b = DBObjectSelector.getBookingFromUID(DBConnection.getConnection(), this.user.getUid());

        if(b == null){
            throw new RuntimeException("No active bookings for user " + this.user.getUsername());
        }

        if(b.isActive()){
            throw new RuntimeException("You cannot unbook if you already picked up the bike");
        }
    }

    @Override
    protected void executeAction(HttpExchange httpExchange) throws Exception {
        DBObjectRemove.removeBookingFromUser(DBConnection.getConnection(), this.user.getUid());
    }

    @Override
    protected String produceAnswer(HttpExchange httpExchange) throws Exception {
        return "{}";
    }
}
