package pt.ulisboa.tecnico.cmu.ubibike;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

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

       showPastTrajectoriesList();

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
    private void replaceFragment (Fragment fragment, boolean explicitReplace, boolean addToBackStack){
        String backStateName =  fragment.getClass().getName();
        String fragmentTag = backStateName;

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (explicitReplace || !fragmentPopped && manager.findFragmentByTag(fragmentTag) == null){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.content_frame, fragment, fragmentTag);
            if(addToBackStack) {
                ft.addToBackStack(backStateName);
            }
            ft.commit();
        }
    }

    public void showLoginFragment(){
        getSupportActionBar().hide();

        Fragment fragment = new LoginFragment();
        replaceFragment(fragment, false, false);
    }

    public void showRegisterAccountFragment(){
        Fragment fragment = new RegisterAccountFragment();
        replaceFragment(fragment, false, true);
    }

    public void showBikeStationsNearbyOnMap(){

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Stations nearby");

        Fragment fragment = new MapFragment();
        replaceFragment(fragment, true, true);
    }

    public void showPastTrajectoriesList(){
        Fragment fragment = new TrajectoryListFragment();
        replaceFragment(fragment, true, true);
    }

    public void showTrajectoryOnMap(int trajectoryID, boolean addToBackStack){
        Fragment fragment = new MapFragment();

        int trajectoryCount = ApplicationContext.getInstance().getData().getTrajectoriesCount();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Trajectory view (" + (trajectoryID + 1) + "/" + trajectoryCount + ")");

        Bundle arguments = new Bundle();
        arguments.putInt("trajectoryID", trajectoryID);
        fragment.setArguments(arguments);

        replaceFragment(fragment, true, addToBackStack);
    }

}
