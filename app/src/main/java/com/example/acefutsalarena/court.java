package com.example.acefutsalarena;

public class court {

    private String courtId;
    private String courtName;
    private double price;
    private boolean available;

    public court(
            String courtId,
            String courtName,
            double price,
            boolean available
    ) {
        this.courtId = courtId;
        this.courtName = courtName;
        this.price = price;
        this.available = available;
    }

    public String getCourtId() {
        return courtId;
    }

    public String getCourtName() {
        return courtName;
    }

    public double getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}