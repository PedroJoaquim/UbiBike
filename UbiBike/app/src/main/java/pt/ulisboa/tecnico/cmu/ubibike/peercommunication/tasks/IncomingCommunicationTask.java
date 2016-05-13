package pt.ulisboa.tecnico.cmu.ubibike.peercommunication.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.NearbyPeerCommunication;
import pt.ulisboa.tecnico.cmu.ubibike.utils.CommunicationUtils;


public class IncomingCommunicationTask extends AsyncTask<Void, String, Void> {

    private SimWifiP2pSocketServer serverSocket;

    @Override
    protected Void doInBackground(Void... params) {

        try {

            serverSocket = new SimWifiP2pSocketServer(10001);

        } catch (IOException e) {
            Log.e("Uncaught exception", e.toString());
        }
        while (!Thread.currentThread().isInterrupted()) {

            try {

                SimWifiP2pSocket sock = serverSocket.accept();

                try {
                    BufferedReader inputStream = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                    BufferedOutputStream outputStream = new BufferedOutputStream(sock.getOutputStream());

                    String content = CommunicationUtils.fromChannelFormat(inputStream.readLine());

                    String response = NearbyPeerCommunication.processReceivedMessage(content);

                    outputStream.write((response + "\n").getBytes());
                    outputStream.flush();
                }
                catch (IOException e) {
                    Log.d("Error reading socket:", e.getMessage());
                }
            }
            catch (IOException e) {
                Log.d("Error socket:", e.getMessage());
                break;
            }
        }

        return null;
    }
}