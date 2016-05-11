package pt.ulisboa.tecnico.cmu.ubibike.fragments.chats;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.security.PrivateKey;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.R;
import pt.ulisboa.tecnico.cmu.ubibike.UbiBike;
import pt.ulisboa.tecnico.cmu.ubibike.adapters.MessageListAdapter;
import pt.ulisboa.tecnico.cmu.ubibike.fragments.UpdatableUI;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.Chat;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.ChatMessage;


public abstract class ChatFragment extends Fragment implements UpdatableUI {

    public static final String ARGUMENT_KEY_USERNAME = "username";

    protected ListView mListView;
    protected Chat mChat;
    protected List<ChatMessage> mMessages;

    public ChatFragment() {
        // Required empty public constructor
    }

    protected UbiBike getParentActivity(){
        return (UbiBike) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createSpecificChat();
    }

    protected abstract void createSpecificChat();

    @Override
    public void onResume() {
        super.onResume();

        ApplicationContext.getInstance().setCurrentFragment(this);
    }

    @Override
    public void onPause(){
        super.onPause();

        ApplicationContext.getInstance().setCurrentFragment(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        setViewElements(view);

        return view;
    }


    protected abstract void setViewElements(View view);

    public void cleanMessageInput(){
        EditText message_editText = (EditText) getView().findViewById(R.id.chat_message_editText);
        message_editText.setText("");
    }

    @Override
    public void updateUI() {
        ((MessageListAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }
}
