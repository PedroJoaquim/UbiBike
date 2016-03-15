package pt.ist.cmu.ubibike.httpserver.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import pt.ist.cmu.ubibike.httpserver.db.DBConnection;
import pt.ist.cmu.ubibike.httpserver.db.DBObjectSelector;
import pt.ist.cmu.ubibike.httpserver.model.Station;

import java.io.OutputStream;
import java.util.List;

/**
 * Created by Pedro Joaquim on 14-03-2016.
 */
public class StationsInfoHandler extends AuthRequiredHandler {

    private List<Station> result;

    @Override
    protected void continueActionValidation(HttpExchange httpExchange) {
        //user already authenticated no need for further validation
    }

    @Override
    protected void executeAction(HttpExchange httpExchange) throws Exception {
        this.result = DBObjectSelector.getAllStations(DBConnection.getConnection());
    }

    @Override
    protected void produceAnswer(HttpExchange httpExchange) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        String json =  mapper.writeValueAsString(this.result);

        httpExchange.sendResponseHeaders(200, json.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(json.getBytes());
        os.close();

    }
}
