package pt.ist.cmu.ubibike.httpserver.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import pt.ist.cmu.ubibike.httpserver.cipher.CipherManager;
import pt.ist.cmu.ubibike.httpserver.cipher.CipherUtils;
import pt.ist.cmu.ubibike.httpserver.consistency.ConsistencyManager;
import pt.ist.cmu.ubibike.httpserver.model.PointsTransactionAllInfo;
import pt.ist.cmu.ubibike.httpserver.model.PointsTransactionBaseInfo;
import pt.ist.cmu.ubibike.httpserver.session.tokens.PublicKeyToken;
import pt.ist.cmu.ubibike.httpserver.session.tokens.TokenHandler;
import pt.ist.cmu.ubibike.httpserver.util.JSONSchemaValidation;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Arrays;

public class PointsTransactionHandler extends AuthRequiredHandler {

    private PointsTransactionAllInfo ptAllInfo;

    protected void continueActionValidation(HttpExchange httpExchange) throws Exception {
        if(!"post".equalsIgnoreCase(httpExchange.getRequestMethod())){
            throw new RuntimeException("Points transaction request must be a post request");
        }

        String json = getRequestBody(httpExchange);

        if(!JSONSchemaValidation.validateSchema(json, JSONSchemaValidation.POINTS_TRANSACTION)){
            throw new RuntimeException("Invalid json submited");
        }

        ObjectMapper mapper = new ObjectMapper();
        ptAllInfo = mapper.readValue(json, PointsTransactionAllInfo.class);

        validatePointsTransaction(ptAllInfo);
    }

    private void validatePointsTransaction(PointsTransactionAllInfo ptAllInfo) {

        PointsTransactionBaseInfo ptBaseInfo;
        PublicKeyToken pkToken;

        String originalJSON = new String(CipherUtils.decodeBase64FromString(ptAllInfo.getOriginalJSONBase64()));
        String validationToken = ptAllInfo.getValidationToken();
        String sourcePublicKeyToken = ptAllInfo.getSourcePublicKeyToken();

        //plain request validation
        ptBaseInfo = plainRequestValidation(originalJSON);
        ptAllInfo.setTransactionInfo(ptBaseInfo);

        //public key token validation
        pkToken = publicKeyTokenValidation(sourcePublicKeyToken, ptBaseInfo);

        //integrity check
        requestIntegrityCheck(pkToken, originalJSON, validationToken);

        //freshness check
        //finally check the user sending the request is either the source user or the target user
        if(!(ptBaseInfo.getSourceUsername().equals(this.user.getUsername())) && !(ptBaseInfo.getTargetUsername().equals(this.user.getUsername()))){
            throw new RuntimeException("user not in the reported transaction");
        }

    }

    /*
     * Check the plain transaction points request format
     */
    private PointsTransactionBaseInfo plainRequestValidation(String originalJSON) {

        try {
            return  new ObjectMapper().readValue(originalJSON, PointsTransactionBaseInfo.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid Transaction Info Submited");
        }
    }


    /*
     * Validates the public Token used in a points transaction
     *      -check token format
     *      -check that the token it is from the source user
     *      -check the validation timestamp
     */
    private PublicKeyToken publicKeyTokenValidation(String encodedPKToken, PointsTransactionBaseInfo ptBaseInfo) {

        PublicKeyToken pkToken = TokenHandler.readPublicKeyToken(encodedPKToken);

        //check if public token it is from the source user
        if(!pkToken.getUsername().equals(ptBaseInfo.getSourceUsername())){
            throw new RuntimeException("Invalid Transaction Validation Token");
        }

        //check if the public key token is valid
        if(Long.valueOf(pkToken.getValidationTimestamp()) <= System.currentTimeMillis()){
            throw new RuntimeException("Invalid Transaction Validation Token");
        }

        return pkToken;
    }

    /*
    * Performs the integrity check to ensure that the data was not modified in the network
    * the validation token is a hash of the original content signed by the source user
    */
    private void requestIntegrityCheck(PublicKeyToken pkToken, String plainRequest, String validationToken) {

        //get the public key in the public key token to decipher the signed content
        String publicKeyString = pkToken.getPublicKey();
        PublicKey sourcePublicKey = CipherUtils.readPublicKeyFromBytes(CipherUtils.decodeBase64FromString(publicKeyString));

        //check if signed content matches what we received
        byte[] hash1 = CipherUtils.getSHA2Digest(plainRequest.getBytes());
        byte[] hash2 = CipherManager.decipher(CipherUtils.decodeBase64FromString(validationToken), sourcePublicKey);

        if(!Arrays.equals(hash1, hash2)){
            throw new RuntimeException("The signed content does not match the plain content received, data integrity violated");
        }
    }

    protected void executeAction(HttpExchange httpExchange) throws Exception {
        ConsistencyManager.getInstance().addNewPointsTransaction(ptAllInfo);
    }

    protected String produceAnswer(HttpExchange httpExchange) throws Exception {
        return "{}";
    }
}
