package pt.ist.cmu.ubibike.httpserver.cipher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

}
