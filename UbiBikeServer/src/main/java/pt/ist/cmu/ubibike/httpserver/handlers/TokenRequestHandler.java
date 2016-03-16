package pt.ist.cmu.ubibike.httpserver.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import pt.ist.cmu.ubibike.httpserver.session.tokens.TokenHandler;

import java.io.OutputStream;

/**
 * Created by Pedro Joaquim on 13-03-2016.
 */
public class TokenRequestHandler extends AuthRequiredHandler{

    private String token;

    @Override
    protected void continueActionValidation(HttpExchange httpExchange) {
        if(!"get".equalsIgnoreCase(httpExchange.getRequestMethod())){
            throw new RuntimeException("Public Key Token request must be a get request");
        }
    }

    @Override
    protected void executeAction(HttpExchange httpExchange) throws Exception {
        this.token = TokenHandler.generatePublicKeyToken(this.user);
    }

    @Override
    protected String produceAnswer(HttpExchange httpExchange) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jNode = mapper.createObjectNode();

        jNode.put("public_key_token", this.token);
        jNode.put("uid", this.user.getUid());

        return jNode.toString();
    }
}
