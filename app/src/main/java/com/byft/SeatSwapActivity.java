package com.byft;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SeatSwapActivity extends AppCompatActivity {

    private ListView bookingsListView;
    private DatabaseHelper databaseHelper;
    private List<Booking> bookingsList;
    private BookingsListAdapter bookingsListAdapter;
    private Button viewSwapRequestsButton;
    String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_swap);

        databaseHelper = new DatabaseHelper(this);

        bookingsListView = findViewById(R.id.bookings_list_view);
        viewSwapRequestsButton = findViewById(R.id.view_swap_requests_button);

        bookingsList = new ArrayList<>();
        bookingsListAdapter = new BookingsListAdapter(this, bookingsList);
        bookingsListView.setAdapter(bookingsListAdapter);

        // Get email from intent
         email = getIntent().getStringExtra("email");
        if (email != null) {


                loadBookings();

        } else {
            Toast.makeText(this, "Email not provided", Toast.LENGTH_SHORT).show();
        }

        bookingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Booking selectedBooking = bookingsList.get(position);
                Intent intent = new Intent(SeatSwapActivity.this, SeatLayoutActivity.class);
                intent.putExtra("bookingId", selectedBooking.getBookingId());
                startActivity(intent);
            }
        });

        viewSwapRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeatSwapActivity.this, SwapRequestsActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
    }

    private void loadBookings() {
        bookingsList.clear();
        List<Booking> bookings = databaseHelper.getUserBookings(email);
        if (bookings != null && !bookings.isEmpty()) {
            bookingsList.addAll(bookings);
            bookingsListAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "No bookings found", Toast.LENGTH_SHORT).show();
        }
    }
}