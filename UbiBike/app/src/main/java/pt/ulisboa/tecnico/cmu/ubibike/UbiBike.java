package pt.ulisboa.tecnico.cmu.ubibike;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import pt.ulisboa.tecnico.cmu.ubibike.fragments.HomeFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.LoginFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.MapFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.RegisterAccountFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.TrajectoryListFragment;

public class UbiBike extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubi_bike);

        setViewElements();

       showHome();

    }

    @Override
    protected void onResume() {
        super.onResume();

        getSupportActionBar().show();
        getSupportActionBar().setTitle("UbiBike");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
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
    private void replaceFragment (Fragment fragment, boolean explicitReplace){
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
            ft.addToBackStack(backStateName);
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
     * Shows Home screen
     */
    public void showHome(){
        Fragment fragment = new HomeFragment();
        replaceFragment(fragment, false);
    }

    /**
     * Shows Login screen
     */
    public void showLogin(){
        getSupportActionBar().hide();

        Fragment fragment = new LoginFragment();
        replaceFragment(fragment, false);
    }

    /**
     * Shows Register Account screen
     */
    public void showRegisterAccountFragment(){
        Fragment fragment = new RegisterAccountFragment();
        replaceFragment(fragment, false);
    }

    /**
     * Shows nearby bike stations on map
     */
    public void showBikeStationsNearbyOnMap(){

        Fragment fragment = new MapFragment();
        replaceFragment(fragment, false);
    }

    /**
     * Shows past trajectories on list, most recent first
     */
    public void showPastTrajectoriesList(){
        Fragment fragment = new TrajectoryListFragment();
        replaceFragment(fragment, false);
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

        replaceFragment(fragment, explicitReplace);
    }

}
