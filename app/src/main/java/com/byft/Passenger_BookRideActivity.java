package com.byft;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Passenger_BookRideActivity extends AppCompatActivity {

    private Spinner startLocationSpinner, endLocationSpinner, tripDateSpinner;
    private Button searchBusesButton, viewRouteButton;
    private ListView busListView;
    private DatabaseHelper databaseHelper;
    private List<String> busList;
    private ArrayAdapter<String> busListAdapter;
    private String selectedStartLocation, selectedEndLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_booking);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        databaseHelper = new DatabaseHelper(this);

        startLocationSpinner = findViewById(R.id.start_location);
        endLocationSpinner = findViewById(R.id.end_location);
        tripDateSpinner = findViewById(R.id.trip_date);
        searchBusesButton = findViewById(R.id.search_buses_button);
        busListView = findViewById(R.id.bus_list);
        viewRouteButton = findViewById(R.id.view_route_button);
        viewRouteButton.setVisibility(View.GONE);

        ArrayAdapter<CharSequence> startLocationAdapter = ArrayAdapter.createFromResource(this, R.array.sri_lankan_routes, android.R.layout.simple_spinner_item);
        startLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startLocationSpinner.setAdapter(startLocationAdapter);

        ArrayAdapter<CharSequence> endLocationAdapter = ArrayAdapter.createFromResource(this, R.array.sri_lankan_routes, android.R.layout.simple_spinner_item);
        endLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        endLocationSpinner.setAdapter(endLocationAdapter);

        ArrayAdapter<CharSequence> tripDateAdapter = ArrayAdapter.createFromResource(this, R.array.days_of_week, android.R.layout.simple_spinner_item);
        tripDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tripDateSpinner.setAdapter(tripDateAdapter);

        busList = new ArrayList<>();
        busListAdapter = new BusListAdapter(this, busList);
        busListView.setAdapter(busListAdapter);

        searchBusesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBuses();
            }
        });

        viewRouteButton.setOnClickListener(v -> {
            if (selectedStartLocation != null && selectedEndLocation != null) {
                Intent intent1 = new Intent(Passenger_BookRideActivity.this, RouteMapActivity.class);
                intent1.putExtra("start_location", selectedStartLocation);
                intent1.putExtra("end_location", selectedEndLocation);
                startActivity(intent1);
            } else {
                Toast.makeText(Passenger_BookRideActivity.this, "Invalid route details.", Toast.LENGTH_SHORT).show();
            }
        });

        busListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedBus = busList.get(position);
            String tripDate = tripDateSpinner.getSelectedItem().toString();
            Intent intent2 = new Intent(Passenger_BookRideActivity.this, SeatSelectionActivity.class);
            intent2.putExtra("busNumber", selectedBus);
            intent2.putExtra("email", email);
            intent2.putExtra("scheduleID", getScheduleID(selectedBus, tripDate)); // Pass schedule ID
            startActivity(intent2);
        });
    }

    private void searchBuses() {
        selectedStartLocation = startLocationSpinner.getSelectedItem().toString();
        selectedEndLocation = endLocationSpinner.getSelectedItem().toString();
        String tripDate = tripDateSpinner.getSelectedItem().toString();

        // Clear previous results and notify adapter
        busList.clear();
        busListAdapter.notifyDataSetChanged();

        if (selectedStartLocation.equals(selectedEndLocation)) {
            Toast.makeText(this, "Start and end locations cannot be the same.", Toast.LENGTH_SHORT).show();
            viewRouteButton.setVisibility(View.GONE); // Hide the View Route button
            return;
        }

        busList.clear();
        List<String> buses = databaseHelper.getBusesForRouteAndDate(selectedStartLocation, selectedEndLocation, tripDate);
        if (buses != null && !buses.isEmpty()) {
            busList.addAll(buses);
            busListAdapter.notifyDataSetChanged();
            viewRouteButton.setVisibility(View.VISIBLE); // Show the View Route button
        } else {
            Toast.makeText(this, "No buses found for the selected route and date", Toast.LENGTH_SHORT).show();
            viewRouteButton.setVisibility(View.GONE);
        }
    }

    private int getScheduleID(String busNumber, String tripDate) {
        return databaseHelper.getScheduleID(busNumber, tripDate);
    }
}
