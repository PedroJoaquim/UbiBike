package pt.ulisboa.tecnico.cmu.ubibike;

import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageInstaller;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import pt.ulisboa.tecnico.cmu.ubibike.domain.Data;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.HomeFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.LoginFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.MapFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.RegisterAccountFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.TrajectoryListFragment;
import pt.ulisboa.tecnico.cmu.ubibike.managers.MobileConnectionManager;
import pt.ulisboa.tecnico.cmu.ubibike.managers.SessionManager;
import pt.ulisboa.tecnico.cmu.ubibike.utils.JsonParser;

public class UbiBike extends AppCompatActivity {

    private SessionManager mSessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubi_bike);

        ApplicationContext.getInstance().setActivity(this);
        mSessionManager = new SessionManager(this);

        setViewElements();

        checkLogin();

    }



    private void checkLogin() {

        if(mSessionManager.isLoggedIn()){
            showHome();
        }
        else{
            showLogin();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        getSupportActionBar().show();
        getSupportActionBar().setTitle("UbiBike");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_logout:
                mSessionManager.logoutUser();
                showLogin();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void setViewElements() {

        // Set a toolbar to replace the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }


    /**
     * Loads new fragment
     *
     * @param fragment - fragment to be showed
     */
    private void replaceFragment (Fragment fragment, boolean explicitReplace,  boolean addToBackStack){
        String backStateName =  fragment.getClass().getName();
        String fragmentTag = backStateName;
        boolean fragmentPopped = false;

        FragmentManager manager = getSupportFragmentManager();

        if(!explicitReplace){
            fragmentPopped = manager.popBackStackImmediate (backStateName, 0);
        }

        if(!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null){ //fragment not in back stack, create it.

            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.content_frame, fragment, fragmentTag);
            if(addToBackStack) {
                ft.addToBackStack(backStateName);
            }
            ft.commit();
        }
        else if(explicitReplace){

            manager.popBackStack();
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.content_frame, fragment, fragmentTag);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    /**
     * Creates a session to user and shows Home screen
     */
    public void finishLogin(){
        Data appData = ApplicationContext.getInstance().getData();
        mSessionManager.createLoginSession(appData.getUid());

        showHome();
    }


    /**
     * Shows Home screen
     */
    public void showHome(){
        Fragment fragment = new HomeFragment();
        replaceFragment(fragment, true, false);
    }

    /**
     * Shows Login screen
     */
    public void showLogin(){
        Fragment fragment = new LoginFragment();
        replaceFragment(fragment, true, false);
    }

    /**
     * Shows Register Account screen
     */
    public void showRegisterAccountFragment(){
        Fragment fragment = new RegisterAccountFragment();
        replaceFragment(fragment, false, true);
    }

    /**
     * Shows nearby bike stations on map
     */
    public void showBikeStationsNearbyOnMap(boolean afterRequest){


        if(!afterRequest && MobileConnectionManager.isOnline(this)){    //perform request
            ApplicationContext.getInstance().getServerCommunicationHandler().performStationsNearbyRequest();
            return;
        }

        Fragment fragment = new MapFragment();
        replaceFragment(fragment, false, true);
    }

    /**
     * Shows past trajectories on list, most recent first
     */
    public void showPastTrajectoriesList(){

        if(ApplicationContext.getInstance().getData().getAllTrajectories().isEmpty()){
            Toast.makeText(UbiBike.this, "No trajectories registered yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        Fragment fragment = new TrajectoryListFragment();
        replaceFragment(fragment, false, true);
    }

    /**
     * Shows a given trajectory on map
     *
     * @param trajectoryID - trajectory to show
     * @param explicitReplace - not relevant here
     */
    public void showTrajectoryOnMap(int trajectoryID, boolean explicitReplace){
        Fragment fragment = new MapFragment();

        int trajectoriesCount = ApplicationContext.getInstance().getData().getTrajectoriesCount();

        Bundle arguments = new Bundle();
        arguments.putInt("trajectoryID", trajectoryID);
        arguments.putInt("trajectoriesCount", trajectoriesCount);
        fragment.setArguments(arguments);

        replaceFragment(fragment, explicitReplace, true);
    }


}
