package pt.ist.cmu.ubibike.httpserver.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import pt.ist.cmu.ubibike.httpserver.db.*;
import pt.ist.cmu.ubibike.httpserver.model.Booking;
import pt.ist.cmu.ubibike.httpserver.util.JSONSchemaValidation;

import java.util.Arrays;

/**
 * Created by Pedro Joaquim on 14-03-2016.
 */
public class BikePickDropHandler extends AuthRequiredHandler {

    private static final String BIKE_PICK = "bike_pick";
    private static final String BID = "bid";
    private static final String SID = "sid";
    private boolean bickePick;
    private int sid;
    private int bid;
    private Booking b;

    public BikePickDropHandler() {
        super();
        setUseTransactions(true);
    }

    @Override
    protected void continueActionValidation(HttpExchange httpExchange) throws Exception {

        if(!"post".equalsIgnoreCase(httpExchange.getRequestMethod())){
            throw new RuntimeException("Bike Pick Drop request must be a post request");
        }

        String json = getRequestBody(httpExchange);

        if(!JSONSchemaValidation.validateSchema(json, JSONSchemaValidation.BIKE_PICK_DROP)){
            throw new RuntimeException("Invalid json submited");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonObj = mapper.readTree(json);

        this.bid = jsonObj.get(BID).intValue();
        this.sid = jsonObj.get(SID).intValue();
        this.bickePick = jsonObj.get(BIKE_PICK).booleanValue();


        this.b = DBObjectSelector.getBookingFromUID(DBConnection.getConnection(), this.user.getUid());

        if(b == null){
            throw new RuntimeException("The user has no bookings");
        }

        if(bickePick){
            if(!Arrays.asList(DBObjectSelector.getBikesFromStation(DBConnection.getConnection(), this.sid)).contains(this.bid)){
                throw new RuntimeException("The selected bike is not in the selected station");
            }
        }

        if(!bickePick){
            if(!b.isActive()){
                throw new RuntimeException("You cannot drop a bike that you didnt picked yet");
            }
        }

        if(b.getBid() != this.bid){
            throw new RuntimeException("The requested bid does not match your booking's bid");
        }
    }

    @Override
    protected void executeAction(HttpExchange httpExchange) throws Exception {


        if(bickePick){
            this.b.setActive(true);
            DBObjectUpdater.updateBooking(DBConnection.getConnection(), b);
            DBObjectRemove.removeBikeFromStation(DBConnection.getConnection(), this.bid);
        }
        else{
            DBObjectRemove.removeBookingFromUser(DBConnection.getConnection(), this.user.getUid());
            DBObjectCreation.addBikeToStation(DBConnection.getConnection(), this.bid, this.sid);
        }
    }

    @Override
    protected String produceAnswer(HttpExchange httpExchange) throws Exception {
        return "{}";
    }
}
