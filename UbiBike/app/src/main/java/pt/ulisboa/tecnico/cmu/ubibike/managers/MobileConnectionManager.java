package pt.ulisboa.tecnico.cmu.ubibike.managers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class MobileConnectionManager {

    public static final boolean isOnline(Context context){

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();

        return (netInfo != null && netInfo.isConnected());
    }

    public static final boolean isWifiConn(Context context){

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return (netInfo != null && netInfo.isConnected());
    }

    public static final boolean isDataConn(Context context){

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return (netInfo != null && netInfo.isConnected());
    }
}

