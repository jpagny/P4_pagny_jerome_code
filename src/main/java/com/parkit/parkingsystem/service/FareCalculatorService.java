package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) throws CloneNotSupportedException {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        LocalDateTime inTime = ticket.getInTime();
        LocalDateTime outTime = ticket.getOutTime();

        long minutes = java.time.Duration.between(inTime, outTime).toMinutes();

        if (minutes <= 30) {
            ticket.setPrice(0);

        } else {

            double duration = Math.ceil((float) minutes / 60);

            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                    break;
                }
                case BIKE: {
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown Parking Type");
            }

            if (ticket.getHaveDiscount5Percent()) {
                double valueDiscount = ticket.getPrice() * 0.05;
                ticket.setPrice(ticket.getPrice() - valueDiscount);
            }
        }
    }
}