package com.byft;

public class Booking {
    private int bookingId;
    private int scheduleId;
    private String busNumber;
    private int seatNumber;
    private String userId;
    private String route; // New attribute

    public Booking(int bookingId, int scheduleId, String busNumber, int seatNumber, String userId, String route) {
        this.bookingId = bookingId;
        this.scheduleId = scheduleId;
        this.busNumber = busNumber;
        this.seatNumber = seatNumber;
        this.userId = userId;
        this.route = route; // Initialize new attribute
    }

    public int getBookingId() {
        return bookingId;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public String getUserId() {
        return userId;
    }

    public String getRoute() {
        return route; // Getter for new attribute
    }

    @Override
    public String toString() {
        return "Booking ID: " + bookingId + ", Bus Number: " + busNumber + ", Seat Number: " + seatNumber + ", Route: " + route;
    }
}