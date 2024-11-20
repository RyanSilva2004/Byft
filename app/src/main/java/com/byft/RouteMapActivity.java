package com.byft;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RouteMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "RouteMapActivity";
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private MapView mapView;
    private GoogleMap mMap;
    private TextView routeTextView;
    private Button backButton;

    private HashMap<String, LatLng> highwayTerminals;
    private LatLng startLocation, endLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_map);

        // Initialize UI components
        mapView = findViewById(R.id.mapView);
        routeTextView = findViewById(R.id.route_text_view);
        backButton = findViewById(R.id.back_button);

        // Setup the MapView
        Bundle mapViewBundle = savedInstanceState != null ? savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY) : null;
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        // Setup highway terminals
        setupHighwayTerminals();

        // Fetch route details from intent
        String startPoint = getIntent().getStringExtra("start_location");
        String endPoint = getIntent().getStringExtra("end_location");

        // Validate startPoint and endPoint
        if (startPoint == null || endPoint == null) {
            Toast.makeText(this, "Invalid route data received.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Check if start and end points are the same
        if (startPoint.equals(endPoint)) {
            Toast.makeText(this, "Start and End locations cannot be the same.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Assign start and end locations
        startLocation = highwayTerminals.get(startPoint);
        endLocation = highwayTerminals.get(endPoint);

        if (startLocation == null || endLocation == null) {
            Toast.makeText(this, "Invalid route data received.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set route text
        routeTextView.setText("Route: " + startPoint + " to " + endPoint);

        // Back button functionality
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(7.0, 81.0), 7));

        if (startLocation != null && endLocation != null) {
            fetchRouteFromApi(startLocation, endLocation);
        }
    }

    private void fetchRouteFromApi(LatLng origin, LatLng destination) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&key=AIzaSyAeHUnzfNx6jsdy2n14ZBN996NrgbP6XL4";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(RouteMapActivity.this, "Failed to fetch route.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONArray routes = jsonResponse.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                            String encodedPolyline = overviewPolyline.getString("points");
                            List<LatLng> decodedPath = PolyUtil.decode(encodedPolyline);

                            runOnUiThread(() -> {
                                mMap.clear();
                                mMap.addMarker(new MarkerOptions().position(origin).title("Start"));
                                mMap.addMarker(new MarkerOptions().position(destination).title("End"));
                                mMap.addPolyline(new PolylineOptions().addAll(decodedPath).color(Color.BLUE).width(10));
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(RouteMapActivity.this, "No route found.", Toast.LENGTH_SHORT).show());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing route: " + e.getMessage());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(RouteMapActivity.this, "Error fetching route.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }
}
