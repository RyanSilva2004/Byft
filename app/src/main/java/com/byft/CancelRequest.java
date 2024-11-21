package com.byft;

public class CancelRequest {
    private int cancelId;
    private int bookingId;
    private String busNumber;
    private int seatNumber;
    private String state;
    private String userEmail;

    public CancelRequest(int cancelId, int bookingId, String busNumber, int seatNumber, String state, String userEmail) {
        this.cancelId = cancelId;
        this.bookingId = bookingId;
        this.busNumber = busNumber;
        this.seatNumber = seatNumber;
        this.state = state;
        this.userEmail = userEmail;
    }

    public int getCancelId() {
        return cancelId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public String getState() {
        return state;
    }

    public String getUserEmail() {
        return userEmail;
    }
    public void setState(String state) {
        this.state = state;
    }
}