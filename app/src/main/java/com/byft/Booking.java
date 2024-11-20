package com.byft;

public class Booking {
    private int bookingId;
    private int scheduleId;
    private String busNumber;
    private int seatNumber;
    private int userId;

    public Booking(int bookingId, int scheduleId, String busNumber, int seatNumber, int userId) {
        this.bookingId = bookingId;
        this.scheduleId = scheduleId;
        this.busNumber = busNumber;
        this.seatNumber = seatNumber;
        this.userId = userId;
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

    public int getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "Booking ID: " + bookingId + ", Bus Number: " + busNumber + ", Seat Number: " + seatNumber;
    }
}