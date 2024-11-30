package com.byft;

public class SwapRequest {
    private int requestId;
    private int fromBookingId;
    private int toBookingId;

    public SwapRequest(int requestId, int fromBookingId, int toBookingId) {
        this.requestId = requestId;
        this.fromBookingId = fromBookingId;
        this.toBookingId = toBookingId;
    }

    public int getRequestId() {
        return requestId;
    }

    public int getFromBookingId() {
        return fromBookingId;
    }

    public int getToBookingId() {
        return toBookingId;
    }
}