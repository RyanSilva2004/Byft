package com.byft;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.List;

public class RouteMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap mMap;
    private TextView routeTextView;
    private Button backButton;
    private HashMap<String, LatLng> highwayTerminals;
    private LatLng startLocation, endLocation;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_map);

        databaseHelper = new DatabaseHelper(this);
        mapView = findViewById(R.id.mapView);
        routeTextView = findViewById(R.id.route_text_view);
        backButton = findViewById(R.id.back_button);

        Bundle mapViewBundle = savedInstanceState != null ? savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY) : null;
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        setupHighwayTerminals();

        // Get start and end locations from intent
        String startPoint = getIntent().getStringExtra("start_location");
        String endPoint = getIntent().getStringExtra("end_location");
        startLocation = highwayTerminals.get(startPoint);
        endLocation = highwayTerminals.get(endPoint);

        // Set route text
        routeTextView.setText("Route: " + startPoint + " to " + endPoint);

        // Set back button click listener
        backButton.setOnClickListener(v -> finish());
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

    private boolean isHighwayRouteSelected() {
        return startLocation != null && endLocation != null;
    }

    private void fetchRoute(LatLng origin, LatLng destination) {
        List<LatLng> routePoints = getHardcodedRoute(origin, destination);
        if (routePoints != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(origin).title("Start"));
            mMap.addMarker(new MarkerOptions().position(destination).title("End"));
            mMap.addPolyline(new PolylineOptions().addAll(routePoints).color(Color.BLUE).width(10));

            // Calculate the bounds of the route
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng point : routePoints) {
                builder.include(point);
            }
            LatLngBounds bounds = builder.build();

            // Move the camera to fit the bounds
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } else {
            Toast.makeText(this, "No route found between selected locations.", Toast.LENGTH_SHORT).show();
        }
    }

    private List<LatLng> getHardcodedRoute(LatLng origin, LatLng destination) {
        // Define hardcoded routes with detailed waypoints
        if (origin.equals(highwayTerminals.get("Colombo Fort")) && destination.equals(highwayTerminals.get("Katunayake"))) {
            return List.of(
                    new LatLng(6.9355, 79.8506), // Colombo Fort
                    new LatLng(6.9563, 79.8788), // Waypoint
                    new LatLng(7.1699, 79.8900)  // Katunayake
            );
        } else if (origin.equals(highwayTerminals.get("Colombo Fort")) && destination.equals(highwayTerminals.get("Galle"))) {
            return List.of(
                    new LatLng(6.9355, 79.8506), // Colombo Fort
                    new LatLng(6.9271, 79.8889), // Kaduwela
                    new LatLng(6.0328, 80.2170)  // Galle
            );
        } else if (origin.equals(highwayTerminals.get("Colombo Fort")) && destination.equals(highwayTerminals.get("Matara"))) {
            return List.of(
                    new LatLng(6.9355, 79.8506), // Colombo Fort
                    new LatLng(6.9271, 79.8889), // Kaduwela
                    new LatLng(6.0328, 80.2170), // Galle
                    new LatLng(5.9549, 80.5550)  // Matara
            );
        } else if (origin.equals(highwayTerminals.get("Colombo Fort")) && destination.equals(highwayTerminals.get("Hambantota"))) {
            return List.of(
                    new LatLng(6.9355, 79.8506), // Colombo Fort
                    new LatLng(6.9271, 79.8889), // Kaduwela
                    new LatLng(6.0328, 80.2170), // Galle
                    new LatLng(6.1242, 81.1185)  // Hambantota
            );
        } else if (origin.equals(highwayTerminals.get("Kadawatha")) && destination.equals(highwayTerminals.get("Galle"))) {
            return List.of(
                    new LatLng(7.0015, 79.9528), // Kadawatha
                    new LatLng(6.0328, 80.2170)  // Galle
            );
        } else if (origin.equals(highwayTerminals.get("Kadawatha")) && destination.equals(highwayTerminals.get("Matara"))) {
            return List.of(
                    new LatLng(7.0015, 79.9528), // Kadawatha
                    new LatLng(6.0328, 80.2170), // Galle
                    new LatLng(5.9549, 80.5550)  // Matara
            );
        } else if (origin.equals(highwayTerminals.get("Kadawatha")) && destination.equals(highwayTerminals.get("Hambantota"))) {
            return List.of(
                    new LatLng(7.0015, 79.9528), // Kadawatha
                    new LatLng(6.0328, 80.2170), // Galle
                    new LatLng(6.1242, 81.1185)  // Hambantota
            );
        } else if (origin.equals(highwayTerminals.get("Galle")) && destination.equals(highwayTerminals.get("Matara"))) {
            return List.of(
                    new LatLng(6.0328, 80.2170), // Galle
                    new LatLng(5.9549, 80.5550)  // Matara
            );
        } else if (origin.equals(highwayTerminals.get("Galle")) && destination.equals(highwayTerminals.get("Hambantota"))) {
            return List.of(
                    new LatLng(6.0328, 80.2170), // Galle
                    new LatLng(6.1242, 81.1185)  // Hambantota
            );
        } else if (origin.equals(highwayTerminals.get("Matara")) && destination.equals(highwayTerminals.get("Hambantota"))) {
            return List.of(
                    new LatLng(5.9549, 80.5550), // Matara
                    new LatLng(6.1242, 81.1185)  // Hambantota
            );
        } else if (origin.equals(highwayTerminals.get("Kaduwela")) && destination.equals(highwayTerminals.get("Kadawatha"))) {
            return List.of(
                    new LatLng(6.9271, 79.8889), // Kaduwela
                    new LatLng(7.0015, 79.9528)  // Kadawatha
            );
        } else if (origin.equals(highwayTerminals.get("Katunayake")) && destination.equals(highwayTerminals.get("Colombo Fort"))) {
            return List.of(
                    new LatLng(7.1699, 79.8900), // Katunayake
                    new LatLng(6.9563, 79.8788), // Waypoint
                    new LatLng(6.9355, 79.8506)  // Colombo Fort
            );
        } else if (origin.equals(highwayTerminals.get("Galle")) && destination.equals(highwayTerminals.get("Colombo Fort"))) {
            return List.of(
                    new LatLng(6.0328, 80.2170), // Galle
                    new LatLng(6.9271, 79.8889), // Kaduwela
                    new LatLng(6.9355, 79.8506)  // Colombo Fort
            );
        } else if (origin.equals(highwayTerminals.get("Matara")) && destination.equals(highwayTerminals.get("Colombo Fort"))) {
            return List.of(
                    new LatLng(5.9549, 80.5550), // Matara
                    new LatLng(6.0328, 80.2170), // Galle
                    new LatLng(6.9271, 79.8889), // Kaduwela
                    new LatLng(6.9355, 79.8506)  // Colombo Fort
            );
        } else if (origin.equals(highwayTerminals.get("Hambantota")) && destination.equals(highwayTerminals.get("Colombo Fort"))) {
            return List.of(
                    new LatLng(6.1242, 81.1185), // Hambantota
                    new LatLng(6.0328, 80.2170), // Galle
                    new LatLng(6.9271, 79.8889), // Kaduwela
                    new LatLng(6.9355, 79.8506)  // Colombo Fort
            );
        } else if (origin.equals(highwayTerminals.get("Galle")) && destination.equals(highwayTerminals.get("Kadawatha"))) {
            return List.of(
                    new LatLng(6.0328, 80.2170), // Galle
                    new LatLng(7.0015, 79.9528)  // Kadawatha
            );
        } else if (origin.equals(highwayTerminals.get("Matara")) && destination.equals(highwayTerminals.get("Kadawatha"))) {
            return List.of(
                    new LatLng(5.9549, 80.5550), // Matara
                    new LatLng(6.0328, 80.2170), // Galle
                    new LatLng(7.0015, 79.9528)  // Kadawatha
            );
        } else if (origin.equals(highwayTerminals.get("Hambantota")) && destination.equals(highwayTerminals.get("Kadawatha"))) {
            return List.of(
                    new LatLng(6.1242, 81.1185), // Hambantota
                    new LatLng(6.0328, 80.2170), // Galle
                    new LatLng(7.0015, 79.9528)  // Kadawatha
            );
        } else if (origin.equals(highwayTerminals.get("Matara")) && destination.equals(highwayTerminals.get("Galle"))) {
            return List.of(
                    new LatLng(5.9549, 80.5550), // Matara
                    new LatLng(6.0328, 80.2170)  // Galle
            );
        } else if (origin.equals(highwayTerminals.get("Hambantota")) && destination.equals(highwayTerminals.get("Galle"))) {
            return List.of(
                    new LatLng(6.1242, 81.1185), // Hambantota
                    new LatLng(6.0328, 80.2170)  // Galle
            );
        } else if (origin.equals(highwayTerminals.get("Hambantota")) && destination.equals(highwayTerminals.get("Matara"))) {
            return List.of(
                    new LatLng(6.1242, 81.1185), // Hambantota
                    new LatLng(5.9549, 80.5550)  // Matara
            );
        } else if (origin.equals(highwayTerminals.get("Kadawatha")) && destination.equals(highwayTerminals.get("Kaduwela"))) {
            return List.of(
                    new LatLng(7.0015, 79.9528), // Kadawatha
                    new LatLng(6.9271, 79.8889)  // Kaduwela
            );
        }
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(7.0, 81.0), 7));

        // Fetch and display the route when the map is ready
        if (isHighwayRouteSelected()) {
            fetchRoute(startLocation, endLocation);
        }

        // Set marker click listener to show bus departure time
        mMap.setOnMarkerClickListener(marker -> {
            String departureTime = marker.getSnippet();
            Toast.makeText(RouteMapActivity.this, "Departure Time: " + departureTime, Toast.LENGTH_SHORT).show();
            return false;
        });
    }
}