package pt.ist.cmu.ubibike.httpserver.handlers;

import com.sun.net.httpserver.HttpExchange;

/**
 * Created by Pedro Joaquim on 14-03-2016.
 */
public class TrajectoriesHistoryHandler extends AuthRequiredHandler {

    @Override
    protected void continueActionValidation(HttpExchange httpExchange) {

    }

    @Override
    protected void executeAction(HttpExchange httpExchange) throws Exception {

    }

    @Override
    protected String produceAnswer(HttpExchange httpExchange) throws Exception {
        return "todo";
    }
}
