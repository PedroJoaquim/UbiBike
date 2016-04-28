package pt.ulisboa.tecnico.cmu.ubibike.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.R;
import pt.ulisboa.tecnico.cmu.ubibike.adapters.MessageListAdapter;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.Chat;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.ChatMessage;

public class ChatFragment extends ListFragment {

    public static final String KEY_GROUP_OWNER = "group_owner";

    private List<ChatMessage> mMessages;
    private Chat mChat;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String groupOwner = getArguments().getString(KEY_GROUP_OWNER);
        mChat = ApplicationContext.getInstance().getData().
                             getGroupChatsNearby().getGroupChatByGroupOwner(groupOwner).getChat();

        mMessages = mChat.getAllMessages();

        MessageListAdapter messageListAdapter = new MessageListAdapter(getActivity(), mMessages);
        setListAdapter(messageListAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        setViewElements(view);

        return view;
    }


    private void setViewElements(View view){

        final ListView conversation_listView = (ListView) view.findViewById(R.id.conversation_listView);
        ImageButton send_button = (ImageButton) view.findViewById(R.id.chat_send_button);
        final EditText message_editText = (EditText) view.findViewById(R.id.chat_message_editText);

        send_button.setClickable(false);
        send_button.setEnabled(false);


        send_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String messageContent = message_editText.getText().toString();

                //TODO action to send message
            }
        });

    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        TextView date;

        //getting right index to access the element. Because elements that are not totally
        // visible are deleted from viewGroup
        int index = position - getListView().getFirstVisiblePosition();

        //in case if element is not totally visible, android doesn't have it in memory
        // (is not in viewGroup)
        if (index > getListView().getLastVisiblePosition()) return;

        if (mMessages.get(position).isReceived()){
            date = (TextView) getListView().getChildAt(index).
                    findViewById(R.id.dateInBubble_textView);
        }
        else{
            date = (TextView) getListView().getChildAt(index).
                    findViewById(R.id.dateOutBubble_textView);
        }

        if (date.getVisibility() == View.GONE) {
            date.setVisibility(View.VISIBLE);
        }
        else {
            date.setVisibility(View.GONE);
        }

        int shown = getListView().getLastVisiblePosition() -
                getListView().getFirstVisiblePosition();

        if (index == shown) {
            getListView().setSelection(getListView().getAdapter().getCount());
        }

    }

    public void cleanMessageInput(){
        EditText message_editText = (EditText) getView().findViewById(R.id.chat_message_editText);
        message_editText.setText("");
    }

    public void updateMessagesView(){
        ((MessageListAdapter) getListAdapter()).notifyDataSetChanged();
    }
}
