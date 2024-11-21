package com.byft;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class CancelRequestsActivity extends AppCompatActivity {

    private ListView cancelRequestsListView;
    private DatabaseHelper databaseHelper;
    private List<CancelRequest> cancelRequestsList;
    private CancelRequestsListAdapter cancelRequestsListAdapter;
    private String driverEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_requests);

        Intent intent = getIntent();
        driverEmail = intent.getStringExtra("email");

        databaseHelper = new DatabaseHelper(this);

        cancelRequestsListView = findViewById(R.id.cancel_requests_list_view);

        cancelRequestsList = new ArrayList<>();
        cancelRequestsListAdapter = new CancelRequestsListAdapter(this, cancelRequestsList);
        cancelRequestsListView.setAdapter(cancelRequestsListAdapter);

        loadCancelRequests(driverEmail);
    }

    private void loadCancelRequests(String driverEmail) {
        cancelRequestsList.clear();
        List<CancelRequest> cancelRequests = databaseHelper.getCancelRequestsForDriver(driverEmail);
        if (cancelRequests != null && !cancelRequests.isEmpty()) {
            cancelRequestsList.addAll(cancelRequests);
            cancelRequestsListAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "No cancel requests found for the driver", Toast.LENGTH_SHORT).show();
        }
    }
}