package com.parkit.parkingsystem.model;

import java.time.LocalDateTime;

public class Ticket {
    private int id;
    private ParkingSpot parkingSpot;
    private String vehicleRegNumber;
    private double price;
    private LocalDateTime inTime;
    private LocalDateTime outTime;
    private boolean haveDiscount5Percent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ParkingSpot getParkingSpot() throws CloneNotSupportedException {
        return (ParkingSpot) parkingSpot.clone();
    }

    public void setParkingSpot(ParkingSpot parkingSpot) throws CloneNotSupportedException {
        this.parkingSpot = (ParkingSpot) parkingSpot.clone();
    }

    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    public void setVehicleRegNumber(String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getInTime() {
        return inTime;
    }

    public void setInTime(LocalDateTime inTime) {
        this.inTime = inTime;
    }

    public LocalDateTime getOutTime() {
        return outTime;
    }

    public void setOutTime(LocalDateTime outTime) {
        this.outTime = outTime;
    }

    public boolean getHaveDiscount5Percent() {
        return haveDiscount5Percent;
    }

    public void setHaveDiscount5Percent(boolean haveDiscount5Percent) {
        this.haveDiscount5Percent = haveDiscount5Percent;
    }
}
