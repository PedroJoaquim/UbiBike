package pt.ulisboa.tecnico.cmu.ubibike.fragments.chats;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.security.PrivateKey;

import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.R;
import pt.ulisboa.tecnico.cmu.ubibike.adapters.MessageListAdapter;
import pt.ulisboa.tecnico.cmu.ubibike.managers.CipherManager;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.ChatMessage;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.NearbyPeerCommunication;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.tasks.OutgoingCommunicationTask;
import pt.ulisboa.tecnico.cmu.ubibike.utils.AndroidUtil;
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
        ImageButton send_message_button = (ImageButton) view.findViewById(R.id.chat_send_button);
        final EditText message_editText = (EditText) view.findViewById(R.id.chat_message_editText);
        ImageButton send_points_button = (ImageButton) view.findViewById(R.id.send_points_button);

        send_message_button.setOnClickListener(new View.OnClickListener() {
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


        send_points_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                long myPoints = ApplicationContext.getInstance().getData().getTotalPoints();

                final AlertDialog.Builder inputAlert = new AlertDialog.Builder(getActivity());
                inputAlert.setTitle("Points Exchange");
                inputAlert.setMessage("Available points:" + myPoints);

                final EditText userInput = new EditText(getActivity());
                userInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                userInput.setRawInputType(Configuration.KEYBOARD_12KEY);
                userInput.setHint("Points to transfer");

                FrameLayout container = new FrameLayout(getActivity());
                FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int margin = (int) AndroidUtil.convertDpToPixel(15);
                params.leftMargin = margin;
                params.rightMargin = margin;
                userInput.setLayoutParams(params);
                container.addView(userInput);

                inputAlert.setView(container);

                inputAlert.setPositiveButton("Send", null);
                inputAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


                AlertDialog alertDialog = inputAlert.create();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                String userInputValue = userInput.getText().toString();
                                
                                try {
                                    int points = Integer.parseInt(userInputValue);

                                    Toast.makeText(getActivity(), "" + points, Toast.LENGTH_SHORT).show();

                                    //TODO send points here
                                }
                                catch(Exception e){
                                    Toast.makeText(getActivity(), "Invalid Number", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                });
                alertDialog.show();
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

    @Override
    public void updateUI(){
        super.updateUI();

        if(!ApplicationContext.getInstance().getNearbyPeerCommunication().doesGroupMemberExistByUsername(mUsername)){
            Toast.makeText(getActivity(), "User out of range. Chat unavailable.", Toast.LENGTH_SHORT).show();
            getParentActivity().onBackPressed();
        }
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
