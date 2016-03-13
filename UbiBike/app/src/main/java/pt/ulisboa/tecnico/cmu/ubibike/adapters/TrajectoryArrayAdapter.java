package pt.ulisboa.tecnico.cmu.ubibike.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmu.ubibike.R;
import pt.ulisboa.tecnico.cmu.ubibike.UbiBike;
import pt.ulisboa.tecnico.cmu.ubibike.domain.Trajectory;


public class TrajectoryArrayAdapter extends ArrayAdapter<Trajectory> {

    private final Context context;

    public TrajectoryArrayAdapter(Context context, ArrayList<Trajectory> values) {
        super(context, -1, values);
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View rowView = inflater.inflate(R.layout.trajectories_list_row, parent, false);

        TextView from = (TextView) rowView.findViewById(R.id.from_station_name_textView);
        TextView to = (TextView) rowView.findViewById(R.id.to_station_name_textView);
        TextView distance = (TextView) rowView.findViewById(R.id.distance_textView);
        TextView points = (TextView) rowView.findViewById(R.id.points_textView);
        TextView time = (TextView) rowView.findViewById(R.id.time_textView);
        TextView timeAgo = (TextView) rowView.findViewById(R.id.time_ago_textView);

        Trajectory trajectory = getItem(position);

        from.setText(trajectory.getStartStationName());
        to.setText(trajectory.getEndStationName());
        distance.setText(String.format("%.3f km", trajectory.getTravelledDistanceInKm()));
        points.setText(String.valueOf(trajectory.getPointsEarned()));
        time.setText(trajectory.getReadableTravelTime());
        timeAgo.setText(trajectory.getReadableFinishTime());


        return rowView;
    }
}
