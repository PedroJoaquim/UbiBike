package pt.ulisboa.tecnico.cmu.ubibike.fragments.chats;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.R;
import pt.ulisboa.tecnico.cmu.ubibike.adapters.MessageListAdapter;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.ChatMessage;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.NearbyPeerCommunication;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.tasks.OutgoingCommunicationTask;

/**
 * Created by ASUS on 11/05/2016.
 */
public class GroupChatFragment extends ChatFragment{


    @Override
    protected void createSpecificChat() {
        mChat = ApplicationContext.getInstance().getNearbyPeerCommunication().getGroupChat().getChat();

        String groupOwner = ApplicationContext.getInstance().getNearbyPeerCommunication().
                getGroupChat().getOwner();

        try{
            if(groupOwner != null) {
                getParentActivity().getSupportActionBar().setTitle("Group hosted by '" + groupOwner + "'");
            }
            else{
                getParentActivity().getSupportActionBar().setTitle("Group chat");
            }
        }catch (Exception e){}
    }

    @Override
    protected void setViewElements(View view) {

        mListView = (ListView) view.findViewById(R.id.list);
        ImageButton send_button = (ImageButton) view.findViewById(R.id.chat_send_button);
        final EditText message_editText = (EditText) view.findViewById(R.id.chat_message_editText);

        send_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String input = message_editText.getText().toString();
                String myUsername = ApplicationContext.getInstance().getData().getUsername();

                String msg = NearbyPeerCommunication.buildGroupChatMessage(myUsername, input);

                mChat.addNewMessage(new ChatMessage(false, myUsername, input));
                updateUI();

                ArrayList<String> groupDeviceNames = new ArrayList<>(ApplicationContext.getInstance().
                        getNearbyPeerCommunication().getGroupChat().getMembers());


                for(String deviceName : groupDeviceNames){
                    new OutgoingCommunicationTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            deviceName, msg);
                }

                cleanMessageInput();
            }
        });


        mMessages = mChat.getAllMessages();

        MessageListAdapter messageListAdapter = new MessageListAdapter(getActivity(), mMessages, true);
        mListView.setAdapter(messageListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView date;

                //getting right index to access the element. Because elements that are not totally
                // visible are deleted from viewGroup
                int index = position - mListView.getFirstVisiblePosition();

                //in case if element is not totally visible, android doesn't have it in memory
                // (is not in viewGroup)
                if (index > mListView.getLastVisiblePosition()) return;

                if (mMessages.get(position).isReceived()) {
                    date = (TextView) mListView.getChildAt(index).
                            findViewById(R.id.dateInBubble_textView);
                } else {
                    date = (TextView) mListView.getChildAt(index).
                            findViewById(R.id.dateOutBubble_textView);
                }

                if (date.getVisibility() == View.GONE) {
                    date.setVisibility(View.VISIBLE);
                } else {
                    date.setVisibility(View.GONE);
                }

                int shown = mListView.getLastVisiblePosition() -
                        mListView.getFirstVisiblePosition();

                if (index == shown) {
                    mListView.setSelection(mListView.getAdapter().getCount());
                }
            }
        });
    }


    @Override
    public void updateUI(){
        super.updateUI();

        if(ApplicationContext.getInstance().getNearbyPeerCommunication().getGroupChat().isEmpty()){
            Toast.makeText(getActivity(), "Group is empty. No users to chat with.", Toast.LENGTH_SHORT).show();
            getParentActivity().onBackPressed();
        }
    }
}
