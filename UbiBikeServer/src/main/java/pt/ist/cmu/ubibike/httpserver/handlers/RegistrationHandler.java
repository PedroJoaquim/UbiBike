package pt.ist.cmu.ubibike.httpserver.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import pt.ist.cmu.ubibike.httpserver.cipher.CipherUtils;
import pt.ist.cmu.ubibike.httpserver.db.DBConnection;
import pt.ist.cmu.ubibike.httpserver.db.DBObjectCreation;
import pt.ist.cmu.ubibike.httpserver.db.DBObjectSelector;
import pt.ist.cmu.ubibike.httpserver.model.User;
import pt.ist.cmu.ubibike.httpserver.session.SessionManager;
import pt.ist.cmu.ubibike.httpserver.session.tokens.TokenHandler;
import pt.ist.cmu.ubibike.httpserver.util.JSONSchemaValidation;

import java.io.*;
import java.sql.Connection;

public class RegistrationHandler extends BaseHandler {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String PUBLIC_KEY = "public_key";

    private String username;
    private String password;
    private String publicKey;

    private int uid;
    private String sessionToken;
    private String publicKeyToken;

    @Override
    @SuppressWarnings("unchecked")
    protected void validateAction(HttpExchange httpExchange) throws Exception {

        if(!"post".equalsIgnoreCase(httpExchange.getRequestMethod())){
            throw new RuntimeException("Registration must be a post request");
        }

        String json = getRequestBody(httpExchange);

        if(!JSONSchemaValidation.validateSchema(json, JSONSchemaValidation.REGISTER_USER)){
            throw new RuntimeException("Invalid json submited");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonObj = mapper.readTree(json);

        this.username = jsonObj.get(USERNAME).textValue();
        this.password = jsonObj.get(PASSWORD).textValue();
        this.publicKey = jsonObj.get(PUBLIC_KEY).textValue();

        if(DBObjectSelector.getUserFromUsername(DBConnection.getConnection(),  this.username) != null){
            throw new RuntimeException("username already in use");
        }
    }

    @Override
    protected void executeAction(HttpExchange httpExchange) throws Exception{

        Connection conn = DBConnection.getConnection();
        byte[] passwordHash = CipherUtils.getSHA2Digest(this.password.getBytes());

        this.uid = DBObjectCreation.insertUser(conn, this.username, this.publicKey, passwordHash);

        User u = new User(uid, username, publicKey, passwordHash);

        this.publicKeyToken = TokenHandler.generatePublicKeyToken(u);
        this.sessionToken = SessionManager.startSession(u);
    }

    @Override
    protected String produceAnswer(HttpExchange httpExchange) throws Exception{

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jNode = mapper.createObjectNode();

        jNode.put("session_token", this.sessionToken);
        jNode.put("public_key_token", this.publicKeyToken);
        jNode.put("uid", this.uid);

        return jNode.toString();
    }
}
