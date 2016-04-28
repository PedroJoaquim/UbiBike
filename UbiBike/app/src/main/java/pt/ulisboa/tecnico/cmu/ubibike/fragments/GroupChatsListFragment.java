package pt.ulisboa.tecnico.cmu.ubibike.fragments;

import android.os.Bundle;
import android.app.ListFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.R;
import pt.ulisboa.tecnico.cmu.ubibike.UbiBike;
import pt.ulisboa.tecnico.cmu.ubibike.adapters.GroupChatArrayAdapter;

public class GroupChatsListFragment extends ListFragment {

    private static final String TITLE = "Group chats nearby";

    private ArrayList<String> mGroupChatOwners;

    public GroupChatsListFragment() {
    }

    private UbiBike getParentActivity(){
        return (UbiBike) getActivity();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGroupChatOwners = new ArrayList<>(ApplicationContext.getInstance().getData().
                getGroupChatsNearby().getGroupChats().keySet());

        GroupChatArrayAdapter adapter = new GroupChatArrayAdapter(getActivity(), mGroupChatOwners);

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

        getParentActivity().showGroupChat(mGroupChatOwners.get(position));
    }

}
