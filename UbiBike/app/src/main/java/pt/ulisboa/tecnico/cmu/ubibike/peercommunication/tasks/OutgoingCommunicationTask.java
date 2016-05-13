package pt.ulisboa.tecnico.cmu.ubibike.peercommunication.tasks;

import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.utils.CommunicationUtils;


//params[0] = device name
//params[1] = message

public class OutgoingCommunicationTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {

        try {

            String virtualAddress = ApplicationContext.getInstance().
                    getNearbyPeerCommunication().getDeviceVirtualAddressByName(params[0]);

            String[] splittedAddr = virtualAddress.split(":");

            SimWifiP2pSocket clientSocket = new SimWifiP2pSocket(splittedAddr[0], Integer.parseInt(splittedAddr[1]));
            BufferedOutputStream outputStream = new BufferedOutputStream(clientSocket.getOutputStream());
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            outputStream.write((CommunicationUtils.toChannelFormat(params[1]) + "\n").getBytes());
            outputStream.flush();

            inputReader.readLine();
        } catch (UnknownHostException e) {
            return "Unknown Host:" + e.getMessage();
        } catch (IOException e) {
            return "IO error:" + e.getMessage();
        }

        return null;
    }
}
