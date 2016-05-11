package pt.ulisboa.tecnico.cmu.ubibike.fragments.chats;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.security.PrivateKey;

import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.R;
import pt.ulisboa.tecnico.cmu.ubibike.adapters.MessageListAdapter;
import pt.ulisboa.tecnico.cmu.ubibike.managers.CipherManager;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.Chat;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.ChatMessage;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.NearbyPeerCommunication;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.tasks.OutgoingCommunicationTask;
import pt.ulisboa.tecnico.cmu.ubibike.utils.DigitalSignature;
import pt.ulisboa.tecnico.cmu.ubibike.utils.JsonParser;

/**
 * Created by ASUS on 11/05/2016.
 */
public class IndividualChatFragment extends ChatFragment {

    private String mUsername;
    private String mDeviceName;

    @Override
    protected void createSpecificChat() {
        mUsername = getArguments().getString(ARGUMENT_KEY_USERNAME);
        mDeviceName = ApplicationContext.getInstance().getNearbyPeerCommunication().getDeviceNearbyByUsername(mUsername).deviceName;
        mChat = ApplicationContext.getInstance().getNearbyPeerCommunication().getIndividualChat(mUsername);

        try{getParentActivity().getSupportActionBar().setTitle(mUsername);}
        catch (Exception e) {}
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

                String msg = NearbyPeerCommunication.buildIndividualChatMessage(myUsername, input);

                mChat.addNewMessage(new ChatMessage(false, myUsername, input));
                updateUI();

                new OutgoingCommunicationTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mDeviceName, msg);
                cleanMessageInput();
            }
        });

        mMessages = mChat.getAllMessages();

        MessageListAdapter messageListAdapter = new MessageListAdapter(getActivity(), mMessages, false);
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

    private void sendPoints(long points) throws Exception {
        String fromClientId = ApplicationContext.getInstance().getData().getUsername();
        PrivateKey privateKey = CipherManager.getPrivateKey(); //TODO

        // ideias do jaquim
        // { from_client_id: 1, to_client_id : 2 , points : 230, points_source: "ride" points_source_id : "ride_client_id", nounce: 3}
        // { uid: 1 , username: "lol" , public_key : "string da chave", ttl : timestamp de validade }

        // TODO: hardcoded next variables
        String toClientId = "";
        String pointsSource = "";
        String pointsSourceId = "";
        int nounce = -1;
        int ttl = -1;

        JSONObject pointsTransactionData = JsonParser.buildPointsTransactionDataJson(
                fromClientId, toClientId, points, pointsSource, pointsSourceId, nounce, ttl);
        //TODO: buildPointsTransactionDataJson

        byte[] signature = DigitalSignature.signData(
                CipherManager.getSHA2Digest(
                        pointsTransactionData.toString().getBytes()), privateKey);

        String base64publicKey = ApplicationContext.getInstance().getData().getPublicToken(); // representing publicKey

        // create final json
        JSONObject pointsTransaction =
                JsonParser.buildPointsTransactionJson(
                        pointsTransactionData, signature, base64publicKey);

        String deviceName = toClientId; // TODO: is the same?

        // send points
        getParentActivity().wifiP2pSendMessageToPeer(deviceName, pointsTransaction.toString());
    }
}
