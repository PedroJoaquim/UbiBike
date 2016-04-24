//package pt.ulisboa.tecnico.cmu.ubibike.adapters;
//
//import android.app.Activity;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
//import pt.ulisboa.tecnico.cmu.ubibike.R;
//import pt.ulisboa.tecnico.cmu.ubibike.domain.ChatMessage;
//
//
///**
// * Created by andriy on 12.03.2016.
// */
//public class MessageListAdapter extends BaseAdapter{
//    private Activity activity;
//
//    private List<ChatMessage> messages;
//    private static LayoutInflater inflater;
//
//    private View vi;
//
//    public MessageListAdapter(Activity a, List<ChatMessage> d) {
//        activity = a;
//        messages = d;
//        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }
//
//    public List<ChatMessage> getMessages() {
//        return messages;
//    }
//
//    @Override
//    public int getCount() {
//        return messages.size();
//    }
//
//    public boolean isEmpty(){
//        return messages.size() == 0;
//    }
//
//    @Override
//    public ChatMessage getItem(int position) {
//        return messages.get(position);
//    }
//
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        vi = convertView;
//        ChatMessage msg = messages.get(position);
//
//
//        if(convertView==null)
//            vi = inflater.inflate(R.layout.list_row_message, null);
//
//        LinearLayout inBubble = (LinearLayout) vi.findViewById(R.id.inMessageBubble);
//        LinearLayout outBubble = (LinearLayout) vi.findViewById(R.id.outMessageBubble);
//        TextView inMessage_textView = (TextView) vi.findViewById(R.id.inMessage_textView);
//        TextView outMessage_textView = (TextView) vi.findViewById(R.id.outMessage_textView);
//        TextView dateInBubble_textView = (TextView) vi.findViewById(R.id.dateInBubble_textView);
//        TextView dateOutBubble_textView = (TextView) vi.findViewById(R.id.dateOutBubble_textView);
//
//
//        String date = msg.getDay();
//        String currentDay = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
//
//        if(date.equals(currentDay))   //msg sent today
//            date = msg.getHour();
//
//        else
//            date = msg.getHour() + "  " + msg.getDate();
//
//        if(msg.isReceived()){
//            outBubble.setVisibility(View.GONE);
//            dateOutBubble_textView.setVisibility(View.GONE);
//            dateInBubble_textView.setVisibility(View.GONE);
//
//            inBubble.setVisibility(View.VISIBLE);
//
//            inMessage_textView.setText(msg.getContent());
//            dateInBubble_textView.setText(date);
//        }
//        else{
//            inBubble.setVisibility(View.GONE);
//            dateOutBubble_textView.setVisibility(View.GONE);
//            dateInBubble_textView.setVisibility(View.GONE);
//
//            outBubble.setVisibility(View.VISIBLE);
//
//            outMessage_textView.setText(msg.getContent());
//            dateOutBubble_textView.setText(date);
//        }
//
//        return vi;
//    }
//}
//
//
