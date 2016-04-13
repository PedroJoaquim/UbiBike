package pt.ist.cmu.ubibike.httpserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import pt.ist.cmu.ubibike.httpserver.db.DBConnection;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseHandler implements HttpHandler {

    private static final int BUFFER_SIZE = 248;
    protected Map<String, String> urlQueyParams;
    private static final String ERROR_MESSAGE = "Uknown internal server error";
    private boolean useTransactions = false;

    public void handle(HttpExchange httpExchange) throws IOException {

        Connection conn = DBConnection.getConnection();
        Savepoint save = null;

        try{

            if(useTransactions){
                conn.setAutoCommit(false);
                save = conn.setSavepoint();
            }

            this.parseUrlQuery(httpExchange);
            this.validateAction(httpExchange);
            this.executeAction(httpExchange);
            this.writeAnswer(httpExchange, this.produceAnswer(httpExchange));

            if(useTransactions){
                conn.commit();
            }

        }catch (Exception e){
            if(e.getMessage() == null){
                httpExchange.sendResponseHeaders(500, ERROR_MESSAGE.length());
                OutputStream os = httpExchange.getResponseBody();
                writeToStream(os, ERROR_MESSAGE.getBytes());
            }
            else{
                httpExchange.sendResponseHeaders(400, e.getMessage().getBytes().length);
                OutputStream os = httpExchange.getResponseBody();
                writeToStream(os, e.getMessage().getBytes());
            }
            if(useTransactions){
                try {
                    conn.rollback(save);
                } catch (SQLException e1) {
                    //ignore
                }
            }
        }

    }


    protected abstract void validateAction(HttpExchange httpExchange) throws Exception;
    protected abstract void executeAction(HttpExchange httpExchange) throws Exception;
    protected abstract String produceAnswer(HttpExchange httpExchange) throws Exception;

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

    protected void parseUrlQuery(HttpExchange httpExchange){

        String query =  httpExchange.getRequestURI().getQuery();
        urlQueyParams = new HashMap<String, String>();

        if(query != null){
            for (String param : query.split("&")) {
                try{
                    String pair[] = param.split("=");
                    if (pair.length>1) {
                        urlQueyParams.put(pair[0], pair[1]);
                    }else{
                        urlQueyParams.put(pair[0], "");
                    }
                }catch (Exception e){
                    //ignore
                }
            }
        }
    }

    protected void writeAnswer(HttpExchange httpExchange, String s) throws IOException {
        byte[] answerBytes = s.getBytes();
        httpExchange.sendResponseHeaders(200, answerBytes.length);
        OutputStream os = httpExchange.getResponseBody();
        writeToStream(os, answerBytes);
    }


    protected void writeToStream(OutputStream out, byte[] data) throws IOException {

        byte [] buffer = new byte [BUFFER_SIZE];
        int count ;
        ByteArrayInputStream bis = null;

        try{
            bis = new ByteArrayInputStream(data);

            while ((count = bis.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
        }finally {
            try{
                out.close();
                bis.close();
            }catch (Exception e){
                //ignore
            }
        }
    }

    protected void setUseTransactions(boolean useTransactions){
        this.useTransactions = useTransactions;
    }


}
