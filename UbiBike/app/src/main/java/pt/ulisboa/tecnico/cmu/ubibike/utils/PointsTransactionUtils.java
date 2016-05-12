package pt.ulisboa.tecnico.cmu.ubibike.utils;


import org.json.JSONException;
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

    public static int validateTransaction(JSONObject transactionJSON) {

        try {
            String validationToken = transactionJSON.getString(JsonParser.VALIDATION_TOKEN);
            String sourcePublicKeyToken = transactionJSON.getString(JsonParser.SOURCE_PUBLIC_KEY_TOKEN);
            String originalJSONBase64 = transactionJSON.getString(JsonParser.ORIGINAL_JSON_BASE_64);
            String originalJSON = new String(CipherManager.decodeFromBase64String(originalJSONBase64));

            JSONObject baseTransactionInfo = JsonParser.parseBasePointsTransaction(originalJSON);

            String sourceUsername = baseTransactionInfo.getString(JsonParser.SOURCE_USERNAME);
            String targetUsername = baseTransactionInfo.getString(JsonParser.TARGET_USERNAME);
            int sourceLogicalClock = baseTransactionInfo.getInt(JsonParser.SOURCE_LOGICAL_CLOCK);
            int points = baseTransactionInfo.getInt(JsonParser.POINTS);
            long timestamp = baseTransactionInfo.getLong(JsonParser.TIMESTAMP);


            JSONObject publicKeyToken = readPublicKeyToken(sourcePublicKeyToken);



        } catch (JSONException e) {
            return -1;
        }

        return -1;
    }

    private static JSONObject readPublicKeyToken(String sourcePublicKeyToken) {


        byte[] encodedToken = CipherManager.decodeFromBase64String(sourcePublicKeyToken);
        byte[] plainToken = CipherManager.decipher()


    }
}
