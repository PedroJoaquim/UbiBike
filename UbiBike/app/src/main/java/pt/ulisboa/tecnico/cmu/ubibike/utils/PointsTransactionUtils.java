package pt.ulisboa.tecnico.cmu.ubibike.utils;


import org.json.JSONObject;

import java.security.PrivateKey;

import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.managers.CipherManager;

public class PointsTransactionUtils {

    public static JSONObject generatePointsTransactionJSON(String deviceName, String targetUsername, int points) {

        String sourceUsername = ApplicationContext.getInstance().getData().getUsername();
        String publicKeyToken = ApplicationContext.getInstance().getData().getPublicToken();
        int logicalClock = ApplicationContext.getInstance().getData().getNextLogicalClock();
        long timestamp = System.currentTimeMillis();


        //get original json and then encode to base64
        JSONObject pointsTransactionData = JsonParser.buildPointsTransactionDataJson(sourceUsername, targetUsername, logicalClock, points, timestamp);
        String originalJson = pointsTransactionData.toString();
        String originalJsonBase64 = CipherManager.encodeToBase64String(originalJson.getBytes());


        String validationToken = generateValidationToken(originalJson);

        //get the final JSON to send
        return JsonParser.buildPointsTransactionFinalJson(originalJsonBase64, publicKeyToken, validationToken);

    }


    private static String generateValidationToken(String originalJSON){

        //hash and sign the original json content
        PrivateKey sKey = ApplicationContext.getInstance().getData().getPrivateKey();
        byte[] plainDataHash = CipherManager.getSHA2Digest(originalJSON.getBytes());
        byte[] encodedDataHash = CipherManager.cipher(plainDataHash, sKey);

        return CipherManager.encodeToBase64String(encodedDataHash);
    }

}
