package com.byft;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Driver_ViewBusDetailsActivity extends AppCompatActivity {

    private TextView busNumberTextView;
    private TextView numberOfSeatsTextView;
    private TextView otherDetailsTextView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_view_bus_details);

        busNumberTextView = findViewById(R.id.bus_number);
        numberOfSeatsTextView = findViewById(R.id.number_of_seats);
        otherDetailsTextView = findViewById(R.id.other_details);
        databaseHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        loadBusDetails(email);
    }

    private void loadBusDetails(String driverEmail) {
        String busNumber = databaseHelper.getBusNumberForDriver(driverEmail);
        if (busNumber != null) {
            int totalSeats = databaseHelper.getTotalSeats(busNumber);
            String otherDetails = databaseHelper.getOtherBusDetails(busNumber);

            busNumberTextView.setText(busNumber);
            numberOfSeatsTextView.setText(String.valueOf(totalSeats));
            otherDetailsTextView.setText(otherDetails);
        }
    }
}