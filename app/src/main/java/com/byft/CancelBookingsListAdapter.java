package com.byft;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast; // Add this import statement
import java.util.List;

public class CancelBookingsListAdapter extends ArrayAdapter<Booking> {
    private Context context;
    private List<Booking> bookings;
    private DatabaseHelper databaseHelper;

    public CancelBookingsListAdapter(Context context, List<Booking> bookings) {
        super(context, 0, bookings);
        this.context = context;
        this.bookings = bookings;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cancel_booking_list_item, parent, false);
        }

        Booking booking = bookings.get(position);

        TextView bookingDetails = convertView.findViewById(R.id.booking_details);
        Button cancelButton = convertView.findViewById(R.id.cancel_button);

        bookingDetails.setText("Booking ID: " + booking.getBookingId() + ", Bus Number: " + booking.getBusNumber() + ", Seat Number: " + booking.getSeatNumber());

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the booking details in the CancelBookingsTable with state "pending"
                boolean isInserted = databaseHelper.insertCancelBooking(
                        booking.getBookingId(),
                        booking.getBusNumber(),
                        booking.getSeatNumber(),
                        booking.getUserId()
                );

                if (isInserted) {
                    bookings.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Booking cancellation requested", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to request booking cancellation", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }
}