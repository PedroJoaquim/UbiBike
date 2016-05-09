package pt.ist.cmu.ubibike.httpserver.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import pt.ist.cmu.ubibike.httpserver.db.DBConnection;
import pt.ist.cmu.ubibike.httpserver.db.DBObjectSelector;
import pt.ist.cmu.ubibike.httpserver.model.Station;

import java.util.HashMap;


public class StationsInfoHandler extends AuthRequiredHandler {

    private Station[] result;

    @Override
    protected void continueActionValidation(HttpExchange httpExchange) {
        //user already authenticated no need for further validation
    }

    @Override
    protected void executeAction(HttpExchange httpExchange) throws Exception {
        this.result = DBObjectSelector.getAllStations(DBConnection.getConnection());
    }

    @Override
    protected String produceAnswer(HttpExchange httpExchange) throws Exception {

        HashMap<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("stations", this.result);

        ObjectMapper mapper = new ObjectMapper();
        return  mapper.writeValueAsString(jsonMap);
    }
}
