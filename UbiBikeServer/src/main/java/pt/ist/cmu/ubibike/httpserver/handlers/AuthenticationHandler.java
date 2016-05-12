package pt.ist.cmu.ubibike.httpserver.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.main.JsonValidator;
import com.sun.net.httpserver.HttpExchange;
import pt.ist.cmu.ubibike.httpserver.cipher.CipherUtils;
import pt.ist.cmu.ubibike.httpserver.cipher.PublicKeyReader;
import pt.ist.cmu.ubibike.httpserver.db.DBConnection;
import pt.ist.cmu.ubibike.httpserver.db.DBObjectSelector;
import pt.ist.cmu.ubibike.httpserver.model.User;
import pt.ist.cmu.ubibike.httpserver.session.SessionManager;
import pt.ist.cmu.ubibike.httpserver.session.tokens.TokenHandler;
import pt.ist.cmu.ubibike.httpserver.util.JSONSchemaValidation;

import java.io.OutputStream;
import java.sql.Connection;
import java.util.Arrays;


public class AuthenticationHandler extends BaseHandler {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private String sessionToken;
    private User user;
    private String encodedPublicKey;

    @Override
    protected void validateAction(HttpExchange httpExchange) throws Exception{

        if(!"post".equalsIgnoreCase(httpExchange.getRequestMethod())){
            throw new RuntimeException("Authentication must be a post request");
        }

        String json = getRequestBody(httpExchange);

        if(!JSONSchemaValidation.validateSchema(json, JSONSchemaValidation.AUTHENTICATE_USER)){
            throw new RuntimeException("Invalid json submited");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonObj = mapper.readTree(json);

        String username = jsonObj.get(USERNAME).textValue();
        String password = jsonObj.get(PASSWORD).textValue();

        if((this.user = DBObjectSelector.getUserFromUsername(DBConnection.getConnection(),  username)) == null){
            throw new RuntimeException("the submited user does not exist");
        }

        byte[] stored = this.user.getPassword();
        byte[] submitedPass = CipherUtils.getSHA2Digest(password.getBytes());

        if(!Arrays.equals(this.user.getPassword(), CipherUtils.getSHA2Digest(password.getBytes()))){
            throw new RuntimeException("invalid credentials");
        }

        this.encodedPublicKey = CipherUtils.encodeToBase64String(PublicKeyReader.getKey().getEncoded());

    }

    @Override
    protected void executeAction(HttpExchange httpExchange) throws Exception {
        this.sessionToken = SessionManager.startSession(this.user);
    }

    @Override
    protected String produceAnswer(HttpExchange httpExchange) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jNode = mapper.createObjectNode();

        jNode.put("session_token", this.sessionToken);
        jNode.put("uid", this.user.getUid());
        jNode.put("server_public_key", this.encodedPublicKey);

        return jNode.toString();
    }
}
