package com.byft;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class SwapRequestsActivity extends AppCompatActivity {

    private ListView swapRequestsListView;
    private DatabaseHelper databaseHelper;
    private List<SwapRequest> swapRequestsList;
    private SwapRequestsAdapter swapRequestsAdapter;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swap_requests);

        databaseHelper = new DatabaseHelper(this);

        swapRequestsListView = findViewById(R.id.swap_requests_list_view);

        email = getIntent().getStringExtra("email");
        if (email != null) {
            loadSwapRequests();
        } else {
            Toast.makeText(this, "Email not provided", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if email is null
        }

        swapRequestsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SwapRequest selectedRequest = swapRequestsList.get(position);
                showSwapRequestDialog(selectedRequest);
            }
        });
    }

    private void showSwapRequestDialog(SwapRequest request) {
        int requestorSeatNumber = databaseHelper.getSeatNumberByBookingId(request.getFromBookingId());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seat Swap Request");
        builder.setMessage("Do you want to swap your seat with seat number " + requestorSeatNumber + "?");
        builder.setPositiveButton("Accept", (dialog, which) -> {
            databaseHelper.acceptSwapRequest(request.getRequestId());
            Toast.makeText(this, "Swap request accepted", Toast.LENGTH_SHORT).show();
            loadSwapRequests();
        });
        builder.setNegativeButton("Reject", (dialog, which) -> {
            databaseHelper.rejectSwapRequest(request.getRequestId());
            Toast.makeText(this, "Swap request rejected", Toast.LENGTH_SHORT).show();
            loadSwapRequests();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void loadSwapRequests() {
        swapRequestsList = databaseHelper.getSwapRequestsForUser(email);
        swapRequestsAdapter = new SwapRequestsAdapter(this, swapRequestsList);
        swapRequestsListView.setAdapter(swapRequestsAdapter);
    }
}