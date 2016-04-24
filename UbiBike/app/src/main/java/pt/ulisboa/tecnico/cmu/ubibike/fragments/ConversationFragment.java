//package pt.ulisboa.tecnico.cmu.ubibike.fragments;
//
//
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import pt.ulisboa.tecnico.cmu.ubibike.R;
//import pt.ulisboa.tecnico.cmu.ubibike.adapters.MessageListAdapter;
//import pt.ulisboa.tecnico.cmu.ubibike.domain.Chat;
//
//
//public class ConversationFragment extends Fragment {
//
//
//    public ConversationFragment() {
//        // Required empty public constructor
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
//
//        String clientID = getArguments().getString("clientID");
//
//
//        //setViewElements(view);
//
//        return view;
//    }
//
//    private Chat chat;
//
//    private ListView conversation_listView;
//    private MessageListAdapter messageListAdapter;
//
//
//    private void setViewElements(View view, Chat chat) {
//
//        conversation_listView = (ListView) view.findViewById(R.id.conversation_listView);
//        ImageButton send_button = (ImageButton) view.findViewById(R.id.chat_send_button);
//        final EditText message_editText = (EditText) view.findViewById(R.id.chat_message_editText);
//
//        send_button.setClickable(false);
//        send_button.setEnabled(false);
//
//        conversation_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> myAdapter, View myView, int pos, long mylng) {
//                TextView date;
//
//                //getting right index to access the element. Because elements that are not totally visible are deleted from viewGroup
//                int index = pos - conversation_listView.getFirstVisiblePosition();
//
//                //in case if element is not totally visible, android doesn't have it in memory (is not in viewGroup)
//                if (index > conversation_listView.getLastVisiblePosition())
//                    return;
//
//                if (messageListAdapter.getMessages().get(pos).isReceived())
//                    date = (TextView) conversation_listView.getChildAt(index).findViewById(R.id.dateInBubble_textView);
//                else
//                    date = (TextView) conversation_listView.getChildAt(index).findViewById(R.id.dateOutBubble_textView);
//
//                if (date.getVisibility() == View.GONE)
//                    date.setVisibility(View.VISIBLE);
//                else
//                    date.setVisibility(View.GONE);
//
//                int shown = conversation_listView.getLastVisiblePosition() - conversation_listView.getFirstVisiblePosition();
//
//                if (index == shown)
//                    conversation_listView.setSelection(conversation_listView.getAdapter().getCount());
//            }
//        });
//
//        messageListAdapter = new MessageListAdapter(getActivity(), chat.getAllMessages());
//        conversation_listView.setAdapter(messageListAdapter);
//
//        send_button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                String messageContent = message_editText.getText().toString(); //TODO char lim
//
//                //TODO action to send message
//            }
//        });
//
//    }
//
//    public void cleanMessageInput(){
//        EditText message_editText = (EditText) getView().findViewById(R.id.chat_message_editText);
//        message_editText.setText("");
//    }
//
//    public void updateMessagesView(){
//        ((MessageListAdapter) conversation_listView.getAdapter()).notifyDataSetChanged();
//    }
//}
