package pt.ist.cmu.ubibike.httpserver.cipher;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by Pedro Joaquim on 11-03-2016.
 */
public class CipherTest {

    private static final String BANANA = "BANANA";
    private static final String LETUCE = "LETUCE";
    private static final String TOMATO = "TOMATO";


    private static PublicKey publicKey;
    private static PrivateKey privateKey;

    @BeforeClass
    public static void oneTimeSetup(){
        publicKey = PublicKeyReader.getKey();
        privateKey = PrivateKeyReader.getKey();
    }

    @Test
    public void testPublicCipher(){

        byte[] encodedBananaBytes = CipherManager.cipher(BANANA.getBytes(), publicKey);
        byte[] decodedBananaBytes = CipherManager.decipher(encodedBananaBytes, privateKey);

        Assert.assertEquals(BANANA, new String(decodedBananaBytes));
    }

    @Test
    public void testPublicCipher2(){

        byte[] encodedLetuceBytes = CipherManager.cipher(LETUCE.getBytes(), publicKey);
        byte[] decodedLetuceBytes = CipherManager.decipher(encodedLetuceBytes, privateKey);

        Assert.assertEquals(LETUCE, new String(decodedLetuceBytes));
    }

    @Test
    public void testPublicCipher3(){

        byte[] encodedLetuceBytes = CipherManager.cipher(LETUCE.getBytes(), publicKey);
        byte[] badDecodedLetuceBytes = CipherManager.decipher(encodedLetuceBytes, publicKey);

        Assert.assertNull(badDecodedLetuceBytes);
    }

    @Test
    public void testPrivateCipher(){

        byte[] encodedBananaBytes = CipherManager.cipher(BANANA.getBytes(), privateKey);
        byte[] decodedBananaBytes = CipherManager.decipher(encodedBananaBytes, publicKey);

        Assert.assertEquals(BANANA, new String(decodedBananaBytes));
    }

    @Test
    public void testPrivateCipher2(){

        byte[] encodedLetuceBytes = CipherManager.cipher(LETUCE.getBytes(), privateKey);
        byte[] decodedLetuceBytes = CipherManager.decipher(encodedLetuceBytes, publicKey);

        Assert.assertEquals(LETUCE, new String(decodedLetuceBytes));
    }

    @Test
    public void testPrivateCipher3(){

        byte[] encodedLetuceBytes = CipherManager.cipher(LETUCE.getBytes(), privateKey);
        byte[] badDecodedLetuceBytes = CipherManager.decipher(encodedLetuceBytes, privateKey);

        Assert.assertNull(badDecodedLetuceBytes);
    }



}
