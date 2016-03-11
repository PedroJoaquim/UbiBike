package pt.ist.cmu.ubibike.httpserver.session.tokens;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import pt.ist.cmu.ubibike.httpserver.cipher.CipherManager;
import pt.ist.cmu.ubibike.httpserver.cipher.CipherUtils;
import pt.ist.cmu.ubibike.httpserver.cipher.PublicKeyReader;
import pt.ist.cmu.ubibike.httpserver.model.User;

import java.io.IOException;

/**
 * Created by ASUS on 11/03/2016.
 */
public class TokenGenerationTests {

    private static User u;
    private static final int uid = 1;
    private static final String username = "ANDRIY BATATA";
    private static final String email = "lol@lol.pt";
    private static final String publicKey = "PUBLIC KEY TEST";
    private static final byte[] password = "PASSWORD".getBytes();

    @BeforeClass
    public static void oneTimeSetup(){
        u = new User(uid, username, email, publicKey, password);
    }

    @Test
    public void publicKeyToken1() throws IOException {

        String token = TokenFactory.generatePublicKeyToken(u);
        ObjectMapper mapper = new ObjectMapper();

        byte[] encodedData = CipherUtils.decodeBase64FromString(token);
        byte[] decodedData = CipherManager.decipher(encodedData, PublicKeyReader.getKey());

        String json = new String(decodedData);

        PublicKeyToken tokenObj = mapper.readValue(json, PublicKeyToken.class);

        Assert.assertNotNull(tokenObj);
        Assert.assertEquals(username, tokenObj.getUsername());
        Assert.assertEquals(uid, tokenObj.getUid());
        Assert.assertEquals(publicKey, tokenObj.getPublicKey());





    }


}
