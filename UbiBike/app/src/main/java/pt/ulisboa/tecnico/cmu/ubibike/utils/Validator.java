package pt.ulisboa.tecnico.cmu.ubibike.utils;

/**
 * Created by andriy on 19.04.2016.
 */
public class Validator {

    public static boolean isUsernameValid(String username){
        return !(username.length() < 5 || username.length() > 25 || !username.matches("^[a-zA-Z0-9_]+$"));
    }

    public static boolean isPasswordValid(String password){
        return !(password.length() < 3 || password.length() > 25);
    }
}
