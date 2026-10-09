package com.example.acefutsalarena;

public class Admin_Booking {

    private String bookingId;
    private String customerName;
    private String customerEmail;
    private String courtName;
    private String date;
    private String time;
    private String status;
    private double totalPrice;

    public Admin_Booking(
            String bookingId,
            String customerName,
            String customerEmail,
            String courtName,
            String date,
            String time,
            String status,
            double totalPrice
    ) {

        this.bookingId = bookingId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.courtName = courtName;
        this.date = date;
        this.time = time;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getCourtName() {
        return courtName;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    // Update booking status
    public void setStatus(String status) {
        this.status = status;
    }
}