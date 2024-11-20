package com.byft;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import java.util.List;

public class BusListAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> buses;
    private DatabaseHelper databaseHelper;

    public BusListAdapter(Context context, List<String> buses) {
        super(context, 0, buses);
        this.context = context;
        this.buses = buses;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.bus_list_item, parent, false);
        }

        String busNumber = buses.get(position);

        TextView busDetails = convertView.findViewById(R.id.bus_details);
        RatingBar busRating = convertView.findViewById(R.id.bus_rating);

        busDetails.setText(busNumber);

        // Retrieve and set the average rating
        float averageRating = databaseHelper.getAverageRating(busNumber);
        busRating.setRating(averageRating);

        return convertView;
    }
}