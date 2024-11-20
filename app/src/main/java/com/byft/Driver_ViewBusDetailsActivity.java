package com.byft;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class Driver_ViewBusDetailsActivity extends AppCompatActivity {

    private ListView tripsListView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_view_bus_details);

        tripsListView = findViewById(R.id.trips_list);
        databaseHelper = new DatabaseHelper(this);

        loadBusDetails();
    }

    private void loadBusDetails() {
        List<String> busDetails = new ArrayList<>();
        List<String> busNumbers = databaseHelper.getDrivers(); // Assuming driver is logged in and we have their ID

        for (String busNumber : busNumbers) {
            List<String> trips = databaseHelper.getBusesForRouteAndDate(busNumber); // Fetch trips for the bus number
            for (String trip : trips) {
                int scheduleID = databaseHelper.getScheduleID(busNumber, trip);
                int totalSeats = databaseHelper.getTotalSeats(busNumber);
                int bookedSeats = databaseHelper.getBookedSeats(scheduleID).size();
                String route = "Route: " + trip;
                String dateTime = "Date & Time: " + trip;
                String passengers = "Passengers: " + bookedSeats + "/" + totalSeats;
                busDetails.add(route + "\n" + dateTime + "\n" + passengers);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, busDetails);
        tripsListView.setAdapter(adapter);
    }
}