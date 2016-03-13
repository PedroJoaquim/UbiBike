package pt.ulisboa.tecnico.cmu.ubibike;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import pt.ulisboa.tecnico.cmu.ubibike.fragments.LoginFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.MapFragment;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.RegisterAccountFragment;

public class UbiBike extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubi_bike);

        setViewElements();

        //showTrajectoryOnMap(0);
        showBikeStationsNearbyOnMap();

    }

    private void setViewElements() {

        // Set a toolbar to replace the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        showLoginFragment();
    }


    /**
     * Loads new fragment
     *
     * @param fragment - fragment to be showed
     */
    private void replaceFragment (Fragment fragment){
        String backStateName =  fragment.getClass().getName();
        String fragmentTag = backStateName;

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.content_frame, fragment, fragmentTag);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    public void showLoginFragment(){
        Fragment fragment = new LoginFragment();
        replaceFragment(fragment);
    }

    public void showRegisterAccountFragment(){
        Fragment fragment = new RegisterAccountFragment();
        replaceFragment(fragment);
    }

    public void showBikeStationsNearbyOnMap(){
        Fragment fragment = new MapFragment();
        replaceFragment(fragment);
    }

    public void showTrajectoryOnMap(int trajectoryID){
        Fragment fragment = new MapFragment();

        Bundle arguments = new Bundle();
        arguments.putInt("trajectoryID", trajectoryID);
        fragment.setArguments(arguments);

        replaceFragment(fragment);
    }

    public void showToolbar(boolean show){

        if(show) {
            getSupportActionBar().show();
        }
        else {
            getSupportActionBar().hide();
        }
    }


}
