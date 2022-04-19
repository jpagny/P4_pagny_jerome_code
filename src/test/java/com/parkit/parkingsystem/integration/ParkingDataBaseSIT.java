package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseSIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static final String VEHICLE_REGISTRATION_NUMBER = "ABCDEF";

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    public static void setUp() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    public void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown() {

    }

    @Test
    public void testParkingACar() throws CloneNotSupportedException {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String inTime = "2022-04-18 10:00:00";

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle(LocalDateTime.parse(inTime, dtf));

        Ticket ticket = ticketDAO.getTicket(VEHICLE_REGISTRATION_NUMBER);

        assertEquals(ticket.getParkingSpot().getParkingType(), ParkingType.CAR);
        assertEquals(ticket.getVehicleRegNumber(), VEHICLE_REGISTRATION_NUMBER);
        assertEquals(ticket.getInTime().format(dtf), inTime);
        assertNull(ticket.getOutTime());
        assertFalse(ticket.getHaveDiscount5Percent());
        assertEquals(ticket.getPrice(), 0);

        assertEquals(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR), 2);
    }

    @Test
    public void testParkingLotExit() throws CloneNotSupportedException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String inTime = "2022-04-18 10:00:00";
        String outTime = "2022-04-18 11:00:00";

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle(LocalDateTime.parse(inTime, dtf));
        parkingService.processExitingVehicle(LocalDateTime.parse(outTime, dtf));

        Ticket ticket = ticketDAO.getTicket(VEHICLE_REGISTRATION_NUMBER);

        assertEquals(ticket.getParkingSpot().getParkingType(), ParkingType.CAR);
        assertEquals(ticket.getVehicleRegNumber(), VEHICLE_REGISTRATION_NUMBER);
        assertEquals(ticket.getInTime().format(dtf), inTime);
        assertEquals(ticket.getOutTime().format(dtf), outTime);
        assertFalse(ticket.getHaveDiscount5Percent());
        assertEquals(ticket.getPrice(), 1.5);
    }

    @Test
    public void testParkingACarWithRecurringUser() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String inTime = "2022-04-18 10:00:00";
        String outTime = "2022-04-18 11:00:00";
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        parkingService.processIncomingVehicle(LocalDateTime.parse(inTime, dtf));
        parkingService.processExitingVehicle(LocalDateTime.parse(outTime, dtf));

        inTime = "2022-04-18 12:00:00";
        parkingService.processIncomingVehicle(LocalDateTime.parse(inTime, dtf));

        Ticket ticket = ticketDAO.getTicket(VEHICLE_REGISTRATION_NUMBER);

        assertEquals(ticket.getInTime().format(dtf), inTime);
        assertNull(ticket.getOutTime());
        assertEquals(ticket.getPrice(), 0);
        assertTrue(ticket.getHaveDiscount5Percent());
    }

    @Test
    public void testParkingLotExitWithRecurringUser() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String inTime = "2022-04-18 10:00:00";
        String outTime = "2022-04-18 11:00:00";
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        parkingService.processIncomingVehicle(LocalDateTime.parse(inTime, dtf));
        parkingService.processExitingVehicle(LocalDateTime.parse(outTime, dtf));

        inTime = "2022-04-18 12:00:00";
        outTime = "2022-04-18 13:00:00";
        parkingService.processIncomingVehicle(LocalDateTime.parse(inTime, dtf));
        parkingService.processExitingVehicle(LocalDateTime.parse(outTime, dtf));

        Ticket ticket = ticketDAO.getTicket(VEHICLE_REGISTRATION_NUMBER);

        assertEquals(ticket.getInTime().format(dtf), inTime);
        assertEquals(ticket.getOutTime().format(dtf), outTime);
        assertEquals(ticket.getPrice(), 1.425);
        assertTrue(ticket.getHaveDiscount5Percent());
    }


}
