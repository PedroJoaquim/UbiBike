package pt.ulisboa.tecnico.cmu.ubibike.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Set;

import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.R;
import pt.ulisboa.tecnico.cmu.ubibike.UbiBike;
import pt.ulisboa.tecnico.cmu.ubibike.adapters.PeersChatAdapter;

public class ChatsListFragment extends Fragment {

    private static final String TITLE = "Chats nearby";

    private ListView mPeersNearbyListView;

    public ChatsListFragment() {
    }

    private UbiBike getParentActivity(){
        return (UbiBike) getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats_list, container, false);

        setUIElements(view);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getParentActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getParentActivity().getSupportActionBar().setTitle(TITLE);
    }

    public void setUIElements(View view){

        RelativeLayout groupChat = (RelativeLayout) view.findViewById(R.id.group_chat_layout);
        mPeersNearbyListView = (ListView) view.findViewById(R.id.peers_nearby_listView);


        if(ApplicationContext.getInstance().getNearbyPeerCommunication().getGroupChat().isEmpty()){
            groupChat.setVisibility(View.GONE);
        }
        else{
            groupChat.setVisibility(View.VISIBLE);
        }


        groupChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO open group chat here
            }
        });


        Set<String> nearDevices = ApplicationContext.getInstance().getNearbyPeerCommunication().getNearDevicesSet();
        final PeersChatAdapter adapter = new PeersChatAdapter(getActivity(), new ArrayList<>(nearDevices));

        mPeersNearbyListView.setAdapter(adapter);

        mPeersNearbyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String username = adapter.getItem(position);

                if (!ApplicationContext.getInstance().getNearbyPeerCommunication().doesIndividualChatExist(username)) {
                      ApplicationContext.getInstance().getNearbyPeerCommunication().addIndividualChat(username);
                }

                //TODO open individual chat here
            }
        });

    }
}
