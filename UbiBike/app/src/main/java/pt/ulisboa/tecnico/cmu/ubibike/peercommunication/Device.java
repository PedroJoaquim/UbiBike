package pt.ulisboa.tecnico.cmu.ubibike.peercommunication;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;

public class Device{

    private String mDeviceName;
    private String mUsername;
    private String mVirtualAddress;
    private SimWifiP2pSocket mClientSocket;


    public Device(String deviceName, String virtualAddress){
        mDeviceName = deviceName;
        mVirtualAddress = virtualAddress;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getVirtualAddress() {
        return mVirtualAddress;
    }

    public void setVirtualAddress(String mVirtualAddress) {
        this.mVirtualAddress = mVirtualAddress;
    }

    public SimWifiP2pSocket getClientSocket() {
        return mClientSocket;
    }

    public void setClientSockets(SimWifiP2pSocket clientSocket) {
        mClientSocket = clientSocket;
    }
}