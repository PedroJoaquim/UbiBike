package pt.ist.cmu.ubibike.httpserver.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import pt.ist.cmu.ubibike.httpserver.db.DBConnection;
import pt.ist.cmu.ubibike.httpserver.db.DBObjectCreation;
import pt.ist.cmu.ubibike.httpserver.model.Trajectory;
import pt.ist.cmu.ubibike.httpserver.util.JSONSchemaValidation;




public class NewTrajectoryHandler extends AuthRequiredHandler {


    private String inputJSON;
    private int tid;

    @Override
    protected void continueActionValidation(HttpExchange httpExchange) throws Exception {

        if(!"post".equalsIgnoreCase(httpExchange.getRequestMethod())){
            throw new RuntimeException("new Trajectory request must be a post request");
        }

        this.inputJSON  = getRequestBody(httpExchange);

        if(!JSONSchemaValidation.validateSchema(this.inputJSON, JSONSchemaValidation.TRAJECTORY)){
            throw new RuntimeException("Invalid json submited");
        }
    }

    @Override
    protected void executeAction(HttpExchange httpExchange) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        Trajectory t = mapper.readValue(this.inputJSON, Trajectory.class);

        t.setUid(this.user.getUid());
        t.setPointsEarned((int) (t.getDistance() * 10));

        this.tid = DBObjectCreation.insertTrajectory(DBConnection.getConnection(), t);
    }

    @Override
    protected String produceAnswer(HttpExchange httpExchange) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jNode = mapper.createObjectNode();

        jNode.put("tid", this.tid);

        return jNode.toString();
    }
}
