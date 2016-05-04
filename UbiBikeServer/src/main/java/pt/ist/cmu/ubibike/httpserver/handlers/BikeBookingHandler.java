package pt.ist.cmu.ubibike.httpserver.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import pt.ist.cmu.ubibike.httpserver.db.DBConnection;
import pt.ist.cmu.ubibike.httpserver.db.DBObjectCreation;
import pt.ist.cmu.ubibike.httpserver.db.DBObjectRemove;
import pt.ist.cmu.ubibike.httpserver.db.DBObjectSelector;
import pt.ist.cmu.ubibike.httpserver.model.Bike;

import java.util.Random;


public class BikeBookingHandler extends AuthRequiredHandler {

    private static final String STATION_SID_ATTR = "sid";
    private Bike[] availableBikes;
    private Bike bike;

    public BikeBookingHandler() {
        super();
        this.setUseTransactions(true);
    }

    @Override
    protected void continueActionValidation(HttpExchange httpExchange) throws Exception{

        //check that user does not already has a bike booked
        if(DBObjectSelector.getBookingFromUID(DBConnection.getConnection(), this.user.getUid()) != null){
            throw new RuntimeException("User already has an active booking");
        }

        //check that requested station exists and has bikes available to book
        if(!urlQueyParams.containsKey(STATION_SID_ATTR)){
            throw new RuntimeException("No station selected");
        }

        int sid = Integer.valueOf(urlQueyParams.get(STATION_SID_ATTR));

        if((this.availableBikes = DBObjectSelector.getAvailableBikesFromStation(DBConnection.getConnection(), sid)).length == 0){
            throw new RuntimeException("The selected station has no bikes available for booking");
        }
    }

    @Override
    protected void executeAction(HttpExchange httpExchange) throws Exception {

        this.bike = availableBikes[new Random().nextInt(availableBikes.length)];
        DBObjectCreation.insertBikeBooking(DBConnection.getConnection(), this.user.getUid(), bike.getBid());

    }

    @Override
    protected String produceAnswer(HttpExchange httpExchange) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return  mapper.writeValueAsString(this.bike);
    }
}
