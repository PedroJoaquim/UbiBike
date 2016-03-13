package pt.ist.cmu.ubibike.httpserver.session.tokens;

import com.fasterxml.jackson.databind.ObjectMapper;
import pt.ist.cmu.ubibike.httpserver.cipher.CipherManager;
import pt.ist.cmu.ubibike.httpserver.cipher.CipherUtils;
import pt.ist.cmu.ubibike.httpserver.cipher.PrivateKeyReader;
import pt.ist.cmu.ubibike.httpserver.cipher.PublicKeyReader;
import pt.ist.cmu.ubibike.httpserver.model.User;

public class TokenHandler {

    public static String generatePublicKeyToken(User u){

        try {
            PublicKeyToken token = new PublicKeyToken(u);
            ObjectMapper mapper = new ObjectMapper();
            String json =  mapper.writeValueAsString(token);
            byte[] encodedToken = CipherManager.cipher(json.getBytes(), PrivateKeyReader.getKey());

            return CipherUtils.encodeToBase64String(encodedToken);

        } catch (Exception e) {
            throw new RuntimeException("Error generating public key token");
        }
    }

    public static String generateSessionToken(int sessionId){

        try {
            SessionToken token = new SessionToken(sessionId);
            ObjectMapper mapper = new ObjectMapper();
            String json =  mapper.writeValueAsString(token);

            return CipherUtils.encodeToBase64String(json.getBytes());

        } catch (Exception e) {
            throw new RuntimeException("Error generating session token");
        }
    }

    public static SessionToken readSessionToken(String token){

        try{
            ObjectMapper mapper = new ObjectMapper();
            String json =  new String(CipherUtils.decodeBase64FromString(token));

            return  mapper.readValue(json, SessionToken.class);

        }catch (Exception e){
            throw new RuntimeException("Invalid  session token");
        }

    }

    public static PublicKeyToken readPublicKeyToken(String token){

        try{
            ObjectMapper mapper = new ObjectMapper();

            byte[] encodedData = CipherUtils.decodeBase64FromString(token);
            byte[] decodedData = CipherManager.decipher(encodedData, PublicKeyReader.getKey());
            String json = new String(decodedData);

            return mapper.readValue(json, PublicKeyToken.class);

        }catch (Exception e){
            throw new RuntimeException("Invalid public key token");
        }



    }

}
