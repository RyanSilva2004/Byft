package com.byft;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast; // Add this import statement

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class CancelBookingActivity extends AppCompatActivity {

    private ListView bookingsListView;
    private DatabaseHelper databaseHelper;
    private List<Booking> bookingsList;
    private CancelBookingsListAdapter bookingsListAdapter;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_booking);

        databaseHelper = new DatabaseHelper(this);

        bookingsListView = findViewById(R.id.bookings_list_view);

        bookingsList = new ArrayList<>();
        bookingsListAdapter = new CancelBookingsListAdapter(this, bookingsList);
        bookingsListView.setAdapter(bookingsListAdapter);

        // Get email from intent
        String email = getIntent().getStringExtra("email");
        if (email != null) {
            userId = databaseHelper.getUserIdByEmail(email);
            if (userId != -1) {
                loadBookings();
            } else {
                Toast.makeText(this, "Invalid user email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Email not provided", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadBookings() {
        bookingsList.clear();
        List<Booking> bookings = databaseHelper.getBookingsForUser(userId);
        if (bookings != null && !bookings.isEmpty()) {
            bookingsList.addAll(bookings);
            bookingsListAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "No bookings found", Toast.LENGTH_SHORT).show();
        }
    }
}