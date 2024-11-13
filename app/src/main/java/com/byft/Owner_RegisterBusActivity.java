package com.byft;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class Owner_RegisterBusActivity extends AppCompatActivity {

    private EditText busNumberEditText;
    private Spinner busSeatsSpinner;
    private Spinner driverSpinner;
    private Spinner routeSpinner;
    private EditText departureIntervalEditText;
    private Button registerBusButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_owner_registerbus);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Initialize views
        busNumberEditText = findViewById(R.id.busNumber);
        busSeatsSpinner = findViewById(R.id.busSeatsSpinner);
        driverSpinner = findViewById(R.id.driverSpinner);
        routeSpinner = findViewById(R.id.routeSpinner);
        departureIntervalEditText = findViewById(R.id.departureInterval);
        registerBusButton = findViewById(R.id.registerBusButton);

        // Load drivers list
        loadDrivers();

        // Set register button click listener
        registerBusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerBus();
            }
        });
    }

    private void loadDrivers() {
        List<String> drivers = databaseHelper.getDrivers();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, drivers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverSpinner.setAdapter(adapter);
    }

    private void registerBus() {
        String busNumber = busNumberEditText.getText().toString().trim();
        String busSeats = busSeatsSpinner.getSelectedItem().toString();
        String driver = driverSpinner.getSelectedItem().toString();
        String route = routeSpinner.getSelectedItem().toString();
        String departureInterval = departureIntervalEditText.getText().toString().trim();

        // Validate input fields
        if (busNumber.isEmpty() || departureInterval.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate bus number format (Sri Lankan vehicle number format)
        if (!busNumber.matches("^[A-Z]{2,3}-\\d{4}$")) {
            Toast.makeText(this, "Invalid bus number format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if bus number already exists in the database
        if (databaseHelper.isBusNumberExists(busNumber)) {
            Toast.makeText(this, "Bus number already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert busSeats and departureInterval to integers
        int busSeatsInt;
        int departureIntervalInt;
        try {
            busSeatsInt = Integer.parseInt(busSeats);
            departureIntervalInt = Integer.parseInt(departureInterval);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert bus into the database
        boolean success = databaseHelper.insertBus(busNumber, busSeatsInt, driver, route, departureIntervalInt);
        if (success) {
            Toast.makeText(this, "Bus registered successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Bus registration failed", Toast.LENGTH_SHORT).show();
        }
    }
}