package pt.ulisboa.tecnico.cmu.ubibike.fragments.chats;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.R;
import pt.ulisboa.tecnico.cmu.ubibike.UbiBike;
import pt.ulisboa.tecnico.cmu.ubibike.adapters.MessageListAdapter;
import pt.ulisboa.tecnico.cmu.ubibike.adapters.PeersChatAdapter;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.UpdatableUI;

public class ChatsListFragment extends Fragment implements UpdatableUI {

    private static final String TITLE = "Chats nearby";

    private ListView mPeersNearbyListView;
    private View mView;

    public ChatsListFragment() {
    }

    private UbiBike getParentActivity(){
        return (UbiBike) getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_chats_list, container, false);

        getParentActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setUIElements();

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getParentActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getParentActivity().getSupportActionBar().setTitle(TITLE);

        ApplicationContext.getInstance().setCurrentFragment(this);

    }

    @Override
    public void onPause(){
        super.onPause();

        ApplicationContext.getInstance().setCurrentFragment(null);
    }

    public void setUIElements(){

        RelativeLayout groupChat = (RelativeLayout) mView.findViewById(R.id.group_chat_layout);

        mPeersNearbyListView = (ListView) mView.findViewById(R.id.peers_nearby_listView);

        updateGroupChatUI();


        groupChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentActivity().showGroupChat();
            }
        });


        ArrayList<String> username =  ApplicationContext.getInstance().getNearbyPeerCommunication().getGroupUsernameSet();
        final PeersChatAdapter adapter = new PeersChatAdapter(getActivity(), username);

        mPeersNearbyListView.setAdapter(adapter);

        mPeersNearbyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String username = adapter.getItem(position);

                if (!ApplicationContext.getInstance().getNearbyPeerCommunication().doesIndividualChatExist(username)) {
                    ApplicationContext.getInstance().getNearbyPeerCommunication().addIndividualChat(username);
                }

                getParentActivity().showIndividualChat(username);
            }
        });
    }

    @Override
    public void updateUI() {

        ((PeersChatAdapter) mPeersNearbyListView.getAdapter()).notifyDataSetChanged();

        updateGroupChatUI();
    }


    private void updateGroupChatUI(){
        RelativeLayout groupChat = (RelativeLayout) mView.findViewById(R.id.group_chat_layout);


        if(ApplicationContext.getInstance().getNearbyPeerCommunication().getGroupChat().isEmpty()){
            groupChat.setVisibility(View.GONE);
            mView.findViewById(R.id.no_nearby_textView).setVisibility(View.VISIBLE);
        }
        else{
            mView.findViewById(R.id.no_nearby_textView).setVisibility(View.GONE);
            groupChat.setVisibility(View.VISIBLE);
            TextView group_textView = (TextView) mView.findViewById(R.id.group_textView);

            String groupOwner = ApplicationContext.getInstance().getNearbyPeerCommunication().
                    getGroupChat().getOwner();
            if( groupOwner != null) {
                group_textView.setText("Group hosted by '" + groupOwner + "'");
            }
        }
    }
}
