package pt.ulisboa.tecnico.cmu.ubibike.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmu.ubibike.ApplicationContext;
import pt.ulisboa.tecnico.cmu.ubibike.R;
import pt.ulisboa.tecnico.cmu.ubibike.UbiBike;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Trajectory;


public class GroupChatArrayAdapter extends ArrayAdapter<String> {

    private final Context context;

    public GroupChatArrayAdapter(Context context, ArrayList<String> values) {
        super(context, -1, values);
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View rowView = inflater.inflate(R.layout.trajectories_list_row, parent, false);

        TextView hosted_by = (TextView) rowView.findViewById(R.id.hosted_by_textView);

        String groupOwner = getItem(position);

        hosted_by.setText(groupOwner);

        return rowView;
    }
}
