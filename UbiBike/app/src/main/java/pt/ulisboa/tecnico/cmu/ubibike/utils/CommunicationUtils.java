package pt.ulisboa.tecnico.cmu.ubibike.utils;

/**
 * Created by Pedro Joaquim on 13-05-2016.
 */
public class CommunicationUtils {

    private static final String PATTERN = "JoAquIM";

    public static String toChannelFormat(String input){
        return input.replaceAll("\\n", PATTERN);
    }

    public static String fromChannelFormat(String input){
        return input.replaceAll(PATTERN, "\n");
    }
}
