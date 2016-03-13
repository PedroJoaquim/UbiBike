package pt.ist.cmu.ubibike.httpserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;

public abstract class BaseHandler implements HttpHandler {

    public void handle(HttpExchange httpExchange) throws IOException {

        try{
            this.validateAction(httpExchange);
            this.executeAction(httpExchange);
            this.produceAnswer(httpExchange);
        }catch (Exception e){
            httpExchange.sendResponseHeaders(400, e.getMessage().length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(e.getMessage().getBytes());
            os.close();
        }

    }

    protected abstract void validateAction(HttpExchange httpExchange) throws Exception;
    protected abstract void executeAction(HttpExchange httpExchange) throws Exception;
    protected abstract void produceAnswer(HttpExchange httpExchange) throws Exception;

    protected String getRequestBody(HttpExchange httpExchange) throws IOException {

        String body = "";
        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(),"utf-8");
        BufferedReader br = new BufferedReader(isr);
        String line = br.readLine();

        while(line != null){
            body += line;
            line = br.readLine();
        }

        return body;
    }
}
