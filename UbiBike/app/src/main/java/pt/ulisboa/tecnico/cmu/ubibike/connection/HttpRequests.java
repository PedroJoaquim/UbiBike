package pt.ulisboa.tecnico.cmu.ubibike.connection;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import pt.ulisboa.tecnico.cmu.ubibike.exceptions.HttpFailedRequestException;


/**
 * Created by andriy on 03.02.2016.
 */
public class HttpRequests {

    public static String performHttpCall(String type, String requestURL, JSONObject json) throws Exception{

        String response = "";

        URL url = new URL(requestURL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        //urlConnection.setReadTimeout(10000);
        //urlConnection.setConnectTimeout(10000);
        urlConnection.setRequestMethod(type);
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(type.equals("POST"));

        if(type.equals("POST")){
            urlConnection.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(json.toString());
            out.close();
        }


        int httpResponseCode = urlConnection.getResponseCode();

        if (httpResponseCode == HttpsURLConnection.HTTP_OK){
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while ((line = br.readLine()) != null) {
                response += line;
            }
        }
        else if(httpResponseCode == HttpURLConnection.HTTP_BAD_REQUEST){
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
            while ((line = br.readLine()) != null) {
                response += line;
            }
        }
        else if(httpResponseCode == HttpsURLConnection.HTTP_UNAUTHORIZED){
            urlConnection.disconnect();
        }
        else {
            urlConnection.disconnect();
            throw new HttpFailedRequestException();
        }

        urlConnection.disconnect();

        return response;
    }
}
