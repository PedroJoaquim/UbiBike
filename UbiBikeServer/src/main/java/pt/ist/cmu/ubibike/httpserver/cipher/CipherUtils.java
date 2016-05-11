package pt.ist.cmu.ubibike.httpserver.cipher;

import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


public class CipherUtils {

    /*
     * Base 64 decode + decode
     */
    public static String encodeToBase64String(byte[] rawData){
        return Base64.getEncoder().encodeToString(rawData);
    }

    public static byte[] decodeBase64FromString(String encodedData){
        return Base64.getDecoder().decode(encodedData);
    }

    /*
     * SHA2 Hash function
     */

    public static byte[] getSHA2Digest(byte[] data){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(data);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }


    public static PublicKey readPublicKeyFromBytes(byte[] encodedKey) {
        try {
            return  KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encodedKey));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
