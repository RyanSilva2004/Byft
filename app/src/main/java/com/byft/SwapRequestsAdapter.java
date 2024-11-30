package com.byft;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class SwapRequestsAdapter extends BaseAdapter {
    private Context context;
    private List<SwapRequest> swapRequests;

    public SwapRequestsAdapter(Context context, List<SwapRequest> swapRequests) {
        this.context = context;
        this.swapRequests = swapRequests;
    }

    @Override
    public int getCount() {
        return swapRequests.size();
    }

    @Override
    public Object getItem(int position) {
        return swapRequests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return swapRequests.get(position).getRequestId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.swap_request_item, parent, false);
        }

        SwapRequest swapRequest = swapRequests.get(position);

        TextView fromBookingIdTextView = convertView.findViewById(R.id.from_booking_id);
        TextView toBookingIdTextView = convertView.findViewById(R.id.to_booking_id);

        fromBookingIdTextView.setText("From Booking ID: " + swapRequest.getFromBookingId());
        toBookingIdTextView.setText("To Booking ID: " + swapRequest.getToBookingId());

        return convertView;
    }
}