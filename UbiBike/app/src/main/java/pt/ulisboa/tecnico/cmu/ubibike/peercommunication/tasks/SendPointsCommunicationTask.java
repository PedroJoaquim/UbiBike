package pt.ulisboa.tecnico.cmu.ubibike.peercommunication.tasks;

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.NearbyPeerCommunication;
import pt.ulisboa.tecnico.cmu.ubibike.utils.CommunicationUtils;
import pt.ulisboa.tecnico.cmu.ubibike.utils.JsonParser;
import pt.ulisboa.tecnico.cmu.ubibike.utils.PointsTransactionUtils;

public class SendPointsCommunicationTask extends AsyncTask<String, Void, String> {

    /*
     * param[0] = device name
     * param[1] = target username
     * param[2] = points
     */

    @Override
    protected String doInBackground(String... params) {

        if(params.length < 3){
            return "invalid usage: device_name target_username points";
        }

        String deviceName = params[0];
        String targetUsername = params[1];
        int points = Integer.valueOf(params[2]);
        JSONObject pointsTransactionJSON = PointsTransactionUtils.generatePointsTransactionJSON(deviceName, targetUsername, points);
        String finalMessage = NearbyPeerCommunication.buildPointsTransactionMessage(pointsTransactionJSON.toString());

        int targetLogicalClock = sendMessage(finalMessage, deviceName);

        if(targetLogicalClock == -1){
            return "Erorr in the communication";
        }

        ApplicationContext.getInstance().getData().removePoints(points);

        //send points to server
        JSONObject pointsTransactionServer = JsonParser.buildPointsTransactionServerJSON(pointsTransactionJSON, targetLogicalClock);
        ApplicationContext.getInstance().getServerCommunicationHandler().performPointsTransactionRequest(pointsTransactionServer);

        return "";
    }

    private int sendMessage(String finalMessage, String deviceName) {

        String targetLogicalClock = "";


        try {

            String virtualAddress = ApplicationContext.getInstance().getNearbyPeerCommunication().getDeviceVirtualAddressByName(deviceName);

            String[] splittedAddr = virtualAddress.split(":");

            SimWifiP2pSocket clientSocket = new SimWifiP2pSocket(splittedAddr[0], Integer.parseInt(splittedAddr[1]));
            BufferedOutputStream outputStream = new BufferedOutputStream(clientSocket.getOutputStream());
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            outputStream.write((CommunicationUtils.toChannelFormat(finalMessage) + "\n").getBytes());
            outputStream.flush();

            targetLogicalClock = inputReader.readLine();

        } catch (UnknownHostException e) {
            return -1;
        } catch (IOException e) {
            return -1;
        }

        return Integer.valueOf(targetLogicalClock);
    }
}
