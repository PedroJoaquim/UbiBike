package pt.ist.cmu.ubibike.httpserver.cipher;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Pedro Joaquim on 11-03-2016.
 */
public class CipherUtilsTest {

    private static final String BANANA = "BANANA";
    private static final String PASSWORD = "UBIBIKE2015";
    private static final int SHA2_DIGEST_SIZE = 32;

    /**
     * test normal encoded decode
     */
    @Test
    public void testBase64(){

        String encodedBase64 = CipherUtils.encodeToBase64String(BANANA.getBytes());
        String decodedBase64 = new String(CipherUtils.decodeBase64FromString(encodedBase64));

        Assert.assertEquals(BANANA, decodedBase64);
    }


    @Test
    public void test2Base64(){

        String encodedBase64 = CipherUtils.encodeToBase64String(PASSWORD.getBytes());
        String decodedBase64 = new String(CipherUtils.decodeBase64FromString(encodedBase64));

        Assert.assertEquals(PASSWORD, decodedBase64);
    }

    /**
     * test bad cases
     */
    @Test
    public void test3Base64(){

        String encodedBase64 = CipherUtils.encodeToBase64String(PASSWORD.getBytes());
        String decodedBase64 = new String(CipherUtils.decodeBase64FromString(encodedBase64));

        Assert.assertNotEquals(BANANA, decodedBase64);
    }

    @Test
    public void test4Base64(){

        String encodedBase64 = CipherUtils.encodeToBase64String(PASSWORD.getBytes());
        String decodedBase64 = CipherUtils.encodeToBase64String(encodedBase64.getBytes());

        Assert.assertNotEquals(PASSWORD, decodedBase64);
    }

    @Test
    public void testSHA2Digest1(){

        byte[] digest = CipherUtils.getSHA2Digest(PASSWORD.getBytes());
        byte[] digest2 = CipherUtils.getSHA2Digest(PASSWORD.getBytes());

        Assert.assertArrayEquals(digest, digest2);
    }

    @Test
    public void testSHA2Digest2(){

        byte[] digest = CipherUtils.getSHA2Digest(PASSWORD.getBytes());

        Assert.assertEquals(digest.length, SHA2_DIGEST_SIZE);
    }

    @Test
    public void testSHA2Digest3(){

        byte[] digest = CipherUtils.getSHA2Digest(PASSWORD.getBytes());
        byte[] digest2 = CipherUtils.getSHA2Digest(BANANA.getBytes());

        Assert.assertNotEquals(digest, digest2);
    }
}
