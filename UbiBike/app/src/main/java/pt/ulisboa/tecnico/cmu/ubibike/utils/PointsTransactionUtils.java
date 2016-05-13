package pt.ulisboa.tecnico.cmu.ubibike.utils;


import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Array;
import java.util.Arrays;

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

            final String sourceUsername = baseTransactionInfo.getString(JsonParser.SOURCE_USERNAME);
            String targetUsername = baseTransactionInfo.getString(JsonParser.TARGET_USERNAME);
            final int points = baseTransactionInfo.getInt(JsonParser.POINTS);
            long timestamp = baseTransactionInfo.getLong(JsonParser.TIMESTAMP);


            JSONObject publicKeyToken = readPublicKeyToken(sourcePublicKeyToken);

            if(publicKeyToken == null){
                return -1;
            }

            if(!validPublicKeyToken(publicKeyToken, sourceUsername)){
                return -1;
            }


            PublicKey sourcePublicKey = readPublicKeyFromToken(publicKeyToken);

            //check data integrity
            if(!checkValidationToken(validationToken, originalJSON, sourcePublicKey)){
                return -1;
            }

            //check freshness
            if(ApplicationContext.getInstance().getData().doesTransactionExist(sourceUsername, timestamp)){
                return -1;
            }

            //check im the intended target user
            if(!targetUsername.equals(ApplicationContext.getInstance().getData().getUsername())){
                return -1;
            }

            ApplicationContext.getInstance().getData().addTransactionLog(sourceUsername, timestamp);

            ApplicationContext.getInstance().getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ApplicationContext.getInstance(),"Received " + points + " points from " + sourceUsername , Toast.LENGTH_SHORT).show();
                }
            });
            //finally return the points
            return points;

        } catch (JSONException e) {
            return -1;
        }
    }

    private static boolean checkValidationToken(String validationToken, String originalJSON, PublicKey sourcePublicKey) {

        byte[] hash1 = CipherManager.decipher(CipherManager.decodeFromBase64String(validationToken), sourcePublicKey);
        byte[] hash2 = CipherManager.getSHA2Digest(originalJSON.getBytes());

        return Arrays.equals(hash1, hash2);
    }

    private static PublicKey readPublicKeyFromToken(JSONObject publicKeyToken) throws JSONException {
        String publicKey = publicKeyToken.getString(JsonParser.PUBLIC_KEY);
        byte[] encodedSourcePublicKey = CipherManager.decodeFromBase64String(publicKey);

        return CipherManager.getPublicKeyFromBytes(encodedSourcePublicKey);
    }

    private static boolean validPublicKeyToken(JSONObject publicKeyToken, String sourceUsername) throws JSONException {

        String username = publicKeyToken.getString(JsonParser.USERNAME);
        long ttl = Long.valueOf(publicKeyToken.getString(JsonParser.TTL));
        long currentTime = System.currentTimeMillis();

        return username.equals(sourceUsername) &&
                ttl >= currentTime;
    }

    private static JSONObject readPublicKeyToken(String sourcePublicKeyToken) {


        byte[] encodedToken = CipherManager.decodeFromBase64String(sourcePublicKeyToken);
        byte[] plainToken = CipherManager.decipher(encodedToken, ApplicationContext.getInstance().getData().getServerPublicKey());

        return JsonParser.parsePublicKeyToken(new String(plainToken));
    }
}
