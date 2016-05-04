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


public class TokenGenerationTests {

    private static User u;
    private static final int uid = 1;
    private static final int sessionID = 123456789;
    private static final String username = "ANDRIY BATATA";
    private static final String publicKey = "PUBLIC KEY TEST";
    private static final byte[] password = "PASSWORD".getBytes();

    @BeforeClass
    public static void oneTimeSetup(){
        u = new User(uid, username, publicKey, password, 0);
    }

    @Test
    public void publicKeyTokenTest1() throws IOException {

        String token = TokenHandler.generatePublicKeyToken(u);
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

    @Test
    public void publicKeyTokenTest2(){

        String token = TokenHandler.generatePublicKeyToken(u);
        PublicKeyToken tokenObj = TokenHandler.readPublicKeyToken(token);

        Assert.assertEquals(username, tokenObj.getUsername());
        Assert.assertEquals(uid, tokenObj.getUid());
        Assert.assertEquals(publicKey, tokenObj.getPublicKey());

    }

    @Test
    public void sessionTokenTest1() throws IOException {

        String token = TokenHandler.generateSessionToken(sessionID);
        ObjectMapper mapper = new ObjectMapper();

        String json =  new String(CipherUtils.decodeBase64FromString(token));

        SessionToken tokenObj =  mapper.readValue(json, SessionToken.class);

        Assert.assertNotNull(tokenObj);
        Assert.assertEquals(sessionID, tokenObj.getSessionId());
    }

    @Test
    public void sessionTokenTest2(){

        String token = TokenHandler.generateSessionToken(sessionID);
        SessionToken tokenObj = TokenHandler.readSessionToken(token);

        Assert.assertEquals(sessionID, tokenObj.getSessionId());
    }
}
