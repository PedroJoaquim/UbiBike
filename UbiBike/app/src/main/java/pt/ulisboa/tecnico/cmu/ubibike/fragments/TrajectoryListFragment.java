package pt.ulisboa.tecnico.cmu.ubibike.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.R;
import pt.ulisboa.tecnico.cmu.ubibike.UbiBike;
import pt.ulisboa.tecnico.cmu.ubibike.adapters.TrajectoryArrayAdapter;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Trajectory;


public class TrajectoryListFragment extends ListFragment {

    private static final String TITLE = "Trajectories";

    private ArrayList<Trajectory> mTrajectories;

    public TrajectoryListFragment() {
    }

    private UbiBike getParentActivity(){
        return (UbiBike) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTrajectories = ApplicationContext.getInstance()
                                                .getData().getAllTrajectories();

        setHasOptionsMenu(false);
        getParentActivity().invalidateOptionsMenu();

        TrajectoryArrayAdapter adapter = new TrajectoryArrayAdapter(getActivity(), mTrajectories);

        setListAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        getParentActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getParentActivity().getSupportActionBar().setTitle(TITLE);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem item = menu.findItem(R.id.action_logout);
        item.setVisible(true);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        getParentActivity().showTrajectoryOnMap(mTrajectories.get(position).getTrajectoryID(), false, false);
    }

}
