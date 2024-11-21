package com.byft;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "HomeActivity";

    private DatabaseHelper databaseHelper;
    private MapView mapView;
    private GoogleMap mMap;
    private Spinner terminalSpinner;
    private HashMap<String, LatLng> highwayTerminals;

    private TextView textUsername;
    private ImageView profileImage;

    // UI Components for different roles
    private LinearLayout bookRideButton, rideHistoryButton, supportButton;
    private LinearLayout registerBusButton, manageBusButton;
    private LinearLayout viewBusDetailsButton, tripsButton;
    private LinearLayout requestCancelBookingButton, requestSwapSeatButton;
    private LinearLayout CheckCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Retrieve Intent data
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");

        // Setup profile header
        textUsername = findViewById(R.id.user_name);
        profileImage = findViewById(R.id.profile_image);

        String name = databaseHelper.getUserName(email, password);
        textUsername.setText("Hello, " + name);

        byte[] profileImageData = databaseHelper.getUserProfileImage(email, password);
        if (profileImageData != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(profileImageData, 0, profileImageData.length);
            profileImage.setImageBitmap(bitmap);
        }

        // Initialize UI components
        mapView = findViewById(R.id.map_view);
        terminalSpinner = findViewById(R.id.terminal_spinner);

        // Initialize buttons
        bookRideButton = findViewById(R.id.book_ride_button);
        rideHistoryButton = findViewById(R.id.ride_history_button);
        supportButton = findViewById(R.id.support_button);
        registerBusButton = findViewById(R.id.register_bus_button);
        manageBusButton = findViewById(R.id.view_buses_button);
        viewBusDetailsButton = findViewById(R.id.view_bus_details_button);
        tripsButton = findViewById(R.id.trips_button);
        requestCancelBookingButton = findViewById(R.id.cancel_bookings_button);
        requestSwapSeatButton = findViewById(R.id.swap_seats_button);
        CheckCancel = findViewById(R.id.check_cancel_button);


        setupHighwayTerminals();
        setupSpinner();

        // Initialize map
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Determine user role and adjust visibility
        String role = databaseHelper.getUserRole(email, password);
        adjustVisibilityBasedOnRole(role);

        // Set listeners for buttons
        setupButtonListeners(email, password);
    }

    private void setupHighwayTerminals() {
        highwayTerminals = new HashMap<>();
        highwayTerminals.put("Colombo Fort", new LatLng(6.9355, 79.8506));
        highwayTerminals.put("Katunayake", new LatLng(7.1699, 79.8900));
        highwayTerminals.put("Kadawatha", new LatLng(7.0015, 79.9528));
        highwayTerminals.put("Galle", new LatLng(6.0328, 80.2170));
        highwayTerminals.put("Matara", new LatLng(5.9549, 80.5550));
        highwayTerminals.put("Hambantota", new LatLng(6.1242, 81.1185));
        highwayTerminals.put("Kaduwela", new LatLng(6.9271, 79.8889));
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, highwayTerminals.keySet().toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        terminalSpinner.setAdapter(adapter);

        terminalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTerminal = (String) parent.getItemAtPosition(position);
                displayBusesAtStation(selectedTerminal);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default to "Colombo Fort" if nothing is selected
                String defaultTerminal = "Colombo Fort";
                terminalSpinner.setSelection(getIndexForTerminal(defaultTerminal));
                displayBusesAtStation(defaultTerminal);
            }
        });
    }

    private int getIndexForTerminal(String terminal) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) terminalSpinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(terminal)) {
                return i;
            }
        }
        return 0;
    }

    private void displayBusesAtStation(String terminal) {
        try {
            // Clear existing markers on the map
            mMap.clear();
            String today = new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date());
            Cursor cursor = databaseHelper.getAvailableBuses(terminal, today);

            // Define radius and angle variables for marker placement
            double radius = 0.0001; // Increased radius for better separation
            double angle = 0;

            if (cursor != null && cursor.moveToFirst()) {
                int busCount = cursor.getCount();
                double angleIncrement = (2 * Math.PI) / busCount;

                // Retrieve the terminal's central location
                LatLng terminalLocation = highwayTerminals.get(terminal);

                if (terminalLocation == null) {
                    Toast.makeText(this, "Terminal location not found on the map.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Loop through the buses and place them on the map
                do {
                    String busNumber = cursor.getString(cursor.getColumnIndexOrThrow("busNumber"));
                    String tripTime = cursor.getString(cursor.getColumnIndexOrThrow("tripTime"));
                    String endLocation = cursor.getString(cursor.getColumnIndexOrThrow("endLocation"));

                    // Calculate offset for clear placement around the terminal
                    double offsetLatitude = terminalLocation.latitude + radius * Math.sin(angle);
                    double offsetLongitude = terminalLocation.longitude + radius * Math.cos(angle);
                    angle += angleIncrement;

                    // Add a marker for each bus
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(offsetLatitude, offsetLongitude))
                            .title("Bus: " + busNumber)
                            .snippet("Departure: " + tripTime + " | Destination: " + endLocation)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_icon))); // Use custom icon

                    // Store bus details as a tag for easy retrieval
                    marker.setTag(new String[]{busNumber, terminal, endLocation});
                } while (cursor.moveToNext());

                cursor.close(); // Close cursor after use
            } else {
                // Show a message if no buses are found
                Toast.makeText(this, "No buses available at the selected terminal: " + terminal, Toast.LENGTH_SHORT).show();
            }

            // Focus the map on the selected terminal
            LatLng terminalLocation = highwayTerminals.get(terminal);
            if (terminalLocation != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(terminalLocation, 16)); // Adjust zoom level as needed
            }

            // Add marker click listener
            mMap.setOnMarkerClickListener(marker -> {
                // Display bus details in a Toast or dialog
                String[] busDetails = (String[]) marker.getTag();
                if (busDetails != null) {
                    Toast.makeText(this, "Bus: " + busDetails[0] + "\nTerminal: " + busDetails[1] + "\nDestination: " + busDetails[2], Toast.LENGTH_LONG).show();
                }
                return false; // Return false to allow info window to show
            });

            // Add info window click listener
            mMap.setOnInfoWindowClickListener(marker -> {
                String[] busDetails = (String[]) marker.getTag();
                if (busDetails != null) {
                    // Transition to RouteMapActivity with bus details
                    Intent intent = new Intent(this, RouteMapActivity.class);
                    intent.putExtra("start_location", busDetails[1]); // Terminal
                    intent.putExtra("end_location", busDetails[2]); // Destination
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            // Handle any errors and log them
            Log.e("RouteMapActivity", "Error displaying buses: " + e.getMessage());
            Toast.makeText(this, "Error displaying buses. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }





    private void adjustVisibilityBasedOnRole(String role) {
        // Hide all buttons initially
        bookRideButton.setVisibility(View.GONE);
        rideHistoryButton.setVisibility(View.GONE);
        supportButton.setVisibility(View.GONE);
        requestCancelBookingButton.setVisibility(View.GONE);
        requestSwapSeatButton.setVisibility(View.GONE);
        registerBusButton.setVisibility(View.GONE);
        manageBusButton.setVisibility(View.GONE);
        viewBusDetailsButton.setVisibility(View.GONE);
        tripsButton.setVisibility(View.GONE);
        CheckCancel.setVisibility(View.GONE);


        // Adjust visibility based on role
        if ("PASSENGER".equalsIgnoreCase(role)) {
            bookRideButton.setVisibility(View.VISIBLE);
            rideHistoryButton.setVisibility(View.VISIBLE);
            supportButton.setVisibility(View.VISIBLE);
            requestCancelBookingButton.setVisibility(View.VISIBLE);
            requestSwapSeatButton.setVisibility(View.VISIBLE);
        } else if ("BUS OWNER".equalsIgnoreCase(role)) {
            registerBusButton.setVisibility(View.VISIBLE);
            manageBusButton.setVisibility(View.VISIBLE);
            supportButton.setVisibility(View.VISIBLE);

        } else if ("BUS DRIVER".equalsIgnoreCase(role)) {
            viewBusDetailsButton.setVisibility(View.VISIBLE);
            tripsButton.setVisibility(View.VISIBLE);
            supportButton.setVisibility(View.VISIBLE);
            CheckCancel.setVisibility(View.VISIBLE);

        }
    }

    private void setupButtonListeners(String email, String password) {
        bookRideButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, Passenger_BookRideActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        registerBusButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, Owner_RegisterBusActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        rideHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, RideHistoryActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        viewBusDetailsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, Driver_ViewBusDetailsActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        tripsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, Driver_ViewTripDetailsActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        requestSwapSeatButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SeatSwapActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        requestCancelBookingButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CancelBookingActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        CheckCancel.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CancelRequestsActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        displayBusesAtStation("Colombo Fort");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
