package pt.ulisboa.tecnico.cmu.ubibike.peercommunication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;


public class CommunicationTasks {

    public static final String TAG = "UbiBike";

    private static final int PORT = 8000;

    public class IncomingCommunicationTask extends AsyncTask<Void, String, Void> {

        private SimWifiP2pSocketServer serverSocket;

        @Override
        protected Void doInBackground(Void... params) {

            Log.d(TAG, "IncommingCommTask started (" + this.hashCode() + ").");
            String receivedContent = "";

            try {

                serverSocket = new SimWifiP2pSocketServer(PORT);

                ApplicationContext.getInstance().getNearbyPeerCommunication().setDeviceServerSocket(serverSocket);

            } catch (IOException e) {
                Log.e("Uncaught exception", e.toString());
            }
            while (!Thread.currentThread().isInterrupted()) {

                try {

                    SimWifiP2pSocket sock = serverSocket.accept();

                    try {
                        BufferedReader sockIn = new BufferedReader(
                                new InputStreamReader(sock.getInputStream()));
                        String st = sockIn.readLine();
                        receivedContent += st;
                        sock.getOutputStream().write(("\n").getBytes());
                        Log.d(TAG, "Received: " + receivedContent);

                        NearbyPeerCommunication.processReceivedMessage(receivedContent);

                    }
                    catch (IOException e) {
                        Log.d("Error reading socket:", e.getMessage());
                    }
                    finally {
                        sock.close();
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

    public class OutgoingCommunicationTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {

                String virtualAddress = ApplicationContext.getInstance().
                        getNearbyPeerCommunication().getDeviceNearbyVirtualAddress(params[0]);

                SimWifiP2pSocket clientSocket = new SimWifiP2pSocket(virtualAddress, PORT);

                ApplicationContext.getInstance().
                        getNearbyPeerCommunication().addNearDeviceClientSocket(params[0], clientSocket);



            } catch (UnknownHostException e) {
                return "Unknown Host:" + e.getMessage();
            } catch (IOException e) {
                return "IO error:" + e.getMessage();
            }
            return null;
        }
    }

    public class TransferDataTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... param) {
            try {

                SimWifiP2pSocket clientSocket = ApplicationContext.getInstance().
                        getNearbyPeerCommunication().getNearDeviceClientSocketByUsername(param[0]);

                clientSocket.getOutputStream().write((param[1] + "\n").getBytes());
                BufferedReader sockIn = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                sockIn.readLine();
                clientSocket.close();

            } catch (IOException e) {
                Log.e("Uncaught exception", e.toString());
            }

            return null;
        }
    }

}
