package pt.ulisboa.tecnico.cmu.ubibike.managers;

import android.util.Base64;
import android.util.Log;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;

/**
 * Created by andriy on 13.04.2016.
 */
public class CipherManager {

    private static PublicKey publicKey = null;
    private static PrivateKey privateKey = null;



    /**
     * @param rawData - data to encode
     * @return - base 64 string
     */
    public static String encodeToBase64String(byte[] rawData){
        return Base64.encodeToString(rawData, Base64.DEFAULT);
    }

    /**
     * @param encodedData - data to decode from
     * @return - decoded byte[]
     */
    public static byte[] decodeFromBase64String(String encodedData){
        return Base64.decode(encodedData, Base64.DEFAULT);
    }

    /**
     * Gets PublicKey
     *
     * @param keyBytes - key bytes
     * @return - public key object
     */
    public static  PublicKey getPublicKeyFromBytes(byte[] keyBytes){

        try {

            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = null;

            kf = KeyFactory.getInstance("RSA");

            publicKey = kf.generatePublic(spec);
            return publicKey;

        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Gets PrivateKey
     *
     * @param keyBytes - key bytes
     * @return - private key object
     */
    public static PrivateKey getPrivateKeyFromBytes(byte[] keyBytes) {

        try {

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            privateKey = kf.generatePrivate(spec);
            return privateKey;

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Generates key pair
     */
    public static void generatePublicPrivateKeyPair(){

        try {

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(512);
            KeyPair keyPair = keyGen.generateKeyPair();

            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();


        } catch (NoSuchAlgorithmException e) {
            Log.e("Uncaught exception", e.toString());
        }
    }

    /**
     * @return - previously generated public key
     */
    public static PublicKey getPublicKey(){
        return publicKey;
    }

    /**
     * @return - previously generated private key
     */
    public static PrivateKey getPrivateKey(){
        return privateKey;
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
