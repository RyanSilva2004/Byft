package com.byft;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RideHistoryActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private RecyclerView rideHistoryRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history);

        databaseHelper = new DatabaseHelper(this);
        rideHistoryRecyclerView = findViewById(R.id.ride_history_recycler_view);
        rideHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get email from intent
        String email = getIntent().getStringExtra("email");
        if (email != null) {
            int userId = databaseHelper.getUserIdByEmail(email);
            if (userId != -1) {
                List<Booking> rideHistory = databaseHelper.getBookingsByUserId(userId);
                if (rideHistory != null && !rideHistory.isEmpty()) {
                    RideHistoryAdapter adapter = new RideHistoryAdapter(this, rideHistory, email);
                    rideHistoryRecyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(this, "No ride history found for this user.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Email not found.", Toast.LENGTH_SHORT).show();
        }
    }
}