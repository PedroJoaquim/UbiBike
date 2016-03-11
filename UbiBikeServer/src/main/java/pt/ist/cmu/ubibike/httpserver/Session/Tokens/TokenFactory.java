package pt.ist.cmu.ubibike.httpserver.session.tokens;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import pt.ist.cmu.ubibike.httpserver.cipher.CipherManager;
import pt.ist.cmu.ubibike.httpserver.cipher.CipherUtils;
import pt.ist.cmu.ubibike.httpserver.cipher.PrivateKeyReader;
import pt.ist.cmu.ubibike.httpserver.model.User;

public class TokenFactory {

    public static String generatePublicKeyToken(User u){

        try {
            PublicKeyToken token = new PublicKeyToken(u);
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
            String json =  mapper.writeValueAsString(token);
            byte[] encodedToken = CipherManager.cipher(json.getBytes(), PrivateKeyReader.getKey());

            return CipherUtils.encodeToBase64String(encodedToken);

        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
