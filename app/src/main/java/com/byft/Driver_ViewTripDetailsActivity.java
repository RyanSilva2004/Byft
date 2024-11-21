package com.byft;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class Driver_ViewTripDetailsActivity extends AppCompatActivity {

    private ListView tripsListView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_view_trip_details);
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        tripsListView = findViewById(R.id.trips_list);
        databaseHelper = new DatabaseHelper(this);

        loadBusDetails(email);
    }

    private void loadBusDetails(String driverEmail) {
        List<String> busDetails = new ArrayList<>();
        String busNumber = databaseHelper.getBusNumberForDriver(driverEmail);

        if (busNumber != null) {
            List<String> trips = databaseHelper.getBusesForRouteAndDate(busNumber); // Fetch trips for the bus number
            int tripCount = 0;
            for (String trip : trips) {
                if (tripCount >= 7) break; // Limit to next 7 trips
                int scheduleID = databaseHelper.getScheduleID(busNumber, trip);
                int totalSeats = databaseHelper.getTotalSeats(busNumber);
                int bookedSeats = databaseHelper.getBookedSeats(scheduleID).size();
                String route = "Route: " + trip;
                String dateTime = "Date & Time: " + trip;
                String passengers = "Passengers: " + bookedSeats + "/" + totalSeats;
                busDetails.add(route + "\n" + dateTime + "\n" + passengers);
                tripCount++;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, busDetails);
        tripsListView.setAdapter(adapter);
    }
}