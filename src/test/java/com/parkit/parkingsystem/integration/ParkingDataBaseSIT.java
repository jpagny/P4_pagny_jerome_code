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

import java.text.ParseException;
import java.text.SimpleDateFormat;

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
    public static void setUp() throws Exception{
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
    private static void tearDown(){

    }

    @Test
    public void testParkingACar() throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String inTime = "2022-04-18 10:00:00.0";

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle(sdf.parse(inTime));

        Ticket ticket = ticketDAO.getTicket(VEHICLE_REGISTRATION_NUMBER);

        assertEquals(ticket.getParkingSpot().getParkingType(),ParkingType.CAR);
        assertEquals(ticket.getVehicleRegNumber(),VEHICLE_REGISTRATION_NUMBER);
        assertEquals(ticket.getInTime().toString(), inTime);
        assertNull(ticket.getOutTime());
        assertFalse(ticket.getHaveDiscount5Percent());
        assertEquals(ticket.getPrice(),0);

        assertEquals(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR),2);
    }

    @Test
    public void testParkingLotExit() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String inTime = "2022-04-18 10:00:00.0";
        String outTime = "2022-04-18 11:00:00.0";

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle(sdf.parse(inTime));
        parkingService.processExitingVehicle(sdf.parse(outTime));

        Ticket ticket = ticketDAO.getTicket(VEHICLE_REGISTRATION_NUMBER);

        assertEquals(ticket.getParkingSpot().getParkingType(),ParkingType.CAR);
        assertEquals(ticket.getVehicleRegNumber(),VEHICLE_REGISTRATION_NUMBER);
        assertEquals(ticket.getInTime().toString(), inTime);
        assertEquals(ticket.getOutTime().toString(), outTime);
        assertFalse(ticket.getHaveDiscount5Percent());
        assertEquals(ticket.getPrice(),1.5);
    }

    @Test
    public void testParkingACarWithRecurringUser() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
        String inTime = "2022-04-18 10:00:00.0";
        String outTime = "2022-04-18 11:00:00.0";
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        parkingService.processIncomingVehicle(sdf.parse(inTime));
        parkingService.processExitingVehicle(sdf.parse(outTime));

        inTime = "2022-04-18 12:00:00.0";
        parkingService.processIncomingVehicle(sdf.parse(inTime));

        Ticket ticket = ticketDAO.getTicket(VEHICLE_REGISTRATION_NUMBER);

        assertEquals(ticket.getInTime().toString(), inTime);
        assertNull(ticket.getOutTime());
        assertEquals(ticket.getPrice(),0);
        assertTrue(ticket.getHaveDiscount5Percent());
    }

    @Test
    public void testParkingLotExitWithRecurringUser() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
        String inTime = "2022-04-18 10:00:00.0";
        String outTime = "2022-04-18 11:00:00.0";
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        parkingService.processIncomingVehicle(sdf.parse(inTime));
        parkingService.processExitingVehicle(sdf.parse(outTime));

        inTime = "2022-04-18 12:00:00.0";
        outTime = "2022-04-18 13:00:00.0";
        parkingService.processIncomingVehicle(sdf.parse(inTime));
        parkingService.processExitingVehicle(sdf.parse(outTime));

        Ticket ticket = ticketDAO.getTicket(VEHICLE_REGISTRATION_NUMBER);

        assertEquals(ticket.getInTime().toString(),inTime);
        assertEquals(ticket.getOutTime().toString(),outTime);
        assertEquals(ticket.getPrice(),1.425);
        assertTrue(ticket.getHaveDiscount5Percent());
    }


}
