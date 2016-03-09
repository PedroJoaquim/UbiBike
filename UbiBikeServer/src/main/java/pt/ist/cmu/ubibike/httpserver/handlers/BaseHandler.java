package pt.ist.cmu.ubibike.httpserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public abstract class BaseHandler implements HttpHandler {

    public void handle(HttpExchange httpExchange) throws IOException {

        try{
            this.validateAction(httpExchange);
            this.executeAction(httpExchange);
            this.produceAnswer(httpExchange);
        }catch (Exception e){
            //do cenas
        }

    }

    protected abstract void validateAction(HttpExchange httpExchange) throws Exception;
    protected abstract void executeAction(HttpExchange httpExchange) throws Exception;
    protected abstract void produceAnswer(HttpExchange httpExchange) throws Exception;
}
