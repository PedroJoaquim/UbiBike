package pt.ulisboa.tecnico.cmu.ubibike.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pt.ulisboa.tecnico.cmu.ubibike.R;
import pt.ulisboa.tecnico.cmu.ubibike.peercommunication.ChatMessage;


public class MessageListAdapter extends BaseAdapter{

    private final Context context;

    private List<ChatMessage> messages;
    private boolean groupChat;

    public MessageListAdapter(Context c, List<ChatMessage> msgs, boolean grpChat) {
        context = c;
        messages = msgs;
        groupChat = grpChat;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    public boolean isEmpty(){
        return messages.size() == 0;
    }

    @Override
    public ChatMessage getItem(int position) {
        return messages.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View vi = inflater.inflate(R.layout.list_row_message, parent, false);

        LinearLayout inBubble = (LinearLayout) vi.findViewById(R.id.inMessageBubble);
        LinearLayout outBubble = (LinearLayout) vi.findViewById(R.id.outMessageBubble);
        TextView inMessage_textView = (TextView) vi.findViewById(R.id.inMessage_textView);
        TextView outMessage_textView = (TextView) vi.findViewById(R.id.outMessage_textView);
        TextView dateInBubble_textView = (TextView) vi.findViewById(R.id.dateInBubble_textView);
        TextView dateOutBubble_textView = (TextView) vi.findViewById(R.id.dateOutBubble_textView);
        TextView inMessage_sender_textView = (TextView) vi.findViewById(R.id.msg_sender_textView);

        ChatMessage msg = messages.get(position);

        String date = msg.getDay();
        String currentDay = new SimpleDateFormat("dd-MM-yyyy").format(new Date());


        //only show sender name on incoming messages in group chat
        if(msg.isReceived() && groupChat) {
            inMessage_sender_textView.setText(msg.getSenderUsername());
        }
        else{
            inMessage_sender_textView.setVisibility(View.GONE);
        }

        if(date.equals(currentDay))   //msg sent today
            date = msg.getHour();

        else
            date = msg.getHour() + "  " + msg.getDate();

        if(msg.isReceived()){
            outBubble.setVisibility(View.GONE);
            dateOutBubble_textView.setVisibility(View.GONE);
            dateInBubble_textView.setVisibility(View.GONE);

            inBubble.setVisibility(View.VISIBLE);

            inMessage_textView.setText(msg.getContent());
            dateInBubble_textView.setText(date);
        }
        else{
            inBubble.setVisibility(View.GONE);
            dateOutBubble_textView.setVisibility(View.GONE);
            dateInBubble_textView.setVisibility(View.GONE);

            outBubble.setVisibility(View.VISIBLE);

            outMessage_textView.setText(msg.getContent());
            dateOutBubble_textView.setText(date);
        }

        return vi;
    }
}


