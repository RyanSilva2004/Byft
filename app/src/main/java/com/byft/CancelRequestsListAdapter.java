package com.byft;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CancelRequestsListAdapter extends ArrayAdapter<CancelRequest> {
    private Context context;
    private List<CancelRequest> cancelRequests;
    private DatabaseHelper databaseHelper;

    public CancelRequestsListAdapter(Context context, List<CancelRequest> cancelRequests) {
        super(context, 0, cancelRequests);
        this.context = context;
        this.cancelRequests = cancelRequests;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cancel_request_list_item, parent, false);
        }

        CancelRequest cancelRequest = cancelRequests.get(position);

        TextView cancelDetails = convertView.findViewById(R.id.cancel_details);
        cancelDetails.setText("Booking ID: " + cancelRequest.getBookingId() + ", Bus Number: " + cancelRequest.getBusNumber() + ", Seat Number: " + cancelRequest.getSeatNumber() + ", State: " + cancelRequest.getState());

        Button acceptButton = convertView.findViewById(R.id.accept_button);
        Button rejectButton = convertView.findViewById(R.id.reject_button);

        acceptButton.setOnClickListener(v -> {
            boolean success = databaseHelper.acceptCancelRequest(cancelRequest.getCancelId(), cancelRequest.getBookingId());
            if (success) {
                cancelRequest.setState("completed");
                notifyDataSetChanged();
                Toast.makeText(context, "Request accepted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to accept request", Toast.LENGTH_SHORT).show();
            }
        });

        rejectButton.setOnClickListener(v -> {
            boolean success = databaseHelper.rejectCancelRequest(cancelRequest.getCancelId());
            if (success) {
                cancelRequest.setState("rejected");
                notifyDataSetChanged();
                Toast.makeText(context, "Request rejected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to reject request", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}