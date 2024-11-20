package com.byft;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.ViewHolder> {
    private Context context;
    private List<Booking> bookings;
    private DatabaseHelper databaseHelper;
    private String userEmail;

    public RideHistoryAdapter(Context context, List<Booking> bookings, String userEmail) {
        this.context = context;
        this.bookings = bookings;
        this.databaseHelper = new DatabaseHelper(context);
        this.userEmail = userEmail;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ride_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.rideDetails.setText(booking.toString());

        // Retrieve and set the previous rating
        float previousRating = databaseHelper.getRating(userEmail, booking.getBusNumber());
        holder.rideRating.setRating(previousRating);

        // Save the rating when it changes
        holder.rideRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    databaseHelper.saveRating(userEmail, booking.getBusNumber(), rating);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView rideDetails;
        public RatingBar rideRating;

        public ViewHolder(View itemView) {
            super(itemView);
            rideDetails = itemView.findViewById(R.id.ride_details);
            rideRating = itemView.findViewById(R.id.ride_rating);
        }
    }
}