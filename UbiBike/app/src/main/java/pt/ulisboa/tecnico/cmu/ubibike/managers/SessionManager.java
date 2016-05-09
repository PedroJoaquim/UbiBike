package pt.ulisboa.tecnico.cmu.ubibike.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import pt.ulisboa.tecnico.cmu.ubibike.UbiBike;

public class SessionManager {

    SharedPreferences mPref;
    Editor mEditor;

    UbiBike mActivity;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "UbiBikePref";
    private static final String IS_LOGGED_IN = "IsLoggedIn";
    public static final String KEY_USER_ID = "uid";


    public SessionManager(UbiBike acty){
        mActivity = acty;
        mPref = mActivity.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mEditor = mPref.edit();
    }

    public SessionManager(Context ctx){
        mActivity = null;
        mPref = ctx.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mEditor = mPref.edit();
    }



    /**
     * Creates login session given an user id
     */
    public void createLoginSession(int uid){

        mEditor.putBoolean(IS_LOGGED_IN, true);
        mEditor.putInt(KEY_USER_ID, uid);
        mEditor.commit();
    }

    /**
     * Checks login status
     */
    public void checkLogin(){

        if(!isLoggedIn()){

        }
        else{

        }

    }

    /**
     * Clears session details
     * */
    public void logoutUser(){

        mEditor.clear();
        mEditor.commit();

        //TODO redirect to login screen

    }

    /**
     * Gets login state
     *
     * @return - boolean
     */
    public boolean isLoggedIn(){
        return mPref.getBoolean(IS_LOGGED_IN, false);
    }

    public int getLoggedUser(){
        return mPref.getInt(KEY_USER_ID, 0);
    }
}
