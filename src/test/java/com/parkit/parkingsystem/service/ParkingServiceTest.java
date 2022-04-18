package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;


    @Nested
    @DisplayName("Tests with CAR")
    class CarTest {

        private ParkingSpot parkingSpot;
        private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
        private String inTime = "2022-04-18 10:00:00.0";
        private Ticket ticket;

        @BeforeEach
        private void setUpPerTest() {
            try {
                parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
                parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

                ticket = new Ticket();
                ticket.setInTime(sdf.parse(inTime));
                ticket.setParkingSpot(parkingSpot);
                ticket.setVehicleRegNumber("ABCDEF");

            } catch (Exception e) {
                e.printStackTrace();
                throw  new RuntimeException("Failed to set up test mock objects");
            }
        }

        @Test
        public void processIncomingVehicleWithParkingSpotAvailableTest() throws Exception {

            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(2);

            parkingService.processIncomingVehicle(sdf.parse(inTime));

            verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
            verify(ticketDAO, times(1)).countRecurringVehicle(any(String.class));
            verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));

            assertFalse(parkingSpot.isAvailable());
        }

        @Test
        public void processIncomingVehicleWithParkingSpotNotAvailableTest() throws Exception {

            try {
                when(inputReaderUtil.readSelection()).thenReturn(1);
                when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(0);

                parkingService.processIncomingVehicle(sdf.parse(inTime));

            } catch (Exception exception){
                assertTrue(exception instanceof Exception);
                assertTrue(exception.getMessage().contains("Error fetching parking number from DB. Parking slots might be full"));
            }
        }

        @Test
        public void processIncomingVehicleWithRecurringUserTest() throws Exception {
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(2);

            when(ticketDAO.countRecurringVehicle(any(String.class))).thenReturn(4);
            when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

            parkingService.processIncomingVehicle(sdf.parse(inTime));

            assertTrue(outContent.toString().contains("Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount."));
        }

        @Test
        public void processIncomingVehicleWithoutRecurringUserTest() throws Exception {
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(2);

            when(ticketDAO.countRecurringVehicle(any(String.class))).thenReturn(0);
            when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

            parkingService.processIncomingVehicle(sdf.parse(inTime));

            assertFalse(outContent.toString().contains("Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount."));
        }

        @Test
        public void failToProcessIncomingVehicleTest() throws Exception {

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
                String inTime = "2022-04-18 10:00:00.0";

                when(inputReaderUtil.readSelection()).thenReturn(1);
                when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
                when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(2);
                when(ticketDAO.saveTicket(null)).thenReturn(false);

                parkingService.processIncomingVehicle(sdf.parse(inTime));
            } catch (Exception exception){
                assertTrue(exception instanceof Exception);
                assertTrue(exception.getMessage().contains("Unable to process incoming vehicle"));
            }
        }

        @Test
        public void processExitingVehicleTest() throws Exception {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
            String outTime = "2022-04-18 11:00:00.0";

            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService.processExitingVehicle(sdf.parse(outTime));

            verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
            verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));

            assertTrue(parkingSpot.isAvailable());
        }

        @Test
        public void processExitingVehicleWithFailUpdateTest() throws Exception {

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
                String outTime = "2022-04-18 11:00:00.0";

                when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
                when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
                when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);

                parkingService.processExitingVehicle(sdf.parse(outTime));

            } catch (Exception exception){
                assertTrue(exception instanceof Exception);
                assertTrue(exception.getMessage().contains("Unable to update ticket information. Error occurred"));
            }
        }

        @Test
        public void failProcessExitingVehicleTest() throws Exception {

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
                String outTime = "2022-04-18 11:00:00.0";

                when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
                when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
                when(ticketDAO.updateTicket(null)).thenReturn(true);

                parkingService.processExitingVehicle(sdf.parse(outTime));

            } catch (Exception exception){
                assertTrue(exception instanceof Exception);
                assertTrue(exception.getMessage().contains("Unable to process exiting vehicle"));
            }
        }

    }



}