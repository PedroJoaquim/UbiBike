package pt.ulisboa.tecnico.cmu.ubibike.peercommunication.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;



public class TransferDataTask extends AsyncTask<String, String, Void> {

    @Override
    protected Void doInBackground(String... param) {
        try {

            SimWifiP2pSocket clientSocket = ApplicationContext.getInstance().
                    getNearbyPeerCommunication().getNearDeviceClientSocketByDeviceName(param[0]);

            clientSocket.getOutputStream().write((param[1] + "\n").getBytes());
            BufferedReader sockIn = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            sockIn.readLine();

        } catch (IOException e) {
            Log.e("Uncaught exception", e.toString());
        }

        return null;
    }
}
