package pt.ist.cmu.ubibike.httpserver.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import pt.ist.cmu.ubibike.httpserver.model.PointsTransaction;
import pt.ist.cmu.ubibike.httpserver.util.JSONSchemaValidation;

public class PointsTransactionHandler extends AuthRequiredHandler {


    private int targetLogicalClock;
    private int sourceLogicalCLock;
    private int targetUid;
    private int sourceUid;


    protected void continueActionValidation(HttpExchange httpExchange) throws Exception {
        if(!"post".equalsIgnoreCase(httpExchange.getRequestMethod())){
            throw new RuntimeException("Points transaction request must be a post request");
        }

        String json = getRequestBody(httpExchange);

        if(!JSONSchemaValidation.validateSchema(json, JSONSchemaValidation.POINTS_TRANSACTION)){
            throw new RuntimeException("Invalid json submited");
        }

        ObjectMapper mapper = new ObjectMapper();
        PointsTransaction pt = mapper.readValue(json, PointsTransaction.class);





    }

    protected void executeAction(HttpExchange httpExchange) throws Exception {

    }

    protected String produceAnswer(HttpExchange httpExchange) throws Exception {
        return null;
    }
}
