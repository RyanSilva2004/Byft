package com.byft;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class BookingsListAdapter extends ArrayAdapter<Booking> {
    private Context context;
    private List<Booking> bookings;

    public BookingsListAdapter(Context context, List<Booking> bookings) {
        super(context, 0, bookings);
        this.context = context;
        this.bookings = bookings;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.booking_list_item, parent, false);
        }

        Booking booking = bookings.get(position);

        TextView bookingDetails = convertView.findViewById(R.id.booking_details);
        bookingDetails.setText("Booking ID: " + booking.getBookingId() + ", Bus Number: " + booking.getBusNumber() + ", Seat Number: " + booking.getSeatNumber());

        return convertView;
    }
}