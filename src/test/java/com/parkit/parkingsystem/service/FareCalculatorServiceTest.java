package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareWithEmptyParkingType() throws CloneNotSupportedException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String inTime = "2022-04-18 10:00:00";
        String outTime = "2022-04-18 11:00:00";
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);
        ticket.setInTime(LocalDateTime.parse(inTime,dtf));
        ticket.setOutTime(LocalDateTime.parse(outTime,dtf));
        ticket.setParkingSpot(parkingSpot);

        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareUnknownType() throws CloneNotSupportedException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String inTime = "2022-04-18 10:00:00";
        String outTime = "2022-04-18 11:00:00";
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.UNKNOWN, false);
        ticket.setInTime(LocalDateTime.parse(inTime,dtf));
        ticket.setOutTime(LocalDateTime.parse(outTime,dtf));
        ticket.setParkingSpot(parkingSpot);

        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareWithEmptyTime() throws CloneNotSupportedException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String inTime = "2022-04-18 10:00:00";
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        ticket.setInTime(LocalDateTime.parse(inTime,dtf));
        ticket.setOutTime(null);
        ticket.setParkingSpot(parkingSpot);

        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareWithFutureInTime() throws CloneNotSupportedException {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String inTime = "2022-04-18 12:00:00";
        String outTime = "2022-04-18 11:00:00";
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        ticket.setInTime(LocalDateTime.parse(inTime,dtf));
        ticket.setOutTime(LocalDateTime.parse(outTime,dtf));
        ticket.setParkingSpot(parkingSpot);

        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Nested
    @DisplayName("Tests with CAR")
    class CarTest {

        @Test
        public void calculateFareCarWithOneHourParkingTime() throws ParseException, CloneNotSupportedException {

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String inTime = "2022-04-18 10:00:00";
            String outTime = "2022-04-18 11:00:00";
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            ticket.setInTime(LocalDateTime.parse(inTime,dtf));
            ticket.setOutTime(LocalDateTime.parse(outTime,dtf));
            ticket.setParkingSpot(parkingSpot);
            fareCalculatorService.calculateFare(ticket);

            assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
        }

        @Test
        public void calculateFareCarWithLessThanOneHourParkingTime() throws ParseException, CloneNotSupportedException {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String inTime = "2022-04-18 11:00:00";
            String outTime = "2022-04-18 11:45:00";
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            ticket.setInTime(LocalDateTime.parse(inTime,dtf));
            ticket.setOutTime(LocalDateTime.parse(outTime,dtf));
            ticket.setParkingSpot(parkingSpot);

            fareCalculatorService.calculateFare(ticket);

            assertEquals((Math.ceil(0.75) * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
        }

        @Test
        public void calculateFareCarWithMoreThanOneHourParkingTime() throws CloneNotSupportedException {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String inTime = "2022-04-18 11:00:00";
            String outTime = "2022-04-18 12:01:00";
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            ticket.setInTime(LocalDateTime.parse(inTime,dtf));
            ticket.setOutTime(LocalDateTime.parse(outTime,dtf));
            ticket.setParkingSpot(parkingSpot);

            fareCalculatorService.calculateFare(ticket);

            double duration = Math.ceil((float) 61 / 60);
            assertEquals((duration * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
        }

        @Test
        public void calculateFareCarWithMoreThanADayParkingTime() throws CloneNotSupportedException {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String inTime = "2022-04-18 11:00:00";
            String outTime = "2022-04-19 11:00:00";
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            ticket.setInTime(LocalDateTime.parse(inTime,dtf));
            ticket.setOutTime(LocalDateTime.parse(outTime,dtf));
            ticket.setParkingSpot(parkingSpot);

            fareCalculatorService.calculateFare(ticket);

            assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
        }

        @Test
        public void calculateFareCarWithLess30MinutesParkingTime() throws CloneNotSupportedException {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String inTime = "2022-04-18 11:00:00";
            String outTime = "2022-04-18 11:20:00";
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            ticket.setInTime(LocalDateTime.parse(inTime,dtf));
            ticket.setOutTime(LocalDateTime.parse(outTime,dtf));
            ticket.setParkingSpot(parkingSpot);

            fareCalculatorService.calculateFare(ticket);

            assertEquals(0, ticket.getPrice());
        }

        @Test
        public void calculateFareCarWithDiscount5PercentForRecurringUsersWithOneHour() throws ParseException, CloneNotSupportedException {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String inTime = "2022-04-18 11:00:00";
            String outTime = "2022-04-18 12:00:00";
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

            ticket.setInTime(LocalDateTime.parse(inTime,dtf));
            ticket.setOutTime(LocalDateTime.parse(outTime,dtf));
            ticket.setParkingSpot(parkingSpot);
            ticket.setHaveDiscount5Percent(true);
            fareCalculatorService.calculateFare(ticket);

            double priceDiscount = Fare.CAR_RATE_PER_HOUR * (0.05);

            assertEquals((Fare.CAR_RATE_PER_HOUR - priceDiscount), ticket.getPrice());
        }

    }

    @Nested
    @DisplayName("Tests with BIKE")
    class BikeTest {

        @Test
        public void calculateFareBikeWithOneHourParkingTime() throws ParseException, CloneNotSupportedException {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String inTime = "2022-04-18 10:00:00";
            String outTime = "2022-04-18 11:00:00";
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
            ticket.setInTime(LocalDateTime.parse(inTime,dtf));
            ticket.setOutTime(LocalDateTime.parse(outTime,dtf));
            ticket.setParkingSpot(parkingSpot);
            fareCalculatorService.calculateFare(ticket);

            assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
        }

        @Test
        public void calculateFareBikeWithLessThanOneHourParkingTime() throws CloneNotSupportedException {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String inTime = "2022-04-18 11:00:00";
            String outTime = "2022-04-18 11:45:00";
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
            ticket.setInTime(LocalDateTime.parse(inTime,dtf));
            ticket.setOutTime(LocalDateTime.parse(outTime,dtf));
            ticket.setParkingSpot(parkingSpot);

            fareCalculatorService.calculateFare(ticket);

            assertEquals((Math.ceil(0.75) * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
        }

        @Test
        public void calculateFareBikeWithMoreThanOneHourParkingTime() throws CloneNotSupportedException {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String inTime = "2022-04-18 11:00:00";
            String outTime = "2022-04-18 12:01:00";
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
            ticket.setInTime(LocalDateTime.parse(inTime,dtf));
            ticket.setOutTime(LocalDateTime.parse(outTime,dtf));
            ticket.setParkingSpot(parkingSpot);

            fareCalculatorService.calculateFare(ticket);

            double duration = Math.ceil((float) 61 / 60);
            assertEquals((duration * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
        }

        @Test
        public void calculateFareBikeWithLess30MinutesParkingTime() throws CloneNotSupportedException {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String inTime = "2022-04-18 11:00:00";
            String outTime = "2022-04-18 11:20:00";
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
            ticket.setInTime(LocalDateTime.parse(inTime,dtf));
            ticket.setOutTime(LocalDateTime.parse(outTime,dtf));
            ticket.setParkingSpot(parkingSpot);

            fareCalculatorService.calculateFare(ticket);

            assertEquals(0, ticket.getPrice());
        }

        @Test
        public void calculateFareBikeWithDiscount5PercentForRecurringUsersWithOneHour() throws ParseException, CloneNotSupportedException {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String inTime = "2022-04-18 11:00:00";
            String outTime = "2022-04-18 12:00:00";
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

            ticket.setInTime(LocalDateTime.parse(inTime,dtf));
            ticket.setOutTime(LocalDateTime.parse(outTime,dtf));
            ticket.setParkingSpot(parkingSpot);
            ticket.setHaveDiscount5Percent(true);

            fareCalculatorService.calculateFare(ticket);

            double priceDiscount = Fare.BIKE_RATE_PER_HOUR * (0.05);

            assertEquals((Fare.BIKE_RATE_PER_HOUR - priceDiscount), ticket.getPrice());
        }

    }


}
