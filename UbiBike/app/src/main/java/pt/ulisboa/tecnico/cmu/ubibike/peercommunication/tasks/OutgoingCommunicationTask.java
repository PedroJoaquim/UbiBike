package pt.ulisboa.tecnico.cmu.ubibike.peercommunication.tasks;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;



/**
 * Receives 1 argument to create client socket
 * Receives 2 arguments to create client socket and send a message
 */
public class OutgoingCommunicationTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {

        SimWifiP2pSocket clientSocket = null;

        try {

            String virtualAddress = ApplicationContext.getInstance().
                    getNearbyPeerCommunication().getDeviceVirtualAddressByName(params[0]);

            String[] splittedAddr = virtualAddress.split(":");

            clientSocket = new SimWifiP2pSocket(splittedAddr[0], Integer.parseInt(splittedAddr[1]));

            clientSocket.getOutputStream().write((params[1] + "\n").getBytes());

            BufferedReader sockIn = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            sockIn.readLine();

        } catch (UnknownHostException e) {
            return "Unknown Host:" + e.getMessage();
        } catch (IOException e) {
            return "IO error:" + e.getMessage();
        } finally {
            try{
                if(clientSocket != null){
                    clientSocket.close();
                }
            } catch (IOException e) {
                //ignore
            }
        }

        return null;
    }
}
