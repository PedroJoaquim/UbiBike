package pt.ulisboa.tecnico.cmu.ubibike.connection;

/**
 * Created by andriy on 12.03.2016.
 */

import android.content.Context;

import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.managers.StorageManager;


/**
 * Created by andriy on 03.02.2016.
 */
public class ServerCommunicationHandler {

    private Context context;
    private String token;

    private String clientID;


    public static String HOST_SERVER = "...";

    public ServerCommunicationHandler(Context context){
        this.context = context;
    }





    /**
     * Getters & setters
     */
    private StorageManager getStorageManager(){
        return ApplicationContext.getInstance().getStorageManager();
    }
}

