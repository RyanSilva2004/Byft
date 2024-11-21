package com.byft;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatLayoutActivity extends AppCompatActivity {

    private TextView busNumberTextView;
    private TextView busRouteTextView;
    private TextView busTimeTextView;
    private GridLayout seatGrid;
    private Button bookButton;
    private DatabaseHelper databaseHelper;
    private int bookingId;
    private int userId;
    private int scheduleId;
    private List<Integer> bookedSeats;
    private int userBookedSeat = -1;
    private Button selectedSeatButton = null;
    private Map<Button, ColorStateList> originalColors = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_layout);

        busNumberTextView = findViewById(R.id.bus_number);
        busRouteTextView = findViewById(R.id.bus_route);
        busTimeTextView = findViewById(R.id.bus_time);
        seatGrid = findViewById(R.id.seat_grid);
        bookButton = findViewById(R.id.book_button);
        databaseHelper = new DatabaseHelper(this);

        bookingId = getIntent().getIntExtra("bookingId", -1);
        if (bookingId != -1) {
            loadBookingDetails(bookingId);
        } else {
            Toast.makeText(this, "Invalid booking ID", Toast.LENGTH_SHORT).show();
        }

        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSeatButton != null) {
                    int newSeatNumber = Integer.parseInt(selectedSeatButton.getText().toString());
                    updateSeatNumberInDatabase(bookingId, newSeatNumber);
                } else {
                    Toast.makeText(SeatLayoutActivity.this, "Please select a seat", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadBookingDetails(int bookingId) {
        Booking booking = databaseHelper.getBookingById(bookingId);
        if (booking != null) {
            busNumberTextView.setText("Bus Number: " + booking.getBusNumber());
            // Assuming you have methods to get route and time details
            busRouteTextView.setText("Route: " + getRouteDetails(booking.getBusNumber()));
            busTimeTextView.setText("Time: " + getTimeDetails(booking.getBusNumber()));

            userId = booking.getUserId();
            scheduleId = booking.getScheduleId();
            bookedSeats = databaseHelper.getBookedSeatsForSchedule(scheduleId);
            userBookedSeat = booking.getSeatNumber();

            // Load seat layout
            loadSeatLayout(booking.getBusNumber());
        } else {
            Toast.makeText(this, "Booking not found", Toast.LENGTH_SHORT).show();
        }
    }

    private String getRouteDetails(String busNumber) {
        // Placeholder method to get route details
        return "Route details for " + busNumber;
    }

    private String getTimeDetails(String busNumber) {
        // Placeholder method to get time details
        return "Time details for " + busNumber;
    }

    private void loadSeatLayout(String busNumber) {
        int totalSeats = databaseHelper.getBusTotalSeats(busNumber); // Get total seats from database
        int columnCount = 4; // Maximum 4 seats per row
        int rowCount = (int) Math.ceil((double) totalSeats / columnCount);

        seatGrid.setRowCount(rowCount);

        for (int i = 0; i < totalSeats; i++) {
            Button seatButton = new Button(this);
            seatButton.setText(String.valueOf(i + 1));
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(i % columnCount, 1f);
            params.rowSpec = GridLayout.spec(i / columnCount);
            seatButton.setLayoutParams(params);

            // Store the original color of the seat button
            ColorStateList originalColor = seatButton.getBackgroundTintList();
            if (bookedSeats.contains(i + 1) && i + 1 != userBookedSeat) {
                seatButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.darker_gray)));
                seatButton.setEnabled(false);
            } else if (i + 1 == userBookedSeat) {
                seatButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_blue_light)));
            }
            originalColors.put(seatButton, originalColor);

            seatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Reset the background color of the previously selected seat
                    if (selectedSeatButton != null) {
                        selectedSeatButton.setBackgroundTintList(originalColors.get(selectedSeatButton));
                    }

                    // Highlight the newly selected seat
                    seatButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_light)));
                    selectedSeatButton = seatButton;

                    // Show the book button
                    bookButton.setVisibility(View.VISIBLE);
                }
            });
            seatGrid.addView(seatButton);
        }
    }

    private void updateSeatNumberInDatabase(int bookingId, int newSeatNumber) {
        databaseHelper.updateSeatNumber(bookingId, newSeatNumber);
        showSuccessDialog();
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seat Swap Successful");
        builder.setMessage("Your seat has been successfully swapped.");
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            finish(); // Close the activity after updating the seat
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}